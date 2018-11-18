package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/5/18.
 */

public class PayManagerActivity extends BaseMoreStatusActivity {


    @Override
    public void requestData() {
        showSuccessPage();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_pay_manager;
    }

    @Override
    public String setTitle() {
        return "支付绑定";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        View mAlpayLay = findViewById(R.id.acti_pay_manager_alpay_tv) ;
        View mWxLay = findViewById(R.id.acti_pay_manager_wx_tv) ;
        View mYlLay = findViewById(R.id.acti_pay_manager_yl_tv) ;

        mAlpayLay.setOnClickListener(this);
        mWxLay.setOnClickListener(this);
        mYlLay.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_pay_manager_alpay_tv == vId){
            PayManagerBindActivity.launch(mContext ,PayManagerBindActivity.TYPE_ALPAY) ;
        }else if(R.id.acti_pay_manager_wx_tv == vId){
            PayManagerBindActivity.launch(mContext ,PayManagerBindActivity.TYPE_WX) ;
        }else if(R.id.acti_pay_manager_yl_tv == vId){
            PayManagerBindActivity.launch(mContext ,PayManagerBindActivity.TYPE_YL) ;
        }
    }
}
