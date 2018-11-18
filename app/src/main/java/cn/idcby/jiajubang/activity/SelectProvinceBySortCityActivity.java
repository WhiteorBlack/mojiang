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
import cn.idcby.jiajubang.adapter.AdapterSortProvinceCity;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 选择选中省下的市
 */
public class SelectProvinceBySortCityActivity extends BaseMoreStatusActivity implements AdapterView.OnItemClickListener {
    private String provinceId = "";
    private String provinceName = "";

    private ListView mListView;
    private AdapterSortProvinceCity addressAdapter;

    public static void launch(Activity context,String proId ,String proName ,int requestCode){
        Intent toScIt = new Intent(context ,SelectProvinceBySortCityActivity.class) ;
        toScIt.putExtra("provinceId" ,proId) ;
        toScIt.putExtra("provinceName" ,proName) ;
        context.startActivityForResult(toScIt , requestCode) ;
    }

    @Override
    public void requestData() {
        provinceId = getIntent().getStringExtra("provinceId");
        provinceName = getIntent().getStringExtra("provinceName");
        requestCity();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_select_province_by_sort_city;
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
        initView();
        initListenner();
    }

    private void initView() {
        mListView = findViewById(R.id.acti_select_sort_province_city_lv);
    }

    private void initListenner() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {

    }


    private void requestCity() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", provinceId);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_CITY, false, para,
                new RequestListCallBack<Address>("城市", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        if(bean.size() > 0){
                            bean.add(0,new Address(provinceId ,"全省")) ;
                        }

                        addressAdapter = new AdapterSortProvinceCity(bean, mContext);
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

        Intent intent = new Intent();
        Address address = (Address) addressAdapter.getItem(position);

        if(0 == position){//第一是全省
            intent.putExtra(SkipUtils.LOCATION_TYPE, SkipUtils.LOCATION_TYPE_PROVINCE);
            intent.putExtra(SkipUtils.LOCATION_CONTENT_NAME, provinceName);
        }else{
            intent.putExtra(SkipUtils.LOCATION_TYPE, SkipUtils.LOCATION_TYPE_CITY);
            intent.putExtra(SkipUtils.LOCATION_CONTENT_NAME, address.AreaName);
        }
        intent.putExtra(SkipUtils.LOCATION_CONTENT_ID, address.AreaId);

        setResult(RESULT_OK ,intent) ;
        finish() ;
    }
}
