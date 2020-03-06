package com.easing.commons.android.app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.easing.commons.android.R;
import com.easing.commons.android.code.CodeUtil;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.code.Logger;
import com.easing.commons.android.helper.data.Data;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.struct.Collections;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.manager.Permissions;
import com.easing.commons.android.manager.Uris;
import com.easing.commons.android.ui.control.view.NavigationBarPlaceholder;
import com.easing.commons.android.ui.dialog.TipBox;
import com.easing.commons.android.value.color.Colors;
import com.easing.commons.android.value.identity.Actions;
import com.easing.commons.android.value.identity.Codes;
import com.easing.commons.android.value.identity.Keys;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.identity.Values;
import com.yanzhenjie.permission.AndPermission;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

//通过泛型实现ctx的自动转型
@SuppressWarnings("all")
public class CommonActivity<T extends CommonActivity> extends AppCompatActivity {

    public T ctx;
    public Handler handler;

    //记录Activity创建时间，用于判断是否首次启动
    @Getter
    private long firstLauchTime;

    //固定屏幕方向
    @Setter
    private Values.ORIENTATION requestedOrientation = null;

    private Runnable controlBarsAdapter;

    private Boolean hasNavigationBar = null;

    //进程被回收后重新恢复
    protected void onProcessReset(Data<Boolean> whetherFinishActivity) {
    }

    //onCreate之前执行的代码
    //返回false表示结束当前Activity
    protected boolean beforeCreate() {
        //判断进程是否是被恢复的新进程
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            if (extras.getInt("processId") != Process.myPid()) {
                Data<Boolean> whetherFinishActivity = Data.create(false);
                onProcessReset(whetherFinishActivity);
                if (whetherFinishActivity.data) return false;
            }
        //屏幕始终打开
        super.getWindow().setFlags(LayoutParams.FLAG_KEEP_SCREEN_ON, LayoutParams.FLAG_KEEP_SCREEN_ON);
        //不自动弹出键盘
        super.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //总是竖屏显示
        if (requestedOrientation == Values.ORIENTATION.ORIENTATION_PORTRAIT)
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //总是横屏显示
        if (requestedOrientation == Values.ORIENTATION.ORIENTATION_LANDSCAPE)
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保存全局环境
        this.ctx = (T) this;
        this.handler = CommonApplication.handler;
        this.firstLauchTime = Times.millisOfNow();
        //加入任务栈
        this.myApplication().addToStack(this);
        //是否启动当前Activity
        if (beforeCreate()) create();
        else finish();
    }

    //onCreate代码，由子类自己定义
    protected void create() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controlBarsAdapter != null && !isFirstLauch())
            detectNavigationBar();
        resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    //子类定义，实现自己的方法
    protected void resume() {
    }

    //子类定义，实现自己的方法
    protected void pause() {
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
        this.myApplication().removeFromStack(this);
    }

    //onDestroy代码，由子类自己定义
    protected void destroy() {
    }

    //申请权限
    public void requestPermission(Permissions.PermissionGroup... groups) {
        Permissions.requestPermissionGroup(this, groups);
    }

    //申请权限，带成功和失败回调
    public void requestPermissionWithCallback(Permissions.PermissionGroup... groups) {
        //先进行权限申请，拿到权限后，才进行操作
        Action okAction = new Action() {
            @Override
            public void act() {
                onPermissionOk();
            }
        };
        //获取权限失败
        Action noAction = new Action() {
            @Override
            public void act() {
                onPermissionFail();
            }
        };
        //获取权限
        Permissions.requestPermissionGroup(this, okAction, noAction, groups);
    }

    //申请全部权限
    public void requestAllPermission() {
        requestPermission(
                Permissions.PermissionGroup.STORAGE,
                Permissions.PermissionGroup.LOCATION,
                Permissions.PermissionGroup.PHONE,
                Permissions.PermissionGroup.SMS,
                Permissions.PermissionGroup.CONTACTS,
                Permissions.PermissionGroup.CALENDAR,
                Permissions.PermissionGroup.CAMERA,
                Permissions.PermissionGroup.MICROPHONE,
                Permissions.PermissionGroup.SENSORS
        );
    }

    //申请全部权限
    public void requestAllPermissionWithCallback() {
        requestPermissionWithCallback(
                Permissions.PermissionGroup.STORAGE,
                Permissions.PermissionGroup.LOCATION,
                Permissions.PermissionGroup.PHONE,
                Permissions.PermissionGroup.SMS,
                Permissions.PermissionGroup.CONTACTS,
                Permissions.PermissionGroup.CALENDAR,
                Permissions.PermissionGroup.CAMERA,
                Permissions.PermissionGroup.MICROPHONE,
                Permissions.PermissionGroup.SENSORS
        );
    }

    //申请权限成功
    protected void onPermissionOk() {
    }

    //申请权限失败
    protected void onPermissionFail() {
    }

    //解析布局
    public <T extends View> T inflate(int layoutId) {
        return (T) LayoutInflater.from(ctx).inflate(layoutId, null);
    }

    //获取所在的APP
    public <T extends CommonApplication> T myApplication() {
        return (T) super.getApplication();
    }

    //隐藏窗口
    public void hide() {
        super.moveTaskToBack(true);
    }

    //窗口跳转
    public void start(Class<? extends Activity> clazz, Map<String, Serializable> params) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("processId", Process.myPid());
        if (params != null)
            for (String key : params.keySet())
                intent.putExtra(key, params.get(key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    //窗口跳转，并返回结果
    public void startForResult(Class<? extends Activity> clazz, int requestCode, Map<String, Serializable> params) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("processId", Process.myPid());
        if (params != null)
            for (String key : params.keySet())
                intent.putExtra(key, params.get(key));
        super.startActivityForResult(intent, requestCode);
    }

    //窗口跳转
    public void start(Class<? extends Activity> clazz) {
        start(clazz, null);
    }

    //窗口跳转，并返回结果
    public void startForResult(Class<? extends Activity> clazz, int requestCode) {
        startForResult(clazz, requestCode, null);
    }

    //跳转并结束自己
    public void startAndFinish(Class<? extends Activity> clazz) {
        startAndFinish(clazz, null);
    }

    //跳转并结束自己
    public void startAndFinishLater(Class<? extends Activity> clazz, long ms) {
        handler.postDelayed(() -> {
            startAndFinish(clazz, null);
        }, ms);
    }


    //跳转并结束自己
    public void startAndFinish(Class<? extends Activity> clazz, Map<String, Serializable> params) {
        start(clazz, params);
        this.finish();
    }

    //停止服务
    public void stopService(Class<? extends Service> clazz) {
        Intent start_activity = new Intent(this, clazz);
        super.stopService(start_activity);
    }

    //在主线程执行
    public void post(Runnable r) {
        handler.post(() -> {
            try {
                r.run();
            } catch (Exception e) {
                Logger.error(CodeUtil.getExceptionDetail(e), "app/" + CommonApplication.projectName + "/error");
                TipBox.toast(CommonApplication.ctx, "程序发生未知异常", R.layout.layout_tip_box_1);
                CommonApplication.ctx.handleGlobalException(e);
                //结束进程
                if (CommonApplication.exitOnException) {
                    Threads.sleep(CommonApplication.waitTimeOnException);
                    CommonApplication.ctx.finishProcess();
                }
            }
        });
    }

    //在主线程延时执行
    public void postLater(Runnable r, long ms) {
        handler.postDelayed(() -> {
            try {
                r.run();
            } catch (Exception e) {
                Logger.error(CodeUtil.getExceptionDetail(e), "app/" + CommonApplication.projectName + "/error");
                TipBox.toast(CommonApplication.ctx, "程序发生未知异常", R.layout.layout_tip_box_1);
                CommonApplication.ctx.handleGlobalException(e);
                //结束进程
                if (CommonApplication.exitOnException) {
                    Threads.sleep(CommonApplication.waitTimeOnException);
                    CommonApplication.ctx.finishProcess();
                }
            }
        }, ms);
    }

    public void hideStatuBar() {
        super.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatuBar() {
        super.getWindow().clearFlags(LayoutParams.FLAG_FULLSCREEN);
    }

    public void hideActionBar() {
        super.getSupportActionBar().hide();
    }

    public void showActionBar() {
        super.getSupportActionBar().show();
    }

    public void changeBarColor(int statuBarColor, int actionBarDrawable, int avigationBarColor) {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        super.getWindow().setStatusBarColor(getResources().getColor(statuBarColor));
        super.getSupportActionBar().setBackgroundDrawable(getDrawable(actionBarDrawable));
        super.getWindow().setNavigationBarColor(getResources().getColor(avigationBarColor));
    }

    public void translucentMode() {
        super.getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public void immersiveMode() {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void exitImmersiveMode(boolean showStatuBar) {
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (showStatuBar) showStatuBar();
    }

    //让状态栏和导航栏浮动并透明，不占布局空间
    //这样就可以自己定义两个View，分别放在statuBar和navigationBar的位置，来模拟自定义的状态栏和导航栏
    //默认使用R.id.v_top作为statuBar占位View，使用R.id.v_bottom作为navigationBar占位View
    public void adaptControlBars() {

        //延迟到布局加载完毕再调用，布局加载完毕才知道屏幕是否有虚拟按键
        controlBarsAdapter = () -> {
            //让状态栏和导航栏浮动，不占布局空间
            super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

            //设置状态栏和导航栏颜色
            super.getWindow().setStatusBarColor(Colors.TRANSPARENT);
            super.getWindow().setNavigationBarColor(Colors.TRANSPARENT);

            //显示状态栏占位View
            View statuPlaceholder = Views.getRootView(ctx).findViewById(R.id.v_top);
            if (statuPlaceholder != null) {
                int statuBarHeight = Device.statuBarHeight(ctx);
                Views.size(statuPlaceholder, null, statuBarHeight);
            }

            //显示导航栏占位View
            View navigationBarPlaceholder = Views.getRootView(ctx).findViewById(R.id.v_bottom);
            if (navigationBarPlaceholder != null && hasNavigationBar) {
                int navigationBarHeight = Device.navigationBarHeight(ctx);
                Views.size(navigationBarPlaceholder, null, navigationBarHeight);
            }

            //去除全面屏底部黑边
            if (!hasNavigationBar) {
                LinkedList<View> list = Views.allWindowNode(ctx);
                //正常屏幕的第一个节点和第三个节点都是全屏高的
                //全面屏第三个节点会比第一个节点小一个导航栏的高度
                Views.size(list.get(2), null, list.get(0).getMeasuredHeight());
            }
        };
    }

    //通过解析Window布局，判断有无虚拟按键
    public void detectNavigationBar() {
        int navigationBarHeight = Device.navigationBarHeight(ctx);
        int screenWidth = Device.getScreenSize(ctx).w;
        List<View> nodes = Views.allWindowNode(ctx);
        nodes = Collections.filter(nodes, node -> {
            if (node.getClass() != NavigationBarPlaceholder.class)
                if (node.getMeasuredHeight() == navigationBarHeight)
                    if (node.getMeasuredWidth() == screenWidth)
                        return true;
            return false;
        });
        hasNavigationBar = nodes.size() > 0;

        //适配状态栏和导航栏
        //因为适配状态栏和导航栏需要先知道手机是否是全面屏，所以需要在此执行
        if (controlBarsAdapter != null) controlBarsAdapter.run();
    }

    //判断是否首次进入界面
    public boolean isFirstLauch() {
        return Times.millisOfNow() - firstLauchTime < 3000;
    }

    //选择文件
    public void pickFile(String mediaType) {
        Intent it = new Intent(Actions.ACTION_PICK_FILE);
        it.setType(mediaType);
        startActivityForResult(it, Codes.CODE_PICK_FILE);
    }

    //拍照，路径需是png格式
    public void captureImage(String path) {
        Intent intent = new Intent(Actions.ACTION_CAPTURE_IMAGE);
        Uri uri = Uris.fromFile(path);
        intent.putExtra(Keys.KEY_OUTPUT, uri);
        startActivityForResult(intent, Codes.CODE_IMAGE_CAPTURE);
    }

    //录像，路径需是mp4格式
    public void captureVideo(String path) {
        Intent intent = new Intent(Actions.ACTION_CAPTURE_VIDEO);
        Uri uri = Uris.fromFile(Files.getAndroidExternalFile(path));
        intent.putExtra(Keys.KEY_OUTPUT, uri);
        startActivityForResult(intent, Codes.CODE_VIDEO_CAPTURE);
    }

    //处理选择文件和拍照结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = (resultCode == RESULT_OK);
        if (requestCode == Codes.CODE_PICK_FILE) {
            if (!result) return;
            String path = Uris.uriToPath(ctx, data.getData());
            onFilePick(path);
        } else if (requestCode == Codes.CODE_IMAGE_CAPTURE) {
            if (!result) return;
            onImageCapture(data);
        } else if (requestCode == Codes.CODE_VIDEO_CAPTURE) {
            if (!result) return;
            onVideoCapture(data);
        } else
            onActivityResult(requestCode, result, data);
    }

    //选择文件回调
    protected void onFilePick(String path) {
    }

    //拍照回调
    protected void onImageCapture(Intent data) {
    }

    //录像回调
    protected void onVideoCapture(Intent data) {
    }

    //调用其它窗口返回结果
    protected void onActivityResult(int code, boolean result, Intent data) {
    }
}
