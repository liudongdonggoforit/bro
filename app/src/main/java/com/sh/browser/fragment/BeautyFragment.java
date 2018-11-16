package com.sh.browser.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sh.browser.R;
import com.sh.browser.UCAppliCation;
import com.sh.browser.activities.BigImgActivity;
import com.sh.browser.adapter.BeautyAdapter;
import com.sh.browser.cbhttp.MyOkHttp;
import com.sh.browser.cbhttp.response.GsonResponseHandler;
import com.sh.browser.models.Artical;
import com.sh.browser.models.SogouImg;
import com.sh.browser.views.RecyclerViewDivider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class BeautyFragment extends BaseFragment {

    private static final String TAG = "BeautyFragment";

    private View mView;
    private RecyclerView mRyMain;
    private RecyclerViewDivider itemDecoration;
    private List<Artical> articals = new ArrayList<>();
    private static BeautyAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading;
    private Handler handler = new Handler();
    private StaggeredGridLayoutManager layoutManager;
    private static final int ON_DATA_SUCCESS_INIT = 11;
    private int index = 0;
    private static int lastImgPosition = 0;
    private static int dataLength = 20;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_DATA_SUCCESS_INIT:
                    mResp = (SogouImg) msg.obj;
                    List<SogouImg.ImgItem> all_items = mResp.getAll_items();
                    if (all_items != null && mAll_items!= null) {
                        int index = lastImgPosition == 0 ? 0 : lastImgPosition - dataLength;
                        mAll_items.addAll(index,all_items);
                    }

                    mAdapter.setData(mAll_items);
                    mAdapter.notifyDataSetChanged();
//                    mRyMain.scrollToPosition(lastImgPosition == 0 ? 0 : lastImgPosition-dataLength);
//                    mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                    break;
            }
        }
    };
    private SogouImg mResp;
    private List<SogouImg.ImgItem> mAll_items = null;

    public static BeautyFragment newInstance(String title, String channel_id) {

        BeautyFragment fragment = new BeautyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("channel_id", channel_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_beauty,null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mRyMain = mView.findViewById(R.id.rv_img);
        mSwipeRefreshLayout = mView.findViewById(R.id.sr_img);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);

        itemDecoration = new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL,2, Color.parseColor("#e2e2e2"));
        layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
//        mRyMain.addItemDecoration(itemDecoration);
        mRyMain.setLayoutManager(layoutManager);
        mRyMain.setItemAnimator(new DefaultItemAnimator());
        mAll_items = new ArrayList<>();
        mAdapter = new BeautyAdapter(mAll_items, mContext);
        mRyMain.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BeautyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("imgurl", mAll_items.get(position).getPic_url());
                intent.setClass(mContext, BigImgActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mRyMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] into = new int[3];
                layoutManager.findLastVisibleItemPositions(into);
                int max = findMax(into);
                if (max + 1 == mAdapter.getItemCount()) {
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
                                fetchDataSogou(lastImgPosition,dataLength,ON_DATA_SUCCESS_INIT);
                                // load more
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastImgPosition = 0;
                fetchDataSogou(lastImgPosition,dataLength,ON_DATA_SUCCESS_INIT);
            }
        });

        lastImgPosition = 0;
        fetchDataSogou(lastImgPosition,dataLength,ON_DATA_SUCCESS_INIT);
//        fetchData(ON_DATA_SUCCESS_INIT,"http://umei.cc/p/gaoqing/rihan/",1);
    }

    private void fetchDataSogou(int start,int len,final int event) {
        lastImgPosition = start + len;
        MyOkHttp mOkhttp = UCAppliCation.getInstance().getMyOkHttp();
        String url = "https://pic.sogou.com/pics/channel/getAllRecomPicByTag.jsp?category=%E7%BE%8E%E5%A5%B3&tag=%E5%85%A8%E9%83%A8&";
        url = url + "start=" + start + "&len=" + len;
        mOkhttp.get()
                .url(url)
                .enqueue(new GsonResponseHandler<SogouImg>() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }

                    @Override
                    public void onSuccess(int statusCode, SogouImg response) {
                        mSwipeRefreshLayout.setRefreshing(false);
//                        mResp = response;
                        if (response != null) {
                            Message message = mHandler.obtainMessage();
                            message.what = event;
                            message.obj = response;
                            mHandler.sendMessage(message);
                        }
                    }
                });

    }


    private int findMax(int[] data) {
        if (data.length == 0) return -1;

        int[] tmp = data;
        int tmpInt = tmp[0];
        for(int i = 0;i< tmp.length;i++) {
            if (tmpInt < tmp[i]) {
                tmpInt = tmp[i];
            }
        }
        return tmpInt;
    }


    private void fetchData(final int event, final String url, final int index) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                for (int i = 0;i < 30;i++ ) {
                    Artical artical = new Artical();
                    artical.setCell_type(3);
                    artical.setImages(new String[] {"https://img01.sogoucdn.com/app/a/100520021/14c7f9f0871db2b1387a278c6042f36e"});
                    articals.add(artical);
                }
                //http://m.umei.cc/p/gaoqing/rihan/1.htm
                String qUrl = url + index + ".htm";

//                System.setProperty("http.maxRedirects", "50");
//                System.getProperties().setProperty("proxySet", "true");
//                System.getProperties().put("http.proxyHost", "m.umei.cc");
//                System.getProperties().put("http.proxyPort", "3128");//注意端口为String类型。

                Connection connect = Jsoup.connect(qUrl)
//                        .cookie("Cookie","Hm_lvt_c605a31292b623d214d012ec2a737685=1540260572; Hm_lpvt_c605a31292b623d214d012ec2a737685=1540260587; Hm_lvt_b149f4852e14a5bdd212c4c86068bbfd=1540260596,1540260660; Hm_lpvt_b149f4852e14a5bdd212c4c86068bbfd=1540261302")
                        .userAgent("Mozilla/5.0 (Linux; Android 8.0; Pixel 2 " +
                                "Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36")
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .method(Connection.Method.GET)
                        .timeout(5000)
                        ;
                Document document = null;
                try {
                    document = connect.get();
                    Elements elementsByClass = document.getElementsByClass("pic-list pic-list-tag pic-list-shadow");
                    Element element = elementsByClass.get(0);
                    Elements elements = element.getElementsByTag("li");
                    for (Element eles : elements) {
                        Elements elementsByTag = eles.getElementsByTag("a");
                        Element element1 = elementsByTag.get(0);
                        String href = element1.attr("href");
//                        String title = element1.attr("title");
                        Log.i(TAG,href);

                        Elements img = element1.getElementsByTag("img");
                        String src = img.attr("lazysrc");
                        String title = img.attr("alt");
                        Log.i(TAG,src);
                        Log.i(TAG,title);

//                        Elements time = element1.getElementsByTag("time");
//                        String timeText = time.eachText().get(0);
//                        Log.i(TAG,timeText);

                        Artical artical = new Artical();
                        artical.setArticle_from("图片");
                        artical.setDesc(title);
//                        artical.setTime(timeText);
                        artical.setArticle_id(href);
                        artical.setImages(new String[]{src});
//                        artical.setBase_url("https://m.51qumi.com");
                        artical.setArticle_name(title);
                        artical.setCell_type(3);
                        articals.add(artical);
                    }

                    mHandler.sendEmptyMessage(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();



    }


}
