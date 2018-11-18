package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.BitmapToBase64Utils;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.StoreApplyInfo;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 我要开店（店铺认证）
 * 2018-05-30 18:07:11
 * 改：去掉 协议相关
 *
 * 2018-05-31 17:48:49
 * 为跟ios一致，隐藏所有 *
 */
public class OpenStoreActivity extends BaseMoreStatusActivity implements EasyPermissions.PermissionCallbacks {

    private EditText mStoreName;
    private EditText mManagerNumEv;
    private EditText mManagerCardEv;
    private EditText mDetailLocationET;
    private TextView mScopeTV;
    private TextView mSubTV;
    private ImageView mLicenseiv;
    private ImageView mStorePiciv;
    private EditText mDesceEv;


    private String mLicenseImgUrl = "";//营业执照
    private String mStorePicImgUrl = "";//dianzhao店招

    private boolean mIsHasChild = false ;
    private boolean mIsMoreCheck = true ;
    private String mCategoryIds = "";
    private ArrayList<UnusedCategory> mSelectedCategory = new ArrayList<>();

    private RecyclerView imgRecycleView;
    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private int uploadIndex = 0;
    private ArrayList<String> imageUploadList = new ArrayList<>();

    private String mProvinceName = "";
    private String mCityName = "";
    private String mLatitude = "";
    private String mLongitude = "";


    private StoreApplyInfo mApplyInfo;

    private boolean mIsShowState = false ;//是否是 展示模式，不能编辑
    private String mUserId ;

    private static final int REQUEST_CODE_CHOOSE_MAP = 1020 ;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100 ;


    @Override
    public void requestData() {
        showSuccessPage();

        requestInstallApplyInfo();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_open_store;
    }

    @Override
    public String setTitle() {
        return "我要开店";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID) ;
        mIsShowState = !"".equals(StringUtils.convertNull(mUserId));

        loadingDialog = new LoadingDialog(mContext);

        mStoreName = findViewById(R.id.acti_openstore_storename_ev);
        mManagerCardEv = findViewById(R.id.acti_openstore_manager_card_ev);
        mManagerNumEv = findViewById(R.id.acti_openstore_manager_num_ev);
        mDetailLocationET = findViewById(R.id.acti_openstore_location_detail_tv);
        mDesceEv = findViewById(R.id.acti_openstore_desce_tv);
        mScopeTV = findViewById(R.id.acti_openstore_scope_tv);
        mScopeTV.setOnClickListener(this);
        View mLocationIv = findViewById(R.id.acti_store_apply_address_iv);
        View mAddressTipsTv = findViewById(R.id.acti_store_apply_address_tips_tv);
        mLocationIv.setOnClickListener(this);
        mSubTV = findViewById(R.id.acti_openstore_sub_tv);
        mSubTV.setOnClickListener(this);

        mLicenseiv = findViewById(R.id.acti_openstore_license_iv);
        mLicenseiv.setOnClickListener(this);
        mStorePiciv = findViewById(R.id.acti_openstore_storepic_iv);
        mStorePiciv.setOnClickListener(this);
        imgRecycleView = findViewById(R.id.acti_openstore_goodspic_rv);

        if(!mIsShowState){
            mAdapterImageList.add(null);//添加图片的那个图标
        }
        initPhotoContainer();

        View scopeIv = findViewById(R.id.acti_store_apply_scope_iv) ;

        View aboutTv = findViewById(R.id.acti_store_apply_help_tv) ;
        View payTv = findViewById(R.id.acti_store_apply_pay_tv) ;
        aboutTv.setOnClickListener(this);
        payTv.setOnClickListener(this);

        mStoreName.setFocusable(true);
        mStoreName.setFocusableInTouchMode(true);
        mStoreName.requestFocus();
        mStoreName.findFocus();

        if(mIsShowState){
            setTitleText("店铺认证");

            mStoreName.setHint("");
            mStoreName.setEnabled(false);
            mManagerNumEv.setHint("");
            mManagerNumEv.setEnabled(false);
            mManagerCardEv.setHint("");
            mManagerCardEv.setEnabled(false);
            mDetailLocationET.setHint("");
            mDetailLocationET.setEnabled(false);
            mDesceEv.setHint("");
            mDesceEv.setEnabled(false);

            scopeIv.setVisibility(View.GONE);
            mAddressTipsTv.setVisibility(View.GONE);
            mLocationIv.setVisibility(View.GONE);
            payTv.setVisibility(View.GONE);
            aboutTv.setVisibility(View.GONE);
            mSubTV.setVisibility(View.GONE);
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

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext, 15) * 2) / 5;

        mLicenseiv.getLayoutParams().width = itemWidHei ;
        mLicenseiv.getLayoutParams().height = itemWidHei ;
        mStorePiciv.getLayoutParams().width = itemWidHei ;
        mStorePiciv.getLayoutParams().height = itemWidHei ;

        imgRecycleView.setLayoutManager(new GridLayoutManager(mContext, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(mContext, mAdapterImageList
                , itemWidHei, itemWidHei, MAX_PIC_SIZE,!mIsShowState, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {//删除
                    //删除要判断当前位置的图片是否已经上传过了，以此判断 uploadIndex 的加减
                    if (position < uploadIndex) {
                        uploadIndex--;
                    }

                    mAdapterImageList.remove(position);
                    if (localImageList.size() > position) {
                        localImageList.remove(position);
                    }
                    imageUploadList.remove(position);
                    imageSelectorResultAdapter.notifyDataSetChanged();
                } else if (1 == type) {//原图
                    if(mIsShowState){
                        if(localImageList.size() > 0){
                            SkipUtils.toImageShowActivity(mActivity, localImageList, position);
                        }
                    }else{
                        if (position < MAX_PIC_SIZE && position == mAdapterImageList.size() - 1) {
                            //选择图片
                            checkPermission();
                        } else {
                            SkipUtils.toImageShowActivity(mActivity, localImageList, position);
                        }
                    }
                }
            }

            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        imgRecycleView.setAdapter(imageSelectorResultAdapter);
    }

    private final static int REQUEST_CODE_PERMI_IMAGE = 101;

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMI_IMAGE, perms);
        }
    }

    private final static int REQUEST_CODE_FOR_CATEGORY = 1005;

    private int which_img_chosed;

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if (vId == R.id.acti_openstore_scope_tv) {
            if(!mIsShowState){
                ChooseUnuesdCategoryActivity.launch(mActivity ,mIsHasChild ,mIsMoreCheck
                        , mSelectedCategory,REQUEST_CODE_FOR_CATEGORY);
            }

        } else if (vId == R.id.acti_openstore_license_iv) {
            if(mIsShowState){
                if(!"".equals(StringUtils.convertNull(mLicenseImgUrl))){
                    SkipUtils.toImageShowActivity(mActivity ,mLicenseImgUrl,0);
                }
            }else{
                which_img_chosed = 0;
                checkPhoto();
            }
        } else if (vId == R.id.acti_openstore_storepic_iv) {
            if(mIsShowState){
                if(!"".equals(StringUtils.convertNull(mLicenseImgUrl))){
                    SkipUtils.toImageShowActivity(mActivity ,mStorePicImgUrl,0);
                }
            }else{
                which_img_chosed = 1;
                checkPhoto();
            }
        } else if (vId == R.id.acti_store_apply_address_iv) {//地图选点
            if(!mIsShowState){
                String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    toChooseLocationFromMap() ;
                } else {
                    EasyPermissions.requestPermissions(this, "地图选点需要定位权限",
                            REQUEST_CODE_PERMISSION_LOCATION, perms);
                }

            }
        } else if (vId == R.id.acti_openstore_sub_tv) {
            if(!mIsShowState){
                doUpdatatoServicce();
            }
        } else if (vId == R.id.acti_store_apply_help_tv) {
            if(!mIsShowState){
                getBondTipsAndToWeb() ;
            }
        } else if (vId == R.id.acti_store_apply_pay_tv) {
            if(!mIsShowState){
                MoneyManagerActivity.launch(mContext ,2) ;
            }
        }
    }

    /**
     * 跳转到地图选点
     */
    private void toChooseLocationFromMap(){
        Bundle bundle = null ;
        if(!"".equals(StringUtils.convertNull(mLongitude))
                && !"".equals(StringUtils.convertNull(mLatitude))){
            bundle = new Bundle() ;
            bundle.putString("latitude" ,mLatitude) ;
            bundle.putString("longitude" ,mLongitude) ;
        }
        ChooseMapLocationActivity.launch(mActivity ,bundle ,REQUEST_CODE_CHOOSE_MAP) ;
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

    private final int PRC_PHOTO_PICKER_DESC = 301;

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (PRC_PHOTO_PICKER == requestCode) {
            checkPhoto();
        } else if (PRC_PHOTO_PICKER_DESC == requestCode) {
            goCheckPhoto();
        } else if(REQUEST_CODE_PERMISSION_LOCATION == requestCode){
            toChooseLocationFromMap() ;
        }
    }

    private ImageConfig imageConfig;
    private static final int MAX_PIC_SIZE = 3;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
    private ArrayList<String> localImageList = new ArrayList<>();

    private void goCheckPhoto() {
        if (imageConfig == null)
            imageConfig = new ImageConfig.Builder(new GlideImageLoader())
                    .steepToolBarColor(Color.BLACK)
                    .titleBgColor(Color.BLACK)
                    .titleSubmitTextColor(getResources().getColor(R.color.white))
                    .titleTextColor(getResources().getColor(R.color.white))
                    // 开启多选   （默认为多选）
                    .mutiSelectMaxSize(MAX_PIC_SIZE)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();

        imageConfig.setMaxSize(MAX_PIC_SIZE - localImageList.size());
        ImageSelector.open(OpenStoreActivity.this, imageConfig);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "您拒绝了相关权限，会导致部分功能异常!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_FOR_CATEGORY == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                List<UnusedCategory> serverCategory = (ArrayList<UnusedCategory>)
                        data.getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
                mSelectedCategory.clear();
                mCategoryIds = "" ;

                if(serverCategory != null && serverCategory.size() > 0){
                    mSelectedCategory.addAll(serverCategory) ;

                    List<UnusedCategory> titleCategory = new ArrayList<>() ;
                    for(UnusedCategory category : mSelectedCategory){
                        if(mIsHasChild){
                            titleCategory.addAll(category.getSelectedCategory()) ;
                        }else{
                            titleCategory.add(category) ;
                        }
                    }

                    String title = "" ;
                    for(UnusedCategory category : titleCategory){
                        title += (category.getCategoryTitle() + ",") ;
                        mCategoryIds += (category.getCategoryID() + ",") ;
                    }
                    if(title.length() > 0){
                        title = title.substring(0 ,title.length() - 1) ;
                    }
                    if(mCategoryIds.length() > 0){
                        mCategoryIds = mCategoryIds.substring(0 ,mCategoryIds.length() - 1) ;
                    }

                    mScopeTV.setText(title);
                }else{
                    mScopeTV.setText("请选择") ;
                }
            }
        } else if (requestCode == REQUEST_CODE_FOR_COMPANY_LOGO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                uploadLogoPhoto(photos.get(0));
            }
        } else if (REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                localImageList.addAll(pathList);
                int index = mAdapterImageList.size() - 1;
                if (index < 0) {
                    index = 0;
                }
                mAdapterImageList.addAll(index, pathList);

                handler.sendEmptyMessage(UPLOAD_PHOTO);
            }
        }else if(REQUEST_CODE_CHOOSE_MAP == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mLatitude = data.getStringExtra("latitude") ;
                mLongitude = data.getStringExtra("longitude") ;
                mProvinceName = data.getStringExtra("provinceName") ;
                mCityName = data.getStringExtra("cityName") ;
                String mStoreAddress = StringUtils.convertNull(data.getStringExtra("address")) ;

                mDetailLocationET.setText(mStoreAddress) ;
                mDetailLocationET.setSelection(mStoreAddress.length());
            }
        }
    }

    private final static int UPLOAD_PHOTO = 23;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_PHOTO:
                    //上传图片
                    if (imageUploadList != null && localImageList.size() > 0) {
                        new GetImageBase64Task(localImageList.get(uploadIndex), localImageList.size()).execute() ;
                    }
                    break;
            }
        }
    };

    /**
     * 获取图片base64
     */
    private class GetImageBase64Task extends AsyncTask<Void,Void,String> {
        private String imageUrl ;
        private int size ;

        public GetImageBase64Task(String imageUrl, int size) {
            this.imageUrl = imageUrl;
            this.size = size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(null == loadingDialog){
                loadingDialog = new LoadingDialog(mContext) ;
            }
            loadingDialog.setCancelable(false) ;
            loadingDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                    + "/" + localImageList.size() + ")");
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return FileUtil.getUploadImageBase64String(imageUrl);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(null == s){
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();

                ToastUtils.showErrorToast(mContext, "图片上传失败");
            }else{
                requestUploadPhoto(s,size) ;
            }
        }
    }
    private void requestUploadPhoto(String base64Image, final int size) {
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("base64Image", base64Image);

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (loadingDialog != null && loadingDialog.isShowing())
                            loadingDialog.dismiss();
                        ToastUtils.showNetErrorToast(mContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.showLog("上传图片成功json>>>" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("type");
                            if (code == 1) {
                                String url = jsonObject.optString("resultdata");
                                imageUploadList.add(url);
                                if (uploadIndex == size - 1) {
                                    uploadIndex++;

                                    //上传完成
                                    imageSelectorResultAdapter.notifyDataSetChanged();
                                    if (loadingDialog != null && loadingDialog.isShowing())
                                        loadingDialog.dismiss();
                                } else {
                                    uploadIndex++;
                                    loadingDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                                            + "/" + localImageList.size() + ")");
                                    handler.sendEmptyMessage(UPLOAD_PHOTO);
                                }
                            } else {
                                ToastUtils.showErrorToast(mContext, "图片上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private final int REQUEST_CODE_FOR_COMPANY_LOGO = 1100;
    private final int PRC_PHOTO_PICKER = 300;

    private void checkPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(),
                    "BGAPhotoPickerTakePhoto");
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .cameraFileDir(takePhotoDir)
                    // 图片选择张数的最大值
                    .maxChooseCount(1)
                    // 当前已选中的图片路径集合
                    .selectedPhotos(null)
                    // 滚动列表时是否暂停加载图片
                    .pauseOnScroll(false)
                    .build();
            startActivityForResult(photoPickerIntent, REQUEST_CODE_FOR_COMPANY_LOGO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    PRC_PHOTO_PICKER, perms);
        }
    }

    private LoadingDialog loadingDialog;

    private void uploadLogoPhoto(String photoLocalPath) {
        loadingDialog.setLoadingText("正在上传");
        loadingDialog.show();

        LogUtils.showLog("上传图片的路径>>>" + photoLocalPath);
        Map<String, String> para = ParaUtils.getPara(mContext);
        Bitmap bitmapImage = BitmapFactory.decodeFile(photoLocalPath);
        String base64Image = BitmapToBase64Utils.bitmapToBase64(bitmapImage);
        para.put("Base64Image", base64Image);
        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new RequestObjectCallBack<String>("uploadLogoPhoto", mContext, String.class) {

                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (which_img_chosed == 0) {

                            GlideUtils.loader(mContext, bean, mLicenseiv);
                            mLicenseImgUrl = bean;
                        } else if (which_img_chosed == 1) {
                            GlideUtils.loader(mContext, bean, mStorePiciv);
                            mStorePicImgUrl = bean;
                        }

                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        ToastUtils.showToast(mContext, "上传失败");
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        ToastUtils.showToast(mContext, "上传失败");
                    }
                });
    }

    private void doUpdatatoServicce() {
        String storeNamestr = mStoreName.getText().toString().trim();
        if (TextUtils.isEmpty(storeNamestr)) {
            ToastUtils.showToast(mContext, "店铺名不能为空");
            return;
        }
        if (TextUtils.isEmpty(mCategoryIds)) {
            ToastUtils.showToast(mContext, "请选择经营范围");
            return;
        }
        String managerNum = mManagerNumEv.getText().toString().trim() ;
        if(TextUtils.isEmpty(managerNum)) {
            ToastUtils.showToast(mContext, "管理员帐号不能为空");
            return ;
        }
        String managerCard = mManagerCardEv.getText().toString().trim() ;
        if(!MyUtils.isRigghtIDCard(managerCard)) {
            ToastUtils.showToast(mContext, "请输入正确的管理员身份证号");
            return ;
        }

        if (TextUtils.isEmpty(mLongitude) || TextUtils.isEmpty(mLatitude)) {
            ToastUtils.showToast(mContext, "请从地图上选择店铺位置");
            return;
        }

        String storelocdetailtr = mDetailLocationET.getText().toString().trim();
        if (TextUtils.isEmpty(storelocdetailtr)) {
            ToastUtils.showToast(mContext, "请填写店铺地址");
            return;
        }

        String descestr = mDesceEv.getText().toString().trim();
        if (TextUtils.isEmpty(descestr)) {
            ToastUtils.showToast(mContext, "请填写店铺简介");
            return;
        }

        if("".equals(StringUtils.convertNull(mLicenseImgUrl))){
            ToastUtils.showToast(mContext, "请上传营业执照");
            return ;
        }

        String img1str = "";
        String img2str = "";
        String img3str = "";
        if(imageUploadList.size() > 0){
            img1str = imageUploadList.get(0);
        }
        if(imageUploadList.size() > 1){
            img2str = imageUploadList.get(1);
        }
        if(imageUploadList.size() > 2){
            img3str = imageUploadList.get(2);
        }

        boolean isNoChange = true;
        if (null == mApplyInfo
                || !mApplyInfo.getName().equals(storeNamestr)
                || !mApplyInfo.getManageUserIDNumber().equals(managerCard)
                || !mApplyInfo.getManageUserAccount().equals(managerNum)
                || !mApplyInfo.getProvinceName().equals(mProvinceName)
                || !mApplyInfo.getCityName().equals(mCityName)
                || !mApplyInfo.getLatitude().equals(mLatitude)
                || !mApplyInfo.getLongitude().equals(mLongitude)
                || !mApplyInfo.getScopeOperationId().equals(mCategoryIds)
                || !mApplyInfo.getAddress().equals(storelocdetailtr)
                || !mApplyInfo.getIntroduce().equals(descestr)
                || !mApplyInfo.getBusinessLicenseImg().equals(mLicenseImgUrl)
                || !mApplyInfo.getShopImg().equals(mStorePicImgUrl)
                || !mApplyInfo.getImg1().equals(img1str)
                || !mApplyInfo.getImg2().equals(img2str)
                || !mApplyInfo.getImg3().equals(img3str)) {
            isNoChange = false;
        }

        final Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getShopAuthenticationId() : "");
        paramMap.put("ScopeOperationIds", mCategoryIds);
        paramMap.put("ProvinceName", StringUtils.convertNull(mProvinceName));//从地图选点来的，没取到该值（null）
        paramMap.put("CityName", StringUtils.convertNull(mCityName));
        paramMap.put("Latitude", mLatitude);
        paramMap.put("Longitude", mLongitude);
        paramMap.put("ManageUserAccount", managerNum);
        paramMap.put("ManageUserIDNumber", managerCard);
        paramMap.put("Introduce", descestr);
        paramMap.put("Name", storeNamestr);
        paramMap.put("Address", storelocdetailtr);
        paramMap.put("BusinessLicenseImg", mLicenseImgUrl);
        paramMap.put("ShopImg", mStorePicImgUrl);
        paramMap.put("Img1", img1str);
        paramMap.put("Img2", img2str);
        paramMap.put("Img3", img3str);

        if (isNoChange) {
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
                            finish();
                        }
                    });
            return;
        }

        submitApplyRequest(paramMap);
    }

    private void submitApplyRequest(Map<String, String> para) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show();

        NetUtils.getDataFromServerByPost(mContext, Urls.STORE_APPLY, para,
                new RequestObjectCallBack<String>("submitApplyRequest", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                }
        );
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
            para.put("code" , StringUtils.convertNull(mUserId)) ;
            urls = Urls.STORE_APPLY_INFO_OTHER ;
        }else{
            urls = Urls.STORE_APPLY_INFO ;
        }

        NetUtils.getDataFromServerByPost(mContext, urls,para,
                new RequestObjectCallBack<StoreApplyInfo>("获取安装认证信息", mContext,
                        StoreApplyInfo.class) {
                    @Override
                    public void onSuccessResult(StoreApplyInfo bean) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }

                        if (bean != null) {
                            mApplyInfo = bean ;
                        }

                        updateUI();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        updateUI();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        updateUI();
                    }
                });
    }


    /**
     * 填充数据
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

        mSubTV.setText(mContext.getResources().getString(R.string.apply_modify_text));

        mStoreName.setText(mApplyInfo.getName());
        mCategoryIds = mApplyInfo.getScopeOperationId();
        mScopeTV.setText(mApplyInfo.getScopeOperationName());

        mProvinceName = mApplyInfo.getProvinceName();
        mCityName = mApplyInfo.getCityName();
        mLongitude = mApplyInfo.getLongitude();
        mLatitude = mApplyInfo.getLatitude();

        mDetailLocationET.setText(mApplyInfo.getAddress());
        mManagerCardEv.setText(mApplyInfo.getManageUserIDNumber());
        mManagerNumEv.setText(mApplyInfo.getManageUserAccount());

        mDesceEv.setText(mApplyInfo.getIntroduce());

        mLicenseImgUrl = mApplyInfo.getBusinessLicenseImg();
        GlideUtils.loaderAddPic(mLicenseImgUrl, mLicenseiv);

        mStorePicImgUrl = mApplyInfo.getShopImg();
        GlideUtils.loaderAddPic(mStorePicImgUrl, mStorePiciv);

        String img1 = mApplyInfo.getImg1();
        String img2 = mApplyInfo.getImg2();
        String img3 = mApplyInfo.getImg3();

        if (!"".equals(img3)) {
            localImageList.add(0, img3);
            mAdapterImageList.add(0, img3);
            imageUploadList.add(0, img3);
        }
        if (!"".equals(img2)) {
            localImageList.add(0, img2);
            mAdapterImageList.add(0, img2);
            imageUploadList.add(0, img2);
        }
        if (!"".equals(img1)) {
            localImageList.add(0, img1);
            mAdapterImageList.add(0, img1);
            imageUploadList.add(0, img1);
        }

        uploadIndex = localImageList.size() ;

        imageSelectorResultAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
