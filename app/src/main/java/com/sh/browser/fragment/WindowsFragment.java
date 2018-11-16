package com.sh.browser.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sh.browser.R;
import com.sh.browser.browser.AlbumController;
import com.sh.browser.browser.BrowserContainer;
import com.sh.browser.interfaces.ITransmitData;
import com.sh.browser.interfaces.ItransactionFragment;
import com.sh.browser.views.NinjaRelativeLayout;

/**
 * 浏览器窗口内容
 */
public class WindowsFragment extends BaseFragment implements View.OnClickListener, ITransmitData {

    private ItransactionFragment itransactionFragment;
    private TextView cancel;
    private TextView clearAll;
    private LinearLayout switcher_container;
    private ImageView setting;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_windows, null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        byte[] bitmaps = getArguments().getByteArray("bitmaps");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmaps,0,bitmaps.length);
        cancel = mView.findViewById(R.id.window_back);
        clearAll = mView.findViewById(R.id.window_all_clear);
        switcher_container = mView.findViewById(R.id.switcher_container);
        setting = mView.findViewById(R.id.windows_manager_setting);
        cancel.setOnClickListener(this);
        clearAll.setOnClickListener(this);
        setting.setOnClickListener(this);
        final AlbumController holder;
        NinjaRelativeLayout layout = new NinjaRelativeLayout(mContext);
        layout.setAlbumTitle(getString(R.string.app_name));
        layout.setBrowserController(new BrowserFragment());
        layout.setAlbumCover(bitmap);
        holder = layout;
        View albumView = holder.getAlbumView();
        BrowserContainer.add(holder);
        switcher_container.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItransactionFragment) {
            itransactionFragment = (ItransactionFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itransactionFragment = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.window_back:
                itransactionFragment.transcationFragment(4,null);
                break;
            case R.id.window_all_clear:
//                itransactionFragment.transcationFragment(4,null);
                break;
            case R.id.windows_manager_setting:
                Toast.makeText(mContext,"setting",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void transmitData(Bundle data) {

    }
}
