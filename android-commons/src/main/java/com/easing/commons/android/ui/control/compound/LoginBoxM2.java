package com.easing.commons.android.ui.control.compound;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easing.commons.android.R;
import com.easing.commons.android.view.Views;

public class LoginBoxM2 extends LinearLayout {

    private EditText accountEdit;
    private EditText passwordEdit;
    private ImageView passwordEyeButton;
    private ImageView rememberCheckBox;
    private ImageView autoLoginCheckBox;
    private Button loginButton;

    private OnSelectChange onRememberChange;
    private OnSelectChange onAutoLoginChange;
    private OnLogin onLogin;

    public LoginBoxM2(Context context) {
        super(context);
        init(context, null);
    }

    public LoginBoxM2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoginBoxM2(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrSet) {
        View root = Views.inflate(context, R.layout.layout_login_box_m02);
        super.addView(root, new LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT));

        accountEdit = Views.findView(root, R.id.user_edit);
        passwordEdit = Views.findView(root, R.id.password_edit);
        passwordEyeButton = Views.findView(root, R.id.bt_eye);
        rememberCheckBox = Views.findView(root, R.id.bt_remember);
        autoLoginCheckBox = Views.findView(root, R.id.bt_auto_login);
        loginButton = Views.findView(root, R.id.bt_login);

        Views.removeDoubleClickEvent(accountEdit);
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

        rememberCheckBox.setOnClickListener(v -> {
            boolean b = rememberCheckBox.isSelected();
            rememberCheckBox.setSelected(!b);
            if (onRememberChange != null)
                onRememberChange.action(!b);
        });

        autoLoginCheckBox.setOnClickListener(v -> {
            boolean b = autoLoginCheckBox.isSelected();
            autoLoginCheckBox.setSelected(!b);
            if (onAutoLoginChange != null)
                onAutoLoginChange.action(!b);
        });

        loginButton.setOnClickListener(v -> {
            if (onLogin != null)
                onLogin.login(account(), password());
        });
    }

    public String account() {
        return Views.text(accountEdit, true, true);
    }

    public String password() {
        return Views.text(passwordEdit, true, true);
    }

    public String account(boolean trim, boolean ignoreCase) {
        return Views.text(accountEdit, trim, ignoreCase);
    }

    public String password(boolean trim, boolean ignoreCase) {
        return Views.text(passwordEdit, trim, ignoreCase);
    }

    public void account(String account) {
        Views.text(accountEdit, account);
    }

    public void password(String password) {
        Views.text(passwordEdit, password);
    }

    public void remember(boolean selected) {
        rememberCheckBox.setSelected(selected);
    }

    public void autoLogin(boolean selected) {
        autoLoginCheckBox.setSelected(selected);
    }

    public void enableRemember(boolean enable) {
        Views.findView(this, R.id.bt_remember).setVisibility(enable ? View.VISIBLE : View.GONE);
        Views.findView(this, R.id.text_remember).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void enableAutoLogin(boolean enable) {
        Views.findView(this, R.id.bt_auto_login).setVisibility(enable ? View.VISIBLE : View.GONE);
        Views.findView(this, R.id.text_auto_login).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public boolean remember() {
        return rememberCheckBox.isSelected();
    }

    public boolean autoLogin() {
        return autoLoginCheckBox.isSelected();
    }

    public void onRememberChange(OnSelectChange listener) {
        this.onRememberChange = listener;
    }

    public void setOnAutoLoginChange(OnSelectChange listener) {
        this.onAutoLoginChange = listener;
    }

    public void onLogin(OnLogin listener) {
        this.onLogin = listener;
    }

    public interface OnSelectChange {
        void action(boolean selected);
    }

    public interface OnLogin {
        void login(String account, String password);
    }
}
