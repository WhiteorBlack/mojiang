package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.BaseResultBean;
import cn.idcby.jiajubang.Bean.JobRecAdv;
import cn.idcby.jiajubang.Bean.JobsList;
import cn.idcby.jiajubang.Bean.RecommendJobsPostList;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterJobList;
import cn.idcby.jiajubang.adapter.RecommendJobAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.StationaryGridView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 招聘
 */
public class GiveJobActivity extends BaseActivity{
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mListView;

    //375 155
    private Banner mTopBanner ;
    private ImageView mMidLeftIv ;
    private TextView mMidLeftTv ;
    private ImageView mMidRightIv ;
    private TextView mMidRightTv ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;

    private StationaryGridView mGvJob;
    private RecommendJobAdapter mRecommendJobAdapter;
    private List<RecommendJobsPostList> mRecommentJobList = new ArrayList<>() ;

    private boolean mIsRefreshing = false ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private List<JobsList> mRecommendComJobList = new ArrayList<>() ;
    private AdapterJobList mAdapter;

    private int mCurPosition ;
    private LoadingDialog mLoadingDialog ;

    private static final int REQUEST_CODE_CHOOSE_RESUME = 1000 ;
    private static final int REQUEST_CODE_SUBMIT_RESUME = 1001 ;
    private static final int REQUEST_CODE_CREATE_RESUME = 1002 ;
    private static final int REQUEST_CODE_MY_COLLECTION = 1003 ;
    private static final int REQUEST_CODE_SEARCH = 1004 ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    private void initAda(){
        mTopBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext , mTopBannerList.get(position - 1)) ;
            }
        });
        //设置banner动画效果
        mTopBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mTopBanner.setDelayTime(5000);
        mTopBanner.setImageLoader(new BannerImageLoader()) ;


    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_give_job ;
    }

    @Override
    public void initView() {

        StatusBarUtil.setTransparentForImageView(this,null);

        mRefreshLay = findViewById(R.id.acti_give_job_refresh_lay) ;
        mListView = findViewById(R.id.acti_give_job_lv);
        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;

        initHeadView();

        mAdapter = new AdapterJobList(mContext, mRecommendComJobList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;

                    intentToChooseResume() ;
                }else if(1 == type){
                    SkipUtils.toJobDetailActivity(mContext , mRecommendComJobList.get(position).getRecruitID());
                }
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i1 + i >= i2){
                    getRecommendCompany() ;
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                mIsRefreshing = true ;
                getTopBanner() ;
            }
        });

        initAda() ;
    }

    @Override
    public void initData() {
        mCurPage = 1 ;
        mIsMore = true ;

        getTopBanner() ;
        getMidImg() ;
        getRecommendJobs() ;
        getRecommendCompany() ;
    }

    @Override
    public void initListener() {
    }

    private void initHeadView() {
        View headView = View.inflate(mContext, R.layout.view_head_for_give_job, null);
        mGvJob = headView.findViewById(R.id.gv_job);
        mTopBanner = headView.findViewById(R.id.header_for_give_job_banner) ;
        mMidLeftIv = headView.findViewById(R.id.header_for_give_job_mid_left_iv) ;
        mMidLeftTv = headView.findViewById(R.id.header_for_give_job_mid_left_tv) ;
        mMidRightIv = headView.findViewById(R.id.header_for_give_job_mid_right_iv) ;
        mMidRightTv = headView.findViewById(R.id.header_for_give_job_mid_right_tv) ;
        mMidLeftIv.setOnClickListener(this);
        mMidRightIv.setOnClickListener(this);

        View searchLay = headView.findViewById(R.id.header_give_job_search_lay) ;
        View mResumeAllLay = headView.findViewById(R.id.header_give_job_resume_all_lay) ;
        View mResumeHalfLay = headView.findViewById(R.id.header_give_job_resume_half_lay) ;
        View mCompanyJobLay = headView.findViewById(R.id.header_give_job_company_job_lay) ;
        View mMyCollectionLay = headView.findViewById(R.id.header_give_job_my_collection_lay) ;
        mMyCollectionLay.setOnClickListener(this);
        mCompanyJobLay.setOnClickListener(this);
        mResumeHalfLay.setOnClickListener(this);
        mResumeAllLay.setOnClickListener(this);
        searchLay.setOnClickListener(this);

        TextView createReTv = headView.findViewById(R.id.header_give_job_create_resume_tv) ;
        createReTv.setOnClickListener(this);

        mRecommendJobAdapter = new RecommendJobAdapter(mRecommentJobList, mContext);
        mGvJob.setAdapter(mRecommendJobAdapter);
        mListView.addHeaderView(headView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mListView.addFooterView(mFooterTv) ;

        //推荐职位
        mGvJob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecommendJobsPostList jobsPostList = mRecommentJobList.get(i) ;
                if(jobsPostList != null){
                    String key = jobsPostList.getWorkPostId() ;
                    Intent toLtIt = new Intent(mContext , JobListRecommendActivity.class) ;
                    toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID , key) ;
                    startActivity(toLtIt) ;
                }
            }
        });

        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;
        int mImgHei = (int) ((screenWidth - ResourceUtils.dip2px(mContext , 15) * 3) / 2 * 85 / 185F);

        mTopBanner.getLayoutParams().height = (int) (screenWidth / ImageWidthUtils.nomalBannerImageRote());
        mMidLeftIv.getLayoutParams().height = mImgHei ;
        mMidRightIv.getLayoutParams().height = mImgHei ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_give_job_create_resume_tv == vId){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_CREATE_RESUME);
            }else{
                Intent toCrIt = new Intent(mContext ,CreateResumeActivity.class) ;
                startActivity(toCrIt) ;
            }
        }else if(R.id.header_give_job_search_lay == vId){//搜索
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            mActivity.startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
        }else if(R.id.header_give_job_resume_all_lay == vId){//全职简历
            intentToResumeList(1) ;
        }else if(R.id.header_give_job_resume_half_lay == vId){//兼职简历
            intentToResumeList(2) ;
        }else if(R.id.header_give_job_company_job_lay == vId){//公司招聘
            Intent toLtIt = new Intent(mContext , JobListActivity.class) ;
            startActivity(toLtIt) ;
        }else if(R.id.header_give_job_my_collection_lay == vId){//我的收藏
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_MY_COLLECTION);
            }else{
                Intent toCtIt = new Intent(mContext ,MyCollectionResumeActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(R.id.header_for_give_job_mid_left_iv == vId){//职位推荐
            Intent toLtIt = new Intent(mContext , JobListHotActivity.class) ;
            startActivity(toLtIt) ;
        }else if(R.id.header_for_give_job_mid_right_iv == vId){//人才推荐
//            intentToResumeList(3) ;
            Intent toLtIt = new Intent(mContext , ResumeHotListActivity.class) ;
            toLtIt.putExtra(SkipUtils.INTENT_RESUME_TYPE , 3) ;
            startActivity(toLtIt) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mListView.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 选择简历来投递
     */
    private void intentToChooseResume(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity , REQUEST_CODE_CHOOSE_RESUME) ;
        }else{
            Intent toCtIt = new Intent(mContext,ChooseResumeActivity.class) ;
            startActivityForResult(toCtIt , REQUEST_CODE_SUBMIT_RESUME) ;
        }
    }

    /**
     * 简历列表
     * @param type 0全部 1全职 2兼职
     */
    private void intentToResumeList(int type){
        Intent toLtIt = new Intent(mContext , ResumeListActivity.class) ;
        toLtIt.putExtra(SkipUtils.INTENT_RESUME_TYPE , type) ;
        startActivity(toLtIt) ;
    }

    /**
     * 获取推荐职位
     */
    private void getRecommendJobs(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "1") ;
        paramMap.put("PageSize" , "18") ;
        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.RECOMMEND_JOBS_LIST, paramMap
                , new RequestListCallBack<RecommendJobsPostList>("getRecommendJobs" ,mContext , RecommendJobsPostList.class) {
                    @Override
                    public void onSuccessResult(List<RecommendJobsPostList> bean) {
                        mRecommentJobList.clear();
                        mRecommentJobList.addAll(bean) ;
                        mRecommendJobAdapter.notifyDataSetChanged() ;

                        if(mIsRefreshing){
                            getRecommendCompany() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getRecommendCompany() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getRecommendCompany() ;
                        }
                    }
                });
    }

    /**
     * 获取中间
     */
    private void getMidImg(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "IndustryRecruitment") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<JobRecAdv>("getMidImg" ,mContext , JobRecAdv.class) {
                    @Override
                    public void onSuccessResult(List<JobRecAdv> bean) {
                        if(bean.size() > 1){
                            String leftName = bean.get(0).getItemName() ;
                            String leftImgUrl = bean.get(0).getIconUrl() ;
                            String rightName = bean.get(1).getItemName() ;
                            String rightImgUrl = bean.get(1).getIconUrl() ;

                            mMidLeftTv.setText(leftName);
                            mMidRightTv.setText(rightName);
                            GlideUtils.loader(MyApplication.getInstance(), leftImgUrl , mMidLeftIv) ;
                            GlideUtils.loader(MyApplication.getInstance(), rightImgUrl , mMidRightIv) ;
                        }

                        if(mIsRefreshing){
                            getRecommendJobs() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getRecommendJobs() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getRecommendJobs() ;
                        }
                    }
                });
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppWorkHead") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, paramMap
                , new RequestListCallBack<AdvBanner>("getTopBanner" ,mContext , AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;
                        if(bean.size() > 0){
                            List<String> imageUrl = new ArrayList<>(bean.size()) ;
                            for(AdvBanner banner : bean){
                                imageUrl.add(banner.getImgUrl()) ;
                            }
                            mTopBanner.update(imageUrl);
                        }

                        if(mIsRefreshing){
                            getMidImg() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getMidImg() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getMidImg() ;
                        }
                    }
                });
    }

    /**
     * 获取推荐企业
     */
    private void getRecommendCompany(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        //2018-09-15 10:23:47 改：暂时不传定位的省市
//        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
//        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.RECOMMEND_COMPANY_LIST, paramMap
                , new RequestListCallBack<JobsList>("getRecommendCompany" ,mContext
                        , JobsList.class) {
                    @Override
                    public void onSuccessResult(List<JobsList> bean) {
                        if(1 == mCurPage){
                            mRecommendComJobList.clear();
                        }

                        mRecommendComJobList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

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

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mRecommendComJobList.size() == 0){
            mFooterTv.setText("暂无推荐") ;
        }

        if(mIsRefreshing){
            mIsRefreshing = false ;
            mRefreshLay.finishRefresh();
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

        JobsList jobsList = mRecommendComJobList.get(mCurPosition) ;
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

        if(REQUEST_CODE_CHOOSE_RESUME == requestCode){
            if(RESULT_OK == resultCode){
                intentToChooseResume() ;
            }
        }else if(REQUEST_CODE_SUBMIT_RESUME == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ResumeList resumeList = (ResumeList) data.getSerializableExtra(SkipUtils.INTENT_RESUME_INFO);
                if(resumeList != null){
                    submitResumeSend(resumeList) ;
                }
            }
        }else if(REQUEST_CODE_CREATE_RESUME == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCrIt = new Intent(mContext ,CreateResumeActivity.class) ;
                startActivity(toCrIt) ;
            }
        }else if(REQUEST_CODE_MY_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,MyCollectionResumeActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                Intent toLiIt = new Intent(mContext ,JobListActivity.class) ;
                toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,mSearchKey) ;
                startActivity(toLiIt) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getTopBanner") ;
        NetUtils.cancelTag("getRecommendJobs") ;
        NetUtils.cancelTag("getRecommendCompany") ;
        NetUtils.cancelTag("submitResumeSend") ;

    }
}
