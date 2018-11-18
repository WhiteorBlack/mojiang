package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterWordTypeList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/2.
 */

public class GetTypeByCodeActivity extends BaseActivity {
    private View mLoadingLay ;

    private AdapterWordTypeList mAdapter ;
    private List<WordType> mDataList = new ArrayList<>() ;

    private boolean mIsMoreCheck = false ;//是否多选
    private String mTypeCode ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_more_type;
    }

    @Override
    public void initView() {
        mIsMoreCheck = getIntent().getBooleanExtra(SkipUtils.INTENT_WORD_TYPE_MORE,mIsMoreCheck) ;
        mTypeCode = getIntent().getStringExtra(SkipUtils.INTENT_WORD_TYPE_CODE) ;
        String mTitle = getIntent().getStringExtra(SkipUtils.INTENT_TITLE) ;

        mLoadingLay = findViewById(R.id.acti_more_type_list_loading_lay) ;
        ListView mLv = findViewById(R.id.acti_choose_more_type_lv) ;
        TextView mTitleTv = findViewById(R.id.acti_choose_more_type_title_tv) ;
        if(mIsMoreCheck){
            TextView mSubmitTv = findViewById(R.id.acti_choose_more_type_right_tv) ;
            mSubmitTv.setOnClickListener(this);
            mSubmitTv.setVisibility(View.VISIBLE);
        }

        if(!TextUtils.isEmpty(mTitle)){
            mTitleTv.setText(mTitle) ;
        }

        mAdapter = new AdapterWordTypeList(mContext,mIsMoreCheck , mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                WordType wordType = mDataList.get(position) ;
                if(0 == type){//item点击
                    if(mIsMoreCheck){
                        boolean isSelected = wordType.isSelected ;
                        if(isSelected){
                            wordType.isSelected = false ;
                        }else{
                            wordType.isSelected = true ;
                        }
                        mAdapter.notifyDataSetChanged() ;
                    }else{
                        wordType.isSelected = true ;
                        mAdapter.notifyDataSetChanged() ;
                        submitChoose() ;
                    }
                }
            }
        }) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getWordTypeList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_choose_more_type_right_tv == view.getId()){
            submitChoose() ;
        }
    }

    /**
     * 提交选择
     */
    private void submitChoose(){
        ArrayList<WordType> chooseList = new ArrayList<>() ;
        for(WordType service : mDataList){
            if(service.isSelected){
                chooseList.add(service) ;
            }
        }

        Intent reIt = new Intent() ;
        reIt.putExtra(SkipUtils.INTENT_WORD_TYPE_INFO ,chooseList) ;
        setResult(RESULT_OK ,reIt);

        finish() ;
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取福利列表
     */
    private void getWordTypeList(){
        if(TextUtils.isEmpty(mTypeCode)){
            hiddenLoading();
            return ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mTypeCode) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<WordType>("getWordTypeList" , mContext , WordType.class) {
                    @Override
                    public void onSuccessResult(List<WordType> bean) {
                        hiddenLoading();

                        List<String> chooseIds = new ArrayList<>() ;
                        List<WordType> welfareLists = (List<WordType>) getIntent().getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                        if(welfareLists != null){
                            for(WordType welfare : welfareLists){
                                chooseIds.add(welfare.getItemDetailId()) ;
                            }
                        }

                        for(WordType welBean : bean){
                            if(welBean.getItemDetailId() != null
                                    && chooseIds.contains(welBean.getItemDetailId())){
                                welBean.isSelected = true ;
                            }
                            mDataList.add(welBean) ;
                        }
                        mAdapter.notifyDataSetChanged() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        hiddenLoading();
                    }
                    @Override
                    public void onFail(Exception e) {
                        hiddenLoading();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getWordTypeList");

    }

}
