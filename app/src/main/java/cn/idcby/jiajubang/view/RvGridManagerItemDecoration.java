package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import cn.idcby.jiajubang.R;

/**
 * D:recyclerView 分割线
 * Created on 2017/7/20.
 *
 * 注：有bug，慎用
 */

public class RvGridManagerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider = null ;
    private int mHorizationSpc = 1 ;
    private int mVerticalSpc = 1 ;

    private int mHeaderCount = 0 ;
    private int mFooterCount = 0 ;

    public RvGridManagerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.drawable_white_trans) ;
    }
    public RvGridManagerItemDecoration(Context context , Drawable divider) {
        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_white_trans) ;
        }else{
            mDivider = divider ;
        }
    }

    public RvGridManagerItemDecoration(Context context , int horVerSpc) {
        this(context) ;

        mHorizationSpc = mVerticalSpc = horVerSpc >= 0 ? horVerSpc : 1 ;
    }

    public RvGridManagerItemDecoration(Context context , int headerCount ,int horVerSpc) {
        this(context) ;
        this.mHeaderCount = headerCount >= 0 ? headerCount : 0 ;
        mHorizationSpc = mVerticalSpc = horVerSpc >= 0 ? horVerSpc : 1 ;
    }

    public RvGridManagerItemDecoration(Context context , int headerCount ,int footerCount ,int horVerSpc,Drawable divider) {
        this(context) ;
        this.mHeaderCount = headerCount >= 0 ? headerCount : 0 ;
        this.mFooterCount = footerCount >= 0 ? footerCount : 0 ;
        mHorizationSpc = mVerticalSpc = horVerSpc >= 0 ? horVerSpc : 1 ;

        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_white_trans) ;
        }else{
            mDivider = divider ;
        }
    }

    public RvGridManagerItemDecoration(Context context , int horVerSpc , Drawable divider) {
        this(context , horVerSpc) ;

        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_white_trans) ;
        }else{
            mDivider = divider ;
        }
    }

    /**
     * 构造方法
     * @param context context
     * @param horizationSpc 水平方向的线，注意：不是针对item，是针对线
     * @param verticalSpc 竖直方向的线，注意：不是针对item，是针对线
     * @param divider divider color
     */
    public RvGridManagerItemDecoration(Context context , int horizationSpc , int verticalSpc , Drawable divider) {
        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_white_trans) ;
        }else{
            mDivider = divider ;
        }

        mHorizationSpc = horizationSpc >= 0 ? horizationSpc : 1 ;
        mVerticalSpc = verticalSpc >= 0 ? verticalSpc : 1 ;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {

        drawHorizontal(c, parent);
        drawVertical(c, parent);

    }

    public void drawHorizontal(Canvas c, RecyclerView parent)
    {
        int childCount = parent.getChildCount() ;
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mVerticalSpc;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mHorizationSpc;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent)
    {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mVerticalSpc;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        boolean isLastRow = isLastRow(parent, itemPosition + mHeaderCount, spanCount, childCount);
        boolean isLastColumn = isLastColumn(parent, itemPosition + mHeaderCount, spanCount, childCount);

        int left;
        int right;
        int bottom;
        int eachWidth = (spanCount - 1) * mVerticalSpc / spanCount;
        int dl = mVerticalSpc - eachWidth;

        left = (itemPosition + mHeaderCount) % spanCount * dl;
        right = eachWidth - left;
        bottom = mHorizationSpc;
        if (isLastRow) {
            bottom = 0;
        }

        if (isLastColumn) {
            right = 0;
        }

        if(mHeaderCount > 0 && itemPosition < mHeaderCount){
            left = 0 ;
            right = 0 ;
            bottom = 0 ;
        }
        if(mFooterCount > 0 && itemPosition >= (childCount - mFooterCount)){
            left = 0 ;
            right = 0 ;
        }

        outRect.set(left, 0, right, bottom);
    }
}
