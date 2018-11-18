package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.NeedsBidSeller;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedBidSeller;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 *  需求参与商家列表
 * Created on 2018/3/29.
 */

public class NeedsBidSellerActivity extends BaseMoreStatusActivity {
    private String mNeedId ;
    private boolean mIsSelfSend = false ;
    private boolean mIsBonded = false ;
    private int mNeedStyleType = 0 ;

    private List<NeedsBidSeller> mBidList = new ArrayList<>() ;
    private AdapterNeedBidSeller mAdapter ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    public void requestData() {
        showLoadingPage() ;

        getNeedBidSeller() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_needs_seller_list ;
    }

    @Override
    public String setTitle() {
        return "参与商家";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mNeedStyleType = getIntent().getIntExtra(SkipUtils.INTENT_NEEDS_CATEGORY_TYPE,mNeedStyleType) ;
        mIsBonded = getIntent().getBooleanExtra(SkipUtils.INTENT_NEEDS_IS_BONDED,mIsBonded) ;
        mIsSelfSend = getIntent().getBooleanExtra(SkipUtils.INTENT_NEEDS_IS_SELF ,false) ;
        mNeedId = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_ID) ;

        ListView mLv = findViewById(R.id.acti_needs_seller_list_lv) ;
        mAdapter = new AdapterNeedBidSeller(mContext, mBidList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                NeedsBidSeller seller = mBidList.get(position) ;
                if(seller != null){
                    if(0 == type){
                        if(mIsSelfSend || SPUtils.newIntance(mContext).getUserNumber().equals(seller.UserId)){
                            String offerId = seller.OfferId ;
                            Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , offerId) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_SELF , mIsSelfSend) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED , mIsBonded) ;
                            startActivity(toCtIt) ;
                        }
                    }else if(1 == type){
                        String userId = seller.getUserId() ;
                        if(SkipUtils.NEED_STYLE_TYPE_SERVER == mNeedStyleType
                                || SkipUtils.NEED_STYLE_TYPE_INSTALL == mNeedStyleType){//服务需求、安装需求
                            ServerDetailActivity.launch(mContext ,userId,false) ;
                        }else if(SkipUtils.NEED_STYLE_TYPE_GOOD == mNeedStyleType
                                || SkipUtils.NEED_STYLE_TYPE_UNUSE == mNeedStyleType){//商品需求、闲置需求
                            SkipUtils.toOtherUserInfoActivity(mContext ,userId) ;
                        }
                    }
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);
        mAdapter.setIsSelf(mIsSelfSend) ;

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 10 && i + i1 >= i2){
                    getNeedBidSeller() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void getNeedBidSeller(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("NeedId" , StringUtils.convertNull(mNeedId)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "20") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_SELLER_LIST, paramMap
                , new RequestListCallBack<NeedsBidSeller>("getSellerList" , mContext ,NeedsBidSeller.class) {
                    @Override
                    public void onSuccessResult(List<NeedsBidSeller> bean) {
                        showSuccessPage() ;

                        if(1 == mCurPage){
                            mBidList.clear();
                        }

                        mBidList.addAll(bean) ;
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
                        if(1 == mCurPage){
                            showErrorPage() ;
                        }
                        mIsLoading = false ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(1 == mCurPage){
                            showErrorPage() ;
                        }
                        mIsLoading = false ;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getSellerList") ;
    }
}
