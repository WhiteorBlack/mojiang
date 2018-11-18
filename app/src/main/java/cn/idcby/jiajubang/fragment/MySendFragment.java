package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.JobsCollectionList;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ActivityBondPay;
import cn.idcby.jiajubang.activity.CreateZhaopinActivity;
import cn.idcby.jiajubang.activity.NeedsSendActivity;
import cn.idcby.jiajubang.activity.UnusedSendActivity;
import cn.idcby.jiajubang.adapter.AdapterMySendJobs;
import cn.idcby.jiajubang.adapter.AdapterMySendNeedsList;
import cn.idcby.jiajubang.adapter.AdapterMySendUnuse;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的发布--需求、招聘、闲置
 * 2018-04-16
 *
 * 注：由于接口是一个（数据返回不一致），计划用同一个fragment显示不同adapter
 *
 */

public class MySendFragment extends BaseFragment {
    private ListView mListView;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    //需求
    private List<NeedsList> mNeedsList = new ArrayList<>() ;
    private AdapterMySendNeedsList mNeedsAdapter ;

    //招聘
    private List<JobsCollectionList> mJobList = new ArrayList<>() ;
    private AdapterMySendJobs mJobAdapter ;

    //闲置
    private List<UnuseGoodList> mUnuseList = new ArrayList<>() ;
    private AdapterMySendUnuse mUnuseAdapter ;


    //1.服务2.安装3.需求5.招聘6.闲置
    private int mSendType;

    public static final int SEND_TYPE_NEEDS = 3 ;
    public static final int SEND_TYPE_JOBS = 5 ;
    public static final int SEND_TYPE_UNUSE = 6 ;
    public static final int SEND_TYPE_CIRCLE = 7 ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_NEED = 1000 ;
    private static final int REQUEST_CODE_JOB = 1002 ;
    private static final int REQUEST_CODE_UNUSE = 1003 ;

    //招标缴纳保证金
    private int mCurPosition ;
    private Dialog mPayBondDialog ;
    private EditText mPayBondEv ;


    public static MySendFragment getInstance(int type) {
       MySendFragment fragment = new MySendFragment() ;
       fragment.mSendType = type ;
       return fragment ;
    }

    @Override
    protected void requestData() {
        loadPage.showSuccessPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getDataList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {

        EventBus.getDefault().register(this) ;

        mDialog = new LoadingDialog(mContext) ;
        mDialog.setCancelable(false);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mListView = view.findViewById(R.id.lay_refresh_lv_list_lv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_lv_refresh_lay) ;
        mNullTv = view.findViewById(R.id.lay_refresh_lv_null_tv) ;

        if(SEND_TYPE_NEEDS == mSendType){
            mNeedsAdapter = new AdapterMySendNeedsList(mActivity, mNeedsList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    NeedsList needsList = mNeedsList.get(position) ;
                    final String optionId = needsList.getNeedId() ;
                    final int optionType = needsList.getTypeId() ;

                    if(0 == type){
                        if (!TextUtils.isEmpty(optionId)){
                            Intent intent=new Intent(mContext, NeedsSendActivity.class);
                            intent.putExtra(SkipUtils.INTENT_NEEDS_ID,optionId);
                            intent.putExtra(SkipUtils.INTENT_NEEDS_TYPE,optionType);
                            mFragment.startActivityForResult(intent,REQUEST_CODE_NEED) ;
                        }
                    }else if(1 == type){
                        DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                deleteNeed(optionId) ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }else if(2 == type){//缴纳保证金
                        mCurPosition = position ;
                        showPayBondDialog() ;
                    }else if(3 == type){//完成需求
                        DialogUtils.showCustomViewDialog(mContext, "完成需求", "完成该需求？", null
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        finishNeed(optionId) ;
                                    }
                                }, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }else if(4 == type){//刷新
                        refreshOptions(optionId) ;
                    }else if(5 == type){//上下架
                        updownOptions(optionId) ;
                    }
                }
            }) ;
            mListView.setAdapter(mNeedsAdapter) ;
        }else if(SEND_TYPE_JOBS == mSendType){
            mJobAdapter = new AdapterMySendJobs(mContext, mJobList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    final String optionId = mJobList.get(position).getRecruitID() ;
                    if(0 == type){
                        if (!TextUtils.isEmpty(optionId)){
                            Intent intent=new Intent(mContext, CreateZhaopinActivity.class);
                            intent.putExtra(SkipUtils.INTENT_JOB_ID,optionId);
                            mFragment.startActivityForResult(intent,REQUEST_CODE_JOB) ;
                        }
                    }else if(1 == type){
                        DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        deleteJobs(optionId) ;
                                    }
                                }, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }
                }
            }) ;
            mListView.setAdapter(mJobAdapter) ;
        }else if(SEND_TYPE_UNUSE == mSendType){
            mUnuseAdapter = new AdapterMySendUnuse(mActivity, mUnuseList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    final String optionId = mUnuseList.get(position).getProductID() ;
                    if(0 == type){
                        if (!TextUtils.isEmpty(optionId)){
                            Intent intent=new Intent(mContext, UnusedSendActivity.class);
                            intent.putExtra(SkipUtils.INTENT_UNUSE_ID,optionId);
                            mFragment.startActivityForResult(intent,REQUEST_CODE_UNUSE) ;
                        }
                    }else if(1 == type){
                        DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        deleteUnuse(optionId) ;
                                    }
                                }, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }else if(2 == type){//刷新
                        refreshOptions(optionId) ;
                    }else if(3 == type){//上下架
                        updownOptions(optionId) ;
                    }
                }
            }) ;
            mListView.setAdapter(mUnuseAdapter) ;
        }
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.lay_refresh_lv;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getDataList() ;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getDataList();
                }
            }
        });
    }


    /**
     * 显示缴纳保证金dialog
     */
    private void showPayBondDialog(){
        if(null == mPayBondDialog){
            mPayBondDialog = new Dialog(mContext,cn.idcby.commonlibrary.R.style.my_custom_dialog) ;
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_bond , null) ;
            mPayBondDialog.setContentView(v);

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.7F);

            mPayBondEv = v.findViewById(R.id.dialog_pay_bond_money_ev) ;
            TextView mOkTv = v.findViewById(R.id.dialog_pay_bond_ok_tv) ;
            TextView mCancelTv = v.findViewById(R.id.dialog_pay_bond_cancel_tv) ;
            mOkTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String money = mPayBondEv.getText().toString().trim() ;
                    if("".equals(money) || StringUtils.convertString2Float(money) <= 0){
                        ToastUtils.showToast(mContext ,"请输入正确的金额") ;
                        return ;
                    }
                    mPayBondDialog.dismiss();

                    showOrHiddenKeyBoard(false) ;
                    payBondMoney(money) ;
                }
            });
            mCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPayBondDialog.dismiss();
                    showOrHiddenKeyBoard(false) ;
                }
            });
        }

        mPayBondEv.setText("") ;

        showOrHiddenKeyBoard(true) ;

        mPayBondDialog.show() ;
    }

    private void showOrHiddenKeyBoard(boolean isShow){
        InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager == null){
            return ;
        }

        if(isShow){
            manager.showSoftInput(mPayBondEv , InputMethodManager.SHOW_FORCED) ;
        }else{
            manager.hideSoftInputFromWindow(mPayBondEv.getWindowToken() , 0) ;
        }
    }


    /**
     * 缴纳保证金
     * @param money 保证金
     */
    private void payBondMoney(final String money){
        mDialog.show() ;

        String mNeedId = mNeedsList.get(mCurPosition).getNeedId() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("NeedId" , mNeedId) ;
        paramMap.put("BidBond" , money) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_PAY_BOND, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("payBondMoney" , mContext , NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mDialog.dismiss();

                        if(bean != null){
                            String moneys = bean.PayableAmount ;
                            if("".equals(StringUtils.convertNull(moneys))){
                                moneys = money ;
                            }
                            //跳转到付款界面
                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,bean.OrderID) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_BOND_BID) ;
                            startActivityForResult(toPyIt,1000);
                        }else{
                            ToastUtils.showErrorToast(mContext , "缴纳失败") ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showErrorToast(mContext , "缴纳失败") ;
                    }
                });
    }

    /**
     * 上下架
     * @param optionId id
     */
    private void updownOptions(String optionId){
        mDialog.show() ;

        String url = "" ;
        if(SEND_TYPE_NEEDS == mSendType){
            url = Urls.NEEDS_UPDOWN ;
        }else if(SEND_TYPE_JOBS == mSendType){

        }else if(SEND_TYPE_UNUSE == mSendType){
            url = Urls.UNUSE_UPDOWN ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, url, paramMap
                , new RequestObjectCallBack<String>("updownOptions" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 刷新
     * @param optionId id
     */
    private void refreshOptions(String optionId){
        mDialog.show() ;

        String url = "" ;
        if(SEND_TYPE_NEEDS == mSendType){
            url = Urls.NEEDS_REFRESH ;
        }else if(SEND_TYPE_JOBS == mSendType){

        }else if(SEND_TYPE_UNUSE == mSendType){
            url = Urls.UNUSE_REFRESH ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, url, paramMap
                , new RequestObjectCallBack<String>("refreshOptions" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 删除需求
     * @param optionId id
     */
    private void deleteNeed(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("NeedId" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteNeed" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 完成需求
     * @param optionId id
     */
    private void finishNeed(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishNeed" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 删除服务
     * @param optionId id
     */
    private void deleteServer(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteServer" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 删除招聘
     * @param optionId id
     */
    private void deleteJobs(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteJobs" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 删除闲置
     * @param optionId id
     */
    private void deleteUnuse(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteUnuse" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        mCurPage = 1 ;
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getDataList(){
        mIsLoading = true ;

        if(SEND_TYPE_NEEDS == mSendType){
            getNeedsList() ;
        }else if(SEND_TYPE_JOBS == mSendType){
            getJobsList() ;
        }else if(SEND_TYPE_UNUSE == mSendType){
            getUnuseList() ;
        }
    }

    /**
     * 获取列表--需求
     */
    private void getNeedsList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mSendType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SEND_LIST, false, paramMap
                , new RequestListCallBack<NeedsList>("getDataList" + mSendType, mContext ,NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {
                        if(1 == mCurPage){
                            loadPage.showSuccessPage() ;
                            mNeedsList.clear();
                        }

                        mNeedsList.addAll(bean) ;
                        mNeedsAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mCurPage ++ ;
                        }

                        finishRequest();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest();
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest();
                    }
                });
    }

    /**
     * 获取列表--工作
     */
    private void getJobsList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mSendType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SEND_LIST, false, paramMap
                , new RequestListCallBack<JobsCollectionList>("getDataList" + mSendType, mContext ,JobsCollectionList.class) {
                    @Override
                    public void onSuccessResult(List<JobsCollectionList> bean) {
                        if(1 == mCurPage){
                            loadPage.showSuccessPage() ;
                            mJobList.clear();
                        }

                        mJobList.addAll(bean) ;
                        mJobAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mCurPage ++ ;
                        }

                        finishRequest();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest();
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest();
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getUnuseList(){
        //2018-04-27 10:18:58 注意：最新的和附近的，都是日期倒序
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mSendType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SEND_LIST, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseList" , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        if(1 == mCurPage){
                            mUnuseList.clear() ;
                        }
                        mUnuseList.addAll(bean) ;
                        mUnuseAdapter.notifyDataSetChanged() ;

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


    private void finishRequest(){
        mRefreshLay.finishRefresh() ;
        mIsLoading = false ;
        loadPage.showSuccessPage();

        int size = 0 ;

        if(SEND_TYPE_NEEDS == mSendType){
            size = mNeedsList.size() ;
        }else if(SEND_TYPE_JOBS == mSendType){
            size = mJobList.size() ;
        }else if(SEND_TYPE_UNUSE == mSendType){
            size = mUnuseList.size() ;
        }

        if(size == 0){
            if(mNullTv.getVisibility() != View.VISIBLE){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else{
            if(mNullTv.getVisibility() != View.GONE){
                mNullTv.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mIsReload && isVisibleToUser){
            int size = 0 ;

            if(SEND_TYPE_NEEDS == mSendType){
                size = mNeedsList.size() ;
            }else if(SEND_TYPE_JOBS == mSendType){
                size = mJobList.size() ;
            }else if(SEND_TYPE_UNUSE == mSendType){
                size = mUnuseList.size() ;
            }

            if(size == 0){
                mCurPage = 1 ;
                getDataList() ;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.MySendRefresh ev){
        if(ev.isRefresh()){//需要刷新
            mCurPage = 1 ;
            getDataList() ;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Activity.RESULT_OK == resultCode){
            mCurPage = 1 ;
            getDataList() ;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this) ;

        NetUtils.cancelTag("getDataList" + mSendType);

    }

}
