package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.idcby.jiajubang.R;

/**
 * Created on 2018/5/16.
 */

public class TopBarRightView extends RelativeLayout {
    private TextView mCountTv ;

    public TopBarRightView(Context context) {
        super(context);

        init(context) ;
    }

    public TopBarRightView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context) ;
    }

    public TopBarRightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context) ;
    }

    private void init(Context context){
        inflate(context , R.layout.lay_right_msg_item ,this) ;
        mCountTv = findViewById(R.id.top_right_msg_count_tv) ;
    }


    public void setUnreadCount(int count){
        if(null == mCountTv){
            return ;
        }

        mCountTv.setVisibility(count > 0 ? VISIBLE : GONE);

        if(count > 99){
            mCountTv.setText("99+");
        }else{
            mCountTv.setText("" + count);
        }
    }

}
