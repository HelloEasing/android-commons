package com.easing.commons.android.ui.control.compound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;
import com.easing.commons.android.ui.dialog.TipBox;

import java.util.Date;

import lombok.Setter;

@SuppressLint("DrawAllocation")
public class VertifyBox extends LinearLayout {

    private EditText input1;
    private EditText input2;
    private Button button1;
    private Button button2;

    @Setter
    private Action sendAction;
    @Setter
    private Action loginAction;

    private String expectCode = ""; // 验证码
    private long expectTime = -1; // 过期时间
    private long waitTime = 30; // 等待时间

    private int phoneLenth = 11;
    private int codeLength = 6;

    public VertifyBox(Context context) {
        super(context);
        init(context, null);
    }

    public VertifyBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VertifyBox(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        View root = Views.inflate(context, R.layout.layout_validation_view);
        super.addView(root, new LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT));
        input1 = Views.findView(root, R.id.input1);
        input2 = Views.findView(root, R.id.input2);
        button1 = Views.findView(root, R.id.bt1);
        button2 = Views.findView(root, R.id.bt2);
        input1.setHint("手机号码");
        input2.setHint("短信验证码");
        button1.setText("发送验证码");
        button2.setText("登 录");

        button1.setOnClickListener((v) -> {
            if (sendAction != null)
                sendAction.act();
            button1.setClickable(false);
            button1.setBackgroundResource(R.drawable.color_white_40);
            Threads.post(() -> {
                for (long i = waitTime; i >= 1; i--) {
                    final String sec = i + "";
                    GlobalHandler.post(() -> button1.setText(sec));
                    Threads.sleep(1);
                }
                GlobalHandler.post(() -> {
                    button1.setText("发送验证码");
                    button1.setBackgroundResource(R.drawable.color_white_20_40);
                    button1.setClickable(true);
                });
            });
        });

        button2.setOnClickListener((v) -> {
            if (!checkCode())
                TipBox.tip("验证码错误");
            else if (loginAction != null)
                loginAction.act();
        });
    }

    private boolean checkCode() {
        if (!input2.getText().toString().equalsIgnoreCase(expectCode))
            return false;
        if (new Date().getTime() > expectTime)
            return false;
        return true;
    }

    public void updateResult(String expectCode, long expectTime) {
        this.expectCode = expectCode;
        this.expectTime = expectTime;
    }

    public String getPhoneNum() {
        return input1.getText().toString();
    }
}
