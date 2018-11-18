package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterCircleActive;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/5/10.
 * 圈子列表
 */

public class CircleListActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;

    private View mLoadingPb ;
    private TextView mNullTv ;

    private HeaderFooterAdapter<AdapterCircleActive> mAdapter ;
    private List<UserActive> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private int mCurPosition ;

    private LoadingDialog loadingDialog;

    private int mListType = 0 ;// 0热门 1同城
    private String mCategoryId ;
    private String mCategoryName ;

    private static final int REQUEST_CODE_FOCUS = 1001 ;
    private static final int REQUEST_CODE_SEND = 1002 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1003 ;
    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1005 ;

    private TextView mFooterTv ;

    public static void launch(Activity context ,int type,String categoryId ,String categoryName ,int requestCode){
        Intent toLiIt = new Intent(context ,CircleListActivity.class) ;
        toLiIt.putExtra("circleType" ,type) ;
        toLiIt.putExtra("circleCateId" ,categoryId) ;
        toLiIt.putExtra("circleCateName" ,categoryName) ;
        context.startActivityForResult(toLiIt ,requestCode) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_circle_list ;
    }

    @Override
    public void initView() {
        mListType = getIntent().getIntExtra("circleType" , mListType) ;
        mCategoryId = getIntent().getStringExtra("circleCateId") ;
        mCategoryName = getIntent().getStringExtra("circleCateName") ;

        TextView mTitleTv = findViewById(R.id.acti_circle_list_title_tv) ;
        View mSendIv = findViewById(R.id.acti_circle_list_send_iv) ;
        mSendIv.setOnClickListener(this);

        if(!"".equals(StringUtils.convertNull(mCategoryName))){
            mTitleTv.setText(mCategoryName) ;
        }else{
            if(0 == mListType){
                mTitleTv.setText("热门") ;
            }else{
                mTitleTv.setText("同城");
            }
        }

        mLoadingPb = findViewById(R.id.acti_circle_list_load_pb) ;
        mNullTv = findViewById(R.id.acti_circle_list_null_tv) ;
        mRecyclerView = findViewById(R.id.acti_circle_list_rv) ;
        mRefreshLay = findViewById(R.id.acti_circle_list_refresh_lay) ;

        AdapterCircleActive adapter = new AdapterCircleActive(mActivity, mDataList,false
                , new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;

                if(AdapterCircleActive.CLICK_TYPE_FOCUS == type){
                    changeFocusState();
                }else if(AdapterCircleActive.CLICK_TYPE_SUPPORT == type){
                    supportCircle() ;
                }else if(AdapterCircleActive.CLICK_TYPE_TRANS == type){
                    if (LoginHelper.isNotLogin(mContext)) {
                        SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CIRCLE_TO_TRANSPORT);
                        return;
                    }

                    toTransportActivity();
                }else if(AdapterCircleActive.CLICK_TYPE_SHARE == type){
                    UserActive info = mDataList.get(mCurPosition) ;
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

        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(mIsMore && !mIsLoading && mDataList.size() > 5
                        && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getActiveList() ;
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getActiveList() ;
            }
        });

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mAdapter.addFooter(mFooterTv);
    }

    @Override
    public void initData() {
        getActiveList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_circle_list_send_iv == view.getId()){
            SkipUtils.toSendCircleActivity(mActivity ,mCategoryId ,mCategoryName ,REQUEST_CODE_SEND);
        }
    }

    /**
     * 跳转到转载
     */
    private void toTransportActivity(){
        UserActive info = mDataList.get(mCurPosition) ;
        if(info != null){
            CircleTransportActivity.launch(mActivity ,info.getPostID()
                    ,info.getBodyContent() ,info.getImgUrl()
                    ,info.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
        }

    }

    /**
     * 获取列表
     */
    private void getActiveList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        String url = Urls.HOT_POST_LIST ;
        if(1 == mListType){
            url = Urls.SAME_CITY_POST_LIST ;
            paramMap.put("AreaName", "" + MyApplication.getCurrentCityName());
            paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());
        }
        paramMap.put("CategoryID", StringUtils.convertNull(mCategoryId)) ;

        NetUtils.getDataFromServerByPost(mContext, url , false, paramMap
                , new RequestListCallBack<UserActive>("getActive" , mContext ,UserActive.class) {
                    @Override
                    public void onSuccessResult(List<UserActive> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                            mAdapter.notifyDataSetChanged() ;
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

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
        if(mLoadingPb.getVisibility() == View.VISIBLE){
            mLoadingPb.setVisibility(View.GONE);
        }

        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("");
            mNullTv.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 关注、取消关注
     */
    private void changeFocusState(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_FOCUS);
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("FollowType" , "1") ;
        paramMap.put("ResourceId" , mDataList.get(mCurPosition).getCreateUserId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }

                        mDataList.get(mCurPosition).setIsFollow(bean);
                        mAdapter.notifyDataSetChanged() ;
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
            SkipUtils.toLoginActivityForResult(mActivity , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        String articleID = mDataList.get(mCurPosition).getPostID() ;

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

                        mDataList.get(mCurPosition).setSupportState(bean.AddOrDelete ,bean.LikeNumber) ;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOCUS == requestCode){
            if(Activity.RESULT_OK == resultCode){
                changeFocusState() ;
            }
        }else if(REQUEST_CODE_SEND == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;

                getActiveList() ;
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
                if(mDataList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getActiveList() ;
                }else{
                    mDataList.get(mCurPosition).updateTransCount();
                    mAdapter.notifyDataSetChanged() ;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getActive");
        NetUtils.cancelTag("changeFocusState");

    }
}
