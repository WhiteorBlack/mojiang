package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * D:跟SpecBaseParentScrollView配合使用
 */

public class SpecBaseChildListView extends ListView {
    private int mMinLimitY ;

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    private int itemScreenPosi[] = new int[2] ;

    public SpecBaseChildListView(Context context) {
        super(context);
    }

    public SpecBaseChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpecBaseChildListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMinLimitY(int mMinLimitY) {
        this.mMinLimitY = mMinLimitY;
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

                if(xDistance > yDistance){
                    return false;
                }

                if(!isSelfMove(yDis)){
                    return false ;
                }

                default:
                    break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isSelfMove(float dY){
        if(null == getAdapter() || getChildCount() == 0){
            return false ;
        }
        if(dY > 0){//向下
            if(getFirstVisiblePosition() == 0 && getChildAt(0).getTop() == 0){
                return false ;
            }else{
                return true ;
            }
        }else if(dY < 0){//手指向上
            View topChild = getChildAt(0) ;
            topChild.getLocationOnScreen(itemScreenPosi);

            int y = itemScreenPosi[1] ;
            if(y <= mMinLimitY){
                return true ;
            }
        }
        return false ;
    }
}
