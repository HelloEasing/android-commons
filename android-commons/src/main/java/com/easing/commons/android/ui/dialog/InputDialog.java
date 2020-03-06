package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.ui.control.button.ImageButtonM2;

//输入框
public class InputDialog extends DialogFragment {

    private String message = "";
    private String content = "";

    private AppCompatActivity ctx;

    private TextView messageText;
    private EditText contentEdit;
    private OnInput onInput;
    private OnCancel onCancel;

    //静态创建方法
    public static InputDialog create(AppCompatActivity ctx, Object msg, Object content, OnInput onInput, OnCancel onCancel) {
        InputDialog dialog = new InputDialog();
        dialog.ctx = ctx;
        dialog.message = msg.toString();
        dialog.content = content == null ? "" : content.toString();
        dialog.onInput = onInput;
        dialog.onCancel = onCancel;
        dialog.setCancelable(true);
        return dialog;
    }

    //显示
    public void show() {
        FragmentManager manager = ctx.getSupportFragmentManager();
        super.show(manager, "InputDialog");
    }

    //是否可取消
    public InputDialog cancelable(boolean cancelable) {
        setCancelable(cancelable);
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //解析控件
        messageText = root.findViewById(R.id.text_msg);
        contentEdit = root.findViewById(R.id.edit_content);
        contentEdit.setText(content);
        ImageButtonM2 okButton = root.findViewById(R.id.bt_ok);
        messageText.setText(message);
        okButton.setOnClickListener(v -> {
            dismiss();
            if (onInput != null)
                onInput.onInput(contentEdit.getText().toString());
        });
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (onCancel != null)
            onCancel.onDismiss();
    }

    public interface OnInput {

        void onInput(String content);
    }

    public interface OnCancel {

        void onDismiss();
    }
}
