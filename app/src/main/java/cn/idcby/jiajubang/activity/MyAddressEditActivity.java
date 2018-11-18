package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 添加、编辑我的地址
 * Created on 2018/3/31.
 *
 * 地址数据注意：所有信息不能做隐藏（*代替） ，不然修改功能会异常，导致真实信息没法修改
 */

public class MyAddressEditActivity extends BaseMoreStatusActivity {
    private EditText mNameEv ;
    private EditText mPhoneEv ;
    private EditText mAddressEv ;
    private TextView mAreaTv ;

    private String mAddressId ;
    private ReceiveAddress mAddressInfo ;
    private boolean mIsOnlyAddress = false ;//服务预约时，填写服务地址信息，需要这个界面，但是只要bean，不请求接口

    private String mProvinceId ;
    private String mProvinceName ;
    private String mCityId ;
    private String mCityName ;
    private String mAreaId ;

    private LoadingDialog mLoadingDialog ;

    public static void launch(Activity context ,ReceiveAddress addressInfo ,int requestCode){
        Intent toAeIt = new Intent(context ,MyAddressEditActivity.class) ;
        toAeIt.putExtra(SkipUtils.INTENT_ADDRESS_INFO ,addressInfo) ;
        toAeIt.putExtra("addressIsOnlyInfo" ,true) ;
        context.startActivityForResult(toAeIt ,requestCode) ;
    }


    @Override
    public void requestData() {
        if(mAddressId != null){
            showLoadingPage() ;
            getAddressDetails() ;
        }else{
            showSuccessPage() ;
            if(mAddressInfo != null){
                updateDataInfo(mAddressInfo) ;
            }
        }
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_address_edit ;
    }

    @Override
    public String setTitle() {
        return "添加地址";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mAddressId = getIntent().getStringExtra(SkipUtils.INTENT_ADDRESS_ID);
        mAddressInfo = (ReceiveAddress) getIntent().getSerializableExtra(SkipUtils.INTENT_ADDRESS_INFO);
        mIsOnlyAddress = getIntent().getBooleanExtra("addressIsOnlyInfo" ,mIsOnlyAddress) ;

        mNameEv = findViewById(R.id.acti_my_address_edit_name_ev) ;
        mPhoneEv = findViewById(R.id.acti_my_address_edit_phone_ev) ;
        mAddressEv = findViewById(R.id.acti_my_address_edit_address_ev) ;
        mAreaTv = findViewById(R.id.acti_my_address_edit_area_tv) ;
        View submitView = findViewById(R.id.acti_my_address_edit_sub_tv) ;
        submitView.setOnClickListener(this) ;
        mAreaTv.setOnClickListener(this) ;
        mNameEv.requestFocus() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_address_edit_area_tv == vId){
            SelectedProvinceActivity.launch(mActivity,1003);
        }else if(R.id.acti_my_address_edit_sub_tv == vId){
            submitAddressEdit() ;
        }
    }

    /**
     * 填充内容
     */
    private void updateDataInfo(ReceiveAddress addressInfo){
        showSuccessPage() ;
        mAddressInfo = addressInfo ;

        if(mAddressInfo != null){
            setTitleText(mIsOnlyAddress ? "填写联系方式" : "修改地址");

            String name= mAddressInfo.getContacts() ;
            String phone = mAddressInfo.getAccount() ;
            String address = mAddressInfo.getAddress() ;
            String province = mAddressInfo.getProvinceName() ;
            String city = mAddressInfo.getCityName() ;
            String area = mAddressInfo.getCountyName() ;
            mProvinceId = mAddressInfo.getProvinceId() ;
            mCityId = mAddressInfo.getCityId() ;
            mAreaId = mAddressInfo.getAddressId() ;

            mNameEv.setText(name);
            mNameEv.setSelection(name.length()) ;
            mPhoneEv.setText(phone);
            mAddressEv.setText(address);
            mAreaTv.setText(province + " " + city + " " + area) ;
        }else{
            setTitleText(mIsOnlyAddress ? "填写联系方式" : "添加地址");
        }
    }

    /**
     * 获取详细
     */
    private void getAddressDetails(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("AddressId" ,mAddressId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_DETAILS, paramMap
                , new RequestObjectCallBack<ReceiveAddress>("getAddressDetails" ,mContext , ReceiveAddress.class) {
                    @Override
                    public void onSuccessResult(ReceiveAddress bean) {
                        updateDataInfo(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDataInfo(null) ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDataInfo(null) ;
                    }
                });
    }

    /**
     * 提交 新增 / 修改
     */
    private void submitAddressEdit(){
        String name = mNameEv.getText().toString().trim() ;
        if("".equals(name)){
            mNameEv.requestFocus() ;
            mNameEv.setText("") ;
            ToastUtils.showToast(mContext , "请输入姓名");
            return ;
        }

        String phone = mPhoneEv.getText().toString().trim() ;
        if("".equals(phone) || !MyUtils.isRightPhone(phone)){
            mPhoneEv.requestFocus() ;
            mPhoneEv.setSelection(phone.length()) ;
            ToastUtils.showToast(mContext , "请输入正确的联系方式");
            return ;
        }

        if(null == mProvinceId || null == mAreaId || null == mCityId){
            ToastUtils.showToast(mContext , "请选择地区");
            return ;
        }

        String address = mAddressEv.getText().toString().trim() ;
        if("".equals(address)){
            mAddressEv.requestFocus() ;
            mAddressEv.setText("") ;
            ToastUtils.showToast(mContext , "请输入详细地址");
            return ;
        }

        if(mIsOnlyAddress){//纯选择地址了，不需要调用接口添加
            ReceiveAddress editAddress = new ReceiveAddress(name ,phone
                    ,mProvinceId ,mProvinceName ,mCityId ,mCityName ,address) ;
            Intent reIt = new Intent() ;
            reIt.putExtra(SkipUtils.INTENT_ADDRESS_INFO ,editAddress) ;
            setResult(RESULT_OK ,reIt) ;
            finish() ;
        }else{
            if(null == mLoadingDialog){
                mLoadingDialog = new LoadingDialog(mContext) ;
            }
            mLoadingDialog.setLoadingText("正在提交");
            mLoadingDialog.show() ;

            Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
            paramMap.put("ProvinceId" , StringUtils.convertNull(mProvinceId)) ;
            paramMap.put("CityId" ,StringUtils.convertNull(mCityId)) ;
            paramMap.put("CountyId" ,StringUtils.convertNull(mAreaId)) ;
            paramMap.put("Address" ,address) ;
            paramMap.put("isDefault" ,null == mAddressInfo ? "0" : ("" + mAddressInfo.IsDefault)) ;
            paramMap.put("AddressId" ,null == mAddressInfo ? "" : mAddressInfo.getAddressId()) ;
            paramMap.put("Contacts" ,name) ;
            paramMap.put("ContactsPhone" ,phone) ;

            NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_EDIT, paramMap
                    , new RequestObjectCallBack<String>("editAddress",mContext ,String.class) {
                        @Override
                        public void onSuccessResult(String bean) {
                            if(mLoadingDialog != null){
                                mLoadingDialog.dismiss() ;
                            }

                            ToastUtils.showToast(mContext ,"提交成功");
                            setResult(RESULT_OK) ;
                            finish() ;
                        }
                        @Override
                        public void onErrorResult(String str) {
                            if(mLoadingDialog != null){
                                mLoadingDialog.dismiss() ;
                            }
                            ToastUtils.showToast(mContext ,"提交失败，请重试");
                        }
                        @Override
                        public void onFail(Exception e) {
                            if(mLoadingDialog != null){
                                mLoadingDialog.dismiss() ;
                            }
                            ToastUtils.showToast(mContext ,"提交失败，请重试");
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1003 == requestCode){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                mAreaId = data.getStringExtra("areaId");
                mProvinceName = StringUtils.convertNull(data.getStringExtra("provinceName"));
                mCityName = StringUtils.convertNull(data.getStringExtra("cityName"));
                String areaName = StringUtils.convertNull(data.getStringExtra("areaName"));

                mAreaTv.setText(mProvinceName + mCityName + areaName);
            }
        }

    }
}
