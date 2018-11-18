package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.BaseResultBean;
import cn.idcby.jiajubang.Bean.JobsList;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.Bean.QuestionList;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ChooseResumeActivity;
import cn.idcby.jiajubang.activity.CircleTransportActivity;
import cn.idcby.jiajubang.activity.QuestionDetailsActivity;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.adapter.AdapterCircleActive;
import cn.idcby.jiajubang.adapter.AdapterCircleActiveLv;
import cn.idcby.jiajubang.adapter.AdapterJobList;
import cn.idcby.jiajubang.adapter.AdapterNeedsList;
import cn.idcby.jiajubang.adapter.AdapterQuestionList;
import cn.idcby.jiajubang.adapter.AdapterUnuseLvGood;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 个人主页
 * 2018-06-27 17:18:13
 */

public class UserSendFragment extends BaseFragment {
    private ListView mListView;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    //闲置
    private List<UnuseGoodList> mUnuseList = new ArrayList<>() ;
    private AdapterUnuseLvGood mUnuseAdapter ;

    //需求
    private List<NeedsList> mNeedsList = new ArrayList<>() ;
    private AdapterNeedsList mNeedsAdapter ;

    //招聘
    private List<JobsList> mJobList = new ArrayList<>() ;
    private AdapterJobList mJobAdapter ;

    //圈子
    private List<UserActive> mCircleList = new ArrayList<>() ;
    private AdapterCircleActiveLv mCircleAdapter ;

    //问答
    private List<QuestionList> mQuestionList = new ArrayList<>() ;
    private AdapterQuestionList mQuestionAdapter;

    private String mUserId ;

    //6.闲置 3.需求 5.招聘 7.圈子8.问答
    private int mSendType;
    public static final int SEND_TYPE_UNUSE = 6 ;
    public static final int SEND_TYPE_NEEDS = 3 ;
    public static final int SEND_TYPE_JOBS = 5 ;
    public static final int SEND_TYPE_CIRCLE = 7 ;
    public static final int SEND_TYPE_QUESTION = 8 ;

    private LoadingDialog mLoadingDialog;
    private int mCurPosition ;

    private static final int REQUEST_CODE_CHOOSE_RESUME = 1000 ;
    private static final int REQUEST_CODE_SUBMIT_RESUME = 1001 ;
    private static final int REQUEST_CODE_QUESTION = 1002 ;
    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1005 ;
    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1006 ;


    public static UserSendFragment getInstance(String userId ,int type) {
       UserSendFragment fragment = new UserSendFragment() ;
       fragment.mSendType = type ;
       fragment.mUserId = userId ;
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
        mLoadingDialog = new LoadingDialog(mContext) ;
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mListView = view.findViewById(R.id.lay_refresh_lv_list_lv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_lv_refresh_lay) ;
        mNullTv = view.findViewById(R.id.lay_refresh_lv_null_tv) ;

        if(SEND_TYPE_NEEDS == mSendType){

            mNeedsAdapter = new AdapterNeedsList(mActivity,mNeedsList) ;
            mListView.setAdapter(mNeedsAdapter) ;

        }else if(SEND_TYPE_JOBS == mSendType){
            mJobAdapter = new AdapterJobList(mContext, mJobList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){
                        mCurPosition = position ;

                        intentToChooseResume() ;
                    }else if(1 == type){
                        SkipUtils.toJobDetailActivity(mContext , mJobList.get(position).getRecruitID());
                    }
                }
            }) ;
            mListView.setAdapter(mJobAdapter) ;
        }else if(SEND_TYPE_UNUSE == mSendType){
            mUnuseAdapter = new AdapterUnuseLvGood(mActivity, mUnuseList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    UnuseGoodList goodList = mUnuseList.get(position) ;
                    if(goodList != null){
                        if(0 == type){
                            Intent toDtIt = new Intent(mContext ,UnuseGoodDetailsActivity.class) ;
                            toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID,goodList.getProductID()) ;
                            startActivity(toDtIt) ;
                        }else if(1 == type){
                            //已经在主页了，不用再跳转
//                            SkipUtils.toOtherUserInfoActivity(mContext ,goodList.getCreateUserId());
                        }
                    }
                }
            }) ;
            mListView.setAdapter(mUnuseAdapter) ;
        }else if(SEND_TYPE_CIRCLE == mSendType){
            mCircleAdapter = new AdapterCircleActiveLv(mActivity , mCircleList,true,new RecyclerViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    mCurPosition = position ;

                    if(AdapterCircleActive.CLICK_TYPE_SUPPORT == type){
                        supportCircle() ;
                    }else if(AdapterCircleActive.CLICK_TYPE_TRANS == type){
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mFragment, REQUEST_CODE_CIRCLE_TO_TRANSPORT);
                            return;
                        }

                        toTransportActivity();
                    }else if(AdapterCircleActive.CLICK_TYPE_SHARE == type){
                        UserActive info = mCircleList.get(mCurPosition) ;
                        if(info != null){
                            String title = info.getBodyContent() ;
                            String imgUrl = info.getImgUrl() ;
                            String strUrl = info.getH5Url();

                            ShareUtils.shareWeb(mActivity ,title,strUrl ,imgUrl ,"") ;
                        }
                    }
                }
                @Override
                public void onItemLongClickListener(int type, int position) {
                }
            }) ;

            mListView.setAdapter(mCircleAdapter) ;
        }else if(SEND_TYPE_QUESTION == mSendType){
            mQuestionAdapter = new AdapterQuestionList(mActivity, mQuestionList,new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){
                        QuestionList questionList = mQuestionList.get(position) ;
                        if(questionList != null){
                            String questId =questionList.getQuestionId() ;

                            Intent toDtIt = new Intent(mContext , QuestionDetailsActivity.class) ;
                            toDtIt.putExtra(SkipUtils.INTENT_QUESTION_ID ,questId) ;
                            mFragment.startActivityForResult(toDtIt ,REQUEST_CODE_QUESTION);
                        }
                    }
                }
            }) ;

            mListView.setAdapter(mQuestionAdapter) ;
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
     * 跳转到转载
     */
    private void toTransportActivity(){
        UserActive info = mCircleList.get(mCurPosition) ;
        if(info != null){
            CircleTransportActivity.launch(mFragment ,info.getPostID()
                    ,info.getBodyContent() ,info.getImgUrl()
                    ,info.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
        }

    }


    /**
     * 选择简历来投递
     */
    private void intentToChooseResume(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mFragment , REQUEST_CODE_CHOOSE_RESUME) ;
        }else{
            Intent toCtIt = new Intent(mContext,ChooseResumeActivity.class) ;
            startActivityForResult(toCtIt , REQUEST_CODE_SUBMIT_RESUME) ;
        }
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
        }else if(SEND_TYPE_CIRCLE == mSendType){
            getCircleList();
        }else if(SEND_TYPE_QUESTION == mSendType){
            getQuestionList();
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
        paramMap.put("FollowUserId" , StringUtils.convertNull(mUserId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_SEND_LIST, false, paramMap
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
        paramMap.put("FollowUserId" , StringUtils.convertNull(mUserId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_SEND_LIST, false, paramMap
                , new RequestListCallBack<JobsList>("getDataList" + mSendType, mContext ,JobsList.class) {
                    @Override
                    public void onSuccessResult(List<JobsList> bean) {
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
        paramMap.put("FollowUserId" , StringUtils.convertNull(mUserId)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_SEND_LIST, paramMap
                , new RequestListCallBack<UnuseGoodList>("getDataList" + mSendType , mContext , UnuseGoodList.class) {
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

    /**
     * 获取列表
     */
    private void getCircleList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mSendType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("FollowUserId" , StringUtils.convertNull(mUserId)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_SEND_LIST, paramMap
                , new RequestListCallBack<UserActive>("getDataList" + mSendType , mContext , UserActive.class) {
                    @Override
                    public void onSuccessResult(List<UserActive> bean) {
                        if(1 == mCurPage){
                            mCircleList.clear() ;
                        }
                        mCircleList.addAll(bean) ;
                        mCircleAdapter.notifyDataSetChanged() ;

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
     * 获取列表
     */
    private void getQuestionList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mSendType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("FollowUserId" , StringUtils.convertNull(mUserId)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_SEND_LIST, paramMap
                , new RequestListCallBack<QuestionList>("getDataList" + mSendType , mContext , QuestionList.class) {
                    @Override
                    public void onSuccessResult(List<QuestionList> bean) {
                        if(1 == mCurPage){
                            mQuestionList.clear() ;
                        }
                        mQuestionList.addAll(bean) ;
                        mQuestionAdapter.notifyDataSetChanged() ;

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
        }else if(SEND_TYPE_CIRCLE == mSendType){
            size = mCircleList.size() ;
        }else if(SEND_TYPE_QUESTION == mSendType){
            size = mQuestionList.size() ;
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

    /**
     * 投递简历
     */
    private void submitResumeSend(ResumeList resume){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        JobsList jobsList = mJobList.get(mCurPosition) ;
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
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }


    /**
     * 点赞
     */
    private void supportCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mFragment , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        String articleID = mCircleList.get(mCurPosition).getPostID() ;

        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", articleID);
        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_SUPPORT, false, para,
                new RequestObjectCallBack<SupportResult>("supportCircle", mContext, SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();

                        mCircleList.get(mCurPosition).setSupportState(bean.AddOrDelete ,bean.LikeNumber) ;
                        mCircleAdapter.notifyDataSetChanged() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                    }
                });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CHOOSE_RESUME == requestCode){
            if(Activity.RESULT_OK == resultCode){
                intentToChooseResume() ;
            }
        }else if(REQUEST_CODE_SUBMIT_RESUME == requestCode){
            if(Activity.RESULT_OK == resultCode && data != null){
                ResumeList resumeList = (ResumeList) data.getSerializableExtra(SkipUtils.INTENT_RESUME_INFO);
                if(resumeList != null){
                    submitResumeSend(resumeList) ;
                }
            }
        }else if(REQUEST_CODE_CIRCLE_SUPPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                supportCircle() ;
            }
        }else if(REQUEST_CODE_CIRCLE_TO_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                toTransportActivity() ;
            }
        }else if(REQUEST_CODE_CIRCLE_FROM_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                if(mCircleList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getCircleList() ;
                }else{
                    mCircleList.get(mCurPosition).updateTransCount();
                    mCircleAdapter.notifyDataSetChanged() ;
                }
            }
        }else {
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;
                getDataList() ;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList" + mSendType);

    }

}
