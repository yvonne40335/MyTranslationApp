package com.example.mytranslationapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by yvonne40335 on 2017/10/23.
 */

public class RecycleItemTouchHelper extends ItemTouchHelper.Callback {
    private static final String TAG ="RecycleItemTouchHelper" ;
    private final ItemTouchHelperCallback helperCallback;

    public RecycleItemTouchHelper(ItemTouchHelperCallback helperCallback) {
        this.helperCallback = helperCallback;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.e(TAG, "getMovementFlags: ");
        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
        return makeMovementFlags(0, ItemTouchHelper.START);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        /*Log.e(TAG, "onMove: " );
        helperCallback.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());*/
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e(TAG, "onSwiped: ");
        helperCallback.onItemDelete(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            View itemView = viewHolder.itemView;

            Paint p = new Paint();
            if (dX > 0) {
            // Set your color for positive displacement

                p.setColor(Color.rgb(255, 171, 147));
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), p);
            } else {
            //Set your color for negative displacement
                p.setColor(Color.rgb(255, 171, 147));
                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), p);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    /*@Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //滑动时自己实现背景及图片
        if (actionState==ItemTouchHelper.ACTION_STATE_SWIPE){

            //dX大于0时向右滑动，小于0向左滑动
            View itemView=viewHolder.itemView;//获取滑动的view
            SideBarDelete sideBarDelete = new SideBarDelete();
            Resources resources= sideBarDelete.getAppContext().getResources();
            Bitmap bitmap= BitmapFactory.decodeResource(resources, R.drawable.delete);//获取删除指示的背景图片
            int padding =10;//图片绘制的padding
            int maxDrawWidth=2*padding+bitmap.getWidth();//最大的绘制宽度
            Paint paint=new Paint();
            paint.setColor(resources.getColor(R.color.dark_grey));
            int x=Math.round(Math.abs(dX));
            int drawWidth=Math.min(x,maxDrawWidth);//实际的绘制宽度，取实时滑动距离x和最大绘制距离maxDrawWidth最小值
            int itemTop=itemView.getBottom()-itemView.getHeight();//绘制的top位置
            //向右滑动
            if(dX>0){
                //根据滑动实时绘制一个背景
                c.drawRect(itemView.getLeft(),itemTop,drawWidth,itemView.getBottom(),paint);
                //在背景上面绘制图片
                if (x>padding){//滑动距离大于padding时开始绘制图片
                    //指定图片绘制的位置
                    Rect rect=new Rect();//画图的位置
                    rect.left=itemView.getLeft()+padding;
                    rect.top=itemTop+(itemView.getBottom()-itemTop-bitmap.getHeight())/2;//图片居中
                    int maxRight=rect.left+bitmap.getWidth();
                    rect.right=Math.min(x,maxRight);
                    rect.bottom=rect.top+bitmap.getHeight();
                    //指定图片的绘制区域
                    Rect rect1=null;
                    if (x<maxRight){
                        rect1=new Rect();//不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                        rect1.left=0;
                        rect1.top = 0;
                        rect1.bottom=bitmap.getHeight();
                        rect1.right=x-padding;
                    }
                    c.drawBitmap(bitmap,rect1,rect,paint);
                }
                //绘制时需调用平移动画，否则滑动看不到反馈
                itemView.setTranslationX(dX);
            }else {
                //如果在getMovementFlags指定了向左滑动（ItemTouchHelper。START）时则绘制工作可参考向右的滑动绘制，也可直接使用下面语句交友系统自己处理
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }else {
            //拖动时有系统自己完成
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }*/

    /**
     * Item被选中时候回调
     *
     * @param viewHolder
     * @param actionState
     *          当前Item的状态
     *          ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     *          ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     *          ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        /*if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){  // 闲置状态
            if (viewHolder instanceof ItemTouchHelperViewHolder){
                ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemTouchHelperViewHolder.onItemSelected();
            }
        }*/
        super.onSelectedChanged(viewHolder, actionState);
    }
    public interface ItemTouchHelperCallback{
        void onItemDelete(int positon);
        void onMove(int fromPosition,int toPosition);
    }
}
