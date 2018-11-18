package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.idcby.jiajubang.R;

/**
 * D:LinearLayoutManager divider
 * Created on 2017/7/20.
 */
public class RvLinearManagerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;
    private int mDividerHeight = 1 ;//默认1px
    private int mDividerLeftRightPadding = 0 ;

    private int mOrientation;


    public RvLinearManagerItemDecoration(Context context) {
        this(context , VERTICAL_LIST);
    }

    public RvLinearManagerItemDecoration(Context context ,int dividerHeight) {
        this(context , dividerHeight , null);
    }
    public RvLinearManagerItemDecoration(Context context ,int dividerHeight , int leftRightPadding) {
        this(context , dividerHeight , null);

        mDividerLeftRightPadding = leftRightPadding >= 0 ? leftRightPadding : 0 ;
    }

    public RvLinearManagerItemDecoration(Context context ,int dividerHeight , Drawable divider) {
        this(context , dividerHeight , divider ,VERTICAL_LIST);
    }

    public RvLinearManagerItemDecoration(Context context, int dividerHeight , Drawable divider , int orientation) {
        if(dividerHeight >= 0){
            mDividerHeight = dividerHeight ;
        }

        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_lightgrey_f2) ;
        }else{
            mDivider = divider ;
        }

        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + mDividerLeftRightPadding;
        final int right = parent.getWidth() - parent.getPaddingRight() - mDividerLeftRightPadding ;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop() + mDividerLeftRightPadding ;
        final int bottom = parent.getHeight() - parent.getPaddingBottom() - mDividerLeftRightPadding;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight, 0);
        }
    }

}