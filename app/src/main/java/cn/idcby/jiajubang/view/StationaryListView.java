package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by mrrlb on 2016/9/24.
 */
public class StationaryListView extends ListView {
    public StationaryListView(Context context) {
        super(context);
    }

    public StationaryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationaryListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置上下不滚动
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;//true:禁止滚动
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int expandSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }




}
