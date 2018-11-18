package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.alpay.MyPayUtil;
import cn.idcby.jiajubang.alpay.ZFBPay;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.wxapi.WeiXinPay;
import cn.idcby.jiajubang.wxapi.WxPayUtil;

/**
 * 充值
 * Created on 2018/4/13.
 */

public class RechargeActivity extends BaseActivity  implements MyPayUtil.IZhifuBaoPay{
    private View mPayZfbLay ;
    private ImageView mPayZfbIv ;
    private View mPayWxLay ;
    private ImageView mPayWxIv ;
    private EditText mMoneyEv ;

    private String mOrderId ;
    private String mOrderCode ;

    private int mCheckedPayType = 1 ;//1 微信 2 支付宝
    private static final int PAY_TYPE_WX = 1 ;
    private static final int PAY_TYPE_ZFB = 2 ;

    private LoadingDialog mLoadingDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_recharge ;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this) ;

        mMoneyEv = findViewById(R.id.acti_pay_recharge_money_ev) ;
        mPayZfbIv = findViewById(R.id.acti_pay_recharge_zfb_check_iv) ;
        mPayZfbLay = findViewById(R.id.acti_pay_recharge_zfb_lay) ;
        mPayWxIv = findViewById(R.id.acti_pay_recharge_wx_check_iv) ;
        mPayWxLay = findViewById(R.id.acti_pay_recharge_wx_lay) ;
        TextView mSubmitTv = findViewById(R.id.acti_pay_recharge_sub_tv) ;

        mSubmitTv.setOnClickListener(this);
        mPayZfbLay.setOnClickListener(this);
        mPayWxLay.setOnClickListener(this);
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

        if(R.id.acti_pay_recharge_sub_tv == vId){//支付
            submitRechargeInfo() ;
        }else if(R.id.acti_pay_recharge_zfb_lay == vId){
            changePayType(PAY_TYPE_ZFB) ;
        }else if(R.id.acti_pay_recharge_wx_lay == vId){
            changePayType(PAY_TYPE_WX) ;
        }
    }

    /**
     * 切换支付方式
     * @param type 1 微信 2 支付宝
     */
    private void changePayType(int type){
        if(mCheckedPayType == type){
            return ;
        }

        switch (mCheckedPayType){
            case PAY_TYPE_WX:
                mPayWxIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_nomal)) ;
                break;
            case PAY_TYPE_ZFB:
                mPayZfbIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_nomal)) ;
                break;
            default:
                break;
        }
        switch (type){
            case PAY_TYPE_WX:
                mPayWxIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_checked_blue)) ;
                break;
            case PAY_TYPE_ZFB:
                mPayZfbIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_checked_blue)) ;
                break;
            default:
                break;
        }

        mCheckedPayType = type ;
    }

    /**
     * 提交支付信息
     */
    private void submitRechargeInfo(){
        String money = mMoneyEv.getText().toString() ;
        if(StringUtils.convertString2Float(money) <= 0){
            mMoneyEv.requestFocus() ;
            mMoneyEv.setSelection(money.length());
            ToastUtils.showToast(mContext ,"请输入正确的充值金额");
            return ;
        }

        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("BidBond" ,money) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_RECHARGE, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("" , mContext , NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        if(bean != null && bean.OrderCode != null){
                            mOrderCode = bean.OrderCode ;
                            mOrderId = bean.OrderID ;
                            goPay();
                        }else{
                            mLoadingDialog.dismiss();

                            ToastUtils.showToast(mContext ,"充值失败");
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss();

                        ToastUtils.showToast(mContext ,"充值失败");
                    }
                });
    }

    /**
     * 根据类型获取支付信息
     */
    private void goPay(){
        if(PAY_TYPE_ZFB == mCheckedPayType){
            getZfbPayParamFromServer() ;
        }else{
            getWxPayParamFromServer() ;
        }
    }

    /**
     * 启动微信支付
     */
    private void startWxPay(WeiXinPay payPara){
        if(null == payPara){
            ToastUtils.showToast(mContext , "支付失败，请重试");
            return ;
        }

        LogUtils.showLog("testPayParam" ,"info=" + payPara.toString()) ;

        ToastUtils.showToast(mContext , "启动微信");

        WxPayUtil.WXPay(payPara);
    }


    /**
     * 启动支付宝支付
     */
    private void startZfbPay(ZFBPay payPara){
        if(null == payPara){
            ToastUtils.showToast(mContext , "支付失败，请重试");
            return ;
        }

        LogUtils.showLog("testPayParam" ,"info=" + payPara.toString()) ;

        ToastUtils.showToast(mContext , "启动支付宝");

        MyPayUtil payUtil = new MyPayUtil(RechargeActivity.this);
        payUtil.pay(RechargeActivity.this, payPara);
    }

    /**
     * 获取支付信息
     */
    private void getZfbPayParamFromServer(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderID" , mOrderId) ;
        paramMap.put("OrderCode" , mOrderCode) ;
        paramMap.put("OrderType" , "" + SkipUtils.PAY_ORDER_TYPE_RECHARGE) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_ZFB_URL , paramMap
                , new RequestObjectCallBack<ZFBPay>("getZfbInfo",mContext ,ZFBPay.class) {
                    @Override
                    public void onSuccessResult(ZFBPay bean) {
                        mLoadingDialog.dismiss() ;

                        startZfbPay(bean) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss() ;

                        ToastUtils.showToast(mContext ,"充值失败");
                    }
                });
    }

    /**
     * 获取支付信息
     */
    private void getWxPayParamFromServer(){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderID" , mOrderId) ;
        paramMap.put("OrderCode" , mOrderCode) ;
        paramMap.put("OrderType" , "" + SkipUtils.PAY_ORDER_TYPE_RECHARGE) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_WX_URL , paramMap
                , new RequestObjectCallBack<WeiXinPay>("getWxInfo",mContext ,WeiXinPay.class) {
                    @Override
                    public void onSuccessResult(WeiXinPay bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        startWxPay(bean) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }
                    }
                });
    }


    @Override
    public void checkDeviceResult(boolean checkResult) {

    }

    @Override
    public void payResult(boolean payResult) {
        if (payResult) {
            //支付成功
            finishPay();
        } else {
            //支付失败

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBusLogin(BusEvent.WxPayEvent ev){
        if(ev.getErrorCode() == 0){
            finishPay() ;
        }else if(ev.getErrorCode() == -1){
            ToastUtils.showToast(mContext, "支付异常");
        }else if(ev.getErrorCode() == -2){
            ToastUtils.showToast(mContext, "支付取消了");
        }
    }

    //支付完成之后的操作
    private void finishPay() {
        ToastUtils.showToast(mContext, "支付成功");

        setResult(RESULT_OK) ;
        finish() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this) ;
    }
}
