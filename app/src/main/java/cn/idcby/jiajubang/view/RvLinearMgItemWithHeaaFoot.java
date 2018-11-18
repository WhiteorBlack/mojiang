package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvDecorationHiddenCallBack;

/**
 * D:LinearLayoutManager divider
 * Created on 2017/7/20.
 */
public class RvLinearMgItemWithHeaaFoot extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mDividerHeight = 1 ;//默认1px
    private int mDividerLeftRightPadding = 0 ;

    private RvDecorationHiddenCallBack mCallBack = null ;

    public RvLinearMgItemWithHeaaFoot(Context context , int dividerHeight,RvDecorationHiddenCallBack mCallBack) {
        this(context , dividerHeight , null,mCallBack);
    }
    public RvLinearMgItemWithHeaaFoot(Context context , int dividerHeight , int leftRightPadding,RvDecorationHiddenCallBack mCallBack) {
        this(context , dividerHeight , null,mCallBack);

        mDividerLeftRightPadding = leftRightPadding >= 0 ? leftRightPadding : 0 ;
    }

    public RvLinearMgItemWithHeaaFoot(Context context , int dividerHeight , Drawable divider,RvDecorationHiddenCallBack mCallBack) {
        if(dividerHeight >= 0){
            mDividerHeight = dividerHeight ;
        }

        if(null == divider){
            mDivider = context.getResources().getDrawable(R.drawable.drawable_lightgrey_f2) ;
        }else{
            mDivider = divider ;
        }

        this.mCallBack = mCallBack ;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + mDividerLeftRightPadding;
        final int right = parent.getWidth() - parent.getPaddingRight() - mDividerLeftRightPadding ;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDividerHeight;

            int index = parent.getChildAdapterPosition(child);
            boolean isHidden = false ;
            if(mCallBack != null){
                isHidden = mCallBack.isHidden(index) ;
            }

            mDivider.setBounds(left, isHidden ? 0 : top, right, isHidden ? 0 : bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect,view,parent,state);

        int pos = parent.getChildAdapterPosition(view);

        boolean isHidden = false ;
        if(mCallBack != null){
            isHidden = mCallBack.isHidden(pos) ;
        }

        outRect.set(0, 0, 0, isHidden ? 0 : mDividerHeight);
    }

}