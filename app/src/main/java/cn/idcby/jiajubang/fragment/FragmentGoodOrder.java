package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
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
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodOrderSendActivity;
import cn.idcby.jiajubang.activity.MyGoodOrderDetailsActivity;
import cn.idcby.jiajubang.adapter.AdapterMyGoodOrder;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 商品订单
 * Created on 2018/4/24.
 *
 * 购买商品：
 * 全部 、待付款（支付）、待发货 (取消)、待收货（确认收货）
 * 、 待评价（评价）
 *
 * 提供商品：全部、 待付款（闲置和厂家直供都允许修改订单）
 * 、 待发货（发货 弹出一个层 上面填写物流公司 单号 发货方联系人 联系电话 ） 待收货、 待评价
 *
 * 注意：编辑订单功能 待付款
 * 删除订单功能：待评价,已完成,已取消
 *
 * 2018-05-30 15:09:49
 * 添加 确认收货 提示
 *
 * 2018-07-03 17:17:54
 * 我购买的商品订单合并到别的界面了，当前界面只是我提供的订单
 */

public class FragmentGoodOrder extends Fragment {
    private Activity context ;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv;

    private int mOrderType = 1; //1.直供商品 2.闲置商品
    public static final int GOOD_ORDER_TYPE_GOOD = 1 ;
    public static final int GOOD_ORDER_TYPE_UNUSE = 2 ;

    private int mOrderStatus; // 订单状态(0.全部 1.待付款 2.待发货 3.待收货 4.待评价)

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterMyGoodOrder mAdapter ;
    private List<GoodOrderList> mOrderList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private boolean mIsRefresh = false ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_PAY_ORDER = 1001 ;

    //编辑订单相关
    private Dialog mOrderEditDialog ;
    private EditText mOrderDescEv ;
    private EditText mOrderMoneyEv ;
    private EditText mOrderDriverEv ;
    private String mCurEditOrderId ;
    private int mCurPosition ;

    public static FragmentGoodOrder getInstance(int orderStatus , int orderType){
        FragmentGoodOrder fs = new FragmentGoodOrder() ;
        fs.mOrderType = orderType ;
        fs.mOrderStatus = orderStatus ;
        return fs ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this) ;

        this.context = getActivity() ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_my_good_order, container , false) ;

            mRefreshLay = mView.findViewById(R.id.fragment_my_good_order_refresh_lay);
            mRv = mView.findViewById(R.id.fragment_my_good_order_lv);
            mNullTv =  mView.findViewById(R.id.fragment_my_good_order_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_my_good_order_loading_null_pb);

            mAdapter = new AdapterMyGoodOrder(context,mOrderType, mOrderList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    final GoodOrderList orderList = mOrderList.get(position) ;
                    if(orderList != null){
                        final String orderId = orderList.getOrderId() ;

                        switch (type){
                            case 1: //item
                                Intent toGoIt = new Intent(context , MyGoodOrderDetailsActivity.class) ;
                                toGoIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                                startActivity(toGoIt);
                                break;
                            case 2://发货

                                if(orderList.isNotAllGoodAfterSale()){
                                    GoodOrderSendActivity.launch(context ,orderId
                                            ,orderList.getDeliveryName() ,orderList.getDeliveryMobile()
                                            ,orderList.getReciever() ,orderList.getRecieverPhone()) ;
                                }else{
                                    DialogUtils.showCustomViewDialog(context
                                            , context.getResources().getString(R.string.order_no_finish_tips)
                                            ,"确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                }

                                break;
                            case 3://编辑
                                mCurPosition = position ;
                                showEditDialog() ;
                                break;
                            case 4://删除
                                DialogUtils.showCustomViewDialog(context, "删除订单", "删除该订单？"
                                        , null, "确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                                deleteOrder(orderId) ;
                                            }
                                        }, "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                break;
                            default:
                                break;
                        }
                    }
                }
            }) ;
            mRv.setLayoutManager(new LinearLayoutManager(context)) ;
            mRv.setAdapter(mAdapter) ;

            mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mCurPage = 1 ;
                    mIsMore = true ;
                    mIsRefresh = true ;

                    getGoodOrderList() ;
                }
            });

            mRv.setFocusable(false) ;
            mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(mIsMore && !mIsLoading && ViewUtil.isSlideToBottom(mRv)){
                        getGoodOrderList() ;
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    getGoodOrderList();
                }
            }
        }

        return mView ;
    }

    /**
     * 编辑订单
     */
    private void showEditDialog(){
        if(null == mOrderEditDialog){
            mOrderEditDialog = new Dialog(context ,R.style.my_custom_dialog) ;
            mOrderEditDialog.setCancelable(false);
            View v = LayoutInflater.from(context).inflate(R.layout.dialog_good_order_edit ,null) ;
            mOrderEditDialog.setContentView(v);

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(context) * 0.8F) ;
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

        GoodOrderList orderList = mOrderList.get(mCurPosition) ;
        mCurEditOrderId = orderList.getOrderId() ;
        String money = orderList.getFinalProductAmount() ;
        String driverMoney = orderList.getFinalExpressFee() ;

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
            ToastUtils.showToast(context ,"请输入修改描述") ;
            mOrderDescEv.requestFocus() ;
            mOrderDescEv.setText("") ;
            return ;
        }

        String money = mOrderMoneyEv.getText().toString() ;
        if(!MyUtils.isRightMoney(money)){
            ToastUtils.showToast(context ,"请输入正确的商品金额") ;
            mOrderMoneyEv.requestFocus() ;
            mOrderMoneyEv.setSelection(money.length());
            return ;
        }

        String driver = mOrderDriverEv.getText().toString() ;
        if(!MyUtils.isRightMoney(driver)){
            ToastUtils.showToast(context ,"请输入正确的运费") ;
            mOrderDriverEv.requestFocus() ;
            mOrderDriverEv.setSelection(driver.length());
            return ;
        }
        mOrderEditDialog.dismiss() ;

        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(context) ;
        paramMap.put("Description" ,desc) ;
        paramMap.put("OrderAmount" ,money) ;
        paramMap.put("ExpressFee" ,driver) ;
        paramMap.put("OrderId" , StringUtils.convertNull(mCurEditOrderId)) ;

        NetUtils.getDataFromServerByPost(context, Urls.GOOD_ORDER_EDIT, paramMap
                , new RequestObjectCallBack<String>("editOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getGoodOrderList() ;
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
                        getGoodOrderList() ;
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
    private void getGoodOrderList(){
        if(1 == mCurPage && !mIsRefresh){
            showNullTipsOrLoading(false , true) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("Status" , "" + mOrderStatus) ;
        paramMap.put("Type" , "2") ;
        paramMap.put("OrderType" , "" + mOrderType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(context, Urls.GOOD_ORDER_LIST, paramMap
                , new RequestListCallBack<GoodOrderList>("getGoodOrderList" , context , GoodOrderList.class) {
            @Override
            public void onSuccessResult(List<GoodOrderList> bean) {

                if(1 == mCurPage){
                    mOrderList.clear() ;
                }
                mOrderList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if(bean.size() == 0){
                    mIsMore = false ;
                }else{
                    mIsMore = true ;
                    mCurPage ++ ;
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
        mIsRefresh = false ;
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
    public void updateEventBus(BusEvent.GoodOrderRefresh ev){
        if(ev.isRefresh()){//需要刷新
            mCurPage = 1 ;
            mOrderList.clear();
            mAdapter.notifyDataSetChanged();

            if(getUserVisibleHint()){
                getGoodOrderList();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mOrderList != null &&  mOrderList.size() == 0){
                    getGoodOrderList();
                }
            }else{
                NetUtils.cancelTag("getGoodOrderList") ;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY_ORDER == requestCode){
            if(Activity.RESULT_OK == resultCode){
                EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){
            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }

        EventBus.getDefault().unregister(this);
    }
}
