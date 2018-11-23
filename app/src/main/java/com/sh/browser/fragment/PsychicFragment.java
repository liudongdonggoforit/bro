package com.sh.browser.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sh.browser.R;
import com.sh.browser.adapter.PsychicAdapter;
import com.sh.browser.models.Artical;
import com.sh.browser.views.RecyclerViewDivider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PsychicFragment extends BaseFragment {

    private static final String TAG = "PsychicFragment";

    private View mView;
    private RecyclerView mRyMain;
    private RecyclerViewDivider itemDecoration;
    private List<Artical> articals = new ArrayList<>();
    private static PsychicAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading;
    private Handler handler = new Handler();
    private LinearLayoutManager layoutManager;
    private static final int ON_DATA_SUCCESS_INIT = 1;
    private int index = 1;
    private AlertDialog alertDialog; //加载对话框
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_DATA_SUCCESS_INIT:
                    mAdapter.notifyDataSetChanged();
                    mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                    break;
            }
        }
    };

    public static PsychicFragment newInstance(String title, String channel_id) {

        PsychicFragment fragment = new PsychicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("channel_id", channel_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_main,null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mRyMain = mView.findViewById(R.id.main_fragment);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeRefreshlayout);
        mSwipeRefreshLayout.setEnabled(false);
        itemDecoration = new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL,2, Color.parseColor("#e2e2e2"));
        layoutManager = new LinearLayoutManager(mContext);
        mRyMain.addItemDecoration(itemDecoration);
        mRyMain.setLayoutManager(layoutManager);
        mAdapter = new PsychicAdapter(articals, mContext);
        mRyMain.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PsychicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("artical_id", articals.get(position).getArticle_id());
                intent.putExtra("time", articals.get(position).getTime());
                intent.putExtra("title", articals.get(position).getArticle_name());
                intent.putExtra("desc", articals.get(position).getDesc());
                intent.putExtra("base_url", articals.get(position).getBase_url());
                if (articals.get(position).getCell_type() >0) {
                    intent.putExtra("imgurl", articals.get(position).getImages()[0]);
                }
//                intent.setClass(mContext, ArticalContentActivity.class);
//                startActivity(intent);
                showUrl.openUrl(articals.get(position).getBase_url() + articals.get(position).getArticle_id());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        index = 1;
        fetchData(ON_DATA_SUCCESS_INIT,"https://www.51qumi.com/lysj/", index);
        mRyMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                index++;
                                fetchData(ON_DATA_SUCCESS_INIT,"https://51qumi.com/lysj/",
                                        ++index);
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }



    private void fetchData(final int event, final String url, final int index) {
        if (index ==0) {
            showLoadingDialog();
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                String qUrl = "";
                if (index != 1) {
                    qUrl = url + "index_" + index + ".html";
                }else {
                    qUrl = url + "index.html";
                }

                Connection connect = Jsoup.connect(qUrl);
                Document document = null;
                try {
                    document = connect.get();
                    Elements elementsByClass = document.getElementsByClass("fo-list");
                    Element element = elementsByClass.get(0);
                    Elements elements = element.getElementsByTag("li");
                    for (Element eles : elements) {
                        Elements elementsByTag = eles.getElementsByTag("a");
                        Element element1 = elementsByTag.get(0);
                        String href = element1.attr("href");
                        String title = element1.attr("title");
                        Log.i(TAG,href);
                        Log.i(TAG,title);

                        Elements zpimg = element1.getElementsByClass("zpimg");
                        String src = zpimg.attr("data-original");
                        Log.i(TAG,src);

                        Elements dd = eles.getElementsByTag("dd");
                        List<String> eachText = dd.eachText();
                        String timeText = "";
                        String desc = "";
                        if (eachText.size() > 0) {
                            timeText = eachText.get(0);
                        }

                        if (eachText.size() > 1) {
                            desc = eachText.get(1);
                        }

                        Log.i(TAG,timeText);
                        Log.i(TAG,desc);

                        Artical artical = new Artical();
                        artical.setArticle_from("未解之谜");
                        artical.setDesc(title);
                        artical.setTime(timeText);
                        artical.setArticle_id(href);
                        artical.setImages(new String[]{src});
                        artical.setBase_url("https://m.51qumi.com");
                        artical.setArticle_name(title);
                        artical.setCell_type(1);
                        articals.add(artical);
                    }

                    mHandler.sendEmptyMessage(event);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                        }
                    });
                }

            }
        }.start();



    }
    //Android loading 对话框
    public void showLoadingDialog() {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        alertDialog.show();
        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    //取消loading
    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
