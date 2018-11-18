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
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/11.
 * 2018-05-26 20:47:07
 * 只能选一个分类下的数据（单选或者多选）
 */

public class ChooseServerCategoryActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<ServerCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseServerCategory mAdapter ;
    private boolean mIsInstall = false ; //行业服务有2级分类，安装服务只有1级
    private boolean mIsMoreCheck = false ; //是否可以多选，跟分类级数无关

    private int mCurPosition ;

    private ArrayList<ServerCategory> mSelectedCategory ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_server_category;
    }

    @Override
    public void initView() {
        mSelectedCategory = (ArrayList<ServerCategory>) getIntent().getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);
        mIsInstall = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL,mIsInstall) ;
        mIsMoreCheck = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_MORE,mIsMoreCheck) ;

        mLoadingLay = findViewById(R.id.acti_server_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_server_categoty_lv) ;
        TextView mRightTv = findViewById(R.id.acti_choose_server_category_right_tv) ;
        if(mIsMoreCheck){
            mRightTv.setVisibility(View.VISIBLE);
        }

        mAdapter = new AdapterChooseServerCategory(mContext ,!mIsInstall,mIsMoreCheck ,mDataList) ;
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
                    if(mIsInstall){
                        boolean isSelected =category.isSelected() ;
                        category.setSelected(!isSelected);
                        mAdapter.notifyDataSetChanged() ;
                    }else{
                        mCurPosition = i ;

                        ArrayList<String> checkIds = new ArrayList<>() ;
                        if(category.getSelectedCategory() != null){
                            List<ServerCategory> checkList =category.getSelectedCategory() ;
                            for(ServerCategory cate : checkList){
                                checkIds.add(cate.getServiceCategoryID()) ;
                            }
                        }

                        Intent toCiIt = new Intent(mContext ,ChooseServerCategoryChildActivity.class) ;
                        toCiIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO,checkIds) ;
                        toCiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.getServiceCategoryID()) ;
                        toCiIt.putExtra(SkipUtils.INTENT_SERVER_IS_MORE ,mIsMoreCheck) ;
                        startActivityForResult(toCiIt ,1000) ;
                    }
                }else{//不支持多选，直接选中即可
                    if(mIsInstall){
                        for(ServerCategory cCategory : mDataList){
                            cCategory.setSelected(false);
                        }
                        category.setSelected(true);
                        mAdapter.notifyDataSetChanged() ;

                        finishCheckIntent();
                    }else{
                        mCurPosition = i ;

                        ArrayList<String> checkIds = new ArrayList<>() ;
                        if(category.getSelectedCategory() != null){
                            List<ServerCategory> checkList =category.getSelectedCategory() ;
                            for(ServerCategory cate : checkList){
                                checkIds.add(cate.getServiceCategoryID()) ;
                            }
                        }

                        Intent toCiIt = new Intent(mContext ,ChooseServerCategoryChildActivity.class) ;
                        toCiIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO,checkIds) ;
                        toCiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.getServiceCategoryID()) ;
                        toCiIt.putExtra(SkipUtils.INTENT_SERVER_IS_MORE ,mIsMoreCheck) ;
                        startActivityForResult(toCiIt ,1000) ;
                    }
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
        if(mIsInstall){
            for(ServerCategory category : mDataList){
                if(category.isSelected()){
                    categorys.add(category) ;
                }
            }
        }else{
            for(ServerCategory category : mDataList){
                List<ServerCategory> childList = category.getSelectedCategory() ;
                if(childList != null && childList.size() > 0){
                    categorys.add(category) ;
                }
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
        paramMap.put("id" ,mIsInstall ? "1" : "2") ;
        paramMap.put("Layer" ,"1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, paramMap
                , new RequestListCallBack<ServerCategory>("getServerCategory" , mContext , ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        hiddenLoading();

                        if(mSelectedCategory != null && mSelectedCategory.size() > 0){
                            if(mIsInstall){//安装-只有1级
                                List<String> seleIds = new ArrayList<>(mSelectedCategory.size()) ;
                                for(ServerCategory serverCategory : mSelectedCategory){
                                    seleIds.add(serverCategory.getServiceCategoryID()) ;
                                }

                                for(ServerCategory category : bean){
                                    if(seleIds.contains(category.getServiceCategoryID())){
                                        category.setSelected(true);
                                    }

                                    mDataList.add(category) ;
                                }
                            }else{
                                for(ServerCategory category : bean){
                                    String parentId = category.getServiceCategoryID() ;

                                    for(ServerCategory seleCate : mSelectedCategory){
                                        if(parentId.equals(seleCate.getServiceCategoryID())){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<ServerCategory> childCate = (ArrayList<ServerCategory>) data
                        .getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);
                if(childCate != null){
                    if(mIsMoreCheck){
//                        //2018-05-26 20:48:51 改选择分类时只选一个分类下的多级分类
//                        mDataList.get(mCurPosition).setSelectedCategory(childCate);
//                        mAdapter.notifyDataSetChanged() ;

                        for(ServerCategory serverCategory : mDataList){
                            serverCategory.setSelectedCategory(null) ;
                        }
                        mDataList.get(mCurPosition).setSelectedCategory(childCate);
                        mAdapter.notifyDataSetChanged() ;
                    }else{
                        for(ServerCategory serverCategory : mDataList){
                            serverCategory.setSelectedCategory(null) ;
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

        NetUtils.cancelTag("getServerCategory");

    }
}
