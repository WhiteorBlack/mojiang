package cn.idcby.jiajubang.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.PayBindInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 提现
 * Created on 2018/4/14.
 */

public class WithdrawActivity extends BaseActivity {
    private TextView mTypeTv;
    private TextView mNumberTv;
    private TextView mOwnerTv;
    private TextView mPhoneTv;
    private EditText mMoneyEv ;

    private LoadingDialog mDialog ;

    private int mPayType = -1 ;
    private PayBindInfo mPayInfo ;
    private String mMoney ;

    private Dialog mPayTypeDialog ;

    private static final int MIN_MONEY = 50 ;

    private static final int REQUEST_CODE_FOR_BIND = 1001 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_withdraw ;
    }

    @Override
    public void initView() {
        mMoney = getIntent().getStringExtra("money") ;

        mDialog = new LoadingDialog(mContext) ;

        mTypeTv = findViewById(R.id.acti_withdraw_bankName_ev) ;
        mNumberTv = findViewById(R.id.acti_withdraw_bankNumber_ev) ;
        mOwnerTv = findViewById(R.id.acti_withdraw_bankOwner_ev) ;
        mPhoneTv = findViewById(R.id.acti_withdraw_bankPhone_ev) ;
        mMoneyEv = findViewById(R.id.acti_withdraw_money_ev) ;
        View moneyLay = findViewById(R.id.acti_withdraw_money_lay) ;
        TextView moneyTv = findViewById(R.id.acti_withdraw_money_tv) ;
        View submitView = findViewById(R.id.acti_withdraw_sub_tv) ;
        submitView.setOnClickListener(this);

        if(mMoney != null){
            moneyTv.setText(mMoney + "元") ;
            moneyLay.setVisibility(View.VISIBLE);
        }

        mTypeTv.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_withdraw_sub_tv == view.getId()){
            submitWithdraw() ;
        }else if(R.id.acti_withdraw_bankName_ev == view.getId()){
            showTypeDialog() ;
        }
    }

    /**
     * 显示类型
     */
    private void showTypeDialog(){
        if(null == mPayTypeDialog){
            mPayTypeDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;

            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose_pay_type ,null) ;
            mPayTypeDialog.setContentView(view);

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F);

            View oneTv = view.findViewById(R.id.dialog_check_pay_type_one_tv) ;
            View twoTv = view.findViewById(R.id.dialog_check_pay_type_two_tv) ;
            View threeTv = view.findViewById(R.id.dialog_check_pay_type_three_tv) ;

            oneTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPayTypeDialog.dismiss();

                    mPayInfo = null ;
                    mPayType = PayManagerBindActivity.TYPE_ALPAY ;
                    mTypeTv.setText("支付宝");
                    getDetails() ;
                }
            });
            twoTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPayTypeDialog.dismiss();

                    mPayInfo = null ;
                    mPayType = PayManagerBindActivity.TYPE_WX ;
                    mTypeTv.setText("微信");
                    getDetails() ;
                }
            });
            threeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPayTypeDialog.dismiss();

                    mPayInfo = null ;
                    mPayType = PayManagerBindActivity.TYPE_YL ;
                    mTypeTv.setText("银联");
                    getDetails() ;
                }
            });
        }

        mPayTypeDialog.show() ;
    }

    /**
     * 填充内容
     */
    private void updateDisplay(){
        mDialog.dismiss();

        if(mPayInfo != null){
            String num = mPayInfo.getReceiveNumber() ;
            String name = mPayInfo.getReceiveUserName() ;
            String phone = mPayInfo.getReceiveUserPhone() ;
            String bkName = mPayInfo.getReceiveBankName() ;

            if((""+PayManagerBindActivity.TYPE_YL).equals(mPayInfo.getType())){
                num = bkName + "-" + num ;
            }
            mNumberTv.setText(num);
            mOwnerTv.setText(name);
            mPhoneTv.setText(phone);
        }else{
            String typeText = "" ;
            if(mPayType == PayManagerBindActivity.TYPE_ALPAY){
                typeText = "支付宝" ;
            }else if(mPayType == PayManagerBindActivity.TYPE_WX){
                typeText = "微信" ;
            }else if(mPayType == PayManagerBindActivity.TYPE_YL){
                typeText = "银行卡" ;
            }

            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "您尚未绑定" + typeText + "信息，是否去绑定？"
                    , null, "去绑定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    PayManagerBindActivity.launch(mActivity ,mPayType,REQUEST_CODE_FOR_BIND) ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mNumberTv.setText("");
            mOwnerTv.setText("");
            mPhoneTv.setText("");
        }
    }

    /**
     * 获取信息
     */
    private void getDetails(){
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,"" + mPayType) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_BIND_INFO, paramMap
                , new RequestObjectCallBack<PayBindInfo>("getDetails" ,mContext ,PayBindInfo.class) {
                    @Override
                    public void onSuccessResult(PayBindInfo bean) {
                        mPayInfo = bean ;

                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                    }
                });
    }

    /**
     * 提交
     */
    private void submitWithdraw(){
        if(StringUtils.convertString2Float(mMoney) < MIN_MONEY){
            ToastUtils.showToast(mContext ,"可提现金额不足");
            return ;
        }

        if(null == mPayInfo){
            ToastUtils.showToast(mContext ,"请选择合适的类型");
            return ;
        }

        String money = mMoneyEv.getText().toString() ;
        if(StringUtils.convertString2Float(money) <= 0 || !MyUtils.isRightMoney(money)){
            ToastUtils.showToast(mContext ,"请输入正确的提现金额");
            mMoneyEv.requestFocus() ;
            mMoneyEv.setSelection(money.length()) ;
            return ;
        }

        if(StringUtils.convertString2Float(money) > StringUtils.convertString2Float(mMoney)){
            ToastUtils.showToast(mContext ,"请输入正确的提现金额");
            return ;
        }

        if(StringUtils.convertString2Float(money) % MIN_MONEY > 0){
            ToastUtils.showToast(mContext ,"提现金额为50的倍数");
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mPayInfo.getWithdrawInfoId()) ;
        paramMap.put("ID" , money) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_WITHDRAW, paramMap
                , new RequestObjectCallBack<String>("submitWithdraw" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"提交成功");
                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext ,"提交失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_BIND == requestCode){
            if(RESULT_OK == resultCode){
                getDetails() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDetails") ;
    }
}
