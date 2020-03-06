package com.easing.commons.android.http;

import com.easing.commons.android.clazz.BeanUtil;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.helper.data.Result;
import com.easing.commons.android.manager.HttpUtil;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.ui.dialog.TipBox;
import com.easing.commons.android.value.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//Postman使用方法：
//设置Url -> 设置Method -> 设置Head，Param，Form，File -> 设置Option
//设置ExceptionHandler -> 设置ResponseHandler -> Execute
public class Postman {

    //请求参数
    private String url;
    private HttpMethod method;
    private LinkedHashMap<String, Object> paramMap = new LinkedHashMap();
    private LinkedHashMap<String, Object> formMap = new LinkedHashMap();
    private LinkedHashMap<String, Object> headMap = new LinkedHashMap();
    private LinkedHashMap<File, String> fileMap = new LinkedHashMap();
    private OnException onIoException;
    private OnException onBizException;
    private OnException onCodeException;
    private OnResponse onResponse;

    //Client复用
    private boolean useGlobalClient;
    private static final OkHttpClient defaultClient;

    //请求构造器
    private Request.Builder requestBuilder = new Request.Builder();
    private OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    //以下变量无实际作用，仅仅方便外部调试
    private Call call;
    private Response response;
    private Exception exception;
    private Integer status;
    private Boolean success;

    //初始化共用的Client
    static {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(5, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(5, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(false);
        clientBuilder.connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS));
        clientBuilder.sslSocketFactory(SSL.sslSocketFactory(), SSL.trustManager());
        clientBuilder.hostnameVerifier(SSL.hostnameVerifier());
        defaultClient = clientBuilder.build();
    }

    //初始化自己的Client
    {
        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(5, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(5, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(false);
        clientBuilder.connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS));
        clientBuilder.sslSocketFactory(SSL.sslSocketFactory(), SSL.trustManager());
        clientBuilder.hostnameVerifier(SSL.hostnameVerifier());
    }

    public static Postman create() {
        return new Postman();
    }

    private Postman() {
        useGlobalClient(true);
        useLongConnection(false);
    }

    public Postman head(String key, Object value) {
        this.headMap.put(key, value);
        return this;
    }

    public Postman head(Map<String, Object> headMap) {
        this.headMap.putAll(headMap);
        return this;
    }

    public Postman param(String key, Object value) {
        this.paramMap.put(key, value);
        return this;
    }

    public Postman param(Map<String, Object> paramMap) {
        this.paramMap.putAll(paramMap);
        return this;
    }

    public Postman param(Object entity) {
        BeanUtil.copyAttribute(entity, paramMap);
        return this;
    }

    public Postman form(String key, Object value) {
        this.formMap.put(key, value);
        return this;
    }

    public Postman form(Map<String, Object> formMap) {
        this.formMap.putAll(formMap);
        return this;
    }

    public Postman form(Object entity) {
        BeanUtil.copyAttribute(entity, formMap);
        return this;
    }

    public Postman file(String key, String file) {
        fileMap.put(new File(file), key);
        return this;
    }

    public Postman file(String key, File file) {
        fileMap.put(file, key);
        return this;
    }

    public Postman url(String url) {
        this.url = url;
        return this;
    }

    public Postman method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Postman onIoException(OnException onIoException) {
        this.onIoException = onIoException;
        return this;
    }

    public Postman onBizException(OnException onBizException) {
        this.onBizException = onBizException;
        return this;
    }

    public Postman onCodeException(OnException onCodeException) {
        this.onCodeException = onCodeException;
        return this;
    }

    public Postman onResponse(OnResponse onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    public Postman execute(boolean sync) {
        return sync ? executeInSerial() : executeInParallel();
    }

    public Postman executeInSerial() {
        //构建Http请求
        try {
            Postman.this.call = buildCall();
        } catch (Exception e) {
            Console.error(e);
            Postman.this.exception = e;
            if (onCodeException != null)
                onCodeException.onException(Postman.this, null, e);
            else if (onIoException != null)
                onIoException.onException(Postman.this, null, e);
            else
                TipBox.tip("错误的网络请求");
            return this;
        }

        //执行同步请求
        try {
            Response response = call.execute();
            Postman.this.response = response;
            Postman.this.status = response.code();
            Postman.this.success = response.code() == 200;
            if (onResponse != null)
                onResponse.onResponse(Postman.this, call, response);
        } catch (SocketTimeoutException e) {
            Console.error(e);
            Postman.this.exception = e;
            if (onIoException != null)
                onIoException.onException(Postman.this, call, e);
        } catch (ConnectException e) {
            Console.error(e);
            Postman.this.exception = e;
            if (onIoException != null)
                onIoException.onException(Postman.this, call, e);
        } catch (Exception e) {
            Console.error(e);
            Postman.this.exception = e;
            if (onBizException != null)
                onBizException.onException(Postman.this, call, e);
        }
        return this;
    }

    public Postman executeInParallel() {
        //构建Http请求
        try {
            Postman.this.call = buildCall();
        } catch (Exception e) {
            Console.error(e);
            Postman.this.exception = e;
            if (onCodeException != null)
                onCodeException.onException(Postman.this, null, e);
            else if (onIoException != null)
                onIoException.onException(Postman.this, null, e);
            else
                TipBox.tip("错误的网络请求");
            return this;
        }

        //执行异步请求
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Console.error(e);
                Postman.this.exception = e;
                if (onIoException != null)
                    onIoException.onException(Postman.this, call, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Postman.this.response = response;
                    Postman.this.status = response.code();
                    Postman.this.success = response.code() == 200;
                    if (onResponse != null)
                        onResponse.onResponse(Postman.this, call, response);
                } catch (Exception e) {
                    Console.error(e);
                    if (onBizException != null)
                        onBizException.onException(Postman.this, call, e);
                }
            }
        };
        call.enqueue(callback);
        return this;
    }

    //构建最终的Http请求
    private Call buildCall() {
        //设置URL
        url = url + HttpUtil.paramToString(paramMap);
        requestBuilder.url(url);
        //设置请求头
        for (String key : headMap.keySet()) {
            String value = headMap.get(key).toString();
            requestBuilder.header(key, value);
        }
        //设置请求体
        switch (method) {
            case GET: {
                requestBuilder.get();
                break;
            }
            case URL_ENCODED_POST: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.post(formBody);
                break;
            }
            case MULTI_FORM_POST: {
                MultipartBody multipartBody = HttpUtil.paramToMultipartForm(formMap, fileMap);
                requestBuilder.post(multipartBody);
                break;
            }
            case RAW_POST: {
                break;
            }
            case BINARY_POST: {
                break;
            }
            case PUT: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.put(formBody);
                break;
            }
            case PATCH: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.patch(formBody);
                break;
            }
            case DELETE: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.delete(formBody);
                break;
            }
        }
        //执行请求
        Request request = requestBuilder.build();
        OkHttpClient client = useGlobalClient ? defaultClient : clientBuilder.build();
        Call call = client.newCall(request);
        return call;
    }

    //设置连接超时（与服务器连接成功所需时间）
    public Postman connectTimeOut(long ms) {
        useGlobalClient = false;
        clientBuilder.connectTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    //设置写数据超时（上传文件，上传请求数据所需时间）
    public Postman writeTimeOut(long ms) {
        useGlobalClient = false;
        clientBuilder.writeTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    //设置读数据超时（下载文件，读取回复数据所需时间）
    public Postman readTimeOut(long ms) {
        useGlobalClient = false;
        clientBuilder.readTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    //失败后重新连接
    public Postman retry(boolean retry) {
        useGlobalClient = false;
        clientBuilder.retryOnConnectionFailure(retry);
        return this;
    }

    //使用长连接
    public Postman useLongConnection(boolean use) {
        headMap.put("Connection", use ? "keep-alive" : "close");
        return this;
    }

    //使用默认OkHttp客户端
    //凡是会修改ClientBuilder的功能，都不该使用默认客户端
    public Postman useGlobalClient(boolean useGlobalClient) {
        this.useGlobalClient = useGlobalClient;
        return this;
    }

    public static boolean ping(String ip, String port) {
        final Result<Boolean> result = Result.ok();
        Postman.create()
                .url("http://" + ip + ":" + port)
                .method(HttpMethod.GET)
                .onIoException((postman, call, e) -> {
                    result.data = false;
                })
                .onCodeException((postman, call, e) -> {
                    result.data = false;
                })
                .onResponse((postman, call, resp) -> {
                    result.data = true;
                })
                .executeInParallel();
        long time = 0;
        while (time <= 1500) {
            if (result.data != null)
                return result.data;
            time += 100;
            Threads.sleep(100);
        }
        return false;
    }

    public static boolean pingWithHttps(String ip, String port) {
        final Result<Boolean> result = Result.ok();
        Postman.create()
                .url("https://" + ip + ":" + port)
                .method(HttpMethod.GET)
                .onIoException((postman, call, e) -> {
                    result.data = false;
                })
                .onCodeException((postman, call, e) -> {
                    result.data = false;
                })
                .onResponse((postman, call, resp) -> {
                    result.data = true;
                })
                .executeInParallel();
        long time = 0;
        while (time <= 1500) {
            if (result.data != null)
                return result.data;
            time += 100;
            Threads.sleep(100);
        }
        return false;
    }

    public interface OnException {
        void onException(Postman postman, Call call, Exception e);
    }

    public interface OnResponse {
        void onResponse(Postman postman, Call call, Response response) throws Exception;
    }
}
