package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.Bean.MyReceiveOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodOrderCommentSendActivity;
import cn.idcby.jiajubang.activity.MyGoodOrderDetailsActivity;
import cn.idcby.jiajubang.activity.MyServerOrderCommentActivity;
import cn.idcby.jiajubang.activity.MyServerOrderDetailsActivity;
import cn.idcby.jiajubang.activity.ServerConfirmActivity;
import cn.idcby.jiajubang.adapter.AdapterMyReceiveOrder;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemMoreViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的订单（购买的商品、预约的服务、需求3种）
 * Created on 2018/6/7.
 *
 * 订单状态：
 *      待付款
 *      进行中
 *      待评价
 *      已完成
 *      售后
 *
 *      2018-07-03 14:48:43 改：
 *      订单状态：
 *      待付款
 *      待发货
 *      待收货
 *      待评价
 *      已完成
 *      售后
 */

public class FragmentMyReceiveOrder extends Fragment {
    private Activity context ;
    private Fragment mFragment ;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv;

    private int mOrderStatus; // 订单状态(全部、待付款、进行中、待评价、已完成、售后)

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterMyReceiveOrder mAdapter ;
    private List<MyReceiveOrderList> mOrderList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private static final int REQUEST_CODE_PAY_SERVER = 1101 ;
    private static final int REQUEST_CODE_PAY_GOOD = 1102 ;
    private static final int REQUEST_CODE_PAY_NEED = 1103 ;
    private static final int REQUEST_CODE_COMMENT_RETURN = 1104 ;

    private LoadingDialog mDialog ;

    //商品售后相关
    private int mCurPosition ;
    private int mGoodPosition ;


    public static FragmentMyReceiveOrder getInstance(int orderStatus){
        FragmentMyReceiveOrder fs = new FragmentMyReceiveOrder() ;
        fs.mOrderStatus = orderStatus ;
        return fs ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this) ;

        this.context = getActivity() ;
        this.mFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_my_receive_order, container , false) ;

            mRefreshLay = mView.findViewById(R.id.fragment_my_receive_order_refresh_lay);
            mRv = mView.findViewById(R.id.fragment_my_receive_order_rv);
            mNullTv =  mView.findViewById(R.id.fragment_my_receive_order_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_my_receive_order_loading_null_pb);

            mAdapter = new AdapterMyReceiveOrder(context,mOrderList, new RvItemMoreViewClickListener() {
                @Override
                public void onItemClickListener(int type, int... position) {
                    final MyReceiveOrderList orderList = mOrderList.get(position[0]) ;
                    if(orderList != null){
                        final String orderId = orderList.getOrderId() ;

                        if(AdapterMyReceiveOrder.TYPE_OP_ITEM == type){

                            if(orderList.isGood() || orderList.isUnuse()){
                                Intent toOdIt = new Intent() ;
                                toOdIt.setClass(context , MyGoodOrderDetailsActivity.class) ;
                                toOdIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                                startActivity(toOdIt) ;
                            }else if(orderList.isServer()){
                                Intent toOdIt = new Intent() ;
                                toOdIt.setClass(context , MyServerOrderDetailsActivity.class) ;
                                toOdIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                                startActivity(toOdIt) ;
                            }
                        }else if(AdapterMyReceiveOrder.TYPE_OP_EDIT_SERVER == type){//服务--编辑

                            ServerConfirmActivity.launch(context ,orderId) ;

                        }else if(AdapterMyReceiveOrder.TYPE_OP_PAY_SERVER == type){//服务--付款

                            String money = orderList.getOrderAmount() ;
                            String orderCode = orderList.getOrderNO() ;

                            //跳转到付款
                            SkipUtils.toPayBondActivity(FragmentMyReceiveOrder.this , REQUEST_CODE_PAY_SERVER
                                    ,money , orderId ,orderCode ,SkipUtils.PAY_ORDER_TYPE_ORDER_SERVICE);

                        }else if(AdapterMyReceiveOrder.TYPE_OP_FINISH_SERVER == type){//服务--完成

                            DialogUtils.showCustomViewDialog(context, "温馨提示"
                                    ,context.getResources().getString(R.string.server_finish_tips), null
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

                        }else if(AdapterMyReceiveOrder.TYPE_OP_COMMENT_SERVER == type){//服务--去评价

                            Intent toCmIt = new Intent(context ,MyServerOrderCommentActivity.class) ;
                            toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                            startActivity(toCmIt) ;

                        }else if(AdapterMyReceiveOrder.TYPE_OP_CANCEL_SERVER == type){//服务--取消

                            DialogUtils.showCustomViewDialog(context, "取消订单", "取消该服务订单？"
                                    , null, "确定", new DialogInterface.OnClickListener() {
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

                        }else if(AdapterMyReceiveOrder.TYPE_OP_PAY_GOOD == type){//付款--商品

                            SkipUtils.toPayBondActivity(FragmentMyReceiveOrder.this , REQUEST_CODE_PAY_GOOD
                                    ,orderList.getOrderAmount()
                                    ,orderList.getOrderId(),orderList.getOrderNO()
                                    ,SkipUtils.PAY_ORDER_TYPE_MALL );

                        }else if(AdapterMyReceiveOrder.TYPE_OP_FINISH_GOOD == type){//确认收货--商品

                            if(!orderList.isGoodNoAfterSale()){//有未处理的售后订单，不允许确认收货
                                DialogUtils.showCustomViewDialog(context
                                        , context.getResources().getString(R.string.order_no_finish_tips)
                                        ,"确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }else{
                                DialogUtils.showCustomViewDialog(context, "确认收货"
                                        , context.getResources().getString(R.string.order_finish_tips)
                                        , null, "确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                                finishOrder(orderId) ;
                                            }
                                        }, "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }
                        }else if(AdapterMyReceiveOrder.TYPE_OP_CANCEL_GOOD == type){//取消--商品

                            DialogUtils.showCustomViewDialog(context, "取消订单", "取消该订单？"
                                    , null, "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            cancelOrder(orderId) ;
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                        }else if(AdapterMyReceiveOrder.TYPE_OP_COMMENT_GOOD == type){//评价--商品

                            GoodOrderCommentSendActivity.launch(mFragment,orderId ,orderList.getOrderItem()
                                    ,REQUEST_CODE_COMMENT_RETURN);

                        }else if(AdapterMyReceiveOrder.TYPE_OP_DELETE == type){//删除订单
                            DialogUtils.showCustomViewDialog(context, "删除订单", "删除该订单？"
                                    , null, "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            if(orderList.isGood() || orderList.isUnuse()){
                                                deleteOrder(orderId) ;
                                            }else{
                                                deleteServerOrder(orderId) ;
                                            }
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                        }else if(AdapterMyReceiveOrder.TYPE_OP_AFTER_SALE_OTHER == type){//售后--服务
                            SkipUtils.toOrderAfterSaleActivity(context,orderList.getOrderType() ,orderId
                                    ,"" ,orderList.getName()
                                    ,orderList.getImageUrl() ,orderList.getOrderAmount(),true);
                        }else if(AdapterMyReceiveOrder.TYPE_OP_AFTER_SALE_GOOD == type){//售后--商品
                            mCurPosition = position[0] ;
                            mGoodPosition = position[1] ;
                            GoodOrderGood orderGood = orderList.getOrderItem().get(mGoodPosition) ;
                            SkipUtils.toOrderAfterSaleActivity(context ,orderList.getOrderType(),orderId
                                    ,orderGood.getOrderItemID()
                                    ,orderGood.getProductTitle() ,orderGood.getImgUrl(),orderGood.getTotalPrice(),true);
                        }
                    }
                }
            }) ;
            mRv.setLayoutManager(new LinearLayoutManager(context));
            mRv.addItemDecoration(new RvLinearManagerItemDecoration(context
                    , ResourceUtils.dip2px(context
                    ,ResourceUtils.getXmlDef(context,R.dimen.nomal_divider_height))
                    ,context.getResources().getDrawable(R.drawable.drawable_white_trans))) ;
            mRv.setAdapter(mAdapter) ;

            mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mCurPage = 1 ;
                    mIsMore = true ;

                    EventBus.getDefault().post(new BusEvent.OrderCountRefresh(true)) ;

                    getReceiveOrderList() ;
                }
            });

            mRv.setFocusable(false) ;

            mRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(!mIsLoading && mIsMore && mOrderList.size() > 5 && ViewUtil.isSlideToBottom(mRv)){
                        getReceiveOrderList() ;
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    showNullTipsOrLoading(false , true) ;
                    getReceiveOrderList();
                }
            }
        }

        return mView ;
    }

    /**
     * 完成服务
     */
    private void finishServer(final String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false) ;
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("ServiceOrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.SERVER_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishServer" ,context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(context ,"提交成功");

                        //2018-07-06 15:01:48 完成之后，跳转到评价界面
                        Intent toCmIt = new Intent(context ,MyServerOrderCommentActivity.class) ;
                        toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                        startActivityForResult(toCmIt,REQUEST_CODE_COMMENT_RETURN) ;

//                        mCurPage = 1 ;
//                        mIsMore = true ;
//                        getReceiveOrderList() ;
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
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("OrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.GOOD_ORDER_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getReceiveOrderList() ;
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
     * 取消服务
     * @param orderId orderId
     */
    private void cancelServerOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.SERVER_ORDER_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelServerOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getReceiveOrderList() ;
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
     * 删除服务
     * @param orderId orderId
     */
    private void deleteServerOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.SERVER_ORDER_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteServerOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getReceiveOrderList() ;
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
     * 删除order
     * @param orderId orderId
     */
    private void deleteOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("OrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.GOOD_ORDER_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getReceiveOrderList() ;
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
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("OrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.GOOD_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getReceiveOrderList() ;
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
     * 获取列表
     */
    private void getReceiveOrderList(){
        mIsLoading = true ;
        Map<String,String> paramMap = ParaUtils.getParaWithToken(context) ;
        paramMap.put("ID" , "" + mOrderStatus) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        NetUtils.getDataFromServerByPost(context, Urls.MY_RECEIVE_ORDER_LIST, paramMap
                , new RequestListCallBack<MyReceiveOrderList>("getReceiveOrderList" + mOrderStatus , context , MyReceiveOrderList.class) {
            @Override
            public void onSuccessResult(List<MyReceiveOrderList> bean) {

                if(1 == mCurPage){
                    mOrderList.clear() ;
                }
                mOrderList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if(bean.size() == 0){
                    mIsMore = false ;
                }else{
                    mCurPage ++ ;
                    mIsMore = true ;
                }

                finishRequest() ;
            }
            @Override
            public void onErrorResult(String str) {
                finishRequest() ;
            }
            @Override
            public void onFail(Exception e) {
                finishRequest() ;
            }
        });
    }

    /**
     * 完成请求
     */
    private void finishRequest(){
        mIsLoading = false ;
        mAdapter.notifyDataSetChanged();
        mRefreshLay.finishRefresh() ;
        showNullTipsOrLoading(mOrderList.size() == 0 , false) ;
    }

    /**
     * 显示或隐藏null提示和loading提示
     */
    private void showNullTipsOrLoading(boolean isNull , boolean isLoading){
        if(isNull){
            if(mNullTv.getVisibility() != View.VISIBLE){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else{
            if(mNullTv.getVisibility() != View.GONE){
                mNullTv.setVisibility(View.GONE);
            }
        }

        if(isLoading){
            if(mLoadingPb.getVisibility() != View.VISIBLE){
                mLoadingPb.setVisibility(View.VISIBLE);
            }
        }else{
            if(mLoadingPb.getVisibility() != View.GONE){
                mLoadingPb.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.ReceiveOrderRefresh ev){
        if(ev.isRefresh()){//需要刷新
            EventBus.getDefault().post(new BusEvent.OrderCountRefresh(true)) ;

            mCurPage = 1 ;
            mOrderList.clear();
            mAdapter.notifyDataSetChanged();

            if(getUserVisibleHint()){
                showNullTipsOrLoading(false , true) ;
                getReceiveOrderList();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mOrderList != null &&  mOrderList.size() == 0){
                    showNullTipsOrLoading(false , true) ;
                    getReceiveOrderList();
                }
            }else{
                NetUtils.cancelTag("getReceiveOrderList" + mOrderStatus) ;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY_SERVER == requestCode
                || REQUEST_CODE_PAY_GOOD == requestCode
                || REQUEST_CODE_PAY_NEED == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;

                showNullTipsOrLoading(false , true) ;
                getReceiveOrderList() ;
            }
        }else if(REQUEST_CODE_COMMENT_RETURN == requestCode){
            mCurPage = 1 ;
            mIsMore = true ;
            getReceiveOrderList() ;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getReceiveOrderList" + mOrderStatus) ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }

        EventBus.getDefault().unregister(this);
    }
}
