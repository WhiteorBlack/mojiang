package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.JobCompanyInfo;
import cn.idcby.jiajubang.Bean.JobsCollectionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CreateZhaopinActivity;
import cn.idcby.jiajubang.activity.MyJobCompanyEditActivity;
import cn.idcby.jiajubang.adapter.AdapterMySendJobs;
import cn.idcby.jiajubang.adapter.AdapterNomalImage;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的发布--招聘
 */

public class MyJobsFragment extends BaseFragment implements View.OnClickListener{
    private ListView mListView;
    private MaterialRefreshLayout mRefreshLay ;

    //header 相关 开始
    private View mComNullView ;
    private View mComEditTv ;
    private ImageView mComIv ;
    private TextView mComNameTv ;
    private TextView mComDescTipsTv;
    private TextView mComContentTv ;
    private TextView mComAddressTv ;
    private RecyclerView mComPicRv ;
    private TextView mComDescTv ;
    private ImageView mDescMoreIv ;
    //header 相关 结束

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsRefresh = false ;
    private boolean mIsReload = false ;

    //招聘
    private List<JobsCollectionList> mJobList = new ArrayList<>() ;
    private AdapterMySendJobs mJobAdapter ;

    private LoadingDialog mDialog ;

    private JobCompanyInfo mCompanyInfo ;
    private List<ImageThumb> mThumbList = new ArrayList<>();
    private AdapterNomalImage mImgAdapter;

    private static final int REQUEST_CODE_JOB = 1002 ;
    private static final int REQUEST_CODE_EDIT = 1003 ;

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                mIsRefresh = true ;//刚开始可以模拟一下
                getJobsCompanyInfo() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mDialog = new LoadingDialog(mContext) ;
        mDialog.setCancelable(false);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mListView = view.findViewById(R.id.lay_refresh_lv_list_lv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_lv_refresh_lay) ;

        initHeader() ;
    }

    private void initHeader(){
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_my_job_top ,null) ;

        mComNullView = headerView.findViewById(R.id.header_my_jobs_com_add_tv) ;
        mComEditTv = headerView.findViewById(R.id.header_my_jobs_com_edit_tv) ;
        mComIv = headerView.findViewById(R.id.header_my_jobs_com_iv) ;
        mComNameTv = headerView.findViewById(R.id.header_my_jobs_com_name_tv) ;
        mComContentTv = headerView.findViewById(R.id.header_my_jobs_com_content_tv) ;
        mComAddressTv = headerView.findViewById(R.id.header_my_jobs_com_address_tv) ;
        mComPicRv = headerView.findViewById(R.id.header_my_jobs_com_desc_pic_rv) ;
        mComDescTv = headerView.findViewById(R.id.header_my_jobs_com_desc_tv) ;
        mComDescTipsTv = headerView.findViewById(R.id.header_my_jobs_com_desc_tips_tv) ;
        mDescMoreIv = headerView.findViewById(R.id.header_my_jobs_com_desc_more_iv) ;
        mComNullView.setOnClickListener(this);
        mComEditTv.setOnClickListener(this);
        mDescMoreIv.setOnClickListener(this);
        mComPicRv.setNestedScrollingEnabled(false);
        mComPicRv.setFocusable(false);

        mListView.addHeaderView(headerView);

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
                }else if(2 == type){//刷新
                    refreshOptions(optionId);
                }else if(3 == type){//置顶
                    upOptions(optionId) ;
                }else if(5 == type){//上下架
                    updownOptions(optionId);
                }
            }
        }) ;
        mListView.setAdapter(mJobAdapter) ;
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
                refreshAll() ;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsRefresh && !mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getDataList();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_my_jobs_com_edit_tv == vId || R.id.header_my_jobs_com_add_tv == vId){
            Intent toEtIt = new Intent(mContext , MyJobCompanyEditActivity.class) ;
            startActivityForResult(toEtIt ,REQUEST_CODE_EDIT) ;
        }
    }

    /**
     * 填充内容
     */
    private void updateDisplay(){
        if(null == mCompanyInfo){
            return ;
        }

        mComNullView.setVisibility(View.GONE);
        mComEditTv.setVisibility(View.VISIBLE);
        mComNameTv.setVisibility(View.VISIBLE);
        mComAddressTv.setVisibility(View.VISIBLE);
        mComDescTv.setVisibility(View.VISIBLE);
        mComDescTipsTv.setVisibility(View.VISIBLE);
        mComContentTv.setVisibility(View.VISIBLE);
        mComIv.setVisibility(View.VISIBLE);
        mComPicRv.setVisibility(View.VISIBLE);

        String logoUrl = mCompanyInfo.getCompanyLogoImage() ;
        String comName = mCompanyInfo.getCompanyName() ;
        String comScal = mCompanyInfo.getCompanyScale() ;
        String comType = mCompanyInfo.getCompanyType() ;
        String comAddress = mCompanyInfo.getCompanyAddress() ;
        String comIndus = mCompanyInfo.getCompanyIntroduce() ;
        List<ImageThumb> thumbList = mCompanyInfo.getImageThumb() ;

        mComNameTv.setText(comName);
        mComAddressTv.setText(comAddress);
        mComDescTv.setText(comIndus);
        mComContentTv.setText(comType + " | " + comScal);
        GlideUtils.loader(logoUrl ,mComIv) ;

        mThumbList.clear();
        mThumbList.addAll(thumbList) ;
        if(null == mImgAdapter){
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                    , ResourceUtils.dip2px(mContext ,5)
                    ,getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            mComPicRv.setLayoutManager(layoutManager);
            mComPicRv.addItemDecoration(itemDecoration);
            mImgAdapter = new AdapterNomalImage(mActivity ,mThumbList) ;
            mComPicRv.setAdapter(mImgAdapter) ;
        }else{
            mImgAdapter.notifyDataSetChanged() ;
        }
    }


    /**
     * 置顶
     * @param optionId id
     */
    private void upOptions(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_TOTOP, paramMap
                , new RequestObjectCallBack<String>("upOptions" ,mContext ,String.class) {
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
     * 上下架
     * @param optionId id
     */
    private void updownOptions(String optionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_UPDOWN, paramMap
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

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_REFRESH, paramMap
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
     * 获取列表
     */
    private void getDataList(){
        mIsLoading = true ;

        getJobsList() ;
    }

    /**
     * 获取列表--公司模版
     */
    private void getJobsCompanyInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_COM_NOMAL_INFO, paramMap
                , new RequestObjectCallBack<JobCompanyInfo>("getJobsCompanyInfo", mContext ,JobCompanyInfo.class) {
                    @Override
                    public void onSuccessResult(JobCompanyInfo bean) {
                        mCompanyInfo = bean ;

                        updateDisplay() ;

                        if(mIsRefresh){
                            getDataList();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                        if(mIsRefresh){
                            getDataList();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                        if(mIsRefresh){
                            getDataList();
                        }
                    }
                });
    }

    /**
     * 获取列表--工作
     */
    private void getJobsList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + MySendFragment.SEND_TYPE_JOBS) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SEND_LIST, false, paramMap
                , new RequestListCallBack<JobsCollectionList>("getDataList", mContext ,JobsCollectionList.class) {
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

    private void finishRequest(){
        mIsLoading = false ;
        loadPage.showSuccessPage();

        if(mIsRefresh){
            mIsRefresh = false ;
            mRefreshLay.finishRefresh() ;
        }
    }

    private void refreshAll(){
        mCurPage = 1 ;

        mIsRefresh = true ;
        getJobsCompanyInfo() ;
    }

    public void refreshList(){
        mCurPage = 1 ;
        getDataList() ;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mIsReload && isVisibleToUser){
            if(null == mCompanyInfo){
                getJobsCompanyInfo() ;
            }

            int size = mJobList.size() ;
            if(size == 0){
                mCurPage = 1 ;
                getDataList() ;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_EDIT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                getJobsCompanyInfo() ;
            }
        }else if(REQUEST_CODE_JOB == requestCode){
            if(Activity.RESULT_OK == resultCode){
                refreshAll();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList");

    }

}
