package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import cn.idcby.jiajubang.adapter.SpecFragPageAdapter;
import cn.idcby.jiajubang.fragment.SpecBaseFragment;

/**
 * D:自定义ScrollView，适配ViewPager
 */

public class SpecBaseParentScrollView extends ScrollView {
    private SpecFragPageAdapter mAdapter ;

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    private ScrollListener mScrollListener ;

    public void setFragmentAdapter(SpecFragPageAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void setScrollListener(ScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    public SpecBaseParentScrollView(Context context) {
        super(context);
    }

    public SpecBaseParentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpecBaseParentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 是否当前滑动，即ScrollView滑动
     * @param yDis y 移动距离，正值手指向下，负值手指向上
     * @return true 自己滑动，fasle child滑动
     */
    private boolean isSelfMove(float yDis){
        if(null == mAdapter || mAdapter.getCount() == 0){
            return true ;
        }

        SpecBaseFragment subFragment = mAdapter.getCurrentFragment();
        if(null == subFragment){
            return true ;
        }

        if(yDis > 0 && subFragment.isListViewFirstItemOnTop()){
            return true ;
        }

        if(yDis < 0 && getScrollY() + getHeight() < computeVerticalScrollRange()){//还没到底部
            return true ;
        }

        return false ;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                float yDis = curY - yLast ;
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(yDis);
                xLast = curX;
                yLast = curY;

                if(xDistance > yDistance || !isSelfMove(yDis)){
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(mScrollListener != null){
            mScrollListener.onScroll(t) ;
        }
    }

    public interface ScrollListener {
        void onScroll(int top) ;
    }

}
