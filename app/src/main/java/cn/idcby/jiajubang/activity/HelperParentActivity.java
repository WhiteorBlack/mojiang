package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.HelperBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterHelperParent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 帮助与反馈--一级菜单
 * Created on 2018/5/2.
 */
public class HelperParentActivity extends BaseMoreStatusActivity {
    private List<HelperBean> mDataList = new ArrayList<>() ;
    private AdapterHelperParent mAdapter ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    public void requestData() {
        getCategoryList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_helper;
    }

    @Override
    public String setTitle() {
        return "帮助与反馈";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        tvRight.setText("反馈");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNextActivity(FeedBackActivity.class);
            }
        });
    }

    @Override
    public void init() {
        ListView mLv = findViewById(R.id.acti_helper_lv) ;

        mAdapter = new AdapterHelperParent(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter);
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && mDataList.size() >= 10 && i + i1 >= i2){
                    getCategoryList();
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void getCategoryList(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("code" ,"Systems") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.HELPER_PARENT_URL, paramMap
                , new RequestListCallBack<HelperBean>("getCategoryList" ,mContext ,HelperBean.class) {
                    @Override
                    public void onSuccessResult(List<HelperBean> bean) {
                        showSuccessPage() ;

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

                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCategoryList") ;

    }
}
