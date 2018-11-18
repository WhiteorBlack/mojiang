package cn.idcby.jiajubang.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created on 2018/5/26.
 */

public class RecyclerViewFocusble extends RecyclerView {
    private boolean mIsClickable = true ;

    public void setClickable(boolean mIsClickable) {
        this.mIsClickable = mIsClickable;
    }

    public RecyclerViewFocusble(Context context) {
        super(context);
    }

    public RecyclerViewFocusble(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewFocusble(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(!mIsClickable){
            return true ;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!mIsClickable){
            return false ;
        }
        return super.onTouchEvent(e);
    }
}
