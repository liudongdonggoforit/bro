package com.sh.browser.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mr.liu
 * On 2016/7/13
 * At 20:00
 * My Application
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mContext;
    protected View mView;
    protected ShowUrl showUrl;
    public interface ShowUrl{
        void openUrl(String url);
    }

    public String getName() {

        return getArguments().getString("title");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initViews();
        initEvents();
        onLoadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView;
    }

    protected void onLoadData() {
    }

    protected void initEvents() {
    }

    protected void initViews() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ShowUrl) {
            showUrl = (ShowUrl) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showUrl = null;
    }
}
