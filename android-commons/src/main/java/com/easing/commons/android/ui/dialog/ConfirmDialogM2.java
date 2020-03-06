package com.easing.commons.android.ui.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.manager.FontUtil;
import com.easing.commons.android.manager.FontUtil.Font;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.ui.control.button.ImageButtonM1;

//确认框
public class ConfirmDialogM2 {

    //自由样式，可自己指定布局，标题id为tv，确认按钮id为ok，取消按钮id为no即可
    public static PopupWindow confirm(Activity context, Object msg, Action confirmAction, Action denyAction, Action cancelAction, int layoutId) {
        //创建弹窗
        View root = LayoutInflater.from(context).inflate(layoutId, null);
        PopupWindow dialog = new PopupWindow(root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialog.setFocusable(true);
        dialog.setTouchable(true);
        dialog.setOutsideTouchable(true);
        dialog.showAtLocation(Views.getRootView(context), Gravity.CENTER, 0, 0);

        //设置弹窗内容
        TextView tv = Views.findView(root, R.id.tv);
        ImageButtonM1 okButton = Views.findView(root, R.id.bt_ok);
        ImageButtonM1 noButton = Views.findView(root, R.id.bt_no);
        tv.setText(msg.toString());

        //确定事件
        okButton.setOnClickListener((v) -> {
            root.setTag(0); //标记是通过按钮dismiss
            if (confirmAction != null)
                confirmAction.act();
            dialog.dismiss();
        });

        //否定事件
        noButton.setOnClickListener((v) -> {
            root.setTag(0);
            if (denyAction != null)
                denyAction.act();
            dialog.dismiss();
        });

        //取消事件，视为否定
        dialog.setOnDismissListener(() -> {
            if (root.getTag() == null)
                if (cancelAction != null)
                    cancelAction.act();
        });

        //绑定字体
        FontUtil.bindFont(tv, Font.YAHEI_CONSOLAS);
        FontUtil.bindFont(okButton, Font.YAHEI_CONSOLAS);
        FontUtil.bindFont(noButton, Font.YAHEI_CONSOLAS);

        return dialog;
    }

    //M1样式确认框
    public static PopupWindow confirmM1(Activity context, Object msg, Action confirmAction, Action denyAction, Action cancelAction) {
        return ConfirmDialogM2.confirm(context, msg, confirmAction, denyAction, cancelAction, R.layout.layout_confirm_dialog_m02_1);
    }

    //M2样式确认框
    public static PopupWindow confirmM2(Activity context, Object msg, Action confirmAction, Action denyAction, Action cancelAction) {
        return ConfirmDialogM2.confirm(context, msg, confirmAction, denyAction, cancelAction, R.layout.layout_confirm_dialog_m02_2);
    }
}
