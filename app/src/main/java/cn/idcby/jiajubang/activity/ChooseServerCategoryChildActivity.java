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
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseServerCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/26.
 */

public class ChooseServerCategoryChildActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<ServerCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseServerCategory mAdapter ;

    private boolean mIsMoreCheck = false ;
    private String mParentId ;
    private ArrayList<String> mSelectedCategoryIds;

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_server_category;
    }

    @Override
    public void initView() {
        mIsMoreCheck = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_MORE,mIsMoreCheck) ;
        mParentId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mSelectedCategoryIds = (ArrayList<String>) getIntent().getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);

        mLoadingLay = findViewById(R.id.acti_server_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_server_categoty_lv) ;
        TextView mRightTv = findViewById(R.id.acti_choose_server_category_right_tv) ;
        if(mIsMoreCheck){
            mRightTv.setVisibility(View.VISIBLE) ;
        }

        mAdapter = new AdapterChooseServerCategory(mContext ,false ,mIsMoreCheck,mDataList) ;
        mLv.setAdapter(mAdapter) ;

        mRightTv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getServerCategory() ;
    }

    @Override
    public void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServerCategory category = mDataList.get(i) ;
                if(mIsMoreCheck){
                    boolean isSelected =category.isSelected() ;
                    category.setSelected(!isSelected);

                    mAdapter.notifyDataSetChanged() ;
                }else{
                    for(ServerCategory cCategory : mDataList){
                        cCategory.setSelected(false) ;
                    }
                    category.setSelected(true) ;
                    finishCheckIntent() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        if(view.getId() == R.id.acti_choose_server_category_right_tv){//确定
            finishCheckIntent() ;
        }
    }

    private void finishCheckIntent(){
        ArrayList<ServerCategory> categorys = new ArrayList<>() ;

        for(ServerCategory category : mDataList){
            if(category.isSelected()){
                categorys.add(category) ;
            }
        }

        Intent reIt = new Intent() ;
        reIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO,categorys) ;
        setResult(RESULT_OK , reIt) ;
        finish() ;
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取列表
     */
    private void getServerCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("id" ,"2") ;
        paramMap.put("code" , StringUtils.convertNull(mParentId)) ;
        paramMap.put("Layer" ,"2") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, paramMap
                , new RequestListCallBack<ServerCategory>("getServerCategory" , mContext , ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        hiddenLoading();

                        if(mSelectedCategoryIds != null && mSelectedCategoryIds.size() > 0){
                            for(ServerCategory category : bean){
                                if(mSelectedCategoryIds.contains(category.getServiceCategoryID())){
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

        NetUtils.cancelTag("getServerCategory");

    }
}
