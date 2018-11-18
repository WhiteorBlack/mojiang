package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.NomalRvCategory;
import cn.idcby.jiajubang.Bean.QuestionAnswers;
import cn.idcby.jiajubang.Bean.QuestionCategory;
import cn.idcby.jiajubang.Bean.QuestionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalOptionCategory;
import cn.idcby.jiajubang.adapter.AdapterQuestionAnswers;
import cn.idcby.jiajubang.adapter.AdapterQuestionList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * bg_index_hywd
 * Created on 2018/4/16.
 *
 * 2018-05-10 19:03:37
 * 隐藏推荐的大咖列表
 */

public class QuestionActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private Banner mTopBanner ;
    private RecyclerView mQuestionCateRv ;
    private RecyclerView mHotUserRv;

    private List<QuestionAnswers> mAnswersList = new ArrayList<>() ;
    private AdapterQuestionAnswers mAnswerAdapter ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private List<QuestionCategory> mCateList = new ArrayList<>() ;
    private List<NomalRvCategory> mShowCateList = new ArrayList<>() ;
    private AdapterNomalOptionCategory mCateAdapter ;
    private LinearLayoutManager mCateManager ;

    private String mQuestionCateId = "" ;
    private int mQuestionType = 1; // 1.新问题 2.零回答 3.悬赏高 4.已解决
    private int mCurPosition = 0 ;
    private static final int REQUEST_CODE_SEARCH = 1002 ;

    private AdapterQuestionList mAdapter ;
    private List<QuestionList> mQuestionList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_question ;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(this,null) ;

        mRefreshLay = findViewById(R.id.acti_question_parent_sv);
        mLv = findViewById(R.id.acti_question_lv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_question_item ,null) ;

        mTopBanner = headerView.findViewById(R.id.acti_question_banner_lay);
        View mSearchLay = headerView.findViewById(R.id.acti_question_search_lay) ;
        TextView mCreateNeedTv = headerView.findViewById(R.id.acti_question_create_question_tv) ;
        mHotUserRv = headerView.findViewById(R.id.acti_question_user_rv) ;
        mQuestionCateRv = headerView.findViewById(R.id.acti_question_category_rv) ;

        RadioGroup mTypeGroup = headerView.findViewById(R.id.acti_question_radio_lay) ;
        mTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(R.id.acti_question_radio_new_rb == i){
                    updateListByType(1) ;
                }else if(R.id.acti_question_radio_zero_rb == i){
                    updateListByType(2) ;
                }else if(R.id.acti_question_radio_reward_rb == i){
                    updateListByType(3) ;
                }else{
                    updateListByType(4) ;
                }
            }
        });

        mSearchLay.setOnClickListener(this);
        mCreateNeedTv.setOnClickListener(this);

        mCateAdapter = new AdapterNomalOptionCategory(mContext, mShowCateList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                updateCateInfo(position) ;
            }
        }) ;

        mCateManager = new LinearLayoutManager(mContext);
        mCateManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mQuestionCateRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext ,ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,RvLinearManagerItemDecoration.HORIZONTAL_LIST)) ;
        mQuestionCateRv.setLayoutManager(mCateManager);
        mQuestionCateRv.setHasFixedSize(true);
        mQuestionCateRv.setAdapter(mCateAdapter) ;

        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;
        int height = (int) (screenWidth / ImageWidthUtils.nomalBannerImageRote());
        mTopBanner.getLayoutParams().height = height ;

        mTopBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext ,mTopBannerList.get(position - 1)) ;
            }
        });
        //设置banner动画效果
        mTopBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mTopBanner.setDelayTime(5000);
        mTopBanner.setImageLoader(new BannerImageLoader()) ;

        mAnswerAdapter = new AdapterQuestionAnswers(mContext, mAnswersList,true, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    if(mCateList.size() > 0 && mCateList.size() > mCurPosition){
                        Intent toLtIt = new Intent(mContext ,QuestionAnswersActivity.class) ;
                        toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,mCateList.get(mCurPosition).getIndustryCategoryID()) ;
                        startActivity(toLtIt) ;
                    }
                }
            }
        }) ;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHotUserRv.setLayoutManager(layoutManager);
        mHotUserRv.setAdapter(mAnswerAdapter) ;

        mLv.addHeaderView(headerView) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mAdapter = new AdapterQuestionList(mActivity, mQuestionList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    QuestionList questionList = mQuestionList.get(position) ;
                    if(questionList != null){
                        String questId =questionList.getQuestionId() ;

                        Intent toDtIt = new Intent(mContext , QuestionDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_QUESTION_ID ,questId) ;
                        mActivity.startActivityForResult(toDtIt ,1000);
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

                if(!mIsRefreshing && !mIsLoading && mIsMore
                        && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){
                    getQuestionList();
                }

                ViewUtil.setViewVisible(mToTopIv ,firstVisibleItem > 10);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;
                mCurPage = 1 ;
                getTopBanner();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_question_search_lay == vId){
            Intent toLtIt = new Intent(mContext ,SearchNomalActivity.class) ;
            startActivityForResult(toLtIt,REQUEST_CODE_SEARCH) ;
        }else if(R.id.acti_question_create_question_tv == vId){
            Intent toCtIt = new Intent(mContext ,QuestionCreateActivity.class) ;
            startActivity(toCtIt) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0);
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 根据类型切换列表
     * @param type type
     */
    private void updateListByType(int type){
        if(mQuestionType == type){
            return ;
        }

        mQuestionType = type ;

        mLv.setSelection(0);
        mToTopIv.setVisibility(View.GONE);

        mCurPage = 1 ;
        getQuestionList() ;
    }

    /**
     * 根据分类切换显示内容
     * @param position posi
     */
    private void updateCateInfo(int position){
        if(mCurPosition == position){
            return ;
        }

        mShowCateList.get(mCurPosition).setSelected(false);
        mShowCateList.get(position).setSelected(true);
        mCateAdapter.notifyDataSetChanged() ;

        mCurPosition = position ;
        mQuestionCateId = mCateList.get(mCurPosition).getIndustryCategoryID() ;

        //更新行业顾问
        getQuestionAnswerList(mQuestionCateId) ;
        //更新列表
        mCurPage = 1 ;
        getQuestionList() ;
    }

    /**
     * 切换显示
     */
    private void resetLvContent(){
        if(mCurPosition < mCateList.size()){
            mQuestionCateId = mCateList.get(mCurPosition).getIndustryCategoryID() ;

            getQuestionAnswerList(mQuestionCateId) ;
            mCurPage = 1 ;
            getQuestionList() ;
        }else{
            if(mIsRefreshing){
                mRefreshLay.finishRefresh() ;
            }
        }
    }

    /**
     * 更新列表显示
     * @param bean
     */
    private void updateCategoryInfo(List<QuestionCategory> bean){
        if(null == bean){
            bean = new ArrayList<>() ;
        }

        mCateList.clear();
        mCateList.addAll(bean) ;
        mCateList.add(0,new QuestionCategory(true ,"推荐")) ;

        mCurPosition = 0 ;
        mShowCateList.clear();

        int size = mCateList.size() ;
        if(size > 0){
            for(int x = 0 ; x < size ; x ++){
                QuestionCategory category = mCateList.get(x) ;
                if(category.getIndustryCategoryID().equals(mQuestionCateId)){
                    mCurPosition = x ;
                }
                mShowCateList.add(new NomalRvCategory(category)) ;
            }
        }

        mCateAdapter.notifyDataSetChanged();
        if(mCurPosition < mShowCateList.size()){
            mShowCateList.get(mCurPosition).setSelected(true) ;
            ViewUtil.moveToPosition(mCateManager,mQuestionCateRv ,mCurPosition);
        }

        resetLvContent() ;
    }

    /**
     * 行业顾问
     */
    private void getQuestionAnswerList(String cateId){
        mAnswersList.clear();
        mAnswerAdapter.notifyDataSetChanged() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page" ,"1") ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("Keyword" ,cateId) ;

        NetUtils.getDataFromServerByPost(mContext, 0 == mCurPosition
                        ? Urls.QUESTION_ANSWER_LIST_HOT
                        : Urls.QUESTION_ANSWER_LIST, paramMap
                , new RequestListCallBack<QuestionAnswers>("getQuestionAnswerList" ,mContext ,QuestionAnswers.class) {
                    @Override
                    public void onSuccessResult(List<QuestionAnswers> bean) {
                        mAnswersList.addAll(bean) ;
                        mAnswerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 获取分类
     */
    private void requestQuestionCategory() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_CATEGORY, false, para,
                new RequestListCallBack<QuestionCategory>("getQuestionCategory", mContext, QuestionCategory.class) {
                    @Override
                    public void onSuccessResult(List<QuestionCategory> bean) {
                        updateCategoryInfo(bean);
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateCategoryInfo(null);
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateCategoryInfo(null);
                    }
                });
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppQAHead") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, paramMap
                , new RequestListCallBack<AdvBanner>("getTopBanner" ,mContext , AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;

                        List<String> imageUrl = new ArrayList<>(bean.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }

                        mTopBanner.update(imageUrl) ;

                        if(mIsRefreshing){
                            requestQuestionCategory() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            requestQuestionCategory() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            requestQuestionCategory() ;
                        }
                    }
                });
    }


    /**
     * 获取问题列表
     */
    private void getQuestionList(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;

            mQuestionList.clear();
            mAdapter.notifyDataSetChanged() ;
        }

        String mQuestionCateId = mCateList.get(mCurPosition).getIndustryCategoryID() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("TypeId" , "" + mQuestionType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("CategoryId" , StringUtils.convertNull(mQuestionCateId)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_LIST, paramMap
                , new RequestListCallBack<QuestionList>("getQuestionList", mContext , QuestionList.class) {
                    @Override
                    public void onSuccessResult(List<QuestionList> bean) {
                        mQuestionList.addAll(bean) ;
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

    /**
     * 完成请求
     */
    private void finishRequest(){
        mIsLoading = false ;

        if(mIsRefreshing){
            mRefreshLay.finishRefresh() ;
        }

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mQuestionList.size() == 0){
            mFooterTv.setText("暂无问答");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTopBannerList.size() == 0){
            getTopBanner() ;
        }

        if(mCateList.size() <= 1){
            requestQuestionCategory() ;
        }else{
            if(mQuestionList.size() == 0){
                mCurPage = 1 ;
                getQuestionList() ;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                Intent toLiIt = new Intent(mContext ,QuestionListActivity.class) ;
                toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,mSearchKey) ;
                startActivity(toLiIt) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

         NetUtils.cancelTag("getTopBanner");
         NetUtils.cancelTag("getQuestionCategory");
         NetUtils.cancelTag("getQuestionList");
    }
}
