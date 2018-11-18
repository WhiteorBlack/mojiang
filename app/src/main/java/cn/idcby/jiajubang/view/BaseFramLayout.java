package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.idcby.jiajubang.interf.ScreenHeightChange;

/**
 * D:
 * Created on 2016/12/21.
 */

public class BaseFramLayout extends FrameLayout {
    private Context context ;
    private ScreenHeightChange changeListener ;

    public void setScreenChangeListener(ScreenHeightChange changeListener){
        this.changeListener = changeListener ;
    }

    public BaseFramLayout(Context context) {
        super(context);

        this.context = context ;
    }

    public BaseFramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context ;
    }

    public BaseFramLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context ;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(changeListener != null && changed){
            changeListener.screenChanged();
        }

    }
}
