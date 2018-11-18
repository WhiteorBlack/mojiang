package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.Serializable;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.NewsCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNewsCategory;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;

/**
 * 资讯分类
 * Created on 2018/4/2.
 */

public class NewsCategoryActivity extends BaseActivity {


    public static void launch(Activity mContext ,List<NewsCategory> mList,int position,int requestCode){
        Intent toCiIt = new Intent(mContext ,NewsCategoryActivity.class) ;
        toCiIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO , (Serializable) mList) ;
        toCiIt.putExtra(SkipUtils.INTENT_VP_INDEX ,position) ;
        mContext.startActivityForResult(toCiIt ,requestCode) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_news_category ;
    }

    @Override
    public void initView() {
        overridePendingTransition(R.anim.push_bottom_in, 0);

        View mCloseIv = findViewById(R.id.acti_news_category_close_iv) ;
        mCloseIv.setOnClickListener(this);

        RecyclerView mRv = findViewById(R.id.acti_news_category_rv) ;

        int position = getIntent().getIntExtra(SkipUtils.INTENT_VP_INDEX ,0) ;
        List<NewsCategory> mDataList = (List<NewsCategory>) getIntent().getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);
        if(mDataList != null){
            if(position < mDataList.size()){
                mDataList.get(position).setSelelcted(true) ;
            }

            AdapterNewsCategory mAdapter = new AdapterNewsCategory(mContext, mDataList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    Intent reIt  = new Intent() ;
                    reIt.putExtra(SkipUtils.INTENT_VP_INDEX ,position) ;
                    setResult(RESULT_OK ,reIt) ;
                    finish() ;
                }
            }) ;

            mRv.setLayoutManager(new GridLayoutManager(mContext ,4)) ;
            mRv.addItemDecoration(new RvGridManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext ,10))) ;
            mRv.setAdapter(mAdapter) ;
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_news_category_close_iv == vId){
            finish() ;
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.push_bottom_out);
    }
}
