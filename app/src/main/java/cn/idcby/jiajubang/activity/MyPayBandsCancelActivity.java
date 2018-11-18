package cn.idcby.jiajubang.activity;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.PayBindInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * （保证金）退保
 * Created on 2018/4/13.
 */

public class MyPayBandsCancelActivity extends BaseActivity {
    private TextView mTypeTv;
    private TextView mNumberTv;
    private TextView mOwnerTv;
    private TextView mPhoneTv;

    private int mBondType = 0 ; //1.安装工 2.服务工 3.店铺

    private LoadingDialog mDialog ;

    private int mPayType = -1 ;
    private PayBindInfo mPayInfo ;

    private Dialog mPayTypeDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_pay_bond_cancel ;
    }

    @Override
    public void initView() {
        mBondType = getIntent().getIntExtra("payBondType" , 0) ;

        mDialog = new LoadingDialog(mContext) ;

        mTypeTv = findViewById(R.id.acti_pay_bond_cancel_type_tv) ;
        mNumberTv = findViewById(R.id.acti_pay_bond_cancel_number_tv) ;
        mOwnerTv = findViewById(R.id.acti_pay_bond_cancel_owner_ev) ;
        mPhoneTv = findViewById(R.id.acti_pay_bond_cancel_phone_ev) ;
        View submitView = findViewById(R.id.acti_pay_bond_cancel_sub_tv) ;
        submitView.setOnClickListener(this);
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
        if(R.id.acti_pay_bond_cancel_sub_tv == view.getId()){
            submitCancelBond() ;
        }else if(R.id.acti_pay_bond_cancel_type_tv == view.getId()){
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
            ToastUtils.showToast(mContext ,"暂无信息");
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
     * 退保提交
     */
    private void submitCancelBond(){
        if(null == mPayInfo){
            ToastUtils.showToast(mContext ,"请选择合适的类型");
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mPayInfo.getWithdrawInfoId()) ;
        paramMap.put("ID" , "" + mBondType) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_BONDS_CANCEL, paramMap
                , new RequestObjectCallBack<String>("submitCancelBond" ,mContext ,String.class) {
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
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDetails") ;
    }
}
