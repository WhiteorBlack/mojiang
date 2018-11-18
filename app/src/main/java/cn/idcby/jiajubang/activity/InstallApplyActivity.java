package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.InstallApplyInfo;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 安装认证
 *
 * 2018-04-26 16:47:43
 * 调整了服务类型的选择，安装认证要求一级分类而且单选
 * ，详细解释参考服务认证和发布服务
 * 2018-05-09 13:46:00
 * 添加2个按钮，保证金介绍（点击跳转到h5）、缴纳保证金
 *
 * 2018-05-24 16:20:53
 * 添加 商家位置 ，选择省市，非必填
 */
public class InstallApplyActivity extends BaseMoreStatusActivity
        implements EasyPermissions.PermissionCallbacks {

    private final int REQUEST_CODE_FOR_TYPE = 323;
    private final int REQUEST_CODE_FOR_PROMISE = 324;
    private final int REQUEST_CODE_FOR_SELECTED_PROVINCE = 1230;
    private final int REQUEST_CODE_FOR_ADDRESS = 12515;
    private final int PRC_PHOTO_PICKER = 300;
    private final int REQUEST_CODE_FOR_CAR_FRONT = 456;
    private final int REQUEST_CODE_FOR_CAR_SIDE = 457;
    private final int REQUEST_CODE_FOR_CAR_ID = 458;
    private final int REQUEST_CODE_FOR_SELECTED_SELLER_ADDRESS = 520;

    private int uploadFlag = 0;
    private LoadingDialog loadingDialog;

    private String serverProvinceId = "";
    private String serverCityId = "";
    private String serverBusiProvinceId = "";
    private String serverBusiCityId = "";

    private String depotAreaId = "";
    private String depotAreaName = "";
    private String depotProvinceId = "";
    private String depotProvinceName = "";
    private String depotCityId = "";
    private String depotCityName = "";
    private String depotAddressDetail = "";

    private String typeId = "";
    private String promiseId = "";
    private String carFrontUrl = "";
    private String carSideUrl = "";
    private String carIdUrl = "";

    private ArrayList<ServerCategory> mServerCategory = new ArrayList<>();
    private ArrayList<WordType> mSerPromiseList ;

    private LinearLayout mLlType;
    private TextView mTvType;
    private LinearLayout mLlServerArea;
    private TextView mTvServerArea;
    private TextView mTvServerSellerArea;
    private LinearLayout mLlServerPromise;
    private TextView mTvServerPromise;
    private EditText mServerDescEv ;
    private EditText mEtCompanyPersonCount;
    private LinearLayout mLlDepotAddress;
    private TextView mTvDepotAddress;
    private EditText mEtDeporArea;
    private ImageView mImgCarFront;
    private ImageView mImgCarSide;
    private ImageView mImgCarId;
    private TextView mTvApply;

    private InstallApplyInfo mApplyInfo ;

    private boolean mIsShowState = false ;//是否是 展示模式，不能编辑
    private String mUserId ;

    @Override
    public void requestData() {
        showSuccessPage();
        requestInstallApplyInfo();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_install;
    }

    @Override
    public String setTitle() {
        return "安装工认证";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        initView();
        inisListenner();
    }

    private void initView() {
        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID) ;
        mIsShowState = !"".equals(StringUtils.convertNull(mUserId));

        mLlType = findViewById(R.id.ll_type);
        mTvType = findViewById(R.id.tv_type);
        mLlServerArea = findViewById(R.id.ll_server_area);
        mTvServerArea = findViewById(R.id.tv_server_area);
        mLlServerPromise = findViewById(R.id.ll_promise);
        mTvServerPromise = findViewById(R.id.tv_promise);
        mEtCompanyPersonCount = findViewById(R.id.et_company_person_count);
        mLlDepotAddress = findViewById(R.id.ll_depot_address);
        mTvDepotAddress = findViewById(R.id.tv_depot_address);
        mEtDeporArea = findViewById(R.id.et_depot_area);
        mImgCarFront = findViewById(R.id.img_car_front);
        mImgCarSide = findViewById(R.id.img_car_side);
        mImgCarId = findViewById(R.id.img_car_id);
        mTvApply = findViewById(R.id.tv_apply);
        mServerDescEv = findViewById(R.id.et_server_desc);

        View typeIv = findViewById(R.id.acti_install_apply_type_iv) ;
        View areaIv = findViewById(R.id.acti_install_apply_area_iv) ;
        View promissIv = findViewById(R.id.acti_install_apply_promiss_iv) ;
        View addressIv = findViewById(R.id.acti_install_apply_address_iv) ;

        View sellserAddressIv = findViewById(R.id.acti_install_apply_seller_address_iv) ;
        View sellserAddressLay = findViewById(R.id.ll_seller_address) ;
        mTvServerSellerArea = findViewById(R.id.tv_seller_address) ;

        View aboutTv = findViewById(R.id.acti_install_apply_help_tv) ;
        View payTv = findViewById(R.id.acti_install_apply_pay_tv) ;
        aboutTv.setOnClickListener(this);
        payTv.setOnClickListener(this);
        sellserAddressLay.setOnClickListener(this);


        if(mIsShowState){
            mEtCompanyPersonCount.setEnabled(false);
            mEtCompanyPersonCount.setHint("");
            mEtDeporArea.setEnabled(false);
            mEtDeporArea.setHint("");

            typeIv.setVisibility(View.GONE);
            areaIv.setVisibility(View.GONE);
            promissIv.setVisibility(View.GONE);
            addressIv.setVisibility(View.GONE);
            sellserAddressIv.setVisibility(View.GONE);
            aboutTv.setVisibility(View.GONE);
            payTv.setVisibility(View.GONE);
            mTvApply.setVisibility(View.GONE);
        }else{
            if(!LoginHelper.isPersonApplyAcross(mContext)){
                DialogUtils.showCustomViewDialog(mContext, "温馨提示" ,StringUtils.getPersonApplyTips(mContext)
                        ,null , "去认证", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                SkipUtils.toApplyActivity(mContext);
                                finish() ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                finish() ;
                            }
                        });
            }
        }
    }

    private void inisListenner() {
        mLlType.setOnClickListener(this);
        mLlServerArea.setOnClickListener(this);
        mLlServerPromise.setOnClickListener(this);
        mLlDepotAddress.setOnClickListener(this);
        mImgCarFront.setOnClickListener(this);
        mImgCarSide.setOnClickListener(this);
        mImgCarId.setOnClickListener(this);
        mTvApply.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.ll_type) {
            if(!mIsShowState){
                Intent toCtIt = new Intent(mContext, ChooseServerCategoryActivity.class);
                toCtIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, true);
                toCtIt.putExtra(SkipUtils.INTENT_SERVER_IS_MORE, true);
                toCtIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO, mServerCategory);
                startActivityForResult(toCtIt, REQUEST_CODE_FOR_TYPE);
            }
        } else if (i == R.id.ll_promise) {
            if(!mIsShowState){
                SkipUtils.toWordTypeMoreActivity(mActivity, SkipUtils.WORD_TYPE_SERVER_PROMISE
                        , mSerPromiseList, REQUEST_CODE_FOR_PROMISE);
            }
        } else if (i == R.id.ll_server_area) {
            if(!mIsShowState){
                goSelectedProvince();
            }
        } else if (i == R.id.ll_depot_address) {
            if(!mIsShowState){
                goSelectedAddress();
            }
        }  else if (i == R.id.ll_seller_address) {
            if(!mIsShowState){
                SelectedProvinceActivity.launch(mActivity ,true,false,REQUEST_CODE_FOR_SELECTED_SELLER_ADDRESS);
            }
        } else if (i == R.id.tv_apply) {
            if(!mIsShowState){
                intallApply();
            }
        } else if (i == R.id.img_car_front) {
            if(!mIsShowState){
                checkPhoto(REQUEST_CODE_FOR_CAR_FRONT, 1);
            }else{
                SkipUtils.toImageShowActivity(mActivity ,carFrontUrl,0);
            }
        } else if (i == R.id.img_car_side) {
            if(!mIsShowState){
                checkPhoto(REQUEST_CODE_FOR_CAR_SIDE, 1);
            }else{
                SkipUtils.toImageShowActivity(mActivity ,carSideUrl,0);
            }
        } else if (i == R.id.img_car_id) {
            if(!mIsShowState){
                checkPhoto(REQUEST_CODE_FOR_CAR_ID, 1);
            }else{
                SkipUtils.toImageShowActivity(mActivity ,carIdUrl,0);
            }
        } else if (i == R.id.acti_install_apply_help_tv) {
            if(!mIsShowState){
                getBondTipsAndToWeb() ;
            }
        } else if (i == R.id.acti_install_apply_pay_tv) {
            if(!mIsShowState){
                MoneyManagerActivity.launch(mContext ,2) ;
            }
        }
    }

    /**
     * 填充数据
     */
    private void updateUI(){
        if(null == mApplyInfo){
            if(mIsShowState){
                DialogUtils.showCustomViewDialog(mContext, "获取认证信息失败，请返回重试"
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
            }
            return ;
        }

        mTvApply.setText(mContext.getResources().getString(R.string.apply_modify_text)) ;

        typeId = mApplyInfo.getTypeIds() ;
        String serviceType = mApplyInfo.getTypeNames() ;

        serverProvinceId = mApplyInfo.getProvinceId() ;
        serverCityId = mApplyInfo.getCityId() ;
        String serverProvinceName = mApplyInfo.getProvinceName() ;
        String serverCityName = mApplyInfo.getCityName() ;
        String serviceArea = serverProvinceName + serverCityName ;

        serverBusiProvinceId = mApplyInfo.getProvinceId() ;
        serverBusiCityId = mApplyInfo.getCityId() ;
        String serverBusiProvinceName = mApplyInfo.getBusinessProvinceName() ;
        String serverBusiCityName = mApplyInfo.getBusinessCityName() ;
        String serverBusiArea = serverBusiProvinceName + serverBusiCityName ;

        promiseId = mApplyInfo.getPromiseIds() ;
        String servicePromise = mApplyInfo.getPromiseNames() ;

        String memberCount = mApplyInfo.getTeamPeopleCount() ;

        depotProvinceId = mApplyInfo.getWarehouseProvinceId() ;
        depotProvinceName = mApplyInfo.getWarehouseProvinceName() ;
        depotCityId = mApplyInfo.getWarehouseCityId() ;
        depotCityName = mApplyInfo.getWarehouseCityName() ;
        depotAreaId = mApplyInfo.getWarehouseCountyId() ;
        depotAreaName = mApplyInfo.getWarehouseCountyName() ;
        depotAddressDetail = mApplyInfo.getWarehouseAddress() ;

        String address = depotProvinceName + depotCityName
                + depotAreaName + depotAddressDetail ;
        String houseArea = mApplyInfo.getWarehouseArea() ;

        carFrontUrl = mApplyInfo.getCarPositiveImg() ;
        carSideUrl = mApplyInfo.getCarSideImg() ;
        carIdUrl = mApplyInfo.getVehicleLicenceDiscImg() ;

        mTvType.setText(serviceType);
        mTvServerArea.setText(serviceArea);
        mTvServerSellerArea.setText(serverBusiArea);
        mTvServerPromise.setText(servicePromise);
        mEtCompanyPersonCount.setText(memberCount);
        mTvDepotAddress.setText(address);
        mEtDeporArea.setText(houseArea);
        mServerDescEv.setText(mApplyInfo.getExplain()) ;
        GlideUtils.loaderAddPic(carFrontUrl ,mImgCarFront);
        GlideUtils.loaderAddPic(carSideUrl ,mImgCarSide);
        GlideUtils.loaderAddPic(carIdUrl ,mImgCarId);
    }

    private void goSelectedAddress() {
        SelectedAddressActivity.launch(mActivity,false ,REQUEST_CODE_FOR_ADDRESS);
    }

    private void goSelectedProvince() {
        SelectedProvinceActivity.launch(mActivity ,true,false,REQUEST_CODE_FOR_SELECTED_PROVINCE);
    }

    private void intallApply() {
        if (TextUtils.isEmpty(typeId)) {
            ToastUtils.showErrorToast(mContext, "请选择服务类型");
            return;
        }

        if (TextUtils.isEmpty(mTvServerArea.getText().toString().trim())) {
            ToastUtils.showErrorToast(mContext, "请选择服务区域");
            return;
        }

        if (TextUtils.isEmpty(promiseId)) {
            ToastUtils.showErrorToast(mContext, "请选择服务承诺");
            return;
        }

        String serverDesc = mServerDescEv.getText().toString().trim();
        if (TextUtils.isEmpty(serverDesc)) {
            ToastUtils.showErrorToast(mContext, "请输入服务描述");
            return;
        }
        String companyPersonCount = mEtCompanyPersonCount.getText().toString().trim();
        if (TextUtils.isEmpty(companyPersonCount)) {
            ToastUtils.showErrorToast(mContext, "请输入团队人数");
            return;
        }

        //如果仓库位置选了，则必须填写仓库面积
        boolean isAre = false ;
        if(!TextUtils.isEmpty(depotProvinceId)
                || !TextUtils.isEmpty(depotCityId)){
            isAre = true ;
        }

        if(isAre && TextUtils.isEmpty(depotProvinceId)){
            ToastUtils.showToast(mContext, "请选择仓库位置");
            return ;
        }

        String deporArea = mEtDeporArea.getText().toString().trim();
        if(isAre && deporArea.equals("")){
            ToastUtils.showToast(mContext, "请输入仓库面积");
            return ;
        }

        //如果车辆照没传就算了，传了必须3张都传
        boolean isPic = false ;
        if(!TextUtils.isEmpty(carFrontUrl)
                || !TextUtils.isEmpty(carSideUrl)
                || !TextUtils.isEmpty(carIdUrl)){
            isPic = true ;
        }
        if (isPic && TextUtils.isEmpty(carFrontUrl)) {
            ToastUtils.showErrorToast(mContext, "请上传车辆正面照");
            return;
        }
        if (isPic && TextUtils.isEmpty(carSideUrl)) {
            ToastUtils.showErrorToast(mContext, "请上传车辆侧面照");
            return;
        }
        if (isPic && TextUtils.isEmpty(carIdUrl)) {
            ToastUtils.showErrorToast(mContext, "请上传车辆证照");
            return;
        }

        boolean isNoChange = true ;
        if(null == mApplyInfo
                || !mApplyInfo.getTypeIds().equals(typeId)
                || !mApplyInfo.getProvinceId().equals(serverProvinceId)
                || !mApplyInfo.getCityId().equals(serverCityId)
                || !mApplyInfo.getBusinessProvinceId().equals(serverBusiProvinceId)
                || !mApplyInfo.getBusinessCityId().equals(serverBusiCityId)
                || !mApplyInfo.getPromiseIds().equals(promiseId)
                || !mApplyInfo.getTeamPeopleCount().equals(companyPersonCount)
                || !mApplyInfo.getWarehouseProvinceId().equals(depotProvinceId)
                || !mApplyInfo.getWarehouseCityId().equals(depotCityId)
                || !mApplyInfo.getWarehouseAddress().equals(depotAddressDetail)
                || !mApplyInfo.getWarehouseArea().equals(deporArea)
                || !mApplyInfo.getCarPositiveImg().equals(carFrontUrl)
                || !mApplyInfo.getCarSideImg().equals(carSideUrl)
                || !mApplyInfo.getExplain().equals(serverDesc)
                || !mApplyInfo.getVehicleLicenceDiscImg().equals(carIdUrl)){
            isNoChange = false ;
        }

        final Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getInstallAuthenticationId() : "");
        para.put("TypeIds", StringUtils.convertNull(typeId));
        para.put("PromiseIds", StringUtils.convertNull(promiseId));
        para.put("ProvinceId", StringUtils.convertNull(serverProvinceId));
        para.put("CityId", StringUtils.convertNull(serverCityId));
        para.put("BusinessProvinceId", StringUtils.convertNull(serverBusiProvinceId));
        para.put("BusinessCityId", StringUtils.convertNull(serverBusiCityId));
        para.put("Explain", serverDesc);
        para.put("WarehouseProvinceId", StringUtils.convertNull(depotProvinceId));
        para.put("WarehouseCityId", StringUtils.convertNull(depotCityId));
        para.put("WarehouseCountyId", StringUtils.convertNull(depotAreaId));
        para.put("TeamPeopleCount", companyPersonCount);
        para.put("WarehouseAddress", StringUtils.convertNull(depotAddressDetail));
        para.put("WarehouseArea", deporArea);
        para.put("VehicleLicenceDiscImg", StringUtils.convertNull(carIdUrl));
        para.put("CarPositiveImg", StringUtils.convertNull(carFrontUrl));
        para.put("CarSideImg", StringUtils.convertNull(carSideUrl));

        if(isNoChange){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示！", "您未做任何修改，是否继续？", null
                    , "继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        submitApplyRequest(para) ;
    }

    private void submitApplyRequest(Map<String,String> para){
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();


        NetUtils.getDataFromServerByPost(mContext, Urls.INSTALL_APPLY, false, para,
                new RequestObjectCallBack<String>("安装认证申请", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        DialogUtils.showCustomViewDialog(mContext,
                                getResources().getString(R.string.apply_submit_success)
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                        finish();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }
    /**
     * 获取安装认证信息
     */
    private void requestInstallApplyInfo() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        String urls ;
        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        if(mIsShowState){
            para.put("code" ,StringUtils.convertNull(mUserId)) ;
            urls = Urls.INSTALL_APPLY_INFO_OTHER ;
        }else{
            urls = Urls.GET_INSTALL_APPLY_INFO ;
        }

        NetUtils.getDataFromServerByPost(mContext, urls, false, para,
                new RequestObjectCallBack<InstallApplyInfo>("获取安装认证信息", mContext,
                        InstallApplyInfo.class) {
                    @Override
                    public void onSuccessResult(InstallApplyInfo bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }

                        if(bean != null){
                            mApplyInfo = bean ;
                        }
                        updateUI() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        updateUI() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        updateUI() ;
                    }
                });
    }

    /**
     * 获取保证金介绍，并且跳转
     */
    private void getBondTipsAndToWeb(){
        loadingDialog.setLoadingText("");
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_REVIEW_BOND) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL_BY_CODE, paramMap
                , new RequestObjectCallBack<NewsDetail>("getBondTips" ,mContext ,NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        loadingDialog.dismiss() ;

                        if(bean != null){
                            String title = bean.getTitle() ;
                            if("".equals(title)){
                                title = "保证金介绍" ;
                            }

                            String contentUrl = bean.getContentH5Url() ;
                            SkipUtils.toShowWebActivity(mContext ,title ,contentUrl);
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss() ;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SELECTED_PROVINCE){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                serverProvinceId = data.getStringExtra("provinceId");
                String serverProvinceName = data.getStringExtra("provinceName");
                serverCityId = data.getStringExtra("cityId");
                String serverCityName = data.getStringExtra("cityName");
                mTvServerArea.setText(serverProvinceName + serverCityName);
            }
        }else if (requestCode == REQUEST_CODE_FOR_SELECTED_SELLER_ADDRESS){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                serverBusiProvinceId = data.getStringExtra("provinceId");
                String serverBusiProvinceName = data.getStringExtra("provinceName");
                serverBusiCityId = data.getStringExtra("cityId");
                String serverBusiCityName = data.getStringExtra("cityName");
                mTvServerSellerArea.setText(serverBusiProvinceName + serverBusiCityName);
            }
        } else if (requestCode == REQUEST_CODE_FOR_TYPE) {
            if(RESULT_OK == resultCode && data != null){
                List<ServerCategory> serverCategory = (ArrayList<ServerCategory>) data
                        .getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);
                mServerCategory.clear();
                typeId = "" ;

                if(serverCategory != null && serverCategory.size() > 0){
                    mServerCategory.addAll(serverCategory) ;

                    String title = "" ;
                    for(ServerCategory category : mServerCategory){
                        title += (category.getCategoryTitle() + ",") ;
                        typeId += (category.getServiceCategoryID() + ",") ;
                    }

                    if(title.length() > 0){
                        title = title.substring(0 ,title.length() - 1) ;
                    }
                    typeId = typeId.substring(0 , typeId.length() - 1) ;
                    mTvType.setText(title);
                }else{
                    mTvType.setText("请选择") ;
                }
            }
        }else if (requestCode == REQUEST_CODE_FOR_PROMISE) {
            if(RESULT_OK == resultCode && data != null){
                mSerPromiseList = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);

                if(mSerPromiseList != null && mSerPromiseList.size() > 0){
                    promiseId = "" ;
                    String name = "" ;
                    for(WordType type : mSerPromiseList){
                        name += (type.getItemName() + ",") ;
                        promiseId += (type.getItemDetailId() + ",") ;
                    }

                    name = name.substring(0 , name.length() - 1) ;
                    promiseId = promiseId.substring(0 , promiseId.length() - 1) ;
                    mTvServerPromise.setText(name);
                }
            }
        }else if (requestCode == REQUEST_CODE_FOR_ADDRESS
                && resultCode == SelectedAddressActivity.RESULT_CODE_FOR_SELECTED_ADDRESS) {
            depotAreaId = data.getStringExtra("areaId");
            depotAreaName = data.getStringExtra("areaName");
            depotProvinceId = data.getStringExtra("provinceId");
            depotProvinceName = data.getStringExtra("provinceName");
            depotCityId = data.getStringExtra("cityId");
            depotCityName = data.getStringExtra("cityName");
            depotAddressDetail = data.getStringExtra("addressDetail");
            mTvDepotAddress.setText(depotProvinceName + depotCityName + depotAreaName
                    + depotAddressDetail);
        } else if (resultCode == RESULT_OK) {
            //图片上传
            if (requestCode == REQUEST_CODE_FOR_CAR_FRONT
                    || requestCode == REQUEST_CODE_FOR_CAR_SIDE
                    || requestCode == REQUEST_CODE_FOR_CAR_ID) {
                ArrayList<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                uploadPhoto(photos.get(0));
            }

        }
    }


    private void checkPhoto(int requestCode, int maxSize) {
        uploadFlag = requestCode;
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(),
                    "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .cameraFileDir(takePhotoDir)
                    // 图片选择张数的最大值
                    .maxChooseCount(maxSize)
                    // 当前已选中的图片路径集合
                    .selectedPhotos(null)
                    // 滚动列表时是否暂停加载图片
                    .pauseOnScroll(false)
                    .build();
            startActivityForResult(photoPickerIntent, requestCode);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    PRC_PHOTO_PICKER, perms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadPhoto(String photoLocalPath) {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        LogUtils.showLog("上传图片的路径>>>" + photoLocalPath);

        Map<String, String> para = ParaUtils.getPara(mContext);
        String base64Image = FileUtil.getUploadImageBase64String(photoLocalPath);
        para.put("Base64Image", base64Image);
        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new RequestObjectCallBack<String>("上传图片", mContext, String.class) {

                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (uploadFlag == REQUEST_CODE_FOR_CAR_FRONT) {
                            GlideUtils.loader(mContext, bean, mImgCarFront);
                            carFrontUrl = bean;
                        } else if (uploadFlag == REQUEST_CODE_FOR_CAR_SIDE) {
                            GlideUtils.loader(mContext, bean, mImgCarSide);
                            carSideUrl = bean;
                        } else if (uploadFlag == REQUEST_CODE_FOR_CAR_ID) {
                            GlideUtils.loader(mContext, bean, mImgCarId);
                            carIdUrl = bean;
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

}
