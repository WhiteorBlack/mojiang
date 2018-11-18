package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.UserBond;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyPayBandsCancelActivity;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 保证金
 * Created on 2018/5/18.
 */

public class MoneyMgBzjFragment extends Fragment implements View.OnClickListener{
    private Activity mContext ;

    private View mView ;

    private TextView mInstallTv ;
    private TextView mInstallRepayTv ;
    private TextView mInstallCancelTv ;
    private TextView mInstallStateTv ;
    private TextView mInstallMoneyTv ;
    private TextView mTradeTv ;
    private TextView mTradeRepayTv ;
    private TextView mTradeCancelTv ;
    private TextView mTradeStateTv ;
    private TextView mTradeMoneyTv ;
    private TextView mStoreTv ;
    private TextView mStoreRepayTv ;
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
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = getActivity() ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mView) {
            mView = inflater.inflate(R.layout.fragment_money_mg_bzj, container, false);

            mLoadingDialog = new LoadingDialog(mContext) ;

            mInstallTv = mView.findViewById(R.id.frag_money_mg_bzj_install_edit_tv) ;
            mInstallRepayTv = mView.findViewById(R.id.frag_money_mg_bzj_install_repay_tv) ;
            mInstallCancelTv = mView.findViewById(R.id.frag_money_mg_bzj_install_cancel_tv) ;
            mInstallStateTv = mView.findViewById(R.id.frag_money_mg_bzj_install_state_tv) ;
            mInstallMoneyTv = mView.findViewById(R.id.frag_money_mg_bzj_install_money_tv) ;

            mTradeTv = mView.findViewById(R.id.frag_money_mg_bzj_trade_edit_tv) ;
            mTradeRepayTv = mView.findViewById(R.id.frag_money_mg_bzj_trade_repay_tv) ;
            mTradeCancelTv = mView.findViewById(R.id.frag_money_mg_bzj_trade_cancel_tv) ;
            mTradeStateTv = mView.findViewById(R.id.frag_money_mg_bzj_trade_state_tv) ;
            mTradeMoneyTv = mView.findViewById(R.id.frag_money_mg_bzj_trade_money_tv) ;

            mStoreTv = mView.findViewById(R.id.frag_money_mg_bzj_store_edit_tv) ;
            mStoreRepayTv = mView.findViewById(R.id.frag_money_mg_bzj_store_repay_tv) ;
            mStoreCancelTv = mView.findViewById(R.id.frag_money_mg_bzj_store_cancel_tv) ;
            mStoreStateTv = mView.findViewById(R.id.frag_money_mg_bzj_store_state_tv) ;
            mStoreMoneyTv = mView.findViewById(R.id.frag_money_mg_bzj_store_money_tv) ;

            mInstallTv.setOnClickListener(this);
            mInstallRepayTv.setOnClickListener(this);
            mInstallCancelTv.setOnClickListener(this);
            mTradeTv.setOnClickListener(this);
            mTradeRepayTv.setOnClickListener(this);
            mTradeCancelTv.setOnClickListener(this);
            mStoreTv.setOnClickListener(this);
            mStoreRepayTv.setOnClickListener(this);
            mStoreCancelTv.setOnClickListener(this);

            if(getUserVisibleHint()){
                getUserBondInfo() ;
            }
        }

        return mView ;
    }

    @Override
    public void onClick(View view) {
        if(null == mUserBond){
            ToastUtils.showToast(mContext , "获取保证金信息失败");
            return ;
        }

        int vId = view.getId() ;
        if(R.id.frag_money_mg_bzj_install_edit_tv == vId){//安装
            toPayBonds(1) ;
        }else if(R.id.frag_money_mg_bzj_trade_edit_tv == vId){//行业
            toPayBonds(2) ;
        }else if(R.id.frag_money_mg_bzj_store_edit_tv == vId){//店铺
            toPayBonds(3) ;
        }else if(R.id.frag_money_mg_bzj_store_cancel_tv == vId){//店铺退保
            toCancelBonds(3) ;
        }else if(R.id.frag_money_mg_bzj_trade_cancel_tv == vId){//行业(服务)退保
            toCancelBonds(2) ;
        }else if(R.id.frag_money_mg_bzj_install_cancel_tv == vId){//安装退保
            toCancelBonds(1) ;
        }else if(R.id.frag_money_mg_bzj_store_repay_tv == vId){//店铺--续缴

        }else if(R.id.frag_money_mg_bzj_trade_repay_tv == vId){//行业服务--续缴

        }else if(R.id.frag_money_mg_bzj_install_repay_tv == vId){//安装--续缴

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
            mStoreState = storeState ;
        }

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
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "请先认证", null
                    , "去认证", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    SkipUtils.toApplyActivity(mContext) ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return ;
        }
        if(applyState != SkipUtils.APPLY_PAY_BOND_STATE && applyState != SkipUtils.APPLY_ACCESS_STATE){
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

        //2018-05-22 10:11:29 改：不用判断 money 和 moneyPay 的一致性

        if(money <= 0 ){
            ToastUtils.showToast(mContext ,"保证金暂不用缴纳");
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
            ToastUtils.showToast(mContext , "保证金信息更新失败");
            return ;
        }

        boolean isInstallPay = mUserBond.isInstallIsPay() ;
        boolean isServicePay = mUserBond.isServiceIsPay() ;
        boolean isShopPay = mUserBond.isShopInfoisPay() ;

        mInstallTv.setVisibility(isInstallPay ? View.GONE : View.VISIBLE);
        mInstallCancelTv.setVisibility(isInstallPay ? View.VISIBLE : View.GONE);
//        mInstallRepayTv.setVisibility(isInstallPay ? View.VISIBLE : View.GONE);

        mTradeTv.setVisibility(isServicePay ? View.GONE : View.VISIBLE);
        mTradeCancelTv.setVisibility(isServicePay ? View.VISIBLE : View.GONE);
//        mTradeRepayTv.setVisibility(isServicePay ? View.VISIBLE : View.GONE);

        mStoreTv.setVisibility(isShopPay ? View.GONE : View.VISIBLE);
        mStoreCancelTv.setVisibility(isShopPay ? View.VISIBLE : View.GONE);
//        mStoreRepayTv.setVisibility(isShopPay ? View.VISIBLE : View.GONE);

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(getUserVisibleHint()){
                getUserBondInfo() ;
            }else{
                NetUtils.cancelTag("getUserBondInfo") ;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){
            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }
    }

}
