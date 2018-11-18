package cn.idcby.jiajubang.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.MyShareBean;
import cn.idcby.jiajubang.Bean.ReceiveOrderCount;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.HelperParentActivity;
import cn.idcby.jiajubang.activity.MoneyManagerActivity;
import cn.idcby.jiajubang.activity.MyApplyInfoActivity;
import cn.idcby.jiajubang.activity.MyCenterCircleActivity;
import cn.idcby.jiajubang.activity.MyCenterJobsActivity;
import cn.idcby.jiajubang.activity.MyCenterNeedActivity;
import cn.idcby.jiajubang.activity.MyCenterServerActivity;
import cn.idcby.jiajubang.activity.MyCenterStoreActivity;
import cn.idcby.jiajubang.activity.MyCenterUnuseActivity;
import cn.idcby.jiajubang.activity.MyCollectionActivity;
import cn.idcby.jiajubang.activity.MyCommentListActivity;
import cn.idcby.jiajubang.activity.MyFocusStoreActivity;
import cn.idcby.jiajubang.activity.MyFollowsActivity;
import cn.idcby.jiajubang.activity.MyOrderCenterAllActivity;
import cn.idcby.jiajubang.activity.MyQuestionListActivity;
import cn.idcby.jiajubang.activity.MySubscriptionActivity;
import cn.idcby.jiajubang.activity.OpenStoreActivity;
import cn.idcby.jiajubang.activity.SettingActivity;
import cn.idcby.jiajubang.activity.UserInfoActivity;
import cn.idcby.jiajubang.adapter.AdapterFragmentMe;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;


/**
 * 我的
 * 2018-06-04 13:13:28
 * 添加刷新效果，所以替换成 ListView ，顶部用header
 *
 * 2018-08-14 15:27:41
 * 添加广告，添加  我的评价
 */

public class MeFragment extends BaseFragment implements View.OnClickListener{
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv;

    //header相关

//    private String[] titles = {"我的订单", "我提供的"};
//    private MyIndicatorAdapter myIndicatorAdapter;
//    private IndicatorFragmentAdapter orderFragmentAdapter;
//
//    private List<Fragment> fragmentList;
//    private MagicIndicator magicIndicator;
//    private ViewPager mViewPager;

    private View mOrderFirstLay ;
    private TextView mOrderCountFirstTv ;
    private View mOrderSecondLay ;
    private TextView mOrderCountSecondTv ;
    private View mOrderThirdLay ;
    private TextView mOrderCountThirdTv ;
    private View mOrderFourthLay ;
    private TextView mOrderCountFourthTv ;
    private View mOrderFifthLay ;
    private TextView mOrderCountFifthTv ;

    private View mSettingLay;
    private ImageView mUserHeadIv;
    private TextView mUserNameTv;
    private View mUserVIv ;//大咖标志
    private TextView mIntegralTv;//帮值
    private TextView mMyApplyTipsTv;//我的认证tips

    private View mMyCollectionLay;//我的收藏
    private TextView mCollectionCountTv;
    private View mMyFocusLay;//我的关注
    private TextView mFocusCountTv;
    private View mMyFocusStoreLay;//我关注的店铺
    private TextView mFocusStoreCountTv;
    private View mMyFansLay;//我的粉丝
    private TextView mFansCountTv;

    private View mSelfMoneyView;//余额
    private View mBangMoneyView;//积分
    private View mPayBondsView;//保证金

    private View mZhigongLay;//厂家直供
    private View mJobsLay;//招聘
    private View mUnuseLay;//闲置
    private View mServerLay;//安装
    private View mCommentLay;//服务
    private View mNeedLay;//需求
    private View mQuestionLay ;//问答
    private View mCircleLay;//圈子
    private View mMySubsView;//我的订阅
    private View mMyOperateView;//经营分析
    private View mMyHelperView;//帮助与反馈
    private View mMyShareView;//分享推广

    private View mBannerLay ;
    private Banner mBanner ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>();

    private UserInfo mUserInfo;
    private boolean mIsInit = false;
    private boolean mIsRefresh = false ;

    private LoadingDialog mDialog;

    @Override
    protected void requestData() {
        loadPage.showSuccessPage();
    }

    @Override
    protected void initView(View view) {
        mIsInit = true;

        mRefreshLay = view.findViewById(R.id.frag_me_refresh_lay) ;
        mRv = view.findViewById(R.id.frag_me_lv) ;

        AdapterFragmentMe adatper = new AdapterFragmentMe() ;
        HeaderFooterAdapter<AdapterFragmentMe> mAdapter = new HeaderFooterAdapter<>(adatper) ;
        mRv.setLayoutManager(new LinearLayoutManager(mContext)) ;
        mRv.setAdapter(mAdapter) ;

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_fragment_me,null) ;
        View mTopBgIv = headerView.findViewById(R.id.frag_me_top_bg_iv);
        mSettingLay = headerView.findViewById(R.id.frag_me_user_set_tv);
        mUserHeadIv = headerView.findViewById(R.id.frag_me_user_icon_iv);
        mUserNameTv = headerView.findViewById(R.id.frag_me_user_name_tv);
        mUserVIv = headerView.findViewById(R.id.frag_me_user_v_iv);
        mIntegralTv = headerView.findViewById(R.id.frag_me_user_exp_tv);
        mMyApplyTipsTv = headerView.findViewById(R.id.frag_me_my_apply_tv);

        View mMoneyLay = headerView.findViewById(R.id.frag_me_money_lay) ;
        mMyCollectionLay = headerView.findViewById(R.id.frag_me_my_collection_lay);
        mCollectionCountTv = headerView.findViewById(R.id.frag_me_my_collection_count_tv);
        mMyFocusLay = headerView.findViewById(R.id.frag_me_my_focus_member_lay);
        mFocusCountTv = headerView.findViewById(R.id.frag_me_my_focus_member_count_tv);
        mMyFocusStoreLay = headerView.findViewById(R.id.frag_me_my_focus_store_lay);
        mFocusStoreCountTv = headerView.findViewById(R.id.frag_me_my_focus_store_count_tv);
        mMyFansLay = headerView.findViewById(R.id.frag_me_my_fans_lay);
        mFansCountTv = headerView.findViewById(R.id.frag_me_my_fans_count_tv);

        mSelfMoneyView = headerView.findViewById(R.id.frag_me_my_money_lay);
        mBangMoneyView = headerView.findViewById(R.id.frag_me_my_money_bang_lay);
        mPayBondsView = headerView.findViewById(R.id.frag_me_my_pay_bonds_lay);

        View mOpTopLay = headerView.findViewById(R.id.frag_me_option_top_lay);
        mZhigongLay = headerView.findViewById(R.id.frag_me_option_zhigong_lay);
        mJobsLay = headerView.findViewById(R.id.frag_me_option_jobs_lay);
        mUnuseLay = headerView.findViewById(R.id.frag_me_option_unuse_lay);
        mServerLay = headerView.findViewById(R.id.frag_me_option_server_lay);

        View mOpMidLay = headerView.findViewById(R.id.frag_me_option_mid_lay);
        mCommentLay = headerView.findViewById(R.id.frag_me_option_comment_lay);
        mNeedLay = headerView.findViewById(R.id.frag_me_option_needs_lay);
        mQuestionLay = headerView.findViewById(R.id.frag_me_option_question_lay);
        mCircleLay = headerView.findViewById(R.id.frag_me_option_circle_lay);

        View mOpBotLay = headerView.findViewById(R.id.frag_me_option_bottom_lay);
        mMySubsView = headerView.findViewById(R.id.frag_me_option_subscription_lay);
        mMyHelperView = headerView.findViewById(R.id.frag_me_option_help_lay);
        mMyShareView = headerView.findViewById(R.id.frag_me_option_share_lay);
        mMyOperateView = headerView.findViewById(R.id.frag_me_option_jinying_lay);

//        magicIndicator = headerView.findViewById(R.id.frag_me_order_indicator);
//        mViewPager = headerView.findViewById(R.id.frag_me_order_vp);

        mBannerLay = headerView.findViewById(R.id.frag_me_option_adv_lay) ;
        mBanner = headerView.findViewById(R.id.frag_me_option_adv_banner) ;

        View mOrderTv = headerView.findViewById(R.id.frag_me_order_tv) ;
        mOrderFirstLay = headerView.findViewById(R.id.frag_me_order_first_lay) ;
        mOrderCountFirstTv = headerView.findViewById(R.id.frag_me_order_first_count_tv) ;
        mOrderSecondLay = headerView.findViewById(R.id.frag_me_order_second_lay) ;
        mOrderCountSecondTv = headerView.findViewById(R.id.frag_me_order_second_count_tv) ;
        mOrderThirdLay = headerView.findViewById(R.id.frag_me_order_third_lay) ;
        mOrderCountThirdTv = headerView.findViewById(R.id.frag_me_order_third_count_tv) ;
        mOrderFourthLay = headerView.findViewById(R.id.frag_me_order_fourth_lay) ;
        mOrderCountFourthTv = headerView.findViewById(R.id.frag_me_order_fourth_count_tv) ;
        mOrderFifthLay = headerView.findViewById(R.id.frag_me_order_fifth_lay) ;
        mOrderCountFifthTv = headerView.findViewById(R.id.frag_me_order_fifth_count_tv) ;

        mOrderTv.setOnClickListener(this);
        mOrderFirstLay.setOnClickListener(this);
        mOrderSecondLay.setOnClickListener(this);
        mOrderThirdLay.setOnClickListener(this);
        mOrderFourthLay.setOnClickListener(this);
        mOrderFifthLay.setOnClickListener(this);

//        //设置高度，跟设计图宽高比一致，是否有必要！
//        int width = ResourceUtils.getScreenWidth(mContext) ;
//        //420 250
//        mTopBgIv.getLayoutParams().height = (int) (width / 1.68F) + ResourceUtils.dip2px(mContext ,15) ;
//        //400 75
//        mMoneyLay.getLayoutParams().height = (int) ((width - ResourceUtils.dip2px(mContext ,10) * 2) / 5.33F);
//        //400 80
////        mViewPager.getLayoutParams().height = (int) ((width - ResourceUtils.dip2px(mContext ,10) * 2) / 5F);
//
//        //400 115
//        mOpTopLay.getLayoutParams().height = (int) ((width - ResourceUtils.dip2px(mContext ,10) * 2) / 3.5F);
//        mOpMidLay.getLayoutParams().height = (int) ((width - ResourceUtils.dip2px(mContext ,10) * 2) / 3.5F);
//        mOpBotLay.getLayoutParams().height = (int) ((width - ResourceUtils.dip2px(mContext ,10) * 2) / 3.5F);

        mAdapter.addHeader(headerView) ;

        //设置banner参数
        int screenWidth = ResourceUtils.getScreenWidth(mContext) - ResourceUtils.dip2px(mContext ,34) ;
        mBannerLay.getLayoutParams().height = (int) (screenWidth / 5F) + ResourceUtils.dip2px(mContext ,5) ;

        //设置banner动画效果
        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR) ;
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
//                SkipUtils.intentToOtherByAdvId(mContext ,mTopBannerList.get(position - 1)) ;
                //暂定一张图，跳转到我要开店界面，后期可能变成轮播图，先不动
                goNextActivity(OpenStoreActivity.class) ;

            }
        });
        //设置轮播时间
        mBanner.setDelayTime(5000);
        mBanner.setImageLoader(new BannerImageLoader()) ;

//        initIndicator();
//        initViewPager();
    }

//    private void initIndicator() {
//        CommonNavigator commonNavigator = new CommonNavigator(mContext);
//        commonNavigator.setAdjustMode(false);
//        commonNavigator.setScrollPivotX(0.65f);
//        myIndicatorAdapter = new MyIndicatorAdapter(titles, mViewPager, LinePagerIndicator.MODE_WRAP_CONTENT);
//        commonNavigator.setAdapter(myIndicatorAdapter);
//        magicIndicator.setNavigator(commonNavigator);
//        ViewPagerHelper.bind(magicIndicator, mViewPager);
//    }
//
//    private void initViewPager() {
//        if (fragmentList == null) {
//            fragmentList = new ArrayList<>();
//        }
//
//        ServerOrderInMeFragment serverOrderInMeFragment = new ServerOrderInMeFragment();
//        MallOrderInMeFragment mallOrderInMeFragment = new MallOrderInMeFragment();
//        NeedOrderInMeFragment needOrderInMeFragment = new NeedOrderInMeFragment();
//        fragmentList.add(serverOrderInMeFragment);
//        fragmentList.add(mallOrderInMeFragment);
//        fragmentList.add(needOrderInMeFragment);
//        orderFragmentAdapter = new IndicatorFragmentAdapter(getChildFragmentManager(), fragmentList);
//        mViewPager.setAdapter(orderFragmentAdapter);
//
//        mViewPager.setCurrentItem(0);
//    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefresh = true ;

                getMyInfo();
            }
        });

        mSettingLay.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        mUserHeadIv.setOnClickListener(this);

        mMyApplyTipsTv.setOnClickListener(this);

        mMyCollectionLay.setOnClickListener(this);
        mMyFocusLay.setOnClickListener(this);
        mMyFocusStoreLay.setOnClickListener(this);
        mMyFansLay.setOnClickListener(this);

        mSelfMoneyView.setOnClickListener(this);
        mBangMoneyView.setOnClickListener(this);
        mPayBondsView.setOnClickListener(this);

        mZhigongLay.setOnClickListener(this);
        mJobsLay.setOnClickListener(this);
        mUnuseLay.setOnClickListener(this);
        mServerLay.setOnClickListener(this);
        mCommentLay.setOnClickListener(this);
        mNeedLay.setOnClickListener(this);
        mQuestionLay.setOnClickListener(this);
        mCircleLay.setOnClickListener(this);
        mMySubsView.setOnClickListener(this);
        mMyHelperView.setOnClickListener(this);
        mMyShareView.setOnClickListener(this);
        mMyOperateView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
         if (R.id.frag_me_user_icon_iv == vId
                || R.id.frag_me_user_name_tv == vId) {//个人信息

            goNextActivity(UserInfoActivity.class);

        }else if (R.id.frag_me_my_collection_lay == vId) {//我的收藏

            goNextActivity(MyCollectionActivity.class);

        } else if (R.id.frag_me_my_focus_member_lay == vId) {//关注的人

            Intent intent = new Intent(mContext, MyFollowsActivity.class);
            intent.putExtra(SkipUtils.INTENT_FOLLOWTYPE, 0);
            startActivity(intent);

        } else if (R.id.frag_me_my_fans_lay == vId) {//我的粉丝

            Intent intent = new Intent(mContext, MyFollowsActivity.class);
            intent.putExtra(SkipUtils.INTENT_FOLLOWTYPE, 1);
            startActivity(intent);

        } else if (R.id.frag_me_my_focus_store_lay == vId) {//我的关注的店铺

            goNextActivity(MyFocusStoreActivity.class);

        }else if (R.id.frag_me_my_apply_tv == vId) {//我的认证

            goNextActivity(MyApplyInfoActivity.class);

        }else if (R.id.frag_me_my_money_lay == vId) {//余额

             MoneyManagerActivity.launch(mContext ,0) ;

        } else if (R.id.frag_me_my_money_bang_lay == vId) {//我的积分

             MoneyManagerActivity.launch(mContext ,1) ;

        } else if (R.id.frag_me_my_pay_bonds_lay == vId) {//保证金

             MoneyManagerActivity.launch(mContext ,2) ;

         } else if (R.id.frag_me_user_set_tv == vId) {//设置

            goNextActivity(SettingActivity.class);

        }
        else if (R.id.frag_me_order_tv == vId) {//订单--我的订单

             MyOrderCenterAllActivity.launch(mContext ,0) ;

        }
        else if (R.id.frag_me_order_first_lay == vId) {//订单--待付款

             MyOrderCenterAllActivity.launch(mContext ,1) ;

        } else if (R.id.frag_me_order_second_lay == vId) {//订单--待发货

             MyOrderCenterAllActivity.launch(mContext ,2) ;

        } else if (R.id.frag_me_order_fifth_lay == vId) {//订单--待收货

             MyOrderCenterAllActivity.launch(mContext ,3) ;

         } else if (R.id.frag_me_order_third_lay == vId) {//订单--待评价

             MyOrderCenterAllActivity.launch(mContext ,4) ;

        } else if (R.id.frag_me_order_fourth_lay == vId) {//订单--售后

             MyOrderCenterAllActivity.launch(mContext ,5) ;

        } else if (R.id.frag_me_option_zhigong_lay == vId) {//厂家直供

             if(mUserInfo != null){
                 if(mUserInfo.isShop()){
                     Bundle storeBd = new Bundle() ;
                     storeBd.putString(SkipUtils.INTENT_STORE_ID ,mUserInfo.getShopId()) ;
                     goNextActivity(MyCenterStoreActivity.class,storeBd);
                 }else{
                     goNextActivity(OpenStoreActivity.class) ;
                 }

//                 //2018-05-30 15:27:41 改：暂时只跳转到开店，不再要直供商品商品
//                 goNextActivity(OpenStoreActivity.class) ;
             }

        }else if (R.id.frag_me_option_jobs_lay == vId) {//行业招聘

            goNextActivity(MyCenterJobsActivity.class);

        } else if (R.id.frag_me_option_unuse_lay == vId) {//bg_index_hyxz

            goNextActivity(MyCenterUnuseActivity.class) ;

        } else if (R.id.frag_me_option_server_lay == vId) {//我的服务

             if(MyApplication.isServerHidden()){
                 DialogUtils.showCustomViewDialog(mContext,
                         getResources().getString(R.string.server_hidden_tips)
                         , "确定", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 dialogInterface.dismiss();
                             }
                         });

                 return ;
             }
             goNextActivity(MyCenterServerActivity.class) ;

        } else if (R.id.frag_me_option_comment_lay == vId) {//我的评价

            goNextActivity(MyCommentListActivity.class) ;

        } else if (R.id.frag_me_option_needs_lay == vId) {//最新需求

            goNextActivity(MyCenterNeedActivity.class);

        } else if (R.id.frag_me_option_question_lay == vId) {//bg_index_hywd

            goNextActivity(MyQuestionListActivity.class);

        } else if (R.id.frag_me_option_circle_lay == vId) {//我的圈子

            goNextActivity(MyCenterCircleActivity.class);

        } else if (R.id.frag_me_option_subscription_lay == vId) {//我的订阅

            goNextActivity(MySubscriptionActivity.class);

        }else if (R.id.frag_me_option_help_lay == vId) {//帮助与反馈

            goNextActivity(HelperParentActivity.class);

        } else if (R.id.frag_me_option_share_lay == vId) {//分享推广

            getShareUrlInfo();
        } else if (R.id.frag_me_option_jinying_lay == vId) {//经营分析

//            goNextActivity(MyOperateActivity.class);

        }
    }

    /**
     * 填充订单数量
     */
    private void updateOrderCount(ReceiveOrderCount orderInfo){
        if(null == orderInfo){
            return ;
        }

        int waitPay = orderInfo.getWaitPay() ;
        int waitSend = orderInfo.getWaitSend() ;
        int waitReceive = orderInfo.getWaitReceive() ;
        int waitEvaluate = orderInfo.getWaitEvaluate() ;
        int afterSale = orderInfo.getAfterSale() ;

        mOrderCountFirstTv.setText("" + waitPay) ;
        mOrderCountSecondTv.setText("" + waitSend) ;
        mOrderCountThirdTv.setText("" + waitEvaluate) ;
        mOrderCountFourthTv.setText("" + afterSale) ;
        mOrderCountFifthTv.setText("" + waitReceive) ;
        mOrderCountFirstTv.setVisibility(waitPay > 0 ? View.VISIBLE : View.GONE) ;
        mOrderCountSecondTv.setVisibility(waitSend > 0 ? View.VISIBLE : View.GONE) ;
        mOrderCountThirdTv.setVisibility(waitEvaluate > 0 ? View.VISIBLE : View.GONE) ;
        mOrderCountFourthTv.setVisibility(afterSale > 0 ? View.VISIBLE : View.GONE) ;
        mOrderCountFifthTv.setVisibility(waitReceive > 0 ? View.VISIBLE : View.GONE) ;
    }

    /**
     * 填充数据
     */
    private void updateDisplay() {
        LoginHelper.saveUserInfoToLocal(mContext, mUserInfo);

        String name = mUserInfo.getNickName();
        String headIconUrl = mUserInfo.getHeadIcon();
        //帮币/积分
        String integral = mUserInfo.getIntegral();
        mUserNameTv.setText(name);
        mIntegralTv.setText(integral);
        GlideUtils.loaderRound(headIconUrl ,mUserHeadIv,3);

        int collecCount = mUserInfo.getCollectNumber();
        int followMan = mUserInfo.getFollowMan();
        int followShop = mUserInfo.getFollowShop();
        int followFans = mUserInfo.getFollowfans();
        mCollectionCountTv.setText(collecCount + "");
        mFocusCountTv.setText(followMan + "");
        mFocusStoreCountTv.setText(followShop + "");
        mFansCountTv.setText(followFans + "");

        mUserVIv.setVisibility(mUserInfo.isIndusV() ? View.VISIBLE : View.INVISIBLE) ;
    }

    /**
     * 获取个人信息
     */
    private void getMyInfo() {
        if (LoginHelper.isNotLogin(mContext)) {
            mRefreshLay.finishRefresh() ;
            EventBus.getDefault().post(new BusEvent.LoginOutEvent(true));
            return;
        }
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo", mContext, UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if (bean != null) {
                            mUserInfo = bean;
                            updateDisplay();
                        }

                        if(mIsRefresh){
                            getOrderCount() ;
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefresh){
                            getOrderCount() ;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefresh){
                            getOrderCount() ;
                        }
                    }
                });
    }

    /**
     * 获取分享推广链接
     */
    private void getShareUrlInfo() {
        if (null == mDialog) {
            mDialog = new LoadingDialog(mContext);
            mDialog.setCancelable(false);
        }
        mDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SHARE_URL, paramMap
                , new RequestObjectCallBack<MyShareBean>("getShareUrlInfo", mContext, MyShareBean.class) {
                    @Override
                    public void onSuccessResult(MyShareBean bean) {
                        mDialog.dismiss();

                        if (bean != null) {
                            SkipUtils.toShowWebShareActivity(mContext, bean.getTitle(), bean.getH5Url(), bean.getImgUrl());
                        } else {
                            ToastUtils.showToast(mContext, "分享内容有误，请稍后重试");
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext, "分享内容有误，请稍后重试");
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext, "分享内容有误，请稍后重试");
                    }
                });
    }

    /**
     * 获取订单数量
     */
    private void getOrderCount(){
        if(LoginHelper.isNotLogin(mContext)){
            return;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RECEIVE_ORDER_COUNT, paramMap
                , new RequestObjectCallBack<ReceiveOrderCount>("getOrderCount" ,mContext ,ReceiveOrderCount.class) {
                    @Override
                    public void onSuccessResult(ReceiveOrderCount bean) {
                        updateOrderCount(bean) ;

                        if(mIsRefresh){
                            requestMidBanner() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefresh){
                            requestMidBanner() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefresh){
                            requestMidBanner() ;
                        }
                    }
                });
    }


    /**
     * banner
     */
    private void requestMidBanner() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "AppMyMiddle");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("requestMidBanner", mContext, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;
                        List<String> imageUrl = new ArrayList<>(mTopBannerList.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }
                        mBanner.update(imageUrl) ;

                        ViewUtil.setViewVisible(mBannerLay,imageUrl.size() > 0);

                        if(mIsRefresh){
                            mIsRefresh = false ;

                            mRefreshLay.finishRefresh() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefresh){
                            mIsRefresh = false ;

                            mRefreshLay.finishRefresh() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefresh){
                            mIsRefresh = false ;

                            mRefreshLay.finishRefresh() ;
                        }
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && mIsInit) {
            getMyInfo();
            getOrderCount();

            if(mTopBannerList.size() == 0){
                requestMidBanner();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsInit && !isHidden()) {
            getMyInfo();
            getOrderCount();

            if(mTopBannerList.size() == 0){
                requestMidBanner();
            }
        }
    }

}
