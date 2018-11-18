package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.MyCommentAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的评价
 * Created on 2018/9/15.
 */

public class MyCommentListActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private View mLoadingLay ;
    private View mNullTv ;

    //评价
    private List<GoodComment> mGoodCommentList = new ArrayList<>() ;
    private MyCommentAdapter mCommentAdapter ;

    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private int mCurPage = 1 ;

    private int mCurPosition ;
    private int mCurType = 0 ;

    private LoadingDialog mDialog ;
    private static final int REQUEST_CODE_COMMENT_READD = 1000 ;


    public static void launch(Context context){
        Intent toClIt = new Intent(context ,MyCommentListActivity.class) ;
        context.startActivity(toClIt) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_comment_list ;
    }

    @Override
    public void initView() {

        mLoadingLay = findViewById(R.id.acti_my_comment_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_my_comment_list_null_tv) ;
        mRefreshLay = findViewById(R.id.acti_my_comment_list_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_my_comment_list_lv) ;

        mCommentAdapter = new MyCommentAdapter(mActivity, mGoodCommentList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                GoodComment comment = mGoodCommentList.get(position) ;
                mCurPosition = position ;

                if(comment != null){
                    if(0 == type){
                        //自己没必要跳了
//                        SkipUtils.toOtherUserInfoActivity(mContext ,comment.getCreateUserId()) ;
                    }else if(1 == type){
                        SkipUtils.toGoodDetailsActivity(mContext ,comment.getProductID()) ;
                    }else if(2 == type){//删除评价
                        DialogUtils.showCustomViewDialog(mContext, "温馨提示", "删除该评价？", null
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                mCurType = 1 ;
                                deleteComment() ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }else if(3 == type){//添加评价
                        GoodOrderCommentResendActivity.launch(mActivity
                                ,comment.getOrderItemCommentID()
                                ,comment.getProductTitle()
                                ,comment.getImgUrl(),REQUEST_CODE_COMMENT_READD);
                    }else if(4 == type){//删除追评
                        DialogUtils.showCustomViewDialog(mContext, "温馨提示", "删除该评价？", null
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        mCurType = 2 ;
                                        deleteComment() ;
                                    }
                                }, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }
                }

            }
        }) ;
        mLv.setAdapter(mCommentAdapter) ;

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 10 && i + i1 >= i2){
                    getGoodComment() ;
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getGoodComment() ;
            }
        });
    }

    @Override
    public void initData() {
        getGoodComment() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }

    /**
     * 删除评价
     */
    private void deleteComment(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show();

        GoodComment comment = mGoodCommentList.get(mCurPosition) ;

        String commentId ;
        if(1 == mCurType){
            commentId = comment.getOrderItemCommentID() ;
        }else{
            commentId = comment.getChildInfo().getOrderItemCommentID() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , commentId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_COMMENT_LIST_MY_DELETE
                , paramMap, new RequestObjectCallBack<String>("getGoodComment" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        //暂时手动刷新界面
                        finishDelete(true) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        finishDelete(false) ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishDelete(false) ;
                    }
                });
    }

    private void finishDelete(boolean isOk){
        mDialog.dismiss() ;

        if(isOk){
            ToastUtils.showToast(mContext ,"删除成功");

            if(mCurType == 1){
                mGoodCommentList.remove(mCurPosition) ;
            }else{
                mGoodCommentList.get(mCurPosition).removeChildInfo() ;
            }
            mCommentAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 获取评价列表
     */
    private void getGoodComment(){
        ViewUtil.setViewVisible(mNullTv,false);

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_COMMENT_LIST_MY
                , paramMap, new RequestListCallBack<GoodComment>("getGoodComment" ,mContext ,GoodComment.class) {
            @Override
            public void onSuccessResult(List<GoodComment> bean) {

                if(1 == mCurPage){
                    mGoodCommentList.clear();
                }

                mGoodCommentList.addAll(bean) ;
                mCommentAdapter.notifyDataSetChanged();

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

        ViewUtil.setViewVisible(mLoadingLay,false);
        ViewUtil.setViewVisible(mNullTv,mGoodCommentList.size() == 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_COMMENT_READD == requestCode){
            if(RESULT_OK == resultCode){
                //是否刷新界面 ?
                if(mGoodCommentList.size() <= 10){
                    mCurPage = 1 ;
                    mIsMore = true ;
                    getGoodComment() ;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodComment");

    }
}
