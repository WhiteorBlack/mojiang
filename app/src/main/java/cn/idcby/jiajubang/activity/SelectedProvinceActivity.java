package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AddressAdapter;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 选择省市区
 *
 * 2018-05-02 15:29:38
 * 功能调整，所有地方不再选择区域，直接精确到  市
 */
public class SelectedProvinceActivity extends BaseMoreStatusActivity implements AdapterView.OnItemClickListener {

    private final int REQUEST_CODE_FOR_SELECTED_CITY = 220;
    public static final int RESULT_CODE_FOR_SELECTED_CITY = 220212;

    private ListView mListView;
    private AddressAdapter addressAdapter;

    private boolean mIsShowCity = true ;
    private boolean mIsShowArea = false ;//默认不再选区域了


    public static void launch(Activity context ,int requestCode){
        launch(context ,true,false,requestCode) ;
    }

    public static void launch(Activity context ,boolean showCity
            , boolean showArea ,int requestCode){
        Intent toPrIt = new Intent(context ,SelectedProvinceActivity.class) ;
        toPrIt.putExtra("isShowCity" ,showCity) ;
        toPrIt.putExtra("isShowArea" ,showArea) ;
        context.startActivityForResult(toPrIt ,requestCode) ;
    }
    public static void launch(Activity context ,boolean showCity
            , boolean showArea ,boolean isstope,int requestCode){
        Intent toPrIt = new Intent(context ,SelectedProvinceActivity.class) ;
        toPrIt.putExtra("isShowCity" ,showCity) ;
        toPrIt.putExtra("isShowArea" ,showArea) ;
        toPrIt.putExtra(SkipUtils.LOCATION_ISCITYSTOP ,isstope) ;
        context.startActivityForResult(toPrIt ,requestCode) ;
    }


    @Override
    public void requestData() {
        requestProvince();
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_selected_province;
    }

    @Override
    public String setTitle() {
        return "选择省份";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mIsShowCity = getIntent().getBooleanExtra("isShowCity" ,mIsShowCity) ;
        mIsShowArea = getIntent().getBooleanExtra("isShowArea" ,mIsShowArea) ;
        isstope = getIntent().getBooleanExtra(SkipUtils.LOCATION_ISCITYSTOP,false);
        initView();
        initListenner();
    }

    private void initView() {
        mListView = findViewById(R.id.list_view);
    }

    private void initListenner() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void requestProvince() {

        Map<String, String> para = ParaUtils.getPara(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_PROVINCE, false, para,
                new RequestListCallBack<Address>("省份", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        addressAdapter = new AddressAdapter(bean, mContext, mIsShowCity);
                        mListView.setAdapter(addressAdapter);
                        showSuccessPage();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        showErrorPage();
                    }

                    @Override
                    public void onFail(Exception e) {
                        showNetErrorPage();
                    }
                });
    }
    private Boolean isstope;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = (Address) addressAdapter.getItem(position);
        if(mIsShowCity){
            Intent intent = new Intent(mContext, SelectedCityActivity.class);
            intent.putExtra("provinceId", address.AreaId);
            intent.putExtra("provinceName", address.AreaName);
            intent.putExtra("isShowArea", mIsShowArea);
            intent.putExtra(SkipUtils.LOCATION_ISCITYSTOP,isstope);
            startActivityForResult(intent, REQUEST_CODE_FOR_SELECTED_CITY);
        }else{
            Intent intent = new Intent();
            intent.putExtra("provinceId", address.AreaId);
            intent.putExtra("provinceName", address.AreaName);
            intent.putExtra("cityId", "");
            intent.putExtra("cityName", "");
            intent.putExtra("areaId", "");
            intent.putExtra("areaName", "");
            setResult(RESULT_CODE_FOR_SELECTED_CITY, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOR_SELECTED_CITY
                && resultCode == SelectedCityActivity.RESULT_CODE_FOR_SELECTED_AREA) {
            setResult(RESULT_CODE_FOR_SELECTED_CITY, data);
            finish();
        }
    }
}
