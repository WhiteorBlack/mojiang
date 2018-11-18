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
import cn.idcby.jiajubang.utils.Urls;

public class SelectedAreaActivity extends BaseMoreStatusActivity implements AdapterView.OnItemClickListener {

    public static final int RESULT_CODE_FOR_SELECTED_AREA = 77523;
    private String cityId = "";
    private String cityName = "";
    private String provinceId = "";
    private String provinceName = "";

    private ListView mListView;
    private AddressAdapter addressAdapter;

    @Override
    public void requestData() {
        requestArea();
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_selected_area;
    }

    @Override
    public String setTitle() {
        return "选择地区";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        cityId = getIntent().getStringExtra("cityId");
        cityName = getIntent().getStringExtra("cityName");
        provinceId = getIntent().getStringExtra("provinceId");
        provinceName = getIntent().getStringExtra("provinceName");

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

    private void requestArea() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", cityId);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_AREA, false, para,
                new RequestListCallBack<Address>("地区", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        addressAdapter = new AddressAdapter(bean, mContext);
                        addressAdapter.setIsShowArrow(false);
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
        Intent data = new Intent();
        data.putExtra("areaId", address.getAreaId());
        data.putExtra("areaName", address.getAreaName());
        data.putExtra("provinceId", provinceId);
        data.putExtra("provinceName", provinceName);
        data.putExtra("cityId", cityId);
        data.putExtra("cityName", cityName);
        setResult(RESULT_CODE_FOR_SELECTED_AREA, data);
        finish();
    }
}
