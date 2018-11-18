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
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.TopicList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterTopicList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/3/24.
 */

public class TopicHotActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;

    private AdapterTopicList mTopicAdapter ;
    private List<TopicList> mHotTopicList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    public void requestData() {
        showLoadingPage();

        getHotList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_hot_topic_list;
    }

    @Override
    public String setTitle() {
        return "热门话题";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mRefreshLay = findViewById(R.id.acti_hot_topic_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_hot_topic_lv) ;

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i > 5 && i1 + i2 >= i){
                    getHotList() ;
                }
            }
        });

        int topImgWid = ResourceUtils.getScreenWidth(mContext) ;
        mTopicAdapter = new AdapterTopicList(mContext ,mHotTopicList ,topImgWid) ;
        mLv.setAdapter(mTopicAdapter) ;

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getHotList() ;
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
    }

    /**
     * 热门话题
     */
    private void getHotList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.TOPIC_LIST, false, paramMap
                , new RequestListCallBack<TopicList>("getHotList" , mContext ,TopicList.class) {
                    @Override
                    public void onSuccessResult(List<TopicList> bean) {
                        showSuccessPage() ;

                        if(1 == mCurPage){
                            mHotTopicList.clear();
                        }

                        if(bean != null){
                            mHotTopicList.addAll(bean) ;
                        }
                        mTopicAdapter.notifyDataSetChanged() ;

                        if(null == bean || bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        mRefreshLay.finishRefresh() ;
                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mRefreshLay.finishRefresh() ;
                        mIsLoading = false ;
                        showSuccessPage() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mRefreshLay.finishRefresh() ;
                        mIsLoading = false ;
                        showSuccessPage() ;
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getHotList");
    }
}
