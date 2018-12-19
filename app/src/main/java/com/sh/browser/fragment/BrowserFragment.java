package com.sh.browser.fragment;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.zxing.activity.CaptureActivity;
import com.sh.browser.R;
import com.sh.browser.activities.MainActivity_F;
import com.sh.browser.browser.AdBlock;
import com.sh.browser.browser.AlbumController;
import com.sh.browser.browser.BrowserContainer;
import com.sh.browser.browser.BrowserController;
import com.sh.browser.browser.Cookie;
import com.sh.browser.browser.Javascript;
import com.sh.browser.database.RecordAction;
import com.sh.browser.interfaces.IBack;
import com.sh.browser.interfaces.ICanGo;
import com.sh.browser.interfaces.ISearch;
import com.sh.browser.services.ClearService;
import com.sh.browser.services.HolderService;
import com.sh.browser.utils.BrowserUnit;
import com.sh.browser.utils.HelperUnit;
import com.sh.browser.utils.IntentUnit;
import com.sh.browser.utils.ViewUnit;
import com.sh.browser.views.BaiduProgressBar;
import com.sh.browser.views.CommonPopupWindow;
import com.sh.browser.views.FullscreenHolder;
import com.sh.browser.views.NinjaRelativeLayout;
import com.sh.browser.views.NinjaToast;
import com.sh.browser.views.NinjaWebView;
import com.sh.browser.views.SwitcherPanel;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.sh.browser.activities.Main.REQ_QRCODE;

/**
 * 浏览器页面
 */
public class BrowserFragment extends BaseFragment implements BrowserController, View.OnClickListener, ICanGo, ISearch {
    private ImageButton searchUp;
    private ImageButton searchDown;
    private ImageButton searchCancel;
    private ImageButton omniboxRefresh;
    private AutoCompleteTextView inputBox;
    private ProgressBar progressBar;
    private BaiduProgressBar main_progress_anim;
    private ProgressBar progressBar2;
    private EditText searchBox;
    private SwitcherPanel switcherPanel;
    private BottomSheetDialog bottomSheetDialog;
    private NinjaWebView ninjaWebView;
    private TextView omniboxTitle;
    private View customView;
    private VideoView videoView;

    // Layouts

    private RelativeLayout omnibox;
    private RelativeLayout searchPanel;
    private FrameLayout contentFrame;
//    private LinearLayout switcherContainer;
    private NinjaRelativeLayout ninjaRelativeLayout;
    private FrameLayout fullscreenHolder;
    private String url;
    private BroadcastReceiver downloadReceiver;

    private SharedPreferences sp;
    private CommonPopupWindow window;
    private CommonPopupWindow.LayoutGravity layoutGravity;
    private static final float[] NEGATIVE_COLOR = {
            -1.0f, 0, 0, 0, 255, // Red
            0, -1.0f, 0, 0, 255, // Green
            0, 0, -1.0f, 0, 255, // Blue
            0, 0, 0, 1.0f, 0     // Alpha
    };

    public boolean prepareRecord() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            return true;
        }

        NinjaWebView webView = (NinjaWebView) currentAlbumController;
        String title = webView.getTitle();
        String url = webView.getUrl();
        return (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT));
    }

    public NinjaWebView getNinjaWebView() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            return null;
        }

        NinjaWebView webView = (NinjaWebView) currentAlbumController;
        return webView;
    }

    private int originalOrientation;
    private int shortAnimTime = 1000;
    private int start_tab;
    private float dimen144dp;
    private float dimen108dp;
    private float dimen16dp;
    private boolean create = true;
    private IBack iBack;

    private WebChromeClient.CustomViewCallback customViewCallback;
    private ValueCallback<Uri[]> filePathCallback = null;
    private AlbumController currentAlbumController = null;

    private Bitmap bitmap;
    @Override
    public void goBack() {
        if (ninjaWebView.canGoBack() && omnibox.getVisibility() == View.VISIBLE) {
            ninjaWebView.goBack();
        } else {
            iBack.back();
        }
    }

    @Override
    public void goForward() {
        if (ninjaWebView.canGoForward()) {
            ninjaWebView.goForward();
        }
    }

    @Override
    public void search(String query, boolean search) {
        if (search) {
            omnibox.setVisibility(View.VISIBLE);
            inputBox.setText(query);
        } else {
            omnibox.setVisibility(View.GONE);
        }
        updateAlbum(query);
    }

    // Classes

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }
    }

    /*************              窗口管理逻辑         ***************/
    private TextView cancel;
    private TextView clearAll;
    private LinearLayout switcher_container;
    private ImageView setting,add_window;
    private RelativeLayout windows;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_bro_win, null);
    }

    @Override
    protected void initViews() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        HelperUnit.setTheme(mContext);

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (getArguments().getByteArray("bitmap") != null) {
            byte[] b = getArguments().getByteArray("bitmap");
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        start_tab = BrowserUnit.FLAG_HISTORY;

        if (sp.getString("saved_key_ok", "no").equals("no")) {
            char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!§$%&/()=?;:_-.,+#*<>".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 25; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }

            if (Locale.getDefault().getCountry().equals("CN")) {
                sp.edit().putString(getString(R.string.sp_search_engine), "2").apply();
            }

            sp.edit().putString("saved_key", sb.toString()).apply();
            sp.edit().putString("saved_key_ok", "yes").apply();
            sp.edit().putBoolean(getString(R.string.sp_location), false).apply();
        }

        sp.edit().putInt("restart_changed", 0).apply();

        contentFrame = mView.findViewById(R.id.main_content);
        create = true;
        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        switcherPanel = mView.findViewById(R.id.switcher_panel);
        switcherPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = switcherPanel.getRootView().getHeight() - switcherPanel.getHeight();
                if (currentAlbumController != null) {
                    if (heightDiff > 100) {
                        omniboxTitle.setVisibility(View.GONE);
                    } else {
                        omniboxTitle.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen16dp = getResources().getDimensionPixelOffset(R.dimen.layout_margin_16dp);

        initSwitcherView();
        initOmnibox();
        initSearchPanel();

//        new AdBlock(mContext); // For AdBlock cold boot
        new Javascript(mContext);

        try {
            new Cookie(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            mContext.deleteDatabase("Ninja4.db");
            mContext.recreate();
        }
        initWindows();
        dispatchIntent(mContext.getIntent());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                searchBox.requestFocus();
            }
        }, 500);

        downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                bottomSheetDialog = new BottomSheetDialog(mContext);
                View dialogView = View.inflate(mContext, R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_downloadComplete);
                Button action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        bottomSheetDialog.cancel();
                    }
                });
                Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(downloadReceiver, filter);

        initPopWindow();
//
//        windows.setVisibility(View.VISIBLE);
//        switcherPanel.setVisibility(View.GONE);
//        ((MainActivity_F)mContext).setBottomGone();
    }

    public void setGone(){
        switcherPanel.setVisibility(View.GONE);
        windows.setVisibility(View.GONE);
    }

    public void setGone2(){
        switcherPanel.setVisibility(View.VISIBLE);
        windows.setVisibility(View.GONE);
    }


    public void setVisible() {
        switcherPanel.setVisibility(View.GONE);
        windows.setVisibility(View.VISIBLE);
    }


    /**
     * 初始化窗口
     */
    private void initWindows() {
    //        byte[] bitmaps = getArguments().getByteArray("bitmaps");
    //        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmaps,0,bitmaps.length);
        cancel = mView.findViewById(R.id.window_back);
        clearAll = mView.findViewById(R.id.window_all_clear);
        switcher_container = mView.findViewById(R.id.switcher_container);
        setting = mView.findViewById(R.id.windows_manager_setting);
        add_window = mView.findViewById(R.id.new_window);
        windows = mView.findViewById(R.id.windows);
        cancel.setOnClickListener(this);
        clearAll.setOnClickListener(this);
        setting.setOnClickListener(this);
        add_window.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        boolean clearIndexedDB = sp.getBoolean(("sp_clearIndexedDB"), false);
        if (clearIndexedDB) {
            BrowserUnit.clearIndexedDB(mContext);
        }

        Intent toHolderService = new Intent(mContext, HolderService.class);
        IntentUnit.setClear(true);
        mContext.stopService(toHolderService);

        boolean exit = true;
        if (sp.getBoolean(getString(R.string.sp_clear_quit), false)) {
            Intent toClearService = new Intent(mContext, ClearService.class);
            mContext.startService(toClearService);
            exit = false;
        }

        BrowserContainer.clear();
        IntentUnit.setContext(null);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if (currentAlbumController != null && currentAlbumController instanceof NinjaRelativeLayout) {
            ninjaRelativeLayout = (NinjaRelativeLayout) currentAlbumController;
        } else if (currentAlbumController != null && currentAlbumController instanceof NinjaWebView) {
            ninjaWebView = (NinjaWebView) currentAlbumController;
            try {
                url = ninjaWebView.getUrl().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (v.getId()) {

            case R.id.switcher_plus:
                addAlbum(start_tab);
                break;

            case R.id.omnibox_refresh:
                if (currentAlbumController == null) {
                    NinjaToast.show(mContext, getString(R.string.toast_refresh_failed));
                    return;
                }

                if (currentAlbumController instanceof NinjaWebView) {
                    ninjaWebView = (NinjaWebView) currentAlbumController;
                    ninjaWebView.stopLoading();
                    if (ninjaWebView.isLoadFinish()) {

                        if (!url.startsWith("https://")) {
                            bottomSheetDialog = new BottomSheetDialog(mContext);
                            View dialogView2 = View.inflate(mContext, R.layout.dialog_action, null);
                            TextView textView2 = dialogView2.findViewById(R.id.dialog_text);
                            textView2.setText(R.string.toast_unsecured);
                            Button action_ok2 = dialogView2.findViewById(R.id.action_ok);
                            action_ok2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bottomSheetDialog.cancel();
                                    ninjaWebView.loadUrl(url.replace("http://", "https://"));
                                }
                            });
                            Button action_cancel2 = dialogView2.findViewById(R.id.action_cancel);
                            action_cancel2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bottomSheetDialog.cancel();
                                    ninjaWebView.reload();
                                }
                            });
                            bottomSheetDialog.setContentView(dialogView2);
                            bottomSheetDialog.show();
                        } else {
                            ninjaWebView.reload();
                        }


                    } else {
                        ninjaWebView.stopLoading();
                    }
                } else {
                    NinjaToast.show(mContext, getString(R.string.toast_refresh_failed));
                }
                break;
            case R.id.window_back:
                ((MainActivity_F)mContext).setBottomGone();
                break;
            case R.id.window_all_clear:
                switcher_container.removeAllViews();
                setWindows();
                break;
            case R.id.windows_manager_setting:
                layoutGravity.setHoriGravity(CommonPopupWindow.LayoutGravity.CENTER_HORI);
                window.showBashOfAnchor(setting, layoutGravity, 0, 0);
                break;
            case R.id.new_window:
                addAlbum(BrowserUnit.FLAG_NINJA);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(mContext, CaptureActivity.class), REQ_QRCODE);
            } else {
                // Permission Denied
                Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("onResume", " query : " + getArguments().getString("query"));
//        switcherPanel.setVisibility(View.VISIBLE);
//        windows.setVisibility(View.GONE);
        IntentUnit.setContext(mContext);
        if (create) {
            return;
        }

        if (IntentUnit.isDBChange()) {
            updateBookmarks();
            updateAutoComplete();
            IntentUnit.setDBChange(false);
        }

        if (IntentUnit.isSPChange()) {
            for (AlbumController controller : BrowserContainer.list()) {
                if (controller instanceof NinjaWebView) {
                    ((NinjaWebView) controller).initPreferences();
                }
            }
            IntentUnit.setSPChange(false);
        }

        if (sp.getInt("restart_changed", 1) == 1) {
            sp.edit().putInt("restart_changed", 0).apply();
            mContext.finish();
        }

        if (sp.getBoolean("pdf_create", false)) {

            bottomSheetDialog = new BottomSheetDialog(mContext);
            View dialogView = View.inflate(mContext, R.layout.dialog_action, null);
            TextView textView = dialogView.findViewById(R.id.dialog_text);

            Button action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    bottomSheetDialog.cancel();
                }
            });
            Button action_cancel = dialogView.findViewById(R.id.action_cancel);
            action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.cancel();
                }
            });
            bottomSheetDialog.setContentView(dialogView);

            final File pathFile = new File(sp.getString("pdf_path", ""));

            if (sp.getBoolean("pdf_share", false)) {

                if (pathFile.exists() && !sp.getBoolean("pdf_delete", false)) {
                    sp.edit().putBoolean("pdf_delete", true).commit();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, pathFile.getName());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, pathFile.getName());
                    sharingIntent.setType("*/pdf");
                    Uri bmpUri = Uri.fromFile(pathFile);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.menu_share)));
                } else if (pathFile.exists() && sp.getBoolean("pdf_delete", false)) {
                    pathFile.delete();
                    sp.edit().putBoolean("pdf_create", false).commit();
                    sp.edit().putBoolean("pdf_share", false).commit();
                    sp.edit().putBoolean("pdf_delete", false).commit();
                } else {
                    sp.edit().putBoolean("pdf_create", false).commit();
                    sp.edit().putBoolean("pdf_share", false).commit();
                    sp.edit().putBoolean("pdf_delete", false).commit();

                    textView.setText(R.string.menu_share_pdfToast);
                    bottomSheetDialog.show();
                }

            } else {

                textView.setText(R.string.toast_downloadComplete);
                bottomSheetDialog.show();
                sp.edit().putBoolean("pdf_share", false).commit();
                sp.edit().putBoolean("pdf_create", false).commit();
                sp.edit().putBoolean("pdf_delete", false).commit();
            }
        }

        if (sp.getBoolean("delete_screenshot", false)) {
            File pathFile = new File(sp.getString("screenshot_path", ""));

            if (pathFile.exists()) {
                pathFile.delete();
                sp.edit().putBoolean("delete_screenshot", false).commit();
            }
        }

//        dispatchIntent(mContext.getIntent());
    }

    @Override
    public void onPause() {
        switcherPanel.setVisibility(View.GONE);
        windows.setVisibility(View.GONE);
        Intent toHolderService = new Intent(mContext, HolderService.class);
        IntentUnit.setClear(false);
        mContext.stopService(toHolderService);
        create = false;
        IntentUnit.setContext(mContext);
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        float coverHeight = ViewUnit.getWindowHeight(mContext) - ViewUnit.getStatusBarHeight(mContext) - dimen108dp - dimen16dp;
        hideSoftInput(inputBox);
        hideSearchPanel();
        switcherPanel.expanded();
        switcherPanel.setCoverHeight(coverHeight);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentAlbumController != null && currentAlbumController instanceof NinjaRelativeLayout) {
                    omniboxRefresh.performClick();
                }
            }
        }, shortAnimTime);

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public synchronized void showAlbum(AlbumController controller, final boolean expand) {

        if (currentAlbumController != null) {
            final View av = (View) controller;

            contentFrame.removeAllViews();
            contentFrame.addView(av);
        } else {
            if (contentFrame == null) {
                return;
            } else {
                contentFrame.removeAllViews();
                contentFrame.addView((View) controller);
            }
        }

        currentAlbumController = controller;
        windows.setVisibility(View.GONE);
        switcherPanel.setVisibility(View.VISIBLE);
        ((MainActivity_F)mContext).setBottomVisible();

        if (controller.getAlbumTitle().equals("浏览器")) {
            ((MainActivity_F)mContext).setBottomGone();
            return;
        }
        contentFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, Bitmap.Config.RGB_565));
            }
        }, shortAnimTime);

        setWindows();
    }

    @Override
    public void updateAutoComplete() {
    }

    @Override
    public void updateBookmarks() {
        RecordAction action = new RecordAction(mContext);
        action.open(false);
        action.close();
    }

    @Override
    public void updateInputBox(String query) {
        if (query != null) {
            inputBox.setText(query);
        } else {
            inputBox.setText(null);
        }
    }

    private void dispatchIntent(Intent intent) {
        Intent toHolderService = new Intent(mContext, HolderService.class);
        IntentUnit.setClear(false);
        mContext.stopService(toHolderService);

        String action = intent.getAction();

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_WEB_SEARCH)) {
            // From ActionMode and some others
            pinAlbums(intent.getStringExtra(SearchManager.QUERY));
        } else if (filePathCallback != null) {
            filePathCallback = null;
        } else if ("sc_history".equals(action)) {
            addAlbum(BrowserUnit.FLAG_HISTORY);
        } else if ("sc_bookmark".equals(action)) {
            addAlbum(BrowserUnit.FLAG_BOOKMARKS);
        } else if ("sc_login".equals(action)) {
            addAlbum(BrowserUnit.FLAG_PASS);
        } else if ("sc_startPage".equals(action)) {
            addAlbum(BrowserUnit.FLAG_HOME);
        } else if (Intent.ACTION_SEND.equals(action)) {
            pinAlbums(intent.getStringExtra(Intent.EXTRA_TEXT));
        } else {
            pinAlbums(null);
        }
        mContext.getIntent().setAction("");
        Log.i("Browsercontainer","pine size: " + BrowserContainer.size());
    }

    private void initRendering(View view) {

        if (currentAlbumController instanceof NinjaWebView && sp.getBoolean("sp_invert", false)) {
            Paint paint = new Paint();
            ColorMatrix matrix = new ColorMatrix();
            matrix.set(NEGATIVE_COLOR);
            ColorMatrix gcm = new ColorMatrix();
            gcm.setSaturation(0);
            ColorMatrix concat = new ColorMatrix();
            concat.setConcat(matrix, gcm);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(concat);
            paint.setColorFilter(filter);
            // maybe sometime LAYER_TYPE_NONE would better?
            view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        } else {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private void initSwitcherView() {
        ninjaWebView = (NinjaWebView) currentAlbumController;
    }

    private void initOmnibox() {

        omnibox = mView.findViewById(R.id.main_omnibox);
        omnibox.setVisibility(View.GONE);
        inputBox = mView.findViewById(R.id.main_omnibox_input);
        omniboxRefresh = mView.findViewById(R.id.omnibox_refresh);
        omniboxTitle = mView.findViewById(R.id.omnibox_title);
        progressBar = mView.findViewById(R.id.main_progress_bar);
        main_progress_anim = mView.findViewById(R.id.main_progress_anim);
        progressBar2 = mView.findViewById(R.id.main_progress);
        progressBar2.setVisibility(View.VISIBLE);
        main_progress_anim.setVisibility(View.VISIBLE);
        inputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title",inputBox.getText().toString());
                ((MainActivity_F)mContext).transcationFragment(0,bundle);
            }
        });

        updateBookmarks();
        updateAutoComplete();

        omniboxRefresh.setOnClickListener(this);
    }

    private void initSearchPanel() {
        searchPanel = mView.findViewById(R.id.main_search_panel);
        searchBox = mView.findViewById(R.id.main_search_box);
        searchUp = mView.findViewById(R.id.main_search_up);
        searchDown = mView.findViewById(R.id.main_search_down);
        searchCancel = mView.findViewById(R.id.main_search_cancel);
        searchPanel.setVisibility(View.GONE);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentAlbumController != null && currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    NinjaToast.show(mContext, getString(R.string.toast_input_empty));
                    return true;
                }
                return false;
            }
        });

        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    NinjaToast.show(mContext, getString(R.string.toast_input_empty));
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findNext(false);
                }
            }
        });

        searchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    NinjaToast.show(mContext, getString(R.string.toast_input_empty));
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findNext(true);
                }
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchPanel();
            }
        });
    }

    private synchronized void addAlbum(int flag) {
//      showOmnibox();
        final AlbumController holder;
        NinjaRelativeLayout layout = new NinjaRelativeLayout(mContext);
        layout.setBrowserController(this);
        layout.setFlag(flag);
        layout.setAlbumTitle(getString(R.string.app_name));
        if (bitmap != null) {
            layout.setAlbumCover(bitmap);
        }
        holder = layout;

        View albumView = holder.getAlbumView();
        BrowserContainer.add(holder);
        switcher_container.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        showAlbum(holder, false);
        setWindows();
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {

//        showOmnibox();
        ninjaWebView = new NinjaWebView(mContext);
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
        ninjaWebView.setAlbumTitle(title);
        if (bitmap != null) {
            ninjaWebView.setAlbumCover(bitmap);
        }
        ViewUnit.bound(mContext, ninjaWebView);

        final View albumView = ninjaWebView.getAlbumView();
        if (currentAlbumController != null && (currentAlbumController instanceof NinjaWebView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(ninjaWebView, index);
            switcher_container.addView(albumView, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(ninjaWebView);
            switcher_container.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        if (!foreground) {
            ViewUnit.bound(mContext, ninjaWebView);
            ninjaWebView.loadUrl(url);
//            ninjaWebView.deactivate();
            return;
        }

        showAlbum(ninjaWebView, true);

        if (url != null && !url.isEmpty()) {
            ninjaWebView.loadUrl(url);
        } else if (resultMsg != null) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(ninjaWebView);
            resultMsg.sendToTarget();
        }
    }

    private synchronized void pinAlbums(String url) {
//        showOmnibox();
        hideSoftInput(inputBox);
        hideSearchPanel();
        switcher_container.removeAllViews();

        ninjaWebView = new NinjaWebView(mContext);

        for (AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof NinjaWebView) {
                ((NinjaWebView) controller).setBrowserController(this);
            } else if (controller instanceof NinjaRelativeLayout) {
                ((NinjaRelativeLayout) controller).setBrowserController(this);
            }
//            switcher_container.addView(controller.getAlbumView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            controller.getAlbumView().setVisibility(View.VISIBLE);
//            controller.deactivate();
        }

        Log.i("Browsercontainer","size: " + BrowserContainer.size());
        if (BrowserContainer.size() < 1 && url == null) {
            addAlbum(start_tab);
        } else if (BrowserContainer.size() >= 1 && url == null) {
            if (currentAlbumController != null) {
//                currentAlbumController.activate();
                return;
            }

            int index = BrowserContainer.size() - 1;
            currentAlbumController = BrowserContainer.get(index);
            contentFrame.removeAllViews();
//            contentFrame.addView((View) currentAlbumController);
//            currentAlbumController.activate();
            updateOmnibox();
        } else { // When url != null
            ninjaWebView.setBrowserController(this);
            ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
            ninjaWebView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUnit.bound(mContext, ninjaWebView);
            ninjaWebView.loadUrl(url);

            BrowserContainer.add(ninjaWebView);
            final View albumView = ninjaWebView.getAlbumView();
            switcher_container.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentFrame.removeAllViews();
            contentFrame.addView(ninjaWebView);

//            if (currentAlbumController != null) {
//                currentAlbumController.deactivate();
//            }
            currentAlbumController = ninjaWebView;
//            currentAlbumController.activate();

            updateOmnibox();
        }
    }

    private synchronized void updateAlbum(String url) {

        if (switcher_container.getChildCount() == 0) {
            final AlbumController holder;
            NinjaRelativeLayout layout = new NinjaRelativeLayout(mContext);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_NINJA);
            layout.setAlbumTitle(getString(R.string.app_name));
            if (bitmap != null) {
                layout.setAlbumCover(bitmap);
            }
            holder = layout;

            View albumView = holder.getAlbumView();
            BrowserContainer.add(holder);
            switcher_container.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            currentAlbumController = holder;
            setWindows();
        }

        omniboxTitle.setText(url);
        if (url == null) {
            windows.setVisibility(View.VISIBLE);
            switcherPanel.setVisibility(View.GONE);
        } else {
            windows.setVisibility(View.GONE);
            switcherPanel.setVisibility(View.VISIBLE);
            ((MainActivity_F)mContext).setBottomVisible();
        }

        if (url == null || url.trim().isEmpty()) {
            NinjaToast.show(mContext, R.string.toast_load_error);
            return;
        }

        if (!url.contains("://")) {
            url = BrowserUnit.queryWrapper(mContext, url.trim());
        }

        if (currentAlbumController == null) {
            return;
        }


        if (currentAlbumController instanceof NinjaWebView) {
            ((NinjaWebView) currentAlbumController).setWebViewClient(new WebViewClient(){
                @Override
                public void doUpdateVisitedHistory(WebView webView, String s, boolean b) {
                    super.doUpdateVisitedHistory(webView, s, b);
                    webView.clearHistory();
                }

                @Override
                public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                    super.onPageStarted(webView, s, bitmap);
                    Log.i("block_img","onPageStarted" + System.currentTimeMillis());
                    webView.getSettings().setBlockNetworkImage(true);
                }

                @Override
                public void onPageFinished(WebView webView, String s) {
                    super.onPageFinished(webView, s);
                    webView.getSettings().setBlockNetworkImage(false);
                    Log.i("block_img","onPageFinished" + System.currentTimeMillis());
                }
            });
            ((NinjaWebView) currentAlbumController).loadUrl(url);
            ((NinjaWebView) currentAlbumController).getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            ((NinjaWebView) currentAlbumController).setVisibility(View.GONE);
            updateOmnibox();
            Log.i("currenttime"," currentAlbumController time: " + System.currentTimeMillis());
        } else if (currentAlbumController instanceof NinjaRelativeLayout) {
            ninjaWebView = new NinjaWebView(mContext);
            ninjaWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
                @Override
                public void onProgressChanged(WebView webView, int i) {
                    super.onProgressChanged(webView, i);
                    ninjaWebView.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.VISIBLE);
                    main_progress_anim.setVisibility(View.VISIBLE);
                    Log.i("currenttime"," onProgressChanged time: " + i);
                    progressBar2.setProgress(i);
                    if (i == 100) {
                        progressBar2.setVisibility(View.GONE);
                        main_progress_anim.setVisibility(View.GONE);
                        ninjaWebView.setVisibility(View.VISIBLE);
                        contentFrame.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, Bitmap.Config.RGB_565));
                            }
                        }, shortAnimTime);
                    }
                }
            });
            ninjaWebView.setBrowserController(this);
            ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
            ninjaWebView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUnit.bound(mContext, ninjaWebView);

            int index = switcher_container.indexOfChild(currentAlbumController.getAlbumView());
//            currentAlbumController.deactivate();
            switcher_container.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeAllViews(); ///

            switcher_container.addView(ninjaWebView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(ninjaWebView);
            BrowserContainer.set(ninjaWebView, index);
            currentAlbumController = ninjaWebView;
//            ninjaWebView.activate();
            ninjaWebView.setVisibility(View.GONE);
            ninjaWebView.loadUrl(url);
            updateOmnibox();
            Log.i("currenttime","end time: " + System.currentTimeMillis());
        } else {
            NinjaToast.show(mContext, getString(R.string.toast_load_error));
        }
    }

    private void closeTabConfirmation(final Runnable okAction) {
        if (!sp.getBoolean("sp_close_tab_confirm", true)) {
            okAction.run();
        } else {    
            switcherPanel.expanded();
            bottomSheetDialog = new BottomSheetDialog(mContext);
            View dialogView = View.inflate(mContext, R.layout.dialog_action, null);
            TextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_close_tab);
            Button action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    okAction.run();
                    bottomSheetDialog.cancel();
                }
            });
            Button action_cancel = dialogView.findViewById(R.id.action_cancel);
            action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.cancel();
                }
            });
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        }
    }

    @Override
    public synchronized void removeAlbum(final AlbumController controller) {
        if (currentAlbumController == null || BrowserContainer.size() < 1) {

            if (currentAlbumController != null && currentAlbumController instanceof NinjaWebView) {
                closeTabConfirmation(new Runnable() {
                    @Override
                    public void run() {
                        switcher_container.removeView(controller.getAlbumView());
                        BrowserContainer.remove(controller);
                        pinAlbums(null);
                    }
                });
            } else {
                doubleTapsQuit();
            }
            setWindows();
            return;
        }

        if (controller != currentAlbumController) {
            if (currentAlbumController instanceof NinjaWebView) {
                closeTabConfirmation(new Runnable() {
                    @Override
                    public void run() {
                        switcher_container.removeView(controller.getAlbumView());
                        BrowserContainer.remove(controller);
                    }
                });
            } else {
                switcher_container.removeView(controller.getAlbumView());
                BrowserContainer.remove(controller);
            }


        } else {
            if (currentAlbumController instanceof NinjaWebView) {
                closeTabConfirmation(new Runnable() {
                    @Override
                    public void run() {
                        switcher_container.removeView(controller.getAlbumView());
                        int index = BrowserContainer.indexOf(controller);
                        BrowserContainer.remove(controller);
                        if (index >= BrowserContainer.size()) {
                            index = BrowserContainer.size() - 1;
                        }
//                        showAlbum(BrowserContainer.get(index), false);
                    }
                });
            } else {
                switcher_container.removeView(controller.getAlbumView());
                int index = BrowserContainer.indexOf(controller);
                BrowserContainer.remove(controller);
                if (index >= BrowserContainer.size()) {
                    index = BrowserContainer.size() - 1;
                }
//                showAlbum(BrowserContainer.get(index), false);
            }
        }
        setWindows();
    }

    private void updateOmnibox() {
        Log.i("currenttime","updateOmnibox time: " + System.currentTimeMillis());
        initRendering(contentFrame);
        //omniboxTitle.setText(currentAlbumController.getAlbumTitle());

        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof NinjaRelativeLayout) {
            updateProgress(BrowserUnit.PROGRESS_MAX);
            updateBookmarks();
            updateInputBox(null);
        } else if (currentAlbumController instanceof NinjaWebView) {
            ninjaWebView = (NinjaWebView) currentAlbumController;
            updateProgress(ninjaWebView.getProgress());
            updateBookmarks();
            scrollChange();
            if (ninjaWebView.getUrl() == null && ninjaWebView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (ninjaWebView.getUrl() != null) {
                updateInputBox(ninjaWebView.getUrl());
            } else {
                updateInputBox(ninjaWebView.getOriginalUrl());
            }
        }
        contentFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, Bitmap.Config.RGB_565));
            }
        }, shortAnimTime);

    }

    private void scrollChange() {
        if (sp.getString("sp_hideToolbar", "0").equals("0") ||
                sp.getString("sp_hideToolbar", "0").equals("1")) {
        }
    }

    @Override
    public synchronized void updateProgress(int progress) {

        progressBar.setProgress(progress);

        updateBookmarks();
        if (progress < BrowserUnit.PROGRESS_MAX) {
            updateRefresh(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(mContext, R.drawable.ic_action_close));
        } else {
            if (currentAlbumController instanceof NinjaWebView) {
                try {
                    if (ninjaWebView.getUrl().contains("https://")) {
                        omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(mContext, R.drawable.ic_action_refresh));
                    } else {
                        omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(mContext, R.drawable.icon_alert));
                    }
                } catch (Exception e) {
                    omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(mContext, R.drawable.ic_action_refresh));
                }
            } else if (currentAlbumController instanceof NinjaRelativeLayout) {
                omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(mContext, R.drawable.ic_action_refresh));
            }
        }
    }


    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        this.filePathCallback = filePathCallback;
        try {
            Intent intent = fileChooserParams.createIntent();
            startActivityForResult(intent, IntentUnit.REQUEST_FILE_21);
        } catch (Exception e) {
            NinjaToast.show(mContext, getString(R.string.toast_open_file_manager_failed));
        }
    }

    @Override
    public void onCreateView(WebView view, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
//        switcherPanel.collapsed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addAlbum(getString(R.string.album_untitled), null, true, resultMsg);
            }
        }, shortAnimTime);
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (view == null) {
            return;
        }
        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }

        customView = view;
        originalOrientation = mContext.getRequestedOrientation();

        fullscreenHolder = new FullscreenHolder(mContext);
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) mContext.getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        customView.setKeepScreenOn(true);
        ((View) currentAlbumController).setVisibility(View.GONE);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                videoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                videoView.setOnErrorListener(new VideoCompletionListener());
                videoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        customViewCallback = callback;
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onHideCustomView() {
        if (customView == null || customViewCallback == null || currentAlbumController == null) {
            return false;
        }

        FrameLayout decorView = (FrameLayout) mContext.getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeView(fullscreenHolder);
        }

        customView.setKeepScreenOn(false);
        ((View) currentAlbumController).setVisibility(View.VISIBLE);
        setCustomFullscreen(false);

        fullscreenHolder = null;
        customView = null;
        if (videoView != null) {
            videoView.setOnErrorListener(null);
            videoView.setOnCompletionListener(null);
            videoView = null;
        }
        mContext.setRequestedOrientation(originalOrientation);
        ninjaWebView.reload();

        return true;
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(currentAlbumController instanceof NinjaWebView)) {
            return;
        }
        result = ((NinjaWebView) currentAlbumController).getHitTestResult();

        bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = View.inflate(mContext, R.layout.dialog_menu_link, null);

        if (url != null || (result != null && result.getExtra() != null)) {
            if (url == null) {
                url = result.getExtra();
            }
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        }

        final String target = url;

        TextView tv_title = dialogView.findViewById(R.id.dialog_title);
        tv_title.setText(url);

        LinearLayout tv3_main_menu_new_tab = dialogView.findViewById(R.id.tv3_main_menu_new_tab);
        tv3_main_menu_new_tab.setVisibility(View.VISIBLE);
        tv3_main_menu_new_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(getString(R.string.album_untitled), target, false, null);
                NinjaToast.show(mContext, getString(R.string.toast_new_tab_successful));
                bottomSheetDialog.cancel();
            }
        });

        LinearLayout tv3_main_menu_share_link = dialogView.findViewById(R.id.tv3_main_menu_share_link);
        tv3_main_menu_share_link.setVisibility(View.VISIBLE);
        tv3_main_menu_share_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prepareRecord()) {
                    NinjaToast.show(mContext, getString(R.string.toast_share_failed));
                } else {
                    IntentUnit.share(mContext, "", target);
                }
                bottomSheetDialog.cancel();
            }
        });

        LinearLayout tv3_main_menu_open_link = dialogView.findViewById(R.id.tv3_main_menu_open_link);
        tv3_main_menu_open_link.setVisibility(View.VISIBLE);
        tv3_main_menu_open_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(target));
                Intent chooser = Intent.createChooser(intent, getString(R.string.menu_open_with));
                startActivity(chooser);
                bottomSheetDialog.cancel();
            }
        });

        LinearLayout tv3_main_menu_new_tabOpen = dialogView.findViewById(R.id.tv3_main_menu_new_tabOpen);
        tv3_main_menu_new_tabOpen.setVisibility(View.VISIBLE);
        tv3_main_menu_new_tabOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinAlbums(target);
                bottomSheetDialog.cancel();
            }
        });
    }


    private void doubleTapsQuit() {
    }

    private void hideSoftInput(final EditText view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        omniboxTitle.setVisibility(View.VISIBLE);
        searchBox.setText("");
        searchPanel.setVisibility(View.GONE);
//        omnibox.setVisibility(View.VISIBLE);
    }

    private void setCustomFullscreen(boolean fullscreen) {
        View decorView = mContext.getWindow().getDecorView();
        if (fullscreen) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            showOmnibox();
        }
    }

    private AlbumController nextAlbumController(boolean next) {
        if (BrowserContainer.size() <= 1) {
            return currentAlbumController;
        }

        List<AlbumController> list = BrowserContainer.list();
        int index = list.indexOf(currentAlbumController);
        if (next) {
            index++;
            if (index >= list.size()) {
                index = 0;
            }
        } else {
            index--;
            if (index < 0) {
                index = list.size() - 1;
            }
        }

        return list.get(index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IBack) {
            iBack = (IBack) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iBack = null;
    }

    private void setWindows() {
        ((MainActivity_F)mContext).setWindows(switcher_container.getChildCount());
    }

    private void initPopWindow(){
        layoutGravity=new CommonPopupWindow.LayoutGravity(CommonPopupWindow.LayoutGravity.ALIGN_LEFT);
        window=new CommonPopupWindow(mContext, R.layout.popupwindow_window, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                View view=getContentView();
                final TextView open = view.findViewById(R.id.close_browser_history);

                open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isNoHis = sp.getBoolean("history",false);
                        if (!isNoHis) {
                            sp.edit().putBoolean("history", true).apply();
                            open.setText("关闭无痕浏览");
                        } else {
                            sp.edit().putBoolean("history", false).apply();
                            open.setText("打开无痕浏览");
                        }
                        window.dismiss();
                    }
                });
            }

            @Override
            protected void initEvent() {}
        };
    }


}
