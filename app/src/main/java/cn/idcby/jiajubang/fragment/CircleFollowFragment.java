package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.TopicList;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.Bean.UserVic;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleTransportActivity;
import cn.idcby.jiajubang.activity.TopicHotActivity;
import cn.idcby.jiajubang.activity.VicUserActivity;
import cn.idcby.jiajubang.adapter.AdapterCircleActive;
import cn.idcby.jiajubang.adapter.PagerAdapterHotTopic;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.ZoomOutPageTransformer;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 圈子--关注
 * 2018-05-10 19:54:10
 * 暂时隐藏行业大咖
 *
 * 2018-05-30 16:32:25
 * 显示行业大咖，调整接口了
 *
 * 2018-08-14 14:49:57
 * 隐藏行业大咖
 *
 * 2018-09-11 15:15:04
 * 显示行业大咖，并且在圈子列表上，头像加上V，大咖标志
 */

public class CircleFollowFragment extends BaseFragment {
    private MaterialRefreshLayout mRefreshLay ;

    //热门话题
    private List<TopicList> mHotTopicList = new ArrayList<>() ;
    private ViewPager mHotVp ;
    private PagerAdapterHotTopic mHotTopicAdapter ;

    private Timer ggTimer ;
    private TimerTask ggTimerTask ;
    private boolean isStarted = false ;//是否已将开始播放广告了
    private int mCurIndex = 0 ;
    private boolean isPagerTouched = false ;//是否触摸了，如果true就暂停自动轮播
    private static final int MSG_CHANGE_PHOTO = 1;//handler msg.what
    private static final int PHOTO_CHANGE_TIME = 5000;// 图片自动切换时间

    //行业大咖
    private FrameLayout mFocusListLay ;
    private List<UserVic> mUserVicList = new ArrayList<>() ;

    //关注过的大咖的动态
    private RecyclerView mListView;
    private AdapterCircleActive mAdapter;
    private HeaderFooterAdapter<AdapterCircleActive> mWrapAdapter ;

    private List<UserActive> mLatestActiveList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;
    private boolean mIsRefreshing = false ;

    private int mScrollY = 0 ;
    private int mScreenHeight ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private int mCurPosition ;

    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1005 ;
    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1006 ;

    private LoadingDialog loadingDialog ;

    /**
     * handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGE_PHOTO:
                    if(isPagerTouched){//触摸了轮播banner，这个一次就先不播放，下次再播放
                        isPagerTouched = false ;
                        return ;
                    }

                    int length = mHotTopicList.size() ;
                    if(length != 0){//如果滑动了，不执行播放
                        int index = mHotVp.getCurrentItem();

                        //最后位置是 MAX_REPLAY_ROTE 倍
                        if(index == length * PagerAdapterHotTopic.MAX_SLIP - 1){//到头了
                            mHotVp.setCurrentItem(length * PagerAdapterHotTopic.MAX_SLIP / 2 , false);//中间
                        }else if(index == 0){//到头了
                            mHotVp.setCurrentItem(length * PagerAdapterHotTopic.MAX_SLIP / 2  + 1 , false);//中间
                        }else{
                            mHotVp.setCurrentItem(index + 1);
                        }
                    }

                    break;
                default:
                    break ;

            }
            super.dispatchMessage(msg);
        }
    };

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getHeaderHot() ;
                getFocusUserVic() ;
                getFocusActive() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        mRefreshLay = view.findViewById(R.id.frag_follow_circle_refresh_lay);
        mListView = view.findViewById(R.id.frag_follow_circle_lv);

        mToTopIv = view.findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(new ViewClickListener());

        mAdapter = new AdapterCircleActive( mActivity,mLatestActiveList,true, new RecyclerViewClickListener() {
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
                    UserActive info = mLatestActiveList.get(mCurPosition) ;
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
        });

        mWrapAdapter = new HeaderFooterAdapter<>(mAdapter) ;
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setAdapter(mWrapAdapter) ;
        addHeader() ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_follow_circle;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;

                mCurPage = 1 ;
                getHeaderHot() ;
            }
        });

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mListView)){
                    getFocusActive() ;
                }

                mScrollY += dy ;
                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight * 2) ;
            }
        });
    }

    private class ViewClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int vId = view.getId() ;

            if(R.id.header_follow_circle_hot_more_lay == vId){
                Intent toHtIt = new Intent(mContext , TopicHotActivity.class) ;
                mContext.startActivity(toHtIt) ;
            }else if(R.id.acti_lv_to_top_iv == vId){
                mScrollY = 0 ;
                mToTopIv.setVisibility(View.GONE);
                mListView.scrollToPosition(0) ;
            }
        }
    }

    /**
     * 跳转到转载
     */
    private void toTransportActivity(){
        UserActive info = mLatestActiveList.get(mCurPosition) ;
        if(info != null){
            CircleTransportActivity.launch(mFragment ,info.getPostID()
                    ,info.getBodyContent() ,info.getImgUrl()
                    ,info.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
        }

    }


    /**
     * 添加header
     */
    private void addHeader(){
        View hotView = LayoutInflater.from(mContext).inflate(R.layout.header_follow_circle_hot ,mListView , false) ;
        View mMoreTpIv = hotView.findViewById(R.id.header_follow_circle_hot_more_lay) ;
        mMoreTpIv.setOnClickListener(new ViewClickListener()) ;
        mHotVp = hotView.findViewById(R.id.header_follow_circle_hot_vp) ;
        mHotVp.setPageTransformer(false,new ZoomOutPageTransformer()) ;
        mHotVp.setPageMargin(ResourceUtils.dip2px(mContext ,15));
        int mImageWid = (ResourceUtils.getScreenWidth(mContext) - ResourceUtils.dip2px(mContext ,50) * 2 );
        int itemHeight = (int) (mImageWid / ImageWidthUtils.getTopicImageRote());
        mHotVp.getLayoutParams().height = itemHeight ;
        mHotVp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isPagerTouched){
                    isPagerTouched = true ;
                }
                return false;
            }
        });
        mHotVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mCurIndex = position ;

            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mWrapAdapter.addHeader(hotView);

        View focusView = LayoutInflater.from(mContext).inflate(R.layout.header_follow_circle_focus ,mListView , false) ;
        mFocusListLay = focusView.findViewById(R.id.header_follow_circle_focus_lay) ;
        focusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toVcIt = new Intent(mContext , VicUserActivity.class) ;
                mContext.startActivity(toVcIt) ;
            }
        });

        mWrapAdapter.addHeader(focusView) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mWrapAdapter.addFooter(mFooterTv);
    }

    /**
     * 填充行业大咖
     */
    private void updateFocus(){
        mFocusListLay.removeAllViews() ;

        int vicSize = mUserVicList.size() ;
        if(vicSize > 0){
            if(vicSize > 5){//界面有限，暂定显示5个
                vicSize = 5 ;
            }
            int itemWid = ResourceUtils.dip2px(mContext , 40) ;
            int itemSlid = ResourceUtils.dip2px(mContext , 30) ;

            for(int x = 0 ; x < vicSize ; x ++){
                ImageView userIv = new ImageView(mContext) ;
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(itemWid , itemWid) ;
                lp.setMargins(itemSlid * x ,0 ,0 , 0);
                userIv.setLayoutParams(lp);
                userIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                String userHead = mUserVicList.get(x).getHeadIcon() ;
                GlideUtils.loaderUser(userHead , userIv);

                mFocusListLay.addView(userIv) ;
            }
        }
    }

    /**
     * 开始播放广告
     */
    private void startGuanggao(){
        if(mHotTopicList.size() == 0){
            return ;
        }

        if(ggTimer == null){
            ggTimer = new Timer() ;
        }
        if(ggTimerTask == null){
            ggTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(MSG_CHANGE_PHOTO) ;
                }
            };
        }

        if(ggTimer != null && !isStarted){
            isStarted = true ;
            ggTimer.schedule(ggTimerTask, PHOTO_CHANGE_TIME , PHOTO_CHANGE_TIME);
        }
    }

    /**
     * 停止播放广播
     */
    private void stopGuanggao(){
        if(mHotTopicList.size() == 0){
            return ;
        }

        isStarted = false ;

        if (ggTimer != null) {
            ggTimer.cancel();
            ggTimer = null ;
        }
        if (ggTimerTask != null) {
            ggTimerTask.cancel();
            ggTimerTask = null ;
        }
    }

    /**
     * 热门话题
     */
    private void getHeaderHot(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.TOPIC_LIST, false, paramMap
                , new RequestListCallBack<TopicList>("getHeaderHot" , mContext ,TopicList.class) {
            @Override
            public void onSuccessResult(List<TopicList> bean) {

                mHotTopicList.clear();
                mHotTopicList.addAll(bean) ;
                if(mHotTopicList.size() > 0){
                    mHotVp.setOffscreenPageLimit(mHotTopicList.size());
                }

                if(null == mHotTopicAdapter){
                    mHotTopicAdapter = new PagerAdapterHotTopic(mActivity ,mHotTopicList) ;
                    mHotVp.setAdapter(mHotTopicAdapter) ;
                }else{
                    mHotTopicAdapter.notifyDataSetChanged() ;
                }

                mCurIndex = PagerAdapterHotTopic.MAX_SLIP / 2 * mHotTopicList.size() ;
                mHotVp.setCurrentItem(mCurIndex) ;
                startGuanggao() ;

                if(mIsRefreshing){
                    getFocusUserVic();
//                    getFocusActive();
                }

            }
            @Override
            public void onErrorResult(String str) {

                if(mIsRefreshing){
                    getFocusUserVic();
//                    getFocusActive();
                }

            }
            @Override
            public void onFail(Exception e) {

                if(mIsRefreshing){
                    getFocusUserVic();
//                    getFocusActive();
                }

            }
        });
    }

    /**
     * 行业大咖
     */
    private void getFocusUserVic(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_FOCUS_LIST, false, paramMap
                , new RequestListCallBack<UserVic>("getFocusUserVic" , mContext ,UserVic.class) {
            @Override
            public void onSuccessResult(List<UserVic> bean) {

                if(bean != null){
                    mUserVicList.clear();
                    mUserVicList.addAll(bean) ;
                    updateFocus() ;
                }

                if(mIsRefreshing){
                    getFocusActive();
                }
            }
            @Override
            public void onErrorResult(String str) {
                if(mIsRefreshing){
                    getFocusActive();
                }
            }
            @Override
            public void onFail(Exception e) {
                if(mIsRefreshing){
                    getFocusActive();
                }
            }
        });
    }

    /**
     * 动态
     */
    private void getFocusActive(){
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOLLOW_USER_POST_LIST, false, paramMap
                , new RequestListCallBack<UserActive>("getFocusActive" , mContext ,UserActive.class) {
            @Override
            public void onSuccessResult(List<UserActive> bean) {
                if(1 == mCurPage){
                    mLatestActiveList.clear();
                }

                mLatestActiveList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

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
        mIsRefreshing = false ;
        loadPage.showSuccessPage();
        mRefreshLay.finishRefresh() ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mLatestActiveList.size() == 0){
            mFooterTv.setText("暂无动态");
        }
    }


    /**
     * 点赞
     */
    private void supportCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mFragment , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        String articleID = mLatestActiveList.get(mCurPosition).getPostID() ;

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", articleID);
        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_SUPPORT, false, para,
                new RequestObjectCallBack<SupportResult>("supportCircle", mContext, SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        mLatestActiveList.get(mCurPosition).setSupportState(bean.AddOrDelete ,bean.LikeNumber) ;
                        mAdapter.notifyDataSetChanged() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mIsReload){
            if(isVisibleToUser){
                startGuanggao() ;

                if(mHotTopicList != null && mHotTopicList.size() == 0){
                    getHeaderHot() ;
                }

                if(mUserVicList != null && mUserVicList.size() == 0){
                    getFocusUserVic() ;
                }

                if(mLatestActiveList != null && mLatestActiveList.size() == 0){
                    mIsMore = true ;
                    getFocusActive() ;
                }
            }else{
                stopGuanggao() ;
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(REQUEST_CODE_CIRCLE_SUPPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                supportCircle() ;
            }
        }else if(REQUEST_CODE_CIRCLE_TO_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                toTransportActivity() ;
            }
        }else if(REQUEST_CODE_CIRCLE_FROM_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                if(mLatestActiveList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getFocusActive() ;
                }else{
                    mLatestActiveList.get(mCurPosition).updateTransCount();
                    mAdapter.notifyDataSetChanged() ;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getFocusUserVic") ;
        NetUtils.cancelTag("getHeaderHot") ;
        NetUtils.cancelTag("getFocusActive") ;

    }
}
