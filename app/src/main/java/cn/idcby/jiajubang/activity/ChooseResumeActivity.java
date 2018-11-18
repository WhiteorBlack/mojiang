package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseResume;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 选择简历
 * Created on 2018/3/28.
 */

public class ChooseResumeActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private List<ResumeList> mDataList = new ArrayList<>() ;
    private AdapterChooseResume mAdapter ;

    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private int mCurPage = 1 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_resume ;
    }

    @Override
    public void initView() {
        mLoadingLay = findViewById(R.id.acti_job_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_resume_lv) ;

        mAdapter = new AdapterChooseResume(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getMyResumeList() ;
    }

    @Override
    public void initListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResumeList resumeList = mDataList.get(i) ;
                if(resumeList != null){
                    Intent reIt = new Intent() ;
                    reIt.putExtra(SkipUtils.INTENT_RESUME_INFO ,resumeList) ;
                    setResult(RESULT_OK , reIt) ;
                    finish() ;
                }
            }
        });

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i + i1 >= i2){
                    getMyResumeList() ;
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
     * 获取简历列表
     */
    private void getMyResumeList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RESUME_LIST, paramMap
                , new RequestListCallBack<ResumeList>("getMyResumeList" , mContext , ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        hiddenLoading();

                        if(1 == mCurPage){
                            mDataList.clear();
                        }
                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mCurPage ++ ;
                        }

                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mIsLoading = false ;
                        hiddenLoading();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mIsLoading = false ;
                        hiddenLoading();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getMyResumeList");

    }
}
