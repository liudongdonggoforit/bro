package com.sh.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh.browser.R;
import com.sh.browser.activities.VideoDetailActivity;
import com.sh.browser.models.VideoInfo;
import com.sh.browser.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2017/3/23.
 */
public class CardRecycleViewAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private List<VideoInfo> videoInfos;
    private Context mContext;

    public CardRecycleViewAdapter(Context context, List<VideoInfo> infos) {
        videoInfos = infos;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return videoInfos.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_remove, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        Log.i("onBindViewHolder", "url :" + Constants.VIDE_BASE_URL + videoInfos.get(position).videoPath.trim());
        Picasso.with(mContext).load(Constants.VIDE_BASE_URL + videoInfos.get(position).picPath.trim()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.title = "";
                videoInfo.videoPath = Constants.VIDE_BASE_URL + videoInfos.get(position).videoPath.trim();
                VideoDetailActivity.start(mContext, videoInfo);
            }
        });
    }
}
