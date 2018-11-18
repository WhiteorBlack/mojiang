package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.CircleCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseCircleCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/11.
 */

public class ChooseCircleCategoryActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<CircleCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseCircleCategory mAdapter ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_circle_category;
    }

    @Override
    public void initView() {
        mLoadingLay = findViewById(R.id.acti_circle_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_circle_categoty_lv) ;

        mAdapter = new AdapterChooseCircleCategory(mContext ,mDataList) ;
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
                CircleCategory category = mDataList.get(i) ;
                Intent reIt = new Intent() ;
                reIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO,category) ;
                setResult(RESULT_OK , reIt) ;
                finish() ;
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
    private void getCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("ID" ,"2") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_CATEGORY_LIST, paramMap
                , new RequestListCallBack<CircleCategory>("getCategory" , mContext , CircleCategory.class) {
                    @Override
                    public void onSuccessResult(List<CircleCategory> bean) {
                        hiddenLoading();

                        mDataList.clear();
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

        NetUtils.cancelTag("getCategory");

    }
}
