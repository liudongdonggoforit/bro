package com.sh.browser.browser;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.sh.browser.R;
import com.sh.browser.utils.BrowserUnit;
import com.sh.browser.utils.IntentUnit;
import com.tencent.smtt.export.external.interfaces.DownloadListener;

public class NinjaDownloadListener implements DownloadListener {
    private final Context context;

    public NinjaDownloadListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
        final Context holder = IntentUnit.getContext();
        if (holder == null || !(holder instanceof Activity)) {
            BrowserUnit.download(context, url, contentDisposition, mimeType);
            return;
        }

        String text = holder.getString(R.string.dialog_title_download) + " - " + URLUtil.guessFileName(url, contentDisposition, mimeType);
        final BottomSheetDialog dialog = new BottomSheetDialog(holder);
        View dialogView = View.inflate(holder, R.layout.dialog_action, null);
        TextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(text);
        Button action_ok = dialogView.findViewById(R.id.action_ok);
        action_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowserUnit.download(holder, url, contentDisposition, mimeType);
                dialog.cancel();
            }
        });
        Button action_cancel = dialogView.findViewById(R.id.action_cancel);
        action_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public void onDownloadStart(String s, String s1, byte[] bytes, String s2, String s3, String s4, long l, String s5, String s6) {

    }

    @Override
    public void onDownloadVideo(String s, long l, int i) {

    }
}
