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
import cn.idcby.jiajubang.Bean.NomalH5Bean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 帮助与反馈
 * Created on 2018/5/2.
 */

public class HelperActivity extends BaseMoreStatusActivity {
    private String mCode ;

    private List<NomalH5Bean> mDataList = new ArrayList<>() ;
    private AdapterHelper mAdapter ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    public static void launch(Context context ,String title ,String code){
        Intent toLiIt = new Intent(context ,HelperActivity.class) ;
        toLiIt.putExtra("title" ,title) ;
        toLiIt.putExtra("code" ,code) ;
        context.startActivity(toLiIt) ;
    }

    @Override
    public void requestData() {
        getHelperList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_helper;
    }

    @Override
    public String setTitle() {
        return "帮助与反馈";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        tvRight.setText("反馈");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNextActivity(FeedBackActivity.class);
            }
        });
    }

    @Override
    public void init() {
        mCode = StringUtils.convertNull(getIntent().getStringExtra("code")) ;
        String title = getIntent().getStringExtra("title") ;
        setTitleText(title);

        ListView mLv = findViewById(R.id.acti_helper_lv) ;

        mAdapter = new AdapterHelper(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter);
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && mDataList.size() >= 10 && i + i1 >= i2){
                    getHelperList();
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void getHelperList(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("Keyword" , mCode) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.HELPER_URL, paramMap
                , new RequestListCallBack<NomalH5Bean>("getHelpList" ,mContext ,NomalH5Bean.class) {
                    @Override
                    public void onSuccessResult(List<NomalH5Bean> bean) {
                        showSuccessPage() ;

                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getHelpList") ;

    }
}
