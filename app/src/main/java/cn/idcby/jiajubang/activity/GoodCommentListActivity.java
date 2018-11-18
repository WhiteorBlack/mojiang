package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.GoodComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.GoodCommentAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 商品评价
 * Created on 2018/8/17.
 */

public class GoodCommentListActivity extends BaseActivity {
    private String mGoodId;

    private MaterialRefreshLayout mRefreshLay ;

    //评论
    private List<GoodComment> mGoodCommentList = new ArrayList<>() ;
    private GoodCommentAdapter mCommentAdapter ;


    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;
    private int mCurPage = 1 ;


    public static void launch(Context context ,String goodId,String goodImage){
        Intent toClIt = new Intent(context ,GoodCommentListActivity.class) ;
        toClIt.putExtra(SkipUtils.INTENT_GOOD_ID ,goodId) ;
        toClIt.putExtra(SkipUtils.INTENT_GOOD_IMAGE ,goodImage) ;
        context.startActivity(toClIt) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_good_comment_list ;
    }

    @Override
    public void initView() {
        String goodImgUrl = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_IMAGE));
        mGoodId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_ID));

        if(TextUtils.isEmpty(mGoodId)){
            DialogUtils.showCustomViewDialog(mContext, "商品信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });

            return ;
        }

        ImageView goodIv = findViewById(R.id.acti_good_comment_good_iv) ;
        GlideUtils.loader(goodImgUrl,goodIv);

        mRefreshLay = findViewById(R.id.acti_good_comment_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_good_comment_lv) ;

        mCommentAdapter = new GoodCommentAdapter(mActivity, mGoodCommentList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodComment comment = mGoodCommentList.get(position) ;
                    if(comment != null){
                        SkipUtils.toOtherUserInfoActivity(mContext ,comment.getCreateUserId()) ;
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
     * 获取评价列表
     */
    private void getGoodComment(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Keyword" , mGoodId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_COMMENT_LIST
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodComment");

    }
}
