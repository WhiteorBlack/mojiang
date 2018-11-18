package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
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
 * 我的简历
 * Created on 2018/4/13.
 */

public class MyResumeListActivity extends BaseActivity {
    private TextView mTypeResumeTv;
    private TextView mTypeBuyTv;
    private TextView mBotOpTv;

    private MaterialRefreshLayout mResumeRefreshLay;
    private ListView mResumeLv ;

    private MaterialRefreshLayout mBuyRefreshLay;
    private ListView mBuyLv ;

    private boolean mIsResumeList = true ;

    private int mResumeCurPage = 1 ;
    private boolean mResumeIsMore = true ;
    private boolean mResumeIsLoading = false ;
    private List<ResumeList> mResumeList = new ArrayList<>() ;
    private AdapterMyResumeList mResumeAdapter ;

    private int mBuyCurPage = 1 ;
    private boolean mBuyIsMore = true ;
    private boolean mBuyIsLoading = false ;
    private List<ResumeList> mBuyResumeList = new ArrayList<>() ;
    private AdapterMyResumeList mBuyResumeAdapter ;


    private static final int REQUEST_CODE_RESUME_CRATE = 1000 ;
    private static final int REQUEST_CODE_RESUME_BUY = 1001 ;

    private LoadingDialog mDialog;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_resume;
    }

    @Override
    public void initView() {
        mTypeResumeTv = findViewById(R.id.acti_my_resume_type_resume_tv) ;
        mTypeBuyTv = findViewById(R.id.acti_my_resume_type_buy_tv) ;
        mBotOpTv = findViewById(R.id.acti_my_resume_bot_op_tv) ;

        mResumeRefreshLay = findViewById(R.id.acti_my_resume_list_refresh_lay) ;
        mResumeLv = findViewById(R.id.acti_my_resume_list_lv) ;

        mBuyRefreshLay = findViewById(R.id.acti_my_resume_buy_refresh_lay) ;
        mBuyLv = findViewById(R.id.acti_my_resume_buy_lv) ;

        initMyResumeInfo() ;
        initBuyResumeInfo() ;
    }

    @Override
    public void initData() {
        getResumeList() ;
    }

    @Override
    public void initListener() {
        mTypeResumeTv.setOnClickListener(this) ;
        mTypeBuyTv.setOnClickListener(this) ;
        mBotOpTv.setOnClickListener(this) ;

        mResumeRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mResumeCurPage = 1 ;
                getResumeList() ;
            }
        });
        mResumeLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mResumeIsLoading && mResumeIsMore && i2 > 5 && i + i1 >= i2){
                    getResumeList() ;
                }
            }
        });

        mBuyRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mBuyCurPage = 1 ;
                getBuyResumeList() ;
            }
        });
        mBuyLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mBuyIsLoading && mBuyIsMore && i2 > 5 && i + i1 >= i2){
                    getBuyResumeList() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_resume_type_resume_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_resume_type_buy_tv == vId){
            changeSendType(false) ;
        }else if(R.id.acti_my_resume_bot_op_tv == vId){
            if(mIsResumeList){
                Intent toCtIt = new Intent(mContext ,CreateResumeActivity.class) ;
                startActivityForResult(toCtIt ,REQUEST_CODE_RESUME_CRATE) ;
            }else{
                Intent toCtIt = new Intent(mContext ,ResumeBuyActivity.class) ;
                startActivityForResult(toCtIt ,REQUEST_CODE_RESUME_BUY) ;
            }
        }

    }

    /**
     * 初始化 我的简历
     */
    private void initMyResumeInfo(){
        mResumeAdapter = new AdapterMyResumeList(mContext , false ,mResumeList,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, final int position) {
                if(0 == type){
                    DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                            , "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    deleteResume(mResumeList.get(position).getResumeId());
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                }else if(4 == type){
                    Intent toDtIt = new Intent(mContext , CreateResumeActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,mResumeList.get(position).getResumeId()) ;
                    startActivityForResult(toDtIt ,1000) ;
                }else if(5 == type){
                    Intent toDtIt = new Intent(mContext ,ResumeDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,mBuyResumeList.get(position).getResumeId()) ;
                    startActivity(toDtIt) ;
                }
            }
        }) ;
        mResumeLv.setAdapter(mResumeAdapter) ;
    }

    /**
     * 初始化 购买的简历
     */
    private void initBuyResumeInfo(){
        mBuyResumeAdapter = new AdapterMyResumeList(mContext,true , mBuyResumeList,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, final int position) {
                if(0 == type){
                    DialogUtils.showCustomViewDialog(mContext, "删除", "删除？", null
                            , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            deleteResume(mBuyResumeList.get(position).getResumeId());
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }else if(5 == type){
                    Intent toDtIt = new Intent(mContext ,ResumeDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,mBuyResumeList.get(position).getResumeId()) ;
                    startActivity(toDtIt) ;
                }
            }
        }) ;
        mBuyLv.setAdapter(mBuyResumeAdapter) ;
    }

    /**
     *  切换类型
     *  @param isResumeList is subscribe
     */
    private void changeSendType(boolean isResumeList){
        if(isResumeList == mIsResumeList){
            return ;
        }

        if(mIsResumeList){
            mTypeResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeBuyTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeBuyTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isResumeList){
            mTypeResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeBuyTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeBuyTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsResumeList = isResumeList ;

        mBuyRefreshLay.setVisibility(mIsResumeList ? View.GONE : View.VISIBLE) ;
        mResumeRefreshLay.setVisibility(mIsResumeList ? View.VISIBLE : View.GONE) ;
        mBotOpTv.setText(mIsResumeList ? "创建简历" : "购买简历") ;

        getDataByType() ;
    }

    /**
     * 获取相关数据
     */
    private void getDataByType(){
        if(mIsResumeList){
            if(mResumeList.size() == 0){
                getResumeList() ;
            }
        }else{
            if(mBuyResumeList.size() == 0){
                getBuyResumeList() ;
            }
        }
    }

    /**
     * 获取我的简历列表
     */
    private void getResumeList(){
        Map<String ,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mResumeCurPage) ;
        paramMap.put("PageSize" ,"10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RESUME_LIST, paramMap
                , new RequestListCallBack<ResumeList>("getResumeList" ,mContext ,ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }

                        if(1 == mResumeCurPage){
                            mResumeList.clear();
                        }

                        mResumeList.addAll(bean) ;
                        mResumeAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mResumeIsMore = false ;
                        }else{
                            mResumeIsMore = true ;
                            mResumeCurPage ++ ;
                        }

                        mResumeIsLoading = false ;
                        mResumeRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                        mResumeIsLoading = false ;
                        mResumeRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                        mResumeIsLoading = false ;
                        mResumeRefreshLay.finishRefresh() ;
                    }
                });
    }

    /**
     * 获取购买简历列表
     */
    private void getBuyResumeList(){
        Map<String ,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mBuyCurPage) ;
        paramMap.put("PageSize" ,"10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RESUME_BUY_LIST, paramMap
                , new RequestListCallBack<ResumeList>("getBuyResumeList" ,mContext ,ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                        if(1 == mBuyCurPage){
                            mBuyResumeList.clear();
                        }

                        mBuyResumeList.addAll(bean) ;
                        mBuyResumeAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mBuyIsMore = false ;
                        }else{
                            mBuyIsMore = true ;
                            mBuyCurPage ++ ;
                        }

                        mBuyIsLoading = false ;
                        mBuyRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                        mBuyIsLoading = false ;
                        mBuyRefreshLay.finishRefresh() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                        mBuyIsLoading = false ;
                        mBuyRefreshLay.finishRefresh() ;
                    }
                });
    }

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
                        if(mIsResumeList){
                            mResumeCurPage = 1 ;
                            getResumeList() ;
                        }else{
                            mBuyCurPage = 1 ;
                            getBuyResumeList();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常") ;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mIsResumeList){
            mResumeCurPage = 1 ;
            getResumeList() ;
        }else{
            mBuyCurPage = 1 ;
            getBuyResumeList() ;
        }
    }
}
