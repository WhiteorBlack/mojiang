package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.jiajubang.Bean.MyNeedsOfferList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ActivityBondPay;
import cn.idcby.jiajubang.activity.MyNeedOfferFinishActivity;
import cn.idcby.jiajubang.activity.MyNeedsOfferSendPayActivity;
import cn.idcby.jiajubang.activity.NeedsConfirmBidActivity;
import cn.idcby.jiajubang.activity.NeedsDetailsActivity;
import cn.idcby.jiajubang.adapter.AdapterMyNeedsOffer;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的报价
 * Created on 2018/5/19.
 *
 * 2018-06-01 19:48:40
 * 改：未中标前可以修改
 *
 * 2018-06-08 10:04:12
 * 改：取消 发起付款 功能
 *
 * 2018-06-11 16:53:55
 * 改：添加 完成 功能，跳转到 完成 界面，填写相关信息
 */

public class MyNeedsOfferFragment extends Fragment {
    private Activity mContext ;

    private View mView ;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;
    private View mLoadingPb ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private boolean mIsFirstLoad = true ;

    private List<MyNeedsOfferList> mDataList = new ArrayList<>() ;
    private AdapterMyNeedsOffer mAdapter ;

    private int mCurPosition ;

    private static final int REQUEST_CODE_EDIT = 1010 ;
    private static final int REQUEST_CODE_PAY = 1011 ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = getActivity() ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_my_needs_offer,container ,false) ;

            mRefreshLay = mView.findViewById(R.id.frag_my_needs_offer_refresh_lay) ;
            mLoadingPb = mView.findViewById(R.id.frag_my_needs_offer_loading_pb) ;
            mNullTv = mView.findViewById(R.id.frag_my_needs_offer_null_tv) ;
            RecyclerView mRv = mView.findViewById(R.id.frag_my_needs_offer_rv) ;

            mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mCurPage = 1 ;
                    getList() ;
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
            mRv.setLayoutManager(layoutManager);
            mAdapter = new AdapterMyNeedsOffer(mContext, mDataList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    MyNeedsOfferList offerList = mDataList.get(position) ;
                    if(0 == type){//发起付款
                        if(offerList != null && offerList.canOrderStartPay()){
                            Intent toPyIt = new Intent(mContext ,MyNeedsOfferSendPayActivity.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_NEEDS_ID ,offerList.getNeedId()) ;
                            startActivityForResult(toPyIt ,1000);
                        }
                    }else if(1 == type){//item点击
                        Intent toDtIt = new Intent(mContext ,NeedsDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_NEEDS_ID ,offerList.getNeedId()) ;
                        toDtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED ,offerList.isBid()) ;
                        startActivityForResult(toDtIt ,1001);
                    }else if(2 == type){//查看
                        mCurPosition =  position ;

                        String offerId = offerList.getOfferId() ;
                        if("1".equals(offerList.getOrderStatus())){//待缴纳保证金
                            String moneys = offerList.getOrderAmount() ;
                            //跳转到付款界面
                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,offerId) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,offerList.getOrderNO()) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_BOND_REQ_BID) ;
                            startActivityForResult(toPyIt ,REQUEST_CODE_PAY);
                        }else{
                            Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , offerList.getNeedId()) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_TYPE , StringUtils.convertString2Count(offerList.getTypeId())) ;//招标是2
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED ,offerList.isBid()) ;
                            //2018-06-02 13:51:41 允许修改报价
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , offerId) ;
                            startActivityForResult(toCtIt ,REQUEST_CODE_EDIT);
                        }
                    }else if(3 == type){//上下架--暂时不做

                    }else if(4 == type){//删除--暂时不做

                    }else if(5 == type){//完成需求
                        Intent toFiIt = new Intent(mContext , MyNeedOfferFinishActivity.class) ;
                        startActivity(toFiIt) ;
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

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    getList() ;
                }
            }
        }

        return mView;
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
        mRefreshLay.finishRefresh();
        mIsLoading = false ;

        mLoadingPb.setVisibility(View.GONE);

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mLoadingPb.setVisibility(View.VISIBLE);

                mCurPage = 1 ;
                getList() ;
            }
        }else if(1001 == requestCode){
            mLoadingPb.setVisibility(View.VISIBLE);

            mCurPage = 1 ;
            getList() ;
        }else if(REQUEST_CODE_EDIT == requestCode){
            mLoadingPb.setVisibility(View.VISIBLE);

            mCurPage = 1 ;
            getList() ;
        }else if(REQUEST_CODE_PAY == requestCode){
            if(Activity.RESULT_OK == resultCode){
                MyNeedsOfferList offerList = mDataList.get(mCurPosition) ;

                Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , offerList.getNeedId()) ;
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_TYPE , StringUtils.convertString2Count(offerList.getTypeId())) ;//招标是2
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED ,offerList.isBid()) ;
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , offerList.getOfferId()) ;
//                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_PAY_OFFER_ID , offerList.getOfferId()) ;
                startActivityForResult(toCtIt ,REQUEST_CODE_EDIT);
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mDataList != null &&  mDataList.size() == 0){
                    mLoadingPb.setVisibility(View.VISIBLE);

                    mCurPage = 1 ;
                    getList();
                }
            }else{
                NetUtils.cancelTag("getOfferList") ;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getOfferList") ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }
    }
}
