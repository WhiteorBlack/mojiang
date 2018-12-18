package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.CircleCategory;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleListActivity;
import cn.idcby.jiajubang.activity.CircleTransportActivity;
import cn.idcby.jiajubang.adapter.AdapterCircleActive;
import cn.idcby.jiajubang.adapter.AdapterCircleCategory;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 *
 */

public class CircleHotFragment extends BaseFragment {
    private LoadingDialog loadingDialog;

    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;
    private HeaderFooterAdapter<AdapterCircleActive> mAdapter ;

    private List<CircleCategory> mCategoryList = new ArrayList<>() ;
    private AdapterCircleCategory mCategoryAdapter ;

    private List<UserActive> mHotList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;
    private boolean mIsRefreshing = false ;

    private int mCurPosition ;

    private boolean mIsReload = false ;

    private static final int REQUEST_CODE_FOCUS = 1001 ;
    private static final int REQUEST_CODE_LIST = 1003 ;
    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1005 ;
    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1006 ;

    private int mScrollY = 0 ;
    private int mScreenHeight ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private ImageView ivHeader;
    private static final int REQUEST_CODE_SEND_CIRCLE = 1001 ;

    public List<CircleCategory> getCircleCate(){
        return mCategoryList ;
    }

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getCategoryList() ;
                getHotActive() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        mRefreshLay = view.findViewById(R.id.frag_hot_circle_refresh_lay);
        mRecyclerView = view.findViewById(R.id.frag_hot_circle_lv);

        mToTopIv = view.findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScrollY = 0 ;
                mToTopIv.setVisibility(View.GONE);
                mRecyclerView.scrollToPosition(0) ;
            }
        });

        AdapterCircleActive adapter = new AdapterCircleActive( mActivity, mHotList,false, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;

                if(AdapterCircleActive.CLICK_TYPE_FOCUS == type){
                    changeFocusState();
                }else if(AdapterCircleActive.CLICK_TYPE_SUPPORT == type){
                    supportCircle() ;
                }else if(AdapterCircleActive.CLICK_TYPE_TRANS == type){
                    if (LoginHelper.isNotLogin(mContext)) {
                        SkipUtils.toLoginActivityForResult(mFragment, REQUEST_CODE_CIRCLE_TO_TRANSPORT);
                        return;
                    }

                    toTransportActivity();
                }else if(AdapterCircleActive.CLICK_TYPE_SHARE == type){
                    UserActive info = mHotList.get(mCurPosition) ;
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

        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter) ;

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_circle_category_item ,null) ;
        RecyclerView mCategoryRv = headerView.findViewById(R.id.header_circle_category_rv) ;
        mCategoryAdapter = new AdapterCircleCategory(mContext, mCategoryList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                CircleCategory category = mCategoryList.get(position) ;
                if(category != null){
                    String categoryId = category.getCategoryId() ;
                    String categoryName = category.getCategoryTitle() ;
                    CircleListActivity.launch(mActivity ,0,categoryId ,categoryName,REQUEST_CODE_LIST);
                }
            }
        }) ;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext ,ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans) ,LinearLayoutManager.HORIZONTAL));
        mCategoryRv.setLayoutManager(layoutManager);
        mCategoryRv.setNestedScrollingEnabled(false);
        mCategoryRv.setFocusable(false);
        mCategoryRv.setAdapter(mCategoryAdapter) ;
        ivHeader=headerView.findViewById(R.id.iv_header);
        headerView.findViewById(R.id.ll_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCircle();
            }
        });
        mAdapter.addHeader(headerView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mAdapter.addFooter(mFooterTv);
    }

    private void sendCircle() {
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mFragment ,REQUEST_CODE_SEND_CIRCLE);
            return ;
        }

        SkipUtils.toSendCircleActivity(mContext);
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_hot_circle;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;
                mCurPage = 1 ;
                getCategoryList();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getHotActive();
                }

                mScrollY += dy ;
                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight * 2) ;
            }
        });
    }

    /**
     * 跳转到转载
     */
    private void toTransportActivity(){
        UserActive info = mHotList.get(mCurPosition) ;
        if(info != null){
            CircleTransportActivity.launch(mFragment ,info.getPostID()
                    ,info.getBodyContent() ,info.getImgUrl()
                    ,info.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
        }

    }

    /**
     * 获取分类
     */
    private void getCategoryList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ID" , "2") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_CATEGORY_LIST, false, paramMap
                , new RequestListCallBack<CircleCategory>("getCategoryList" , mContext ,CircleCategory.class) {
                    @Override
                    public void onSuccessResult(List<CircleCategory> bean) {
                        mCategoryList.clear();
                        mCategoryList.addAll(bean) ;
                        mCategoryAdapter.notifyDataSetChanged();

                        if(mIsRefreshing){
                            getHotActive() ;
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getHotActive() ;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getHotActive() ;
                        }
                    }
                }) ;
    }

    /**
     * 动态
     */
    private void getHotActive(){
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
//        paramMap.put("Keyword" , "") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.HOT_POST_LIST, false, paramMap
                , new RequestListCallBack<UserActive>("getHotActive" , mContext ,UserActive.class) {
                    @Override
                    public void onSuccessResult(List<UserActive> bean) {
                        if(1 == mCurPage){
                            mHotList.clear();
                            mAdapter.notifyDataSetChanged() ;
                        }

                        mHotList.addAll(bean) ;
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
        loadPage.showSuccessPage();
        if(mIsRefreshing){
            mIsRefreshing = false ;
            mRefreshLay.finishRefresh();
        }

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mHotList.size() == 0){
            mFooterTv.setText("暂无动态");
        }
    }


    /**
     * 关注、取消关注
     */
    private void changeFocusState(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mFragment ,REQUEST_CODE_FOCUS);
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("FollowType" , "1") ;
        paramMap.put("ResourceId" , mHotList.get(mCurPosition).getCreateUserId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        String userId = mHotList.get(mCurPosition).getCreateUserId() ;
                        for(UserActive user : mHotList){
                            if(userId.equals(user.getCreateUserId())){
                                user.setIsFollow(bean);
                            }
                        }
                        mAdapter.notifyDataSetChanged() ;

                        if(loadingDialog != null){
                            loadingDialog.dismiss();
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
     * 点赞
     */
    private void supportCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mFragment , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        String articleID = mHotList.get(mCurPosition).getPostID() ;

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

                        mHotList.get(mCurPosition).setSupportState(bean.AddOrDelete ,bean.LikeNumber) ;
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

        if(mIsReload && mCategoryList.size() == 0 && isVisibleToUser){
            getCategoryList();
        }

        if(mIsReload && isVisibleToUser && mHotList != null && mHotList.size() == 0){
            mIsMore = true ;
            getHotActive() ;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOCUS == requestCode){
            if(Activity.RESULT_OK == resultCode){
                changeFocusState() ;
            }
        }else if(REQUEST_CODE_LIST == requestCode){

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
                if(mHotList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getHotActive() ;
                }else{
                    mHotList.get(mCurPosition).updateTransCount();
                    mAdapter.notifyDataSetChanged() ;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getHotActive");
        NetUtils.cancelTag("changeFocusState");
        NetUtils.cancelTag("getCategoryList");

    }

}
