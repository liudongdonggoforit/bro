package com.sh.browser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sh.browser.R;
import com.sh.browser.adapter.CardRecycleViewAdapter;
import com.sh.browser.models.VideoInfo;
import com.sh.browser.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.lfrecyclerview.LFRecyclerView;

/**
 * 浏览器短视频内容
 */
public class VideosFragment extends BaseFragment implements LFRecyclerView.LFRecyclerViewListener{

    private LFRecyclerView videoRecyclerView;
    private List<VideoInfo> videoInfos = new ArrayList<>();
    private CardRecycleViewAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_videos, null);
    }

    @Override
    protected void initViews() {
        super.initViews();

        videoRecyclerView = mView.findViewById(R.id.video_recyclerView);
        videoRecyclerView.setLoadMore(true);
        videoRecyclerView.setRefresh(false);
        videoRecyclerView.setLFRecyclerViewListener(this);
        try {
            JSONArray array = new JSONArray(Constants.VIDEOES);
            for (int i = 0; i < 10; i++) {
                VideoInfo videoInfo = new VideoInfo();
                JSONObject object = array.getJSONObject(i);
                videoInfo.picPath = object.getString("pic");
                videoInfo.videoPath = object.getString("video");
                videoInfos.add(i, videoInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new CardRecycleViewAdapter(mContext, videoInfos);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        videoRecyclerView.setLayoutManager(layoutManager);
        videoRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

        videoRecyclerView.stopLoadMore();
        try {
            JSONArray array = new JSONArray(Constants.VIDEOES);
            if (videoInfos.size() + 10 > array.length()) {
                Toast.makeText(mContext, "没有更多数据了！", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = videoInfos.size(); i < videoInfos.size() + 10; i++) {
                VideoInfo videoInfo = new VideoInfo();
                JSONObject object = array.getJSONObject(i);
                videoInfo.picPath = object.getString("pic");
                videoInfo.videoPath = object.getString("video");
                videoInfos.add(i, videoInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyItemRangeInserted(videoInfos.size() - 1, 10);
    }
}
