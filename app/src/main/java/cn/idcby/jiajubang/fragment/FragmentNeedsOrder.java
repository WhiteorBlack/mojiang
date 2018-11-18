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
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.MyNeedsOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyNeedOrderDetailsActivity;
import cn.idcby.jiajubang.activity.MyNeedsOrderCancelActivity;
import cn.idcby.jiajubang.adapter.AdapterMyNeedsOrder;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 需求订单
 * Created on 2018/4/16.
 *
 * 我发起的需求，只看需求信息，不用操作
 * 我的需求操作有：付款、完成、取消
 *
 * 2018-05-30 15:11:22
 * 添加  完成  提醒
 */

public class FragmentNeedsOrder extends Fragment {
    private Activity context ;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv;

    private int mOrderType; // 1.我发起的需求  2.我的需求
    private int mOrderStatus; // 订单状态(1.待付款 2.待完成 3.已取消 4.已完成)

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterMyNeedsOrder mAdapter ;
    private List<MyNeedsOrderList> mOrderList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private boolean mIsRefresh = false ;

    public static final int NEED_ORDER_TYPE_SEND = 1 ;
    public static final int NEED_ORDER_TYPE_MY = 2 ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_PAY_ORDER = 1001 ;


    public static FragmentNeedsOrder getInstance(int orderStatus , int orderType){
        FragmentNeedsOrder fs = new FragmentNeedsOrder() ;
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
            mView = inflater.inflate(R.layout.fragment_my_needs_order, container , false) ;

            mRefreshLay = mView.findViewById(R.id.fragment_my_needs_order_refresh_lay);
            mRv = mView.findViewById(R.id.fragment_my_needs_order_lv);
            mNullTv =  mView.findViewById(R.id.fragment_my_needs_order_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_my_needs_order_loading_null_pb);

            mAdapter = new AdapterMyNeedsOrder(context, mOrderType, mOrderList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                   final MyNeedsOrderList orderList = mOrderList.get(position) ;
                   if(orderList != null){
                       switch (type){
                           case 1:

                               Intent toDtIt = new Intent(context , MyNeedOrderDetailsActivity.class) ;
                               toDtIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderList.getOrderID()) ;
                               startActivity(toDtIt) ;

                               break;
                           case 2://付款

                               SkipUtils.toPayBondActivity(FragmentNeedsOrder.this ,REQUEST_CODE_PAY_ORDER ,orderList.getOrderAmount()
                                       ,orderList.getOrderID(),orderList.getOrderNO() ,SkipUtils.PAY_ORDER_TYPE_ORDER_NEED );

                               break;
                           case 3://取消
                               Intent toCtIt = new Intent(context , MyNeedsOrderCancelActivity.class) ;
                               toCtIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderList.getOrderID()) ;
                               startActivity(toCtIt);
                               break;
                           case 4://完成
                               DialogUtils.showCustomViewDialog(context, "完成订单"
                                       , context.getResources().getString(R.string.need_finish_tips)
                                       , null, "确定", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               dialogInterface.dismiss();

                                               finishNeedOrder(orderList.getOrderID()) ;
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

                    getNeedsOrderList() ;
                }
            });

            mRv.setFocusable(false) ;
            mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(mIsMore && !mIsLoading && ViewUtil.isSlideToBottom(mRv)){
                        getNeedsOrderList() ;
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    getNeedsOrderList();
                }
            }
        }

        return mView ;
    }

    /**
     * 取消order
     * @param orderId orderId
     */
    private void cancelNeedOrder(String orderId,String desc ,String img1,String img2,String img3){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("NeedOrderId" , orderId) ;
        paramMap.put("WorkDescribe" , desc) ;
        paramMap.put("WorkImage1" , img1) ;
        paramMap.put("WorkImage2" , img2) ;
        paramMap.put("WorkImage3" , img3) ;

        NetUtils.getDataFromServerByPost(context, Urls.MY_NEEDS_ORDER_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelNeedOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getNeedsOrderList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(context ,"订单取消失败");
                    }
                });
    }

    /**
     * 完成order
     * @param orderId orderId
     */
    private void finishNeedOrder(String orderId){
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.MY_NEEDS_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishNeedOrder",context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getNeedsOrderList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(context ,"完成订单失败");
                    }
                });
    }

    /**
     * 获取需求列表
     */
    private void getNeedsOrderList(){
        if(1 == mCurPage && !mIsRefresh){
            showNullTipsOrLoading(false , true) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("OrderStatus" , "" + mOrderStatus) ;
        paramMap.put("TypeId" , "" + mOrderType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(context, Urls.MY_NEEDS_ORDER_LIST, paramMap
                , new RequestListCallBack<MyNeedsOrderList>("getNeedsOrderList" , context , MyNeedsOrderList.class) {
            @Override
            public void onSuccessResult(List<MyNeedsOrderList> bean) {

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
    public void updateEventBus(BusEvent.NeedOrderRefresh ev){
        if(ev.isRefresh()){
            mCurPage = 1 ;
            mOrderList.clear();
            mAdapter.notifyDataSetChanged();

            if(getUserVisibleHint()){
                getNeedsOrderList();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mOrderList != null &&  mOrderList.size() == 0){
                    getNeedsOrderList();
                }
            }else{
                NetUtils.cancelTag("getNeedsOrderList" + mOrderType) ;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY_ORDER == requestCode){
            if(Activity.RESULT_OK == resultCode){
                EventBus.getDefault().post(new BusEvent.NeedOrderRefresh(true)) ;
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
