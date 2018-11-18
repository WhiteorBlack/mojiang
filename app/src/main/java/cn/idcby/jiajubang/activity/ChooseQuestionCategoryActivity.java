package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.QuestionCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseQuestionCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/3/30.
 */

public class ChooseQuestionCategoryActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<QuestionCategory> mDataList = new ArrayList<>() ;
    private AdapterChooseQuestionCategory mAdapter ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_need_category;
    }

    @Override
    public void initView() {
        mLoadingLay = findViewById(R.id.acti_need_cate_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_need_categoty_lv) ;

        mAdapter = new AdapterChooseQuestionCategory(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getQuestionCategory() ;
    }

    @Override
    public void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QuestionCategory category = mDataList.get(i) ;
                if(category != null){
                    Intent reIt = new Intent() ;
                    reIt.putExtra(SkipUtils.INTENT_QUESTION_CATEGORY_INFO ,category) ;
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
     * 获取分类列表
     */
    private void getQuestionCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_CATEGORY, paramMap
                , new RequestListCallBack<QuestionCategory>("getQuestionCategory" , mContext , QuestionCategory.class) {
                    @Override
                    public void onSuccessResult(List<QuestionCategory> bean) {
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

        NetUtils.cancelTag("getQuestionCategory");

    }
}
