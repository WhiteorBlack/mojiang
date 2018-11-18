package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 我的余额
 * Created on 2018/4/14.
 */
@Deprecated
public class MyMoneyActivity extends BaseActivity {

    private TextView mMoneyTv ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_WITHDRAW = 1000 ;
    private static final int REQUEST_CODE_RECHARGE = 1001 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_money ;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        String money = getIntent().getStringExtra("money") ;

        mMoneyTv = findViewById(R.id.acti_my_money_money_tv) ;
        View withdrawLay = findViewById(R.id.acti_my_money_withdraw_lay) ;
        View rechargeLay = findViewById(R.id.acti_my_money_rechange_lay) ;
        View detailsLay = findViewById(R.id.acti_my_money_details_lay) ;

        withdrawLay.setOnClickListener(this);
        rechargeLay.setOnClickListener(this);
        detailsLay.setOnClickListener(this);

        if(null == money){
            getMyInfo() ;
        }else{
            mMoneyTv.setText(money) ;
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_money_withdraw_lay == vId){//提现
            Intent toWdIt = new Intent(mContext ,WithdrawActivity.class) ;
            toWdIt.putExtra("money" ,mMoneyTv.getText().toString()) ;
            startActivityForResult(toWdIt ,REQUEST_CODE_WITHDRAW);
        }else if(R.id.acti_my_money_rechange_lay == vId){//充值
            Intent toRiIt = new Intent(mContext ,RechargeActivity.class) ;
            startActivityForResult(toRiIt ,REQUEST_CODE_RECHARGE);
        }else if(R.id.acti_my_money_details_lay == vId){//明细

            Intent toLvIt = new Intent(mContext ,MyMoneyBillListActivity.class) ;
            startActivity(toLvIt);

        }
    }

    /**
     * 获取个人信息
     */
    private void getMyInfo(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo" ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        mDialog.dismiss();

                        if(bean != null){
                            mMoneyTv.setText(bean.getBalance()) ;
                        }else{
                            ToastUtils.showToast(mContext ,"余额信息获取失败");
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext ,"余额信息获取失败");
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext ,"余额信息获取失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_OK == resultCode){
            if(REQUEST_CODE_RECHARGE == requestCode || REQUEST_CODE_WITHDRAW == requestCode){
                getMyInfo() ;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getMyInfo") ;

    }
}
