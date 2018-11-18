package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.commonlibrary.view.LineView;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

public class SelectedAddressActivity extends BaseMoreStatusActivity {

    private final int REQUEST_CODE_FOR_SELECTED_PROVINCE = 1230;
    public static final int RESULT_CODE_FOR_SELECTED_ADDRESS = 12304897;

    private LineView lvAddress;
    private EditText mEtAddressDetail;
    private String areaId;
    private String areaName;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;

    private boolean isShowArea = true ;

    public static void launch(Activity activity ,boolean isShowArea,int requestCode){
        Intent toSaIt = new Intent(activity ,SelectedAddressActivity.class) ;
        toSaIt.putExtra("isShowArea" ,isShowArea) ;
        activity.startActivityForResult(toSaIt ,requestCode) ;
    }
    public static void launch(Activity activity,int requestCode){
        launch(activity ,true,requestCode) ;
    }


    @Override
    public void requestData() {
        showSuccessPage();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_selected_address;
    }

    @Override
    public String setTitle() {
        return "选择位置";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        tvRight.setText("完成");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(this);
    }

    @Override
    public void init() {
        initView();
        initListenner();
    }


    private void initView() {
        isShowArea = getIntent().getBooleanExtra("isShowArea",isShowArea) ;

        lvAddress = findViewById(R.id.lv_address);
        mEtAddressDetail = findViewById(R.id.et_address_detail);
    }

    private void initListenner() {
        lvAddress.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.lv_address:
                goSelectedProvince();
                break;
            case R.id.tv_right:
                giveAddress();
                break;
        }
    }

    private void giveAddress() {
        if ("点击选择城市".equals(lvAddress.getRightText())) {
            ToastUtils.showErrorToast(mContext, "请选择城市");
            return;
        }
        String addressDetail = mEtAddressDetail.getText().toString().trim();
        if (TextUtils.isEmpty(addressDetail)) {
            ToastUtils.showErrorToast(mContext, "请填写详细地址");
            return;
        }
        Intent data = new Intent();
        data.putExtra("areaId", areaId);
        data.putExtra("areaName", areaName);
        data.putExtra("provinceId", provinceId);
        data.putExtra("provinceName", provinceName);
        data.putExtra("cityId", cityId);
        data.putExtra("cityName", cityName);
        data.putExtra("addressDetail", addressDetail);
        setResult(RESULT_CODE_FOR_SELECTED_ADDRESS, data);
        finish();
    }

    private void goSelectedProvince() {
        SelectedProvinceActivity.launch(mActivity ,true ,isShowArea ,REQUEST_CODE_FOR_SELECTED_PROVINCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SELECTED_PROVINCE
                && resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
            areaId = StringUtils.convertNull(data.getStringExtra("areaId"));
            areaName = StringUtils.convertNull(data.getStringExtra("areaName"));
            provinceId = StringUtils.convertNull(data.getStringExtra("provinceId"));
            provinceName = StringUtils.convertNull(data.getStringExtra("provinceName"));
            cityId = StringUtils.convertNull(data.getStringExtra("cityId"));
            cityName = StringUtils.convertNull(data.getStringExtra("cityName"));

            lvAddress.setRightText(provinceName + cityName + areaName);
        }
    }
}
