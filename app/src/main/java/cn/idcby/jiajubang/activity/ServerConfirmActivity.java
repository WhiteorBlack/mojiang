package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.Bean.ServerOrderDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 预约服务
 * Created on 2018/4/11.
 *
 * 2018-06-15 13:43:47
 * 改：服务地址信息，改为每次手动填写 ；可以编辑服务订单
 * 2018-07-03 16:17:22
 * 金额不允许修改
 */

public class ServerConfirmActivity extends BaseActivity {
    private TextView mAddressNameTv ;
    private TextView mAddressPhoneTv ;
    private TextView mAddressTv ;
    private View mAddressChooseLay;
    private View mAddressLay;

    private EditText mServerDescEv ;
    private TextView mServerTimeTv ;
    private EditText mServerMoneyEv ;

    private String mServerOrderId;
    private ServerOrderDetails mDetails ;

    private String mServerUserId;
    private int mServerType = SERVER_INSTALL_TYPE;
//    private String mChooseAddressId ;
    private ReceiveAddress mChooseAddress ;

    private String mTimeStr = "";

    private LoadingDialog mLoadDialog ;
    private DialogDatePicker dialogDatePicker;//选择年月日

    private static final int REQUEST_CODE_CHOOSE_ADDRESS = 1000 ;
    private static final int REQUEST_CODE_PAY_MONEY = 1001 ;

    public static final int SERVER_INSTALL_TYPE = 1 ;
    public static final int SERVER_SERVER_TYPE = 2 ;

    public static void launch(Activity context ,String serverUserId ,boolean isInstall,int requestCode){
        Intent toStIt = new Intent(context ,ServerConfirmActivity.class) ;
        toStIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID , serverUserId) ;
        toStIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL , isInstall) ;
        context.startActivityForResult(toStIt , requestCode);
    }

     public static void launch(Context context , String orderId){
        Intent toStIt = new Intent(context ,ServerConfirmActivity.class) ;
        toStIt.putExtra(SkipUtils.INTENT_ORDER_ID , orderId) ;
        context.startActivity(toStIt);
    }
     public static void launch(Activity context , String orderId,int requestCode){
        Intent toStIt = new Intent(context ,ServerConfirmActivity.class) ;
        toStIt.putExtra(SkipUtils.INTENT_ORDER_ID , orderId) ;
        context.startActivityForResult(toStIt,requestCode);
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_server_confirm ;
    }

    @Override
    public void initView() {
        mServerOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;

        mServerUserId = getIntent().getStringExtra(SkipUtils.INTENT_SERVER_USER_ID) ;
        boolean isInstall = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL,false) ;
        mServerType = isInstall ? SERVER_INSTALL_TYPE : SERVER_SERVER_TYPE ;

        mAddressNameTv = findViewById(R.id.acti_server_confirm_address_name_tv) ;
        mAddressPhoneTv = findViewById(R.id.acti_server_confirm_address_phone_tv) ;
        mAddressTv = findViewById(R.id.acti_server_confirm_address_address_tv) ;
        mAddressChooseLay = findViewById(R.id.acti_server_confirm_address_add_lay) ;
        mAddressLay = findViewById(R.id.acti_server_confirm_address_lay) ;
        mServerDescEv = findViewById(R.id.acti_server_confirm_desc_ev) ;
        mServerMoneyEv = findViewById(R.id.acti_server_confirm_money_ev) ;
        mServerTimeTv = findViewById(R.id.acti_server_confirm_time_tv) ;
        TextView mSubTv = findViewById(R.id.acti_server_confirm_submit_tv) ;
        TextView mTitleTv = findViewById(R.id.acti_server_confirm_title_tv) ;

        mAddressChooseLay.setOnClickListener(this);
        mAddressLay.setOnClickListener(this);
        mServerTimeTv.setOnClickListener(this);
        mSubTv.setOnClickListener(this);

        mLoadDialog = new LoadingDialog(mContext) ;

        if(!"".equals(StringUtils.convertNull(mServerOrderId))){
            mTitleTv.setText("修改服务订单");
            mSubTv.setText("修改");
            mServerMoneyEv.setEnabled(false) ;

            mLoadDialog.show() ;
            getOrderDetails() ;
        }

//        getDefaultAddress() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_server_confirm_address_add_lay == vId
                || R.id.acti_server_confirm_address_lay == vId){

            MyAddressEditActivity.launch(mActivity ,mChooseAddress ,REQUEST_CODE_CHOOSE_ADDRESS);

//            Intent toCtIt = new Intent(mContext ,ChooseAddressActivity.class) ;
//            mActivity.startActivityForResult(toCtIt ,REQUEST_CODE_CHOOSE_ADDRESS);
        }else if(R.id.acti_server_confirm_time_tv == vId){
            datePicker("选择日期" ,mServerTimeTv) ;
        }else if(R.id.acti_server_confirm_submit_tv == vId){
            submitServerConfirm() ;
        }
    }

    //日期选择器
    private void datePicker(String str, final TextView view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(this, false);
        dialogDatePicker.setTitle(str);
        dialogDatePicker.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(true);
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化时间
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sDateFormat.format(new java.util.Date());

                if (DateCompareUtils.compareDay(dialogDatePicker.getDate(),currentDate)) {
                    view.setEnabled(true);
                    view.setText(dialogDatePicker.getDate());
                    mTimeStr = dialogDatePicker.getDate();
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "日期不能小于当前时间");
                }
            }
        });

        dialogDatePicker.show();
    }

    /**
     * 填充地址布局
     */
    private void updateAddressDisplay(ReceiveAddress chooseAddress){
        if(chooseAddress != null){
//        if(mChooseAddress != null && !"".equals(StringUtils.convertNull(mChooseAddress.getAddressId()))){
//            mChooseAddressId = mChooseAddress.getAddressId() ;

            mChooseAddress = chooseAddress ;

            mAddressChooseLay.setVisibility(View.GONE);
            mAddressLay.setVisibility(View.VISIBLE);

            String province = chooseAddress.getProvinceName() ;
            String city = chooseAddress.getCityName() ;
            String area = chooseAddress.getCountyName() ;
            String address = chooseAddress.getAddress() ;

            mAddressNameTv.setText(chooseAddress.getContacts());
            mAddressPhoneTv.setText(chooseAddress.getAccount());
            mAddressTv.setText(province + city + area + address);
        }else{
//            mChooseAddressId = null ;
            mChooseAddress = null ;

            mAddressChooseLay.setVisibility(View.VISIBLE);
            mAddressLay.setVisibility(View.GONE);
        }
    }

    private void updateOrderDisplay(){
        mLoadDialog.dismiss();

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "订单信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        String provinceId = mDetails.getProvinceId() ;
        String provinceName = mDetails.getProvinceName() ;
        String cityId = mDetails.getCityId() ;
        String cityName = mDetails.getCityName() ;
        String address = mDetails.getServiceAddress() ;
        String name = mDetails.getContacts() ;
        String phone = mDetails.getContactsPhone() ;

        mServerUserId = mDetails.getServiceUserId() ;
        String money = mDetails.getServiceAmount() ;
        mTimeStr = mDetails.getServiceTime() ;
        mServerType = mDetails.getOrderType() ;

        mChooseAddress = new ReceiveAddress(name ,phone ,provinceId ,provinceName ,cityId ,cityName ,address) ;
        updateAddressDisplay(mChooseAddress) ;

        mServerMoneyEv.setText(money);
        mServerDescEv.setText(mDetails.getServiceintroduce()) ;
        mServerTimeTv.setText(mTimeStr) ;
    }

    /**
     * 获取信息
     */
    private void getOrderDetails(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mServerOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_DETAILS, paramMap
                , new RequestObjectCallBack<ServerOrderDetails>("getDetails" ,mContext ,ServerOrderDetails.class) {
                    @Override
                    public void onSuccessResult(ServerOrderDetails bean) {
                        mDetails = bean ;
                        updateOrderDisplay() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateOrderDisplay() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateOrderDisplay() ;
                    }
                });
    }

    /**
     * 获取默认地址
     */
    private void getDefaultAddress(){
        mLoadDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_DEFAULT_GET, paramMap
                , new RequestObjectCallBack<ReceiveAddress>("getDefaultAddress" ,mContext ,ReceiveAddress.class) {
                    @Override
                    public void onSuccessResult(ReceiveAddress bean) {
                        if(mLoadDialog != null){
                            mLoadDialog.dismiss();
                        }

                        updateAddressDisplay(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadDialog != null){
                            mLoadDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadDialog != null){
                            mLoadDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 提交服务
     */
    private void submitServerConfirm(){
        if(null == mChooseAddress){
            ToastUtils.showToast(mContext ,"请先填写联系方式");
            return ;
        }

//        if("".equals(StringUtils.convertNull(mChooseAddressId))){
//            ToastUtils.showToast(mContext ,"请选择地址");
//            return ;
//        }

        String desc = mServerDescEv.getText().toString().trim() ;
        if("".equals(desc)){
            ToastUtils.showToast(mContext ,"请输入服务介绍");
            mServerDescEv.setText("");
            mServerDescEv.requestFocus() ;
            return ;
        }

        if("".equals(StringUtils.convertNull(mTimeStr))){
            ToastUtils.showToast(mContext ,"请选择服务时间");
            return ;
        }

        final String money = mServerMoneyEv.getText().toString() ;
        if(StringUtils.convertString2Float(money.trim()) <= 0){
            ToastUtils.showToast(mContext ,"请输入正确的金额");
            mServerMoneyEv.requestFocus() ;
            mServerMoneyEv.setSelection(money.length());
            return ;
        }

        mLoadDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
//        paramMap.put("AddressId" ,mChooseAddressId) ;
        paramMap.put("ProvinceId" ,mChooseAddress.getProvinceId()) ;
        paramMap.put("CityId" ,mChooseAddress.getCityId()) ;
        paramMap.put("Address" ,mChooseAddress.getAddress()) ;
        paramMap.put("Contacts" ,mChooseAddress.getContacts()) ;
        paramMap.put("ContactsPhone" ,mChooseAddress.getAccount()) ;
        paramMap.put("ServiceIntroduce" ,desc) ;
        paramMap.put("ServiceTime" ,mTimeStr) ;
        paramMap.put("ServiceAmount" ,money) ;
        paramMap.put("ServiceUserId" ,StringUtils.convertNull(mServerUserId)) ;
        paramMap.put("ServiceOrderId" ,StringUtils.convertNull(mServerOrderId)) ;
        paramMap.put("OrderType" ,"" + mServerType) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CONFIRM, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("submitServerConfirm" ,mContext ,NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mLoadDialog.dismiss() ;

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        if(!"".equals(StringUtils.convertNull(mServerOrderId))){//编辑订单
                            ToastUtils.showToast(mContext , "修改成功") ;
                            setResult(RESULT_OK);
                            finish() ;
                        }else{
                            if(bean != null){
                                String moneys = bean.PayableAmount ;
                                if("".equals(StringUtils.convertNull(moneys))){
                                    moneys = money ;
                                }
                                //跳转到付款界面
                                Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,bean.OrderID) ;
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_ORDER_SERVICE) ;
                                startActivityForResult(toPyIt ,REQUEST_CODE_PAY_MONEY);
                            }else{
                                ToastUtils.showErrorToast(mContext , "预约成功，需要付款") ;
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadDialog.dismiss() ;

                    }
                    @Override
                    public void onFail(Exception e) {
                        mLoadDialog.dismiss() ;

                        ToastUtils.showErrorToast(mContext , "预约失败") ;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CHOOSE_ADDRESS == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ReceiveAddress mChooseAddress = (ReceiveAddress) data.getSerializableExtra(SkipUtils.INTENT_ADDRESS_INFO);
                updateAddressDisplay(mChooseAddress) ;
            }
        }else if(REQUEST_CODE_PAY_MONEY == requestCode){
            if(RESULT_OK == resultCode){
                setResult(RESULT_OK) ;
            }
            finish() ;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDefaultAddress") ;

    }
}
