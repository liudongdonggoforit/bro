package com.sh.browser.activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Mr.liu
 * On 2016/7/13
 * At 18:51
 * My Application
 */
public abstract class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";
    /**
     * 加载状态 0：加载失败；1：正在加载；2加载成功*/
    protected String loadingState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContentViewResId() != 0){
            setContentView(getContentViewResId());
        }
        initViews();
        initEvents();

    }

    /**
     * 获取导航栏左侧图片
     * @return
     */
    protected int getTopBarLeftImgRes(){
        return 1;
    }

    /**
     * 获取导航栏中间文字
     * @return
     */
    protected int getTopBarTextRes(){
        return 0;
    }

    /**
     * 获取导航栏右侧图片
     * @return
     */
    protected int getTopBarRightImgRes(){
       return 0;
    }

    /**
     * 获取导航栏右侧文字
     * @return
     */
    protected int getTopBarRightTextRes(){
        return 0;
    }


    protected abstract int getContentViewResId();

    protected void initEvents() {
    }

    protected void initViews() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
