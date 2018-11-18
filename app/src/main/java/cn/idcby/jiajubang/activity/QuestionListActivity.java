package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.QuestionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterQuestionList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
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
 * 行业问答列表
 * Created on 2018/4/19.
 */

public class QuestionListActivity extends BaseActivity {
    private TextView mSearchKeyTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;
    private ProgressBar mLoadingPb ;
    private TextView mNullTv ;

    private AdapterQuestionList mAdapter ;
    private List<QuestionList> mQuestionList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private String mSearchKey = "" ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_question_list ;
    }

    @Override
    public void initView() {
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        View searchLay = findViewById(R.id.acti_question_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_question_list_search_key_tv) ;
        searchLay.setOnClickListener(this) ;

        if(mSearchKey != null && !"".equals(mSearchKey.trim())){
            mSearchKeyTv.setText(mSearchKey) ;
        }

        mLoadingPb = findViewById(R.id.acti_question_list_load_pb) ;
        mNullTv = findViewById(R.id.acti_question_list_null_tv) ;
        mLv = findViewById(R.id.acti_question_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_question_list_refresh_lay) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mAdapter = new AdapterQuestionList(mActivity, mQuestionList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    QuestionList questionList = mQuestionList.get(position) ;
                    if(questionList != null){
                        Intent toDtIt = new Intent(mContext ,QuestionDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_QUESTION_ID,questionList.getQuestionId()) ;
                        mActivity.startActivityForResult(toDtIt,1001);
                    }
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
                    getQuestionList();
                }

                ViewUtil.setViewVisible(mToTopIv,i > 10);
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getQuestionList() ;
            }
        });
    }

    @Override
    public void initData() {
        getQuestionList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_question_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mToTopIv.setVisibility(View.GONE);
            mLv.setSelection(0);
        }
    }

    /**
     * 搜索
     */
    private void searchFromList(){
        mToTopIv.setVisibility(View.GONE);
        mLv.setSelection(0);

        mCurPage = 1 ;
        mIsMore = true ;

        getQuestionList() ;
    }

    /**
     * 获取简历列表
     */
    private void getQuestionList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Keyword" , StringUtils.convertNull(mSearchKey)) ;


        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_LIST, paramMap
                , new RequestListCallBack<QuestionList>("getQuestionList" , mContext , QuestionList.class) {
                    @Override
                    public void onSuccessResult(List<QuestionList> bean) {
                        if(1 == mCurPage){
                            mQuestionList.clear();
                        }

                        mQuestionList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

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
        if(mLoadingPb.getVisibility() == View.VISIBLE){
            mLoadingPb.setVisibility(View.GONE);
        }

        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mQuestionList.size() == 0){
            mFooterTv.setText("") ;
            mNullTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1001 == requestCode){
            if(RESULT_OK == resultCode){
                searchFromList() ;
            }
        }else if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;

                searchFromList() ;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getQuestionList") ;
    }
}
