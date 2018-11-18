package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleTransportActivity;
import cn.idcby.jiajubang.adapter.AdapterCircleActive;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我发布的圈子
 * 2018-05-19
 */

public class MySendCircleFragment extends BaseFragment {
    private LoadingDialog loadingDialog;

    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;
    private AdapterCircleActive mAdapter ;

    private List<UserActive> mDataList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    private int mCurPosition ;

    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1005 ;
    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1006 ;


    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getCircleActive() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        loadingDialog = new LoadingDialog(mContext) ;

        mRefreshLay = view.findViewById(R.id.frag_my_circle_refresh_lay);
        mRecyclerView = view.findViewById(R.id.frag_my_circle_lv);

        mAdapter = new AdapterCircleActive( mActivity, mDataList,false,true, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;

                if(AdapterCircleActive.CLICK_TYPE_DELETE == type){
                    DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                            , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            deleteCircle() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }else if(AdapterCircleActive.CLICK_TYPE_SUPPORT == type){
                    supportCircle() ;
                }else if(AdapterCircleActive.CLICK_TYPE_TRANS == type){
                    if (LoginHelper.isNotLogin(mContext)) {
                        SkipUtils.toLoginActivityForResult(mFragment, REQUEST_CODE_CIRCLE_TO_TRANSPORT);
                        return;
                    }

                    toTransportActivity();
                }else if(AdapterCircleActive.CLICK_TYPE_SHARE == type){
                    UserActive info = mDataList.get(mCurPosition) ;
                    if(info != null){
                        String title = info.getBodyContent() ;
                        String imgUrl = info.getImgUrl() ;
                        String strUrl = info.getH5Url();

                        ShareUtils.shareWeb(mActivity ,title,strUrl ,imgUrl ,"") ;
                    }
                }
            }
            @Override
            public void onItemLongClickListener(int type, int position) {

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_my_circle;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getCircleActive();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getCircleActive();
                }
            }
        });
    }

    /**
     * 跳转到转载
     */
    private void toTransportActivity(){
        UserActive info = mDataList.get(mCurPosition) ;
        if(info != null){
            CircleTransportActivity.launch(mFragment ,info.getPostID()
                    ,info.getBodyContent() ,info.getImgUrl()
                    ,info.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
        }

    }

    /**
     * 列表
     */
    private void getCircleActive(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Type" , "" + MySendFragment.SEND_TYPE_CIRCLE) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_SEND_LIST, false, paramMap
                , new RequestListCallBack<UserActive>("getCircleActive" , mContext ,UserActive.class) {
                    @Override
                    public void onSuccessResult(List<UserActive> bean) {
                        loadPage.showSuccessPage();

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

                        mIsLoading = false ;
                        mRefreshLay.finishRefresh();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mIsLoading = false ;
                        loadPage.showSuccessPage();
                        mRefreshLay.finishRefresh();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mIsLoading = false ;
                        loadPage.showSuccessPage();
                        mRefreshLay.finishRefresh();
                    }
                });
    }

    /**
     * 删除圈子
     */
    private void deleteCircle(){
        loadingDialog.show();

        UserActive userActive = mDataList.get(mCurPosition) ;
        String circleId = userActive.getPostID() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , circleId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_MY_DELETE, false, paramMap
                , new RequestListCallBack<UserActive>("deleteCircle" , mContext ,UserActive.class) {
                    @Override
                    public void onSuccessResult(List<UserActive> bean) {
                        loadingDialog.dismiss();

                        refreshListDisplay();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }


    /**
     * 点赞
     */
    private void supportCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mFragment , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        String articleID = mDataList.get(mCurPosition).getPostID() ;

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", articleID);
        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_SUPPORT, false, para,
                new RequestObjectCallBack<SupportResult>("supportCircle", mContext, SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        mDataList.get(mCurPosition).setSupportState(bean.AddOrDelete ,bean.LikeNumber) ;
                        mAdapter.notifyDataSetChanged() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    //刷新
    public void refreshListDisplay(){
        mCurPage = 1 ;
        getCircleActive();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CIRCLE_SUPPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                supportCircle() ;
            }
        }else if(REQUEST_CODE_CIRCLE_TO_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                toTransportActivity() ;
            }
        }else if(REQUEST_CODE_CIRCLE_FROM_TRANSPORT == requestCode){
            if(Activity.RESULT_OK == resultCode){
                if(mDataList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getCircleActive() ;
                }else{
                    mDataList.get(mCurPosition).updateTransCount();
                    mAdapter.notifyDataSetChanged() ;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCircleActive");

    }

}
