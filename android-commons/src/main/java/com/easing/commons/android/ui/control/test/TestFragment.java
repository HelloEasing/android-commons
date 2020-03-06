package com.easing.commons.android.ui.control.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends Fragment {

    private Object value;

    public static TestFragment create(Object value) {
        TestFragment fragment = new TestFragment();
        fragment.value = value;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new TestView(super.getActivity(), value);
    }
}
