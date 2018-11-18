package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.ServerApplyInfo;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 行业服务认证
 *
 * 2018-05-24 16:20:53
 * 添加 商家位置 ，选择省市，非必填
 */
public class ServerApplyActivity extends BaseMoreStatusActivity {
    private final int REQUEST_CODE_FOR_TYPE = 323;
    private final int REQUEST_CODE_FOR_PROMISE = 324;
    private final int REQUEST_CODE_FOR_SELECTED_PROVINCE = 1230;
    private final int REQUEST_CODE_FOR_SELECTED_SELLER_AREA = 1231;

    private LoadingDialog loadingDialog;

    private String provinceId = "";
    private String cityId = "";
    private String provinceBusiId = "";
    private String cityBusiId = "";

    private String typeId = "";
    private String promiseId = "";

    private ArrayList<ServerCategory> mServerCategory = new ArrayList<>();
    private ArrayList<WordType> mSerPromiseList ;

    private LinearLayout mLlType;
    private TextView mTvType;
    private LinearLayout mLlArea;
    private TextView mTvArea;
    private LinearLayout mLlPromise;
    private TextView mTvPromise;
    private EditText mEtServerDesc;
    private TextView mTvApply;
    private TextView mTvSellerArea;

    private ServerApplyInfo mApplyInfo ;

    private boolean mIsShowState = false ;//是否是 展示模式，不能编辑
    private String mUserId ;

    @Override
    public void requestData() {
        showSuccessPage();
        requestServerApplyInfo();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_server_apply;
    }

    @Override
    public String setTitle() {
        return "服务认证";
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
        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID) ;
        mIsShowState = !"".equals(StringUtils.convertNull(mUserId));

        mLlType = findViewById(R.id.ll_type);
        mTvType = findViewById(R.id.tv_type);
        mLlArea = findViewById(R.id.ll_address);
        mTvArea = findViewById(R.id.tv_address);
        mTvSellerArea = findViewById(R.id.tv_seller_address);
        mLlPromise = findViewById(R.id.ll_promise);
        mTvPromise = findViewById(R.id.tv_promise);
        mEtServerDesc = findViewById(R.id.et_server_desc);
        mTvApply = findViewById(R.id.tv_apply);

        View typeIv = findViewById(R.id.acti_server_apply_type_iv) ;
        View areaIv = findViewById(R.id.acti_server_apply_area_iv) ;
        View promissIv = findViewById(R.id.acti_server_apply_promiss_iv) ;

        View sellerAreaLay = findViewById(R.id.ll_seller_address) ;
        View sellerAreaIv = findViewById(R.id.acti_server_apply_seller_address_iv) ;

        View aboutTv = findViewById(R.id.acti_server_apply_help_tv) ;
        View payTv = findViewById(R.id.acti_server_apply_pay_tv) ;
        aboutTv.setOnClickListener(this);
        payTv.setOnClickListener(this);
        sellerAreaLay.setOnClickListener(this);

        if(mIsShowState){
            mEtServerDesc.setEnabled(false);
            mEtServerDesc.setHint("");

            typeIv.setVisibility(View.GONE);
            areaIv.setVisibility(View.GONE);
            promissIv.setVisibility(View.GONE);
            sellerAreaIv.setVisibility(View.GONE);
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

    private void initListenner() {
        mLlType.setOnClickListener(this);
        mLlArea.setOnClickListener(this);
        mLlPromise.setOnClickListener(this);
        mTvApply.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if(mIsShowState){
            return ;
        }

        int i = view.getId();
        if (i == R.id.ll_type) {
            Intent toCtIt = new Intent(mContext, ChooseServerCategoryActivity.class);
            toCtIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, false);
            toCtIt.putExtra(SkipUtils.INTENT_SERVER_IS_MORE, true);
            toCtIt.putExtra(SkipUtils.INTENT_CATEGORY_INFO, mServerCategory);
            startActivityForResult(toCtIt, REQUEST_CODE_FOR_TYPE);


        } else if (i == R.id.ll_address) {

            SelectedProvinceActivity.launch(mActivity, true, false, REQUEST_CODE_FOR_SELECTED_PROVINCE);

        } else if (i == R.id.ll_seller_address) {

            SelectedProvinceActivity.launch(mActivity, true, false, REQUEST_CODE_FOR_SELECTED_SELLER_AREA);

        } else if (i == R.id.ll_promise) {
            SkipUtils.toWordTypeMoreActivity(mActivity, SkipUtils.WORD_TYPE_SERVER_PROMISE
                    , mSerPromiseList, REQUEST_CODE_FOR_PROMISE);

        } else if (i == R.id.tv_apply) {
            apply();

        } else if (i == R.id.acti_server_apply_help_tv) {
            getBondTipsAndToWeb() ;
        } else if (i == R.id.acti_server_apply_pay_tv) {
            MoneyManagerActivity.launch(mContext ,2) ;
        }
    }

    private void apply() {
        if (TextUtils.isEmpty(typeId)) {
            ToastUtils.showErrorToast(mContext, "请选择服务类型");
            return;
        }

        if (TextUtils.isEmpty(provinceId)) {
            ToastUtils.showErrorToast(mContext, "请选择服务区域");
            return;
        }

        if (TextUtils.isEmpty(promiseId)) {
            ToastUtils.showErrorToast(mContext, "请选择服务承诺");
            return;
        }
        String serverDesc = mEtServerDesc.getText().toString().trim();
        if (TextUtils.isEmpty(serverDesc)) {
            ToastUtils.showErrorToast(mContext, "请填写服务说明");
            return;
        }

        boolean isNoChange = true ;
        if(null == mApplyInfo
                || !mApplyInfo.getTypeIds().equals(typeId)
                || !mApplyInfo.getProvinceId().equals(provinceId)
                || !mApplyInfo.getCityId().equals(cityId)
                || !mApplyInfo.getBusinessProvinceId().equals(provinceBusiId)
                || !mApplyInfo.getBusinessCityId().equals(cityBusiId)
                || !mApplyInfo.getPromiseIds().equals(promiseId)
                || !mApplyInfo.getExplain().equals(serverDesc)){
            isNoChange = false ;
        }

        final Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getServiceAuthenticationId() : "");
        para.put("TypeIds", typeId);
        para.put("PromiseIds", promiseId);
        para.put("ProvinceId", provinceId);
        para.put("CityId", cityId);
        para.put("BusinessProvinceId", provinceBusiId);
        para.put("BusinessCityId", cityBusiId);
        para.put("Explain", serverDesc);

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

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_APPLY, false, para,
                new RequestObjectCallBack<String>("服务认证申请", mContext, String.class) {
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
     * 获取服务认证信息
     */
    private void requestServerApplyInfo() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        String urls ;
        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        if(mIsShowState){
            para.put("code" , StringUtils.convertNull(mUserId)) ;
            urls = Urls.SERVER_APPLY_INFO_OTHER ;
        }else{
            urls = Urls.GET_SERVER_APPLY_INFO ;
        }

        NetUtils.getDataFromServerByPost(mContext, urls, para,
                new RequestObjectCallBack<ServerApplyInfo>("获取行业服务认证信息", mContext,
                        ServerApplyInfo.class) {
                    @Override
                    public void onSuccessResult(ServerApplyInfo bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (bean != null){
                            mApplyInfo = bean ;
                        }
                        updateUI();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                        updateUI();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                        updateUI();
                    }
                });
    }

    /**
     * 填充内容
     */
    private void updateUI() {
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
        promiseId = mApplyInfo.getPromiseIds() ;
        provinceId = mApplyInfo.getProvinceId() ;
        cityId = mApplyInfo.getCityId() ;
        provinceBusiId = mApplyInfo.getBusinessProvinceId() ;
        cityBusiId = mApplyInfo.getBusinessCityId() ;

        mTvType.setText(mApplyInfo.getTypeNames());
        mTvArea.setText(mApplyInfo.getProvinceName()+mApplyInfo.getCityName());
        mTvSellerArea.setText(mApplyInfo.getBusinessProvinceName()+mApplyInfo.getBusinessCityName());
        mTvPromise.setText(mApplyInfo.getPromiseNames());
        mEtServerDesc.setText(mApplyInfo.getExplain());
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
        if (requestCode == REQUEST_CODE_FOR_SELECTED_PROVINCE) {
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY){
                provinceId = data.getStringExtra("provinceId");
                String provinceName = data.getStringExtra("provinceName");
                cityId = data.getStringExtra("cityId");
                String cityName = data.getStringExtra("cityName");
                mTvArea.setText(provinceName + cityName);
            }
        }else if (requestCode == REQUEST_CODE_FOR_SELECTED_SELLER_AREA) {
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY){
                provinceBusiId = data.getStringExtra("provinceId");
                String provinceName = data.getStringExtra("provinceName");
                cityBusiId = data.getStringExtra("cityId");
                String cityName = data.getStringExtra("cityName");
                mTvSellerArea.setText(provinceName + cityName);
            }
        } else if (requestCode == REQUEST_CODE_FOR_TYPE) {
            if(RESULT_OK == resultCode && data != null){
                List<ServerCategory> serverCategory = (ArrayList<ServerCategory>) data.getSerializableExtra(SkipUtils.INTENT_CATEGORY_INFO);
                mServerCategory.clear();
                typeId = "" ;

                if(serverCategory != null && serverCategory.size() > 0){
                    mServerCategory.addAll(serverCategory) ;

                    List<ServerCategory> titleCategory = new ArrayList<>() ;
                    for(ServerCategory category : mServerCategory){
                        titleCategory.addAll(category.getSelectedCategory()) ;
                    }

                    String title = "" ;
                    for(ServerCategory category : titleCategory){
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
                    mTvPromise.setText(name);
                }
            }
        }
    }
}