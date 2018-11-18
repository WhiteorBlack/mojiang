package cn.idcby.jiajubang.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.jiajubang.Bean.SellerGoodsBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.SellerGoodsRecycleViewAdapter;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 店铺首页-商品列表
 * 2018-04-24
 */

public class StoreGoodFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private int mGoodType = 0 ;//0 推荐  1全部
    private String mStoreId ;

    private boolean mIsReload = false ;
    List<SellerGoodsBean> mGoodsBeans=new ArrayList<>();
    SellerGoodsRecycleViewAdapter mSellerGoodsRecycleViewAdapter;

    public static StoreGoodFragment newInstance(int type,String storeId){
        StoreGoodFragment fragment = new StoreGoodFragment() ;
        fragment.mGoodType = type ;
        fragment.mStoreId = storeId ;
        return fragment ;
    }

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();
        doinitadapter();
        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
//                getCollectionResumeList() ;
                doinitadapter();
            }
        }
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.frag_store_good_list_rv);

    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_store_good_list;
    }

    @Override
    protected void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }
        });
    }


    /**
     * 列表
     */
    private void getCollectionResumeList(){

        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Page", "1");
        paramMap.put("PageSize", "18");
        paramMap.put("Keyword", "");
        paramMap.put("storeid", mStoreId);//推荐商品还是店铺所有商品
        NetUtils.getDataFromServerByPost(mContext, Urls.DIRECT_SPEC_LIST, paramMap,
                new RequestListCallBack<SellerGoodsBean>("商家商品列表", mContext, SellerGoodsBean.class) {
                    @Override
                    public void onSuccessResult(List<SellerGoodsBean> bean) {
                        if (bean.size() > 0) {
                            mGoodsBeans = bean;
                            doinitadapter();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {

                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }

    private void doinitadapter() {
        SellerGoodsBean msee;
        List<SellerGoodsBean> lists=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            msee=new SellerGoodsBean();
            msee.setProductTitle("ceshi"+i);
            msee.setProductID("我是产品id"+i);
            lists.add(msee);
        }
        mGoodsBeans.addAll(lists);
        mSellerGoodsRecycleViewAdapter=new SellerGoodsRecycleViewAdapter(mContext,mGoodsBeans);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        mRecyclerView.setAdapter(mSellerGoodsRecycleViewAdapter);
        loadPage.showSuccessPage();
        mSellerGoodsRecycleViewAdapter.setItemClickListener(new SellerGoodsRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tags) {
                SkipUtils.toGoodDetailsActivity(mContext,tags) ;
            }
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        doinitadapter();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

}
