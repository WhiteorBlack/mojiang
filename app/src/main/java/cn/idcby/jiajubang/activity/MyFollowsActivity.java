package cn.idcby.jiajubang.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.FoFunsBean;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.FollowAndFunsAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018-04-26.
 */

public class MyFollowsActivity extends BaseActivity {

    private int myFollowsType = 0;//关注我的人是0，我的粉丝是1

    private TextView mNullTv ;
    private MaterialRefreshLayout materialRefreshLayout;
    private RecyclerView mRecycleView;

    private FollowAndFunsAdapter mAdapter;
    private List<FoFunsBean> mDataList = new ArrayList<>();
    private int mPage = 1;//页码
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private TextView mTitleTV;

    private int mCurPosition ;

    private LoadingDialog mDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_myfpllows;
    }

    @Override
    public void initView() {
        myFollowsType = getIntent().getIntExtra(SkipUtils.INTENT_FOLLOWTYPE, myFollowsType);

        mNullTv = findViewById(R.id.acti_my_flow_null_tv);
        materialRefreshLayout = findViewById(R.id.myfollow_mrl);
        mRecycleView = findViewById(R.id.myfollow_rv);
        mTitleTV= findViewById(R.id.title_funs_tv);
        if (myFollowsType==0){
            mTitleTV.setText("关注的人");
        }else {
            mTitleTV.setText("我的粉丝");
        }

        mAdapter = new FollowAndFunsAdapter(mContext,myFollowsType == 0 ,mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;
                FoFunsBean funsBean = mDataList.get(position) ;
                if(0 == type){
                    changeFocusState() ;
                }else if(1 == type){
                    SkipUtils.toOtherUserInfoActivity(mContext ,funsBean.getUserId()) ;
                }
            }
        });
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleView.addItemDecoration(new RvLinearManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext ,1)));
        mRecycleView.setAdapter(mAdapter);

        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mPage = 1 ;
                getListDatas();
            }
        });
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading && mIsMore && ViewUtil.isRecycleView2Bottom(mRecycleView)) {
                    getListDatas();
                }
            }
        });
    }

    private void getListDatas() {
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true;

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Page", mPage + "");
        paramMap.put("PageSize", "10");
        paramMap.put("Keyword", "");

        String url;
        if (myFollowsType == 0) {
            url = Urls.ME_GETMYCOLLECTIONMAN;
            paramMap.put("ID", "1");
        } else {
            url = Urls.ME_GETMYFANS;
        }
        NetUtils.getDataFromServerByPost(mContext, url, paramMap, new RequestListCallBack<FoFunsBean>("getListDatas", mContext, FoFunsBean.class) {
            @Override
            public void onSuccessResult(List<FoFunsBean> bean) {
                if(1 == mPage){
                    mDataList.clear() ;
                }

                mDataList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if (bean.size() <= 0) {
                    mIsMore = false;
                } else {
                    mIsMore = true;
                    mPage++;
                }
                finishRequest();
            }

            @Override
            public void onErrorResult(String str) {
                finishRequest();
            }

            @Override
            public void onFail(Exception e) {
                finishRequest();
            }
        });
    }

    @Override
    public void initData() {
        getListDatas();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 完成请求
     */
    private void finishRequest() {
        mIsLoading = false;
        materialRefreshLayout.finishRefresh();

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 关注、取消关注
     */
    private void changeFocusState(){
        if (mDialog == null)
            mDialog = new LoadingDialog(mContext);
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("FollowType" , "1") ;
        paramMap.put("ResourceId" , StringUtils.convertNull(mDataList.get(mCurPosition).getUserId())) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }

                        //1add
                        if(bean != null){
                            boolean isFocus = bean.AddOrDelete == 1 ;
                            mDataList.get(mCurPosition).setFocused(isFocus);
                            mAdapter.notifyDataSetChanged() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getListDatas") ;
    }
}
