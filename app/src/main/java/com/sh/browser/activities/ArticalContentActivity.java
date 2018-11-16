package com.sh.browser.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sh.browser.R;
import com.sh.browser.utils.UCUtils;

/**
 * Created by dong on 2017/10/18.
 */

public class ArticalContentActivity extends BaseActivity {

    private WebView mWebView;
    private PopupWindow popupWindow;
    private View contentView;
    private String url ;
    private String desc,title,imgurl;
    private String articalId;
    private String base_url;
    private String cell_type;
    @Override
    protected int getContentViewResId() {
        return R.layout.activity_artical_content;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mWebView = (WebView) findViewById(R.id.aritical_webview_content);
        ImageView back = findViewById(R.id.content_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String time = getIntent().getStringExtra("time");
        desc = getIntent().getStringExtra("desc");
        title = getIntent().getStringExtra("title");
        imgurl = getIntent().getStringExtra("imgurl");
        base_url = getIntent().getStringExtra("base_url");
        cell_type = getIntent().getStringExtra("cell_type");
        if(time != null) {
            TextView timeTv = findViewById(R.id.content_time);
            timeTv.setText(UCUtils.getStrTime(UCUtils.getTimeStamp(time, "yyyy-MM-dd HH:mm:ss") / 1000));
        }
        articalId = getIntent().getStringExtra("artical_id");
//        url = "http://api.kcaibao.com/article/" + articalId ;

        if (!TextUtils.isEmpty(cell_type) && cell_type.equals("3")) {
            url = imgurl;
        }else {
            url = base_url + articalId ;
        }
        mWebView.loadUrl(url);
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true); // 显示放大缩小 controler
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true); // 可以缩放
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);// 默认缩放模式
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);

        final ImageView share = findViewById(R.id.artical_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopwindow();
            }
        });
    }

    private void shareText(String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,content);
        startActivity(intent);
    }

    /**
     * 显示popupWindow
     */
    private void showPopwindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View layout = LayoutInflater.from(ArticalContentActivity.this).inflate(R.layout.popupwindow_share, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView cancel = layout.findViewById(R.id.popup_cancel);
        LinearLayout wechat = layout.findViewById(R.id.share_wechat);
        LinearLayout frend = layout.findViewById(R.id.share_friend);
        LinearLayout weibo = layout.findViewById(R.id.share_weibo);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        int[] location = new int[2];
        mWebView.getLocationOnScreen(location);
        popupWindow.showAtLocation(mWebView, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        //添加按键事件监听
//        setButtonListeners(layout);
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(1f);
                popupWindow.dismiss();
            }
        });

        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        frend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        // 按下android回退物理键 PopipWindow消失解决
        cancel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        backgroundAlpha(1f);
                        popupWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private String getResourcesUri(int id) {
        Resources resources = getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        Toast.makeText(this, "Uri:" + uriPath, Toast.LENGTH_SHORT).show();
        return uriPath;
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);  getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

}
