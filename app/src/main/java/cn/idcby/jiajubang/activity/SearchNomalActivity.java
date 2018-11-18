package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 *  搜索
 * Created on 2018/3/28.
 *
 * 只做简单的搜索内容输入
 */

public class SearchNomalActivity extends BaseActivity {
    private TextView mRightTv ;
    private EditText mSearchEv ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_search_nomal ;
    }

    @Override
    public void initView() {
        mRightTv = findViewById(R.id.acti_search_nomal_right_tv) ;
        mSearchEv = findViewById(R.id.acti_search_nomal_ev) ;
    }

    @Override
    public void initData() {
        String mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        if(mSearchKey != null && !"".equals(mSearchKey)){
            mSearchEv.setText(mSearchKey) ;
            mSearchEv.setSelection(mSearchKey.length()) ;
        }
    }

    @Override
    public void initListener() {
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;
        if(R.id.acti_search_nomal_right_tv == vId){
            String searchKey = mSearchEv.getText().toString().trim() ;

            Intent reIt = new Intent() ;
            reIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,searchKey) ;
            setResult(RESULT_OK , reIt) ;
            finish() ;
        }
    }

}
