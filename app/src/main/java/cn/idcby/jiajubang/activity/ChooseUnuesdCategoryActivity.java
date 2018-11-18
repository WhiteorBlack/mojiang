package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseUnuesdCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018-04-19.
 */

public class ChooseUnuesdCategoryActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<UnusedCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseUnuesdCategory mAdapter ;

    private boolean mIsMoreCheck = false ;//是否多选
    private boolean mIsHasChild = true ;//是否有下级

    private int mCurPosition ;

    private ArrayList<UnusedCategory> mSelectedCategory ;


    public static void launch(Activity activity , boolean isChild , boolean isMore
            , ArrayList<UnusedCategory> selectedList , int requestCode){

        Intent toLiIt = new Intent(activity ,ChooseUnuesdCategoryActivity.class) ;
        toLiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_MORE ,isMore) ;
        toLiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_CHILD ,isChild) ;
        toLiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO ,selectedList) ;
        activity.startActivityForResult(toLiIt ,requestCode) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_good_category;
    }

    @Override
    public void initView() {
        mSelectedCategory = (ArrayList<UnusedCategory>) getIntent().getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
        mIsMoreCheck = getIntent().getBooleanExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_MORE , mIsMoreCheck) ;
        mIsHasChild = getIntent().getBooleanExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_CHILD ,mIsHasChild) ;

        TextView rightTv = findViewById(R.id.acti_choose_good_category_right_tv) ;
        rightTv.setOnClickListener(this);
        rightTv.setVisibility(mIsMoreCheck ? View.VISIBLE : View.GONE) ;

        mLoadingLay = findViewById(R.id.acti_good_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_good_categoty_lv) ;

        mAdapter = new AdapterChooseUnuesdCategory(mContext ,mIsHasChild, mIsMoreCheck,mDataList) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getCategory() ;
    }

    @Override
    public void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UnusedCategory category = mDataList.get(i) ;
                if(category != null){
                    if(mIsMoreCheck){
                        if(!mIsHasChild){
                            boolean isSelected =category.isSelected() ;
                            category.setSelected(!isSelected);
                            mAdapter.notifyDataSetChanged() ;
                        }else{
                            mCurPosition = i ;

                            ArrayList<String> checkIds = new ArrayList<>() ;
                            if(category.getSelectedCategory() != null){
                                List<UnusedCategory> checkList =category.getSelectedCategory() ;
                                for(UnusedCategory cate : checkList){
                                    checkIds.add(cate.getCategoryID()) ;
                                }
                            }

                            Intent toCiIt = new Intent(mContext ,ChooseUnusedCategoryChildActivity.class) ;
                            toCiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO ,checkIds) ;
                            toCiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.getCategoryID()) ;
                            toCiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_MORE , mIsMoreCheck) ;
                            startActivityForResult(toCiIt ,1000) ;
                        }
                    }else{//不支持多选，直接选中即可
                        if(!mIsHasChild){
                            //先清空之前选中的
                            for(UnusedCategory cCategory : mDataList){
                                cCategory.setSelected(false) ;
                            }
                            category.setSelected(true);
                            mAdapter.notifyDataSetChanged() ;

                            finishCheckIntent();
                        }else{
                            mCurPosition = i ;

                            ArrayList<String> checkIds = new ArrayList<>() ;
                            if(category.getSelectedCategory() != null){
                                List<UnusedCategory> checkList =category.getSelectedCategory() ;
                                for(UnusedCategory cate : checkList){
                                    checkIds.add(cate.getCategoryID()) ;
                                }
                            }

                            Intent toCiIt = new Intent(mContext ,ChooseUnusedCategoryChildActivity.class) ;
                            toCiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO ,checkIds) ;
                            toCiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.getCategoryID()) ;
                            toCiIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_MORE ,mIsMoreCheck) ;
                            startActivityForResult(toCiIt ,1000) ;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_choose_good_category_right_tv == view.getId()){
            finishCheckIntent() ;
        }
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取列表
     */
    private void getCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Keyword", "");
        paramMap.put("Id", "1");

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<UnusedCategory>("getCategory" , mContext , UnusedCategory.class) {
                    @Override
                    public void onSuccessResult(List<UnusedCategory> bean) {
                        hiddenLoading();

                        if(mSelectedCategory != null && mSelectedCategory.size() > 0){
                            if(!mIsHasChild){//只有一级
                                List<String> seleIds = new ArrayList<>(mSelectedCategory.size()) ;
                                for(UnusedCategory serverCategory : mSelectedCategory){
                                    seleIds.add(serverCategory.getCategoryID()) ;
                                }

                                for(UnusedCategory category : bean){
                                    if(seleIds.contains(category.getCategoryID())){
                                        category.setSelected(true);
                                    }

                                    mDataList.add(category) ;
                                }
                            }else{
                                for(UnusedCategory category : bean){
                                    String parentId = category.getCategoryID() ;

                                    for(UnusedCategory seleCate : mSelectedCategory){
                                        if(parentId.equals(seleCate.getCategoryID())){
                                            category.setSelectedCategory(seleCate.getSelectedCategory()) ;
                                        }
                                    }

                                    mDataList.add(category) ;
                                }
                            }
                        }else{
                            mDataList.addAll(bean) ;
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        hiddenLoading();
                    }
                    @Override
                    public void onFail(Exception e) {
                        hiddenLoading();
                    }
                });
    }


    private void finishCheckIntent(){
        ArrayList<UnusedCategory> categorys = new ArrayList<>() ;
        if(!mIsHasChild){
            for(UnusedCategory category : mDataList){
                if(category.isSelected()){
                    categorys.add(category) ;
                }
            }
        }else{
            for(UnusedCategory category : mDataList){
                List<UnusedCategory> childList = category.getSelectedCategory() ;
                if(childList != null && childList.size() > 0){
                    categorys.add(category) ;
                }
            }
        }

        Intent reIt = new Intent() ;
        reIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO ,categorys) ;
        setResult(RESULT_OK , reIt) ;
        finish() ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<UnusedCategory> childCate = (ArrayList<UnusedCategory>) data
                        .getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
                if(childCate != null){
                    if(mIsMoreCheck){
                        mDataList.get(mCurPosition).setSelectedCategory(childCate);
                        mAdapter.notifyDataSetChanged() ;
                    }else{
                        //单选，先清除选中，再选中
                        for(UnusedCategory category : mDataList){
                            category.setSelectedCategory(null) ;
                        }
                        mDataList.get(mCurPosition).setSelectedCategory(childCate);
                        mAdapter.notifyDataSetChanged() ;
                        finishCheckIntent() ;
                    }
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCategory");
        if(mSelectedCategory != null){
            mSelectedCategory.clear();
        }

    }
}
