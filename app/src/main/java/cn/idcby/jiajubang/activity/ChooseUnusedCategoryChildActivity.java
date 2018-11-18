package cn.idcby.jiajubang.activity;

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
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/26.
 */

public class ChooseUnusedCategoryChildActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<UnusedCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseUnuesdCategory mAdapter ;

    private boolean mIsMoreCheck = false ;
    private String mParentId ;
    private ArrayList<String> mSelectedCategoryIds;

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_good_category;
    }

    @Override
    public void initView() {
        mIsMoreCheck = getIntent().getBooleanExtra(SkipUtils.INTENT_UNUSE_CATEGORY_IS_MORE,mIsMoreCheck) ;
        mParentId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mSelectedCategoryIds = (ArrayList<String>) getIntent().getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);

        mLoadingLay = findViewById(R.id.acti_good_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_good_categoty_lv) ;
        TextView mRightTv = findViewById(R.id.acti_choose_good_category_right_tv) ;
        if(mIsMoreCheck){
            mRightTv.setVisibility(View.VISIBLE) ;
        }

        mAdapter = new AdapterChooseUnuesdCategory(mContext ,false ,mIsMoreCheck,mDataList) ;
        mLv.setAdapter(mAdapter) ;

        mRightTv.setOnClickListener(this);
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
                if(mIsMoreCheck){
                    boolean isSelected =category.isSelected() ;
                    category.setSelected(!isSelected);

                    mAdapter.notifyDataSetChanged() ;
                }else{
                    //单选的话，先清空别的选择
                    for(UnusedCategory cCategory : mDataList){
                        cCategory.setSelected(false) ;
                    }
                    category.setSelected(true) ;
                    mAdapter.notifyDataSetChanged() ;

                    finishCheckIntent() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        if(view.getId() == R.id.acti_choose_good_category_right_tv){//确定
            finishCheckIntent() ;
        }
    }

    private void finishCheckIntent(){
        ArrayList<UnusedCategory> categorys = new ArrayList<>() ;

        for(UnusedCategory category : mDataList){
            if(category.isSelected()){
                categorys.add(category) ;
            }
        }

        Intent reIt = new Intent() ;
        reIt.putExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO ,categorys) ;
        setResult(RESULT_OK , reIt) ;
        finish() ;
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取列表
     */
    private void getCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Id", "2");
        paramMap.put("Keyword", StringUtils.convertNull(mParentId));

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<UnusedCategory>("getCategory" , mContext , UnusedCategory.class) {
                    @Override
                    public void onSuccessResult(List<UnusedCategory> bean) {
                        hiddenLoading();

                        if(mSelectedCategoryIds != null && mSelectedCategoryIds.size() > 0){
                            for(UnusedCategory category : bean){
                                if(mSelectedCategoryIds.contains(category.getCategoryID())){
                                    category.setSelected(true);
                                }

                                mDataList.add(category) ;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCategory");

    }
}
