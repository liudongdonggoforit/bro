package com.sh.browser.models;

import java.util.List;

/**
 * Created by dong on 2017/10/14.
 */

public class Channels extends Result {
    private List<Channel> data;

    public List<Channel> getData() {
        return data;
    }

    public void setData(List<Channel> data) {
        this.data = data;
    }
}
