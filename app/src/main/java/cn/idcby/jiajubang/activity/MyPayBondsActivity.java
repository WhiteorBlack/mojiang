package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.UserBond;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 缴纳保证金
 * Created on 2018/4/3.
 *
 * 2018-05-17 13:13:51
 * 退保直接申请，不再填写信息
 */
@Deprecated
public class MyPayBondsActivity extends BaseActivity {
    private TextView mInstallTv ;
    private TextView mInstallCancelTv ;
    private TextView mInstallStateTv ;
    private TextView mInstallMoneyTv ;
    private TextView mTradeTv ;
    private TextView mTradeCancelTv ;
    private TextView mTradeStateTv ;
    private TextView mTradeMoneyTv ;
    private TextView mStoreTv ;
    private TextView mStoreCancelTv ;
    private TextView mStoreStateTv ;
    private TextView mStoreMoneyTv ;

    private UserBond mUserBond ;
    private int mInstallState = SkipUtils.APPLY_PAY_BOND_STATE ;
    private int mTradeState = SkipUtils.APPLY_PAY_BOND_STATE ;
    private int mStoreState = SkipUtils.APPLY_PAY_BOND_STATE ;

    private LoadingDialog mLoadingDialog ;

    private static final int REQUEST_CODE_CANCEL_BOND = 1000 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_pay_bonds ;
    }

    @Override
    public void initView() {
        int installState = SPUtils.newIntance(mContext).getUserInstallApply();
        int tradeState = SPUtils.newIntance(mContext).getUserServerApply();
        int storeState = SPUtils.newIntance(mContext).getUserStoreApply();

        if(installState != -1){
            mInstallState = installState ;
        }
        if(tradeState != -1){
            mTradeState = tradeState ;
        }
        if(storeState != -1){
            mStoreState = installState ;
        }

        mLoadingDialog = new LoadingDialog(mContext) ;

        mInstallTv = findViewById(R.id.acti_my_pay_bonds_install_edit_tv) ;
        mInstallCancelTv = findViewById(R.id.acti_my_pay_bonds_install_cancel_tv) ;
        mInstallStateTv = findViewById(R.id.acti_my_pay_bonds_install_state_tv) ;
        mInstallMoneyTv = findViewById(R.id.acti_my_pay_bonds_install_money_tv) ;

        mTradeTv = findViewById(R.id.acti_my_pay_bonds_trade_edit_tv) ;
        mTradeCancelTv = findViewById(R.id.acti_my_pay_bonds_trade_cancel_tv) ;
        mTradeStateTv = findViewById(R.id.acti_my_pay_bonds_trade_state_tv) ;
        mTradeMoneyTv = findViewById(R.id.acti_my_pay_bonds_trade_money_tv) ;

        mStoreTv = findViewById(R.id.acti_my_pay_bonds_store_edit_tv) ;
        mStoreCancelTv = findViewById(R.id.acti_my_pay_bonds_store_cancel_tv) ;
        mStoreStateTv = findViewById(R.id.acti_my_pay_bonds_store_state_tv) ;
        mStoreMoneyTv = findViewById(R.id.acti_my_pay_bonds_store_money_tv) ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mInstallTv.setOnClickListener(this);
        mInstallCancelTv.setOnClickListener(this);
        mTradeTv.setOnClickListener(this);
        mTradeCancelTv.setOnClickListener(this);
        mStoreTv.setOnClickListener(this);
        mStoreCancelTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;
        if(R.id.acti_my_pay_bonds_install_edit_tv == vId){//安装
            toPayBonds(1) ;
        }else if(R.id.acti_my_pay_bonds_trade_edit_tv == vId){//行业
            toPayBonds(2) ;
        }else if(R.id.acti_my_pay_bonds_store_edit_tv == vId){//店铺
            toPayBonds(3) ;
        }else if(R.id.acti_my_pay_bonds_store_cancel_tv == vId){//店铺退保
            toCancelBonds(3) ;
        }else if(R.id.acti_my_pay_bonds_trade_cancel_tv == vId){//行业(服务)退保
            toCancelBonds(2) ;
        }else if(R.id.acti_my_pay_bonds_install_cancel_tv == vId){//安装退保
            toCancelBonds(1) ;
        }
    }

    /**
     * 退保
     */
    private void toCancelBonds(int type){
        Intent toClIt = new Intent(mContext ,MyPayBandsCancelActivity.class) ;
        toClIt.putExtra("payBondType" ,type) ;
        startActivityForResult(toClIt ,REQUEST_CODE_CANCEL_BOND) ;
    }

    /**
     * 去支付
     */
    private void toPayBonds(int index){
        int applyState = SkipUtils.APPLY_PAY_BOND_STATE;
        //先判断认证状态
        switch (index){
            case 1:
                applyState = mInstallState ;
                break;
            case 2:
                applyState = mTradeState ;
                break;
            case 3:
                applyState = mStoreState ;
                break;
        }

        if(0 == applyState){
            ToastUtils.showToast(mContext ,"请先认证");
            return ;
        }
        if(applyState != SkipUtils.APPLY_PAY_BOND_STATE){
            ToastUtils.showToast(mContext ,"审核通过才能进行缴纳");
            return ;
        }

        float moneyPay = 0F ;
        float money = 0F ;
        String orderId = "" ;
        String orderCode = "" ;
        int orderType = -1 ;

        if(1 == index){
            money = StringUtils.convertString2Float(mUserBond.getInstallBond()) ;
            moneyPay = StringUtils.convertString2Float(mUserBond.getInstallMoney()) ;
            orderId = StringUtils.convertNull(mUserBond.getInstallOrderID()) ;
            orderCode = StringUtils.convertNull(mUserBond.getInstallOrderCode()) ;
            orderType = SkipUtils.PAY_ORDER_TYPE_BOND_INSTALL ;
        }else if(2 == index){
            money = StringUtils.convertString2Float(mUserBond.getServiceBond()) ;
            moneyPay = StringUtils.convertString2Float(mUserBond.getServiceMoney()) ;
            orderId = StringUtils.convertNull(mUserBond.getServiceOrderID()) ;
            orderCode = StringUtils.convertNull(mUserBond.getServiceOrderCode()) ;
            orderType = SkipUtils.PAY_ORDER_TYPE_BOND_SERVICE ;
        }else if(3 == index){
            money = StringUtils.convertString2Float(mUserBond.getShopInfoBond()) ;
            moneyPay = StringUtils.convertString2Float(mUserBond.getShopMoney()) ;
            orderId = StringUtils.convertNull(mUserBond.getShopOrderID()) ;
            orderCode = StringUtils.convertNull(mUserBond.getShopOrderCode()) ;
            orderType = SkipUtils.PAY_ORDER_TYPE_BOND_STORE ;
        }

        if(money <= 0 || moneyPay <= 0 || moneyPay != money
                || "".equals(orderId) || "".equals(orderCode)){
            ToastUtils.showToast(mContext ,"缴纳失败，请检查是否通过认证");
            return ;
        }

        //跳转到付款界面
        SkipUtils.toPayBondActivity(mContext ,"" + money , orderId ,orderCode ,orderType) ;
    }

    /**
     * 填充数据
     */
    private void updateBondInfo(){
        if(null == mUserBond){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "获取保证金信息失败，请重试", null
                    , "重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    getUserBondInfo() ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

        boolean isInstallPay = mUserBond.isInstallIsPay() ;
        boolean isServicePay = mUserBond.isServiceIsPay() ;
        boolean isShopPay = mUserBond.isShopInfoisPay() ;

        mInstallTv.setVisibility(isInstallPay ? View.GONE : View.VISIBLE);
        mInstallCancelTv.setVisibility(isInstallPay ? View.VISIBLE : View.GONE);

        mTradeTv.setVisibility(isServicePay ? View.GONE : View.VISIBLE);
        mTradeCancelTv.setVisibility(isServicePay ? View.VISIBLE : View.GONE);

        mStoreTv.setVisibility(isShopPay ? View.GONE : View.VISIBLE);
        mStoreCancelTv.setVisibility(isShopPay ? View.VISIBLE : View.GONE);

        mInstallMoneyTv.setText("¥" + mUserBond.getInstallBond()) ;
        mTradeMoneyTv.setText("¥" + mUserBond.getServiceBond());
        mStoreMoneyTv.setText("¥" + mUserBond.getShopInfoBond()) ;

        mInstallStateTv.setText(isInstallPay ? "已缴纳" : "未缴纳");
        mTradeStateTv.setText(isServicePay ? "已缴纳" : "未缴纳");
        mStoreStateTv.setText(isShopPay ? "已缴纳" : "未缴纳");
    }

    /**
     * 获取保证金信息
     */
    private void getUserBondInfo(){
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_BONDS, paramMap
                , new RequestObjectCallBack<UserBond>("getUserBondInfo" , mContext ,UserBond.class) {
                    @Override
                    public void onSuccessResult(UserBond bean) {
                        mLoadingDialog.dismiss() ;

                        mUserBond = bean ;
                        updateBondInfo() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss() ;

                        updateBondInfo() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss() ;

                        updateBondInfo() ;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserBondInfo() ;
    }
}
