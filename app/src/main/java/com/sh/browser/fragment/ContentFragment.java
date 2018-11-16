package com.sh.browser.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sh.browser.R;
import com.sh.browser.UCAppliCation;
import com.sh.browser.adapter.ContentAdapter;
import com.sh.browser.cbhttp.MyOkHttp;
import com.sh.browser.cbhttp.response.GsonResponseHandler;
import com.sh.browser.models.Artical;
import com.sh.browser.models.Articals;
import com.sh.browser.views.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

public class ContentFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private RecyclerViewDivider itemDecoration;
    private MyOkHttp mOkhttp;
    private List<Artical> articals = new ArrayList<>();
    private ContentAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading;
    private Handler handler = new Handler();
    public static ContentFragment newInstance(String title, String channel_id) {

        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("channel_id", channel_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mOkhttp = UCAppliCation.getInstance().getMyOkHttp();
        mRecyclerView = mView.findViewById(R.id.main_fragment);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeRefreshlayout);
        mSwipeRefreshLayout.setEnabled(false);
        itemDecoration = new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL,2, Color.parseColor("#e2e2e2"));
        mRecyclerView.addItemDecoration(itemDecoration);
        manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        getData(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
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
                                getData(false);
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_main,null);
    }
    private void getData(final boolean isLoad) {
        String timestamp = "" + System.currentTimeMillis()/1000;
        if (!isLoad && articals.size() > 0) {
            timestamp = "" + articals.get(articals.size()-1).getTime_stamp();
        }
        mOkhttp.get().url("http://api.kcaibao.com/v1/get-channel-articles/caibao/" +  getArguments().getString("channel_id"))
                .addParam("timestamp", timestamp)
                .addParam("num", "10")
                .enqueue(new GsonResponseHandler<Articals>() {
                    @Override
                    public void onSuccess(int statusCode, Articals response) {
                        if (!isLoad) {
                            if (response.getData().size() == 0) {
                                mAdapter.setNodata();
                                mAdapter.notifyItemRemoved(mAdapter.getItemCount());
//                                Toast.makeText(mContext,"没有更多数据了",Toast.LENGTH_LONG).show();
                            } else {
                                articals.addAll(response.getData());
                            }
                        } else {
                            articals = response.getData();
                            mAdapter = new ContentAdapter(articals, mContext);
                            mRecyclerView.setAdapter(mAdapter);
                        }

                        mAdapter.notifyDataSetChanged();
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        mAdapter.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent();
                                intent.putExtra("artical_id", articals.get(position).getArticle_id());
                                intent.putExtra("time", articals.get(position).getTime());
                                intent.putExtra("title", articals.get(position).getArticle_name());
                                intent.putExtra("desc", articals.get(position).getDesc());
                                intent.putExtra("base_url", "http://api.kcaibao.com/article/");
                                if (articals.get(position).getCell_type() >0) {
                                    intent.putExtra("imgurl", articals.get(position).getImages()[0]);
                                }
                                showUrl.openUrl("http://api.kcaibao.com/article/" + articals.get(position).getArticle_id());
//                                intent.setClass(mContext, ArticalContentActivity.class);
//                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                });
    }

}