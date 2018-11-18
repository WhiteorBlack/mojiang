package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChooseMyAddress;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 选择地址
 * Created on 2018/4/11.
 *
 * 2018-07-17 10:44:28
 * 选择地址用 我的收货地址 界面了，这个界面暂时不用
 */
@Deprecated
public class ChooseAddressActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;

    private List<ReceiveAddress> mDataList = new ArrayList<>() ;
    private AdapterChooseMyAddress mAdapter ;

    private boolean mIsRefresh = false ;

    private static final int REQUEST_CODE_ADD = 1000 ;


    @Override
    public void requestData() {
        getAddressList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_choose_my_address;
    }

    @Override
    public String setTitle() {
        return "选择地址" ;
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        tvRight.setText("添加地址");
        tvRight.setVisibility(View.VISIBLE) ;

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAdIt = new Intent(mContext ,MyAddressEditActivity.class) ;
                mActivity.startActivityForResult(toAdIt , REQUEST_CODE_ADD) ;
            }
        });
    }

    @Override
    public void init() {
        mRefreshLay = findViewById(R.id.acti_choose_my_address_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_choose_my_address_lv) ;

        mAdapter = new AdapterChooseMyAddress(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    Intent reIt = new Intent() ;
                    reIt.putExtra(SkipUtils.INTENT_ADDRESS_INFO ,mDataList.get(position)) ;
                    setResult(RESULT_OK ,reIt) ;
                    finish() ;
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefresh = true ;

                getAddressList() ;
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }


    /**
     * 地址列表
     */
    private void getAddressList(){
        if(!mIsRefresh){
            showLoadingPage();
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_LIST, false, paramMap
                , new RequestListCallBack<ReceiveAddress>("getAddressList" , mContext ,ReceiveAddress.class) {
                    @Override
                    public void onSuccessResult(List<ReceiveAddress> bean) {
                        if(!mIsRefresh){
                            showSuccessPage() ;
                        }else{
                            mRefreshLay.finishRefresh() ;
                        }

                        mDataList.clear() ;

                        if(bean != null){
                            mDataList.addAll(bean) ;
                        }
                        mAdapter.notifyDataSetChanged() ;

                        mIsRefresh = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(!mIsRefresh){
                            showEmptyPage() ;
                        }else{
                            mRefreshLay.finishRefresh() ;
                        }

                        mIsRefresh = false ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(!mIsRefresh){
                            showEmptyPage() ;
                        }else{
                            mRefreshLay.finishRefresh() ;
                        }

                        mIsRefresh = false ;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_OK == resultCode){
            getAddressList() ;
        }

    }
}
