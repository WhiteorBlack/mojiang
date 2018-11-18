package cn.idcby.jiajubang.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyBangMoneyTransferActivity extends BaseActivity {

    private EditText mTransferName;//acti_my_bang_money_transfer_name_ev
    private EditText mMoneyCounts;//acti_my_bang_money_send_moneycounts_ev
    private TextView mBlances;//acti_my_bang_money_balance_tv
    private TextView mSubTextView;//acti_pay_recharge_sub_tv

    private Float ttBangMoney;
    private LoadingDialog mDialog;

    @Override
    public int getLayoutID() {
        return R.layout.activity_bangmoney_transfer;
    }

    @Override
    public void initView() {
        String totleBang = getIntent().getStringExtra("integral");
        ttBangMoney = StringUtils.convertString2Float(totleBang);

        mTransferName = (EditText) findViewById(R.id.acti_my_bang_money_transfer_name_ev);
        mMoneyCounts = (EditText) findViewById(R.id.acti_my_bang_money_send_moneycounts_ev);
        mBlances = (TextView) findViewById(R.id.acti_my_bang_money_balance_tv);
        mSubTextView = (TextView) findViewById(R.id.acti_pay_recharge_sub_tv);
    }

    @Override
    public void initData() {
        mBlances.setText(ttBangMoney+" 个");
    }

    @Override
    public void initListener() {
        mSubTextView.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if (view.getId() == R.id.acti_pay_recharge_sub_tv) {
            doUptaData();
        }
    }

    private void doUptaData() {
        String nameStr = mTransferName.getText().toString().trim();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtils.showToast(mContext, "转账人账户不能为空");
            mTransferName.requestFocus();
            mTransferName.setSelection(nameStr.length());
            return;
        }
        String countsStr = mMoneyCounts.getText().toString().trim();

        if (StringUtils.convertString2Float(countsStr) <= 0 || !MyUtils.isRightMoney(countsStr)) {
            ToastUtils.showToast(mContext, "请输入正确的转账金额");
            mMoneyCounts.requestFocus();
            mMoneyCounts.setSelection(countsStr.length());
            return;
        }

        if (StringUtils.convertString2Float(countsStr) > ttBangMoney) {
            ToastUtils.showToast(mContext, "转账金额不能大于账户余额");
            mMoneyCounts.requestFocus();
            mMoneyCounts.setSelection(countsStr.length());
            return;
        }


//        if(null == mDialog){
//            mDialog = new LoadingDialog(mContext) ;
//        }
//        mDialog.show() ;

//        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
//        paramMap.put("BankName" , bankName) ;
//        paramMap.put("BankNo" , bankNumber) ;
//        paramMap.put("ReceiveUserName" , bankOwner) ;
//        paramMap.put("ReceiveUserPhone" , ownerPhone) ;
//        paramMap.put("WithdrawalsAmount" , money) ;

//        NetUtils.getDataFromServerByPost(mContext, Urls.USER_WITHDRAW, paramMap
//                , new RequestObjectCallBack<String>("submitWithdraw" ,mContext ,String.class) {
//                    @Override
//                    public void onSuccessResult(String bean) {
//                        mDialog.dismiss();
//                        ToastUtils.showToast(mContext ,"提交成功");
//                        setResult(RESULT_OK);
//                        finish() ;
//                    }
//                    @Override
//                    public void onErrorResult(String str) {
//                        mDialog.dismiss();
//                    }
//                    @Override
//                    public void onFail(Exception e) {
//                        mDialog.dismiss();
//
//                        ToastUtils.showToast(mContext ,"提交失败");
//                    }
//                });

    }
}
