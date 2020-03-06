package com.easing.commons.android.ui.control.compound;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.view.Views;

import lombok.Getter;
import lombok.Setter;

public class LoginBox extends LinearLayout {

    @Getter
    private EditText userEdit;
    @Getter
    private EditText passwordEdit;
    @Getter
    private Button rememberPasswordCheckBox;
    @Getter
    private Button autoLoginCheckBox;
    @Getter
    private Button loginButton;

    private TextView rememberPasswordText;
    private TextView autoLoginText;

    private Button passwordEyeButton;

    @Setter
    private OnSelectedChangeListener onRememberPasswordChangeListener;
    @Setter
    private OnSelectedChangeListener onAutoLoginChangeListener;

    public LoginBox(Context context) {
        super(context);
        init(context, null);
    }

    public LoginBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoginBox(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        View root = Views.inflate(context, R.layout.layout_login_box_m01);
        super.addView(root, new LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT));

        userEdit = Views.findView(root, R.id.user_edit);
        passwordEdit = Views.findView(root, R.id.password_edit);
        rememberPasswordCheckBox = Views.findView(root, R.id.bt_remember);
        autoLoginCheckBox = Views.findView(root, R.id.bt_auto_login);
        loginButton = Views.findView(root, R.id.bt_login);
        rememberPasswordText = Views.findView(root, R.id.text_remember);
        autoLoginText = Views.findView(root, R.id.text_auto_login);
        passwordEyeButton = Views.findView(root, R.id.bt_eye);

        Views.removeDoubleClickEvent(userEdit);
        Views.removeDoubleClickEvent(passwordEdit);

        HideReturnsTransformationMethod showRealText = HideReturnsTransformationMethod.getInstance();
        PasswordTransformationMethod showEncryptText = PasswordTransformationMethod.getInstance();
        passwordEyeButton.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN)
                passwordEdit.setTransformationMethod(showRealText);
            else if (e.getAction() == MotionEvent.ACTION_UP)
                passwordEdit.setTransformationMethod(showEncryptText);
            return false;
        });

        rememberPasswordCheckBox.setOnClickListener((v) -> {
            boolean b = rememberPasswordCheckBox.isSelected();
            rememberPasswordCheckBox.setSelected(!b);
            if (onRememberPasswordChangeListener != null)
                onRememberPasswordChangeListener.action(!b);
        });
        autoLoginCheckBox.setOnClickListener((v) -> {
            boolean b = autoLoginCheckBox.isSelected();
            autoLoginCheckBox.setSelected(!b);
            if (onAutoLoginChangeListener != null)
                onAutoLoginChangeListener.action(!b);
        });
    }

    public LoginBox rememberPassword(boolean b) {
        rememberPasswordCheckBox.setSelected(b);
        return this;
    }

    public LoginBox autoLogin(boolean b) {
        autoLoginCheckBox.setSelected(b);
        return this;
    }

    public boolean rememberPassword() {
        return rememberPasswordCheckBox.isSelected();
    }

    public boolean autoLogin() {
        return autoLoginCheckBox.isSelected();
    }

    public LoginBox enableRememberPassword(boolean b) {
        rememberPasswordCheckBox.setVisibility(b ? View.VISIBLE : View.GONE);
        rememberPasswordText.setVisibility(b ? View.VISIBLE : View.GONE);
        return this;
    }

    public LoginBox enableAutoLogin(boolean b) {
        autoLoginCheckBox.setVisibility(b ? View.VISIBLE : View.GONE);
        autoLoginText.setVisibility(b ? View.VISIBLE : View.GONE);
        return this;
    }

    public LoginBox username(String username) {
        Views.text(userEdit, username);
        return this;
    }

    public LoginBox password(String password) {
        Views.text(passwordEdit, password);
        return this;
    }

    public String username() {
        return Views.text(userEdit, false, false);
    }

    public String password() {
        return Views.text(passwordEdit, false, false);
    }

    public interface OnSelectedChangeListener {
        void action(boolean selected);
    }
}
