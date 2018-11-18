package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.PayBindInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 支付绑定
 * Created on 2018/5/18.
 */

public class PayManagerBindActivity extends BaseMoreStatusActivity {
    public static final int TYPE_ALPAY = 1 ;
    public static final int TYPE_WX = 2 ;
    public static final int TYPE_YL = 3 ;

    private int mBindType = TYPE_ALPAY ;

    private EditText mOneEv ;
    private EditText mTwoEv ;
    private EditText mThreeEv ;
    private EditText mFourEv ;

    private LoadingDialog mDialog ;

    private PayBindInfo mPayInfo ;

    public static void launch(Context context ,int type){
        Intent toBiIt = new Intent(context ,PayManagerBindActivity.class) ;
        toBiIt.putExtra("payBindType" ,type) ;
        context.startActivity(toBiIt);
    }

    public static void launch(Activity context , int type, int requestCode){
        Intent toBiIt = new Intent(context ,PayManagerBindActivity.class) ;
        toBiIt.putExtra("payBindType" ,type) ;
        context.startActivityForResult(toBiIt,requestCode);
    }


    @Override
    public void requestData() {
        showSuccessPage();

        getDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_pay_bind_card;
    }

    @Override
    public String setTitle() {
        return "绑定";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mBindType = getIntent().getIntExtra("payBindType",mBindType) ;

        mDialog = new LoadingDialog(mContext) ;

        TextView oneTipsTv = findViewById(R.id.acti_pay_bind_one_tips_tv) ;
        mOneEv = findViewById(R.id.acti_pay_bind_one_ev) ;
        TextView twoTipsTv = findViewById(R.id.acti_pay_bind_two_tips_tv) ;
        mTwoEv = findViewById(R.id.acti_pay_bind_two_ev) ;
        TextView threeTipsTv = findViewById(R.id.acti_pay_bind_three_tips_tv) ;
        mThreeEv = findViewById(R.id.acti_pay_bind_three_ev) ;
        View fourLay = findViewById(R.id.acti_pay_bind_four_lay) ;
        TextView fourTipsTv = findViewById(R.id.acti_pay_bind_four_tips_tv) ;
        mFourEv = findViewById(R.id.acti_pay_bind_four_ev) ;
        View submitTv = findViewById(R.id.acti_pay_bind_sub_tv) ;
        submitTv.setOnClickListener(this);

        if(TYPE_ALPAY == mBindType){
            setTitleText("绑定支付宝");

            oneTipsTv.setText("支付宝账号") ;
            twoTipsTv.setText("姓名");
            threeTipsTv.setText("手机号");

            mOneEv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) ;
            mTwoEv.setInputType(InputType.TYPE_CLASS_TEXT) ;
            mThreeEv.setInputType(InputType.TYPE_CLASS_NUMBER) ;
            mOneEv.setHint("请输入支付宝账号");
            mTwoEv.setHint("请输入姓名");
            mThreeEv.setHint("请输入手机号");
            InputFilter[] filters = {new InputFilter.LengthFilter(11)};
            mThreeEv.setFilters(filters) ;

        }else if(TYPE_WX == mBindType){
            setTitleText("绑定微信");

            oneTipsTv.setText("微信号") ;
            twoTipsTv.setText("姓名");
            threeTipsTv.setText("手机号");

            mOneEv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) ;
            mTwoEv.setInputType(InputType.TYPE_CLASS_TEXT) ;
            mThreeEv.setInputType(InputType.TYPE_CLASS_NUMBER) ;
            mOneEv.setHint("请输入微信号");
            mTwoEv.setHint("请输入姓名");
            mThreeEv.setHint("请输入手机号");
            InputFilter[] filters = {new InputFilter.LengthFilter(11)};
            mThreeEv.setFilters(filters) ;

        }else if(TYPE_YL == mBindType){
            setTitleText("绑定银行卡");

            fourLay.setVisibility(View.VISIBLE);

            oneTipsTv.setText("银行卡号") ;
            twoTipsTv.setText("开户人姓名");
            threeTipsTv.setText("开户人手机号");
            fourTipsTv.setText("开户银行");

            mOneEv.setInputType(InputType.TYPE_CLASS_NUMBER) ;
            mTwoEv.setInputType(InputType.TYPE_CLASS_TEXT) ;
            mThreeEv.setInputType(InputType.TYPE_CLASS_NUMBER) ;
            mFourEv.setInputType(InputType.TYPE_CLASS_TEXT) ;
            mOneEv.setHint("请输入银行卡号");
            mTwoEv.setHint("请输入开户人姓名");
            mThreeEv.setHint("请输入开户人手机号");
            mFourEv.setHint("请输入开户银行");
            InputFilter[] filters = {new InputFilter.LengthFilter(11)};
            mThreeEv.setFilters(filters) ;
        }

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;
        if(R.id.acti_pay_bind_sub_tv == vId){
            submitInfo() ;
        }
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

            mOneEv.setText(num);
            mTwoEv.setText(name);
            mThreeEv.setText(phone);
            mFourEv.setText(bkName);
        }
    }

    /**
     * 获取信息
     */
    private void getDetails(){
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,"" + mBindType) ;

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
     * 提交信息
     */
    private void submitInfo(){
        String num = mOneEv.getText().toString().trim() ;
        if("".equals(num)){
            ToastUtils.showToast(mContext ,"请输入内容") ;
            mOneEv.requestFocus() ;
            mOneEv.setText("") ;
            return ;
        }

        String name = mTwoEv.getText().toString().trim() ;
        if("".equals(name)){
            ToastUtils.showToast(mContext ,"请输入内容") ;
            mTwoEv.requestFocus() ;
            mTwoEv.setText("") ;
            return ;
        }

        String phone = mThreeEv.getText().toString() ;
        if(!MyUtils.isRightPhone(phone.trim())){
            ToastUtils.showToast(mContext ,"请输入正确手机号") ;
            mThreeEv.requestFocus() ;
            mThreeEv.setSelection(phone.length()) ;
            return ;
        }

        String bkName = mFourEv.getText().toString().trim() ;
        if("".equals(bkName) && TYPE_YL == mBindType){
            ToastUtils.showToast(mContext ,"请输入内容") ;
            mFourEv.requestFocus() ;
            mFourEv.setText("") ;
            return ;
        }

        mDialog.show();

        String payId = "" ;
        if(mPayInfo != null){
            payId = mPayInfo.getWithdrawInfoId() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("WithdrawInfoId",payId) ;
        paramMap.put("Type","" + mBindType) ;
        paramMap.put("ReceiveNumber",num) ;
        paramMap.put("ReceiveUserName",name) ;
        paramMap.put("ReceiveUserPhone",phone) ;
        paramMap.put("ReceiveBankName",bkName) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.PAY_BIND_INFO_EDIT, paramMap
                , new RequestObjectCallBack<String>("submitInfo" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDetails") ;
    }
}
