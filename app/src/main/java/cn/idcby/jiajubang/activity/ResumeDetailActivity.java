package cn.idcby.jiajubang.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.ResumeContactTa;
import cn.idcby.jiajubang.Bean.ResumeDetails;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.WorkExperience;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterWorkExp;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.StationaryListView;
import cn.idcby.jiajubang.view.TopBarRightPopup;

/**
 * 简历详细
 *
 * 2018-05-30 17:11:19
 * 去掉联系TA的弹窗里的分享（分享会加积分，但是没做）
 */
public class ResumeDetailActivity extends BaseMoreStatusActivity {
    private ImageView mUserIv;
    private TextView mWorkTypeTv;
    private TextView mUserNameTv;
    private TextView mUserSexTv;//年龄、性别
    private TextView mWorkMoneyTipsTv;//期望薪资
    private TextView mWorkMoneyTv;//期望薪资
    private TextView mWorkNameTv;//应聘职位

    private TextView mJobLocaTv ;
    private TextView mJobExpTv ;//经验
    private TextView mJobEduTv ;//学历

    private TextView mUserDescTv;
    private ImageView mUserDtMoreIv;

    private StationaryListView mExperLv ;//经历

    private FlowLayout mCardLay;//毕业证书

    private ImageView mSupportIv ;
    private ImageView mCollectionIv ;
    private LinearLayout mShareLL;

    private TextView mConnTv ;//联系他
    private TextView mSendTv ;//

    private String mResumeId;
    private ResumeDetails mDetails ;

    private Dialog mConnDialog ;//联系他Dialog
    private TextView mPhoneFirstTv ;
    private TextView mPhoneSecondTv ;
    private TextView mRemainderCountTv ;
    private TextView mWeChatTv ;
    private TextView mQQTv ;
    private TextView mEndTimeTv ;
    private ResumeContactTa mContactInfo ;

    private LoadingDialog loadingDialog ;

    private boolean mIsSelf = false ;

    private static final int REQUEST_CODE_SUPPORT = 1001 ;
    private static final int REQUEST_CODE_COLLECTION = 1002 ;
    private static final int REQUEST_CODE_CONNECTION = 1003 ;
    private static final int REQUEST_CODE_SEND_MSG = 1004 ;
    private static final int REQUEST_CODE_BUY_RESUME = 1005 ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    @Override
    public void requestData() {
        showLoadingPage();

        getResumeDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_resume_detail;
    }

    @Override
    public String setTitle() {
        return "简历详情";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        mRightView = imgRight ;

        imgRight.setVisibility(View.VISIBLE);
        imgRight.setImageResource(R.mipmap.ic_top_more_grey);
        setMessageCountShow(false) ;
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRightPopup();
            }
        });
    }

    /**
     * 显示右边popup
     */
    private void showRightPopup(){
        if(null == mRightPopup){
            mRightPopup = new TopBarRightPopup(mContext, mRightView, new TopBarRightPopup.TopRightPopupCallBack() {
                @Override
                public void onItemClick(int position) {
                    if(TopBarRightPopup.POPUP_ITEM_SHARE == position){//分享
                        shareToOtherPlant();
                    }else if(TopBarRightPopup.POPUP_ITEM_REQUEST == position){//投诉
                        SkipUtils.toRequestActivity(mContext,null) ;
                    }
                }
            }) ;
        }

        mRightPopup.displayDialog() ;
    }

    /**
     * 分享
     */
    private void shareToOtherPlant() {
        if(mDetails != null){
            String strTitle = mDetails.getName();
            String strUrl = mDetails.getH5Url();
            String strImgurl = mDetails.getUserHeadIcon();
            String strSubscriotion = "";
            ShareUtils.shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion);
        }
    }

    @Override
    public void init() {
        mResumeId = getIntent().getStringExtra(SkipUtils.INTENT_RESUME_ID) ;

        if(null == mResumeId){
            mResumeId = "" ;
        }

        mUserIv = findViewById(R.id.acti_resume_dt_user_iv) ;
        mWorkTypeTv = findViewById(R.id.acti_resume_details_job_type_tv) ;
        mUserNameTv = findViewById(R.id.acti_resume_dt_user_name_tv) ;
        mUserSexTv = findViewById(R.id.acti_resume_dt_sex_tv) ;
        mWorkMoneyTipsTv = findViewById(R.id.acti_resume_dt_work_money_tips_tv) ;
        mWorkMoneyTv = findViewById(R.id.acti_resume_dt_work_money_tv) ;
        mWorkNameTv = findViewById(R.id.acti_resume_dt_work_name_tv) ;

        mJobLocaTv = findViewById(R.id.acti_resume_dt_area_tv) ;
        mJobExpTv = findViewById(R.id.acti_resume_dt_exp_tv) ;
        mJobEduTv = findViewById(R.id.acti_resume_dt_edu_tv) ;

        mExperLv = findViewById(R.id.acti_resume_dt_exp_lv) ;

        mUserDescTv = findViewById(R.id.acti_resume_dt_user_desc_tv) ;
        mUserDtMoreIv = findViewById(R.id.acti_resume_dt_user_desc_more_iv) ;

        mCardLay = findViewById(R.id.acti_resume_dt_card_lay) ;
        mShareLL=findViewById(R.id.acti_resume_dt_share_lay);

        LinearLayout mSupportLay = findViewById(R.id.acti_resume_dt_support_lay) ;
        mSupportIv = findViewById(R.id.acti_resume_dt_support_iv) ;
        LinearLayout mCollectionLay = findViewById(R.id.acti_resume_dt_collection_lay) ;
        mCollectionIv = findViewById(R.id.acti_resume_dt_collection_iv) ;

        mSendTv = findViewById(R.id.acti_resume_dt_send_tv) ;
        mConnTv = findViewById(R.id.acti_resume_dt_connection_tv) ;

        mUserDtMoreIv.setOnClickListener(this);
        mSupportLay.setOnClickListener(this);
        mCollectionLay.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        mConnTv.setOnClickListener(this);
        mShareLL.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_resume_dt_connection_tv == vId){//联系TA
            if(!mIsSelf){
                if(null == mContactInfo){
                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(ResumeDetailActivity.this ,REQUEST_CODE_CONNECTION) ;
                        return ;
                    }
                    getResumeContactInfo();
                }else{
                    showConnDialog();
                }
            }
        }else if(R.id.acti_resume_dt_send_tv == vId){//发消息
            if(!mIsSelf){
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(ResumeDetailActivity.this ,REQUEST_CODE_SEND_MSG) ;
                }else{
                    SkipUtils.toMessageChatActivity(mActivity ,mDetails.getHxName());
                }
            }
        }else if(R.id.acti_resume_dt_support_lay == vId){//赞
            goSupport() ;
        }else if(R.id.acti_resume_dt_collection_lay == vId){//收藏
            goCollection() ;
        }else if(R.id.acti_resume_dt_user_desc_more_iv == vId){//个人简介--更多

        }else if(R.id.dialog_resume_conn_closet_tv == vId){//联系方式--close
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }
        }else if(R.id.dialog_resume_conn_share_tv == vId){//联系方式--分享
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }
            shareToOtherPlant() ;
        }else if(R.id.dialog_resume_conn_buy_tv == vId){//联系方式--去购买
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }

            Intent toBtIt = new Intent(mContext ,ResumeBuyActivity.class) ;
            mActivity.startActivityForResult(toBtIt ,REQUEST_CODE_BUY_RESUME);

        }else if(R.id.dialog_resume_conn_phone_first_tv == vId){//联系方式--phone1
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }


        }else if(R.id.dialog_resume_conn_phone_second_tv == vId){//联系方式--phone2
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }


        }else if (R.id.acti_resume_dt_share_lay==vId){
            if(mConnDialog != null){
                mConnDialog.dismiss() ;
            }

            shareToOtherPlant() ;
        }
    }

    /**
     * 填充内容
     */
    private void updateJobDetails(){
        if(null == mDetails){
            showErrorPage() ;
        }else{
            mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
            if(mIsSelf){
                mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
            }

            String comImgUrl = mDetails.getUserHeadIcon() ;
            String comName = mDetails.getName() ;
            String money = mDetails.getApplySalary() ;
            String workName = mDetails.getPname();
            String sex = mDetails.getSex() ;
            String age = mDetails.getAge() ;
            String content = sex ;
            if(age != null && !"".equals(age)){
                content += (" / " + age) ;
            }
            mUserNameTv.setText(comName);
            mUserSexTv.setText(content);

            if(!"".equals(money)){
                mWorkMoneyTipsTv.setVisibility(View.VISIBLE);
                mWorkMoneyTv.setVisibility(View.VISIBLE);
                mWorkMoneyTv.setText("¥" + money);
            }else{
                mWorkMoneyTipsTv.setVisibility(View.INVISIBLE);
                mWorkMoneyTv.setVisibility(View.INVISIBLE);
            }

            if(!"".equals(workName)){
                mWorkNameTv.setVisibility(View.VISIBLE);
                mWorkNameTv.setText("求聘职位：" + workName) ;
            }else{
                mWorkNameTv.setVisibility(View.INVISIBLE);
            }

            GlideUtils.loaderUser(comImgUrl , mUserIv) ;

            String jobAddress = mDetails.getPosition() ;
            String workYear = StringUtils.convertWorkYearExp(mDetails.getWorkYears()) ;
            String education = mDetails.getEducation() ;
            mJobLocaTv.setText(jobAddress);
            mJobExpTv.setText(workYear);
            mJobEduTv.setText(education);

            String jobDt = mDetails.getSelfEvaluation() ;
            mUserDescTv.setText(jobDt);

            boolean isTypeAll = mDetails.isWorkAll() ;
            mWorkTypeTv.setText(mDetails.getWorkTypeName()) ;
            mWorkTypeTv.setTextColor(mContext.getResources().getColor(isTypeAll
                    ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
            mWorkTypeTv.setBackgroundDrawable(mContext.getResources().getDrawable(isTypeAll
                    ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

            //工作经历
            List<WorkExperience> experienceList = mDetails.getExperienceList() ;
            if(experienceList != null && experienceList.size() > 0){
                AdapterWorkExp mExpAdapter = new AdapterWorkExp(mContext ,experienceList) ;
                mExperLv.setAdapter(mExpAdapter) ;
            }

            //相关证书
            final List<ImageThumb> comPicList = mDetails.getAlbumsList() ;
            if(comPicList != null && comPicList.size() > 0){
                int widHei = (ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext , 15) * 2
                        - ResourceUtils.dip2px(mContext , 5) * 2) / 3 ;
                int size = comPicList.size() ;
                for(int x = 0 ; x < size ; x ++){
                    final int curPo = x ;
                    ImageThumb thumbInfo = comPicList.get(x) ;

                    if(thumbInfo != null){
                        ImageView iv = new ImageView(mContext) ;
                        iv.setLayoutParams(new ViewGroup.LayoutParams(widHei , widHei));
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        GlideUtils.loader(MyApplication.getInstance() , thumbInfo.getThumbImgUrl() , iv);

                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(mActivity ,comPicList ,curPo);
                            }
                        });

                        mCardLay.addView(iv) ;
                    }
                }
            }

            boolean isSupported =  mDetails.isLike() ;
            boolean isColection = mDetails.isCollection() ;
            mSupportIv.setImageDrawable(mContext.getResources().getDrawable(!isSupported
                    ? R.mipmap.ic_support_nomal
                    : R.mipmap.ic_support_checked));
            mCollectionIv.setImageDrawable(mContext.getResources().getDrawable(!isColection
                    ? R.mipmap.ic_collection_nomal
                    : R.mipmap.ic_collection_checked));
        }
    }

    /**
     * 联系他dialog
     */
    private void showConnDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss() ;
        }

        if(null == mContactInfo){
            ToastUtils.showToast(mContext ,"信息获取失败，请重试");
            return;
        }

        if(!mContactInfo.isNumber()){
            int reCount = mContactInfo.getSurplusNumber() ;
            String message = "请购买简历(剩余" + reCount + "次" ;
            if(reCount > 0){
                message = message + (",到期时间：" + mContactInfo.getExpiryTime()) ;
            }
            message = message + ")" ;

            DialogUtils.showCustomViewDialog(mContext, "温馨提示" ,message, null
                    ,"去购买", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    Intent toBtIt = new Intent(mContext ,ResumeBuyActivity.class) ;
                    mActivity.startActivityForResult(toBtIt ,REQUEST_CODE_BUY_RESUME);
                }
            },"暂不购买", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return ;
        }

        if(null == mConnDialog){
            mConnDialog = new Dialog(mContext,R.style.my_custom_dialog) ;
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_resume_connection ,null) ;
            mConnDialog.setContentView(view);

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.85F);

            mPhoneFirstTv = view.findViewById(R.id.dialog_resume_conn_phone_first_tv) ;
            mPhoneSecondTv = view.findViewById(R.id.dialog_resume_conn_phone_second_tv) ;
            mWeChatTv = view.findViewById(R.id.dialog_resume_conn_weChat_tv) ;
            mQQTv = view.findViewById(R.id.dialog_resume_conn_qq_tv) ;
            mRemainderCountTv = view.findViewById(R.id.dialog_resume_conn_count_tv) ;
            mEndTimeTv = view.findViewById(R.id.dialog_resume_conn_end_time_tv) ;
            TextView mCloseTv = view.findViewById(R.id.dialog_resume_conn_closet_tv) ;
            TextView mBuyTv = view.findViewById(R.id.dialog_resume_conn_buy_tv) ;
            TextView mShareTv = view.findViewById(R.id.dialog_resume_conn_share_tv) ;

            mPhoneFirstTv.setOnClickListener(this) ;
            mPhoneSecondTv.setOnClickListener(this) ;
            mBuyTv.setOnClickListener(this) ;
            mShareTv.setOnClickListener(this) ;
            mCloseTv.setOnClickListener(this) ;

            String phone1 = mContactInfo.getPhone1() ;
            String phone2 = mContactInfo.getPhone2() ;
            String weChat = mContactInfo.getWeChat() ;
            String qq = mContactInfo.getQq() ;
            String count = "" + mContactInfo.getSurplusNumber() ;
            String endTime = mContactInfo.getExpiryTime() ;

            mPhoneFirstTv.setText("手机1：" + phone1);
            mPhoneSecondTv.setText("手机2：" + phone2);
            mWeChatTv.setText("微信：" + weChat);
            mQQTv.setText("QQ：" + qq);
            mRemainderCountTv.setText(count);
            mEndTimeTv.setText("到期时间：" + endTime) ;
        }

        mConnDialog.show() ;
    }

    /**
     * 获取查看联系方式
     */
    private void getResumeContactInfo(){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在获取") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mResumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_CONTACT_INFO, paramMap
                , new RequestObjectCallBack<ResumeContactTa>("getResumeContactInfo",mContext ,ResumeContactTa.class) {
                    @Override
                    public void onSuccessResult(ResumeContactTa bean) {
                        mContactInfo = bean ;
                        showConnDialog() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showConnDialog();
                    }
                    @Override
                    public void onFail(Exception e) {
                        showConnDialog();
                    }
                });
    }

    /**
     * 获取职位详细
     */
    private void getResumeDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mResumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_DETAILS, paramMap
                , new RequestObjectCallBack<ResumeDetails>("getResumeDetails",mContext ,ResumeDetails.class) {
                    @Override
                    public void onSuccessResult(ResumeDetails bean) {
                        showSuccessPage();

                        mDetails = bean ;
                        updateJobDetails() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showErrorPage();
                    }
                    @Override
                    public void onFail(Exception e) {
                        showErrorPage();
                    }
                });
    }

    /**
     * 点赞
     */
    private void goSupport(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_SUPPORT) ;
            return ;
        }


        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mResumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_SUPPORT, false, paramMap
                , new RequestObjectCallBack<SupportResult>("goSupport" , mContext ,SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }

                        if(bean != null){
                            boolean isSupported = 2 == bean.AddOrDelete ;
                            mSupportIv.setImageDrawable(mContext.getResources().getDrawable(isSupported
                                    ? R.mipmap.ic_support_nomal
                                    : R.mipmap.ic_support_checked));
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 收藏
     */
    private void goCollection(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_COLLECTION) ;
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mResumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_COLLECTION, false, paramMap
                , new RequestObjectCallBack<CollectionResult>("goCollection" , mContext ,CollectionResult.class) {
                    @Override
                    public void onSuccessResult(CollectionResult bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }

                        if(bean != null){
                            boolean isCollected = 2 == bean.AddOrDelete ;
                            mCollectionIv.setImageDrawable(mContext.getResources().getDrawable(isCollected
                                    ? R.mipmap.ic_collection_nomal
                                    : R.mipmap.ic_collection_checked));
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SUPPORT == requestCode){
            if(RESULT_OK == resultCode){
                goSupport() ;
            }
        }else if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                goCollection() ;
            }
        }else if(REQUEST_CODE_CONNECTION == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    getResumeContactInfo() ;
                }
            }
        }else if(REQUEST_CODE_SEND_MSG == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    SkipUtils.toMessageChatActivity(mActivity ,mDetails.getHxName()) ;
                }
            }
        }else if(REQUEST_CODE_BUY_RESUME == requestCode){
            //暂定，只要从购买简历界面返回，都重新获取 剩余简历 状态
            mContactInfo = null ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getResumeDetails") ;
        NetUtils.cancelTag("goSupport") ;
        NetUtils.cancelTag("goCollection") ;

    }
}
