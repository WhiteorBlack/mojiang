package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.jiajubang.Bean.QuestionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.QuestionDetailsActivity;
import cn.idcby.jiajubang.adapter.AdapterQuestionList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.SpecBaseChildListView;

/**
 * Created on 2018/3/28.
 */

public class FragmentQuestion extends SpecBaseFragment {
    private Activity context ;

    private View mView ;
    private SpecBaseChildListView mLv ;

    private int mQuestionType = 1; // 1.新问题 2.零回答 3.悬赏高 4.已解决
    private String mQuestionCateId ;

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterQuestionList mAdapter ;
    private List<QuestionList> mQuestionList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    public static FragmentQuestion getInstance(int subsId,String categoryId){
        FragmentQuestion fs = new FragmentQuestion() ;
        fs.mQuestionType = subsId ;
        fs.mQuestionCateId = categoryId ;
        return fs ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = getActivity() ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_question, container , false) ;

            mLv = mView.findViewById(R.id.fragment_question_lv);
            mNullTv =  mView.findViewById(R.id.fragment_question_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_question_loading_null_pb);

            mAdapter = new AdapterQuestionList(context, mQuestionList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){
                        QuestionList questionList = mQuestionList.get(position) ;
                        if(questionList != null){
                            String questId =questionList.getQuestionId() ;

                            Intent toDtIt = new Intent(context , QuestionDetailsActivity.class) ;
                            toDtIt.putExtra(SkipUtils.INTENT_QUESTION_ID ,questId) ;
                            FragmentQuestion.this.startActivityForResult(toDtIt ,1000);
                        }
                    }
                }
            }) ;
            mLv.setAdapter(mAdapter) ;

            mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                        , int totalItemCount) {

                    if(!mIsLoading && mIsMore
                            && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){
                        getQuestionList();
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    getQuestionList();
                }
            }
        }

        return mView ;
    }


    /**
     * 获取问题列表
     */
    private void getQuestionList(){
        if(1 == mCurPage){
            showNullTipsOrLoading(false , true) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("TypeId" , "" + mQuestionType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("CategoryId" , StringUtils.convertNull(mQuestionCateId)) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(context, Urls.QUESTION_LIST, paramMap
                , new RequestListCallBack<QuestionList>("getQuestionList" + mQuestionType, context , QuestionList.class) {
            @Override
            public void onSuccessResult(List<QuestionList> bean) {

                if(1 == mCurPage){
                    mQuestionList.clear() ;
                }
                mQuestionList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if(bean.size() == 0){
                    mIsMore = false ;
                }else{
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

    /**
     * 完成请求
     */
    private void finishRequest(){
        mIsLoading = false ;
        showNullTipsOrLoading(mQuestionList.size() == 0 , false) ;
    }

    /**
     * 显示或隐藏null提示和loading提示
     */
    private void showNullTipsOrLoading(boolean isNull , boolean isLoading){
        if(isNull){
            if(mNullTv.getVisibility() != View.VISIBLE){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else{
            if(mNullTv.getVisibility() != View.GONE){
                mNullTv.setVisibility(View.GONE);
            }
        }

        if(isLoading){
            if(mLoadingPb.getVisibility() != View.VISIBLE){
                mLoadingPb.setVisibility(View.VISIBLE);
            }
        }else{
            if(mLoadingPb.getVisibility() != View.GONE){
                mLoadingPb.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 当前列表中的第一个item是否到了最上面
     * @return true 到了最上面，false还没有到最上面。默认 true ，是为了让parent能滑动
     */
    @Override
    public boolean isListViewFirstItemOnTop(){
        if(null == mAdapter || mAdapter.isEmpty() || null == mLv){
            return true ;
        }

        if(mLv.getFirstVisiblePosition() == 0){//第一个已经显示出来了
            //getChildCount是当前屏幕可见范围内的count
            int mostTop = (mLv.getChildCount() > 0) ? mLv.getChildAt(0)
                    .getTop() : 0;
            if (mostTop >= 0) {
                return true;
            }
        }

        return false;
    }

    public void updateCurView(String categoryId){
        mQuestionCateId = categoryId ;

        mQuestionList.clear();
        mAdapter.notifyDataSetChanged();
        mCurPage = 1 ;

        if(getUserVisibleHint()){
            getQuestionList();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mQuestionList != null &&  mQuestionList.size() == 0){
                    getQuestionList();
                }
            }else{
                NetUtils.cancelTag("getQuestionList" + mQuestionType) ;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(Activity.RESULT_OK == resultCode){
                mCurPage = 1 ;
                getQuestionList() ;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getQuestionList" + mQuestionType) ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }
    }
}
