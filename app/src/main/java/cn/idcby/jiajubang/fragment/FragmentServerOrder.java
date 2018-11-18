package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
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
import cn.idcby.jiajubang.Bean.ServerOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterServerOrder;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 服务订单
 * Created on 2018/4/12.
 * 注：相关操作   我预约的服务  付款、完工、评价
 *      提供的服务  开始服务 、修改订单
 *
 *      2018-05-25 11:33:08
 *      我预约的服务 和 提供的服务 之前是用同一个，现在为了区分安装工和服务工，拆成3个接口了
 *      ，跳转的基本结构可以不变，但是接口变了
 *
 *      2018-05-30 15:08:38
 *      完工添加提示
 *
 *      2018-07-03 15:39:00
 *      基本上FragmentServerOrder只展示了自己提供的订单，之前的一大堆太乱了
 *      ，直接删了相关操作，去掉 我预约的 和 我提供的服务 的区分
 *      ，只保留我提供的操作及逻辑，删代码了
 */

public class FragmentServerOrder extends Fragment {
    private Activity context ;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private boolean mIsInstall = false ;
    private int mOrderStatus; // 订单状态(1.未支付2.待服务、3服务中4、待评价、5已完成)

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;
    private TextView mFooterView ;

    private AdapterServerOrder mAdapter ;
    private List<ServerOrderList> mOrderList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private static final int REQUEST_CODE_PAY = 1101 ;
    private static final int REQUEST_CODE_COMMENT = 1102 ;
    private static final int REQUEST_CODE_EDIT = 1103 ;

    private LoadingDialog mDialog ;

    private Dialog mEditOrderDialog ;
    private EditText mOrderMoneyEv ;
    private int mCurPosition ;


    public static FragmentServerOrder getInstance(boolean isInstall ,int orderStatus){
        FragmentServerOrder fs = new FragmentServerOrder() ;
        fs.mIsInstall = isInstall ;
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
            mView = inflater.inflate(R.layout.fragment_my_server_order, container , false) ;

            mRefreshLay = mView.findViewById(R.id.fragment_my_server_order_refresh_lay);
            mLv = mView.findViewById(R.id.fragment_my_server_order_lv);
            mNullTv =  mView.findViewById(R.id.fragment_my_server_order_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_my_server_order_loading_null_pb);

            mFooterView = ViewUtil.getLoadingLvFooterView(context) ;
            mLv.addFooterView(mFooterView);

            mAdapter = new AdapterServerOrder(context, mOrderList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    ServerOrderList orderList = mOrderList.get(position) ;
                    if(orderList != null){
                        final String orderId = orderList.getServiceOrderId() ;

                        if(AdapterServerOrder.TYPE_OP_START == type){//开始服务
                            DialogUtils.showCustomViewDialog(context, "温馨提示" ,"开始服务？", null
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
                        }else if(AdapterServerOrder.TYPE_OP_EDIT == type){//编辑
                            mCurPosition = position ;

                            showOrderEditDialog() ;
                        }
                    }
                }
            }) ;
            mLv.setAdapter(mAdapter) ;

            mLv.removeFooterView(mFooterView) ;

            mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mCurPage = 1 ;
                    mIsMore = true ;

                    getServerOrderList() ;
                }
            });

            mLv.setFocusable(false) ;

            mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                        , int totalItemCount) {

                    if(!mIsLoading && mIsMore
                            && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){

                        if(mLv.getFooterViewsCount() == 0){
                            mLv.addFooterView(mFooterView);
                        }
                        getServerOrderList();
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    showNullTipsOrLoading(false , true) ;
                    getServerOrderList();
                }
            }
        }

        return mView ;
    }

    /**
     * 编辑订单
     */
    private void showOrderEditDialog(){
        if(null == mEditOrderDialog){
            mEditOrderDialog = new Dialog(context ,R.style.my_custom_dialog) ;
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_server_order ,null) ;
            mEditOrderDialog.setContentView(view);

            mOrderMoneyEv = view.findViewById(R.id.dialog_server_order_money_ev) ;
            TextView cancelTv = view.findViewById(R.id.dialog_server_order_edit_cancel_tv) ;
            TextView okTv = view.findViewById(R.id.dialog_server_order_edit_ok_tv) ;

            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String editMoney = mOrderMoneyEv.getText().toString() ;
                    if(StringUtils.convertString2Float(editMoney) <= 0){
                        ToastUtils.showToast(context ,"请输入正确的金额") ;
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

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(context) * 0.8F) ;

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

        String money = mOrderList.get(mCurPosition).getServiceAmount() ;
        mOrderMoneyEv.setText(money);
        mOrderMoneyEv.setSelection(money.length()) ;

        mEditOrderDialog.show() ;
    }

    /**
     * 编辑订单
     * @param money money
     */
    private void editOrder(String money){
        String orderId = mOrderList.get(mCurPosition).getServiceOrderId() ;
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false) ;
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(context) ;
        paramMap.put("ServiceAmount" ,money) ;
        paramMap.put("ServiceOrderId" ,StringUtils.convertNull(orderId)) ;

        NetUtils.getDataFromServerByPost(context, Urls.SERVER_ORDER_EDIT, paramMap
                , new RequestObjectCallBack<String>("editOrder" ,context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(context ,"提交成功");

                        mCurPage = 1 ;
                        mIsMore = true ;
                        getServerOrderList() ;
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
        if(null == mDialog){
            mDialog = new LoadingDialog(context) ;
        }
        mDialog.setCancelable(false) ;
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("ServiceOrderId" , orderId) ;

        NetUtils.getDataFromServerByPost(context, Urls.SERVER_ORDER_START, paramMap
                , new RequestObjectCallBack<String>("startServer" ,context ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(context ,"提交成功");

                        mCurPage = 1 ;
                        mIsMore = true ;
                        getServerOrderList() ;
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

                        mCurPage = 1 ;
                        mIsMore = true ;
                        getServerOrderList() ;
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
    private void getServerOrderList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("ID" , "" + mOrderStatus) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mIsLoading = true ;

        String url ;
        if(mIsInstall){//安装工服务
            url = Urls.SERVER_ORDER_LIST_INSTALL ;
        }else{//服务工服务
            url = Urls.SERVER_ORDER_LIST ;
        }

        NetUtils.getDataFromServerByPost(context, url, paramMap
                , new RequestListCallBack<ServerOrderList>("getServerOrderList" , context , ServerOrderList.class) {
            @Override
            public void onSuccessResult(List<ServerOrderList> bean) {

                if(1 == mCurPage){
                    mOrderList.clear() ;
                }
                mOrderList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if(bean.size() == 0){
                    mIsMore = false ;
                }else{
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
        if(mLv.getFooterViewsCount() > 0){
            mLv.removeFooterView(mFooterView);
        }

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
    public void updateEventBus(BusEvent.ServerOrderRefresh ev){
        if(ev.isRefresh()){//需要刷新
            mCurPage = 1 ;
            mOrderList.clear();
            mAdapter.notifyDataSetChanged();

            if(getUserVisibleHint()){
                showNullTipsOrLoading(false , true) ;
                getServerOrderList();
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
                    getServerOrderList();
                }
            }else{
                NetUtils.cancelTag("getServerOrderList") ;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_PAY == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;
                mIsMore = true ;

                showNullTipsOrLoading(false , true) ;
                getServerOrderList() ;
            }
        }else if(REQUEST_CODE_COMMENT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;
                mIsMore = true ;

                showNullTipsOrLoading(false , true) ;
                getServerOrderList() ;
            }
        }else if(REQUEST_CODE_EDIT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;
                mIsMore = true ;

                showNullTipsOrLoading(false , true) ;
                getServerOrderList() ;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getServerOrderList") ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }

        EventBus.getDefault().unregister(this);
    }
}
