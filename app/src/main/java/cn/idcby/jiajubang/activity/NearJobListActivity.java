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
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 附近--附近工作
 * Created on 2018/4/13.
 */

public class NearJobListActivity extends BaseActivity {
    private View mLoadingLay ;
    private TextView mNullTv ;

    private ListView mLv ;
    private MaterialRefreshLayout mRefreshLay ;

    private AdapterJobList mAdapter ;
    private List<JobsList> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private int mListType = 0 ;//0全部 1全职  2兼职

    private int mCurPosition ;
    private LoadingDialog mLoadingDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_near_job_list ;
    }

    @Override
    public void initView() {
        mListType = getIntent().getIntExtra(SkipUtils.INTENT_JOB_TYPE , mListType) ;

        mLoadingLay = findViewById(R.id.acti_near_job_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_near_job_list_null_tv) ;
        mLv = findViewById(R.id.acti_near_job_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_near_job_list_refresh_lay) ;

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

    }

    /**
     * 选择简历来投递
     */
    private void intentToChooseResume(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(NearJobListActivity.this , 1000) ;
        }else{
            Intent toCtIt = new Intent(mContext,ChooseResumeActivity.class) ;
            NearJobListActivity.this.startActivityForResult(toCtIt , 1001) ;
        }
    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取简历列表
     */
    private void getJobList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("TypeId" , "" + mListType) ;
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("PageSize", "10");
        paramMap.put("Longitude", MyApplication.getLongitude());
        paramMap.put("Latitude", MyApplication.getLatitude());
        paramMap.put("AreaId", MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_NEAR_WORK_LIST, paramMap
                , new RequestListCallBack<JobsList>("getJobList" , mContext , JobsList.class) {
                    @Override
                    public void onSuccessResult(List<JobsList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

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

    private void finishRequest(){
        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;
        showOrHiddenLoading(false);

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }else{
            mNullTv.setVisibility(View.GONE) ;
        }
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getJobList") ;

    }
}
