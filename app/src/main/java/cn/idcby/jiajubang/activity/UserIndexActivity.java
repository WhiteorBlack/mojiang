package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.IndicatorFragmentAdapter;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.UserSendFragment;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/6/27.
 *
 * 2018-09-14 17:29:20
 * 添加行业大咖标志
 */

public class UserIndexActivity extends BaseActivity {
    private MagicIndicator magicIndicator;
    private ViewPager mViewPager;

    private View mStoreTv ;
    private View mStoreIv ;

    private ImageView mUserIv ;
    private TextView mNickNameTv;
    private View mUserVIv ;
    private TextView mFocusTv ;
    private TextView mFocusCountTv ;
    private TextView mFansCountTv ;
    private View mConnectionTv ;

    private String mUserId ;
    private String mUserHxName ;
    private UserInfo mUserInfo ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_CONNECTION = 1000 ;
    private static final int REQUEST_CODE_FOCUS = 1001 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_user_index;
    }

    @Override
    public void initView() {
        super.initView();

        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID) ;
        mUserHxName = getIntent().getStringExtra(SkipUtils.INTENT_USER_HX_ID);

        mDialog = new LoadingDialog(mContext) ;
        mDialog.setCancelable(false);

        View backTv = findViewById(R.id.acti_user_index_back_tv) ;
        mStoreTv = findViewById(R.id.acti_user_index_store_tv) ;
        mStoreIv = findViewById(R.id.acti_user_index_store_iv) ;
        mUserIv = findViewById(R.id.acti_user_index_user_iv) ;
        mUserVIv = findViewById(R.id.acti_user_index_user_v_iv) ;
        mNickNameTv = findViewById(R.id.acti_user_index_user_name_tv) ;
        mFocusTv = findViewById(R.id.acti_user_index_focus_tv) ;
        mFocusCountTv = findViewById(R.id.acti_user_index_focus_count_tv) ;
        mFansCountTv = findViewById(R.id.acti_user_index_fans_count_tv) ;
        View mDescTv = findViewById(R.id.acti_user_index_info_tv) ;
        mConnectionTv = findViewById(R.id.acti_user_index_bot_connection_tv) ;
        backTv.setOnClickListener(this);
        mDescTv.setOnClickListener(this);
        mUserIv.setOnClickListener(this);
        mConnectionTv.setOnClickListener(this);
        mStoreTv.setOnClickListener(this);
        mStoreIv.setOnClickListener(this);
        mFocusTv.setOnClickListener(this);
        mFansCountTv.setOnClickListener(this);
        mFocusCountTv.setOnClickListener(this);

        magicIndicator = findViewById(R.id.acti_user_index_nav_indicator) ;
        mViewPager = findViewById(R.id.acti_user_index_vp) ;

        getUserInfo() ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_user_index_focus_tv == vId){
            changeFocusState() ;
        }else if(R.id.acti_user_index_back_tv == vId){
            finish() ;
        }else if(R.id.acti_user_index_bot_connection_tv == vId){
            toHxConnection() ;
        }else if(R.id.acti_user_index_user_iv == vId){
            if(mUserInfo != null && !"".equals(mUserInfo.getHeadIcon())){
                SkipUtils.toImageShowActivity(mActivity ,mUserInfo.getHeadIcon(),0) ;
            }
        }else if(R.id.acti_user_index_info_tv == vId){
            Intent toDiIt = new Intent(mContext ,UserInfoOtherActivity.class) ;
            toDiIt.putExtra(SkipUtils.INTENT_USER_INFO,mUserInfo) ;
            startActivity(toDiIt) ;
        }else if(R.id.acti_user_index_store_iv == vId
                || R.id.acti_user_index_store_tv == vId){
            if(!"".equals(mUserInfo.getShopId())){
                SkipUtils.toStoreIndexActivity(mContext ,mUserInfo.getShopId()) ;
            }
        }
    }


    private void initIndicator() {
        String[] titles = {"闲置", "需求", "招聘", "圈子", "问答"} ;

        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        magicIndicator.setNavigator(commonNavigator);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(UserSendFragment.getInstance(mUserId,UserSendFragment.SEND_TYPE_UNUSE)) ;
        fragmentList.add(UserSendFragment.getInstance(mUserId,UserSendFragment.SEND_TYPE_NEEDS)) ;
        fragmentList.add(UserSendFragment.getInstance(mUserId,UserSendFragment.SEND_TYPE_JOBS)) ;
        fragmentList.add(UserSendFragment.getInstance(mUserId,UserSendFragment.SEND_TYPE_CIRCLE)) ;
        fragmentList.add(UserSendFragment.getInstance(mUserId,UserSendFragment.SEND_TYPE_QUESTION)) ;

        IndicatorFragmentAdapter orderFragmentAdapter = new IndicatorFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(orderFragmentAdapter);
        mViewPager.setOffscreenPageLimit(titles.length) ;
        ViewPagerHelper.bind(magicIndicator, mViewPager);
        mViewPager.setCurrentItem(0) ;
    }


    /**
     * 填充数据
     */
    private void updateDisplay(){
        if(mDialog != null){
            mDialog.dismiss() ;
        }

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

        if(null == mUserId){
            mUserId = mUserInfo.getUserId() ;
        }

        initIndicator() ;

        String headUrl = mUserInfo.getHeadIcon() ;
        String nickName = mUserInfo.getNickName() ;
        int fansCount = mUserInfo.getFollowfans() ;
        int focusCount = mUserInfo.getFollowMan() ;

        GlideUtils.loaderRound(headUrl ,mUserIv,3);
        mNickNameTv.setText(StringUtils.convertNull(nickName));
        mFocusCountTv.setText("关注 " + focusCount);
        boolean isFollow = mUserInfo.isFollow() ;
        changeFocusTextStyle(isFollow,fansCount) ;

        if(!"".equals(StringUtils.convertNull(mUserInfo.getShopId()))){
            mStoreIv.setVisibility(View.VISIBLE);
            mStoreTv.setVisibility(View.VISIBLE);
        }

        if(!LoginHelper.isSelf(mContext ,mUserId)){
            mFocusTv.setVisibility(View.VISIBLE);
            mConnectionTv.setVisibility(View.VISIBLE);
        }

        mUserVIv.setVisibility(mUserInfo.isIndusV() ? View.VISIBLE : View.INVISIBLE) ;
    }

    private void changeFocusTextStyle(boolean follow,int fansCount){
        mFansCountTv.setText("粉丝 " + fansCount);
        mFocusTv.setText(follow ? "已关注" : "关注");
        mFocusTv.setBackgroundDrawable(getResources().getDrawable(follow
                ? R.drawable.round_grey_big_bg : R.drawable.round_theme_bg));
    }

    /**
     * 联系TA
     */
    private void toHxConnection(){
        if(mUserInfo != null){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CONNECTION);
            }else{
                SkipUtils.toMessageChatActivity(mActivity ,mUserInfo.getHxName()) ;
            }
        }
    }

    /**
     * 获取信息
     */
    private void getUserInfo(){
        mDialog.show() ;

        String ID ;
        String Code ;
        if(mUserId != null){
            ID = "2" ;
            Code = mUserId ;
        }else{
            ID = "1" ;
            Code = mUserHxName ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,ID) ;
        paramMap.put("Code" , StringUtils.convertNull(Code)) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.USER_HX_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getUserInfos" ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if(bean != null){
                            mUserInfo = bean ;
                        }
                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                    }
                });
    }

    /**
     * 关注、取消关注
     */
    private void changeFocusState(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_FOCUS);
            return ;
        }

        if (mDialog == null)
            mDialog = new LoadingDialog(mContext);
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("FollowType" , "1") ;
        paramMap.put("ResourceId" , StringUtils.convertNull(mUserId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }

                        //1add
                        if(bean != null){
                            boolean isFocus = bean.AddOrDelete == 1 ;
                            changeFocusTextStyle(isFocus,bean.Number) ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOCUS == requestCode){
            if(RESULT_OK == resultCode){
                boolean isSelf = LoginHelper.isSelf(mContext ,mUserId) ;
                mFocusTv.setVisibility(isSelf ? View.INVISIBLE : View.VISIBLE);
                mConnectionTv.setVisibility(isSelf ? View.INVISIBLE : View.VISIBLE);

                changeFocusState() ;
            }
        }else if(REQUEST_CODE_CONNECTION == requestCode){
            if(RESULT_OK == resultCode){
                boolean isSelf = LoginHelper.isSelf(mContext ,mUserId) ;
                mFocusTv.setVisibility(isSelf ? View.INVISIBLE : View.VISIBLE);
                mConnectionTv.setVisibility(isSelf ? View.INVISIBLE : View.VISIBLE);

                if(!isSelf){
                    toHxConnection() ;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUserInfos");
    }

}
