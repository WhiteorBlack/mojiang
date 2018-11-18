package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.QuestionComment;
import cn.idcby.jiajubang.Bean.QuestionCommentResult;
import cn.idcby.jiajubang.Bean.QuestionDetails;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterQuestionComment;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.FlowLayout;

/**
 * 问答详细
 * Created on 2018/4/20.
 *
 * 2018-05-13 00:26:21
 * 追问改成类似回复，追问、回复来回切换
 *
 * 2018-07-31 17:23:17
 * 内容和图集换位置，图集放下面
 */

public class QuestionDetailsActivity extends BaseMoreStatusActivity {
    private ListView mLv ;

    //header
    private ImageView mUserIv ;
    private TextView mUserNameTv ;
    private TextView mTimeTv ;
    private TextView mRewardTv ;
    private TextView mTitleTv ;
    private TextView mContentTv ;
    private TextView mCommentCountTv ;
    private TextView mApplyTv ;
    private FlowLayout mPicLay ;
    private TextView mAnswerTv ;

    //回答
    private View mAnswerParentLay ;
    private View mAnswerLay ;
    private EditText mAnswerFocusEv ;
    private EditText mAnswerEv ;

    private AdapterQuestionComment mAdapter ;
    private List<QuestionComment> mDataList = new ArrayList<>();

    private String mQuestionId ;
    private QuestionDetails mDetails ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = true ;

    //回答问题相关
    private boolean mIsLoadingAnim = false ;//是否正在显示动画

    private boolean mIsSelf = false ;
    private boolean mIsFinishQuestion = false ;//是否采纳了别人答案

    private LoadingDialog mDialog ;

    private List<String> mTagList = new ArrayList<>() ;

    private static final int REQUEST_CODE_ANSWER = 1001 ;
    private static final int REQUEST_CODE_TIWEN = 1002 ;
    private static final int REQUEST_CODE_SUPPORT = 1003 ;
    private static final int REQUEST_CODE_FINISH = 1004 ;

    private int mCurPosition ;
    private int mAnswerType = 0 ;//0 回答 1 追问 2 回复

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public void requestData() {
        getQuestionDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_question_detail ;
    }

    @Override
    public String setTitle() {
        return "问答";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mQuestionId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_QUESTION_ID)) ;

        mLv = findViewById(R.id.acti_question_details_lv) ;

        ImageView mCloaseIv = findViewById(R.id.acti_question_details_answer_close_iv) ;
        mAnswerParentLay = findViewById(R.id.acti_question_details_answer_lay) ;
        mAnswerLay = findViewById(R.id.acti_question_details_answer_edit_lay) ;
        mAnswerFocusEv = findViewById(R.id.acti_question_details_answer_focus_ev) ;
        mAnswerEv = findViewById(R.id.acti_question_details_answer_sub_ev) ;
        TextView mAnswerSubTv = findViewById(R.id.acti_question_details_answer_sub_tv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mAnswerTv = findViewById(R.id.acti_question_details_answer_tv) ;

        mCloaseIv.setOnClickListener(this);
        mAnswerParentLay.setOnClickListener(this);
        mAnswerLay.setOnClickListener(this);
        mAnswerSubTv.setOnClickListener(this);
        mAnswerTv.setOnClickListener(this);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i + i1 >= i2){
                    getQuestionComment();
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10) ;
            }
        });

        initHeaderAndAdapter() ;
    }

    private void initHeaderAndAdapter(){
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_question_details ,null) ;
        mLv.addHeaderView(headerView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv) ;

        mApplyTv = headerView.findViewById(R.id.header_question_details_apply_tv) ;
        mUserIv = headerView.findViewById(R.id.header_question_details_user_iv) ;
        mUserNameTv = headerView.findViewById(R.id.header_question_details_name_tv) ;
        mTimeTv = headerView.findViewById(R.id.header_question_details_time_tv) ;
        mRewardTv = headerView.findViewById(R.id.header_question_details_reward_tv) ;
        mTitleTv = headerView.findViewById(R.id.header_question_details_title_tv) ;
        mContentTv = headerView.findViewById(R.id.header_question_details_content_tv) ;
        mCommentCountTv = headerView.findViewById(R.id.header_question_details_comment_count_tv) ;
        mPicLay = headerView.findViewById(R.id.header_question_details_pic_lay) ;
        mUserIv.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);

        mAdapter = new AdapterQuestionComment(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;
                final QuestionComment comment = mDataList.get(position) ;
                if(comment != null){
                    if(0 == type){
                        if(LoginHelper.isNotLogin(mContext)){
                            SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_SUPPORT);
                        }else{
                            supportAnswer() ;
                        }
                    }else if(1 == type){//追问或回复
                        mAnswerType = mIsSelf ? 1 : 2 ;

                        if(LoginHelper.isNotLogin(mContext)){
                            SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_TIWEN);
                        }else{
                            setGuigeLayShow(true);
                        }
                    }else if(2 == type){
                        if(LoginHelper.isNotLogin(mContext)){
                            SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_FINISH);
                        }else{
                            DialogUtils.showCustomViewDialog(mContext, "采纳", "采纳该回答？", null
                                    , "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    finishQuestion(comment.getQuestionAnswerID()) ;
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    }
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);
    }

    @Override
    public void dealOhterClick(View view) {
        if(mIsLoadingAnim){
            return ;
        }

        int vId = view.getId() ;

        if(R.id.acti_question_details_answer_close_iv == vId
                || R.id.acti_question_details_answer_lay == vId){
            setGuigeLayShow(false);
        }else if(R.id.acti_question_details_answer_edit_lay == vId){//空操作即可
        }else if(R.id.acti_question_details_answer_tv == vId){//我要回答
            mAnswerType = 0 ;

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_ANSWER);
            }else{
                setGuigeLayShow(true);
            }
        }else if(R.id.acti_question_details_answer_sub_tv == vId){//提交回答
            submitAnswer() ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0);
            mToTopIv.setVisibility(View.GONE);
        }else if(R.id.header_question_details_user_iv == vId
                || R.id.header_question_details_name_tv == vId){
            if(mDetails != null){
                SkipUtils.toOtherUserInfoActivity(mContext ,mDetails.getCreateUserId()) ;
            }
        }
    }

    /**
     * 设置回答布局显示
     */
    private void setGuigeLayShow(boolean isShow){
        mAnswerFocusEv.requestFocus() ;
        DialogUtils.hideKeyBoard(mAnswerEv);
        toGuigeChangeIt(isShow) ;
    }

    /**
     * 选择规格
     */
    private void toGuigeChangeIt(boolean isIn){
        if(isIn){//显示规格布局
            mAnswerParentLay.setVisibility(View.VISIBLE) ;
            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.in_bottomtop);
            mAnswerLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    mAnswerLay.setVisibility(View.VISIBLE) ;
                    mIsLoadingAnim = false ;

                    mAnswerEv.requestFocus() ;
//                    DialogUtils.showKeyBoard(mAnswerEv) ;
                }
            });
        }else{//隐藏规格布局
            mAnswerEv.setText("") ;

            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.out_topbottom);
            mAnswerLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mAnswerParentLay.setVisibility(View.GONE) ;
                    mIsLoadingAnim = false ;
                }
            });
        }
    }

    /**
     * 填充
     */
    private void updateDisplay(){
        if(mDetails != null){
            String userImg = mDetails.getHeadIcon() ;
            String userName = mDetails.getCreateUserName() ;
            String reward = mDetails.getReward() ;
            String title = mDetails.getQuestionTitle() ;
            String time = mDetails.getReleaseTime() ;
            String content = mDetails.getQuestionExplain() ;
            String commentCount = mDetails.getAnswerNumber() ;

            if(!"".equals(mDetails.getApplyText())){
                mApplyTv.setText(mDetails.getApplyText()) ;
            }
            mUserNameTv.setText(userName);
            mRewardTv.setText("悬赏" + reward);
            mTimeTv.setText(time);
            mTitleTv.setText(title);
            mContentTv.setText(content);
            mCommentCountTv.setText(commentCount+"个回答");
            GlideUtils.loaderUser(userImg ,mUserIv);

            //设置是否自己发布的，是否已经采纳
            mIsFinishQuestion = mDetails.isAdopt() ;
            mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
            mAdapter.setIsSelf(mIsSelf,mIsFinishQuestion);
            if(mIsSelf){
                mAnswerTv.setVisibility(View.GONE);
            }

            mPicLay.removeAllViews();
            final List<ImageThumb> imgList = mDetails.getAlbumsList() ;
            if(imgList != null && imgList.size() > 0){
               int mImageWidHei = (ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext , 15) * 2
                        - ResourceUtils.dip2px(mContext , 10) * 2) / 3;

                int size = imgList.size() ;
                for(int x = 0 ; x < size ; x ++){
                    final int curPosition = x ;
                    ImageThumb thumb = imgList.get(x) ;

                    String imgUrl = thumb.getThumbImgUrl() ;

                    ImageView iv = new ImageView(mContext) ;
                    iv.setLayoutParams(new ViewGroup.LayoutParams(mImageWidHei , mImageWidHei));
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    GlideUtils.loader(MyApplication.getInstance(),imgUrl , iv);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SkipUtils.toImageShowActivityWithThumb(mActivity , imgList ,curPosition);
                        }
                    });

                    mPicLay.addView(iv) ;
                }
            }

            getQuestionComment() ;
        }else{
            DialogUtils.showCustomViewDialog(mContext, "信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
        }
    }

    /**
     * 采纳回答
     */
    private void finishQuestion(String answerId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,answerId) ;

        String tag = "finishSub" ;
        if(!mTagList.contains(tag)){
            mTagList.add(tag) ;
        }

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_COMMENT_AGREE, paramMap
                , new RequestObjectCallBack<QuestionCommentResult>(tag,mContext ,QuestionCommentResult.class) {
                    @Override
                    public void onSuccessResult(QuestionCommentResult bean) {
                        mDialog.dismiss();

                        mIsFinishQuestion = true ;
                        mAdapter.setIsSelf(mIsSelf ,true) ;

                        mCurPage = 1 ;
                        getQuestionComment() ;
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
     * 提交回答
     */
    private void submitAnswer(){
        String content = mAnswerEv.getText().toString().trim() ;
        if("".equals(content)){
            ToastUtils.showToast(mContext ,"请输入内容");
            mAnswerEv.setText("") ;
            return ;
        }

        setGuigeLayShow(false);

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        String answerLevel = "" ;
        String parentAnswerID = "" ;
        String answerType = "" ;

        if(0 == mAnswerType){//回答
            answerType = "1" ;
            answerLevel = "1" ;
        }else{
            QuestionComment comment = mDataList.get(mCurPosition) ;
            if(comment.getChildList() != null && comment.getChildList().size() > 0){
                List<QuestionComment> childComment = comment.getChildList() ;
                int size = childComment.size() ;
                QuestionComment lastComment = childComment.get(size - 1) ;
                parentAnswerID = lastComment.getQuestionAnswerID() ;
            }else{
                parentAnswerID = comment.getQuestionAnswerID() ;
            }

            if(1 == mAnswerType){//追问
                answerType = "2" ;
                answerLevel = "2" ;
            }else if(2 == mAnswerType){//回复
                answerType = "1" ;
                answerLevel = "2" ;
            }
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Id" ,StringUtils.convertNull(mQuestionId)) ;
        paramMap.put("AnswerContent" ,content) ;
        paramMap.put("AnswerLevel" ,answerLevel) ;
        paramMap.put("ParentAnswerID" ,parentAnswerID) ;
        paramMap.put("AnswerType" ,answerType) ;

        String tag = "commentSub" ;
        if(!mTagList.contains(tag)){
            mTagList.add(tag) ;
        }

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_COMMENT_SUBMIT, paramMap
                , new RequestObjectCallBack<QuestionCommentResult>(tag,mContext ,QuestionCommentResult.class) {
                    @Override
                    public void onSuccessResult(QuestionCommentResult bean) {
                        mDialog.dismiss();

                        if(bean != null){
                            mCommentCountTv.setText(bean.getAnswerNumber() + "个回答");
                        }

                        mCurPage = 1 ;
                        getQuestionComment() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"回答失败");
                    }
                });
    }

    /**
     * 赞回答
     */
    private void supportAnswer(){
        String mCurAnswerId = mDataList.get(mCurPosition).getQuestionAnswerID() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,StringUtils.convertNull(mCurAnswerId)) ;

        String tag = "supportAnswer" + StringUtils.convertNull(mCurAnswerId) ;
        if(!mTagList.contains(tag)){
            mTagList.add(tag) ;
        }

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_COMMENT_SUPPORT, paramMap
                , new RequestObjectCallBack<SupportResult>(tag ,mContext ,SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        mCurPage = 1 ;
                        getQuestionComment() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 获取问题详细
     */
    private void getQuestionDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" ,mQuestionId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_DETAILS, paramMap
                , new RequestObjectCallBack<QuestionDetails>("getDetails" ,mContext ,QuestionDetails.class) {
                    @Override
                    public void onSuccessResult(QuestionDetails bean) {
                        showSuccessPage() ;
                        mDetails = bean ;

                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;
                    }
                });
    }

    /**
     * 获取问题回答
     */
    private void getQuestionComment(){
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("Keyword" ,mQuestionId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_COMMENT_LIST, paramMap
                , new RequestListCallBack<QuestionComment>("getComment" ,mContext ,QuestionComment.class) {
                    @Override
                    public void onSuccessResult(List<QuestionComment> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() < 10){
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
        mIsLoading = false ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("暂无回答");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_ANSWER == requestCode){
            if(RESULT_OK == resultCode){
                //设置是否自己发布的，是否已经采纳
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                mAdapter.setIsSelf(mIsSelf,mIsFinishQuestion);
                if(mIsSelf){
                    mAnswerTv.setVisibility(View.GONE);
                }
                setGuigeLayShow(true);
            }
        }else if(REQUEST_CODE_SUPPORT == requestCode){
            if(RESULT_OK == resultCode){
                supportAnswer();
            }
        }else if(REQUEST_CODE_TIWEN == requestCode){
            if(RESULT_OK == resultCode){
                setGuigeLayShow(true);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDetails") ;
        NetUtils.cancelTag("getComment") ;
        for(String tag : mTagList){
            NetUtils.cancelTag(tag) ;
        }
    }
}
