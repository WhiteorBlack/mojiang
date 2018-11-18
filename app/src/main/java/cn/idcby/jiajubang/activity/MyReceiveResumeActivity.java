package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.ReceiveResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterReceiveResumeList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我发布的招聘所收到的简历
 * Created on 2018/5/9.
 */

public class MyReceiveResumeActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private View mLoadingPb ;
    private TextView mNullTv ;

    private AdapterReceiveResumeList mAdapter ;
    private List<ReceiveResumeList> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private String mJobId ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_receive_resume_list ;
    }

    @Override
    public void initView() {
        mJobId = getIntent().getStringExtra(SkipUtils.INTENT_JOB_ID) ;

        mLoadingPb = findViewById(R.id.acti_receive_resume_list_load_pb) ;
        mNullTv = findViewById(R.id.acti_receive_resume_list_null_tv) ;
        ListView mLv = findViewById(R.id.acti_receive_resume_list_lv) ;
        mRefreshLay = findViewById(R.id.acti_receive_resume_list_refresh_lay) ;

        mAdapter = new AdapterReceiveResumeList(mContext, mDataList) ;
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getResumeList();
                }
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;

                getResumeList() ;
            }
        });
    }

    @Override
    public void initData() {
        getResumeList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 获取简历列表
     */
    private void getResumeList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mJobId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_LIST_JOBS, paramMap
                , new RequestListCallBack<ReceiveResumeList>("getResumeList" , mContext , ReceiveResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ReceiveResumeList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        finishRequest() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest() ;
                    }
                });
    }


    private void finishRequest(){
        if(mLoadingPb.getVisibility() == View.VISIBLE){
            mLoadingPb.setVisibility(View.GONE);
        }

        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }
}
