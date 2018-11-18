package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterResumeList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 简历推荐
 * Created on 2018/3/27.
 *
 * 暂时只展示推荐的简历
 */

public class ResumeHotListActivity extends BaseActivity {
    private TopBarRightView mRightLay ;

    private TextView mSearchKeyTv ;
    private ListView mLv ;
    private MaterialRefreshLayout mRefreshLay ;
    private View mLoadingPb ;
    private TextView mNullTv ;

    private AdapterResumeList mAdapter ;
    private List<ResumeList> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private int mListType = 0 ; // 0全部 ，3推荐
    private String mSearchKey = "" ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_resume_hot_list ;
    }

    @Override
    public void initView() {
        mListType = getIntent().getIntExtra(SkipUtils.INTENT_RESUME_TYPE , 0) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        mRightLay = findViewById(R.id.acti_resume_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_resume_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_resume_list_search_key_tv) ;
        mRightLay.setOnClickListener(this) ;
        searchLay.setOnClickListener(this) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        if(mSearchKey != null && !"".equals(mSearchKey.trim())){
            mSearchKeyTv.setText(mSearchKey) ;
        }

        mLoadingPb = findViewById(R.id.acti_resume_list_load_pb) ;
        mNullTv = findViewById(R.id.acti_resume_list_null_tv) ;
        mLv = findViewById(R.id.acti_resume_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_resume_list_refresh_lay) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv) ;

        mAdapter = new AdapterResumeList(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                ResumeList resumeList = mDataList.get(position) ;
                if(resumeList != null){
                    Intent toDtIt = new Intent(mContext , ResumeDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,resumeList.getResumeId()) ;
                    mContext.startActivity(toDtIt) ;
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getResumeList();
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10);

            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getResumeList() ;
            }
        });
    }

    @Override
    public void initData() {
        getResumeList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_resume_list_right_iv == vId){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,1009);
            }else{
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }else if(R.id.acti_resume_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索
     */
    private void searchFromList(){
        mToTopIv.setVisibility(View.GONE);
        mLv.setSelection(0) ;

        mCurPage = 1 ;
        mIsMore = true ;

        getResumeList() ;
    }

    /**
     * 获取简历列表
     */
    private void getResumeList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        int workType = 0 ;
        int type = 0 == mListType ? 2 : 1 ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Keyword" , null == mSearchKey ? "" : mSearchKey) ;
        paramMap.put("WorkType" , "" + workType) ;
        paramMap.put("Type" , "" + type) ;//1 优质人才 2 全部

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_LIST, paramMap
                , new RequestListCallBack<ResumeList>("getResumeList" , mContext , ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
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
            mNullTv.setVisibility(View.VISIBLE);
            mFooterTv.setText("") ;
        }
    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mRightLay.setUnreadCount(count);
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

                searchFromList() ;
            }
        }else if(1009 == requestCode){
            if(RESULT_OK == resultCode){
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }

    }
}
