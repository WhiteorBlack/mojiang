package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.UnuseCommentList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterUnuseComment;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.AddCommentPopup;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 闲置留言
 * Created on 2018/9/13.
 */

public class UnuseGoodCommentList extends BaseActivity {
    private String mUnuseId ;

    private MaterialRefreshLayout mRefreshLay ;
    private View mNullTv ;

    private List<UnuseCommentList> mCommentList = new ArrayList<>() ;
    private AdapterUnuseComment mCommentAdapter ;

    private static final int REQUEST_CODE_COMMENT = 1007 ;
    private static final int REQUEST_CODE_COMMENT_REPLY = 1008 ;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_unuse_good_comment_list;
    }

    @Override
    public void initView() {
        super.initView();

        mUnuseId = getIntent().getStringExtra(SkipUtils.INTENT_UNUSE_ID) ;

        View mRightTv = findViewById(R.id.acti_unuse_good_comment_send_tv) ;
        mNullTv = findViewById(R.id.acti_unuse_good_comment_null_tv) ;
        mRefreshLay = findViewById(R.id.acti_unuse_good_comment_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_unuse_good_comment_lv) ;

        mRightTv.setOnClickListener(this);

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                mIsMore = true ;

                getUnuseCommentList() ;
            }
        });

        mCommentAdapter = new AdapterUnuseComment(mCommentList, mActivity, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;

                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_COMMENT_REPLY);
                    }else{
                        UnuseCommentList commentList = mCommentList.get(position) ;
                        if(commentList != null){
                            addCommentToList(2 ,commentList.getID()) ;
                        }
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
                if(!mIsLoading && mIsMore && i2 >=10 && i + i1 >= i2){
                    getUnuseCommentList() ;
                }
            }
        });

        getUnuseCommentList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_unuse_good_comment_send_tv == view.getId()){
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity , REQUEST_CODE_COMMENT);
                return;
            }

            addCommentToList(1,"") ;
        }
    }

    /**
     * 评论
     * @param commentLevel level
     * @param parentId parentId
     */
    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_UNUSE
                    , findViewById(R.id.acti_unuse_comment_parent_lay), new AddCommentCallBack() {
                @Override
                public void commentCallBack(String commentNum) {
                    getUnuseCommentList();
                    if(StringUtils.convertString2Count(commentNum) > 0){
                        mNullTv.setVisibility(View.VISIBLE) ;
                    }else{
                        mNullTv.setVisibility(View.GONE);
                    }
                }
            }) ;
        }

        mCommentPopup.displayDialog(mUnuseId, commentLevel, parentId);
    }

    /**
     * 获取评论列表
     */
    private void getUnuseCommentList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mUnuseId)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_COMMENT_LIST, paramMap
                , new RequestListCallBack<UnuseCommentList>("getUnuseCommentList" ,mContext , UnuseCommentList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseCommentList> bean) {
                        if(1 == mCurPage){
                            mCommentList.clear();
                        }
                        mCommentList.addAll(bean) ;
                        mCommentAdapter.notifyDataSetChanged();

                        if(bean.size() < 10){
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
        mRefreshLay.finishRefresh() ;

        mIsLoading = false ;

        if(mCommentList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }else{
            mNullTv.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_COMMENT == requestCode){
            if(RESULT_OK == resultCode){
                addCommentToList(1 ,"") ;
            }
        }else if(REQUEST_CODE_COMMENT_REPLY == requestCode){
            if(RESULT_OK == resultCode){
                UnuseCommentList commentList = mCommentList.get(mCurPosition) ;
                if(commentList != null){
                    addCommentToList(2 ,commentList.getID()) ;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUnuseCommentList") ;
    }
}
