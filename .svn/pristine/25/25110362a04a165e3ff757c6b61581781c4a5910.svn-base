package com.sh.browser.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sh.browser.R;
import com.sh.browser.browser.AlbumController;
import com.sh.browser.browser.BrowserController;

@SuppressWarnings({"WeakerAccess"})
class Album {
    private final Context context;

    private View albumView;
    public View getAlbumView() {
        return albumView;
    }

    private ImageView albumCover;
    private ImageView remove;
    public void setAlbumCover(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }

    private TextView albumTitle;
    public String getAlbumTitle() {
        return albumTitle.getText().toString();
    }
    public void setAlbumTitle(String title) {
        albumTitle.setText(title);
    }

    private final AlbumController albumController;

    private BrowserController browserController;
    public void setBrowserController(BrowserController browserController) {
        this.browserController = browserController;
    }

    public Album(Context context, AlbumController albumController, BrowserController browserController) {
        this.context = context;
        this.albumController = albumController;
        this.browserController = browserController;
        initUI();
    }

    @SuppressLint("InflateParams")
    private void initUI() {
        albumView = LayoutInflater.from(context).inflate(R.layout.album, null, false);
        albumView.setOnTouchListener(new SwipeToDismissListener(
                albumView,
                new SwipeToDismissListener.DismissCallback() {
                    @Override
                    public void onDismiss() {
                        browserController.removeAlbum(albumController);
                    }
                }
        ));

        albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserController.showAlbum(albumController, false);
            }
        });

        remove = albumView.findViewById(R.id.album_remove);
        albumCover = albumView.findViewById(R.id.album_cover);
        albumTitle = albumView.findViewById(R.id.album_title);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserController.removeAlbum(albumController);
            }
        });
        albumTitle.setText(context.getString(R.string.album_untitled));
    }

    public void activate() {
        albumView.setBackgroundResource(R.drawable.album_shape_accent);
    }

    public void deactivate() {
        albumView.setBackgroundResource(R.drawable.album_shape_transparent);
    }
}
