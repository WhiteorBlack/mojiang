package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.QuestionAnswers;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterQuestionAnswers;
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
 * 行业顾问列表
 * Created on 2018/4/19.
 */

public class QuestionAnswersActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mLv ;

    private ProgressBar mLoadingPb ;
    private TextView mNullTv ;

    private HeaderFooterAdapter<AdapterQuestionAnswers> mAdapter ;
    private List<QuestionAnswers> mDataList = new ArrayList<>() ;

    private String mCategoryId ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;
    private int mScreenHeight ;
    private int mScrollY = 0 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_question_answer ;
    }

    @Override
    public void initView() {
        mCategoryId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID)) ;

        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        TextView mRightTv = findViewById(R.id.acti_question_answer_right_tv) ;
        mRightTv.setOnClickListener(this);

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mLoadingPb = findViewById(R.id.acti_question_answer_load_pb) ;
        mNullTv = findViewById(R.id.acti_question_answer_null_tv) ;
        mRefreshLay = findViewById(R.id.acti_question_answer_refresh_lay) ;
        mLv = findViewById(R.id.acti_question_answer_lv) ;

        AdapterQuestionAnswers adapter = new AdapterQuestionAnswers(mContext, mDataList,false, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {

            }
        }) ;

        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mLv.setLayoutManager(new LinearLayoutManager(mContext));
        mLv.setAdapter(mAdapter);
        mLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(mIsMore && !mIsLoading && ViewUtil.isSlideToBottom(recyclerView)){
                    getQuestionAnswerList() ;
                }

                mScrollY += dy ;

                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight);
            }
        });

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mAdapter.addFooter(mFooterTv);

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getQuestionAnswerList() ;
            }
        });
    }

    @Override
    public void initData() {
        getQuestionAnswerList() ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_question_answer_right_tv == vId){
            Intent toApIt  = new Intent(mContext ,QuestionAnswerApplyActivity.class) ;
            startActivity(toApIt) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
           mLv.scrollToPosition(0);
           mToTopIv.setVisibility(View.GONE);
        }
    }


    /**
     * 获取顾问列表
     */
    private void getQuestionAnswerList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"20") ;
        paramMap.put("Keyword" ,mCategoryId) ;

        NetUtils.getDataFromServerByPost(mContext, "".equals(mCategoryId)
                        ? Urls.QUESTION_ANSWER_LIST_HOT
                        : Urls.QUESTION_ANSWER_LIST, paramMap
                , new RequestListCallBack<QuestionAnswers>("getQuestionAnswerList" ,mContext ,QuestionAnswers.class) {
                    @Override
                    public void onSuccessResult(List<QuestionAnswers> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }
                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() < 20){
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
        if(mLoadingPb.getVisibility() != View.GONE){
            mLoadingPb.setVisibility(View.GONE);
        }

        mIsLoading = false ;
        mRefreshLay.finishRefresh();

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("");
            mNullTv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getQuestionAnswerList");

    }
}
