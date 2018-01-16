package com.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by lw on 2017/2/21.
 */

public class SimpleItemTouchHelperCallback<T extends RecyclerView.Adapter, V> extends ItemTouchHelper.Callback {

    private T mAdapter;
    private ArrayList mData;
    private SwipeStateAlterHelper mSwipeState;
    private float olddX = 0;

    public SimpleItemTouchHelperCallback(T mAdapter, ArrayList mData) {
        this.mAdapter = mAdapter;
        this.mData = mData;
    }

    //滑动，类似于isLongPressDragEnabled
    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }

    //返回true则为所有item都设置可以拖拽
    // 要支持长按RecyclerView item进入拖动操作，你必须在isLongPressDragEnabled()方法中返回true。
    // 也可以调用ItemTouchHelper.startDrag(RecyclerView.ViewHolder) 方法来开始一个拖动
    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

    //当item拖拽开始时调用
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.rgb(176,224,230));
            ViewGroup.LayoutParams params=viewHolder.itemView.getLayoutParams();
            params.height=180;
            viewHolder.itemView.setLayoutParams(params);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //当item拖拽完成时调用
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        restoreView(viewHolder);
        super.clearView(recyclerView, viewHolder);
    }

    //当item视图变化时调用

    /**
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX                x轴移动的距离
     * @param dY
     * @param actionState       Action的状态，是拖拽还是滑动事件
     * @param isCurrentlyActive 事件是用户产生还是动画产生的
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //dX 存储一下，进行对比，来对Like图标进行缩放
            Log.d("dX", "" + dX);
            if (mSwipeState != null && dX > 0) {
                mSwipeState.onRightMove(dX, viewHolder);
            }
            if (mSwipeState != null && dX < 0) {
                mSwipeState.onLeftMove(dX, viewHolder);
            }
            olddX = dX;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }

    /**
     * 指定可以支持的拖放和滑动的方向
     * 上下为拖动（drag），左右为滑动（swipe）。
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0, swipeFlags = 0;
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager || recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            //网格式布局有4个方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            int itemId = recyclerView.getChildAdapterPosition(viewHolder.itemView);
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;//设置侧滑方向为从两个方向都可以
            Log.d("itemId", "" + itemId);
            //线性式布局有2个方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;


        }
        return makeMovementFlags(dragFlags, swipeFlags);//swipeFlags 为0的话item不滑动
    }

    //长摁item拖拽时会回调这个方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        swapListPosition(from, to);
        mAdapter.notifyItemMoved(from, to);//更新适配器中item的位置
        mAdapter.notifyDataSetChanged();
        //restoreView(viewHolder);
        return true;
    }

    private void restoreView(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(0);
        ViewGroup.LayoutParams params=viewHolder.itemView.getLayoutParams();
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        viewHolder.itemView.setLayoutParams(params);
    }

    //这里处理滑动删除
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("Swiped", "" + direction);
        if (mSwipeState != null) {
            //direction 刷新ViewHolder的方向——
            mSwipeState.onMoveConsummation(viewHolder, direction);
        }
    }

    /**
     * @param from
     * @param to
     */
    public void swapListPosition(int from, int to) {
        Object moveItem = mData.get(from);
        mData.remove(from);
        mData.add(to, moveItem);//交换数据链表中数据的位置
    }

    public void setSwipeState(SwipeStateAlterHelper mSwipeState) {
        this.mSwipeState = mSwipeState;
    }

    public interface SwipeStateAlterHelper {
        void onLeftMove(float moveX, RecyclerView.ViewHolder holder);

        void onRightMove(float moveX, RecyclerView.ViewHolder holder);

        void onMoveConsummation(RecyclerView.ViewHolder viewHolder, int direction);

    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return .2f;
    }
}
