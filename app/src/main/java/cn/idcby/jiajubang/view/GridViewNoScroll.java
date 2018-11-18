package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created on 2018/8/15.
 */

public class GridViewNoScroll extends GridView {
    public GridViewNoScroll(Context context) {
        super(context);
    }

    public GridViewNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewNoScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
