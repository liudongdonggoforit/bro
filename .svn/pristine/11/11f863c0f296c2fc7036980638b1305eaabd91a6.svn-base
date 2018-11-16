package com.sh.browser.browser;

import android.os.Handler;
import android.os.Message;

import com.sh.browser.views.NinjaWebView;

public class NinjaClickHandler extends Handler {
    private final NinjaWebView webView;

    public NinjaClickHandler(NinjaWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        webView.getBrowserController().onLongPress(message.getData().getString("url"));
    }
}
