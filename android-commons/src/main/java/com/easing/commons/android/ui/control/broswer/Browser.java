package com.easing.commons.android.ui.control.broswer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.data.JSON;
import com.easing.commons.android.ui.dialog.MessageDialog;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("all")
public class Browser extends WebView {

    public Browser(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //浏览器设置
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //请求处理
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:///"))
                    return false;
                //非HTTP协议调用本地应用打开
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                CommonApplication.ctx.startActivity(intent);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //允许不安全的HTTPS证书
                handler.proceed();
            }
        });

        //内核处理
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                //无内容时隐藏浏览器
                if (view.getUrl().equalsIgnoreCase("about:blank"))
                    setAlpha(0);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //JS弹窗改成APP弹窗
                MessageDialog.create((CommonActivity) context).message(message).showWithoutIcon();
                result.cancel();
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                //允许摄像头等权限
                request.grant(request.getResources());
            }
        });
    }

    //将对象方法绑定到javascript的window对象上，从而使得javascript可以调用Java
    //只有被@JavascriptInterface注解和public关键字修饰的方法，才能被加载到javascript的window对象中
    public Browser addJavascriptInterface(Object windowObject) {
        addJavascriptInterface(windowObject, "JavaCall");
        return this;
    }

    //Java调用javascript方法
    //为了便捷和统一规范，参数统一以JSON对象形式传递，在javascript中可直接作为对象使用
    public Browser callJavascript(String javascriptFuncName, Map<String, Object> jsonMap) {
        if (jsonMap == null) jsonMap = new LinkedHashMap();
        loadUrl("javascript:" + javascriptFuncName + "(" + JSON.stringfy(jsonMap) + ")");
        return this;
    }

    //打开assets文件夹中的页面
    public Browser openAsset(String resource) {
        loadUrl("file:///android_asset/" + resource);
        return this;
    }

    //打开网页
    public Browser openWebPage(String url) {
        loadUrl(url);
        return this;
    }

}
