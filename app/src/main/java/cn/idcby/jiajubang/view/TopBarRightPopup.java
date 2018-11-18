package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.R;

/**
 * titleBar右边操作popup
 * Created on 2018/6/30.
 */

public class TopBarRightPopup extends PopupWindow implements View.OnClickListener{
    private View mParentView ;
    private TopRightPopupCallBack mCallBack ;

    public static final int POPUP_ITEM_SHARE = 1 ;
    public static final int POPUP_ITEM_REQUEST = 2 ;

    private int X_OFF = 0;
    private int Y_OFF = 0;


    public interface TopRightPopupCallBack{
        void onItemClick(int position) ;
    }

    public TopBarRightPopup(Context context, View parentView, TopRightPopupCallBack callBack) {
        super(context);

        mParentView = parentView ;
        mCallBack = callBack ;

        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_top_bar_right,null) ;
        setContentView(contentView);

        View mShareLay = contentView.findViewById(R.id.popup_top_bar_right_share_lay) ;
        View mRequestLay = contentView.findViewById(R.id.popup_top_bar_right_request_lay) ;

        mShareLay.setOnClickListener(this);
        mRequestLay.setOnClickListener(this);
        //X偏移量为 popup的宽度-parentView的宽度 取负值
        X_OFF = ResourceUtils.dip2px(context ,-100 + 2)//2是多设置的一点儿，让箭头对准图片中间
                + parentView.getLayoutParams().width ;
        Y_OFF = ResourceUtils.dip2px(context ,2) ;

        //设置宽与高
        setWidth(ResourceUtils.dip2px(context ,100));
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        //设置进出动画
        setAnimationStyle(R.style.top_bar_right_popup_anim_style);

        //设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new ColorDrawable());

        //设置可以获取集点
        setFocusable(true);

        //设置点击外边可以消失
        setOutsideTouchable(true);
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.popup_top_bar_right_share_lay == vId){//分享
            dismiss() ;

            if(mCallBack != null){
                mCallBack.onItemClick(POPUP_ITEM_SHARE);
            }
        }else if(R.id.popup_top_bar_right_request_lay == vId){//投诉
            dismiss() ;

            if(mCallBack != null){
                mCallBack.onItemClick(POPUP_ITEM_REQUEST);
            }
        }
    }

    public void displayDialog(){
//        showAtLocation(mParentView, Gravity.RIGHT|Gravity.TOP ,X_OFF,Y_OFF);
        showAsDropDown(mParentView,X_OFF ,Y_OFF);
    }

}
