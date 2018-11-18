package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.BaseResultBean;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.JobDetails;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.SupportUser;
import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalImage;
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
import cn.idcby.jiajubang.view.MyCornerTextView;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.TopBarRightPopup;

public class JobDetailActivity extends BaseMoreStatusActivity {

    private ImageView mComIv ;
    private TextView mComNameTv ;
    private TextView mComDescTv ;//公司规模
    private TextView mComAddressTv ;//公司地址

    private TextView mJobNameTv ;
    private TextView mJobTypeTv ;
    private TextView mJobLocaTv ;
    private TextView mJobExpTv ;//经验
    private TextView mJobEduTv ;//学历

    private FlowLayout mFuliLay ;

    private TextView mJobDetailsTv ;
    private ImageView mJobDtMoreIv ;

    private RecyclerView mComDtPicLay ;
    private TextView mComDetailsTv ;
    private ImageView mComDtMoreIv ;

    private TextView mWorkAddressTv ;
    private FlowLayout mApplyLay ;

    private TextView mUserSupCountTv ;
    private TextView mUserSpCountTv ;
    private LinearLayout mUserSpUserLay ;
    private ImageView mUserSupMoreIv ;

    private ImageView mSupportIv ;
    private ImageView mCollectionIv ;

    private TextView mConnTv ;//联系他
    private TextView mSendTv ;//投递简历

    private String mJobId ;
    private JobDetails mDetails ;
    private boolean mIsSelf = false ;

    private LoadingDialog loadingDialog ;

    private static final int REQUEST_CODE_CONNECT = 1005 ;
    private View mShareLL;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    private List<ImageThumb> mThumbList = new ArrayList<>();
    private AdapterNomalImage mImgAdapter;


    @Override
    public void requestData() {
        showLoadingPage();

        getJobDetails() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_job_detail;
    }

    @Override
    public String setTitle() {
        return "招聘详情";
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
            String strTitle = mDetails.getCompanyName();
            String strUrl = mDetails.getH5Url();
            String strImgurl = mDetails.getCompanyLogoImage();
            String strSubscriotion = "";
            ShareUtils.shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion);
        }
    }

    @Override
    public void init() {
        mJobId = getIntent().getStringExtra(SkipUtils.INTENT_JOB_ID) ;

        if(null == mJobId){
            mJobId = "" ;
        }

        mComIv = findViewById(R.id.acti_job_details_com_iv) ;
        mComNameTv = findViewById(R.id.acti_job_details_com_name_tv) ;
        mComDescTv = findViewById(R.id.acti_job_details_com_desc_tv) ;
        mComAddressTv = findViewById(R.id.acti_job_details_com_address_tv) ;

        mJobNameTv = findViewById(R.id.acti_job_details_job_name_tv) ;
        mJobTypeTv = findViewById(R.id.acti_job_details_job_type_tv) ;
        mJobLocaTv = findViewById(R.id.acti_job_details_job_area_tv) ;
        mJobExpTv = findViewById(R.id.acti_job_details_job_exp_tv) ;
        mJobEduTv = findViewById(R.id.acti_job_details_job_edu_tv) ;

        mApplyLay = findViewById(R.id.acti_job_details_job_apply_lay) ;
        mFuliLay = findViewById(R.id.acti_job_dt_well_lay) ;

        mJobDetailsTv = findViewById(R.id.acti_job_details_job_content_tv) ;
        mJobDtMoreIv = findViewById(R.id.acti_job_dt_job_more_iv) ;

        mWorkAddressTv = findViewById(R.id.acti_job_details_address_tv) ;

        mComDtPicLay = findViewById(R.id.acti_job_dt_com_pic_lay) ;
        mComDetailsTv = findViewById(R.id.acti_job_details_com_content_tv) ;
        mComDtMoreIv = findViewById(R.id.acti_job_dt_com_more_iv) ;

        mUserSupCountTv = findViewById(R.id.acti_job_dt_user_support_count_tv) ;
        mUserSpCountTv = findViewById(R.id.acti_job_details_user_sp_count_tv) ;
        mUserSpUserLay = findViewById(R.id.acti_job_details_user_support_user_lay) ;
        mUserSupMoreIv = findViewById(R.id.acti_job_details_user_support_user_more_iv) ;

        LinearLayout mSupportLay = findViewById(R.id.acti_job_details_support_lay) ;
        mSupportIv = findViewById(R.id.acti_job_details_support_iv) ;
        LinearLayout mCollectionLay = findViewById(R.id.acti_job_details_collection_lay) ;
        mCollectionIv = findViewById(R.id.acti_job_details_collection_iv) ;

        mSendTv = findViewById(R.id.acti_job_details_send_tv) ;
        mConnTv = findViewById(R.id.acti_job_details_connection_tv) ;
        mShareLL = findViewById(R.id.acti_job_details_share_lay) ;

        mJobDtMoreIv.setOnClickListener(this);
        mComDtMoreIv.setOnClickListener(this);
        mUserSupMoreIv.setOnClickListener(this);
        mSupportLay.setOnClickListener(this);
        mCollectionLay.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        mConnTv.setOnClickListener(this);
        mShareLL.setOnClickListener(this);

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_job_details_connection_tv == vId){//联系TA
            if(!mIsSelf){
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CONNECT);
                }else{
                    String userHxId = mDetails.getHxName() ;
                    SkipUtils.toMessageChatActivity(mActivity,userHxId) ;
                }
            }
        }else if(R.id.acti_job_details_send_tv == vId){//投简历
            if(!mIsSelf){
                intentToChooseResume() ;
            }
        }else if(R.id.acti_job_details_support_lay == vId){//赞
            goSupport() ;
        }else if(R.id.acti_job_details_collection_lay == vId){//收藏
            goCollection() ;
        }else if(R.id.acti_job_dt_job_more_iv == vId){//职位描述--更多

        }else if(R.id.acti_job_dt_com_more_iv == vId){//公司描述--更多

        }else if(R.id.acti_job_details_user_support_user_more_iv == vId){//职友点赞--更多

        }else if(R.id.acti_job_details_share_lay == vId){//职友点赞--更多
            shareToOtherPlant();
        }
    }

    /**
     * 选择简历来投递
     */
    private void intentToChooseResume(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(JobDetailActivity.this , 10003) ;
        }else{
            Intent toCtIt = new Intent(mContext,ChooseResumeActivity.class) ;
            JobDetailActivity.this.startActivityForResult(toCtIt , 1004) ;
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

            String comImgUrl = mDetails.getCompanyLogoImage() ;
            String comName = mDetails.getCompanyName() ;
            String comType = mDetails.getCompanyType() ;
            String comScale = mDetails.getCompanyScale() ;
            String comAddress = mDetails.getAddress() ;
            mComNameTv.setText(comName);
            mComDescTv.setText(comType + " | " + comScale);
            mComAddressTv.setText(comAddress) ;
            GlideUtils.loader(MyApplication.getInstance(),comImgUrl , mComIv) ;

            String jobName = mDetails.getName() ;
            String jobAddress = mDetails.getCreareAddress() ;
            String workYear = StringUtils.convertWorkYearExp(mDetails.getWorkYears()) ;
            String education = mDetails.getEducation() ;
            mJobNameTv.setText(jobName);
            mJobLocaTv.setText(jobAddress);
            mJobExpTv.setText(workYear);
            mJobEduTv.setText(education);
            mWorkAddressTv.setText(mDetails.getWorkAddress()) ;

            boolean isTypeAll = mDetails.isWorkAll() ;
            mJobTypeTv.setText(mDetails.getWorkTypeName()) ;
            mJobTypeTv.setTextColor(mContext.getResources().getColor(isTypeAll
                    ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
            mJobTypeTv.setBackgroundDrawable(mContext.getResources().getDrawable(isTypeAll
                    ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

            //认证信息
            String applyText = mDetails.getAuthenticationText() ;

            if(mApplyLay.getChildCount() > 0){
                mApplyLay.removeAllViews() ;
            }

            if(applyText.contains(",")){
                String[] applyStates = applyText.split(",") ;
                for(String state : applyStates){
                    View itemLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item ,null) ;
                    ImageView icIv = itemLay.findViewById(R.id.dt_apply_icon_iv) ;
                    TextView nameTv = itemLay.findViewById(R.id.dt_apply_name_tv) ;

                    if(SkipUtils.APPLY_TYPE_PERSON_NO.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                        nameTv.setText(getResources().getString(R.string.apply_text_person_no)) ;
                    }else if(SkipUtils.APPLY_TYPE_PERSON.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                        nameTv.setText(getResources().getString(R.string.apply_text_person)) ;
                    }else if(SkipUtils.APPLY_TYPE_FACTORY.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.fangzi));
                        nameTv.setText(getResources().getString(R.string.apply_text_factory)) ;
                    }else if(SkipUtils.APPLY_TYPE_COMPANY.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.baoxiao));
                        nameTv.setText(getResources().getString(R.string.apply_text_company)) ;
                    }else if(SkipUtils.APPLY_TYPE_INSTALL.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.anzhuang));
                        nameTv.setText(getResources().getString(R.string.apply_text_install)) ;
                    }else if(SkipUtils.APPLY_TYPE_SERVER.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.gongsi));
                        nameTv.setText(getResources().getString(R.string.apply_text_server)) ;
                    }else if(SkipUtils.APPLY_TYPE_STORE.equals(state)){
                        icIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_apply_store));
                        nameTv.setText(getResources().getString(R.string.apply_text_store)) ;
                    }

                    mApplyLay.addView(itemLay) ;
                }
            }else{
                if(SkipUtils.APPLY_TYPE_PERSON.equals(applyText)
                        || SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)){//通过了个人认证
                    View perLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item ,null) ;
                    ImageView icIv = perLay.findViewById(R.id.dt_apply_icon_iv) ;
                    TextView nameTv = perLay.findViewById(R.id.dt_apply_name_tv) ;

                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                    if(SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)){
                        nameTv.setText(getResources().getString(R.string.apply_text_person_no)) ;
                    }else{
                        nameTv.setText(getResources().getString(R.string.apply_text_person)) ;
                    }
                    mApplyLay.addView(perLay) ;
                }
            }


            int flTvPadding = ResourceUtils.dip2px(mContext , 3) ;
            List<WelfareList> mWelfList = mDetails.getWelfareList() ;
            if(mWelfList != null && mWelfList.size() > 0){
                for(WelfareList welfare : mWelfList){
                    if(welfare != null){
                        MyCornerTextView tv = new MyCornerTextView(mContext) ;
                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setPadding(flTvPadding * 2 ,flTvPadding / 2 ,flTvPadding * 2,flTvPadding);
                        tv.setfilColor(Color.parseColor(welfare.getWelfareColorValue())).setCornerSize(flTvPadding);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,10) ;
                        tv.setTextColor(mContext.getResources().getColor(R.color.color_white)) ;
                        tv.setText(welfare.getWelfareText());

                        mFuliLay.addView(tv) ;
                    }
                }
            }

            String jobDt = mDetails.getPostDescription() ;
            mJobDetailsTv.setText(jobDt);

            String comDt = mDetails.getCompanyIntroduce() ;
            mComDetailsTv.setText(comDt);

            final List<ImageThumb> comPicList = mDetails.getAlbumsList() ;
            mThumbList.clear();
            mThumbList.addAll(comPicList) ;
            if(null == mImgAdapter){
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                        , ResourceUtils.dip2px(mContext ,5)
                        ,getResources().getDrawable(R.drawable.drawable_white_trans)) ;
                itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
                mComDtPicLay.setLayoutManager(layoutManager);
                mComDtPicLay.addItemDecoration(itemDecoration);
                mImgAdapter = new AdapterNomalImage(mActivity ,mThumbList) ;
                mComDtPicLay.setAdapter(mImgAdapter) ;
            }else{
                mImgAdapter.notifyDataSetChanged() ;
            }

            boolean isSupported = 1 == mDetails.getIsLike() ;
            boolean isColection = 1 == mDetails.getIsCollection() ;
            int supportCount = mDetails.getLikeNumber() ;
            mUserSupCountTv.setText("" + supportCount) ;
            mUserSpCountTv.setText("" + supportCount) ;
            mSupportIv.setImageDrawable(mContext.getResources().getDrawable(!isSupported
                    ? R.mipmap.ic_support_nomal
                    : R.mipmap.ic_support_checked));
            mCollectionIv.setImageDrawable(mContext.getResources().getDrawable(!isColection
                    ? R.mipmap.ic_collection_nomal
                    : R.mipmap.ic_collection_checked));

            List<SupportUser> mSupportList = mDetails.getLikeList() ;
            if(mSupportList != null && mSupportList.size() > 0){
                int size = mSupportList.size() ;
                if(size > 4){
                    size = 4 ;
                }

                int widHei = ResourceUtils.dip2px(mContext , 40);
                int imgPad = ResourceUtils.dip2px(mContext , 3);

                for(int x = 0 ; x < size ; x ++){
                    SupportUser thumbInfo = mSupportList.get(x) ;

                    if(thumbInfo != null){
                        ImageView iv = new ImageView(mContext) ;
                        iv.setLayoutParams(new ViewGroup.LayoutParams(widHei , widHei));
                        iv.setPadding(imgPad , imgPad ,imgPad ,imgPad);
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP) ;

                        GlideUtils.loaderUser(thumbInfo.getHeadIcon() , iv);

                        mUserSpUserLay.addView(iv) ;
                    }
                }
            }
        }
    }

    /**
     * 获取职位详细
     */
    private void getJobDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mJobId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_DETAILS, paramMap
                , new RequestObjectCallBack<JobDetails>("getJobDetails",mContext ,JobDetails.class) {
                    @Override
                    public void onSuccessResult(JobDetails bean) {
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
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(JobDetailActivity.this ,1001) ;
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mJobId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_SUPPORT, false, paramMap
                , new RequestObjectCallBack<SupportResult>("goSupport" , mContext ,SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }

                        if(bean != null){
                            int count = bean.LikeNumber ;
                            boolean isSupported = 2 == bean.AddOrDelete ;

                            mUserSupCountTv.setText("" + count) ;
                            mUserSpCountTv.setText("" + count) ;
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
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(JobDetailActivity.this ,1002) ;
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mJobId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_COLLECTION, false, paramMap
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


    /**
     * 投递简历
     */
    private void submitResumeSend(ResumeList resume){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("RecruitId" , mJobId) ;
        paramMap.put("ResumeId" , resume.getResumeId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_SEND, paramMap
                , new RequestObjectCallBack<BaseResultBean>("submitResumeSend" , mContext ,BaseResultBean.class) {
                    @Override
                    public void onSuccessResult(BaseResultBean bean) {
                        ToastUtils.showToast(mContext , "申请成功");
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showToast(mContext , "申请失败");
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(mContext , "申请失败");
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1001 == requestCode){
            if(RESULT_OK == resultCode){
                goSupport() ;
            }
        }else if(1002 == requestCode){
            if(RESULT_OK == resultCode){
                goCollection() ;
            }
        }else if(1003 == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = SPUtils.newIntance(mContext).getUserNumber().equals(mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    intentToChooseResume() ;
                }
            }
        }else if(1004 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ResumeList resumeList = (ResumeList) data.getSerializableExtra(SkipUtils.INTENT_RESUME_INFO);
                if(resumeList != null){
                    submitResumeSend(resumeList) ;
                }
            }
        }else if(REQUEST_CODE_CONNECT == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = SPUtils.newIntance(mContext).getUserNumber().equals(mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    String userHxId = mDetails.getHxName() ;
                    SkipUtils.toMessageChatActivity(mActivity,userHxId) ;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getJobDetails") ;
        NetUtils.cancelTag("goSupport") ;
        NetUtils.cancelTag("goCollection") ;

    }
}
