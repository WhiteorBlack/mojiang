package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 售后--卖家填写退货地址信息
 * Created on 2018/6/13.
 */

public class MyOrderAfterSaleEditReceiveAddressActivity extends BaseActivity {
    private EditText mNameEv ;
    private EditText mPhoneEv ;
    private EditText mAddressEv ;
    private TextView mAreaTv ;

    private String mAfterSaleId ;
    private String mProvinceId ;
    private String mCityId;

    private static final int REQUEST_CODE_CHOOSE_AREA = 1000 ;

    private LoadingDialog mDialog ;

    public static void launch(Activity context ,String afterId ,int requestCode){
        Intent toArIt = new Intent(context ,MyOrderAfterSaleEditReceiveAddressActivity.class) ;
        toArIt.putExtra(SkipUtils.INTENT_ORDER_AFTER_SALE_ID ,afterId) ;
        context.startActivityForResult(toArIt,requestCode) ;
    }
    public static void launch(Activity context ,String afterId){
        Intent toArIt = new Intent(context ,MyOrderAfterSaleEditReceiveAddressActivity.class) ;
        toArIt.putExtra(SkipUtils.INTENT_ORDER_AFTER_SALE_ID ,afterId) ;
        context.startActivity(toArIt) ;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_order_after_sale_receive_address;
    }

    @Override
    public void initView() {
        super.initView();

        mAfterSaleId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_AFTER_SALE_ID) ;

        mNameEv = findViewById(R.id.acti_order_after_sale_rd_name_ev) ;
        mPhoneEv = findViewById(R.id.acti_order_after_sale_rd_phone_ev) ;
        mAddressEv = findViewById(R.id.acti_order_after_sale_rd_address_ev) ;
        mAreaTv = findViewById(R.id.acti_order_after_sale_rd_area_tv) ;
        View mSubmitTv = findViewById(R.id.acti_good_order_after_sale_rd_submit_tv) ;
        mAreaTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_good_order_after_sale_rd_submit_tv == view.getId()){//提交

            agreeAfterSale() ;

        }else if(R.id.acti_order_after_sale_rd_area_tv == view.getId()){

            SelectedProvinceActivity.launch(mActivity ,REQUEST_CODE_CHOOSE_AREA);

        }
    }

    /**
     * 同意申请
     */
    private void agreeAfterSale(){
        String name = mNameEv.getText().toString().trim() ;
        if("".equals(name)){
            ToastUtils.showToast(mContext ,"请输入正确的收货人");
            mNameEv.setText("") ;
            mNameEv.requestFocus() ;
            return ;
        }
        String phone = mPhoneEv.getText().toString().trim() ;
        if("".equals(phone)){
            ToastUtils.showToast(mContext ,"请输入正确的收货人电话");
            mPhoneEv.setText("") ;
            mPhoneEv.requestFocus() ;
            return ;
        }

        if("".equals(StringUtils.convertNull(mProvinceId))
                || "".equals(StringUtils.convertNull(mCityId))){
            ToastUtils.showToast(mContext ,"请选择省市");
            return ;
        }

        String address = mAddressEv.getText().toString().trim() ;
        if("".equals(address)){
            ToastUtils.showToast(mContext ,"请输入正确的详细地址");
            mAddressEv.setText("") ;
            mAddressEv.requestFocus() ;
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(mAfterSaleId)) ;
        paramMap.put("ProvinceID" ,StringUtils.convertNull(mProvinceId)) ;
        paramMap.put("CityID" ,StringUtils.convertNull(mCityId)) ;
        paramMap.put("Address" ,address) ;
        paramMap.put("Reciever" ,name) ;
        paramMap.put("RecieverPhone" ,phone) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_AGREE, paramMap
                , new RequestObjectCallBack<String>("agreeAfterSale",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "提交成功", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });

                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CHOOSE_AREA == requestCode){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                String provinceName = data.getStringExtra("provinceName");
                String cityName = data.getStringExtra("cityName");

                mAreaTv.setText(provinceName + cityName);
            }
        }
    }
}
