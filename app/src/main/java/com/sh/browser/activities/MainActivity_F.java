package com.sh.browser.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sh.browser.R;
import com.sh.browser.fragment.BrowserFragment;
import com.sh.browser.fragment.MainFragment;
import com.sh.browser.fragment.MineFragment;
import com.sh.browser.fragment.SearchFragment;
import com.sh.browser.fragment.VideosFragment;
import com.sh.browser.fragment.WindowsFragment;
import com.sh.browser.interfaces.IBack;
import com.sh.browser.interfaces.ICanGo;
import com.sh.browser.interfaces.ISearch;
import com.sh.browser.interfaces.ItransactionFragment;
import com.sh.browser.interfaces.OnPress;
import com.sh.browser.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目主页功能分化处理
 */
public class MainActivity_F extends BaseActivity implements View.OnClickListener, IBack, ItransactionFragment {

    private View[] mBottom;
    private final Class[] mFragments = new Class[]{MainFragment.class, VideosFragment.class, WindowsFragment.class, MineFragment.class, SearchFragment.class, BrowserFragment.class};
    private final Map<Integer, Fragment> mFragmentMaps = new HashMap<Integer, Fragment>();
    private int mCurrentSelectedFragmentPosition = -1;
    //首页MainFragment返回键处理
    public OnPress onPress;
    public ICanGo iCanGo;
    public ISearch iSearch;
    public LinearLayout main_bottom_nav;
    public ImageView bottom_left,bottom_right;
    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main_f;
    }

    @Override
    protected void initViews() {
        super.initViews();
        main_bottom_nav = findViewById(R.id.main_bottom_nav);
        bottom_left = findViewById(R.id.bottom_left);
        bottom_right = findViewById(R.id.bottom_right);
        mBottom = new View[]{findViewById(R.id.bottom_article), findViewById(R.id.bottom_video), findViewById(R.id.bottom_window), findViewById(R.id.bottom_home)};
        mBottom[0].setSelected(true);
        setSelected(0);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        for (int i = 0; i < mBottom.length; i++) {
            final int temp = i;
            mBottom[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(temp);
                }
            });
        }
    }

    /**
     * 底部导航栏处理
     *
     * @param position
     */
    private void setSelected(int position) {

        if (position == 2) {
            main_bottom_nav.setVisibility(View.GONE);
        } else {
            main_bottom_nav.setVisibility(View.VISIBLE);
        }

        if (position == 5) {
            bottom_right.setVisibility(View.VISIBLE);
            bottom_left.setVisibility(View.VISIBLE);
            mBottom[0].setVisibility(View.GONE);
            mBottom[1].setVisibility(View.GONE);
        } else {
            bottom_right.setVisibility(View.GONE);
            bottom_left.setVisibility(View.GONE);
            mBottom[0].setVisibility(View.VISIBLE);
            mBottom[1].setVisibility(View.VISIBLE);
        }

        if (mCurrentSelectedFragmentPosition == position) {
            return;
        }
        try {
            Fragment fragment = mFragmentMaps.get(position);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mCurrentSelectedFragmentPosition != -1) {
                transaction.hide(mFragmentMaps.get(mCurrentSelectedFragmentPosition));
            }
            Bundle bundle = new Bundle();
            if (position == 2) {
                Bitmap bitmap = CommonUtils.screenShot(MainActivity_F.this);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bitmaps = bos.toByteArray();
                bundle.putByteArray("bitmaps", bitmaps);
            }
            if (fragment == null) {
                fragment = (Fragment) mFragments[position].newInstance();
                bundle.putString("tag", fragment.getTag());
                bundle.putString("arg", fragment.getClass().getName());
                fragment.setArguments(bundle);
                mFragmentMaps.put(position, fragment);
                transaction.add(R.id.main_content, fragment, fragment.getClass().getCanonicalName()).commit();
            } else {
                bundle.putString("tag", fragment.getTag());
                bundle.putString("arg", fragment.getClass().getName());
                fragment.setArguments(bundle);
                transaction.show(fragment);
                transaction.commit();
            }
            mCurrentSelectedFragmentPosition = position;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.i("transcation","123456" + fragment.getClass().getCanonicalName());
        if (fragment instanceof MainFragment) {
            onPress = (OnPress) fragment;
        } else if (fragment instanceof BrowserFragment) {
            iCanGo = (ICanGo) fragment;
            iSearch = (ISearch) fragment;
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onPress != null) {
            onPress.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_left:
                iCanGo.goBack();
                break;
            case R.id.bottom_right:
                iCanGo.goForward();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (current != null && current instanceof BrowserFragment) {
            iCanGo.goBack();
            return false;
        } else if (current != null && !(current instanceof MainFragment)) {
            setSelected(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void back() {
        setSelected(0);
    }

    @Override
    public void transcationFragment(int flag, Bundle bundle) {
        switch (flag) {
            case 0:
                setSelected(4);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                setSelected(0);
                break;
            case 5:
                setSelected(5);
                final String query = bundle.getString("query");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (iSearch != null) {
                            Log.i("transcation","123456");
                            iSearch.search(query);
                        }
                    }
                },500);
                break;
        }
    }
}
