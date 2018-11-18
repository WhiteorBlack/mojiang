package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.Bean.SiftCity;
import cn.idcby.jiajubang.Bean.SiftWorkPost;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterResumeList;
import cn.idcby.jiajubang.adapter.AdapterSiftChildPost;
import cn.idcby.jiajubang.adapter.AdapterSiftCity;
import cn.idcby.jiajubang.adapter.AdapterSiftParentPost;
import cn.idcby.jiajubang.adapter.AdapterSiftProvince;
import cn.idcby.jiajubang.adapter.AdapterSiftWordType;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 简历列表
 * Created on 2018/3/27.
 *
 * 为了区分推荐和普通全职、兼职简历列表，
 * 把推荐剥离出去，用{@link ResumeHotListActivity}
 * 当前界面是普通简历列表，有筛选功能，如果后期需求是所有简历都有筛选，可以只用当前界面
 *
 * 2018-09-14 18:17:37
 * 位置默认选择当前定位的城市，即首页选择的城市
 */

public class ResumeListActivity extends BaseActivity {
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

    private int mListType = 0 ;
    private String mSearchKey = "" ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private LoadingDialog mLoadingDialog ;

    /****筛选相关start****/
    private ImageView mNavLocationIv;
    private TextView mNavLocationTv;
    private ImageView mNavPostIv;
    private TextView mNavPostTv;
    private ImageView mNavOtherIv;

    //view 相关
    private View mSiftParentLay ;
    //位置
    private View mSiftLocationLay ;
    //职位
    private View mSiftPostLay ;
    //筛选
    private View mSiftOtherLay ;
    private TextView mSiftOtherTypeAllTv;
    private TextView mSiftOtherTypeHalfTv;

    //数据相关
    private int mSiftState = 0 ;
    private static final int SIFT_STATE_NONE = 0 ; //隐藏状态
    private static final int SIFT_STATE_LOCATION = 1 ;//位置展开
    private static final int SIFT_STATE_POST = 2 ;//职位展开
    private static final int SIFT_STATE_OTHER = 3 ;//筛选展开

    //城市
    private Map<String,List<SiftCity>> mAllCityMap = new HashMap<>();//所有省份以及下级城市，key=省份Id
    private List<SiftCity> mAllProvinceList = new ArrayList<>();
    private List<SiftCity> mCurCityList = new ArrayList<>() ;//当前省份下的城市
    private int mCurProPosition = 0;
    private int mCurCityPosition ;
    private AdapterSiftProvince mProvinceAdapter ;
    private AdapterSiftCity mCityAdapter ;
    //选中的记录
    private int mSelectedProPosition = 0 ;//选中的省份的position，为了防止切换了省，但是没选市，导致mCurCityList数据丢失
    private int mSelectedCityPosition = 0 ;//选中的城市的position

    //职位
    private Map<String,List<SiftWorkPost>> mAllWorkPostMap = new HashMap<>() ;//所有职位下的二级职位，key=职位id
    private List<SiftWorkPost> mAllPostList = new ArrayList<>() ;
    private List<SiftWorkPost> mCurChildPostList = new ArrayList<>() ;//当前职位下的二级职位
    private int mCurParentPostPosition = 0 ;
    private int mCurChildPostPosition = 0 ;
    private AdapterSiftParentPost mParentPostAdapter ;
    private AdapterSiftChildPost mChildPostAdapter ;
    //选中的记录
    private int mSelectedParPostPosition = 0 ;//选中的职位的position，为了防止切换了职位，但是没选二级职位，导致mSelectedChiPostPosition数据丢失
    private int mSelectedChiPostPosition = 0 ;//选中的二级职位的position

    private boolean mIsFirstLoad = true ;

    //右边的筛选内容相关
    private List<WordType> mEduList = new ArrayList<>() ;
    private List<WordType> mExpList = new ArrayList<>() ;
    private AdapterSiftWordType mEduAdapter ;
    private AdapterSiftWordType mExpAdapter ;

    private List<String> mEduCheckList = new ArrayList<>() ;
    private List<String> mExpCheckList = new ArrayList<>() ;

    private int mSiftSexType = 0 ; // 0 全部 ，1 男 2 女
    private static final int SEX_TYPE_NONE = 0 ;
    private static final int SEX_TYPE_ALL = 1 ;
    private static final int SEX_TYPE_HALF = 2 ;
    private int mSelectedSexType = 0 ;

    /****筛选相关end****/


    //默认的位置
    private String mDefaultCityId ;
    private String mDefaultCityType ;



    @Override
    public int getLayoutID() {
        return R.layout.activity_resume_list ;
    }

    @Override
    public void initView() {
        mDefaultCityId = MyApplication.getCurrentCityId() ;
        mDefaultCityType = MyApplication.getCurrentCityType() ;

        mListType = getIntent().getIntExtra(SkipUtils.INTENT_RESUME_TYPE , 0) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        mLoadingDialog = new LoadingDialog(mContext) ;
        mLoadingDialog.setCancelable(false);

        mRightLay = findViewById(R.id.acti_resume_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_resume_list_search_lay) ;
        View backIv = findViewById(R.id.acti_resume_list_back) ;
        mSearchKeyTv = findViewById(R.id.acti_resume_list_search_key_tv) ;
        mRightLay.setOnClickListener(this) ;
        searchLay.setOnClickListener(this) ;
        backIv.setOnClickListener(this) ;

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

        initSiftView() ;
    }

    /**
     * 初始化筛选相关布局
     */
    private void initSiftView(){
        View mNavLocationLay = findViewById(R.id.acti_resume_list_nav_location_lay) ;
        mNavLocationIv = findViewById(R.id.acti_resume_list_nav_location_iv) ;
        mNavLocationTv = findViewById(R.id.acti_resume_list_nav_location_tv) ;
        View mNavPostLay = findViewById(R.id.acti_resume_list_nav_post_lay) ;
        mNavPostIv = findViewById(R.id.acti_resume_list_nav_post_iv) ;
        mNavPostTv = findViewById(R.id.acti_resume_list_nav_post_tv) ;
        View mNavOtherLay = findViewById(R.id.acti_resume_list_nav_other_lay) ;
        mNavOtherIv = findViewById(R.id.acti_resume_list_nav_other_iv) ;
        mNavLocationLay.setOnClickListener(this);
        mNavPostLay.setOnClickListener(this);
        mNavOtherLay.setOnClickListener(this);

        //填充数据，如果需要的话
        if(!"".equals(mDefaultCityType) && !"0".equals(mDefaultCityType)){
            String name = MyApplication.getCurrentCityName() ;
            if(!"".equals(name)){
                mNavLocationTv.setText(name) ;
            }
        }

        mSiftParentLay = findViewById(R.id.acti_resume_list_sift_lay) ;
        mSiftParentLay.setOnClickListener(this);

        mSiftLocationLay = findViewById(R.id.acti_resume_list_sift_location_lay) ;
        RecyclerView mSiftLocationLeftRv = findViewById(R.id.acti_resume_list_sift_location_left_rv) ;
        RecyclerView mSiftLocationRightRv = findViewById(R.id.acti_resume_list_sift_location_right_rv) ;
        mSiftLocationLay.setOnClickListener(this);

        mSiftPostLay = findViewById(R.id.acti_resume_list_sift_post_lay) ;
        RecyclerView mSiftPostLeftRv = findViewById(R.id.acti_resume_list_sift_post_left_rv) ;
        RecyclerView mSiftPostRightRv = findViewById(R.id.acti_resume_list_sift_post_right_rv) ;
        mSiftPostLay.setOnClickListener(this);

        mSiftOtherLay = findViewById(R.id.acti_resume_list_sift_other_lay) ;
        GridView mSiftOtherEduLv = findViewById(R.id.acti_resume_list_sift_other_edu_lv) ;
        GridView mSiftOtherExpLv = findViewById(R.id.acti_resume_list_sift_other_exp_lv) ;
        mSiftOtherTypeAllTv = findViewById(R.id.acti_resume_list_sift_other_work_type_all_tv) ;
        mSiftOtherTypeHalfTv = findViewById(R.id.acti_resume_list_sift_other_work_type_half_tv) ;
        View mOtherOkLay = findViewById(R.id.acti_resume_list_sift_other_ok_tv) ;
        View mOtherResetLay = findViewById(R.id.acti_resume_list_sift_other_reset_tv) ;
        mOtherResetLay.setOnClickListener(this);
        mOtherOkLay.setOnClickListener(this);
        mSiftOtherTypeAllTv.setOnClickListener(this);
        mSiftOtherTypeHalfTv.setOnClickListener(this);
        mSiftOtherLay.setOnClickListener(this);

        mSiftLocationLeftRv.setFocusable(false);
        mSiftLocationRightRv.setFocusable(false);
        mSiftPostLeftRv.setFocusable(false);
        mSiftPostRightRv.setFocusable(false);

        //省份
        mSiftLocationLeftRv.setLayoutManager(new LinearLayoutManager(mContext));
//        mSiftLocationLeftRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext ,1)
//                ,getResources().getDrawable(R.drawable.drawable_white)));
        mProvinceAdapter = new AdapterSiftProvince(mContext, mAllProvinceList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                //选中了省份，需要切换城市，如果没有还需要调用接口
                if(position == mCurProPosition || 0 == position){
                    if(position == 0){
                        //先重置之前选的
                        if(mCurProPosition > 0 && mAllProvinceList.size() > mCurProPosition){
                            SiftCity parCity = mAllProvinceList.get(mCurProPosition) ;
                            parCity.setSelected(false);
                            mAllProvinceList.get(0).setSelected(true) ;
                            mProvinceAdapter.notifyDataSetChanged() ;

                            List<SiftCity> cityList = mAllCityMap.get(parCity.getAreaId()) ;
                            if(mSelectedCityPosition > -1 && cityList != null
                                    && cityList.size() > mSelectedCityPosition){
                                cityList.get(mSelectedCityPosition).setSelected(false);
                            }

                            mCurCityList.clear();
                            mCityAdapter.notifyDataSetChanged() ;
                        }

                        //还原之前选中过的
                        if(mSelectedProPosition > 0 && mAllProvinceList.size() > mSelectedProPosition){
                            SiftCity parCity = mAllProvinceList.get(mSelectedProPosition) ;
                            parCity.setSelected(false);

                            List<SiftCity> cityList = mAllCityMap.get(parCity.getAreaId()) ;
                            if(mSelectedCityPosition > -1 && cityList != null
                                    && cityList.size() > mSelectedCityPosition){
                                cityList.get(mSelectedCityPosition).setSelected(false);
                            }
                        }

                        mCurProPosition = 0 ;

                        //清空默认
                        mDefaultCityId = "" ;
                        mDefaultCityType = "";

                        getCity() ; //内部会直接获取数据
                    }
                    return ;
                }

                mAllProvinceList.get(mCurProPosition).setSelected(false);
                mProvinceAdapter.notifyDataSetChanged();

                mAllProvinceList.get(position).setSelected(true);
                mProvinceAdapter.notifyDataSetChanged();
                mCurProPosition = position ;
                getCity() ;
            }
        }) ;
        mSiftLocationLeftRv.setAdapter(mProvinceAdapter) ;

        //城市
        mSiftLocationRightRv.setLayoutManager(new LinearLayoutManager(mContext));
//        mSiftLocationRightRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext ,1)
//                ,getResources().getDrawable(R.drawable.activity_bg_drawable)));
        mCityAdapter = new AdapterSiftCity(mContext, mCurCityList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    //选中了城市，此时需要调接口更新数据

                    //清空默认
                    mDefaultCityId = "" ;
                    mDefaultCityType = "";

                    //还原之前的,注意：需要还原的是选中的那个
                    if(mSelectedProPosition > -1 && mAllProvinceList.size() > mSelectedProPosition){
                        SiftCity parCity = mAllProvinceList.get(mSelectedProPosition) ;
                        List<SiftCity> cityList = mAllCityMap.get(parCity.getAreaId()) ;
                        if(mSelectedCityPosition > -1 && cityList != null
                                && cityList.size() > mSelectedCityPosition){
                            cityList.get(mSelectedCityPosition).setSelected(false);
                            if(mSelectedProPosition == mCurProPosition){
                                mCityAdapter.notifyDataSetChanged() ;
                            }
                        }
                    }

                    mCurCityPosition = position ;
                    mSelectedCityPosition = position ;
                    mSelectedProPosition = mCurProPosition ;

                    if(mCurCityPosition > -1 && mCurCityList.size() > mCurCityPosition){
                        SiftCity city = mCurCityList.get(mCurCityPosition) ;
                        city.setSelected(true);
                        mCityAdapter.notifyDataSetChanged() ;

                        //填充内容
                        String sortName ;
                        String cityId = city.getAreaId() ;
                        if(!"".equals(cityId)){
                            sortName = city.getAreaName() ;
                        }else{
                            sortName = mAllProvinceList.get(mSelectedProPosition).getAreaName() ;
                        }

                        mNavLocationTv.setText(sortName) ;
                    }

                    changeSiftViewState(SIFT_STATE_LOCATION) ;//理论上当前是展开状态，点击了应该是隐藏，直接调用这个即可

                    //重新获取列表内容
                    searchFromList() ;
                }
            }
        }) ;
        mSiftLocationRightRv.setAdapter(mCityAdapter) ;

        //职位
        //一级职位
        mSiftPostLeftRv.setLayoutManager(new LinearLayoutManager(mContext));
//        mSiftPostLeftRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext ,1)
//                ,getResources().getDrawable(R.drawable.drawable_white)));
        mParentPostAdapter = new AdapterSiftParentPost(mContext, mAllPostList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                //选中了职位，需要切换二级职位，如果没有还需要调用接口
                if(position == mCurParentPostPosition || 0 == position){
                    if(position == 0){
                        //先重置之前选的
                        if(mCurParentPostPosition > 0 && mAllPostList.size() > mCurParentPostPosition){
                            SiftWorkPost parPost = mAllPostList.get(mCurParentPostPosition) ;
                            parPost.setSelected(false);
                            mAllPostList.get(0).setSelected(true) ;
                            mParentPostAdapter.notifyDataSetChanged() ;

                            List<SiftWorkPost> childPostList = mAllWorkPostMap.get(parPost.getWorkPostID()) ;
                            if(mSelectedChiPostPosition > -1 && childPostList != null
                                    && childPostList.size() > mSelectedChiPostPosition){
                                childPostList.get(mSelectedChiPostPosition).setSelected(false);
                            }

                            mCurChildPostList.clear();
                            mChildPostAdapter.notifyDataSetChanged() ;
                        }

                        //还原之前选中过的
                        if(mSelectedParPostPosition > 0 && mAllPostList.size() > mSelectedParPostPosition){
                            SiftWorkPost parPost = mAllPostList.get(mSelectedParPostPosition) ;
                            parPost.setSelected(false);

                            List<SiftWorkPost> childPostList = mAllWorkPostMap.get(parPost.getWorkPostID()) ;
                            if(mSelectedChiPostPosition > -1 && childPostList != null
                                    && childPostList.size() > mSelectedChiPostPosition){
                                childPostList.get(mSelectedChiPostPosition).setSelected(false);
                            }
                        }

                        mCurParentPostPosition = 0 ;

                        getChildPost() ; //内部会直接获取数据
                    }
                    return ;
                }

                mAllPostList.get(mCurParentPostPosition).setSelected(false);
                mParentPostAdapter.notifyDataSetChanged();

                mAllPostList.get(position).setSelected(true);
                mParentPostAdapter.notifyDataSetChanged();
                mCurParentPostPosition = position ;
                getChildPost() ;
            }
        }) ;
        mSiftPostLeftRv.setAdapter(mParentPostAdapter) ;

        //二级职位
        mSiftPostRightRv.setLayoutManager(new LinearLayoutManager(mContext));
//        mSiftPostRightRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext, ResourceUtils.dip2px(mContext ,1)
//                ,getResources().getDrawable(R.drawable.activity_bg_drawable)));
        mChildPostAdapter = new AdapterSiftChildPost(mContext, mCurChildPostList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    //选中了二级职位，此时需要调接口更新数据

                    //还原之前的,注意：需要还原的是选中的那个
                    if(mSelectedParPostPosition > -1 && mAllPostList.size() > mSelectedParPostPosition){
                        SiftWorkPost parPost = mAllPostList.get(mSelectedParPostPosition) ;
                        List<SiftWorkPost> workPosts = mAllWorkPostMap.get(parPost.getWorkPostID()) ;
                        if(mSelectedChiPostPosition > -1 && workPosts != null
                                && workPosts.size() > mSelectedChiPostPosition){
                            workPosts.get(mSelectedChiPostPosition).setSelected(false);

                            if(mSelectedParPostPosition == mCurParentPostPosition){
                                mChildPostAdapter.notifyDataSetChanged() ;
                            }
                        }
                    }

                    mCurChildPostPosition = position ;
                    mSelectedChiPostPosition = position ;
                    mSelectedParPostPosition = mCurParentPostPosition ;

                    if(mCurChildPostPosition > -1 && mCurChildPostList.size() > mCurChildPostPosition){
                        SiftWorkPost childPost = mCurChildPostList.get(mCurChildPostPosition) ;
                        childPost.setSelected(true);
                        mChildPostAdapter.notifyDataSetChanged() ;

                        //填充内容
                        String sortName ;
                        String postId = childPost.getWorkPostID() ;
                        if(!"".equals(postId)){
                            sortName = childPost.getName() ;
                        }else{
                            sortName = mAllPostList.get(mSelectedParPostPosition).getName() ;
                        }

                        mNavPostTv.setText(sortName) ;
                    }

                    changeSiftViewState(SIFT_STATE_POST) ;//理论上当前是展开状态，点击了应该是隐藏，直接调用这个即可

                    //重新获取列表内容
                    searchFromList() ;
                }
            }
        }) ;
        mSiftPostRightRv.setAdapter(mChildPostAdapter) ;

        //学历、经验
        mEduAdapter = new AdapterSiftWordType(mContext, mEduList) ;
        mSiftOtherEduLv.setAdapter(mEduAdapter) ;
        mExpAdapter = new AdapterSiftWordType(mContext, mExpList) ;
        mSiftOtherExpLv.setAdapter(mExpAdapter) ;

        getProvince(false) ;
    }

    /**
     * 确定右边筛选
     */
    private void subOkSift(){
        //学历
        int eduSize = mEduList.size() ;
        mEduCheckList.clear();
        for(int x = 0 ; x < eduSize ; x ++){
            WordType edu = mEduList.get(x) ;
            if(edu.isSelected()){
                mEduCheckList.add(x+"") ;
            }
        }
        //经验
        int expSize = mExpList.size() ;
        mExpCheckList.clear();
        for(int x = 0 ; x < expSize ; x ++){
            WordType edu = mExpList.get(x) ;
            if(edu.isSelected()){
                mExpCheckList.add(x+"") ;
            }
        }

        mSelectedSexType = mSiftSexType;
        changeSiftViewState(SIFT_STATE_OTHER) ;

        searchFromList() ;
    }

    /**
     * 重置右边筛选
     */
    private void resetOtherSift() {
        //重置学历、经验
        for(WordType exp : mEduList){
            exp.setSelected(false) ;
        }
        mEduAdapter.notifyDataSetChanged() ;

        for(WordType exp : mExpList){
            exp.setSelected(false) ;
        }
        mExpAdapter.notifyDataSetChanged() ;

        mEduCheckList.clear();
        mExpCheckList.clear();

        changeWorkType(SEX_TYPE_NONE) ;
        mSelectedSexType = SEX_TYPE_NONE;

        searchFromList() ;
    }

    /**
     * 切换工作类型--全职、兼职
     * @param type type
     */
    private void changeWorkType(int type){
        if(SEX_TYPE_NONE == mSiftSexType){
            if(SEX_TYPE_ALL == type){
                mSiftOtherTypeAllTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                mSiftOtherTypeAllTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));
            }else if(SEX_TYPE_HALF == type){
                mSiftOtherTypeHalfTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                mSiftOtherTypeHalfTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));
            }

            mSiftSexType = type ;
        }else{
            if(SEX_TYPE_ALL == mSiftSexType){
                mSiftOtherTypeAllTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));
                mSiftOtherTypeAllTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_nomal));

                if(SEX_TYPE_HALF == type){
                    mSiftOtherTypeHalfTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                    mSiftOtherTypeHalfTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));

                    mSiftSexType = type ;
                }else{
                    mSiftSexType = SEX_TYPE_NONE;
                }
            }else{
                mSiftOtherTypeHalfTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));
                mSiftOtherTypeHalfTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_nomal));


                if(SEX_TYPE_ALL == type){
                    mSiftOtherTypeAllTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                    mSiftOtherTypeAllTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));

                    mSiftSexType = type ;
                }else{
                    mSiftSexType = SEX_TYPE_NONE;
                }
            }
        }
    }

    /**
     * 切换筛选布局
     * @param state state
     */
    private void changeSiftViewState(int state){
        if(SIFT_STATE_NONE == mSiftState){//隐藏状态，需要展开
            //直接赋值
            mSiftState = state ;

            //显示父布局
            mSiftParentLay.setVisibility(View.VISIBLE);

            boolean isLocation = SIFT_STATE_LOCATION == mSiftState ;
            boolean isPost= SIFT_STATE_POST == mSiftState ;
            boolean isOther = SIFT_STATE_OTHER == mSiftState ;

            //显示相关布局
            mSiftLocationLay.setVisibility(isLocation ? View.VISIBLE : View.GONE);
            mSiftPostLay.setVisibility(isPost ? View.VISIBLE : View.GONE);
            mSiftOtherLay.setVisibility(isOther ? View.VISIBLE : View.GONE);

            //填充数据
            if(isLocation){
                getProvince(false) ;
            }else if(isPost){
                getParentPost(false) ;
            }else{//筛选状态
                if(mEduList.size() == 0){
                    getEduList() ;
                }else if(mExpList.size() == 0){
                    getExpList() ;
                }else{
                    resumeSiftOther() ;
                }
            }
        }else{//显示状态
            //切换的时候，需要把之前的状态还原，防止内容错乱
            if(SIFT_STATE_LOCATION == mSiftState){
                getProvince(true) ;//还原效果写到这里面了
            }else if(SIFT_STATE_POST == mSiftState){
                getParentPost(true) ;
            }else{
                //有bug，如果只获取了学历，经验获取失败，然后操作了学历，再切换，还原效果失败
                if(mEduList.size() == 0){
                    getEduList() ;
                }else if(mExpList.size() == 0){
                    getExpList() ;
                }else{
                    resumeSiftOther() ;
                }
            }

            if(mSiftState == state){//同一个，隐藏
                if(SIFT_STATE_LOCATION == mSiftState){
                    mNavLocationIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                    mSiftLocationLay.setVisibility(View.GONE);
                }else if(SIFT_STATE_POST == mSiftState){
                    mNavPostIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                    mSiftPostLay.setVisibility(View.GONE);
                }else{
                    mSiftOtherLay.setVisibility(View.GONE);
                    mNavOtherIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }

                mSiftParentLay.setVisibility(View.GONE) ;
                mSiftState = SIFT_STATE_NONE ;//重置
            }else{//不是同一个，直接切换
                if(SIFT_STATE_LOCATION == mSiftState){
                    mSiftLocationLay.setVisibility(View.GONE);
                }else if(SIFT_STATE_POST == mSiftState){
                    mSiftPostLay.setVisibility(View.GONE);
                }else{
                    mSiftOtherLay.setVisibility(View.GONE);
                }

                if(SIFT_STATE_LOCATION == state){
                    mSiftLocationLay.setVisibility(View.VISIBLE);
                }else if(SIFT_STATE_POST == state){
                    mSiftPostLay.setVisibility(View.VISIBLE);
                }else{
                    mSiftOtherLay.setVisibility(View.VISIBLE);
                }

                mSiftState = state ;
            }
        }

        //箭头样式
        mNavLocationIv.setImageDrawable(getResources().getDrawable(SIFT_STATE_LOCATION == mSiftState
                ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
        mNavPostIv.setImageDrawable(getResources().getDrawable(SIFT_STATE_POST == mSiftState
                ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
        mNavOtherIv.setImageDrawable(getResources().getDrawable(SIFT_STATE_OTHER == mSiftState
                ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
    }

    /**
     * 还原筛选内容
     */
    private void resumeSiftOther(){
        //学历
        int eduSize = mEduList.size() ;
        for(int x = 0 ; x < eduSize ; x ++){
            WordType edu = mEduList.get(x) ;
            edu.setSelected(mEduCheckList.contains("" + x));
        }
        //经验
        int expSize = mExpList.size() ;
        for(int x = 0 ; x < expSize ; x ++){
            WordType edu = mExpList.get(x) ;
            edu.setSelected(mExpCheckList.contains("" + x));
        }

        if(mSelectedSexType != mSiftSexType){
            if(SEX_TYPE_ALL == mSiftSexType){
                mSiftOtherTypeAllTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));
                mSiftOtherTypeAllTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_nomal));

            }else if(SEX_TYPE_HALF == mSiftSexType){
                mSiftOtherTypeHalfTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));
                mSiftOtherTypeHalfTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_nomal));

            }
            mSiftSexType = mSelectedSexType;

            if(SEX_TYPE_ALL == mSiftSexType){
                mSiftOtherTypeAllTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                mSiftOtherTypeAllTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));
            }else if(SEX_TYPE_HALF == mSiftSexType){
                mSiftOtherTypeHalfTv.setTextColor(mContext.getResources().getColor(R.color.color_good_param_theme));
                mSiftOtherTypeHalfTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_good_param_checked));
            }
        }
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
            if(mSiftState != SIFT_STATE_NONE){
                changeSiftViewState(mSiftState) ;
                return ;
            }

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,1009);
            }else{
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }else if(R.id.acti_resume_list_search_lay == vId){
            if(mSiftState != SIFT_STATE_NONE){
                changeSiftViewState(mSiftState) ;
                return ;
            }

            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_resume_list_back == vId){
            onBackPressed() ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            if(mSiftState != SIFT_STATE_NONE){
                changeSiftViewState(mSiftState) ;
                return ;
            }

            mLv.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }else if(R.id.acti_resume_list_nav_location_lay == vId){//筛选--位置
            changeSiftViewState(SIFT_STATE_LOCATION) ;
        }else if(R.id.acti_resume_list_nav_post_lay == vId){//筛选--职位
            changeSiftViewState(SIFT_STATE_POST) ;
        }else if(R.id.acti_resume_list_nav_other_lay == vId){//筛选--筛选
            changeSiftViewState(SIFT_STATE_OTHER) ;
        }else if(R.id.acti_resume_list_sift_other_ok_tv == vId){//筛选--筛选--确定
            subOkSift() ;
        }else if(R.id.acti_resume_list_sift_other_reset_tv == vId){//筛选--筛选--重置
            resetOtherSift() ;
        }else if(R.id.acti_resume_list_sift_other_work_type_all_tv == vId){//筛选--筛选--全职
            changeWorkType(SEX_TYPE_ALL) ;
        }else if(R.id.acti_resume_list_sift_other_work_type_half_tv == vId){//筛选--筛选--兼职
            changeWorkType(SEX_TYPE_HALF) ;
        }else if(R.id.acti_resume_list_sift_lay == vId){//筛选--父布局--隐藏
            changeSiftViewState(mSiftState) ;
        }else if(R.id.acti_resume_list_sift_location_lay == vId
                || R.id.acti_resume_list_sift_post_lay == vId
                || R.id.acti_resume_list_sift_other_lay == vId){//筛选--子布局--不操作
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

        //根据选的内容，获取省市id，注意：mCurProPosition = 0 时，表示选的是全国，职位一样要注意
        String areaId = "" ;
        String areaType = "1" ;
        if(!"".equals(mDefaultCityType) && !"0".equals(mDefaultCityType)){
            areaId = mDefaultCityId ;
            areaType = mDefaultCityType ;
        }else{
            if(mSelectedProPosition > 0){
                areaId = mAllProvinceList.get(mSelectedProPosition).getAreaId() ;
                if(mSelectedCityPosition > 0){
                    areaType = "2" ;
                    areaId = mCurCityList.get(mSelectedCityPosition).getAreaId() ;
                }
            }
        }

        String postId = "" ;
        String postLevel = "1" ;
        if(mSelectedParPostPosition > 0){
            postId = mAllPostList.get(mSelectedParPostPosition).getWorkPostID() ;

            if(mSelectedChiPostPosition > 0){
                postLevel = "2" ;
                postId = mCurChildPostList.get(mSelectedChiPostPosition).getWorkPostID() ;
            }
        }

        String edu = "" ;
        if(mEduCheckList.size() > 0){
            StringBuilder eduBuilder = new StringBuilder() ;
            for(String position : mEduCheckList){
                int posi = Integer.valueOf(position) ;
                eduBuilder.append(mEduList.get(posi).getItemName()).append(",") ;
            }
            edu = eduBuilder.substring(0,eduBuilder.length() - 1) ;
        }

        String exp = "" ;
        if(mExpCheckList.size() > 0){
            StringBuilder expBuilder = new StringBuilder() ;
            for(String position : mExpCheckList){
                int posi = Integer.valueOf(position) ;
                expBuilder.append(mExpList.get(posi).getItemName()).append(",") ;
            }
            exp = expBuilder.substring(0,expBuilder.length() - 1) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Keyword" , null == mSearchKey ? "" : mSearchKey) ;
        paramMap.put("WorkType" , "" + mListType) ;//1 全职 2 兼职
        paramMap.put("Type" , "2") ;//1 优质人才 2 全部
        paramMap.put("AreaId", areaId);//对应id，如果 省 并且id是“”，表示全国
        paramMap.put("AreaType", areaType);//1 省 2 市
        paramMap.put("PostLevel", postLevel);//1.一级职位 2.二级职位
        paramMap.put("PostId", postId);//对应id，如果 一级 并且id是“”，表示全部职位
        paramMap.put("Education", edu);
        paramMap.put("WorkYears", exp);
        paramMap.put("Sex", "" + mSelectedSexType) ;

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


    /**
     * TODO  获取省份
     */
    private void getProvince(boolean isReset){
        if(mAllProvinceList.size() > 0){
            //需要还原数据，如果需要的话
            if(mCurProPosition != mSelectedProPosition){
                mAllProvinceList.get(mCurProPosition).setSelected(false);
                mAllProvinceList.get(mSelectedProPosition).setSelected(true);
                mProvinceAdapter.notifyDataSetChanged();
                mCurProPosition = mSelectedProPosition ;
                mCurCityPosition = mSelectedCityPosition ;
            }

            if(0 == mCurProPosition){//第一个是不限，所以需要排除
                mCurCityList.clear();
                mCityAdapter.notifyDataSetChanged() ;
            }else{
                String provinceId = mAllProvinceList.get(mCurProPosition).getAreaId() ;
                List<SiftCity> cityList = mAllCityMap.get(provinceId) ;
                if(cityList != null && cityList.size() > 0){
                    mCurCityList.clear();
                    mCurCityList.addAll(cityList) ;
                    if(mCurCityPosition > -1 && mCurCityList.size() > mCurCityPosition){
                        mCurCityList.get(mCurCityPosition).setSelected(true);
                    }
                    mCityAdapter.notifyDataSetChanged() ;
                }else{
                    if(!isReset){
                        getCity() ;
                    }
                }
            }
            return ;
        }

        mLoadingDialog.show() ;

        Map<String, String> para = ParaUtils.getPara(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_PROVINCE, false, para,
                new RequestListCallBack<SiftCity>("getProvince", mContext,
                        SiftCity.class) {
                    @Override
                    public void onSuccessResult(List<SiftCity> bean) {
                        mAllProvinceList.clear();
                        mAllProvinceList.add(new SiftCity("" ,"不限")) ;
                        mAllProvinceList.addAll(bean) ;
                        mAllProvinceList.get(0).setSelected(true);
                        mProvinceAdapter.notifyDataSetChanged() ;
                        mCurProPosition = 0 ;

                        if(mIsFirstLoad){
                            getParentPost(false) ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mIsFirstLoad){
                            getParentPost(false) ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mIsFirstLoad){
                            getParentPost(false) ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * TODO  获取当前省下的市
     */
    private void getCity(){
        if(mCurProPosition < 0){
            return ;
        }

        if(mCurProPosition == 0){//第一个是不限
            mSelectedProPosition = 0 ;
            mCurProPosition = 0 ;

            mNavLocationTv.setText("位置不限") ;
            changeSiftViewState(SIFT_STATE_LOCATION) ;

            searchFromList() ;
            return ;
        }

        String provinceId = mAllProvinceList.get(mCurProPosition).getAreaId() ;

        List<SiftCity> cityList = mAllCityMap.get(provinceId) ;
        if(cityList != null && cityList.size() > 0){
            mCurCityList.clear();
            mCurCityList.addAll(cityList) ;
            if(mSelectedProPosition == mCurProPosition && mSelectedCityPosition > -1){//选中的是当前的
                mCurCityPosition = mSelectedCityPosition ;
                mCurCityList.get(mCurCityPosition).setSelected(true) ;
            }else{
                mCurCityPosition = -1 ;
            }

            mCityAdapter.notifyDataSetChanged();
            return ;
        }

        mLoadingDialog.show() ;

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", provinceId);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_CITY, false, para,
                new RequestListCallBack<SiftCity>("getCity", mContext,
                        SiftCity.class) {
                    @Override
                    public void onSuccessResult(List<SiftCity> bean) {
                        finishCityRequest(bean) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        finishCityRequest(null) ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        finishCityRequest(null) ;
                    }
                });
    }

    private void finishCityRequest(List<SiftCity> bean){
        mLoadingDialog.dismiss();

        //还原之前的
        if(mCurCityPosition >= 0 && mCurCityList.size() > mCurCityPosition){
            mCurCityList.get(mCurCityPosition).setSelected(false) ;
            mCityAdapter.notifyDataSetChanged() ;
        }

        String provinceId = mAllProvinceList.get(mCurProPosition).getAreaId() ;
        if(bean != null){
            //先添加一个全部
            bean.add(0,new SiftCity("" ,"不限")) ;
        }

        mAllCityMap.put(provinceId ,bean) ;

        //更新现在的
        mCurCityList.clear();
        if(bean != null){
            //先添加一个全部
            mCurCityList.addAll(bean) ;
        }

        mCurCityPosition = -1 ;
        mCityAdapter.notifyDataSetChanged() ;

        if(mIsFirstLoad){
            getParentPost(false) ;
        }
    }

    /**
     * TODO  获取一级职位列表
     */
    private void getParentPost(boolean isReset){
        if(mAllPostList.size() > 0){

            //需要还原数据，如果需要的话
            if(mCurParentPostPosition != mSelectedParPostPosition){
                mAllPostList.get(mCurParentPostPosition).setSelected(false);
                mAllPostList.get(mSelectedParPostPosition).setSelected(true);
                mParentPostAdapter.notifyDataSetChanged();
                mCurParentPostPosition = mSelectedParPostPosition ;
                mCurChildPostPosition = mSelectedChiPostPosition ;
            }
            if(0 == mCurParentPostPosition){//第一个是不限，所以需要排除
                mCurChildPostList.clear();
                mChildPostAdapter.notifyDataSetChanged() ;
            }else{
                String parentPostId = mAllPostList.get(mCurParentPostPosition).getWorkPostID() ;
                List<SiftWorkPost> childList = mAllWorkPostMap.get(parentPostId) ;
                if(childList != null && childList.size() > 0){
                    mCurChildPostList.clear();
                    mCurChildPostList.addAll(childList) ;
                    if(mCurChildPostPosition > -1 && mCurChildPostList.size() > mCurChildPostPosition){
                        mCurChildPostList.get(mCurChildPostPosition).setSelected(true);
                    }
                    mChildPostAdapter.notifyDataSetChanged() ;
                }else{
                    if(!isReset){
                        getChildPost() ;
                    }
                }
            }
            return ;
        }

        if(!mIsFirstLoad){
            mLoadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "1") ;
        paramMap.put("PageSize" , "999") ;
        paramMap.put("ID" , "1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_POST_LIST, paramMap
                , new RequestListCallBack<SiftWorkPost>("getParentPost" ,mContext ,SiftWorkPost.class) {
                    @Override
                    public void onSuccessResult(List<SiftWorkPost> bean) {
                        mAllPostList.clear();
                        mAllPostList.add(new SiftWorkPost("不限" ,"")) ;
                        mAllPostList.addAll(bean) ;
                        mAllPostList.get(0).setSelected(true);
                        mParentPostAdapter.notifyDataSetChanged() ;
                        mCurParentPostPosition = 0 ;

                        if(mIsFirstLoad){
                            getEduList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsFirstLoad){
                            getEduList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsFirstLoad){
                            getEduList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * TODO  获取二级职位列表
     */
    private void getChildPost(){
        if(mCurParentPostPosition < 0){
            return ;
        }

        if(mCurParentPostPosition == 0){//第一个是不限
            mSelectedParPostPosition = 0 ;

            mNavPostTv.setText("职位不限") ;
            changeSiftViewState(SIFT_STATE_POST) ;

            searchFromList() ;
            return ;
        }

        String workPostId = mAllPostList.get(mCurParentPostPosition).getWorkPostID() ;

        List<SiftWorkPost> postList = mAllWorkPostMap.get(workPostId) ;
        if(postList != null && postList.size() > 0){
            mCurChildPostList.clear();
            mCurChildPostList.addAll(postList) ;
            if(mSelectedParPostPosition == mCurParentPostPosition && mSelectedChiPostPosition > -1){//选中的是当前的
                mCurChildPostPosition = mSelectedChiPostPosition ;
                mCurChildPostList.get(mCurChildPostPosition).setSelected(true) ;
            }else{
                mCurChildPostPosition = -1 ;
            }

            mChildPostAdapter.notifyDataSetChanged();
            return ;
        }

        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "1") ;
        paramMap.put("PageSize" , "999") ;
        paramMap.put("ID" , "2") ;
        paramMap.put("Keyword" , StringUtils.convertNull(workPostId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_POST_LIST, paramMap
                , new RequestListCallBack<SiftWorkPost>("getChildPost" ,mContext ,SiftWorkPost.class) {
                    @Override
                    public void onSuccessResult(List<SiftWorkPost> bean) {
                        finishChildPostRequest(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishChildPostRequest(null) ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishChildPostRequest(null) ;
                    }
                });
    }


    private void finishChildPostRequest(List<SiftWorkPost> bean){
        mLoadingDialog.dismiss();

        //还原之前的
        if(mCurChildPostPosition >= 0 && mCurChildPostList.size() > mCurChildPostPosition){
            mCurChildPostList.get(mCurChildPostPosition).setSelected(false) ;
            mChildPostAdapter.notifyDataSetChanged() ;
        }

        String workPostId = mAllPostList.get(mCurParentPostPosition).getWorkPostID() ;

        if(bean != null){
            //先添加一个全部
            bean.add(0,new SiftWorkPost("不限" ,"")) ;
        }

        mAllWorkPostMap.put(workPostId ,bean) ;

        //更新现在的
        mCurChildPostList.clear();
        if(bean != null){
            mCurChildPostList.addAll(bean) ;
        }

        mCurChildPostPosition = -1 ;
        mChildPostAdapter.notifyDataSetChanged() ;
    }

    /**
     * 获取学历
     */
    private void getEduList(){
        if(!mIsFirstLoad){
            mLoadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , SkipUtils.WORD_TYPE_EDUCATION) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<WordType>("getEduList" , mContext , WordType.class) {
                    @Override
                    public void onSuccessResult(List<WordType> bean) {
                        mEduList.clear();
                        mEduList.addAll(bean) ;
                        mEduAdapter.notifyDataSetChanged() ;

                        if(mIsFirstLoad || mExpList.size() == 0){
                            getExpList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsFirstLoad || mExpList.size() == 0){
                            getExpList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsFirstLoad || mExpList.size() == 0){
                            getExpList() ;
                        }else{
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 获取工作经验
     */
    private void getExpList(){
        if(!mIsFirstLoad){
            mLoadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , SkipUtils.WORD_TYPE_WORK_YEAR) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<WordType>("getExpList" , mContext , WordType.class) {
                    @Override
                    public void onSuccessResult(List<WordType> bean) {
                        mLoadingDialog.dismiss();

                        mExpList.clear();
                        mExpList.addAll(bean) ;
                        mExpAdapter.notifyDataSetChanged() ;

                        if(mIsFirstLoad){
                            mIsFirstLoad = false ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss();

                        if(mIsFirstLoad){
                            mIsFirstLoad = false ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss();
                        if(mIsFirstLoad){
                            mIsFirstLoad = false ;
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if(mSiftState != SIFT_STATE_NONE){
            changeSiftViewState(mSiftState) ;
            return ;
        }
        super.onBackPressed();
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
