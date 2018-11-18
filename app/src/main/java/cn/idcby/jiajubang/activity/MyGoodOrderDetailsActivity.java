package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderDetails;
import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodOrderDetailsGood;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 商品订单
 * Created on 2018/5/17.
 *
 * 2018-08-06 16:44:27
 * 【问题汇总0804】闲置商品不需要评论
 *
 * 2018-09-14 10:14:11
 * OrderType 改为 1 直供 2 闲置
 *
 *
 */

public class MyGoodOrderDetailsActivity extends BaseMoreStatusActivity {
    private TextView mOrderStateTv ;
    private TextView mOrderNumTv ;

    private TextView mNameTv ;
    private TextView mPhoneTv ;
    private TextView mAddressTv ;

    private View mStoreDv ;
    private View mStoreLay ;
    private TextView mStoreNameTv ;
    private RecyclerView mGoodRv ;
    private TextView mOrderOtherTv ;//留言
    private TextView mOrderTimeTv ;
    private TextView mOrderMoneyTv ;

    private View mOptionsLay ;
    private TextView mFinishTv ;
    private TextView mCancelTv ;
    private TextView mPayTv ;
    private TextView mSendTv ;
    private TextView mEditTv ;
    private TextView mCommentTv ;
    private TextView mDeliveryTypeTv ;
    private TextView mDeliveryMoneyTv ;

    private View mExpressNameDv ;
    private View mExpressNameLay ;
    private TextView mExpressNameTv ;
    private View mExpressNumDv ;
    private View mExpressNumLay ;
    private TextView mExpressNumTv ;

    private String mOrderId ;
    private GoodOrderDetails mDetails ;
    private boolean mIsReceive ;

    private LoadingDialog mDialog ;

    private List<GoodOrderGood> mGoodItemList = new ArrayList<>() ;
    private AdapterGoodOrderDetailsGood mGoodItemAdapter ;

    private static final int REQUEST_CODE_PAY_ORDER = 1001 ;
    private static final int REQUEST_CODE_PAY_COMMENT = 1002 ;

    //编辑订单相关
    private Dialog mOrderEditDialog ;
    private EditText mOrderDescEv ;
    private EditText mOrderMoneyEv ;
    private EditText mOrderDriverEv ;


    @Override
    public void requestData() {
        showSuccessPage();

        getDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_good_order_detail ;
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

        EventBus.getDefault().register(this) ;
        mDialog = new LoadingDialog(mContext) ;

        mOrderStateTv = findViewById(R.id.acti_good_order_dt_state_tv) ;
        mOrderNumTv = findViewById(R.id.acti_good_order_dt_num_tv) ;
        mNameTv = findViewById(R.id.acti_good_order_dt_name_tv) ;
        mPhoneTv = findViewById(R.id.acti_good_order_dt_phone_tv) ;
        mAddressTv = findViewById(R.id.acti_good_order_dt_address_tv) ;
        mStoreDv = findViewById(R.id.acti_good_order_dt_store_dv) ;
        mStoreLay = findViewById(R.id.acti_good_order_dt_store_lay) ;
        mStoreNameTv = findViewById(R.id.acti_good_order_dt_store_name_tv) ;
        mGoodRv = findViewById(R.id.acti_good_order_dt_good_rv) ;
        mOrderOtherTv = findViewById(R.id.acti_good_order_dt_other_tv) ;
        mOrderTimeTv = findViewById(R.id.acti_good_order_dt_time_tv) ;
        mOrderMoneyTv = findViewById(R.id.acti_good_order_dt_money_tv) ;

        mFinishTv = findViewById(R.id.acti_good_order_dt_options_finish_tv) ;
        mCancelTv = findViewById(R.id.acti_good_order_dt_options_cancel_tv) ;
        mPayTv = findViewById(R.id.acti_good_order_dt_options_pay_tv) ;
        mSendTv = findViewById(R.id.acti_good_order_dt_options_send_tv) ;
        mEditTv = findViewById(R.id.acti_good_order_dt_options_edit_tv) ;
        mCommentTv = findViewById(R.id.acti_good_order_dt_options_comment_tv) ;
        mDeliveryTypeTv = findViewById(R.id.acti_good_order_dt_delivery_type_tv) ;
        mDeliveryMoneyTv = findViewById(R.id.acti_good_order_dt_delivery_money_tv) ;
        mOptionsLay = findViewById(R.id.acti_good_order_dt_options_lay) ;

        mExpressNameDv = findViewById(R.id.acti_good_order_dt_express_name_dv) ;
        mExpressNameLay = findViewById(R.id.acti_good_order_dt_express_name_lay) ;
        mExpressNameTv = findViewById(R.id.acti_good_order_dt_express_name_tv) ;
        mExpressNumDv = findViewById(R.id.acti_good_order_dt_express_num_dv) ;
        mExpressNumLay = findViewById(R.id.acti_good_order_dt_express_num_lay) ;
        mExpressNumTv = findViewById(R.id.acti_good_order_dt_express_num_tv) ;

        mStoreLay.setOnClickListener(this);
        mFinishTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);
        mPayTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        mEditTv.setOnClickListener(this);
        mCommentTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_good_order_dt_options_finish_tv == vId){

            if(!mDetails.isGoodNoAfterSale()){//有未处理的售后订单，不允许确认收货
                DialogUtils.showCustomViewDialog(mContext
                        , mContext.getResources().getString(R.string.order_no_finish_tips)
                        ,"确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }else{
                DialogUtils.showCustomViewDialog(mContext, "确认收货"
                        , mContext.getResources().getString(R.string.order_finish_tips)
                        , null, "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                finishOrder(mOrderId) ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }

        }else if(R.id.acti_good_order_dt_options_cancel_tv == vId){

            DialogUtils.showCustomViewDialog(mContext, "取消订单", "取消该订单？"
                    , null, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            cancelOrder(mOrderId) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

        }else if(R.id.acti_good_order_dt_options_pay_tv == vId){
            SkipUtils.toPayBondActivity(mActivity ,REQUEST_CODE_PAY_ORDER
                    ,mDetails.getPayableAmount()
                    ,mOrderId,mDetails.getOrderCode()
                    ,SkipUtils.PAY_ORDER_TYPE_MALL );
        }else if(R.id.acti_good_order_dt_options_send_tv == vId){

            if(mDetails.isNotAllGoodAfterSale()){
                GoodOrderSendActivity.launch(mContext ,mOrderId
                        ,mDetails.getDeliveryName() ,mDetails.getDeliveryMobile()
                        ,mDetails.getReciever() ,mDetails.getRecieverPhone()) ;
            }else{
                DialogUtils.showCustomViewDialog(mContext
                        , mContext.getResources().getString(R.string.order_no_finish_tips)
                        ,"确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }

        }else if(R.id.acti_good_order_dt_options_edit_tv == vId){
            showEditDialog() ;
        }else if(R.id.acti_good_order_dt_options_comment_tv == vId){//评价

            GoodOrderCommentSendActivity.launch(mActivity ,mOrderId
                    ,mDetails.getOrderItem() ,REQUEST_CODE_PAY_COMMENT);

        }else if(R.id.acti_good_order_dt_store_lay == vId){//店铺
            SkipUtils.toStoreIndexActivity(mContext ,mDetails.getMerchantID()) ;
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

        String orderState = mDetails.getStatusText() ;
        String orderNo = mDetails.getOrderCode() ;
        String time = mDetails.getCreateDate() ;
        String money = mDetails.getPayableAmount() ;
        String storeName = mDetails.getMerchantName() ;

        //地址相关
        String receiver = mDetails.getReciever() ;
        String receiverPhone = mDetails.getRecieverPhone() ;
        String address = mDetails.getReceiverAddress() ;

        //留言
        String message = mDetails.getMessage() ;

        //配送方式、运费
        String deliveryType = mDetails.getDeliveryTypeName() ;
        String deliveryMoney = mDetails.getFinalExpressFee() ;

        mDeliveryTypeTv.setText(deliveryType);
        mDeliveryMoneyTv.setText(deliveryMoney);

        //物流相关
        String expressName = mDetails.getExpressName() ;
        String expressNum = mDetails.getExpressNum() ;

        mOrderNumTv.setText(orderNo);
        mOrderStateTv.setText(orderState);
        mOrderTimeTv.setText(time);
        mOrderMoneyTv.setText("¥" + money);
        mStoreNameTv.setText(storeName);
        mStoreLay.setVisibility("".equals(storeName) ? View.GONE : View.VISIBLE) ;
        mStoreDv.setVisibility("".equals(storeName) ? View.GONE : View.VISIBLE) ;

        mNameTv.setText(receiver) ;
        mPhoneTv.setText(receiverPhone) ;
        mAddressTv.setText(address);

        mOrderOtherTv.setText(message) ;

        mExpressNameTv.setText(expressName);
        mExpressNumTv.setText(expressNum);
        mExpressNameLay.setVisibility("".equals(expressName) ? View.GONE : View.VISIBLE);
        mExpressNameDv.setVisibility("".equals(expressName) ? View.GONE : View.VISIBLE);
        mExpressNumLay.setVisibility("".equals(expressName) ? View.GONE : View.VISIBLE);
        mExpressNumDv.setVisibility("".equals(expressName) ? View.GONE : View.VISIBLE);

        mOptionsLay.setVisibility(View.GONE);
        mPayTv.setVisibility(View.GONE);
        mCancelTv.setVisibility(View.GONE);
        mFinishTv.setVisibility(View.GONE);
        mCommentTv.setVisibility(View.GONE);
        mEditTv.setVisibility(View.GONE);
        mSendTv.setVisibility(View.GONE);

        //暂定：商品订单，只有2种情况，一是 我购买的 ，另一个是 我卖出的
        mIsReceive = LoginHelper.isSelf(mContext ,mDetails.getUserId()) ;
        String status = mDetails.getStatus() ;
        if(mIsReceive){//购买的商品
            if("1".equals(status)){//待付款
                mOptionsLay.setVisibility(View.VISIBLE);
                mPayTv.setVisibility(View.VISIBLE);
                mCancelTv.setVisibility(View.VISIBLE);
            }else if("3".equals(status)){
                mOptionsLay.setVisibility(View.VISIBLE);
                mFinishTv.setVisibility(View.VISIBLE);
            }else if("4".equals(status) && !mDetails.isUnuse()){//闲置商品不需要评论
                mOptionsLay.setVisibility(View.VISIBLE);
                mCommentTv.setVisibility(View.VISIBLE);
            }
        }else{
            if("1".equals(status)){
                mOptionsLay.setVisibility(View.VISIBLE);
                mEditTv.setVisibility(View.VISIBLE);
            }else if("2".equals(status)){
                mOptionsLay.setVisibility(View.VISIBLE);
                mSendTv.setVisibility(View.VISIBLE);
            }
        }

        List<GoodOrderGood> goodList = mDetails.getOrderItem() ;
        mGoodItemList.clear();
        mGoodItemList.addAll(goodList) ;

        if(null == mGoodItemAdapter){
            //2018-06-14 10:53:31 暂定 商品付款之后确认收货之前，服务付款之后完成服务之前才能申请售后（主要是钱的问题）
//            boolean canAfter = (mIsReceive && StringUtils.convertString2Count(status) > 1 && StringUtils.convertString2Count(status) < 6) ;
            boolean canAfter = (mIsReceive && StringUtils.convertString2Count(status) > 1 && StringUtils.convertString2Count(status) < 4) ;

            mGoodItemAdapter = new AdapterGoodOrderDetailsGood(mContext,canAfter, mGoodItemList
                    , new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){
                        boolean mIsUnuse = mDetails.isUnuse() ;
                        GoodOrderGood goodInfo = mGoodItemList.get(position) ;
                        String productId = goodInfo.getProductID() ;
                        Intent toDtIt = new Intent();
                        if (!mIsUnuse) {
                            toDtIt.setClass(mContext, GoodDetailActivity.class);
                            toDtIt.putExtra(SkipUtils.INTENT_GOOD_ID, productId);
                        } else {
                            toDtIt.setClass(mContext, UnuseGoodDetailsActivity.class);
                            toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID, productId);
                        }
                        startActivity(toDtIt);
                    }else if(1 == type){
                        GoodOrderGood orderGood = mGoodItemList.get(position) ;
                        SkipUtils.toOrderAfterSaleActivity(mContext ,mDetails.getOrderType(),mOrderId,orderGood.getOrderItemID()
                                ,orderGood.getProductTitle() ,orderGood.getImgUrl()
                                ,orderGood.getTotalPrice(),mIsReceive) ;
                    }
                }
            }) ;
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            mGoodRv.setLayoutManager(layoutManager);
            mGoodRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext));
            mGoodRv.setAdapter(mGoodItemAdapter);
        }else{
            mGoodItemAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 编辑订单
     */
    private void showEditDialog(){
        if(null == mOrderEditDialog){
            mOrderEditDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;
            mOrderEditDialog.setCancelable(false);
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_good_order_edit ,null) ;
            mOrderEditDialog.setContentView(v);

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F) ;
            mOrderDescEv = v.findViewById(R.id.dialog_good_order_edit_desc_ev) ;
            mOrderMoneyEv = v.findViewById(R.id.dialog_good_order_edit_good_ev) ;
            mOrderDriverEv = v.findViewById(R.id.dialog_good_order_edit_driver_ev) ;

            TextView mOkTv = v.findViewById(R.id.dialog_good_order_edit_ok_tv) ;
            TextView mCancelTv = v.findViewById(R.id.dialog_good_order_edit_cancel_tv) ;
            mOkTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toEditOrder() ;
                }
            });
            mCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOrderEditDialog.dismiss() ;
                }
            });

            mOrderEditDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    DialogUtils.showKeyBoard(mOrderMoneyEv);
                }
            });

            mOrderEditDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    DialogUtils.hideKeyBoard(mOrderMoneyEv) ;
                }
            });
        }

        String money = mDetails.getFinalProductAmount() ;
        String driverMoney = mDetails.getFinalExpressFee() ;

        mOrderMoneyEv.setText(money);
        mOrderDriverEv.setText(driverMoney);
        mOrderDescEv.setText("") ;
        mOrderDescEv.requestFocus() ;

        mOrderEditDialog.show() ;
    }

    /**
     * 编辑订单
     */
    private void toEditOrder(){
        String desc = mOrderDescEv.getText().toString().trim() ;
        if("".equals(desc)){
            ToastUtils.showToast(mContext ,"请输入修改描述") ;
            mOrderDescEv.requestFocus() ;
            mOrderDescEv.setText("") ;
            return ;
        }

        String money = mOrderMoneyEv.getText().toString() ;
        if(!MyUtils.isRightMoney(money)){
            ToastUtils.showToast(mContext ,"请输入正确的商品金额") ;
            mOrderMoneyEv.requestFocus() ;
            mOrderMoneyEv.setSelection(money.length());
            return ;
        }

        String driver = mOrderDriverEv.getText().toString() ;
        if(!MyUtils.isRightMoney(driver)){
            ToastUtils.showToast(mContext ,"请输入正确的运费") ;
            mOrderDriverEv.requestFocus() ;
            mOrderDriverEv.setSelection(driver.length());
            return ;
        }
        mOrderEditDialog.dismiss() ;

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Description" ,desc) ;
        paramMap.put("OrderAmount" ,money) ;
        paramMap.put("ExpressFee" ,driver) ;
        paramMap.put("OrderId" , StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_EDIT, paramMap
                , new RequestObjectCallBack<String>("editOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;
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
     * 取消order
     * @param orderId orderId
     */
    private void cancelOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("OrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;
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
     * 完成order
     * @param orderId orderId
     */
    private void finishOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("OrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;
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

    private void getDetails(){
        getDetails(true);
    }

    /**
     * 获取信息
     */
    private void getDetails(boolean show){
        if(show){
            mDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_DETAILS, paramMap
                , new RequestObjectCallBack<GoodOrderDetails>("getDetails" ,mContext ,GoodOrderDetails.class) {
                    @Override
                    public void onSuccessResult(GoodOrderDetails bean) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.GoodOrderRefresh ev){
        if(ev.isRefresh()){//需要刷新
            getDetails(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY_ORDER == requestCode){
            if(Activity.RESULT_OK == resultCode){
                EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;
                EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this) ;
    }
}
