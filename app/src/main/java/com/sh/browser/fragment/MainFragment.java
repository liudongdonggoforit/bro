package com.sh.browser.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.zxing.activity.CaptureActivity;
import com.sh.browser.R;
import com.sh.browser.UCAppliCation;
import com.sh.browser.adapter.ContentFragmentAdapter;
import com.sh.browser.adapter.URLAdapter;
import com.sh.browser.behavior.UCViewHeaderBehavior;
import com.sh.browser.cbhttp.MyOkHttp;
import com.sh.browser.cbhttp.response.GsonResponseHandler;
import com.sh.browser.interfaces.ItransactionFragment;
import com.sh.browser.interfaces.OnPress;
import com.sh.browser.interfaces.SearchBrowser;
import com.sh.browser.models.Channel;
import com.sh.browser.models.Channels;
import com.sh.browser.models.URL;
import com.sh.browser.utils.Constants;
import com.sh.browser.views.NoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览器首页内容
 */
public class MainFragment extends BaseFragment implements OnPress, View.OnClickListener {
    private TabLayout mTabLayout;
    private NoScrollViewPager mViewPager;
    private UCViewHeaderBehavior mUCViewHeaderBehavior;
    private LinearLayout search;
    private EditText contentSearch, titleSearch;
    private RecyclerView mRecyclerView;
    private URLAdapter urlAdapter;
    private MyOkHttp mOkHttp;
    private List<URL> mList;
    public static final int REQ_QRCODE = 0x1111;
    //打开浏览器页面
    private SearchBrowser searchBrowser;
    private ItransactionFragment itransactionFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.uc_main_view_layout, null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mOkHttp = UCAppliCation.getInstance().getMyOkHttp();
        mTabLayout =mView.findViewById(R.id.news_view_tab_layout);
        mViewPager =mView.findViewById(R.id.news_view_content_layout);
        search = mView.findViewById(R.id.search);
        contentSearch = mView.findViewById(R.id.content_search);
        mRecyclerView = mView.findViewById(R.id.urlRecyclerview);
        titleSearch = mView.findViewById(R.id.search_title);
        search.setOnClickListener(this);
        titleSearch.setOnClickListener(this);
        contentSearch.setOnClickListener(this);
        initURLS();
        urlAdapter = new URLAdapter(mContext, mList, itransactionFragment);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
        mRecyclerView.setAdapter(urlAdapter);
        mUCViewHeaderBehavior = (UCViewHeaderBehavior) ((CoordinatorLayout.LayoutParams)mView.findViewById(R.id.news_view_header_layout).getLayoutParams()).getBehavior();
        initViewData();
        titleSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = titleSearch.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > titleSearch.getWidth()
                        - titleSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    qrCode();
                }
                return false;
            }
        });

        contentSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = contentSearch.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > contentSearch.getWidth()
                        - contentSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    qrCode();
                }
                return false;
            }
        });
        //注册广播监测滑动
        registReceiver();
    }
    private void initURLS() {
        try {
            JSONArray array = new JSONArray(Constants.URLS);
            mList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                URL url = new URL();
                JSONObject object = array.getJSONObject(i);
                url.setPic(object.getString("icon_url"));
                url.setTitle(object.getString("name"));
                url.setUrl(object.getString("url"));
                mList.add(i, url);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化加载数据
     */
    private void initViewData() {
        mOkHttp.get().url("http://api.kcaibao.com/v1/get-channels/caibao").enqueue(new GsonResponseHandler<Channels>() {
            @Override
            public void onSuccess(int statusCode, Channels response) {
                List<String> mTitles = new ArrayList<String>();
                List<String> mIds = new ArrayList<String>();
                for (int i = 0; i < response.getData().size(); i++) {
                    Channel channel = response.getData().get(i);
                    mTitles.add(i, channel.getChannel_name());
                    mIds.add(i, channel.getChannel_id());
                }

                mTitles.add("灵异");
                mIds.add("-1");

                mTitles.add("美图");
                mIds.add("-1");

                List<BaseFragment> fragments = new ArrayList<>();
//                fragments.add(PsychicFragment.newInstance("灵异","-2"));
//                fragments.add(BeautyFragment.newInstance("美图","-2"));

                for (int i = 0; i < 5; i++) {
                    fragments.add(ContentFragment.newInstance(mTitles.get(i), mIds.get(i)));
                }

                mTabLayout.setTabMode(fragments.size() <= 6 ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
                ContentFragmentAdapter adapter = new ContentFragmentAdapter(fragments, getActivity().getSupportFragmentManager());
                mViewPager.setAdapter(adapter);
                mTabLayout.setupWithViewPager(mViewPager);
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mUCViewHeaderBehavior.isClosed()){
            mUCViewHeaderBehavior.openPager();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_title:
            case R.id.content_search:
                //searchBrowser.jumpBrowser("");
                itransactionFragment.transcationFragment(0,null);
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_ISCLOSED)) {
                mViewPager.setScroll(intent.getBooleanExtra("isclose", false));
            }
        }
    };

    /**
     * 注册广播
     */
    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ISCLOSED);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchBrowser) {
           searchBrowser = (SearchBrowser) context;
        } else if (context instanceof ItransactionFragment) {
            itransactionFragment = (ItransactionFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchBrowser = null;
        itransactionFragment = null;
    }

    /**
     *
     */
    private void qrCode() {
        //第二个参数是需要申请的权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            //权限已经被授予，在这里直接写要执行的相应方法即可
            startActivityForResult(new Intent(mContext, CaptureActivity.class), REQ_QRCODE);
        }
    }
}
