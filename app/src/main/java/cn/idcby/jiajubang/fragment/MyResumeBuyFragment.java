package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ResumeDetailActivity;
import cn.idcby.jiajubang.adapter.AdapterMyResumeList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我购买的简历
 * 2018-05-19
 */

public class MyResumeBuyFragment extends BaseFragment {
    private LoadingDialog mDialog;

    private View mLoadingView ;
    private View mNullView ;

    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv;

    private List<ResumeList> mDataList = new ArrayList<>() ;
    private AdapterMyResumeList mAdapter;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;


    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getDataList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mDialog = new LoadingDialog(mContext) ;

        mLoadingView = view.findViewById(R.id.lay_loading_lv_progressBar);
        mNullView = view.findViewById(R.id.lay_loading_lv_null_tv);
        mRefreshLay = view.findViewById(R.id.lay_loading_lv_refresh_lay);
        mLv = view.findViewById(R.id.lay_loading_lv_list_lv);

        mAdapter = new AdapterMyResumeList(mContext ,true, mDataList,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, final int position) {
                ResumeList resumeList = mDataList.get(position) ;
                if(0 == type){
                    DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                            , "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    deleteResume(mDataList.get(position).getResumeId());
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                }else if(5 == type){
                    Intent toDtIt = new Intent(mContext ,ResumeDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,resumeList.getResumeId()) ;
                    startActivity(toDtIt) ;
                }
            }
        }) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.lay_loading_lv;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getDataList();
            }
        });

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getDataList() ;
                }
            }
        });
    }

    /**
     * 删除
     * @param resumeId reid
     */
    private void deleteResume(String resumeId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,resumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RESUME_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteResume",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        getDataList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getDataList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RESUME_BUY_LIST, false, paramMap
                , new RequestListCallBack<ResumeList>("getDataList" , mContext ,ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                            mAdapter.notifyDataSetChanged() ;
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

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

    public void refreshList(){
        mCurPage = 1 ;
        getDataList() ;
    }

    private void finishRequest(){
        mLoadingView.setVisibility(View.GONE);
        mNullView.setVisibility(mDataList.size() == 0 ? View.VISIBLE : View.GONE);

        mIsLoading = false ;
        loadPage.showSuccessPage();
        mRefreshLay.finishRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            if(mIsReload && mDataList != null && mDataList.size() == 0){
                getDataList() ;
            }
        }else{
            NetUtils.cancelTag("getDataList");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(Activity.RESULT_OK == resultCode){
                refreshList();
            }
        }

    }
}
