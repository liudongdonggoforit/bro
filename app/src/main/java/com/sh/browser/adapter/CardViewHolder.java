package com.sh.browser.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sh.browser.R;


public class CardViewHolder extends RecyclerView.ViewHolder {

    public ImageView img,play;
    public RelativeLayout layout;
    public CardViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_play);
        img = itemView.findViewById(R.id.windows_img);
        play = itemView.findViewById(R.id.video_play);
    }
}
