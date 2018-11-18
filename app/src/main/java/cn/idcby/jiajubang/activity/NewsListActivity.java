package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.NewsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.HomeHotNewsAdapter;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 资讯列表
 * Created on 2018/3/27.
 */

public class NewsListActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private ProgressBar mLoadingPb ;
    private TextView mNullTv ;
    private TextView mSearchKeyTv ;

    private ArrayList<NewsList> mDataList = new ArrayList<>();
    private HomeHotNewsAdapter mAdapter;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private String mSearchKey = "" ;

    private View mToTopIv ;
    private TextView mFooterTv ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_news_list ;
    }

    @Override
    public void initView() {
        mSearchKey = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY)) ;

        View searchLay = findViewById(R.id.acti_news_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_news_list_search_key_tv) ;
        searchLay.setOnClickListener(this) ;

        if(!"".equals(mSearchKey.trim())){
            mSearchKeyTv.setText(mSearchKey) ;
        }

        mLoadingPb = findViewById(R.id.acti_news_list_load_pb) ;
        mNullTv = findViewById(R.id.acti_news_list_null_tv) ;
        mLv = findViewById(R.id.acti_news_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_news_list_refresh_lay) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mAdapter = new HomeHotNewsAdapter(mContext, mDataList) ;
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getNewsList();
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10) ;
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getNewsList() ;
            }
        });
    }

    @Override
    public void initData() {
        getNewsList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_news_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mToTopIv.setVisibility(View.GONE);
            mLv.setSelection(0) ;
        }
    }

    /**
     * 获取列表
     */
    private void getNewsList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;

            mDataList.clear();
            mAdapter.notifyDataSetChanged() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page", String.valueOf(mCurPage));
        paramMap.put("PageSize", "10");
        paramMap.put("Keyword", mSearchKey);

        NetUtils.getDataFromServerByPost(mContext, Urls.NEWS_LIST, paramMap
                , new RequestListCallBack<NewsList>("requestNewsList" , mContext , NewsList.class) {
                    @Override
                    public void onSuccessResult(List<NewsList> bean) {
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
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("") ;
            mNullTv.setVisibility(View.VISIBLE);
        }
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

                mToTopIv.setVisibility(View.GONE);
                mLv.setSelection(0) ;

                mCurPage = 1 ;
                getNewsList() ;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("requestNewsList") ;
    }
}
