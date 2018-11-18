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
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.MyAfterSaleOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyOrderAfterSaleDetailsActivity;
import cn.idcby.jiajubang.activity.MyOrderAfterSaleEditReceiveAddressActivity;
import cn.idcby.jiajubang.adapter.AdapterMyAfterSaleOrder;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemMoreViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的售后订单
 * Created on 2018/6/7.
 *  1 我购买的 2 我提供的
 */

public class FragmentMyOrderAfterSale extends Fragment {
    private Activity mContext;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv;

    private int mOrderItemType = 0 ;// 1厂家直供 2闲置 3行业服务 4安装服务 5需求
    private int mOrderFrom = 1 ; // 1 我购买的 2 我提供的
    public static final int ORDER_AFTER_SALE_FROM_RECEIVE = 1 ;
    public static final int ORDER_AFTER_SALE_FROM_MY = 2 ;

    public static final int ORDER_ITEM_TYPE_GOOD = 1 ;
    public static final int ORDER_ITEM_TYPE_UNUSE = 2 ;
    public static final int ORDER_ITEM_TYPE_SERVER = 3 ;
    public static final int ORDER_ITEM_TYPE_INSTALL = 4 ;
    public static final int ORDER_ITEM_TYPE_NEED = 5 ;

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterMyAfterSaleOrder mAdapter ;
    private List<MyAfterSaleOrderList> mOrderList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private static final int REQUEST_CODE_PAY_SERVER = 1101 ;
    private static final int REQUEST_CODE_PAY_GOOD = 1102 ;
    private static final int REQUEST_CODE_PAY_NEED = 1103 ;

    private LoadingDialog loadingDialog ;

    private Dialog mEditExpressDialog ;//填写物流信息
    private EditText mEditExpressNameEv ;
    private EditText mEditExpressNumEv ;
    private String mCurAfterSaleId ;


    public static FragmentMyOrderAfterSale getInstance(int orderFrom){
        FragmentMyOrderAfterSale fs = new FragmentMyOrderAfterSale() ;
        fs.mOrderFrom = orderFrom ;
        return fs ;
    }

    public static FragmentMyOrderAfterSale getInstance(int orderFrom,int orderItemType){
        FragmentMyOrderAfterSale fs = new FragmentMyOrderAfterSale() ;
        fs.mOrderFrom = orderFrom ;
        fs.mOrderItemType = orderItemType ;
        return fs ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this) ;

        this.mContext = getActivity() ;

        loadingDialog = new LoadingDialog(context) ;
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

            mAdapter = new AdapterMyAfterSaleOrder(mContext,mOrderFrom,mOrderList, new RvItemMoreViewClickListener() {
                @Override
                public void onItemClickListener(int type, int... position) {
                    MyAfterSaleOrderList orderList = mOrderList.get(position[0]) ;
                    if(orderList != null){
                        final String afterSaleId = orderList.getOrderAfterSaleId() ;

                        if(AdapterMyAfterSaleOrder.TYPE_OP_ITEM == type){//暂定点击item表示查看详细

//                            SkipUtils.toOrderAfterSaleActivity(mContext,false,afterSaleId,mOrderFrom == ORDER_AFTER_SALE_FROM_RECEIVE);
                            MyOrderAfterSaleDetailsActivity.launch(mContext ,afterSaleId) ;

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_EDIT == type){//编辑

                            SkipUtils.toOrderAfterSaleActivity(mContext,true ,afterSaleId,mOrderFrom == ORDER_AFTER_SALE_FROM_RECEIVE);

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_CANCEL == type){//取消申请

                            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "取消该申请？", null
                                    , "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            cancelAfterSale(afterSaleId) ;
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_EXPRESS == type){//填写物流

                            mCurAfterSaleId = afterSaleId ;
                            showEditExpressDialog() ;

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_AGREE == type){//同意申请

                            if((orderList.isGood() || orderList.isUnuse())
                                    && !orderList.isReturenMoneyOnly()){//选了退货退款才让填信息
                                MyOrderAfterSaleEditReceiveAddressActivity.launch(mContext ,afterSaleId);
                            }else{
                                DialogUtils.showCustomViewDialog(mContext, "温馨提示", "同意该申请？", null
                                        , "确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                                agreeServerAfterSale(afterSaleId) ;
                                            }
                                        }, "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_DISAGREE == type){//不同意申请

                            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "不同意该申请？", null
                                    , "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            disagreeAfterSale(afterSaleId) ;
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_FINISH == type){//确认收货

                            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "确认收货？", null
                                    , "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            finishAfterSale(afterSaleId) ;
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                        }else if(AdapterMyAfterSaleOrder.TYPE_OP_STORE == type){//点击title

                            if(orderList.isGood()){//商品跳转到店铺主页

                            }else{//服务跳转到个人主页

                            }

                        }
                    }
                }
            }) ;
            mRv.setLayoutManager(new LinearLayoutManager(mContext));
            mRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext
                    ,ResourceUtils.getXmlDef(mContext,R.dimen.nomal_divider_height))
                    , mContext.getResources().getDrawable(R.drawable.drawable_white_trans))) ;
            mRv.setAdapter(mAdapter) ;

            mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mCurPage = 1 ;
                    mIsMore = true ;

                    getAfterSaleOrderList() ;
                }
            });

            mRv.setFocusable(false) ;

            mRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(!mIsLoading && mIsMore && mOrderList.size() > 5 && ViewUtil.isSlideToBottom(mRv)){
                        getAfterSaleOrderList() ;
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    showNullTipsOrLoading(false , true) ;
                    getAfterSaleOrderList();
                }
            }
        }

        return mView ;
    }


    /**
     * 填写物流信息dialog
     */
    private void showEditExpressDialog(){
        if(null == mEditExpressDialog){
            mEditExpressDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;

            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_receive_order_express ,null) ;
            mEditExpressDialog.setContentView(view);

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F);

            mEditExpressNameEv = view.findViewById(R.id.dialog_receive_order_express_name_ev) ;
            mEditExpressNumEv = view.findViewById(R.id.dialog_receive_order_express_num_ev) ;
            View okTv = view.findViewById(R.id.dialog_receive_order_express_edit_ok_tv) ;
            View cancelTv = view.findViewById(R.id.dialog_receive_order_express_edit_cancel_tv) ;
            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mEditExpressNameEv.getText().toString().trim() ;
                    if("".equals(name)){
                        ToastUtils.showToast(mContext ,"请输入快递或物流公司名称");
                        return ;
                    }
                    String num = mEditExpressNumEv.getText().toString().trim() ;
                    if("".equals(num)){
                        ToastUtils.showToast(mContext ,"请输入快递或物流单号");
                        return ;
                    }

                    mEditExpressDialog.dismiss();

                    addExpressInfo(name,num,mCurAfterSaleId) ;
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditExpressDialog.dismiss() ;
                }
            });
        }

        mEditExpressNumEv.setText("") ;
        mEditExpressNameEv.setText("") ;

        mEditExpressDialog.show() ;
    }

    /**
     * 同意申请--针对服务、需求
     */
    private void agreeServerAfterSale(String mAfterSaleId){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(mAfterSaleId)) ;
        paramMap.put("ProvinceID" ,"") ;
        paramMap.put("CityID" ,"") ;
        paramMap.put("Address" ,"") ;
        paramMap.put("Reciever" ,"") ;
        paramMap.put("RecieverPhone" ,"") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_AGREE, paramMap
                , new RequestObjectCallBack<String>("agreeAfterSale",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "提交成功"
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 不同意售后申请--确认收货
     */
    private void disagreeAfterSale(String mAfterSaleId){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_DISAGREE, paramMap
                , new RequestObjectCallBack<String>("disagreeAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;


                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 添加物流信息
     */
    private void addExpressInfo(String name ,String num ,String mAfterSaleId){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(mAfterSaleId)) ;
        paramMap.put("ReturnExpressName" ,StringUtils.convertNull(name)) ;
        paramMap.put("ReturnExpressNO" ,StringUtils.convertNull(num)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_SEND_EXPRESS, paramMap
                , new RequestObjectCallBack<String>("addExpressInfo" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 完成售后申请--确认收货
     */
    private void finishAfterSale(String mAfterSaleId){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 取消售后申请
     */
    private void cancelAfterSale(String mAfterSaleId){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" , StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getAfterSaleOrderList(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" , "" + mOrderFrom) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Keyword" , mOrderItemType == 0 ? "" : ("" + mOrderItemType)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_LIST, paramMap
                , new RequestListCallBack<MyAfterSaleOrderList>("getAfterSaleOrderList" + mOrderFrom, mContext, MyAfterSaleOrderList.class) {
            @Override
            public void onSuccessResult(List<MyAfterSaleOrderList> bean) {

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
            mCurPage = 1 ;
            mOrderList.clear();
            mAdapter.notifyDataSetChanged();

            if(getUserVisibleHint()){
                showNullTipsOrLoading(false , true) ;
                getAfterSaleOrderList();
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
                    getAfterSaleOrderList();
                }
            }else{
                NetUtils.cancelTag("getAfterSaleOrderList" + mOrderFrom) ;
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
                getAfterSaleOrderList() ;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getAfterSaleOrderList" + mOrderFrom) ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }

        EventBus.getDefault().unregister(this);
    }
}
