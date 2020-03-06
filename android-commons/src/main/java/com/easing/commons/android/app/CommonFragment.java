package com.easing.commons.android.app;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.view.Views;

public abstract class CommonFragment extends Fragment {

    public CommonActivity ctx;
    public Handler handler;
    protected View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ctx = (CommonActivity) getActivity();
        handler = ctx.handler;
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null)
                parent.removeView(root);
        } else {
            root = inflateView();
            createView();
        }
        return root;
    }

    public abstract View inflateView();

    public abstract void createView();

    //显示状态栏占位View
    public void adaptStatuBar(){
        View statuPlaceholder = root.findViewById(R.id.v_top);
        if (statuPlaceholder != null) {
            int statuBarHeight = Device.statuBarHeight(ctx);
            Views.size(statuPlaceholder, null, statuBarHeight);
        }
    }
}
