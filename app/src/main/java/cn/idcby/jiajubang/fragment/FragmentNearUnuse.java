package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
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

import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedsList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.SpecBaseChildListView;

/**
 * 附近--闲置
 * Created on 2018/4/14.
 */

public class FragmentNearUnuse extends SpecBaseFragment {
    private Activity context ;

    private View mView ;
    private SpecBaseChildListView mLv ;

    private int mNeedsType ; // 1 最新 2 最近

    private TextView mNullTv ;
    private ProgressBar mLoadingPb ;
    private boolean mIsFirstLoad = true ;

    private AdapterNeedsList mAdapter ;
    private List<NeedsList> mNeedsList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    public static FragmentNearUnuse getInstance(int subsId){
        FragmentNearUnuse fs = new FragmentNearUnuse() ;
        fs.mNeedsType = subsId ;
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
            mView = inflater.inflate(R.layout.fragment_near_unuse, container , false) ;

            mLv = mView.findViewById(R.id.fragment_near_unuse_lv);
            mNullTv =  mView.findViewById(R.id.fragment_near_unuse_loading_null_tv);
            mLoadingPb = mView.findViewById(R.id.fragment_near_unuse_loading_null_pb);

            mAdapter = new AdapterNeedsList(context , mNeedsList) ;
            mLv.setAdapter(mAdapter) ;

            mLv.setFocusable(false) ;

            mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                        , int totalItemCount) {

                    if(!mIsLoading && mIsMore
                            && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){
                        getNeedList();
                    }
                }
            });

            if(mIsFirstLoad){
                mIsFirstLoad = false ;

                if(getUserVisibleHint()){
                    getNeedList();
                }
            }
        }

        return mView ;
    }

    /**
     * 获取需求列表
     * 125 85
     * 25 17
     */
    private void getNeedList(){
        if(1 == mCurPage){
            showNullTipsOrLoading(false , true) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(context) ;
        paramMap.put("TypeId" , "" + mNeedsType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(context, Urls.NEEDS_LIST, paramMap
                , new RequestListCallBack<NeedsList>("getNeedList" , context , NeedsList.class) {
            @Override
            public void onSuccessResult(List<NeedsList> bean) {

                if(1 == mCurPage){
                    mNeedsList.clear() ;
                }
                mNeedsList.addAll(bean) ;
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
        mAdapter.notifyDataSetChanged();
        showNullTipsOrLoading(mNeedsList.size() == 0 , false) ;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mView != null){
            if(isVisibleToUser){
                if(mNeedsList != null &&  mNeedsList.size() == 0){
                    getNeedList();
                }
            }else{
                NetUtils.cancelTag("getNeedList" + mNeedsType) ;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){

            NetUtils.cancelTag("getNeedList" + mNeedsType) ;

            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }
    }
}
