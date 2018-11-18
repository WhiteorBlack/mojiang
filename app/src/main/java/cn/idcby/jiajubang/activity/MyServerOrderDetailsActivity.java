package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ServerOrderDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 服务详细
 * Created on 2018/5/17.
 */

public class MyServerOrderDetailsActivity extends BaseMoreStatusActivity {
    private TextView mOrderNoTv ;
    private TextView mAddressTv ;
    private TextView mDataTv ;
    private TextView mCategoryTv ;
    private TextView mMoneyTv ;
    private TextView mUserTipsTv ;
    private TextView mUserTv ;
    private TextView mPhoneTv ;
    private TextView mCusNameTv ;
    private TextView mCusPhoneTv ;
    private TextView mStateTv ;

    private View mOpLay ;
    private TextView mOpCommentTv ;
    private TextView mOpFinishTv ;
    private TextView mOpStartTv ;
    private TextView mOpPayTv ;
    private TextView mOpEditTv ;
    private TextView mOpCancelTv ;
    private TextView mOpAfterSaleTv;

    private boolean mIsReceive = false ;
    private String mOrderId ;
    private ServerOrderDetails mDetails ;
    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_PAY = 1101 ;
    private static final int REQUEST_CODE_COMMENT = 1102 ;
    private static final int REQUEST_CODE_EDIT = 1103 ;
    private static final int REQUEST_CODE_FINISH = 1104 ;

    private Dialog mEditOrderDialog ;
    private EditText mOrderMoneyEv ;


    @Override
    public void requestData() {
        showSuccessPage();

        mDialog.show() ;
        getDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_server_order_details;
    }

    @Override
    public String setTitle() {
        return "订单详情";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;

        mDialog = new LoadingDialog(mContext) ;

        mOrderNoTv = findViewById(R.id.acti_server_order_details_num_tv) ;
        mAddressTv = findViewById(R.id.acti_server_order_details_address_tv) ;
        mDataTv = findViewById(R.id.acti_server_order_details_data_tv) ;
        mCategoryTv = findViewById(R.id.acti_server_order_details_category_tv) ;
        mMoneyTv = findViewById(R.id.acti_server_order_details_money_tv) ;
        mUserTipsTv = findViewById(R.id.acti_server_order_details_user_tips_tv) ;
        mUserTv = findViewById(R.id.acti_server_order_details_user_tv) ;
        mPhoneTv = findViewById(R.id.acti_server_order_details_phone_tv) ;
        mCusNameTv = findViewById(R.id.acti_server_order_details_cus_name_tv) ;
        mCusPhoneTv = findViewById(R.id.acti_server_order_details_cus_phone_tv) ;
        mStateTv = findViewById(R.id.acti_server_order_details_state_tv) ;

        mOpLay = findViewById(R.id.acti_server_order_dt_op_lay) ;
        mOpCommentTv = findViewById(R.id.acti_server_order_dt_op_comment_server) ;
        mOpFinishTv = findViewById(R.id.acti_server_order_dt_op_finish_server) ;
        mOpStartTv = findViewById(R.id.acti_server_order_dt_op_start_server) ;
        mOpPayTv = findViewById(R.id.acti_server_order_dt_op_pay_server) ;
        mOpEditTv = findViewById(R.id.acti_server_order_dt_op_edit_server) ;
        mOpCancelTv = findViewById(R.id.acti_server_order_dt_op_cancel_server) ;
        mOpAfterSaleTv = findViewById(R.id.acti_server_order_dt_op_after_server) ;

        mOpCancelTv.setOnClickListener(this);
        mOpCommentTv.setOnClickListener(this);
        mOpFinishTv.setOnClickListener(this);
        mOpStartTv.setOnClickListener(this);
        mOpPayTv.setOnClickListener(this);
        mOpEditTv.setOnClickListener(this);
        mOpAfterSaleTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_server_order_dt_op_comment_server == vId){
            Intent toCmIt = new Intent(mContext ,MyServerOrderCommentActivity.class) ;
            toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID ,mOrderId) ;
            startActivityForResult(toCmIt ,REQUEST_CODE_COMMENT) ;
        }else if(R.id.acti_server_order_dt_op_finish_server == vId){
            final String orderId = mDetails.getServiceOrderId() ;

            DialogUtils.showCustomViewDialog(mContext, "温馨提示"
                    ,mContext.getResources().getString(R.string.server_finish_tips), null
                    ,"确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            finishServer(orderId) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_server_order_dt_op_start_server == vId){
            final String orderId = mDetails.getServiceOrderId() ;

            DialogUtils.showCustomViewDialog(mContext, "温馨提示" ,"开始服务？", null
                    ,"确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            startServerOrder(orderId) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_server_order_dt_op_cancel_server == vId){
            final String orderId = mDetails.getServiceOrderId() ;

            DialogUtils.showCustomViewDialog(mContext, "温馨提示" ,"取消该订单？", null
                    ,"确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            cancelServerOrder(orderId) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_server_order_dt_op_pay_server == vId){
            String money = mDetails.getServiceAmount() ;
            String orderId = mDetails.getServiceOrderId() ;
            String orderCode = mDetails.getOrderNO() ;

            //跳转到付款
            SkipUtils.toPayBondActivity(mActivity ,REQUEST_CODE_PAY
                    ,money , orderId ,orderCode ,SkipUtils.PAY_ORDER_TYPE_ORDER_SERVICE);
        }else if(R.id.acti_server_order_dt_op_after_server == vId){//售后
            SkipUtils.toOrderAfterSaleActivity(mContext ,SkipUtils.ORDER_TYPE_SERVER ,mOrderId,""
                    ,mDetails.getServiceUserName(),"" ,mDetails.getServiceAmount(),mIsReceive) ;
        }else if(R.id.acti_server_order_dt_op_edit_server == vId){//编辑
            if(mIsReceive){
                ServerConfirmActivity.launch(mActivity ,mOrderId ,REQUEST_CODE_EDIT);
            }else{
                showOrderEditDialog() ;
            }
        }
    }

    private void updateDisplay(){
        mDialog.dismiss();

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "订单信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        String orderNo = mDetails.getOrderNO() ;
        String statusName = mDetails.getOrderStatusName() ;
        int orderStatus = mDetails.getOrderStatus() ;
        String desc = mDetails.getServiceintroduce() ;

        String province = mDetails.getProvinceName() ;
        String cityName = mDetails.getCityName() ;
        String address = mDetails.getServiceAddress() ;
        String time = mDetails.getServiceTime() ;
        String name = mDetails.getContacts() ;
        String phone = mDetails.getContactsPhone() ;
        String cusName = mDetails.getServiceUserName() ;
        String cusPhone = mDetails.getServiceUserAccount() ;

        String money = mDetails.getServiceAmount() ;
        mIsReceive = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId());

        mOrderNoTv.setText(orderNo) ;
        mStateTv.setText(statusName);
        mCategoryTv.setText(desc);
        mAddressTv.setText(province + cityName + address);
        mDataTv.setText(time);
        mUserTv.setText(cusName);
        mPhoneTv.setText(cusPhone);
        mCusNameTv.setText(name);
        mCusPhoneTv.setText(phone);
        mMoneyTv.setText(money+"元");

        mOpLay.setVisibility(View.GONE);
        mOpCommentTv.setVisibility(View.GONE);
        mOpFinishTv.setVisibility(View.GONE);
        mOpStartTv.setVisibility(View.GONE);
        mOpPayTv.setVisibility(View.GONE);
        mOpEditTv.setVisibility(View.GONE);
        mOpAfterSaleTv.setVisibility(View.GONE);
        mOpCancelTv.setVisibility(View.GONE);

        if(mIsReceive){
            if(1 == orderStatus){
                mOpLay.setVisibility(View.VISIBLE);
                mOpPayTv.setVisibility(View.VISIBLE);
                mOpEditTv.setVisibility(View.VISIBLE);
                mOpCancelTv.setVisibility(View.VISIBLE);
            }else if(3 == orderStatus){
                mOpLay.setVisibility(View.VISIBLE);
                mOpFinishTv.setVisibility(View.VISIBLE);
            }else if(4 == orderStatus){
                mOpLay.setVisibility(View.VISIBLE);
                mOpCommentTv.setVisibility(View.VISIBLE);
            }
        }else{
            if(1 == orderStatus){
                mOpLay.setVisibility(View.VISIBLE);
                mOpEditTv.setVisibility(View.VISIBLE);
            }else if(2 == orderStatus){
                mOpLay.setVisibility(View.VISIBLE);
                mOpStartTv.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 编辑订单
     */
    private void showOrderEditDialog(){
        if(null == mEditOrderDialog){
            mEditOrderDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_server_order ,null) ;
            mEditOrderDialog.setContentView(view);

            mOrderMoneyEv = view.findViewById(R.id.dialog_server_order_money_ev) ;
            TextView cancelTv = view.findViewById(R.id.dialog_server_order_edit_cancel_tv) ;
            TextView okTv = view.findViewById(R.id.dialog_server_order_edit_ok_tv) ;

            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String editMoney = mOrderMoneyEv.getText().toString() ;
                    if(StringUtils.convertString2Float(editMoney) <= 0){
                        ToastUtils.showToast(mContext ,"请输入正确的金额") ;
                        return ;
                    }
                    mEditOrderDialog.dismiss() ;

                    editOrder(editMoney) ;
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditOrderDialog.dismiss() ;
                }
            });

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F) ;

            mEditOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    DialogUtils.hideKeyBoard(mOrderMoneyEv);
                }
            });
            mEditOrderDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    DialogUtils.showKeyBoard(mOrderMoneyEv) ;
                }
            });
        }

        String money = mDetails.getServiceAmount() ;
        mOrderMoneyEv.setText(money);
        mOrderMoneyEv.setSelection(money.length()) ;

        mEditOrderDialog.show() ;
    }

    /**
     * 获取信息
     */
    private void getDetails(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_DETAILS, paramMap
                , new RequestObjectCallBack<ServerOrderDetails>("getDetails" ,mContext ,ServerOrderDetails.class) {
                    @Override
                    public void onSuccessResult(ServerOrderDetails bean) {
                        mDetails = bean ;

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
     * 取消服务
     * @param orderId orderId
     */
    private void cancelServerOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelServerOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext ,"提交成功");
                        EventBus.getDefault().post(new BusEvent.ServerOrderRefresh(true));
                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        getDetails();
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

    /**
     * 开始服务
     */
    private void startServerOrder(String orderId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ServiceOrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_START, paramMap
                , new RequestObjectCallBack<String>("startServer" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        ToastUtils.showToast(mContext ,"提交成功");
                        EventBus.getDefault().post(new BusEvent.ServerOrderRefresh(true));
                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        getDetails();
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

    /**
     * 完成服务
     */
    private void finishServer(String orderId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ServiceOrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishServer" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        ToastUtils.showToast(mContext ,"提交成功");
                        EventBus.getDefault().post(new BusEvent.ServerOrderRefresh(true));
                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        Intent toCmIt = new Intent(mContext ,MyServerOrderCommentActivity.class) ;
                        toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID ,mOrderId) ;
                        startActivityForResult(toCmIt ,REQUEST_CODE_FINISH) ;
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

    /**
     * 编辑订单
     * @param money money
     */
    private void editOrder(String money){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false) ;
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ServiceAmount" ,money) ;
        paramMap.put("ServiceOrderId" ,StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_EDIT, paramMap
                , new RequestObjectCallBack<String>("editOrder" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"提交成功");

                        EventBus.getDefault().post(new BusEvent.ServerOrderRefresh(true));
                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;
                        getDetails();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mDialog.show() ;
                getDetails();
            }
        }else if(REQUEST_CODE_COMMENT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mDialog.show() ;
                getDetails();
            }
        }else if(REQUEST_CODE_EDIT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mDialog.show() ;
                getDetails();
            }
        }else if(REQUEST_CODE_FINISH == requestCode){
            mDialog.show() ;
            getDetails();
        }
    }
}
