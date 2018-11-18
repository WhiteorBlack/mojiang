package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.JobPost;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterJobPostList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 职位选择-- 一级
 * Created on 2018/3/27.
 */

public class JobPostListActivity extends BaseMoreStatusActivity {
    private AdapterJobPostList mAdapter ;
    private List<JobPost> mJobList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    public void requestData() {
        showLoadingPage() ;

        getJobList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_job_post_list ;
    }

    @Override
    public String setTitle() {
        return "职位选择";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        ListView lv = findViewById(R.id.acti_job_post_list_lv) ;

        mAdapter = new AdapterJobPostList(mContext , mJobList) ;
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JobPost jobPost = mJobList.get(i) ;
                if(jobPost != null){
                    if(jobPost.isHaveChilder()){
                        Intent toCtIt = new Intent(mContext ,JobPostChildListActivity.class) ;
                        toCtIt.putExtra("workPostId" , jobPost.getWorkPostID()) ;
                        mActivity.startActivityForResult(toCtIt ,1000);
                    }else{
                        Intent reIt = new Intent() ;
                        reIt.putExtra(SkipUtils.INTENT_JOB_POST ,jobPost) ;
                        setResult(RESULT_OK , reIt);
                        finish() ;
                    }
                }
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i + i1 >= i2){
                    getJobList() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 获取工作列表
     */
    private void getJobList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "30") ;
        paramMap.put("ID" , "1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_POST_LIST, paramMap
                , new RequestListCallBack<JobPost>("getJobList" ,mContext ,JobPost.class) {
                    @Override
                    public void onSuccessResult(List<JobPost> bean) {
                        showSuccessPage();

                        if(1 == mCurPage){
                            mJobList.clear();
                        }

                        mJobList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

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
                        if(1 == mCurPage){
                            showErrorPage() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {

                        mIsLoading = false ;
                        if(1 == mCurPage){
                            showErrorPage() ;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                setResult(RESULT_OK , data);
                finish() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getJobList") ;
    }
}
