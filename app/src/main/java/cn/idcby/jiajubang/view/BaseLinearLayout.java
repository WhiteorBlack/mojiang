package cn.idcby.jiajubang.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.idcby.jiajubang.interf.ScreenHeightChange;

/**
 * D:
 */

public class BaseLinearLayout extends LinearLayout {
    private Context context ;

    private ScreenHeightChange changeListener ;

    public void setScreenChangeListener(ScreenHeightChange changeListener){
        this.changeListener = changeListener ;
    }

    public BaseLinearLayout(Context context) {
        super(context);

        this.context = context ;
    }

    public BaseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context ;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public BaseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
