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
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.ResumeBuyList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterResumeBuy;
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
 * 购买简历
 * Created on 2018/4/12.
 */

public class ResumeBuyActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;

    private AdapterResumeBuy mAdapter ;
    private List<ResumeBuyList> mDataList = new ArrayList<>() ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private LoadingDialog mLoadDialog ;

    private static final int REQUEST_CODE_BUY_RESUME = 1000 ;


    @Override
    public void requestData() {
        getBuyResumeList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_resume_buy;
    }

    @Override
    public String setTitle() {
        return "购买简历";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mRefreshLay = findViewById(R.id.acti_resume_buy_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_resume_buy_lv) ;

        mAdapter = new AdapterResumeBuy(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    ResumeBuyList resume = mDataList.get(position) ;
                    buyResume(resume.getSetMealId()) ;
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getBuyResumeList() ;
            }
        });

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 10 && i + i1 >= i2){
                    getBuyResumeList() ;
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
    private void getBuyResumeList(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"20") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_BUY_LIST, paramMap
                , new RequestListCallBack<ResumeBuyList>("getBuyResumeList" , mContext ,ResumeBuyList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeBuyList> bean) {

                        if(1 == mCurPage){
                            mDataList.clear();
                        }
                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() > 0){
                            mIsMore = true ;
                            mCurPage ++ ;
                        }else{
                            mIsMore = false ;
                        }

                        mIsLoading = false ;
                        mRefreshLay.finishRefresh() ;
                        showSuccessPage();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mIsLoading = false ;
                        mRefreshLay.finishRefresh() ;


                        showSuccessPage();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mIsLoading = false ;
                        mRefreshLay.finishRefresh() ;
                        showSuccessPage();
                    }
                });
    }

    /**
     * 购买简历
     */
    private void buyResume(String resumeId){
        if(null == mLoadDialog){
            mLoadDialog = new LoadingDialog(mContext) ;
        }
        mLoadDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,resumeId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_BUY, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("buyResume" ,mContext , NeedsBondPayResult.class){
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mLoadDialog.dismiss() ;

                        if(bean != null){
                            String moneys = bean.PayableAmount ;
                            //跳转到付款界面
                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,bean.OrderID) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_RESUME) ;
                            startActivityForResult(toPyIt ,REQUEST_CODE_BUY_RESUME);
                        }else{
                            ToastUtils.showErrorToast(mContext , "提交成功，需要付款") ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mLoadDialog.dismiss() ;
                        ToastUtils.showToast(mContext ,"购买失败") ;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_BUY_RESUME){
            if(RESULT_OK == resultCode){
                ToastUtils.showToast(mContext ,"购买成功") ;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getBuyResumeList") ;

    }
}
