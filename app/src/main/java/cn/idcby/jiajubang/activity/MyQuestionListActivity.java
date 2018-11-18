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
import cn.idcby.jiajubang.Bean.MyAnswer;
import cn.idcby.jiajubang.Bean.MyQuestion;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyQuestionAnswerList;
import cn.idcby.jiajubang.adapter.AdapterMyQuestionList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的问答
 * Created on 2018/5/3.
 */

public class MyQuestionListActivity extends BaseActivity {
    private TextView mTypeQuestTv;
    private TextView mTypeAnswerTv;

    private MaterialRefreshLayout mQuestionRefreshLay;
    private ListView mQuestionLv;

    private MaterialRefreshLayout mAnswerRefreshLay;
    private ListView mAnswerLv;

    private boolean mIsQuestionList = true ;

    private int mQuestionCurPage = 1 ;
    private boolean mQuestionIsMore = true ;
    private boolean mQuestionIsLoading = false ;
    private List<MyQuestion> mQuestionList = new ArrayList<>() ;
    private AdapterMyQuestionList mQuestionAdapter;

    private int mAnswerCurPage = 1 ;
    private boolean mAnswerIsMore = true ;
    private boolean mAnswerIsLoading = false ;
    private List<MyAnswer> mAnswerList = new ArrayList<>() ;
    private AdapterMyQuestionAnswerList mAnswerAdapter;


    private LoadingDialog mDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_question;
    }

    @Override
    public void initView() {
        mDialog = new LoadingDialog(mContext) ;
        mDialog.setCancelable(false);

        mTypeQuestTv = findViewById(R.id.acti_my_question_type_question_tv) ;
        mTypeAnswerTv = findViewById(R.id.acti_my_question_type_answer_tv) ;

        mQuestionRefreshLay = findViewById(R.id.acti_my_question_list_refresh_lay) ;
        mQuestionLv = findViewById(R.id.acti_my_question_list_lv) ;

        mAnswerRefreshLay = findViewById(R.id.acti_my_question_answer_lay) ;
        mAnswerLv = findViewById(R.id.acti_my_question_answer_lv) ;

        initQuestionInfo() ;
        initAnswerInfo() ;
    }

    @Override
    public void initData() {
        getQuestionList() ;
    }

    @Override
    public void initListener() {
        mTypeQuestTv.setOnClickListener(this) ;
        mTypeAnswerTv.setOnClickListener(this) ;

        mQuestionRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mQuestionCurPage = 1 ;
                getQuestionList() ;
            }
        });
        mQuestionLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mQuestionIsLoading && mQuestionIsMore && i2 > 5 && i + i1 >= i2){
                    getQuestionList() ;
                }
            }
        });

        mAnswerRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mAnswerCurPage = 1 ;
                getAnswerList() ;
            }
        });
        mAnswerLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mAnswerIsLoading && mAnswerIsMore && i2 > 5 && i + i1 >= i2){
                    getAnswerList() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_question_type_question_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_question_type_answer_tv == vId){
            changeSendType(false) ;
        }
    }

    /**
     * 初始化 我的问题
     */
    private void initQuestionInfo(){
        mQuestionAdapter = new AdapterMyQuestionList(mActivity, mQuestionList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MyQuestion question = mQuestionList.get(position) ;
                if(question != null){
                    final String questionId = question.getQuestionId() ;

                    if(0 == type){
                        DialogUtils.showCustomViewDialog(mContext, "删除", "删除该问题？", null
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                deleteMyQuestion(questionId) ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }else if(1 == type){//编辑
                        Intent toEtIt = new Intent(mContext ,QuestionCreateActivity.class) ;
                        toEtIt.putExtra(SkipUtils.INTENT_QUESTION_ID ,questionId) ;
                        startActivityForResult(toEtIt ,1000);
                    }else if(2 == type){//刷新
                        refreshMyQuestion(questionId) ;
                    }
                }
            }
        }) ;
        mQuestionLv.setAdapter(mQuestionAdapter) ;
    }

    /**
     * 初始化 我的回答
     */
    private void initAnswerInfo(){
        mAnswerAdapter = new AdapterMyQuestionAnswerList(mContext , mAnswerList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MyAnswer question = mAnswerList.get(position) ;
                if(question != null){
                    if(0 == type){
//                        deleteMyAnswer(question.getQuestionID()) ;
                    }
                }
            }
        }) ;
        mAnswerLv.setAdapter(mAnswerAdapter) ;
    }

    /**
     *  切换类型
     *  @param isResumeList is subscribe
     */
    private void changeSendType(boolean isResumeList){
        if(isResumeList == mIsQuestionList){
            return ;
        }

        if(mIsQuestionList){
            mTypeQuestTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeQuestTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeAnswerTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeAnswerTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isResumeList){
            mTypeQuestTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeQuestTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeAnswerTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeAnswerTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsQuestionList = isResumeList ;

        mAnswerRefreshLay.setVisibility(mIsQuestionList ? View.GONE : View.VISIBLE) ;
        mQuestionRefreshLay.setVisibility(mIsQuestionList ? View.VISIBLE : View.GONE) ;

        getDataByType() ;
    }

    /**
     * 获取相关数据
     */
    private void getDataByType(){
        if(mIsQuestionList){
            if(mQuestionList.size() == 0){
                getQuestionList() ;
            }
        }else{
            if(mAnswerList.size() == 0){
                getAnswerList() ;
            }
        }
    }

    /**
     * 获取我的问题列表
     */
    private void getQuestionList(){
        Map<String ,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mQuestionCurPage) ;
        paramMap.put("PageSize" ,"10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_QUESTION_LIST, paramMap
                , new RequestListCallBack<MyQuestion>("getQuestionList" ,mContext ,MyQuestion.class) {
                    @Override
                    public void onSuccessResult(List<MyQuestion> bean) {
                        mDialog.dismiss();

                        if(1 == mQuestionCurPage){
                            mQuestionList.clear();
                        }

                        mQuestionList.addAll(bean) ;
                        mQuestionAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mQuestionIsMore = false ;
                        }else{
                            mQuestionIsMore = true ;
                            mQuestionCurPage++ ;
                        }

                        mQuestionIsLoading = false ;
                        mQuestionRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();

                        mQuestionIsLoading = false ;
                        mQuestionRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        mQuestionIsLoading = false ;
                        mQuestionRefreshLay.finishRefresh() ;
                    }
                });
    }

    /**
     * 获取我的回答
     */
    private void getAnswerList(){
        Map<String ,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mAnswerCurPage) ;
        paramMap.put("PageSize" ,"10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_QUESTION_ANSWER_LIST, paramMap
                , new RequestListCallBack<MyAnswer>("getAnswerList" ,mContext ,MyAnswer.class) {
                    @Override
                    public void onSuccessResult(List<MyAnswer> bean) {
                        if(1 == mAnswerCurPage){
                            mAnswerList.clear();
                        }

                        mAnswerList.addAll(bean) ;
                        mAnswerAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mAnswerIsMore = false ;
                        }else{
                            mAnswerIsMore = true ;
                            mAnswerCurPage++ ;
                        }

                        mAnswerIsLoading = false ;
                        mAnswerRefreshLay.finishRefresh() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mAnswerIsLoading = false ;
                        mAnswerRefreshLay.finishRefresh() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mAnswerIsLoading = false ;
                        mAnswerRefreshLay.finishRefresh() ;
                    }
                });
    }

    /**
     * 删除我的问题
     * @param questionId id
     */
    private void deleteMyQuestion(String questionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,questionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_QUESTION_DELETE, paramMap
                , new RequestListCallBack<String>("deleteMyQuestion" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(List<String> bean) {
                        mDialog.dismiss();

                        mQuestionCurPage = 1 ;
                        getQuestionList();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    /**
     * 刷新我的问题
     * @param questionId id
     */
    private void refreshMyQuestion(String questionId){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,questionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_QUESTION_REFRESH, paramMap
                , new RequestListCallBack<String>("refreshMyQuestion" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(List<String> bean) {
                        mQuestionCurPage = 1 ;
                        getQuestionList();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext ,"网络异常");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode || RESULT_OK == resultCode){
            if(mIsQuestionList){
                mQuestionCurPage = 1 ;
                getQuestionList() ;
            }else{
                mAnswerCurPage = 1 ;
                getAnswerList() ;
            }
        }

    }
}
