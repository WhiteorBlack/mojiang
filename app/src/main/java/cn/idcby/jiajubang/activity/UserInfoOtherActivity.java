package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 用户信息
 * Created on 2018/5/6.
 *  不展示QQ、微信、邮箱
 *
 *  2018-05-08 14:14:16
 *  添加认证状态展示、联系TA、关注
 *
 *
 *  2018-06-27 17:08:24
 *  重写了其他人信息界面，这个界面用来展示TA的简介
 *  ，所以可以直接传UserInfo实体类过来，没必要再调用接口了
 */

public class UserInfoOtherActivity extends BaseActivity{
    private ImageView mUserIv ;
    private TextView mNickNameEv ;
    private TextView mSexTv ;
    private TextView mBirthdayTv ;
    private TextView mAreaTv ;
    private TextView mDescEv ;
    private TextView mWorkPostTv ;
    private TextView mWorkNameTv ;
    private TextView mCompanyNameTv ;

    private View mApplyCompanyView ;
    private View mApplyFactoryView ;
    private View mApplyServerView ;
    private View mApplyInstallView ;
    private View mApplyStoreView ;

//    private TextView mFocusTv ;
//    private View mConnectionTv ;

//    private LoadingDialog mDialog ;
//
//    private String mUserId ;
//    private String mUserHxName ;
    private UserInfo mUserInfo ;

//    private static final int REQUEST_CODE_CONNECTION = 1000 ;
//    private static final int REQUEST_CODE_FOCUS = 1001 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_info_other;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        mUserInfo = (UserInfo) getIntent().getSerializableExtra(SkipUtils.INTENT_USER_INFO);

//        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID);
//        mUserHxName = getIntent().getStringExtra(SkipUtils.INTENT_USER_HX_ID);

        View topLay = findViewById(R.id.acti_user_info_other_head_lay) ;
        topLay.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext) / 2.1F);

        mUserIv = findViewById(R.id.acti_user_info_other_head_iv) ;
        mNickNameEv = findViewById(R.id.acti_user_info_other_nickName_ev) ;
        mSexTv = findViewById(R.id.acti_user_info_other_sex_tv) ;
        mBirthdayTv = findViewById(R.id.acti_user_info_other_birthday_tv) ;
        mAreaTv = findViewById(R.id.acti_user_info_other_area_tv) ;
        mDescEv = findViewById(R.id.acti_user_info_other_desc_ev) ;
        mWorkNameTv = findViewById(R.id.acti_user_info_other_work_tv) ;
        mWorkPostTv= findViewById(R.id.acti_user_info_other_post_tv) ;
        mCompanyNameTv = findViewById(R.id.acti_user_info_other_company_tv) ;

        mApplyCompanyView = findViewById(R.id.acti_user_info_other_apply_company_lay) ;
        mApplyFactoryView = findViewById(R.id.acti_user_info_other_apply_factory_lay) ;
        mApplyServerView = findViewById(R.id.acti_user_info_other_apply_server_lay) ;
        mApplyInstallView = findViewById(R.id.acti_user_info_other_apply_install_lay) ;
        mApplyStoreView = findViewById(R.id.acti_user_info_other_apply_shop_lay) ;
        mApplyCompanyView.setOnClickListener(this);
        mApplyFactoryView.setOnClickListener(this);
        mApplyServerView.setOnClickListener(this);
        mApplyInstallView.setOnClickListener(this);
        mApplyStoreView.setOnClickListener(this);
        mUserIv.setOnClickListener(this);

//        mFocusTv = findViewById(R.id.acti_user_info_other_focus_tv) ;
//        mConnectionTv = findViewById(R.id.acti_user_info_other_connection_tv) ;
//        mConnectionTv.setOnClickListener(this);
//        mFocusTv.setOnClickListener(this);

        updateDisplay() ;

    }

    @Override
    public void initData() {
//        getUserInfo() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_user_info_other_apply_company_lay == vId){
            toApplyInfoActivity(0) ;
        }else if(R.id.acti_user_info_other_apply_factory_lay == vId){
            toApplyInfoActivity(1) ;
        }else if(R.id.acti_user_info_other_apply_server_lay == vId){
            toApplyInfoActivity(4) ;
        }else if(R.id.acti_user_info_other_apply_install_lay == vId){
            toApplyInfoActivity(3) ;
        }else if(R.id.acti_user_info_other_apply_shop_lay == vId){
            toApplyInfoActivity(2) ;
        }
//        else if(R.id.acti_user_info_other_focus_tv == vId){
//            changeFocusState() ;
//        }else if(R.id.acti_user_info_other_connection_tv == vId){
//            toHxConnection() ;
//        }
        else if(R.id.acti_user_info_other_head_iv == vId){
            if(mUserInfo != null && !"".equals(mUserInfo.getHeadIcon())){
                SkipUtils.toImageShowActivity(mActivity ,mUserInfo.getHeadIcon(),0) ;
            }
        }
    }


    private void toApplyInfoActivity(int type){
        Intent toAiIt = new Intent() ;
        if(0 == type){
            toAiIt.setClass(mContext ,CompanyApplyActivity.class) ;
        }else if(1 == type){
            toAiIt.setClass(mContext ,FactoryApplyActivity.class) ;
        }else if(2 == type){
            toAiIt.setClass(mContext ,OpenStoreActivity.class) ;
        }else if(3 == type){
            toAiIt.setClass(mContext ,InstallApplyActivity.class) ;
        }else if(4 == type){
            toAiIt.setClass(mContext ,ServerApplyActivity.class) ;
        }

        toAiIt.putExtra(SkipUtils.INTENT_USER_ID ,mUserInfo.getUserId()) ;
        startActivity(toAiIt) ;
    }

//    /**
//     * 联系TA
//     */
//    private void toHxConnection(){
//        if(mUserInfo != null){
//            if(LoginHelper.isNotLogin(mContext)){
//                SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CONNECTION);
//            }else{
//                SkipUtils.toMessageChatActivity(mActivity ,mUserInfo.getHxName()) ;
//            }
//        }
//    }

    /**
     * 填充数据
     */
    private void updateDisplay(){
//        if(mDialog != null){
//            mDialog.dismiss() ;
//        }

        if(null == mUserInfo){
            DialogUtils.showCustomViewDialog(mContext, "获取信息失败"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }
//
//        if(null == mUserId){
//            mUserId = mUserInfo.getUserId() ;
//        }

        String headUrl = mUserInfo.getHeadIcon() ;
        String nickName = mUserInfo.getNickName() ;
        String gender = mUserInfo.getGender() ;//1男2女
        if("2".equals(gender)){
            mSexTv.setText("女");
        }else if("1".equals(gender)){
            mSexTv.setText("男");
        }
        String areaProvince = mUserInfo.getProvinceName() ;
        String areaCity = mUserInfo.getCityName() ;
        String areaQu = mUserInfo.getCountyName() ;
        String birthday = mUserInfo.getBirthday() ;
        String desc = mUserInfo.getPersonalitySignature() ;
        String postName = mUserInfo.getPostText() ;
        String workName = mUserInfo.getIndustryNames() ;
        String companyName = mUserInfo.getCompanyName() ;

        GlideUtils.loaderRound(headUrl ,mUserIv,3);
        mNickNameEv.setText(StringUtils.convertNull(nickName));

        mBirthdayTv.setText(birthday);
        mAreaTv.setText(StringUtils.convertNull(areaProvince)
                + StringUtils.convertNull(areaCity)
                + StringUtils.convertNull(areaQu));
        mDescEv.setText(StringUtils.convertNull(desc)) ;

        mWorkNameTv.setText(StringUtils.convertNull(postName)) ;
        mWorkPostTv.setText(StringUtils.convertNull(workName)) ;
        mCompanyNameTv.setText(StringUtils.convertNull(companyName)) ;

        boolean isCompany = mUserInfo.isCompany() ;
        boolean isFactory = mUserInfo.isFactory() ;
        boolean isServer = mUserInfo.isServices() ;
        boolean isInstall = mUserInfo.isInstall() ;
        boolean isShop = mUserInfo.isShop() ;

        if(isCompany){
            mApplyCompanyView.setVisibility(View.VISIBLE);
        }
        if(isFactory){
            mApplyFactoryView.setVisibility(View.VISIBLE);
        }
        if(isServer){
            mApplyServerView.setVisibility(View.VISIBLE);
        }
        if(isInstall){
            mApplyInstallView.setVisibility(View.VISIBLE);
        }
        if(isShop){
            mApplyStoreView.setVisibility(View.VISIBLE);
        }

//        if(!LoginHelper.isSelf(mContext ,mUserId)){
//            mFocusTv.setVisibility(View.VISIBLE);
//            mConnectionTv.setVisibility(View.VISIBLE);
//
//            boolean isFollow = mUserInfo.isFollow() ;
//            changeFocusTextStyle(isFollow) ;
//        }
    }
//
//    private void changeFocusTextStyle(boolean follow){
//        mFocusTv.setText(follow ? "已关注" : "关注");
////        mFocusTv.setTextColor(getResources().getColor(follow
////                ? R.color.color_nomal_text : R.color.color_white));
//        mFocusTv.setBackgroundDrawable(getResources().getDrawable(follow
//                ? R.drawable.round_grey_88_bg : R.drawable.round_theme_small_bg));
//    }
//
//    /**
//     * 获取信息
//     */
//    private void getUserInfo(){
//        if(null == mDialog){
//            mDialog = new LoadingDialog(mContext) ;
//        }
//        mDialog.show() ;
//
//        String ID ;
//        String Code ;
//        if(mUserId != null){
//            ID = "2" ;
//            Code = mUserId ;
//        }else{
//            ID = "1" ;
//            Code = mUserHxName ;
//        }
//
//        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
//        paramMap.put("ID" ,ID) ;
//        paramMap.put("Code" ,StringUtils.convertNull(Code)) ;
//        NetUtils.getDataFromServerByPost(mContext, Urls.USER_HX_INFO, paramMap
//                , new RequestObjectCallBack<UserInfo>("getUserInfos" ,mContext ,UserInfo.class) {
//                    @Override
//                    public void onSuccessResult(UserInfo bean) {
//                        if(bean != null){
//                            mUserInfo = bean ;
//                        }
//                        updateDisplay() ;
//                    }
//                    @Override
//                    public void onErrorResult(String str) {
//                        updateDisplay() ;
//                    }
//                    @Override
//                    public void onFail(Exception e) {
//                        updateDisplay() ;
//                    }
//                });
//    }
//
//
//    /**
//     * 关注、取消关注
//     */
//    private void changeFocusState(){
//        if(LoginHelper.isNotLogin(mContext)){
//            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_FOCUS);
//            return ;
//        }
//
//        if (mDialog == null)
//            mDialog = new LoadingDialog(mContext);
//        mDialog.show();
//
//        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
//        paramMap.put("FollowType" , "1") ;
//        paramMap.put("ResourceId" , StringUtils.convertNull(mUserId)) ;
//
//        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
//                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
//                    @Override
//                    public void onSuccessResult(FocusResult bean) {
//                        if(mDialog != null){
//                            mDialog.dismiss();
//                        }
//
//                        //1add
//                        if(bean != null){
//                            boolean isFocus = bean.AddOrDelete == 1 ;
//                            changeFocusTextStyle(isFocus) ;
//                        }
//                    }
//                    @Override
//                    public void onErrorResult(String str) {
//                        if(mDialog != null){
//                            mDialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(Exception e) {
//                        if(mDialog != null){
//                            mDialog.dismiss();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(REQUEST_CODE_FOCUS == requestCode){
//            if(RESULT_OK == resultCode){
//                changeFocusState() ;
//            }
//        }else if(REQUEST_CODE_CONNECTION == requestCode){
//            if(RESULT_OK == resultCode){
//                toHxConnection() ;
//            }
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUserInfos");
    }
}
