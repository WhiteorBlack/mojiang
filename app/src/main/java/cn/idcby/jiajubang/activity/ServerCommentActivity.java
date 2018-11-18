package cn.idcby.jiajubang.activity;

import android.content.Context;
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
import cn.idcby.jiajubang.Bean.ServerComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServerCommentAdapter;
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
 * 服务评价列表
 * Created on 2018/5/24.
 */

public class ServerCommentActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private boolean mIsInstall = false ;
    private String mServerUserId ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private List<ServerComment> mCommentList = new ArrayList<>() ;
    private ServerCommentAdapter mCommentAdapter ;

    private TextView mFooterTv ;


    public static void launch(Context context ,boolean isInstall ,String serverUserId){
        Intent toClIt = new Intent(context,ServerCommentActivity.class) ;
        toClIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID ,serverUserId) ;
        toClIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL ,isInstall) ;
        context.startActivity(toClIt) ;
    }

    @Override
    public void requestData() {
        showLoadingPage();

        getDataList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_server_comment_list;
    }

    @Override
    public String setTitle() {
        return "服务评价";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mServerUserId = getIntent().getStringExtra(SkipUtils.INTENT_SERVER_USER_ID) ;
        mIsInstall = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL,mIsInstall) ;

        mRefreshLay = findViewById(R.id.acti_server_comment_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_server_comment_list_lv) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mCommentAdapter = new ServerCommentAdapter(mContext ,mCommentList) ;
        mLv.setAdapter(mCommentAdapter) ;

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getDataList() ;
            }
        });
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 7 && i + i1 >= i2){
                    getDataList() ;
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
    private void getDataList(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string));
        }

        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mServerUserId)) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("ID" ,"" + (mIsInstall
                ? ServerConfirmActivity.SERVER_INSTALL_TYPE
                : ServerConfirmActivity.SERVER_SERVER_TYPE)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_COMMENT_LIST, paramMap
                , new RequestListCallBack<ServerComment>("getDataList" ,mContext ,ServerComment.class) {
                    @Override
                    public void onSuccessResult(List<ServerComment> bean) {
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
        showSuccessPage();
        mRefreshLay.finishRefresh();
        mIsLoading = false ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mCommentList.size() == 0){
            mFooterTv.setText("暂无评论");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList") ;
    }
}
