package com.easing.commons.android.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends PagerAdapter {

    private AppCompatActivity activity;
    private List<Fragment> fragments;

    public static FragmentAdapter get(AppCompatActivity activity, List<Fragment> fragments) {
        FragmentAdapter adapter = new FragmentAdapter();
        adapter.activity = activity;
        adapter.fragments = fragments;
        return adapter;
    }

    public static FragmentAdapter get(AppCompatActivity activity, Fragment... fragments) {
        FragmentAdapter adapter = new FragmentAdapter();
        List<Fragment> fragmentList = new ArrayList();
        for (Fragment fragment : fragments)
            fragmentList.add(fragment);
        adapter.activity = activity;
        adapter.fragments = fragmentList;
        return adapter;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if (!fragment.isAdded()) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        if (fragment.getView().getParent() == null)
            container.addView(fragment.getView());
        return fragment.getView();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
}
