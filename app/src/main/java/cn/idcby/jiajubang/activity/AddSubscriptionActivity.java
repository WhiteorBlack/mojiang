package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.SubClass;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created by Administrator on 2018-04-18.
 */

public class AddSubscriptionActivity extends BaseMoreStatusActivity {

    private TextView subcategroy;
    private TextView sublocation;
    private TextView subsuretv;

    private List<SubClass> mSubClass = new ArrayList<>();

    private String mProvinceId ="";
    private String mCityId ="";
    private String coulmnsid="";
    private String takeTypeid="";

    private LoadingDialog mDialog ;

    @Override
    public void requestData() {
        showSuccessPage();

    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_create_subscription;
    }

    @Override
    public String setTitle() {
        return "添加订阅";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        subcategroy = findViewById(R.id.acti_create_sub_cat_tv);
        sublocation = findViewById(R.id.acti_create_sub_location_tv);
        subsuretv = findViewById(R.id.acti_create_sub_sure_tv);
        subcategroy.setOnClickListener(this);
        sublocation.setOnClickListener(this);
        subsuretv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if (view.getId() == R.id.acti_create_sub_cat_tv) {//选择分类
            Intent intent = new Intent(mContext , SubClassificationActivity.class) ;
            intent.putExtra(SkipUtils.INTENT_SUBCLASS ,(ArrayList)mSubClass) ;
            startActivityForResult(intent , 1001) ;

        } else if (view.getId() == R.id.acti_create_sub_location_tv) {//选择地区
            SelectedProvinceActivity.launch(mActivity,true,false,true,1003);

        } else if (view.getId() == R.id.acti_create_sub_sure_tv) {//提交按钮

            updatatoservices();

        }
    }

    private void updatatoservices() {
        if (TextUtils.isEmpty(coulmnsid)){
            ToastUtils.showToast(mContext,"请选择分类");
            return;
        }
        if (TextUtils.isEmpty(mProvinceId)){
            ToastUtils.showToast(mContext,"请选择地区");
            return;
        }
        if (TextUtils.isEmpty(mCityId)){
            takeTypeid="1";
        }else {
            takeTypeid="2";
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Coulmns" ,coulmnsid) ;
        paramMap.put("TakeType" ,takeTypeid) ;
        paramMap.put("ProvinceId" ,mProvinceId) ;
        paramMap.put("CityId" ,mCityId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.SUB_ADDUSERTAKECOULMN, paramMap,
                new RequestObjectCallBack<String>("sendSub",mContext,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss() ;

                        ToastUtils.showToast(mContext ,"订阅成功");
                        finish();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss() ;
                        ToastUtils.showToast(mContext ,"订阅失败");
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1001 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                List<SubClass> chooseSubList = (List<SubClass>) data.getSerializableExtra(SkipUtils.INTENT_SUBCLASS);
                mSubClass.clear();
                coulmnsid = "" ;

                if(chooseSubList != null){
                    mSubClass.addAll(chooseSubList) ;
                    String name = "" ;
                    String ids = "" ;
                    for(SubClass sub : mSubClass){
                        name += (sub.getUserTakeCoulmnList() + ",") ;
                        ids += (sub.getCoulmn() + ",") ;
                    }

                    if(name.length() > 1){
                        name = name.substring(0,name.length()-1) ;
                    }
                    if(ids.length() > 1){
                        ids = ids.substring(0,ids.length()-1) ;
                    }

                    subcategroy.setText(name) ;
                    coulmnsid= ids ;
                }
            }
        }else if(1003==requestCode){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                String provinceName = data.getStringExtra("provinceName");
                String cityName = data.getStringExtra("cityName");
                sublocation.setText(provinceName + cityName);
            }
        }
    }
}
