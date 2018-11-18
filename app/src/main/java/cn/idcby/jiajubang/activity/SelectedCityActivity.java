package cn.idcby.jiajubang.activity;

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

public class SelectedCityActivity extends BaseMoreStatusActivity implements AdapterView.OnItemClickListener {

    private final int REQUEST_CODE_FOR_SELECTED_AREA = 452;
    public static final int RESULT_CODE_FOR_SELECTED_AREA = 45546;
    private String provinceId = "";
    private String provinceName = "";

    private boolean mIsShowArea = true;

    private ListView mListView;
    private AddressAdapter addressAdapter;

    @Override
    public void requestData() {
        requestCity();
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_selected_city;
    }

    @Override
    public String setTitle() {
        return "选择城市";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mIsShowArea = getIntent().getBooleanExtra("isShowArea", true);
        provinceId = getIntent().getStringExtra("provinceId");
        provinceName = getIntent().getStringExtra("provinceName");

        isStope = getIntent().getBooleanExtra(SkipUtils.LOCATION_ISCITYSTOP, false);


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

    private boolean isStope;

    private void requestCity() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", provinceId);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_CITY, false, para,
                new RequestListCallBack<Address>("城市", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        if (isStope) {
                            Address address = new Address("", "不限");
                            bean.add(0, address);
                        }
                        addressAdapter = new AddressAdapter(bean, mContext, mIsShowArea);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = (Address) addressAdapter.getItem(position);
        if (mIsShowArea) {
            Intent intent = new Intent(mContext, SelectedAreaActivity.class);
            intent.putExtra("cityId", address.AreaId);
            intent.putExtra("cityName", address.AreaName);
            intent.putExtra("provinceId", provinceId);
            intent.putExtra("provinceName", provinceName);
            startActivityForResult(intent, REQUEST_CODE_FOR_SELECTED_AREA);
        } else {
            Intent intent = new Intent();

            if (isStope && position == 0) {
                intent.putExtra("cityId", "");
                intent.putExtra("cityName", "");
            } else {

                intent.putExtra("cityId", address.AreaId);
                intent.putExtra("cityName", address.AreaName);
            }
            intent.putExtra("areaId", "");
            intent.putExtra("areaName", "");
            intent.putExtra("provinceId", provinceId);
            intent.putExtra("provinceName", provinceName);
            setResult(RESULT_CODE_FOR_SELECTED_AREA, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SELECTED_AREA
                && resultCode == SelectedAreaActivity.RESULT_CODE_FOR_SELECTED_AREA) {

            setResult(RESULT_CODE_FOR_SELECTED_AREA, data);
            finish();
        }
    }
}
