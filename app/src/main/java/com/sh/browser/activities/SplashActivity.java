package com.sh.browser.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sh.browser.R;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private UMShareAPI umShareAPI;
    private ImageView splash,QQ,Wechat,Sina;
    private LinearLayout splash_login;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            splash.setVisibility(View.GONE);
            splash_login.setVisibility(View.VISIBLE);
            Intent intent = new Intent(SplashActivity.this, MainActivity_F.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_splash;
    }


    @Override
    protected void initViews() {
        super.initViews();
        splash = findViewById(R.id.splash);
        splash.setVisibility(View.VISIBLE);
        QQ = findViewById(R.id.splash_QQ);
        Wechat = findViewById(R.id.splash_wechat);
        Sina = findViewById(R.id.splash_sina);
        splash_login = findViewById(R.id.splash_login);
        splash_login.setVisibility(View.GONE);
        QQ.setOnClickListener(this);
        Wechat.setOnClickListener(this);
        Sina.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

        umShareAPI = UMShareAPI.get(SplashActivity.this);
        mHandler.sendEmptyMessageDelayed(1,     1000);
    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(SplashActivity.this, "start", Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Intent intent = new Intent(SplashActivity.this, Main.class);
            startActivity(intent);
            finish();
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            Toast.makeText(SplashActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(SplashActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splash_QQ:
                umShareAPI.getPlatformInfo(SplashActivity.this, SHARE_MEDIA.QZONE, authListener);
                break;
            case R.id.splash_wechat:
                umShareAPI.getPlatformInfo(SplashActivity.this, SHARE_MEDIA.WEIXIN, authListener);
                break;
            case R.id.splash_sina:
                umShareAPI.getPlatformInfo(SplashActivity.this, SHARE_MEDIA.SINA, authListener);
                break;
        }
    }
}
