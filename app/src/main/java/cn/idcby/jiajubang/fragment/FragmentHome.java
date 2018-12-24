package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.HomeWorkCount;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.Bean.NewsCategory;
import cn.idcby.jiajubang.Bean.NewsList;
import cn.idcby.jiajubang.Bean.NomalRvCategory;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.TopicList;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.DirectSellingActivity;
import cn.idcby.jiajubang.activity.GiveJobActivity;
import cn.idcby.jiajubang.activity.NeedsActivity;
import cn.idcby.jiajubang.activity.NeedsSendActivity;
import cn.idcby.jiajubang.activity.NewsActivity;
import cn.idcby.jiajubang.activity.QuestionActivity;
import cn.idcby.jiajubang.activity.SearchIndexActivity;
import cn.idcby.jiajubang.activity.SelectProvinceBySortActivity;
import cn.idcby.jiajubang.activity.ServerActivity;
import cn.idcby.jiajubang.activity.ServerListActivity;
import cn.idcby.jiajubang.activity.ServiceActivity;
import cn.idcby.jiajubang.activity.ServiceListNewActivity;
import cn.idcby.jiajubang.activity.TopicDetailActivity;
import cn.idcby.jiajubang.activity.UnuseGoodListActivity;
import cn.idcby.jiajubang.activity.UnusedActivity;
import cn.idcby.jiajubang.activity.UnusedSendActivity;
import cn.idcby.jiajubang.adapter.AdapterHomeHotNews;
import cn.idcby.jiajubang.adapter.AdapterNomalOptionCategory;
import cn.idcby.jiajubang.adapter.HomeHotServerCateAdapter;
import cn.idcby.jiajubang.adapter.HomeNeedAdapter;
import cn.idcby.jiajubang.adapter.HomeUnuseCategoryAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.OnCityChanged;
import cn.idcby.jiajubang.interf.OnLayClickListener;
import cn.idcby.jiajubang.interf.RvDecorationHiddenCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RecycleViewUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.RvLinearMgItemWithHeaaFoot;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 根据设计图，重写首页
 * 2018-08-13 13:54:40
 */

public class FragmentHome extends BaseFragment implements View.OnClickListener {
    private TextView mMessageCountTv;
    private MaterialRefreshLayout mRefreshLay;

    private TextView mLocationTv;
    private RecyclerView mHotNewsRv;
    private View mLlTopFixed;
    private View mTopMainLay;

    private Banner mBanner;
    private ViewFlipper mHotQuestionVf;
    private ListView mLvHomeNeed;
    private RecyclerView mRvHotServer;

    private TextView mLeftCountTv;
    private TextView mRightCountTv;
    private TextView mNeedCountTv;
    private TextView mUnuseCountTv;

    private Banner mMiddleBanner;

    private RecyclerView mCategoryRv;
    private RecyclerView mCategoryTopRv;
    private RecyclerView mUnuseSpecRv;

    //热门话题
    private List<TopicList> mTopicList = new ArrayList<>();

    //最新需求
    private ArrayList<NeedsList> mLatestNeedList = new ArrayList<>();
    private HomeNeedAdapter mHomeNeedAdapter;

    //热门服务--展示服务分类
    private List<ServerCategory> mHotServiceList = new ArrayList<>();
    private HomeHotServerCateAdapter mHomeHotServerAdapter;

    //top banner
    private List<AdvBanner> mTopBannerList = new ArrayList<>();
    //middle banner
    private List<AdvBanner> mMiddleBannerList = new ArrayList<>();
    //招聘、简历个数
    private HomeWorkCount mWorkCount;

    //闲置分类
    private List<UnusedCategory> mUnuseCateList = new ArrayList<>();
    private HomeUnuseCategoryAdapter mUnuseCateAdapter;


    //热门资讯
    private ArrayList<NewsList> mHomeHotNews = new ArrayList<>();
    private HeaderFooterAdapter<AdapterHomeHotNews> mHomeHotNewsAdapter;

    private List<NomalRvCategory> mShowRvCateList = new ArrayList<>();
    private List<NewsCategory> mNewsCategoryList = new ArrayList<>();

    private AdapterNomalOptionCategory mShowCateAdapter;
    private LinearLayoutManager mCateManager;

    private AdapterNomalOptionCategory mTopCateAdapter;
    private LinearLayoutManager mTopCateManager;

    private int mCurPosition = 0;
    private String categoryID = "";

    private int mCurPage = 1;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private int mScreenHeight;
    private int mScrollLimitHeight = 0;
    private int mScrollY = 0;

    private boolean mIsInit = false;
    private boolean mIsRefresh = false;//是否刷新
    private boolean mIsChangeType = false;//是否切换资讯类型了

    private static final int REQUEST_CODE_LOCATION = 1020;
    private static final int REQUEST_CODE_DIR_LOGIN = 1000;

    private TextView mFooterTv;

    private int mHeaderCateLimit = 0;
    private int mFirstItemLocation[] = new int[2];
    private RecycleViewUtils mRecyUtils;
    private ImageView ivInstallCate;

    /**
     * 城市切换监听，可以放到MainActivity中使用，此处放到fragment
     */
    private OnCityChanged mCityChangeListener;

    public void setCityChangeListener(OnCityChanged cityChangeListener) {
        this.mCityChangeListener = cityChangeListener;
    }

    public static final int LAY_TYPE_CIRCLE = 1;
    private OnLayClickListener mLayClickListener;

    public void setLayClickListener(OnLayClickListener mLayClickListener) {
        this.mLayClickListener = mLayClickListener;
    }

    @Override
    protected void requestData() {
        mIsInit = true;

        loadPage.showSuccessPage();

        requestHomeBanner();
        requestHotQuestion();
        requestHomeMiddleBanner();
        getUnuseCategory();
        requestTopBarData();
    }

    /**
     * 热门资讯
     */
    private void initHomeNewsData(View headerView) {
        mHotNewsRv.setLayoutManager(new LinearLayoutManager(mContext));
        RvDecorationHiddenCallBack hiddenCallBack = new RvDecorationHiddenCallBack() {
            @Override
            public boolean isHidden(int position) {
                int allCount = mHomeHotNews.size() + 2;//header + footer

                if (0 == position || position >= allCount - 2) {//包含了footer
                    return true;
                }
                return false;
            }
        };

        RvLinearMgItemWithHeaaFoot itemDecoration = new RvLinearMgItemWithHeaaFoot(mContext
                , ResourceUtils.dip2px(mContext, 1)
                , getResources().getDrawable(R.drawable.drawable_ac_bg), hiddenCallBack);
        mHotNewsRv.addItemDecoration(itemDecoration);
        AdapterHomeHotNews homeHotNewsAdapter = new AdapterHomeHotNews(mContext, mHomeHotNews);
        mHomeHotNewsAdapter = new HeaderFooterAdapter<>(homeHotNewsAdapter);
        mHotNewsRv.setAdapter(mHomeHotNewsAdapter);

        mHomeHotNewsAdapter.addHeader(headerView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext);
        mHomeHotNewsAdapter.addFooter(mFooterTv);

        //设置banner参数
        int screenWidth = ResourceUtils.getScreenWidth(mContext);
        mMiddleBanner.getLayoutParams().height = (int) (screenWidth / 3.76F);

        mBanner.getLayoutParams().height = (int) (screenWidth / ImageWidthUtils.nomalBannerImageRote());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext, mTopBannerList.get(position - 1));
            }
        });
        //设置轮播时间
        mBanner.setDelayTime(5000);
        mBanner.setImageLoader(new BannerImageLoader());

        //闲置专场
        int itemWidth = (int) ((screenWidth - ResourceUtils.dip2px(mContext, 8) * 4) / 3.5F);
        int mUnuseHeight = (int) (itemWidth * 3 / 4F);
        mUnuseSpecRv.getLayoutParams().height = mUnuseHeight;

        mRecyUtils = new RecycleViewUtils().with(mHotNewsRv);
    }

    /**
     * 热门服务
     */
    private void initHomeHotServerData() {
        mHomeHotServerAdapter = new HomeHotServerCateAdapter(mContext, mHotServiceList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
//                    if (MyApplication.isServerHidden()) {
//                        DialogUtils.showCustomViewDialog(mContext,
//                                getResources().getString(R.string.server_hidden_tips)
//                                , "确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//
//                        return;
//                    }

                    ServerCategory serviceList = mHotServiceList.get(position);
                    if (serviceList != null) {
                        String serId = serviceList.getServiceCategoryID();
                        Intent toLtIt = new Intent(mContext, ServiceListNewActivity.class);
                        toLtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE, ServerActivity.SERVER_TYPE_SERVER);
                        toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID, serId);
                        toLtIt.putExtra(SkipUtils.INTENT_TITLE, serviceList.getCategoryTitle());
                        startActivity(toLtIt);
                    }
                }
            }
        });
        mRvHotServer.setAdapter(mHomeHotServerAdapter);
    }

    /**
     * 闲置专场
     */
    private void initUnuseSpecData() {
        mUnuseCateAdapter = new HomeUnuseCategoryAdapter(mContext, mUnuseCateList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
                    UnusedCategory category = mUnuseCateList.get(position);
                    if (category != null) {
                        Intent toLiIt = new Intent(mContext, UnuseGoodListActivity.class);
                        toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID, category.getCategoryID());
                        mContext.startActivity(toLiIt);
                    }
                }
            }
        });
        mUnuseSpecRv.setAdapter(mUnuseCateAdapter);
    }

    /**
     * 最新需求
     */
    private void initHomeNeedData() {
        mHomeNeedAdapter = new HomeNeedAdapter(mContext, mLatestNeedList);
        mLvHomeNeed.setAdapter(mHomeNeedAdapter);
    }

    /**
     * 资讯分类
     */
    private void initNewsCategory() {
        mTopCateAdapter = new AdapterNomalOptionCategory(mContext, mShowRvCateList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
                    if (mCurPosition == position) {
                        return;
                    }

                    mShowRvCateList.get(mCurPosition).setSelected(false);
                    mShowRvCateList.get(position).setSelected(true);
                    mShowCateAdapter.notifyDataSetChanged();
                    mTopCateAdapter.notifyDataSetChanged();

                    mCurPosition = position;
//                    ViewUtil.moveToPosition(mCateManager,mCategoryRv ,mCurPosition);
//                    ViewUtil.moveToPosition(mTopCateManager,mCategoryTopRv ,mCurPosition);
                    mIsChangeType = true;
                    resetLvContent();
                }
            }
        });
        mTopCateManager = new LinearLayoutManager(mContext);
        mTopCateManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryTopRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext, 10)
                , getResources().getDrawable(R.drawable.drawable_white_trans)
                , RvLinearManagerItemDecoration.HORIZONTAL_LIST));
        mCategoryTopRv.setLayoutManager(mTopCateManager);
        mCategoryTopRv.setAdapter(mTopCateAdapter);

        mShowCateAdapter = new AdapterNomalOptionCategory(mContext, mShowRvCateList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
                    if (mCurPosition == position) {
                        return;
                    }

                    mShowRvCateList.get(mCurPosition).setSelected(false);
                    mShowRvCateList.get(position).setSelected(true);
                    mShowCateAdapter.notifyDataSetChanged();
                    mTopCateAdapter.notifyDataSetChanged();

                    mCurPosition = position;
//                    ViewUtil.moveToPosition(mCateManager,mCategoryRv ,mCurPosition);
//                    ViewUtil.moveToPosition(mTopCateManager,mCategoryTopRv ,mCurPosition);

                    resetLvContent();
                }
            }
        });
        mCateManager = new LinearLayoutManager(mContext);
        mCateManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext, 10)
                , getResources().getDrawable(R.drawable.drawable_white_trans)
                , RvLinearManagerItemDecoration.HORIZONTAL_LIST));
        mCategoryRv.setLayoutManager(mCateManager);
        mCategoryRv.setAdapter(mShowCateAdapter);

        mCategoryTopRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mCategoryRv.scrollBy(dx, dy);
            }
        });
        mCategoryRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mCategoryTopRv.scrollBy(dx, dy);
            }
        });
    }

    @Override
    protected void initView(View view) {
        mScreenHeight = ResourceUtils.getScreenHeight(mContext);

        mMessageCountTv = view.findViewById(R.id.frag_home_msg_tv);

        mRefreshLay = view.findViewById(R.id.frag_home_refresh_lay);
        mTopMainLay = view.findViewById(R.id.ll_top_fixed_main_top_lay);
        mHotNewsRv = view.findViewById(R.id.lv_unused);
        mCategoryTopRv = view.findViewById(R.id.frag_home_news_category_top_rv);
        mLocationTv = view.findViewById(R.id.frag_home_location_tv);
        mLlTopFixed = view.findViewById(R.id.ll_top_fixed_main_view);
        mHotNewsRv.setFocusable(false);
        mCategoryTopRv.setFocusable(false);

        View searchLay = view.findViewById(R.id.fragment_home_search_lay);
        View msgLay = view.findViewById(R.id.frag_home_msg_iv);
        msgLay.setOnClickListener(this);
        searchLay.setOnClickListener(this);

        initHeaderView();

        initHomeNeedData();
        initHomeHotServerData();
        initUnuseSpecData();
        initNewsCategory();
    }

    /**
     * init header
     */
    private void initHeaderView() {
        View headerLay = LayoutInflater.from(mContext).inflate(R.layout.header_fragment_home, null);

        mHotQuestionVf = headerLay.findViewById(R.id.frag_home_hot_question_vf);
        mLvHomeNeed = headerLay.findViewById(R.id.lv_need);
        mRvHotServer = headerLay.findViewById(R.id.rv_hot_server);
        mLeftCountTv = headerLay.findViewById(R.id.header_home_left_count_tv);
        mRightCountTv = headerLay.findViewById(R.id.header_home_right_count_tv);
        mBanner = headerLay.findViewById(R.id.banner);
        mMiddleBanner = headerLay.findViewById(R.id.header_frag_home_banner_middle);
        mCategoryRv = headerLay.findViewById(R.id.frag_home_news_category_rv);
        mUnuseSpecRv = headerLay.findViewById(R.id.frag_home_unuse_lv);

        //导航操作
        View zhigongLay = headerLay.findViewById(R.id.frag_home_top_nav_one_lay);
        View xuqiuLay = headerLay.findViewById(R.id.frag_home_top_nav_two_lay);
        View zhaopinLay = headerLay.findViewById(R.id.frag_home_top_nav_three_lay);
        View xianzhiLay = headerLay.findViewById(R.id.frag_home_top_nav_four_lay);
        View wendaLay = headerLay.findViewById(R.id.frag_home_top_nav_five_lay);
        zhigongLay.setOnClickListener(this);
        xuqiuLay.setOnClickListener(this);
        zhaopinLay.setOnClickListener(this);
        xianzhiLay.setOnClickListener(this);
        wendaLay.setOnClickListener(this);

        //更多
        View hotNeedsLay = headerLay.findViewById(R.id.header_frag_home_need_more_lay);
        View jobLay = headerLay.findViewById(R.id.header_frag_home_job_more_lay);
        View leftLay = headerLay.findViewById(R.id.header_frag_home_job_left_lay);
        View rightLay = headerLay.findViewById(R.id.header_frag_home_job_right_lay);
        View fuwuLay = headerLay.findViewById(R.id.frag_home_server_tips_tv);
        View sendNeedLay = headerLay.findViewById(R.id.frag_home_needs_send_tv);
        mNeedCountTv = headerLay.findViewById(R.id.frag_home_need_count_tv);
        View sendUnuseLay = headerLay.findViewById(R.id.frag_home_unuse_send_lay);
        View unuseCountLay = headerLay.findViewById(R.id.header_frag_home_unuse_lay);
        mUnuseCountTv = headerLay.findViewById(R.id.frag_home_unuse_count_tv);
        sendUnuseLay.setOnClickListener(this);
        fuwuLay.setOnClickListener(this);
        sendNeedLay.setOnClickListener(this);
        hotNeedsLay.setOnClickListener(this);
        jobLay.setOnClickListener(this);
        leftLay.setOnClickListener(this);
        rightLay.setOnClickListener(this);
        unuseCountLay.setOnClickListener(this);

        mLvHomeNeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goNextActivity(NeedsActivity.class);
            }
        });

        mHotQuestionVf.setFocusable(false);
        mLvHomeNeed.setFocusable(false);
        mRvHotServer.setFocusable(false);
        mBanner.setFocusable(false);
        mMiddleBanner.setFocusable(false);
        mCategoryRv.setFocusable(false);
        mUnuseSpecRv.setFocusable(false);

        initSpecUnuseRecyclerView();
        initHotServerRecyclerView();
        initHomeNewsData(headerLay);

        ivInstallCate = headerLay.findViewById(R.id.iv_hot);
        headerLay.findViewById(R.id.tv_find_install).setOnClickListener(this);
    }

    /**
     * 2018-11-18
     * 修改为固定三个图标
     */
    private void initHotServerRecyclerView() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 3);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvHotServer.setLayoutManager(linearLayoutManager);
        mRvHotServer.setHasFixedSize(true);
    }

    private void initSpecUnuseRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUnuseSpecRv.setLayoutManager(linearLayoutManager);
        mUnuseSpecRv.setHasFixedSize(true);
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initListener() {
        mLocationTv.setOnClickListener(this);
        mHotNewsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                mScrollY += dy ;

                mScrollY = mRecyUtils.getScrollY();

                LogUtils.showLog("testScroll", "scrollY=" + mScrollY + ",getScrollY=" + mRecyUtils.getScrollY());

                if (mHotNewsRv.getChildCount() > 1) {
                    if (mCategoryTopRv != null) {
                        mCategoryRv.getLocationOnScreen(mFirstItemLocation);

                        if (0 == mHeaderCateLimit) {
                            int[] cateLoca = new int[2];
                            mCategoryTopRv.getLocationOnScreen(cateLoca);
                            mHeaderCateLimit = cateLoca[1];
                        }

                        int marTop = mFirstItemLocation[1];
                        mCategoryTopRv.setVisibility(marTop <= mHeaderCateLimit ? View.VISIBLE : View.INVISIBLE);
                    }
                }

                if (0 == mScrollLimitHeight) {
                    mScrollLimitHeight = mBanner.getMeasuredHeight() - mLlTopFixed.getMeasuredHeight();
                }

                if (mScrollY <= 0) {
                    mLlTopFixed.setAlpha(0F);

                } else if (mScrollY >= mScrollLimitHeight) {
                    if (mLlTopFixed.getAlpha() != 1F) {
                        mLlTopFixed.setAlpha(1F);
                    }
                } else {
                    float alpha = (float) mScrollY / mScrollLimitHeight;
                    mLlTopFixed.setAlpha(alpha);
                }

                if (!mIsRefresh && mIsMore && !mIsLoading
                        && mHomeHotNews.size() > 5 && ViewUtil.isSlideToBottom(mHotNewsRv)) {
                    requestHomeHotNewsData();
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefresh = true;
                mCurPage = 1;

                refreshAllView();
            }
        });
    }

    private void goDirectDealActivity() {
        Intent intent = new Intent(mContext, DirectSellingActivity.class);
        startActivity(intent);
    }

    private void goQuestionActivity() {
        Intent intent = new Intent(mContext, QuestionActivity.class);
        startActivity(intent);
    }

    private void goNeedsActivity() {
        Intent intent = new Intent(mContext, NeedsActivity.class);
        startActivity(intent);
    }

    private void goGiveJobActivity() {
        Intent intent = new Intent(mContext, GiveJobActivity.class);
        startActivity(intent);
    }

    private void goUnuseActivity() {
        Intent intent = new Intent(mContext, UnusedActivity.class);
        startActivity(intent);
    }

    private void goServerActivity(int type) {
        Intent intent = new Intent(mContext, ServerActivity.class);
        intent.putExtra(SkipUtils.INTENT_SERVER_TYPE, type);
        startActivity(intent);
    }

    private void goNewsActivity() {
        Intent intent = new Intent(mContext, NewsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.header_frag_home_need_more_lay) {//最新需求

            Intent toNlIt = new Intent(mContext, NeedsActivity.class);
            startActivity(toNlIt);

        } else if (i == R.id.header_frag_home_job_more_lay) {//求职招聘

            Intent toJobIt = new Intent(mContext, GiveJobActivity.class);
            startActivity(toJobIt);

        } else if (i == R.id.header_frag_home_job_left_lay) {

//            Intent toWkIt = new Intent(mContext, JobListActivity.class);
//            toWkIt.putExtra(SkipUtils.INTENT_JOB_TYPE , 1) ;//1是推荐工作
//            startActivity(toWkIt);
            Intent toJobIt = new Intent(mContext, GiveJobActivity.class);
            startActivity(toJobIt);

        } else if (i == R.id.header_frag_home_job_right_lay) {

//            Intent toLtIt = new Intent(mContext, ResumeListActivity.class);
//            toLtIt.putExtra(SkipUtils.INTENT_RESUME_TYPE , 3) ;//3是推荐简历
//            startActivity(toLtIt);
            Intent toJobIt = new Intent(mContext, GiveJobActivity.class);
            startActivity(toJobIt);

        } else if (i == R.id.frag_home_location_tv) {

            Intent toCtIt = new Intent(mContext, SelectProvinceBySortActivity.class);
            startActivityForResult(toCtIt, REQUEST_CODE_LOCATION);

        } else if (i == R.id.fragment_home_search_lay) {

            goNextActivity(SearchIndexActivity.class);

        } else if (i == R.id.frag_home_msg_iv) {
            SkipUtils.toMessageCenterActivity(mContext);
            SPUtils.newIntance(mContext).resetUnreadMessage();
            mMessageCountTv.setVisibility(View.GONE);
        } else if (i == R.id.frag_home_top_nav_one_lay) {//bg_index_cjzg
            goDirectDealActivity();
        } else if (i == R.id.frag_home_top_nav_two_lay) {//bg_index_hyxq
            goNeedsActivity();
        } else if (i == R.id.frag_home_top_nav_three_lay) {//bg_index_hyzp
            goGiveJobActivity();
        } else if (i == R.id.frag_home_top_nav_four_lay
                || R.id.header_frag_home_unuse_lay == i) {//bg_index_hyxz
            goUnuseActivity();
        } else if (i == R.id.frag_home_top_nav_five_lay) {//bg_index_hywd
            goQuestionActivity();
        } else if (i == R.id.frag_home_needs_send_tv) {//一键发布需求
            goNextActivity(NeedsSendActivity.class);
        } else if (i == R.id.frag_home_server_tips_tv) {//服务
//            if (MyApplication.isServerHidden()) {
//                DialogUtils.showCustomViewDialog(mContext,
//                        getResources().getString(R.string.server_hidden_tips)
//                        , "确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//
//                return;
//            }

            goNextActivity(ServiceActivity.class);
//            goNextActivity(ServerActivity.class);
        } else if (i == R.id.frag_home_unuse_send_lay) {//闲置发布
            goNextActivity(UnusedSendActivity.class);
        } else if (i == R.id.tv_find_install) {
            if (mHotServiceList.isEmpty()) {
                return;
            }
            ServerCategory serviceList = mHotServiceList.get(0);
            if (serviceList != null) {
                String serId = serviceList.getServiceCategoryID();
                Intent toLtIt = new Intent(mContext, ServiceListNewActivity.class);
                toLtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE, ServerActivity.SERVER_TYPE_SERVER);
                toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID, serId);
                toLtIt.putExtra(SkipUtils.INTENT_TITLE, serviceList.getCategoryTitle());
                startActivity(toLtIt);
            }
        }
    }


    /**
     * 填充热门话题
     */
    private void updateQuestion() {
        mHotQuestionVf.stopFlipping();
        mHotQuestionVf.removeAllViews();

        if (mTopicList.size() == 0) {
            mHotQuestionVf.setVisibility(View.INVISIBLE);
        } else {
            mHotQuestionVf.setVisibility(View.VISIBLE);

            for (TopicList question : mTopicList) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.lay_hot_question_item_new, null);
                TextView contentTv = view.findViewById(R.id.lay_hot_question_content_tv);

                final String topicId = question.getPostID();
                contentTv.setText(StringUtils.convertNull(question.getTitle()));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toDtIt = new Intent(mContext, TopicDetailActivity.class);
                        toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID, topicId);
                        mContext.startActivity(toDtIt);
                    }
                });

                mHotQuestionVf.addView(view);
            }

            if (mTopicList.size() > 1) {
                mHotQuestionVf.startFlipping();
            }
        }
    }

    /**
     * 顶部banner
     */
    private void requestHomeBanner() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "APPIndexHead");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("getTopBanner", mContext, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean);
                        List<String> imageUrl = new ArrayList<>(mTopBannerList.size());
                        for (AdvBanner banner : mTopBannerList) {
                            imageUrl.add(banner.getImgUrl());
                        }

                        mBanner.update(imageUrl);

                        if (mIsRefresh) {
                            requestHomeMiddleBanner();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestHomeMiddleBanner();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestHomeMiddleBanner();
                        }
                    }
                });
    }

    /**
     * 中间banner
     */
    private void requestHomeMiddleBanner() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "AppIndexMiddle");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("getMiddleBanner", mContext, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mMiddleBannerList.clear();
                        mMiddleBannerList.addAll(bean);
                        List<String> imageUrl = new ArrayList<>(mMiddleBannerList.size());
                        for (AdvBanner banner : mMiddleBannerList) {
                            imageUrl.add(banner.getImgUrl());
                        }

                        //设置banner动画效果
                        mMiddleBanner.setBannerAnimation(Transformer.Default);
                        //设置轮播时间
                        mMiddleBanner.setDelayTime(5000);
                        mMiddleBanner.setImageLoader(new BannerImageLoader());
                        mMiddleBanner.setImages(imageUrl);
                        mMiddleBanner.start();
                        mMiddleBanner.setOnBannerClickListener(new OnBannerClickListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                SkipUtils.intentToOtherByAdvId(mContext, mMiddleBannerList.get(position - 1));
                            }
                        });
                        if (mIsRefresh) {
                            requestHotQuestion();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestHotQuestion();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestHotQuestion();
                        }
                    }
                });
    }

    /**
     * 热门话题
     */
    private void requestHotQuestion() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "1");
        para.put("PageSize", "5");

        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_HOT_QUESTION, false, para,
                new RequestListCallBack<TopicList>("requestHotQuestion", mContext, TopicList.class) {
                    @Override
                    public void onSuccessResult(List<TopicList> bean) {
                        mTopicList.clear();
                        mTopicList.addAll(bean);

                        updateQuestion();

                        if (mIsRefresh) {
                            requestLatestNeedHead();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestLatestNeedHead();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestLatestNeedHead();
                        }
                    }
                });
    }

    /**
     * 最新需求
     */
    private void requestLatestNeedHead() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "1");
        para.put("PageSize", "3");
        para.put("AreaId", "" + MyApplication.getCurrentCityId());
        para.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_LATEST_NEEDS, false, para,
                new RequestListCallBack<NeedsList>("getAppNeedHead", mContext, NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {
                        mLatestNeedList.clear();

                        int size = bean.size();
                        if (size > 3) {
                            size = 3;
                        }
                        for (int x = 0; x < size; x++) {
                            NeedsList list = bean.get(x);
                            mLatestNeedList.add(list);
                        }
                        mHomeNeedAdapter.notifyDataSetChanged();

                        if (mIsRefresh) {
                            requestAppServerHead();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestAppServerHead();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestAppServerHead();
                        }
                    }
                });
    }

    /**
     * 热门服务分类--行业服务的一级分类
     */
    private void requestAppServerHead() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("id", "2");
        paramMap.put("Layer", "1");

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getAppServerHead", mContext, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        mHotServiceList.clear();
                        if (bean != null && bean.size() > 3) {
                            mHotServiceList.addAll(bean.subList(0, 3));
                        } else {
                            mHotServiceList.addAll(bean);
                        }
                        GlideUtils.loader(mHotServiceList.get(0).getImgUrl(),ivInstallCate);
                        mHomeHotServerAdapter.notifyDataSetChanged();
                        if (mIsRefresh) {
                            requestAppWorkHead();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestAppWorkHead();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestAppWorkHead();
                        }
                    }
                });
    }

    /**
     * 求职招聘--数量
     */
    private void requestAppWorkHead() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("AreaId", MyApplication.getCurrentCityId());
        para.put("AreaType", MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_WORK_RESUME_COUNT, para
                , new RequestObjectCallBack<HomeWorkCount>("requestAppWorkHead", mContext, HomeWorkCount.class) {
                    @Override
                    public void onSuccessResult(HomeWorkCount bean) {
                        if (bean != null) {
                            mWorkCount = bean;
                            mLeftCountTv.setText(bean.getResumeCount() + "人");
                            mRightCountTv.setText(bean.getRecruitCount() + "份");
                            mNeedCountTv.setText(bean.getNeedCount() + "人");
                            mUnuseCountTv.setText(bean.getOldProductCountt() + "件");
                        }

                        if (mIsRefresh) {
                            getUnuseCategory();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            getUnuseCategory();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            getUnuseCategory();
                        }
                    }
                });
    }

    /**
     * 获取分类
     */
    private void getUnuseCategory() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Keyword", "");
        paramMap.put("Id", "1");
        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<UnusedCategory>("闲置分类", mContext, UnusedCategory.class) {
                    @Override
                    public void onSuccessResult(List<UnusedCategory> bean) {
                        mUnuseCateList.clear();
                        mUnuseCateList.addAll(bean);
                        mUnuseCateAdapter.notifyDataSetChanged();

                        if (mIsRefresh) {
                            requestTopBarData();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            requestTopBarData();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            requestTopBarData();
                        }
                    }
                });
    }

    /**
     * 根据分类切换显示
     */
    private void resetLvContent() {
        if (mCurPosition < mNewsCategoryList.size()) {
            categoryID = mNewsCategoryList.get(mCurPosition).getCategoryID();

            mCurPage = 1;
            requestHomeHotNewsData();
        } else {
            mIsChangeType = false;

            if (mIsRefresh) {
                mRefreshLay.finishRefresh();
            }
        }
    }

    private void requestTopBarData() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "News");
        NetUtils.getDataFromServerByPost(mContext, Urls.NEWS_CATEGORY_LIST, false, para,
                new RequestListCallBack<NewsCategory>("行业新闻标题栏", mContext, NewsCategory.class) {
                    @Override
                    public void onSuccessResult(List<NewsCategory> bean) {
                        mNewsCategoryList.clear();
                        mNewsCategoryList.add(new NewsCategory("", "推荐"));
                        mNewsCategoryList.addAll(bean);

                        mCurPosition = 0;
                        mShowRvCateList.clear();
                        int size = mNewsCategoryList.size();
                        for (int x = 0; x < size; x++) {
                            NewsCategory category = mNewsCategoryList.get(x);
                            if (category.getCategoryID().equals(categoryID)) {
                                mCurPosition = x;
                            }
                            mShowRvCateList.add(new NomalRvCategory(category));
                        }

                        mShowCateAdapter.notifyDataSetChanged();
                        if (mCurPosition < mShowRvCateList.size()) {
                            mShowRvCateList.get(mCurPosition).setSelected(true);
                            ViewUtil.moveToPosition(mCateManager, mCategoryRv, mCurPosition);
                            ViewUtil.moveToPosition(mTopCateManager, mCategoryTopRv, mCurPosition);
                        }

                        resetLvContent();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mIsRefresh) {
                            resetLvContent();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mIsRefresh) {
                            resetLvContent();
                        }
                    }
                });
    }

    /**
     * 首页热门资讯列表
     */
    private void requestHomeHotNewsData() {
        if (1 == mCurPage) {
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string));
        }

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "" + mCurPage);
        para.put("PageSize", "10");
        String url = Urls.NEWS_LIST;

        if (!"".equals(categoryID)) {
            para.put("CategoryID", categoryID);
        } else {
            url = Urls.NEWS_LIST_HOT;
        }
        NetUtils.getDataFromServerByPost(mContext, url, para,
                new RequestListCallBack<NewsList>("requestHomeHotNewsData", mContext, NewsList.class) {
                    @Override
                    public void onSuccessResult(List<NewsList> bean) {
                        if (1 == mCurPage) {
                            mHomeHotNews.clear();
                            mHomeHotNewsAdapter.notifyDataSetChanged();
                        }

                        mHomeHotNews.addAll(bean);
                        mHomeHotNewsAdapter.notifyDataSetChanged();

                        if (bean.size() < 10) {
                            mIsMore = false;
                        } else {
                            mIsMore = true;
                            mCurPage++;
                        }

                        finishRequest();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        finishRequest();
                    }

                    @Override
                    public void onFail(Exception e) {
                        finishRequest();
                    }
                });
    }

    /**
     * 完成请求
     */
    private void finishRequest() {
        mIsLoading = false;

        if (mIsRefresh) {
            mIsRefresh = false;
            mRefreshLay.finishRefresh();
        }

        if (mIsChangeType) {
            mIsChangeType = false;
            mHotNewsRv.scrollToPosition(1);
        }

        if (!mIsMore) {
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if (mHomeHotNews.size() == 0) {
            mFooterTv.setText("暂无推荐");
        }

    }

    /**
     * 定位成功
     *
     * @param cityName cityName
     */
    public void setCurLocation(String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            return;
        }

        mLocationTv.setText(cityName);

        //暂时放这儿，因为接口要求传定位信息
        updateCityChangeDisplay();
    }


    /**
     * 刷新城市更换影响的模块
     */
    public void updateCityChangeDisplay() {
        //暂定全部清空数据，防止请求异常导致数据更新失败，要么没数据，要么就是选择的城市的数据

        mLatestNeedList.clear();
        mHomeNeedAdapter.notifyDataSetChanged();

        mHotServiceList.clear();
        mHomeHotServerAdapter.notifyDataSetChanged();

        requestLatestNeedHead();
        requestAppServerHead();
        requestAppWorkHead();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();

        startAutoPlay();
    }

    private void startAutoPlay() {
        if (mBanner != null) {
            mBanner.startAutoPlay();
        }
        if (mMiddleBanner != null) {
            mMiddleBanner.startAutoPlay();
        }

        if (mHotQuestionVf != null && mTopicList.size() > 1) {
            mHotQuestionVf.startFlipping();
        }
    }

    private void stopAutoPlay() {
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
        if (mMiddleBanner != null) {
            mMiddleBanner.stopAutoPlay();
        }

        if (mHotQuestionVf != null) {
            mHotQuestionVf.stopFlipping();
        }
    }

    /**
     * 刷新
     */
    private void refreshAllView() {
        requestHomeBanner();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (mIsInit) {
            if (!hidden) {
                startAutoPlay();

                if (mTopBannerList.size() == 0) {
                    requestHomeBanner();
                }
                if (mTopicList.size() == 0) {
                    requestHotQuestion();
                }
                if (mLatestNeedList.size() == 0) {
                    requestLatestNeedHead();
                }
                if (mHotServiceList.size() == 0) {
                    requestAppServerHead();
                }
                if (mMiddleBannerList.size() == 0) {
                    requestHomeMiddleBanner();
                }
                if (null == mWorkCount) {
                    requestAppWorkHead();
                }

                if (mUnuseCateList.size() == 0) {
                    getUnuseCategory();
                }

                if (mNewsCategoryList.size() == 0) {
                    requestTopBarData();
                } else {
                    if (mHomeHotNews.size() == 0) {
                        requestHomeHotNewsData();
                    }
                }
            } else {
                stopAutoPlay();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_LOCATION == requestCode) {

            if (Activity.RESULT_OK == resultCode && data != null) {
                int locationType = data.getIntExtra(SkipUtils.LOCATION_TYPE, SkipUtils.LOCATION_TYPE_ALL);
                String locationContent = data.getStringExtra(SkipUtils.LOCATION_CONTENT_NAME);
                String locationContentId = data.getStringExtra(SkipUtils.LOCATION_CONTENT_ID);

                if (!"".equals(StringUtils.convertNull(locationContent))) {
                    mLocationTv.setText(locationContent);
                }

                if (mCityChangeListener != null) {
                    mCityChangeListener.onCityChanged(locationType, locationContentId, locationContent);
                }
            }
        } else if (REQUEST_CODE_DIR_LOGIN == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                goDirectDealActivity();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.UnreadMsgEvent ev) {
        mMessageCountTv.setVisibility(ev.isHas() ? View.VISIBLE : View.GONE);
//        mMessageCountTv.setText("") ;
    }

    /**
     * 更改未读消息数量
     *
     * @param count count
     */
    public void changeUnreadMessageCount(int count) {
        mMessageCountTv.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
//        mMessageCountTv.setText(count > 99 ? "99+" : ("" + count));

        //暂时只显示一个小红点
    }

}
