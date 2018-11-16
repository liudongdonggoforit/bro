package com.sh.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sh.browser.R;
import com.sh.browser.models.SogouImg;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeautyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<SogouImg.ImgItem> datas = new ArrayList<>();
    private Context mContext;
    private boolean isNoData = false;
    public enum ITEM_TYPE {
        ITEM_TYPE_ZERO,
        ITEM_TYPE_ONE,
        ITEM_TYPE_THREE,
        ITEM_TYPE_FOOTER,
        ITEM_TYPE_NOFOOTER,
        ITEM_BIG_IMG
    }

    public void setData(List<SogouImg.ImgItem> data) {
        List<SogouImg.ImgItem> d = data;
        if (data != null) {
            datas.clear();
            datas.addAll(d);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public BeautyAdapter(List<SogouImg.ImgItem> data, Context context) {
        if (datas != null) {
            this.datas.addAll(data);
        }
        this.mContext = context;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_ZERO.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artical_ziro, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_ONE.ordinal()){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artical_one, parent, false);
            return new OneImgViewHolder(view);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_FOOTER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_NOFOOTER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot_nodata,parent, false);
            return new NoFootViewHolder(view);
        } else if(viewType == ITEM_TYPE.ITEM_BIG_IMG.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brauty_img, parent,
                    false);
            return new BigImgViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artical_three, parent, false);
            return new ThreeImgViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
       if (holder instanceof BigImgViewHolder) {
            Random random = new Random();
            ViewGroup.LayoutParams layoutParams = ((BigImgViewHolder) holder).mImageView.getLayoutParams();
            layoutParams.height=random.nextInt(100)+500;
           ((BigImgViewHolder) holder).mImageView.setLayoutParams(layoutParams);

            Picasso.with(mContext)
                    .load(datas.get(position).getPic_url())
                    .placeholder(R.drawable.img_default)
                    .error(R.drawable.img_default)
                    .into(((BigImgViewHolder) holder).mImageView);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position < datas.size()) {
            return ITEM_TYPE.ITEM_BIG_IMG.ordinal();
        }else{
            return ITEM_TYPE.ITEM_TYPE_FOOTER.ordinal();
        }

    }

    public void setNodata() {
        isNoData = true;
    }

    @Override
    public int getItemCount() {
        return datas.size() == 0 ? 0 : datas.size() + 1;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView title,source,comment,time;
        public View view;

        public TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.artical_from);
            comment =(TextView) itemView.findViewById(R.id.artical_comment_num);
            time = (TextView) itemView.findViewById(R.id.artical_update_time);
        }
    }

    public static class BigImgViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public View view;
        public BigImgViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mImageView = itemView.findViewById(R.id.iv_beauty);
        }
    }

    public static class OneImgViewHolder extends RecyclerView.ViewHolder {

        public TextView title,source,comment,time;
        public ImageView oneImg;
        public View view;

        public OneImgViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.artical_from);
            comment =(TextView) itemView.findViewById(R.id.artical_comment_num);
            time = (TextView) itemView.findViewById(R.id.artical_update_time);
            oneImg = (ImageView) itemView.findViewById(R.id.artical_img_one);
        }
    }

    public static class ThreeImgViewHolder extends RecyclerView.ViewHolder {

        public TextView title,source,comment,time;
        public ImageView oneImg,twoImg, threeImg;
        public View view;

        public ThreeImgViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.artical_from);
            comment =(TextView) itemView.findViewById(R.id.artical_comment_num);
            time = (TextView) itemView.findViewById(R.id.artical_update_time);
            oneImg = (ImageView) itemView.findViewById(R.id.artical_img_one);
            twoImg = (ImageView) itemView.findViewById(R.id.artical_img_two);
            threeImg = (ImageView) itemView.findViewById(R.id.artical_img_three);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    static class NoFootViewHolder extends RecyclerView.ViewHolder {

        public NoFootViewHolder(View view) {
            super(view);
        }
    }

}
