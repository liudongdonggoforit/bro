package com.sh.browser.interfaces;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class CardPagerItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private DragEvent mEvent;

    public CardPagerItemTouchHelperCallback(DragEvent event) {
        this.mEvent = event;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {//设置支持事件类型
        if (viewHolder.getItemViewType() == 1) {
            return makeMovementFlags(0, 0);
        }
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//        final int swipFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {//Item交换事件
        if (viewHolder.getItemViewType() == 1) return false;
        if (viewHolder.getItemViewType() != target.getItemViewType()) return false;
        mEvent.onItemSwap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {//Item移除事件
        mEvent.onItemRemove(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {//选中Item发生变化
        if (viewHolder != null && viewHolder.getItemViewType() == 1) return;
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof OnViewHolderStatChanageListener) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    OnViewHolderStatChanageListener listener = (OnViewHolderStatChanageListener) viewHolder;
                    listener.onItemSelectedSwiped();
                } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    OnViewHolderStatChanageListener listener = (OnViewHolderStatChanageListener) viewHolder;
                    listener.onItemSelectedDrag();
                }
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof OnViewHolderStatChanageListener) {
            OnViewHolderStatChanageListener listener = (OnViewHolderStatChanageListener) viewHolder;
            listener.onItemNormal();
        }
    }
}