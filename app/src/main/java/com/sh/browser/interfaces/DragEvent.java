package com.sh.browser.interfaces;

/**
 * Created by admin on 2017/3/19.
 */
public interface DragEvent {

    void onItemSwap(int fromPosition, int toPosition);//Item交换

    void onItemRemove(int position);//Item移除
}
