package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterServerList;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 服务列表
 * Created on 2018/4/11.
 *
 * 2018-04-26 20:35:02
 * 行业服务，把全部改为可以选择二级分类的功能，安装服务不变
 */

public class ServerListActivity extends BaseActivity {
    private boolean mIsInstallSer = true ;

    private int mSortType = 1 ; //1.综合 2.接单 3.好评
    private boolean mIsArrowUp = true ;//升序

    private int mCategoryLayer = 1 ;
    private String mCheckedCateId ;
    private String mCategoryId ;
    private String mSearchKey ;

    private TopBarRightView mRightLay ;
    private View mLoadingLay ;
    private TextView mSearchKeyTv ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private TextView mTypeAllTv ;
    private ImageView mTypeAllIv;
    private TextView mTypeCountTv;
    private ImageView mTypeCountIv;
    private TextView mTypeCommentTv;
    private ImageView mTypeCommentIv;

    //二级规格相关
    private View mCategoryLay ;
    private FlowLayout mCateFlowLay ;
    private List<ServerCategory> mCategoryList = new ArrayList<>() ;

    private AdapterServerList mAdapter ;
    private List<ServiceList> mDataList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private LoadingDialog mDialog ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_server_list;
    }

    @Override
    public void initView() {
        int serverType = getIntent().getIntExtra(SkipUtils.INTENT_SERVER_TYPE ,ServerActivity.SERVER_TYPE_SERVER) ;
        mIsInstallSer = ServerActivity.SERVER_TYPE_INSTALL == serverType ;

        mCategoryId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID)) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;
        mCheckedCateId = StringUtils.convertNull(mCategoryId) ;

        mRightLay = findViewById(R.id.acti_server_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_server_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_server_list_search_key_tv) ;
        mRefreshLay = findViewById(R.id.acti_server_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_server_list_lv);
        mLoadingLay = findViewById(R.id.acti_server_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_server_list_null_tv) ;
        View mTypeAllLay = findViewById(R.id.acti_server_list_all_lay) ;
        mTypeAllTv = findViewById(R.id.acti_server_list_all_tv) ;
        mTypeAllIv = findViewById(R.id.acti_server_list_all_iv) ;
        View mTypeCountLay = findViewById(R.id.acti_server_list_count_lay) ;
        mTypeCountTv = findViewById(R.id.acti_server_list_count_tv) ;
        mTypeCountIv = findViewById(R.id.acti_server_list_count_iv) ;
        View mTypeCommentLay = findViewById(R.id.acti_server_list_comment_lay) ;
        mTypeCommentTv = findViewById(R.id.acti_server_list_comment_tv) ;
        mTypeCommentIv = findViewById(R.id.acti_server_list_comment_iv) ;

        mCategoryLay = findViewById(R.id.acti_server_list_category_lay) ;
        mCateFlowLay = findViewById(R.id.acti_server_list_category_fl) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mCateFlowLay.setOnClickListener(this);
        mCategoryLay.setOnClickListener(this);

        if(mIsInstallSer){
            mTypeAllIv.setVisibility(View.GONE) ;
        }

        mTypeCountLay.setOnClickListener(this);
        mTypeCommentLay.setOnClickListener(this);
        mTypeAllLay.setOnClickListener(this);
        searchLay.setOnClickListener(this);
        mRightLay.setOnClickListener(this);

        if(mSearchKey != null && !"".equals(mSearchKey)){
            mSearchKeyTv.setText(mSearchKey);
        }

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mAdapter = new AdapterServerList(mContext, mDataList, false, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    ServiceList serverList = mDataList.get(position) ;
                    if(serverList != null){
                        String serId = serverList.getCreateUserId() ;
                        ServerDetailActivity.launch(mContext ,serId,mIsInstallSer) ;
                    }
                }
            }
        }) ;
        mLv.setAdapter(mAdapter) ;

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                    , int totalItemCount) {

                if(!mIsLoading && mIsMore && totalItemCount > 5
                        && visibleItemCount + firstVisibleItem >= totalItemCount){
                    getServerList();
                }
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;
                getServerList();
            }
        });
    }

    @Override
    public void initData() {
        getServerList();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_server_list_right_iv == vId){
            SkipUtils.toMessageCenterActivity(mContext);
        }else if(R.id.acti_server_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_server_list_all_lay == vId){
            if(mIsInstallSer){
                changeTypeTvStyle(1);
            }else{
                if(mCategoryLay.getVisibility() != View.GONE){
                    showCategoryLay(false) ;
                }else{
                    if(mCategoryList.size() == 0){
                        getCategoryList() ;
                    }else{
                        showCategoryLay(true) ;
                    }
                }
            }
        }else if(R.id.acti_server_list_count_lay == vId){
            changeTypeTvStyle(2) ;
        }else if(R.id.acti_server_list_comment_lay == vId){
            changeTypeTvStyle(3) ;
        }else if(R.id.acti_server_list_category_lay == vId){
            showCategoryLay(false) ;
        }else if(R.id.acti_server_list_category_fl == vId){//空实现即可
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0);
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 显示二级规格
     * @param isShow show
     */
    private void showCategoryLay(boolean isShow){
        mTypeAllIv.setImageDrawable(getResources().getDrawable(isShow
                ? R.mipmap.ic_arrow_up_full : R.mipmap.ic_arrow_down_full));

        mCategoryLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void changeTypeTvStyle(int type){
        switch (mSortType){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                break;
            case 2:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            case 3:
                mTypeCommentTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));

                break;
            default:
                break;
        }

        if(1 == type){
            mIsArrowUp = true ;
        }else{
            mIsArrowUp = !mIsArrowUp ;
        }

        switch (type){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;
                break;
            case 2:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }

                break;
            case 3:
                mTypeCommentTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }
                break;
            default:
                break;
        }

        mSortType = type ;
        changeNeedTypeAndGetList() ;
    }

    private void changeNeedTypeAndGetList(){
        mLv.setSelection(0);
        mToTopIv.setVisibility(View.GONE);

        showCategoryLay(false) ;

        NetUtils.cancelTag("getServerList" + mSortType + "," + mIsArrowUp);

        mCurPage = 1 ;
        mIsMore = true ;

        showOrHiddenLoading(true);

        mDataList.clear();
        mAdapter.notifyDataSetChanged() ;

        getServerList();
    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表
     */
    private void getServerList(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        mIsLoading = true ;

        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("TypeId" , "" + mSortType) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mSearchKey)) ;
        paramMap.put("CategoryId" , StringUtils.convertNull(mCheckedCateId)) ;
        paramMap.put("CategoryLevel" , "" + mCategoryLayer) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        String url = mIsInstallSer ? Urls.SERVER_LIST_INSTALL : Urls.SERVER_LIST_SERVER ;

        NetUtils.getDataFromServerByPost(mContext, url, paramMap
                , new RequestListCallBack<ServiceList>("getServerList" + mSortType + "," + mIsArrowUp
                        , mContext , ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }
                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

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

    /**
     * 完成请求
     */
    private void finishRequest(){
        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        showOrHiddenLoading(false);

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("") ;
            mNullTv.setVisibility(View.VISIBLE) ;
        }
    }


    /**
     * 获取分类
     */
    private void getCategoryList(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("code", mCategoryId);
        paramMap.put("id" ,"2") ;
        paramMap.put("Layer" ,"2") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, paramMap
                , new RequestListCallBack<ServerCategory>("getSecondCategory" ,mContext , ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        mCategoryList.addAll(bean) ;

                        updateCategoryDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {

                        updateCategoryDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {

                        updateCategoryDisplay() ;
                    }
                });
    }

    /**
     * 填充分类
     */
    private void updateCategoryDisplay(){
        mDialog.dismiss() ;

        mCateFlowLay.removeAllViews() ;
        //先添加一个默认的（全部按钮）
        TextView allCateTv = new TextView(mContext) ;
        FrameLayout.LayoutParams nTvLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT) ;
        allCateTv.setLayoutParams(nTvLp) ;
        allCateTv.setPadding(ResourceUtils.dip2px(mContext, 10)
                ,ResourceUtils.dip2px(mContext, 5)
                , ResourceUtils.dip2px(mContext, 10)
                , ResourceUtils.dip2px(mContext, 6)) ;
        allCateTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_grey_f2_bg));
        allCateTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14) ;
        allCateTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text)) ;
        allCateTv.setGravity(Gravity.CENTER) ;
        allCateTv.setSingleLine(true);
        allCateTv.setEllipsize(TextUtils.TruncateAt.END) ;
        allCateTv.setText("全部") ;

        allCateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckedCateId = StringUtils.convertNull(mCategoryId) ;
                mCategoryLayer = 1 ;
                mTypeAllTv.setText("全部");
                changeTypeTvStyle(1);
            }
        });

        mCateFlowLay.addView(allCateTv) ;

        if(mCategoryList.size() > 0){
            for(final ServerCategory category : mCategoryList){
                TextView childCateTv = new TextView(mContext) ;
                FrameLayout.LayoutParams cTvLp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT) ;
                childCateTv.setLayoutParams(cTvLp) ;
                childCateTv.setPadding(ResourceUtils.dip2px(mContext, 10)
                        ,ResourceUtils.dip2px(mContext, 6)
                        , ResourceUtils.dip2px(mContext, 10)
                        , ResourceUtils.dip2px(mContext, 5)) ;
                childCateTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_grey_f2_bg));
                childCateTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14) ;
                childCateTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text)) ;
                childCateTv.setGravity(Gravity.CENTER) ;
                childCateTv.setSingleLine(true);
                childCateTv.setEllipsize(TextUtils.TruncateAt.END) ;
                childCateTv.setText(category.getCategoryTitle()) ;
                childCateTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCategoryLayer = 2 ;
                        mCheckedCateId = category.getServiceCategoryID() ;
                        mTypeAllTv.setText(category.getCategoryTitle());
                        changeTypeTvStyle(1);
                    }
                });

                mCateFlowLay.addView(childCateTv) ;
            }
        }

        showCategoryLay(true) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;
                changeNeedTypeAndGetList() ;
            }
        }
    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mRightLay.setUnreadCount(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getServerList" + mSortType + "," + mIsArrowUp);

    }
}
