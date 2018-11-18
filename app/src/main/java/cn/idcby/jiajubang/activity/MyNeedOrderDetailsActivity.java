package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.MyNeedOrderDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalImage;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 需求订单详细
 * Created on 2018/5/17.
 *
 * 2018-07-03 17:28:12
 * 需求订单取消了
 * 改界面理论上不会出现了
 */
@Deprecated
public class MyNeedOrderDetailsActivity extends BaseMoreStatusActivity {
    private TextView mOrderStateTv ;
    private TextView mOrderNoTv ;
    private TextView mOrderMoneyTv ;
    private TextView mOrderTimeTv ;
    private TextView mOrderDescTv ;
    private RecyclerView mOrderPicRv ;
    private View mOrderOpLay ;
    private View mFinishTv ;
    private View mCancelTv ;
    private View mPayTv ;
    private View mAfterSaleTv ;

    private boolean mIsReceive = false ;
    private String mOrderId ;
    private MyNeedOrderDetails mDetails ;
    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_PAY_ORDER = 1001 ;

    @Override
    public void requestData() {
        showSuccessPage() ;

        mDialog.show() ;
        getDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_need_order_details;
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
        EventBus.getDefault().register(this);

        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;

        mDialog = new LoadingDialog(mContext) ;

        mOrderStateTv = findViewById(R.id.acti_need_order_details_state_tv) ;
        mOrderNoTv = findViewById(R.id.acti_need_order_details_num_tv) ;
        mOrderMoneyTv = findViewById(R.id.acti_need_order_details_money_tv) ;
        mOrderTimeTv = findViewById(R.id.acti_need_order_details_time_tv) ;
        mOrderDescTv = findViewById(R.id.acti_need_order_details_desc_tv) ;
        mOrderPicRv = findViewById(R.id.acti_need_order_details_pic_rv) ;
        mFinishTv = findViewById(R.id.acti_need_order_details_options_finish_tv) ;
        mCancelTv = findViewById(R.id.acti_need_order_details_options_cancel_tv) ;
        mPayTv = findViewById(R.id.acti_need_order_details_options_pay_tv) ;
        mAfterSaleTv = findViewById(R.id.acti_need_order_details_options_after_tv) ;
        mOrderOpLay = findViewById(R.id.acti_need_order_details_options_lay) ;

        mFinishTv.setOnClickListener(this);
        mPayTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);
        mAfterSaleTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_need_order_details_options_pay_tv == vId){//付款
            SkipUtils.toPayBondActivity(mActivity ,REQUEST_CODE_PAY_ORDER ,mDetails.getOrderAmount()
                    ,mDetails.getOrderID(),mDetails.getOrderNO() ,SkipUtils.PAY_ORDER_TYPE_ORDER_NEED );
        }else if(R.id.acti_need_order_details_options_cancel_tv == vId){
            Intent toCtIt = new Intent(mContext , MyNeedsOrderCancelActivity.class) ;
            toCtIt.putExtra(SkipUtils.INTENT_ORDER_ID ,mDetails.getOrderID()) ;
            startActivity(toCtIt);
        }else if(R.id.acti_need_order_details_options_finish_tv == vId){
            DialogUtils.showCustomViewDialog(mContext, "完成订单"
                    , mContext.getResources().getString(R.string.need_finish_tips)
                    , null, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            finishNeedOrder(mDetails.getOrderID()) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_need_order_details_options_after_tv == vId){//申请售后
            SkipUtils.toOrderAfterSaleActivity(mContext ,SkipUtils.ORDER_TYPE_NEED ,mOrderId,""
                    ,mDetails.getNeedTitle(),"" ,mDetails.getOrderAmount(),mIsReceive) ;
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

        String orderState = mDetails.getOrderStatusName() ;
        String orderNo = mDetails.getOrderNO() ;
        String content = mDetails.getWorkDescribe() ;
        String time = mDetails.getCreateDate() ;
        String money = mDetails.getOrderAmount() ;

        mOrderStateTv.setText(orderState);
        mOrderNoTv.setText(orderNo);
        mOrderDescTv.setText(content);
        mOrderTimeTv.setText(time);
        mOrderMoneyTv.setText(money);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mOrderPicRv.setLayoutManager(layoutManager) ;
        mOrderPicRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext ,5))
                ,getResources().getColor(R.color.color_grey_f2));
        AdapterNomalImage imageAdapter = new AdapterNomalImage(mActivity ,mDetails.getThumbs()) ;
        mOrderPicRv.setAdapter(imageAdapter);

        mOrderOpLay.setVisibility(View.GONE);
        mPayTv.setVisibility(View.GONE);
        mCancelTv.setVisibility(View.GONE);
        mFinishTv.setVisibility(View.GONE);
        mAfterSaleTv.setVisibility(View.GONE);

        mIsReceive = LoginHelper.isSelf(mContext ,mDetails.getPayUserId()) ;
        if(mIsReceive){//需要自己来付款
            int orderStatus = mDetails.getOrderStatus() ;
            if(1 == orderStatus){//待付款
                mOrderOpLay.setVisibility(View.VISIBLE);
                mPayTv.setVisibility(View.VISIBLE);
            }else if(2 == orderStatus){//待完成
                mOrderOpLay.setVisibility(View.VISIBLE);
                mCancelTv.setVisibility(View.VISIBLE);
                mFinishTv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取信息
     */
    private void getDetails(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_NEEDS_ORDER_DETAILS, paramMap
                , new RequestObjectCallBack<MyNeedOrderDetails>("getDetails" ,mContext ,MyNeedOrderDetails.class) {
                    @Override
                    public void onSuccessResult(MyNeedOrderDetails bean) {
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
     * 完成order
     * @param orderId orderId
     */
    private void finishNeedOrder(String orderId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_NEEDS_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishNeedOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.NeedOrderRefresh(true)) ;
                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

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
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.NeedOrderRefresh ev){
        if(ev.isRefresh()){
           getDetails() ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY_ORDER == requestCode){
            if(Activity.RESULT_OK == resultCode){
                EventBus.getDefault().post(new BusEvent.NeedOrderRefresh(true)) ;
                EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
