package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodParam;
import cn.idcby.jiajubang.Bean.GoodParamParent;
import cn.idcby.jiajubang.Bean.StoreGoodCategory;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodParamParent;
import cn.idcby.jiajubang.adapter.GoodIndicatorAdapter;
import cn.idcby.jiajubang.adapter.PageAdapterCategoryGood;
import cn.idcby.jiajubang.fragment.CategoryGoodFragment;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 商品列表
 * Created on 2018-08-17.
 */

public class CategoryGoodListActivity extends BaseActivity {
    private String mStoreId ;
    private String mCategoryId ;
    private String mCategoryChildId ;
    private String mSearchKey ;
    private boolean mIsFromStore = false ;
    private int mSortType = 1 ; //1.全部 2.价格 3.销量 4.好评
    private boolean mIsArrowUp = true ;//升序

    private List<UnusedCategory> mCategoryList = new ArrayList<>() ;
    private LoadingDialog mDialog ;

    private List<CategoryGoodFragment> mFragmentList = new ArrayList<>() ;

    private TextView mSearchKeyTv ;
    private MagicIndicator mNavLay ;
    private ViewPager mViewPager ;

    //排序
    private TextView mTypeAllTv ;
    private TextView mTypeCountTv;
    private ImageView mTypeCountIv;
    private TextView mTypePriceTv;
    private ImageView mTypePriceIv;
    private TextView mTypeCommentTv;
    private ImageView mTypeCommentIv;

    //筛选功能
    private View mParamParentLay ;
    private View mParamChildLay ;
    private RecyclerView mParamRv ;
    private String mGoodParams = "" ;

    private List<GoodParamParent> mParamList = new ArrayList<>() ;
    private AdapterGoodParamParent mGoodParamAdapter ;

    private boolean mIsLoadingAnim = false ;//是否正在显示动画

    private Map<Integer,List<String>> mCheckParamMap = new HashMap<>() ;//当前选中的
    private Map<Integer,List<String>> mCheckedParamMap = new HashMap<>() ;//最终选中的



    @Override
    public int getLayoutID() {
        return R.layout.activity_category_good_list_new;
    }

    @Override
    public void initView() {
        mStoreId = getIntent().getStringExtra(SkipUtils.INTENT_STORE_ID) ;
        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mCategoryChildId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_CHILD_ID) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;
        mIsFromStore = !"".equals(StringUtils.convertNull(mStoreId)) ;

        View searchLay = findViewById(R.id.acti_category_good_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_category_good_list_search_key_tv) ;
        searchLay.setOnClickListener(this);

        if(!"".equals(StringUtils.convertNull(mSearchKey))){
            mSearchKeyTv.setText(mSearchKey) ;
        }

        mNavLay = findViewById(R.id.acti_category_good_list_tab_lay) ;
        mViewPager = findViewById(R.id.acti_category_good_list_vp) ;

        mTypeAllTv = findViewById(R.id.acti_category_good_list_all_tv) ;
        View mTypeCountLay = findViewById(R.id.acti_category_good_list_count_lay) ;
        mTypeCountTv = findViewById(R.id.acti_category_good_list_count_tv) ;
        mTypeCountIv = findViewById(R.id.acti_category_good_list_count_iv) ;
        View mTypeCommentLay = findViewById(R.id.acti_category_good_list_perfect_lay) ;
        mTypeCommentTv = findViewById(R.id.acti_category_good_list_perfect_tv) ;
        mTypeCommentIv = findViewById(R.id.acti_category_good_list_perfect_iv) ;
        View mTypePriceLay = findViewById(R.id.acti_category_good_list_price_lay) ;
        mTypePriceTv = findViewById(R.id.acti_category_good_list_price_tv) ;
        mTypePriceIv = findViewById(R.id.acti_category_good_list_price_iv) ;
        View mTypeParamLay = findViewById(R.id.acti_category_good_list_shaixuan_lay) ;

        mTypeParamLay.setOnClickListener(this);
        mTypeCountLay.setOnClickListener(this);
        mTypeCommentLay.setOnClickListener(this);
        mTypePriceLay.setOnClickListener(this);
        mTypeAllTv.setOnClickListener(this);

        initParam() ;
    }

    /**
     * 筛选功能
     */
    private void initParam(){
        //筛选功能
        mParamParentLay = findViewById(R.id.acti_category_list_param_parent_lay) ;
        mParamChildLay = findViewById(R.id.acti_category_list_param_child_lay) ;
        mParamRv = findViewById(R.id.acti_category_param_rv) ;
        View mResetTv = findViewById(R.id.acti_category_param_reset_tv) ;
        View mOkTv = findViewById(R.id.acti_category_param_ok_tv) ;
        mResetTv.setOnClickListener(this);
        mOkTv.setOnClickListener(this);
        mParamParentLay.setOnClickListener(this);
        mParamChildLay.setOnClickListener(this);

        mParamRv.setLayoutManager(new LinearLayoutManager(mContext));
        mGoodParamAdapter = new AdapterGoodParamParent(mContext, mParamList, new RvMoreItemClickListener() {
            @Override
            public void onItemClickListener(int type, int... position) {
                if(0 == type && position.length == 2){
                    int parentPosition = position[0] ;
                    int childPosition = position[1] ;

                    GoodParam goodParam = mParamList.get(parentPosition).getChildList().get(childPosition) ;
                    boolean isSelected = goodParam.isSelected() ;
                    if(isSelected){
                        if(mCheckParamMap.containsKey(parentPosition)){
                            List<String> childPoList = mCheckParamMap.get(parentPosition) ;

                            if(childPoList.contains(String.valueOf(childPosition))){
                                childPoList.remove(String.valueOf(childPosition)) ;
                            }
                            if(childPoList.size() == 0){
                                mCheckParamMap.remove(parentPosition) ;
                            }
                        }
                    }else{
                        if(mCheckParamMap.containsKey(parentPosition)){
                            List<String> childPoList = mCheckParamMap.get(parentPosition) ;
                            childPoList.add(String.valueOf(childPosition)) ;
                        }else{
                            List<String> childPoList = new ArrayList<>() ;
                            childPoList.add(String.valueOf(childPosition)) ;
                            mCheckParamMap.put(parentPosition ,childPoList) ;
                        }
                    }
                    goodParam.setSelected(!isSelected);
                    mGoodParamAdapter.notifyDataSetChanged() ;
                }
            }
        }) ;
        mParamRv.setAdapter(mGoodParamAdapter);

        getParamInfo() ;
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

        if(mIsLoadingAnim){
            return;
        }

        if(R.id.acti_category_good_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_category_good_list_all_tv == vId){//综合
            changeTypeTvStyle(1) ;
        }else if(R.id.acti_category_good_list_count_lay == vId){//销量
            changeTypeTvStyle(3) ;
        }else if(R.id.acti_category_good_list_price_lay == vId){//价格
            changeTypeTvStyle(2) ;
        }else if(R.id.acti_category_good_list_perfect_lay == vId){//好评
            changeTypeTvStyle(4) ;
        }else if(R.id.acti_category_good_list_shaixuan_lay == vId){
            toGuigeChangeIt(true) ;
        }else if(R.id.acti_category_list_param_parent_lay == vId){//筛选--隐藏
            toGuigeChangeIt(false);
        }else if(R.id.acti_category_list_param_child_lay == vId){//筛选--无操作即可
        }else if(R.id.acti_category_param_reset_tv == vId){//筛选--重置
            resetGoodParamState() ;
        }else if(R.id.acti_category_param_ok_tv == vId){//筛选--完成
            finishGoodParamCheck() ;
        }
    }


    /**
     * 选择规格
     */
    private void toGuigeChangeIt(boolean isIn){
        if(isIn){//显示规格布局
            mParamParentLay.setVisibility(View.VISIBLE) ;
            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.in_rightleft);
            mParamChildLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mParamChildLay.setVisibility(View.VISIBLE) ;
                    mIsLoadingAnim = false ;
                }
            });
        }else{//隐藏规格布局
            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.out_leftright);
            mParamChildLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mParamParentLay.setVisibility(View.GONE) ;
                    mIsLoadingAnim = false ;

                    if(mCheckParamMap.size() > 0){
                        Set<Integer> keySet = mCheckParamMap.keySet() ;

                        for(int parentPosition : keySet){
                            List<String> childPoList = mCheckParamMap.get(parentPosition) ;
                            for(String childPosi : childPoList){
                                int childPosition = StringUtils.convertString2Count(childPosi) ;
                                mParamList.get(parentPosition).getChildList().get(childPosition).setSelected(false);
                            }
                        }
                    }

                    mCheckParamMap.clear() ;

                    if(mCheckedParamMap.size() > 0){
                        Set<Integer> keySet = mCheckedParamMap.keySet() ;

                        for(int parentPosition : keySet){
                            List<String> childPoList = mCheckedParamMap.get(parentPosition) ;
                            List<String> childPoListNew = new ArrayList<>(childPoList.size()) ;
                            for(String childPosi : childPoList){
                                int childPosition = StringUtils.convertString2Count(childPosi) ;
                                mParamList.get(parentPosition).getChildList().get(childPosition).setSelected(true);

                                childPoListNew.add(childPosi) ;
                            }
                            mCheckParamMap.put(parentPosition,childPoListNew) ;
                        }
                    }

                    mGoodParamAdapter.notifyDataSetChanged() ;
                }
            });
        }
    }

    /**
     * 重置筛选
     */
    private void resetGoodParamState(){
        if(mCheckParamMap.size() > 0){
            Set<Integer> keySet = mCheckParamMap.keySet() ;

            for(int parentPosition : keySet){
                List<String> childPoList = mCheckParamMap.get(parentPosition) ;
                for(String childPosi : childPoList){
                    int childPosition = StringUtils.convertString2Count(childPosi) ;
                    mParamList.get(parentPosition).getChildList().get(childPosition).setSelected(false);
                }
            }

            mGoodParamAdapter.notifyDataSetChanged() ;

            mCheckParamMap.clear() ;
            mCheckedParamMap.clear();
        }

        mGoodParams = "" ;
        changeNeedTypeAndGetList() ;
    }

    /**
     * 完成筛选
     */
    private void finishGoodParamCheck(){
        mGoodParams = "" ;

        if(mCheckParamMap.size() > 0){
            mCheckedParamMap.clear();

            Set<Integer> keySet = mCheckParamMap.keySet() ;
            for(int parentPosition : keySet){
                List<String> childPoList = mCheckParamMap.get(parentPosition) ;
                List<String> childPoListNew = new ArrayList<>(childPoList.size()) ;
                for(String childPosi : childPoList){
                    childPoListNew.add(childPosi) ;

                    int childPosition = StringUtils.convertString2Count(childPosi) ;
                    GoodParam goodParam = mParamList.get(parentPosition).getChildList().get(childPosition);

                    mGoodParams += (goodParam.getParaId() + ",") ;
                }

                mCheckedParamMap.put(parentPosition ,childPoListNew) ;
            }

            if(mGoodParams.endsWith(",")){
                mGoodParams = mGoodParams.substring(0 ,mGoodParams.length() - 1) ;
            }
        }else{//清空了
            if(mCheckedParamMap.size() > 0){
                Set<Integer> keySet = mCheckedParamMap.keySet() ;

                for(int parentPosition : keySet){
                    List<String> childPoList = mCheckedParamMap.get(parentPosition) ;
                    for(String childPosi : childPoList){
                        int childPosition = StringUtils.convertString2Count(childPosi) ;
                        mParamList.get(parentPosition).getChildList().get(childPosition).setSelected(false);
                    }
                }

                mGoodParamAdapter.notifyDataSetChanged() ;

                mCheckParamMap.clear() ;
                mCheckedParamMap.clear();
            }
        }

        toGuigeChangeIt(false);
        changeNeedTypeAndGetList() ;
    }

    private void changeTypeTvStyle(int type){
        switch (mSortType){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                break;
            case 2:
                mTypePriceTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypePriceIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            case 3:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            case 4:
                mTypeCommentTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            default:
                break;
        }

        if(1 == type){
            mIsArrowUp = true ;
        }else{
            if(mSortType != type){
                mIsArrowUp = true ;
            }

            mIsArrowUp = !mIsArrowUp ;
        }

        switch (type){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;
                break;
            case 2:
                mTypePriceTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypePriceIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypePriceIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }
                break;
            case 3:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }

                break;
            case 4:
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
        CategoryGoodFragment fragment = mFragmentList.get(mViewPager.getCurrentItem()) ;
        fragment.refreshList(mSearchKey ,mSortType ,mIsArrowUp,mGoodParams) ;
    }

    /**
     * 填充分类adapter
     */
    private void updateCategoryDisplay(){
        mDialog.dismiss();

        int size = mCategoryList.size() ;
        if(size > 0){
            List<String> titles = new ArrayList<>(mCategoryList.size()) ;
            int position = 0 ;
            for(int x = 0 ; x < size ; x ++){
                UnusedCategory category = mCategoryList.get(x) ;

                titles.add(category.getCategoryTitle()) ;

                String parendId = category.getParentID() ;
                String cateId = category.getCategoryID() ;

                //2018-08-29 15:42:37 IOS未做该定位到选择的二级类的功能，暂时屏蔽，以后再说
//                if(cateId.equals(mCategoryChildId)){//当前选择的分类id
//                    position = x ;
//                }

                CategoryGoodFragment fragment = CategoryGoodFragment.getInstance(parendId
                        ,cateId ,mSearchKey ,mStoreId ,mSortType ,mIsArrowUp ,mIsFromStore) ;
                mFragmentList.add(fragment) ;
            }

            GoodIndicatorAdapter mAdapter = new GoodIndicatorAdapter(titles ,mViewPager) ;
            CommonNavigator commonNavigator = new CommonNavigator(mContext);
            commonNavigator.setLeftPadding(ResourceUtils.dip2px(mContext ,5));
            commonNavigator.setRightPadding(ResourceUtils.dip2px(mContext ,5));
            commonNavigator.setSkimOver(true);
            commonNavigator.setFitIfNecessary();
            commonNavigator.setAdapter(mAdapter);
            mNavLay.setNavigator(commonNavigator);

            PageAdapterCategoryGood pageAdapter = new PageAdapterCategoryGood(getSupportFragmentManager() ,titles ,mFragmentList) ;
            mViewPager.setAdapter(pageAdapter) ;
            ViewPagerHelper.bind(mNavLay, mViewPager);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    CategoryGoodFragment fragment = mFragmentList.get(position) ;
                    fragment.refreshList(mSearchKey ,mSortType ,mIsArrowUp,mGoodParams) ;
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            mViewPager.setOffscreenPageLimit(size) ;
            mViewPager.setCurrentItem(position) ;

            CategoryGoodFragment fragment = mFragmentList.get(position) ;
            fragment.refreshList(mSearchKey ,mSortType ,mIsArrowUp,mGoodParams) ;
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
        if(mIsFromStore){//店铺跳转来的
            paramMap.put("Page","1") ;
            paramMap.put("PageSize","30") ;//接口限制，最多获取30个大分类
            paramMap.put("Keyword", StringUtils.convertNull(mStoreId)) ;

            NetUtils.getDataFromServerByPost(mContext, Urls.STORE_GOOD_CATEGORY, paramMap
                    , new RequestListCallBack<StoreGoodCategory>("getSecondCategory" ,mContext , StoreGoodCategory.class) {
                        @Override
                        public void onSuccessResult(List<StoreGoodCategory> bean) {
                            UnusedCategory category1 = new UnusedCategory() ;
                            category1.setParentID("");
                            category1.setCategoryTitle("精选");
                            category1.setCategoryID("") ;
                            mCategoryList.add(category1) ;

                            for(StoreGoodCategory storeCate : bean){
                                String parendId = storeCate.getCategoryID() ;
                                List<StoreGoodCategory> secondCate = storeCate.getChildCategoryList() ;
                                for(StoreGoodCategory childCate : secondCate){
                                    UnusedCategory category = new UnusedCategory() ;
                                    category.setParentID(parendId);
                                    category.setCategoryTitle(childCate.getCategoryTitle());
                                    category.setCategoryID(childCate.getCategoryID()) ;
                                    mCategoryList.add(category) ;
                                }
                            }

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
        }else{
            paramMap.put("Keyword", StringUtils.convertNull(mCategoryId));
            paramMap.put("Id", "2");
            NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                    , new RequestListCallBack<UnusedCategory>("getSecondCategory" ,mContext , UnusedCategory.class) {
                        @Override
                        public void onSuccessResult(List<UnusedCategory> bean) {
                            UnusedCategory category1 = new UnusedCategory() ;
                            category1.setParentID(mCategoryId);
                            category1.setCategoryTitle("精选");
                            category1.setCategoryID("") ;
                            mCategoryList.add(category1) ;

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
    }

    /**
     * 获取筛选参数
     */
    private void getParamInfo(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code",StringUtils.convertNull(mCategoryId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_PARAM_INFO, paramMap
                , new RequestListCallBack<GoodParamParent>("getParamInfo" ,mContext ,GoodParamParent.class) {
                    @Override
                    public void onSuccessResult(List<GoodParamParent> bean) {
                        mParamList.addAll(bean) ;
                        mGoodParamAdapter.notifyDataSetChanged() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mCategoryList.size() == 0){
            getCategoryList() ;
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

                mSearchKeyTv.setText("".equals(mSearchKey.trim()) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;
                changeNeedTypeAndGetList() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getSecondCategory");

    }
}
