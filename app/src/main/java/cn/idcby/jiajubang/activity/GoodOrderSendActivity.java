package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 发货
 * Created on 2018/4/28.
 */

public class GoodOrderSendActivity extends BaseActivity {
    private String mOrderId ;

    private EditText mExprNameEv ;
    private EditText mExprNumEv ;
    private EditText mReceiverNameEv ;
    private EditText mReceiverPhoneEv ;
    private EditText mSendNameEv ;
    private EditText mSendPhoneEv ;

    private LoadingDialog mDialog ;

    public static void launch(Context context ,String orderId ,String name ,String phone,String Rname ,String Rphone){
        Intent toSdIt = new Intent(context , GoodOrderSendActivity.class) ;
        toSdIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
        toSdIt.putExtra(SkipUtils.INTENT_GOOD_SEND_NAME ,name) ;
        toSdIt.putExtra(SkipUtils.INTENT_GOOD_SEND_PHONE ,phone) ;
        toSdIt.putExtra(SkipUtils.INTENT_RECEIVER_NAME ,Rname) ;
        toSdIt.putExtra(SkipUtils.INTENT_RECEIVER_PHONE ,Rphone) ;
        context.startActivity(toSdIt);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_good_order_send ;
    }

    @Override
    public void initView() {
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;
        String name = getIntent().getStringExtra(SkipUtils.INTENT_GOOD_SEND_NAME) ;
        String phone = getIntent().getStringExtra(SkipUtils.INTENT_GOOD_SEND_PHONE) ;
        String Rname = getIntent().getStringExtra(SkipUtils.INTENT_RECEIVER_NAME) ;
        String Rphone = getIntent().getStringExtra(SkipUtils.INTENT_RECEIVER_PHONE) ;

        mExprNameEv = findViewById(R.id.acti_good_order_send_expr_name_ev) ;
        mExprNumEv = findViewById(R.id.acti_good_order_send_expr_num_ev) ;
        mReceiverNameEv = findViewById(R.id.acti_good_order_send_receiver_name_ev) ;
        mReceiverPhoneEv = findViewById(R.id.acti_good_order_send_receiver_phone_ev) ;
        mSendNameEv = findViewById(R.id.acti_good_order_send_send_name_ev) ;
        mSendPhoneEv = findViewById(R.id.acti_good_order_send_send_phone_ev) ;
        TextView mSubTv = findViewById(R.id.acti_good_order_send_send_submit_tv) ;

        mExprNameEv.requestFocus() ;
        mReceiverNameEv.setText(StringUtils.convertNull(Rname));
        mReceiverPhoneEv.setText(StringUtils.convertNull(Rphone));
        mSendNameEv.setText(StringUtils.convertNull(name));
        mSendPhoneEv.setText(StringUtils.convertNull(phone));

        mSubTv.setOnClickListener(this) ;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_good_order_send_send_submit_tv == view.getId()){
            submitOrderSend() ;
        }
    }

    /**
     * 提交发货
     */
    private void submitOrderSend(){
        String exprName = mExprNameEv.getText().toString().trim() ;
        if("".equals(exprName)){
            mExprNameEv.requestFocus() ;
            mExprNameEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入快递公司名称");
            return ;
        }

        String exprNum = mExprNumEv.getText().toString().trim() ;
        if("".equals(exprNum)){
            mExprNumEv.requestFocus() ;
            mExprNumEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入快递单号");
            return ;
        }

        String receiverName = mReceiverNameEv.getText().toString().trim() ;
        if("".equals(receiverName)){
            mReceiverNameEv.requestFocus() ;
            mReceiverNameEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入收货人姓名");
            return ;
        }

        String receiverPhone = mReceiverPhoneEv.getText().toString().trim() ;
        if("".equals(receiverPhone)){
            mReceiverPhoneEv.requestFocus() ;
            mReceiverPhoneEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入收货人电话");
            return ;
        }

        String sendName = mSendNameEv.getText().toString().trim() ;
        if("".equals(sendName)){
            mSendNameEv.requestFocus() ;
            mSendNameEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入发货人姓名");
            return ;
        }

        String sendPhone = mSendPhoneEv.getText().toString().trim() ;
        if("".equals(sendPhone)){
            mSendPhoneEv.requestFocus() ;
            mSendPhoneEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入发货人电话");
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderId" , StringUtils.convertNull(mOrderId)) ;
        paramMap.put("ExpressName" , exprName) ;
        paramMap.put("ExpressNO" , exprNum) ;
        paramMap.put("Reciever" , receiverName) ;
        paramMap.put("RecieverPhone" , receiverPhone) ;
        paramMap.put("Sender" , sendName) ;
        paramMap.put("SendPhone" , sendPhone) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_SEND, paramMap
                , new RequestObjectCallBack<String>("" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true));

                        DialogUtils.showCustomViewDialog(mContext, "发货成功！"
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish() ;
                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        DialogUtils.showCustomViewDialog(mContext, "发货失败"
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
    }



}
