package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyAddress;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的地址
 * Created on 2018/3/24.
 *
 * 2018-07-17 10:32:29
 * 下单时，选择收货地址也跳转到这个界面
 */

public class MyAddressActivity extends BaseMoreStatusActivity {
    private MaterialRefreshLayout mRefreshLay ;

    private List<ReceiveAddress> mDataList = new ArrayList<>() ;
    private AdapterMyAddress mAdapter ;

    private LoadingDialog mLoadingDialog ;

    private int mCurPosition ;
    private boolean mIsRefresh = false ;

    private String mChooseId ;//选择了的收货地址id
    private ReceiveAddress mChooseAddress = null ;
    private boolean mIsFromOrder = false ;

    public static void launch(Activity activity ,String chooseId ,int requestCode){
        Intent toAiIt = new Intent(activity ,MyAddressActivity.class) ;
        toAiIt.putExtra("isFromOrder" ,true) ;
        toAiIt.putExtra("chooseAddressId" ,chooseId) ;
        activity.startActivityForResult(toAiIt ,requestCode) ;
    }


    @Override
    public void requestData() {
        getAddressList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_address;
    }

    @Override
    public String setTitle() {
        return "常用地址" ;
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        tvRight.setText("添加地址");
        tvRight.setVisibility(View.VISIBLE) ;

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAdIt = new Intent(mContext ,MyAddressEditActivity.class) ;
                MyAddressActivity.this.startActivityForResult(toAdIt , 1001) ;
            }
        });
    }

    @Override
    public void init() {
        mChooseId = getIntent().getStringExtra("chooseAddressId") ;
        mIsFromOrder = getIntent().getBooleanExtra("isFromOrder",mIsFromOrder) ;

        mRefreshLay = findViewById(R.id.acti_my_address_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_my_address_lv) ;

        mAdapter = new AdapterMyAddress(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                mCurPosition = position ;

                if(0 == type){//设为默认
                    setDefaultAddress() ;
                }else if(1 == type){//编辑
                    Intent toAdIt = new Intent(mContext ,MyAddressEditActivity.class) ;
                    toAdIt.putExtra(SkipUtils.INTENT_ADDRESS_ID ,mDataList.get(position).AddressId) ;
                    MyAddressActivity.this.startActivityForResult(toAdIt , 1001) ;
                }else if(2 == type){//删除
                    DialogUtils.showCustomViewDialog(mContext, "删除地址", "删除该地址？", null
                            , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            deleteAddress() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }else if(3 == type){//item
                    if(mIsFromOrder){
                        mChooseAddress = mDataList.get(position) ;
                        mChooseId = mChooseAddress.getAddressId() ;

//                        Intent reIt = new Intent() ;
//                        reIt.putExtra(SkipUtils.INTENT_ADDRESS_INFO ,mDataList.get(position)) ;
//                        setResult(RESULT_OK ,reIt) ;

                        //相关操作在finish方法里
                        finish() ;
                    }
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

                        if(mIsFromOrder && !"".equals(StringUtils.convertNull(mChooseId))){
                            mChooseAddress = null ;
                            for(ReceiveAddress address : mDataList){
                                String id = address.getAddressId() ;
                                if(id.equals(mChooseId)){
                                    mChooseAddress = address ;
                                    break;
                                }
                            }
                        }

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

    /**
     * 删除当前选中的地址
     */
    private void deleteAddress(){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.setLoadingText("正在删除");
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("AddressId" , mDataList.get(mCurPosition).AddressId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteAddress",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        getAddressList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        ToastUtils.showToast(mContext ,"删除失败");
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        ToastUtils.showToast(mContext ,"删除失败");
                    }
                });
    }

    /**
     * 设为默认
     */
    private void setDefaultAddress(){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.setLoadingText("正在提交");
        mLoadingDialog.show() ;

        boolean isDef = 1 == mDataList.get(mCurPosition).IsDefault ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("AddressId" , mDataList.get(mCurPosition).AddressId) ;
        paramMap.put("IsDefault" , isDef ? "0" : "1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_DEFAULT, paramMap
                , new RequestObjectCallBack<String>("setDefaultAddress",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        getAddressList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        ToastUtils.showToast(mContext ,"设置默认失败");
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        ToastUtils.showToast(mContext ,"设置默认失败");
                    }
                });
    }

    @Override
    public void finish() {
        if(mIsFromOrder && !"".equals(mChooseId)){
            Intent reIt = new Intent() ;
            reIt.putExtra(SkipUtils.INTENT_ADDRESS_INFO ,mChooseAddress) ;
            setResult(RESULT_OK ,reIt) ;
        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_OK == resultCode){
            getAddressList() ;
        }

    }
}
