package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.SpecGoodCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.GoodIndicatorAdapter;
import cn.idcby.jiajubang.adapter.PageAdapterSpecGood;
import cn.idcby.jiajubang.fragment.SpecGoodFragment;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 厂家直供专场商品
 * Created on 2018-04-23.
 *
 * 2018-08-27 10:54:33
 * 改为选项卡滑动
 */

public class DirectGoodListsActivity extends BaseActivity {
    private String mSpecialId;
    private String mSearchKey ;

    private TextView mSearchKeyTv ;
    private MagicIndicator mNavLay ;
    private ViewPager mViewPager ;

    private List<SpecGoodFragment> mFragmentList = new ArrayList<>() ;

    private List<SpecGoodCategory> mCategoryList = new ArrayList<>() ;
    private LoadingDialog mDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_spec_good_lists;
    }

    @Override
    public void initView() {
        mSpecialId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        View searchLay = findViewById(R.id.acti_spec_good_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_spec_good_list_search_key_tv) ;
        searchLay.setOnClickListener(this);

        mNavLay = findViewById(R.id.acti_spec_good_nav_lay) ;
        mViewPager = findViewById(R.id.acti_spec_good_vp) ;
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

        if(R.id.acti_spec_good_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }
    }

    /**
     * 填充数据
     */
    private void updateCategoryDisplay(){
        mDialog.dismiss();

        if(mCategoryList.size() > 0){
            List<String> titles = new ArrayList<>(mCategoryList.size()) ;
            for(SpecGoodCategory category : mCategoryList){
                titles.add(category.getCategoryTitle()) ;

                SpecGoodFragment fragment = SpecGoodFragment.getInstance(mSpecialId
                        ,category.getRootCategoryID() ,mSearchKey) ;
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

            PageAdapterSpecGood pageAdapter = new PageAdapterSpecGood(getSupportFragmentManager() ,titles ,mFragmentList) ;
            mViewPager.setAdapter(pageAdapter) ;

            ViewPagerHelper.bind(mNavLay, mViewPager);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    SpecGoodFragment fragment = mFragmentList.get(position) ;
                    fragment.refreshList(mSearchKey) ;
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            SpecGoodFragment fragment = mFragmentList.get(0) ;
            fragment.refreshList(mSearchKey) ;
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
        paramMap.put("SpecialGoodsID", StringUtils.convertNull(mSpecialId));

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_SPEC_GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<SpecGoodCategory>("getSecondCategory" ,mContext , SpecGoodCategory.class) {
                    @Override
                    public void onSuccessResult(List<SpecGoodCategory> bean) {
                        SpecGoodCategory category1 = new SpecGoodCategory() ;
                        category1.setRootCategoryID("");
                        category1.setCategoryTitle("精选");
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


    private void updateCurFragment(){
        SpecGoodFragment goodFragment = mFragmentList.get(mViewPager.getCurrentItem());
        goodFragment.refreshList(mSearchKey) ;
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

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;
                updateCurFragment() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getSecondCategory");

    }
}
