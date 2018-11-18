package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
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
import cn.idcby.jiajubang.view.passwords.PopEnterPassword;
import cn.idcby.jiajubang.wxapi.WeiXinPay;
import cn.idcby.jiajubang.wxapi.WxPayUtil;

/**
 * 支付
 * Created on 2018/3/31.
 *
 * 2018-05-30 14:52:37
 * 添加相关的支付提醒
 * 支付完成后付款至平台担保账户
 *
 * 2018-07-04 15:06:32
 * 商品订单支付的时候，需要弹出对话框：请联系卖家确定运费，才能及时发货哟
 *
 * 2018-07-05 18:09:20
 * 对话框取消，改为写在下面。
 */

public class ActivityBondPay extends BaseActivity implements MyPayUtil.IZhifuBaoPay, PopEnterPassword.PassPopInterImps {
    private View mPayZfbLay;
    private ImageView mPayZfbIv;
    private View mPayWxLay;
    private ImageView mPayWxIv;
    private View mPayYueLay;
    private ImageView mPayYueIv;

    private String mOrderId;
    private String mOrderCode;
    private String mOrderType;

    private int mCheckedPayType = PAY_TYPE_YUE ;//1 微信 2 支付宝 3 余额
    private static final int PAY_TYPE_WX = 1;
    private static final int PAY_TYPE_ZFB = 2;
    private static final int PAY_TYPE_YUE = 3;

    private LoadingDialog mLoadingDialog;

    private String mMoney;

    private static final int REQUEST_CODE_CHANGE_PASS = 1001 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_pay_bond;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);

        mMoney = getIntent().getStringExtra(SkipUtils.INTENT_PAY_MONEY);
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_PAY_ORDER_ID);
        mOrderCode = getIntent().getStringExtra(SkipUtils.INTENT_PAY_ORDER_CODE);
        mOrderType = getIntent().getStringExtra(SkipUtils.INTENT_PAY_ORDER_TYPE);

        TextView mMoneyTv = findViewById(R.id.acti_pay_bond_money_tv);
        mPayZfbIv = findViewById(R.id.acti_pay_bond_zfb_check_iv);
        mPayZfbLay = findViewById(R.id.acti_pay_bond_zfb_lay);
        mPayWxIv = findViewById(R.id.acti_pay_bond_wx_check_iv);
        mPayWxLay = findViewById(R.id.acti_pay_bond_wx_lay);
        mPayYueIv = findViewById(R.id.acti_pay_bond_yue_check_iv);
        mPayYueLay = findViewById(R.id.acti_pay_bond_yue_lay);
        TextView mSubmitTv = findViewById(R.id.acti_pay_bond_sub_tv);

        mSubmitTv.setOnClickListener(this);
        mPayZfbLay.setOnClickListener(this);
        mPayWxLay.setOnClickListener(this);
        mPayYueLay.setOnClickListener(this);

        if (!"".equals(StringUtils.convertNull(mMoney))) {
            mMoneyTv.setText("¥" + mMoney);
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
        int vId = view.getId();

        if (R.id.acti_pay_bond_sub_tv == vId) {//支付
//            if(SkipUtils.PAY_ORDER_TYPE_MALL == StringUtils.convertString2Count(mOrderType)){ //商品订单
//                DialogUtils.showCustomViewDialog(mContext, "温馨提示"
//                        , getResources().getString(R.string.good_before_pay_tips), null
//                        , "确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//
//                                goPay();
//                            }
//                        }, "取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//            }else{
//
//            }

            goPay();
        } else if (R.id.acti_pay_bond_zfb_lay == vId) {
            changePayType(PAY_TYPE_ZFB);
        } else if (R.id.acti_pay_bond_wx_lay == vId) {
            changePayType(PAY_TYPE_WX);
        } else if (R.id.acti_pay_bond_yue_lay == vId) {
            changePayType(PAY_TYPE_YUE);
        }
    }

    /**
     * 切换支付方式
     *
     * @param type 1 微信 2 支付宝
     */
    private void changePayType(int type) {
        if (mCheckedPayType == type) {
            return;
        }

        switch (mCheckedPayType) {
            case PAY_TYPE_WX:
                mPayWxIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_nomal));
                break;
            case PAY_TYPE_ZFB:
                mPayZfbIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_nomal));
                break;
            case PAY_TYPE_YUE:
                mPayYueIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_nomal));
                break;
            default:
                break;
        }
        switch (type) {
            case PAY_TYPE_WX:
                mPayWxIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_checked_blue));
                break;
            case PAY_TYPE_ZFB:
                mPayZfbIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_checked_blue));
                break;
            case PAY_TYPE_YUE:
                mPayYueIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_checked_blue));
                break;
            default:
                break;
        }

        mCheckedPayType = type;
    }

    /**
     * 根据类型获取支付信息
     */
    private void goPay() {
        if (PAY_TYPE_ZFB == mCheckedPayType) {
            getZfbPayParamFromServer();
        } else if (PAY_TYPE_WX == mCheckedPayType) {
            getWxPayParamFromServer();
        } else if (PAY_TYPE_YUE == mCheckedPayType) {
            getYePayParamFromServer();
        }
    }


    /**
     * 启动微信支付
     */
    private void startWxPay(WeiXinPay payPara) {
        if (null == payPara) {
            ToastUtils.showToast(mContext, "支付失败，请重试");
            return;
        }

        LogUtils.showLog("testPayParam", "info=" + payPara.toString());

        ToastUtils.showToast(mContext, "启动微信");

        WxPayUtil.WXPay(payPara);
    }

    /**
     * 启动支付宝支付
     */
    private void startZfbPay(ZFBPay payPara) {
        if (null == payPara) {
            ToastUtils.showToast(mContext, "支付失败，请重试");
            return;
        }

        LogUtils.showLog("testPayParam", "info=" + payPara.toString());

        ToastUtils.showToast(mContext, "启动支付宝");

        MyPayUtil payUtil = new MyPayUtil(ActivityBondPay.this);
        payUtil.pay(ActivityBondPay.this, payPara);
    }

    /**
     * 获取支付信息
     */
    private void getZfbPayParamFromServer() {
        if (null == mLoadingDialog) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("OrderID", mOrderId);
        paramMap.put("OrderCode", mOrderCode);
        paramMap.put("OrderType", mOrderType);

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_ZFB_URL, paramMap
                , new RequestObjectCallBack<ZFBPay>("getZfbInfo", mContext, ZFBPay.class) {
                    @Override
                    public void onSuccessResult(ZFBPay bean) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }

                        startZfbPay(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 获取weixin支付信息
     */
    private void getWxPayParamFromServer() {
        if (null == mLoadingDialog) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("OrderID", mOrderId);
        paramMap.put("OrderCode", mOrderCode);
        paramMap.put("OrderType", mOrderType);

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_WX_URL, paramMap
                , new RequestObjectCallBack<WeiXinPay>("getWxInfo", mContext, WeiXinPay.class) {
                    @Override
                    public void onSuccessResult(WeiXinPay bean) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }

                        startWxPay(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }
                });


    }

    /**
     * 获取余额支付信息
     */
    private void getYePayParamFromServer() {

        if (SPUtils.newIntance(mContext).getPayPassInfo() == 0) {
            DialogUtils.showCustomViewDialog(mContext,
                    getResources().getString(R.string.dialog_password_title),
                    getResources().getString(R.string.dialog_nopassword_msg),
                    null,
                    "去设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ChangeLoginActivity.launch(mActivity, ChangeLoginActivity.SET_TYPE_PAY_PASS, 1002);
                        }
                    },
                    "暂不设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        } else {
            PopEnterPassword popEnterPassword = new PopEnterPassword(this,
                    StringUtils.convertNull(mMoney),
                    "账户余额为：" + SPUtils.newIntance(mContext).getUserBalance());//本次支付tips，最后的参数是金额下面的小字


            // 显示窗口
            popEnterPassword.showAtLocation(this.findViewById(R.id.pay_main_ll),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
            popEnterPassword.setPassPopInterImps(ActivityBondPay.this);
        }
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
    public void updateEventBusLogin(BusEvent.WxPayEvent ev) {
        if (ev.getErrorCode() == 0) {
            finishPay();
        } else if (ev.getErrorCode() == -1) {
            ToastUtils.showToast(mContext, "支付异常");
        } else if (ev.getErrorCode() == -2) {
            ToastUtils.showToast(mContext, "支付取消了");
        }
    }

    //支付完成之后的操作
    private void finishPay() {
        ToastUtils.showToast(mContext, "支付成功");

        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }

    @Override
    public void getPassPopInterImps(int which, String str) {
        if (which == SkipUtils.PAY_PASSWORD_RECALL) {//找回密码
            ChangeLoginActivity.launch(mActivity
                    ,ChangeLoginActivity.CHANGE_TYPE_PAY_PASS ,REQUEST_CODE_CHANGE_PASS);
        } else if (which == SkipUtils.PAY_PASSWORD_RIGHT) {//yanzheng密码的正确性
            checkPassWordsForYue(str);
        }
    }

    //余额支付
    private void checkPassWordsForYue(String str) {

        if (null == mLoadingDialog) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("OrderID", mOrderId);
        paramMap.put("OrderCode", mOrderCode);
        paramMap.put("OrderType", mOrderType);
        paramMap.put("PayPassWord", str);

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_YE_URL, paramMap
                , new RequestObjectCallBack<String>("get余额Info", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        finishPay();
//                        startWxPay(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1002 == requestCode) {
            if (RESULT_OK == resultCode) {
                getYePayParamFromServer();
            }
        }else if(REQUEST_CODE_CHANGE_PASS == requestCode){
            if (RESULT_OK == resultCode) {

            }
        }

    }
}
