package com.sh.browser.models;

import java.util.List;

/**
 * Created by dong on 2017/10/14.
 */

public class Articals extends Result {
    List<Artical> data;

    public List<Artical> getData() {
        return data;
    }

    public void setData(List<Artical> data) {
        this.data = data;
    }
}
