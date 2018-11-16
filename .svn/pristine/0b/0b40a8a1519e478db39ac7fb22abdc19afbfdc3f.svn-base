package com.sh.browser;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sh.browser.cbhttp.MyOkHttp;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by dong on 2017/10/12.
 */

public class UCAppliCation extends Application {
    private static UCAppliCation mInstance;
    private MyOkHttp mMyOkHttp;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mInstance = this;

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);

        UMConfigure.setLogEnabled(true);
        PlatformConfig.setWeixin("wx665eaf66422e0f07", "5d88a51b8f2a9a98ddd6f23cdfa78edb");
        PlatformConfig.setQQZone("101513632", "3c02cc32c0c64205e6665d7f6297c91b");
        PlatformConfig.setSinaWeibo("334671732", "56d676ea6cb13c3ed068363a85add895","http://sns.whalecloud.com");
        UMConfigure.init(this,"5bc6dc55f1f556ca1800036c","123",UMConfigure.DEVICE_TYPE_PHONE,"");
        //自定义OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        mMyOkHttp = new MyOkHttp(okHttpClient);
    }

    public static synchronized UCAppliCation getInstance() {
        return mInstance;
    }

    public MyOkHttp getMyOkHttp() {
        return mMyOkHttp;
    }

    /**
     * 获取全局的context
     */
    public static Context getContext() {
        return mContext;
    }

}
