package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.BangMoneyDetailsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterBangMoneyDtList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyBangMoneyBillListActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mStartTimeTv ;
    private TextView mEndTimeTv ;

    private List<BangMoneyDetailsList> mDataList = new ArrayList<>() ;
    private AdapterBangMoneyDtList mAdapter ;

    private String mStartTime = null ;
    private String mEndTime = null ;

    private String mChooseStartTime = null ;
    private String mChooseEndTime = null ;
    private boolean mIsStart = true ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private DialogDatePicker dialogDatePicker;//选择年月日

    private LoadingDialog mDialog ;


    @Override
    public void requestData() {
        getBillList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_money_details ;
    }

    @Override
    public String setTitle() {
        return "积分明细";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mDialog = new LoadingDialog(mContext) ;

        mRefreshLay = findViewById(R.id.acti_my_money_dt_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_my_money_dt_lv) ;

        mStartTimeTv = findViewById(R.id.acti_my_money_details_start_time_tv) ;
        mEndTimeTv = findViewById(R.id.acti_my_money_details_end_time_tv) ;
        View mSearchView = findViewById(R.id.acti_my_money_details_search_iv) ;
        mStartTimeTv.setOnClickListener(this);
        mEndTimeTv.setOnClickListener(this);
        mSearchView.setOnClickListener(this);

        mAdapter = new AdapterBangMoneyDtList(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 10 && i + i1 >= i2){
                    getBillList() ;
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getBillList() ;
            }
        });

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;
        if(R.id.acti_my_money_details_start_time_tv == vId){
            mIsStart = true ;
            datePicker("开始日期" ,mStartTimeTv);
        }else if(R.id.acti_my_money_details_end_time_tv == vId){
            mIsStart = false ;
            datePicker("结束日期" ,mEndTimeTv);
        }else if(R.id.acti_my_money_details_search_iv == vId){
            toSearchMoneyList() ;
        }
    }

    //日期选择器
    private void datePicker(String str, final TextView view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(this, false);
        dialogDatePicker.setTitle(str);
        dialogDatePicker.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(true);
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化时间
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sDateFormat.format(new java.util.Date());
                if (DateCompareUtils.compareDay(currentDate, dialogDatePicker.getDate())) {
                    if(mIsStart){
                        if(mChooseEndTime != null && DateCompareUtils.compareDayBiger(dialogDatePicker.getDate(), mChooseEndTime)){
                            ToastUtils.showErrorToast(mContext, "开始日期不能大于结束日期");
                        }else{
                            mChooseStartTime = dialogDatePicker.getDate() ;

                            view.setEnabled(true);
                            view.setText(mChooseStartTime);
                            dialogDatePicker.dismiss();
                        }
                    }else{
                        if(mChooseStartTime != null && DateCompareUtils.compareDayLess(dialogDatePicker.getDate(),mChooseStartTime)){
                            ToastUtils.showErrorToast(mContext, "结束日期不能小于开始日期");
                        }else{
                            mChooseEndTime = dialogDatePicker.getDate() ;

                            view.setEnabled(true);
                            view.setText(mChooseEndTime);
                            dialogDatePicker.dismiss();
                        }
                    }
                } else {
                    ToastUtils.showErrorToast(mContext, "日期不能大于当前时间");
                }
            }
        });
        dialogDatePicker.show();
    }

    /**
     * 搜索
     */
    private void toSearchMoneyList(){
        if(null == mChooseStartTime || null == mChooseEndTime){
            ToastUtils.showToast(mContext ,"请选择正确的搜索区间");
            return ;
        }

        mStartTime = mChooseStartTime ;
        mEndTime = mChooseEndTime ;

        mDialog.show();

        mCurPage = 1 ;
        getBillList() ;
    }

    /**
     * 获取列表
     */
    private void getBillList(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("PageSize" ,"20") ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("StartTime" , StringUtils.convertNull(mStartTime)) ;
        paramMap.put("EndTime" ,StringUtils.convertNull(mEndTime)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_JIFEN_BILL_LIST, paramMap
                , new RequestListCallBack<BangMoneyDetailsList>("getBillList",mContext ,BangMoneyDetailsList.class) {
                    @Override
                    public void onSuccessResult(List<BangMoneyDetailsList> bean) {
                        showSuccessPage() ;
                        mDialog.dismiss() ;

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
                        mRefreshLay.finishRefresh();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;
                        mDialog.dismiss() ;
                        mIsLoading = false ;
                        mRefreshLay.finishRefresh();
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;
                        mDialog.dismiss() ;
                        mIsLoading = false ;
                        mRefreshLay.finishRefresh();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getBillList") ;

    }
}
