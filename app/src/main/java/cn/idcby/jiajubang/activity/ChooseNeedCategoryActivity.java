package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.NeedsCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseNeedCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/3/30.
 *
 * 选择需求分类
 *
 * 2018-09-11 13:58:23
 * 暂时不让点击 安装和服务
 */

public class ChooseNeedCategoryActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<NeedsCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseNeedCategory mAdapter ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_need_category;
    }

    @Override
    public void initView() {
        mLoadingLay = findViewById(R.id.acti_need_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_need_categoty_lv) ;

        mAdapter = new AdapterChooseNeedCategory(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getNeedsCategory() ;
    }

    @Override
    public void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NeedsCategory category = mDataList.get(i) ;
                if(category != null
                        && !category.getCategoryTitle().contains("服务")
                        && !category.getCategoryTitle().contains("安装")){
                    Intent reIt = new Intent() ;
                    reIt.putExtra(SkipUtils.INTENT_NEEDS_CATEGORY_INFO ,category) ;
                    setResult(RESULT_OK , reIt) ;
                    finish() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取列表
     */
    private void getNeedsCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_CATEGORY, paramMap
                , new RequestListCallBack<NeedsCategory>("getNeedsCategory" , mContext , NeedsCategory.class) {
                    @Override
                    public void onSuccessResult(List<NeedsCategory> bean) {
                        hiddenLoading();
                        mDataList.addAll(bean) ;
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

        NetUtils.cancelTag("getNeedsCategory");

    }
}
