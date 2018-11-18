package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.Bean.WelfareNomalBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterWelfareList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/2.
 */

public class ChooseWelfareActivity extends BaseActivity {
    private View mLoadingLay ;
    private ListView mLv ;

    private AdapterWelfareList mAdapter ;
    private List<WelfareList> mWelfList = new ArrayList<>() ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_welfare;
    }

    @Override
    public void initView() {
        mLoadingLay = findViewById(R.id.acti_welfare_list_loading_lay) ;
        mLv = findViewById(R.id.acti_choose_welfare_lv) ;
        TextView mSubmitTv = findViewById(R.id.acti_choose_welfare_right_tv) ;
        mSubmitTv.setOnClickListener(this);

        mAdapter = new AdapterWelfareList(mContext , mWelfList) ;
        mLv.setAdapter(mAdapter) ;
    }

    @Override
    public void initData() {
        getWelfareList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_choose_welfare_right_tv == view.getId()){
            submitChoose() ;
        }
    }

    /**
     * 提交选择
     */
    private void submitChoose(){
        ArrayList<WelfareList> chooseList = new ArrayList<>() ;
        for(WelfareList welfare : mWelfList){
            if(welfare.isSelected){
                chooseList.add(welfare) ;
            }
        }

        if(chooseList.size() > 0){
            Intent reIt = new Intent() ;
            reIt.putExtra(SkipUtils.INTENT_WELFARE_INFO ,chooseList) ;
            setResult(RESULT_OK ,reIt);
        }

        finish() ;
    }

    private void hiddenLoading(){
        mLoadingLay.setVisibility(View.GONE);
    }

    /**
     * 获取福利列表
     */
    private void getWelfareList(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , "WorkWelfare") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<WelfareNomalBean>("getWelfareList" , mContext , WelfareNomalBean.class) {
                    @Override
                    public void onSuccessResult(List<WelfareNomalBean> bean) {
                        hiddenLoading();

                        List<String> chooseIds = new ArrayList<>() ;
                        List<WelfareList> welfareLists = (List<WelfareList>) getIntent().getSerializableExtra(SkipUtils.INTENT_WELFARE_INFO);
                        if(welfareLists != null){
                            for(WelfareList welfare : welfareLists){
                                chooseIds.add(welfare.getWelfare()) ;
                            }
                        }

                        for(WelfareNomalBean welBean : bean){
                            WelfareList welfare = new WelfareList(welBean) ;
                            if(chooseIds.contains(welfare.getWelfare())){
                                welfare.isSelected = true ;
                            }
                            mWelfList.add(welfare) ;
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

        NetUtils.cancelTag("getWelfareList");

    }

}
