package com.sh.browser.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.activity.CaptureActivity;
import com.sh.browser.R;
import com.sh.browser.behavior.UCViewHeaderBehavior;
import com.sh.browser.database.Record;
import com.sh.browser.database.RecordAction;
import com.sh.browser.fragment.BaseFragment;
import com.sh.browser.fragment.BrowserFragment;
import com.sh.browser.fragment.MainFragment;
import com.sh.browser.fragment.MineFragment;
import com.sh.browser.fragment.SearchFragment;
import com.sh.browser.fragment.VideoFragment;
import com.sh.browser.interfaces.IBack;
import com.sh.browser.interfaces.ICanGo;
import com.sh.browser.interfaces.ISearch;
import com.sh.browser.interfaces.ItransactionFragment;
import com.sh.browser.interfaces.OnPress;
import com.sh.browser.utils.BrowserUnit;
import com.sh.browser.utils.CommonUtils;
import com.sh.browser.utils.IntentUnit;
import com.sh.browser.views.CustomDialog;
import com.sh.browser.views.NinjaToast;
import com.sh.browser.views.NinjaWebView;
import com.squareup.picasso.Picasso;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目主页功能分化处理
 */
public class MainActivity_F extends BaseActivity implements View.OnClickListener, IBack, ItransactionFragment, BaseFragment.ShowUrl {

    private View[] mBottom;
    private final Class[] mFragments = new Class[]{MainFragment.class, VideoFragment.class, BrowserFragment.class, MineFragment.class, SearchFragment.class};
    private final Map<Integer, Fragment> mFragmentMaps = new HashMap<Integer, Fragment>();
    private int mCurrentSelectedFragmentPosition = -1;
    //首页MainFragment返回键处理
    public OnPress onPress;
    public ICanGo iCanGo;
    public ISearch iSearch;
    public LinearLayout main_bottom_nav;
    public ImageView bottom_left, bottom_right;
    private BottomSheetDialog bottomSheetDialog;
    private boolean backTmp = false;
    private byte[] bitmaps;
    private Bitmap bitmap;
    private ImageView bottom_setting, bottom_home;
    private NinjaWebView ninjaWebView;
    private FrameLayout bottomWindow;
    private boolean isBrowser = true;
    private String iconUrl, userName;
    private ImageView setting_icon;
    private TextView setting_name;
    private TextView bottom_window_size;
    private View view;
    private LinearLayout main_content;
    private BrowserFragment browserFragment;
    private boolean isCH = false;
    public boolean isCHStart = false;
    private int type = 0;

    private int savePosition = 0;
    private String ninjaUrl;
    private String chUrl;
    private String tmpUrl;
    private boolean isFirstOpen = true;

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
        bottom_setting = findViewById(R.id.bottom_setting);
        bottomWindow = findViewById(R.id.bottom_window);
        bottomWindow.setOnClickListener(this);
        bottom_home = findViewById(R.id.bottom_home);
        bottom_window_size = findViewById(R.id.bottom_window_size);
        main_content = findViewById(R.id.main_content);
        bottom_setting.setOnClickListener(this);
        bottom_home.setOnClickListener(this);
        mBottom = new View[]{findViewById(R.id.bottom_article), findViewById(R.id.bottom_video), findViewById(R.id.bottom_window), findViewById(R.id.bottom_home)};
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("url") != null) {
            String url = bundle.getString("url");
            openUrl(url);
            return;
        }
        setSelected(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bitmap = CommonUtils.screenShot(MainActivity_F.this);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bitmaps = bos.toByteArray();
            }
        }, 2000);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        for (int i = 0; i < mBottom.length; i++) {
            if (i == 3) {
                break;
            }
            final int temp = i;
            mBottom[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NiceVideoPlayerManager.instance().suspendNiceVideoPlayer();
                    setSelected(temp);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (temp == 2) {
                                isBrowser = false;
                                Fragment current = mFragmentMaps.get(mCurrentSelectedFragmentPosition);
                                if (current != null && current instanceof BrowserFragment) {
                                    browserFragment = (BrowserFragment) current;
                                    ((BrowserFragment) current).setVisible();
                                    main_bottom_nav.setVisibility(View.GONE);
                                    //main_content.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
                                }
                            }
                        }
                    }, 0);
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
        if (savedState != null) {
            return;
        }
        Bundle bundle = new Bundle();

        if (position == 2 && !isBrowser) {
            main_bottom_nav.setVisibility(View.GONE);
        } else {
            main_bottom_nav.setVisibility(View.VISIBLE);
        }

        if (position == 2) {
            bundle.putByteArray("bitmap", bitmaps);
        }

        if (position == 3) {
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
        Log.i("transcation", "123456" + fragment.getClass().getCanonicalName());
        if (fragment instanceof MainFragment) {
            onPress = (OnPress) fragment;
        } else if (fragment instanceof BrowserFragment) {
            iCanGo = (ICanGo) fragment;
            iSearch = (ISearch) fragment;
        }
        super.onAttachFragment(fragment);
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
            case R.id.bottom_setting:
                showOverSetting();
                break;
            case R.id.bottom_home:
                setSelected(0);
                Fragment current = mFragmentMaps.get(mCurrentSelectedFragmentPosition);
                if (current instanceof MainFragment) {
                    UCViewHeaderBehavior uCViewHeaderBehavior = ((MainFragment) current).mUCViewHeaderBehavior;
                    if (uCViewHeaderBehavior != null && (uCViewHeaderBehavior.isClosed())) {
                        uCViewHeaderBehavior.openPager();
                    }
                }
                break;
        }
    }

    public void setWindows(int size) {
        bottom_window_size.setText("" + size);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if (chUrl != null && isCH) {
            Intent intent = new Intent(MainActivity_F.this, CollectionAndHistoryActivity.class);
            intent.putExtra("type", type);
            startActivityForResult(intent, 10);
            isCH = false;
            if (savePosition == 2) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelected(savePosition);
                        if (tmpUrl != null) {
                            openUrl(tmpUrl);
                        } else {
                            setSelected(0);
                        }
                    }
                }, 0);
            } else {
                setSelected(savePosition);
            }
            return false;
        }

        Fragment current = mFragmentMaps.get(mCurrentSelectedFragmentPosition);
        if (current != null && current instanceof BrowserFragment) {
            NinjaWebView ninjiaWebView = ((BrowserFragment) current).getNinjaWebView();
            if (ninjiaWebView != null && ninjiaWebView.canGoBack()) {
                ((BrowserFragment) current).getNinjaWebView().goBack();
            } else {
                if (ninjiaWebView != null) {
                    ninjiaWebView.setAlbumCover(bitmap);
                    ninjiaWebView.setAlbumTitle("浏览器");
//                    ninjiaWebView.setWebViewClient(new WebViewClient(){
//                        @Override
//                        public void doUpdateVisitedHistory(WebView webView, String s, boolean b) {
//                            super.doUpdateVisitedHistory(webView, s, b);
//                            webView.clearHistory();
//                            webView.clearCache(true);
//                        }
//                    });
                }
                setSelected(0);
            }
            return false;
        } else if (current != null && current instanceof VideoFragment) {
            if (NiceVideoPlayerManager.instance().onBackPressd()) {
            } else {
                setSelected(0);
            }
            return false;
        } else if (current instanceof MainFragment) {
            UCViewHeaderBehavior uCViewHeaderBehavior = ((MainFragment) current).mUCViewHeaderBehavior;
            if (uCViewHeaderBehavior != null && (uCViewHeaderBehavior.isClosed())) {
                uCViewHeaderBehavior.openPager();
                return false;
            } else {
                doubleTapsQuit();
            }
        } else if (current instanceof SearchFragment) {
            setSelected(0);
            return false;
        } else {
            doubleTapsQuit();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", " requestCode :" + requestCode + "resultCode: " + resultCode);
        if (resultCode == -1 && data != null) {
            byte[] result = data.getByteArrayExtra(CaptureActivity.KEY_RESULT);
            if (result == null || result.length == 0) return;
            String query = new String(result);
            openUrl(query);
        } else if (resultCode == 1 && data != null) {
            iconUrl = data.getStringExtra("iconurl");
            userName = data.getStringExtra("name");
        } else if (resultCode == 10 && data != null) {
            isCH = data.getBooleanExtra("flag", false);
            type = data.getIntExtra("type", 0);
            chUrl = data.getStringExtra("url");
            openUrl(chUrl);
        } else if (isCHStart) {
            if (savePosition == 2) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelected(savePosition);
                        if (tmpUrl != null) {
                            openUrl(tmpUrl);
                        } else {
                            setSelected(0);
                        }
                    }
                }, 0);
            } else {
                setSelected(savePosition);
            }
        }
    }

    /**
     * 设置底部导航栏显示
     */
    public void setVisible() {
//        setSelected(0);
        // TODO: 2018/12/4 影响第一次加载
        main_bottom_nav.setVisibility(View.VISIBLE);
    }

    public void setBottomVisible() {
        main_bottom_nav.setVisibility(View.VISIBLE);
    }

    public void setBottomGone() {
        if (!isFirstOpen) {
            setSelected(0);
        }
        isFirstOpen = false;
        main_bottom_nav.setVisibility(View.VISIBLE);
    }

    @Override
    public void back() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (!backTmp) {
            backTmp = true;
            setSelected(0);
        } else if (current instanceof MainFragment) {
            doubleTapsQuit();
        } else {
            setSelected(0);
        }
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
                isBrowser = true;
                setSelected(2);
                final Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_content);

                if (current != null && current instanceof BrowserFragment) {
                    ((BrowserFragment) current).setGone();
                }

                final String query = bundle.getString("query");

                if (!isCH) {
                    tmpUrl = query;
                }

                final boolean search = bundle.getBoolean("search");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!(current instanceof BrowserFragment)) {
                            setSelected(2);
                            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);

                            if (fragment != null && fragment instanceof BrowserFragment) {
                                ((BrowserFragment) fragment).setGone();
                            }
                        }
                        if (iSearch != null) {
                            iSearch.search(query, search);
                        }
                    }
                }, 0);
                break;
        }
    }

    private void doubleTapsQuit() {
        bottomSheetDialog = new BottomSheetDialog(MainActivity_F.this);
        View dialogView = View.inflate(MainActivity_F.this, R.layout.dialog_action, null);
        TextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(R.string.toast_quit);
        Button action_ok = dialogView.findViewById(R.id.action_ok);
        action_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button action_cancel = dialogView.findViewById(R.id.action_cancel);
        action_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                backTmp = false;
            }
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    @Override
    public void openUrl(final String url) {
        isBrowser = true;

        if (!isCH) {
            tmpUrl = url;
        }

        setSelected(2);
        final Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_content);

        if (current != null && current instanceof BrowserFragment) {
            ((BrowserFragment) current).setGone();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!(current instanceof BrowserFragment)) {
                    setSelected(2);
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);

                    if (fragment != null && fragment instanceof BrowserFragment) {
                        ((BrowserFragment) fragment).setGone();
                    }
                }

                if (iSearch != null) {
                    iSearch.search(url, false);
                }
            }
        }, 0);
    }

    private void showOverSetting() {
        final Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (current instanceof BrowserFragment) {
            ninjaWebView = ((BrowserFragment) current).getNinjaWebView();
        }

        bottomSheetDialog = new BottomSheetDialog(MainActivity_F.this);
        view = View.inflate(MainActivity_F.this, R.layout.dialog_setting, null);
        LinearLayout exit = view.findViewById(R.id.setting_exit);
        LinearLayout login = view.findViewById(R.id.setting_login);
        LinearLayout setting = view.findViewById(R.id.setting_setting);
        LinearLayout download = view.findViewById(R.id.setting_download);
        LinearLayout share = view.findViewById(R.id.setting_share);
        LinearLayout collect = view.findViewById(R.id.setting_collect);
        LinearLayout scl = view.findViewById(R.id.setting_ch);
        LinearLayout srmx = view.findViewById(R.id.setting_srmx);
        LinearLayout yqhy = view.findViewById(R.id.setting_yqhy);
        LinearLayout sytx = view.findViewById(R.id.setting_sytx);
        LinearLayout rwzx = view.findViewById(R.id.setting_rwzx);
        setting_name = view.findViewById(R.id.setting_name);
        setting_icon = view.findViewById(R.id.setting_icon);
        if (iconUrl != null) {
            Picasso.with(MainActivity_F.this).load(iconUrl).into(setting_icon);
            setting_name.setText(userName);
        }
        srmx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity_F.this, "暂未开放", Toast.LENGTH_SHORT).show();
                showRemind();
            }
        });
        yqhy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity_F.this, "暂未开放", Toast.LENGTH_SHORT).show();
                showRemind();
            }
        });
        sytx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemind();
            }
        });
        rwzx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemind();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity_F.this, Settings_Activity.class);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepareRecord() && !(current instanceof BrowserFragment)) {
                    NinjaToast.show(MainActivity_F.this, getString(R.string.toast_share_failed));
                } else {
                    IntentUnit.share(MainActivity_F.this, ninjaWebView.getTitle(), ninjaWebView.getUrl());
                }
            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepareRecord() && !(current instanceof BrowserFragment)) {
                    NinjaToast.show(MainActivity_F.this, getString(R.string.toast_collect_failed));
                    return;
                }
                RecordAction action = new RecordAction(MainActivity_F.this);
                bottomSheetDialog.cancel();
                action.open(true);
                if (action.checkBookmark(ninjaWebView.getUrl())) {
                    NinjaToast.show(MainActivity_F.this, getString(R.string.toast_entry_exists));
                } else {
                    action.addBookmark(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), System.currentTimeMillis()));
                    NinjaToast.show(MainActivity_F.this, getString(R.string.toast_add_bookmark_successful));
                }
                action.close();
            }
        });
        scl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePosition = mCurrentSelectedFragmentPosition;
                Intent intent = new Intent(MainActivity_F.this, CollectionAndHistoryActivity.class);
                startActivityForResult(intent, 10);
                isCHStart = true;
                bottomSheetDialog.dismiss();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(MainActivity_F.this, LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void showRemind() {
        final CustomDialog dialog = new CustomDialog(MainActivity_F.this, R.style.customDialog, R.layout.dialog_center);
        dialog.show();
        TextView tvOk = dialog.findViewById(R.id.confirm);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private boolean prepareRecord() {
        if (ninjaWebView == null) {
            return true;
        }
        String title = ninjaWebView.getTitle();
        String url = ninjaWebView.getUrl();
        return (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT));
    }
}
