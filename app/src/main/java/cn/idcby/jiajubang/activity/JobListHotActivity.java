package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.BaseResultBean;
import cn.idcby.jiajubang.Bean.JobsList;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterJobList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 招聘列表--优质企业（职位推荐）
 * Created on 2018/3/28.
 *
 * 2018-09-11 14:30:30
 * 为了区分 普通招聘列表（JobListActivity） 而创建的
 */

public class JobListHotActivity extends BaseActivity {
    private TopBarRightView mRightLay ;

    private View mLoadingLay ;
    private TextView mSearchKeyTv ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private AdapterJobList mAdapter ;
    private List<JobsList> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private String mSearchKey = "" ;

    private int mCurPosition ;
    private LoadingDialog mLoadingDialog ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_job_list_hot ;
    }

    @Override
    public void initView() {
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        mRightLay = findViewById(R.id.acti_job_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_job_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_job_list_search_key_tv) ;
        mRightLay.setOnClickListener(this) ;
        searchLay.setOnClickListener(this) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        if(mSearchKey != null && !"".equals(mSearchKey.trim())){
            mSearchKeyTv.setText(mSearchKey) ;
        }

        mLoadingLay = findViewById(R.id.acti_job_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_job_list_null_tv) ;
        mLv = findViewById(R.id.acti_job_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_job_list_refresh_lay) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv) ;

        mAdapter = new AdapterJobList(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;

                    intentToChooseResume() ;
                }else if(1 == type){
                    JobsList job = mDataList.get(position) ;
                    if(job != null){
                        SkipUtils.toJobDetailActivity(mContext , job.getRecruitID());
                    }
                }
            }
        });
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getJobList();
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getJobList() ;
            }
        });
    }

    @Override
    public void initData() {
        getJobList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_job_list_right_iv == vId){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,1009);
            }else{
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }else if(R.id.acti_job_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            JobListHotActivity.this.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 选择简历来投递
     */
    private void intentToChooseResume(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(JobListHotActivity.this , 1000) ;
        }else{
            Intent toCtIt = new Intent(mContext,ChooseResumeActivity.class) ;
            JobListHotActivity.this.startActivityForResult(toCtIt , 1001) ;
        }
    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 搜索
     */
    private void searchFromList(){
        mToTopIv.setVisibility(View.GONE);
        mLv.setSelection(0) ;

        mCurPage = 1 ;
        mIsMore = true ;
        showOrHiddenLoading(true) ;

        getJobList() ;
    }

    /**
     * 获取列表
     */
    private void getJobList(){
        mIsLoading = true ;

        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Keyword" , null == mSearchKey ? "" : mSearchKey) ;
        paramMap.put("Type" , "1") ;//1 优质企业 2 全部

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_LIST, paramMap
                , new RequestListCallBack<JobsList>("getJobList" , mContext , JobsList.class) {
                    @Override
                    public void onSuccessResult(List<JobsList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() < 10){
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

    private void finishRequest(){
        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("") ;
            mNullTv.setVisibility(View.VISIBLE);
        }

        showOrHiddenLoading(false);
    }

    /**
     * 投递简历
     */
    private void submitResumeSend(ResumeList resume){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        JobsList jobsList = mDataList.get(mCurPosition) ;
        String recId = null ;
        if(jobsList != null){
            recId = jobsList.getRecruitID() ;
        }
        if(null == recId){
            recId = "" ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("RecruitId" , recId) ;
        paramMap.put("ResumeId" , resume.getResumeId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_SEND, paramMap
                , new RequestObjectCallBack<BaseResultBean>("submitResumeSend" , mContext ,BaseResultBean.class) {
                    @Override
                    public void onSuccessResult(BaseResultBean bean) {
                        ToastUtils.showToast(mContext , "申请成功");
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showToast(mContext , "申请失败");
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(mContext , "申请失败");
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode){
                intentToChooseResume() ;
            }
        }else if(1001 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ResumeList resumeList = (ResumeList) data.getSerializableExtra(SkipUtils.INTENT_RESUME_INFO);
                if(resumeList != null){
                    submitResumeSend(resumeList) ;
                }
            }
        }else if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;

                searchFromList() ;
            }
        }else if(1009 == requestCode){
            if(RESULT_OK == resultCode){
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }
    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mRightLay.setUnreadCount(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getJobList") ;

    }
}
