package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.MyNeedsOfferList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyNeedsOffer;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的报价
 * Created on 2018/4/16.
 */
@Deprecated
public class MyNeedsOfferActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private List<MyNeedsOfferList> mDataList = new ArrayList<>() ;
    private AdapterMyNeedsOffer mAdapter ;


    @Override
    public void requestData() {
        getList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_needs_offer;
    }

    @Override
    public String setTitle() {
        return "我的报价";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mRefreshLay = findViewById(R.id.acti_my_needs_offer_refresh_lay) ;
        mNullTv = findViewById(R.id.acti_my_needs_offer_null_tv) ;
        RecyclerView mRv = findViewById(R.id.acti_my_needs_offer_rv) ;

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getList() ;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        mRv.setLayoutManager(layoutManager);
        mAdapter = new AdapterMyNeedsOffer(mActivity, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MyNeedsOfferList offerList = mDataList.get(position) ;
                if(0 == type){//发起付款
                    if(offerList != null && "4".equals(offerList.getOrderStatus())){
                        Intent toPyIt = new Intent(mContext ,MyNeedsOfferSendPayActivity.class) ;
                        toPyIt.putExtra(SkipUtils.INTENT_NEEDS_ID ,offerList.getNeedId()) ;
                        startActivityForResult(toPyIt ,1000);
                    }
                }else if(1 == type){//item点击
                    Intent toDtIt = new Intent(mContext ,NeedsDetailsActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_NEEDS_ID ,offerList.getNeedId()) ;
                    startActivityForResult(toDtIt ,1001);
                }
            }
        }) ;
        mRv.setAdapter(mAdapter);

        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(mIsMore && !mIsLoading && ViewUtil.isSlideToBottom(recyclerView)){
                    getList() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 获取列表
     */
    private void getList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_NEEDS_OFFER_LIST, paramMap
                , new RequestListCallBack<MyNeedsOfferList>("getOfferList" ,mContext , MyNeedsOfferList.class) {
                    @Override
                    public void onSuccessResult(List<MyNeedsOfferList> bean) {
                        if(1 == mCurPage){
                            showSuccessPage() ;

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
                        if(1 == mCurPage){
                            showSuccessPage() ;
                        }

                        finishRequest() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(1 == mCurPage){
                            showSuccessPage() ;
                        }

                        finishRequest() ;
                    }
                });
    }


    private void finishRequest(){
        mRefreshLay.finishRefresh();
        mIsLoading = false ;

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode){
                mCurPage = 1 ;
                getList() ;
            }
        }else if(1001 == requestCode){
            mCurPage = 1 ;
            getList() ;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getOfferList");
    }
}
