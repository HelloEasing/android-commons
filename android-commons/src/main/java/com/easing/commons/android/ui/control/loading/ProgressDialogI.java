package com.easing.commons.android.ui.control.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.easing.commons.android.R;

public class ProgressDialogI extends ProgressBar {

    public ProgressDialogI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        customStyle();
    }

    public ProgressDialogI(Context context, AttributeSet attrs) {
        super(context, attrs);
        customStyle();
    }

    public ProgressDialogI(Context context) {
        super(context);
        customStyle();
    }

    private void customStyle() {
        super.setIndeterminateDrawable(super.getResources().getDrawable(R.drawable.progress_m01, null));
    }
}
