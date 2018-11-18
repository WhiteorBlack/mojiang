package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.FactoryApplyInfo;
import cn.idcby.jiajubang.Bean.ImageThumb;
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
 * 工厂认证
 *
 * 2018-05-03 17:13:31
 * 经营范围替换掉所属行业以及所属类型，与店铺认证保持一致
 */
public class FactoryApplyActivity extends BaseMoreStatusActivity
        implements EasyPermissions.PermissionCallbacks{

    private final int REQUEST_CODE_FOR_COMPANY_TYPE = 124;
    private final int REQUEST_CODE_FOR_ADDRESS = 125;
    private final int PRC_PHOTO_PICKER = 300;
    private final int REQUEST_CODE_FOR_LICENCE = 126;

    private String licenceUrl = "";
    private String mThumbIds ;

    private boolean mIsHasChild = false ;
    private boolean mIsMoreCheck = true ;
    private String mCategoryIds = "";
    private ArrayList<UnusedCategory> mSelectedCategory = new ArrayList<>();

    private String provinceId = "";
    private String provinceName = "";
    private String cityId = "";
    private String cityName = "";
    //区域相关暂时去掉
    private String areaId = "";
    private String areaName = "";
    private String addressDetail = "";

    private EditText mEtFactoryName;
    private LinearLayout mLlType;
    private TextView mTvType;
    private LinearLayout mLlAddress;
    private TextView mTvAddress;
    private EditText mEtFactoryDesc;
    private ImageView mImgLicence;
    private TextView mTvApply;
    private RecyclerView mPicLay ;

    private FactoryApplyInfo mApplyInfo ;

    private boolean mIsShowState = false ;
    private String mUserId ;


    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 5 ;

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

    @Override
    public void requestData() {
        showSuccessPage();
        requestFactoryApplyInfo();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_factory;
    }

    @Override
    public String setTitle() {
        return "工厂认证";
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
        if(!"".equals(StringUtils.convertNull(mUserId))){
            mIsShowState = true ;
        }

        loadingDialog = new LoadingDialog(mContext) ;

        mEtFactoryName = findViewById(R.id.et_factory_name);
        mLlType = findViewById(R.id.ll_type);
        mTvType = findViewById(R.id.tv_type);
        mLlAddress = findViewById(R.id.ll_address);
        mTvAddress = findViewById(R.id.tv_address);
        mEtFactoryDesc = findViewById(R.id.et_factory_desc);
        View mTypeIv = findViewById(R.id.acti_factory_apply_type_iv) ;
        View mAddressIv = findViewById(R.id.acti_factory_apply_address_iv) ;
        mImgLicence = findViewById(R.id.img_licence);
        mTvApply = findViewById(R.id.tv_apply);
        mPicLay = findViewById(R.id.acti_factory_apply_pic_rv);

        if(mIsShowState){
            mEtFactoryName.setEnabled(false);
            mEtFactoryDesc.setEnabled(false);

            mTypeIv.setVisibility(View.GONE);
            mAddressIv.setVisibility(View.GONE);
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

        initPhotoContainer() ;
    }

    private void initListenner() {
        mLlType.setOnClickListener(this);
        mLlAddress.setOnClickListener(this);
        mImgLicence.setOnClickListener(this);
        mTvApply.setOnClickListener(this);
    }


    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.ll_type:

                if(!mIsShowState){
                    ChooseUnuesdCategoryActivity.launch(mActivity ,mIsHasChild ,mIsMoreCheck
                            , mSelectedCategory,REQUEST_CODE_FOR_COMPANY_TYPE);
                }

                break;
            case R.id.ll_address:
                if(!mIsShowState){
                    goSelectedAddress();
                }
                break;
            case R.id.img_licence:
                if(mIsShowState){
                    if(!"".equals(licenceUrl)){
                        SkipUtils.toImageShowActivity(mActivity ,licenceUrl ,0) ;
                    }
                }else{
                    checkPhoto(REQUEST_CODE_FOR_LICENCE, 1);
                }
                break;
            case R.id.tv_apply:
                requstFactoryApply();
                break;
        }
    }


    /**
     * 初始化图片选择
     */
    private void initPhotoContainer() {
        if(!mIsShowState){
            mAdapterImageList.add(null) ;//添加图片的那个图标
        }

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        mImgLicence.getLayoutParams().width = itemWidHei ;
        mImgLicence.getLayoutParams().height = itemWidHei ;

        mPicLay.setLayoutManager(new GridLayoutManager(this, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei,MAX_IMAGE_COUNT ,!mIsShowState, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){//删除
                    //删除要判断当前位置的图片是否已经上传过了，以此判断 uploadIndex 的加减
                    if(position < uploadIndex){
                        uploadIndex -- ;
                    }

                    mAdapterImageList.remove(position) ;
                    localImageList.remove(position) ;
                    imageUploadList.remove(position) ;
                    imageSelectorResultAdapter.notifyDataSetChanged() ;
                }else if(1 == type){//原图
                    if(!mIsShowState){
                        if(position < MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1){
                            //选择图片
                            checkPermission();
                        }else{
                            SkipUtils.toImageShowActivity(mActivity , localImageList ,position);
                        }
                    }else{
                        if(localImageList.size() > 0){
                            SkipUtils.toImageShowActivity(mActivity , localImageList ,position);
                        }
                    }
                }
            }
            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        mPicLay.setAdapter(imageSelectorResultAdapter);
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMI_IMAGE, perms);
        }
    }

    private void goCheckPhoto() {
        if (imageConfig == null)
            imageConfig = new ImageConfig.Builder(new GlideImageLoader())
                    .steepToolBarColor(Color.BLACK)
                    .titleBgColor(Color.BLACK)
                    .titleSubmitTextColor(getResources().getColor(R.color.white))
                    .titleTextColor(getResources().getColor(R.color.white))
                    // 开启多选   （默认为多选）
                    .mutiSelectMaxSize(MAX_IMAGE_COUNT)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();

        imageConfig.setMaxSize(MAX_IMAGE_COUNT - localImageList.size()) ;
        ImageSelector.open(mActivity, imageConfig);
    }


    private void goSelectedAddress() {
        SelectedAddressActivity.launch(mActivity,false ,REQUEST_CODE_FOR_ADDRESS);
    }

    private void requstFactoryApply() {
        String factoryName = mEtFactoryName.getText().toString().trim();
        if (TextUtils.isEmpty(factoryName)) {
            ToastUtils.showErrorToast(mContext, "请填写工厂名称");
            return;
        }
        if (TextUtils.isEmpty(mCategoryIds)) {
            ToastUtils.showErrorToast(mContext, "请选择经营范围");
            return;
        }

        if (TextUtils.isEmpty(mTvAddress.getText().toString().trim())) {
            ToastUtils.showErrorToast(mContext, "请选择所属位置");
            return;
        }

        String factoryDesc = mEtFactoryDesc.getText().toString().trim();
        if (TextUtils.isEmpty(factoryDesc)) {
            ToastUtils.showErrorToast(mContext, "请填写工厂简介");
            return;
        }

        if (TextUtils.isEmpty(licenceUrl)) {
            ToastUtils.showErrorToast(mContext, "请上传营业执照照片");
            return;
        }

        String img1 = "" ;
        String img2 = "" ;
        String img3 = "" ;
        String img4 = "" ;
        String img5 = "" ;
        String thumbList = "" ;
        if(imageUploadList.size() > 0){
            for(String imgurl : imageUploadList){
                if(!"".equals(StringUtils.convertNull(imgurl))){
                    thumbList += (imgurl + ",") ;

                    if("".equals(img1)){
                        img1 = imgurl ;
                    }else if("".equals(img2)){
                        img2 = imgurl ;
                    }else if("".equals(img3)){
                        img3 = imgurl ;
                    }else if("".equals(img4)){
                        img4 = imgurl ;
                    }else if("".equals(img5)){
                        img5 = imgurl ;
                    }
                }
            }
            thumbList = thumbList.substring(0,thumbList.length() - 1) ;
        }

        boolean isNoChange = true ;
        if(null == mApplyInfo
                || !factoryName.equals(mApplyInfo.getName())
                || !mApplyInfo.getScopeOperationIds().equals(mCategoryIds)
                || !mApplyInfo.getIntroduce().equals(factoryDesc)
                || !mApplyInfo.getProvinceId().equals(provinceId)
                || !mApplyInfo.getCityId().equals(cityId)
                || !mApplyInfo.getAddress().equals(addressDetail)
                || !mApplyInfo.getBusinessLicenseImg().equals(licenceUrl)
                || !thumbList.equals(mThumbIds)){
            isNoChange = false ;
        }

        final Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getFactoryAuthenticationId() : "");
        para.put("ScopeOperationIds", StringUtils.convertNull(mCategoryIds));
        para.put("ProvinceId", StringUtils.convertNull(provinceId));
        para.put("CityId", StringUtils.convertNull(cityId));
        para.put("CountyId", StringUtils.convertNull(areaId));
        para.put("Introduce", factoryDesc);
        para.put("Name", factoryName);
        para.put("Address", StringUtils.convertNull(addressDetail));
        para.put("BusinessLicenseImg", StringUtils.convertNull(licenceUrl));
        para.put("Img1", StringUtils.convertNull(img1));
        para.put("Img2", StringUtils.convertNull(img2));
        para.put("Img3", StringUtils.convertNull(img3));
        para.put("Img4", StringUtils.convertNull(img4));
        para.put("Img5", StringUtils.convertNull(img5));

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

        NetUtils.getDataFromServerByPost(mContext, Urls.FACTORY_APPLY, false, para,
                new RequestObjectCallBack<String>("工厂认证申请", mContext, String.class) {
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

    private void requestFactoryApplyInfo() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        String urls ;
        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        if(mIsShowState){
            para.put("code" ,StringUtils.convertNull(mUserId)) ;
            urls = Urls.FACTORY_APPLY_INFO_OTHER ;
        }else{
            urls = Urls.GET_FACTORY_APPLY_INFO ;
        }
        NetUtils.getDataFromServerByPost(mContext, urls, false, para,
                new RequestObjectCallBack<FactoryApplyInfo>("获取工厂认证信息", mContext,
                        FactoryApplyInfo.class) {
                    @Override
                    public void onSuccessResult(FactoryApplyInfo bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (bean != null){
                            mApplyInfo = bean ;
                        }

                        updateUI();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        updateUI();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        updateUI();
                    }
                });

    }

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

        mCategoryIds = mApplyInfo.getScopeOperationIds() ;
        provinceId = mApplyInfo.getProvinceId() ;
        cityId = mApplyInfo.getCityId() ;
        areaId = mApplyInfo.getCountyId() ;
        addressDetail = mApplyInfo.getAddress() ;

        licenceUrl = mApplyInfo.getBusinessLicenseImg() ;

        mEtFactoryName.setText(mApplyInfo.getName());
        mTvType.setText(mApplyInfo.getScopeOperationName());
        mTvAddress.setText(mApplyInfo.getProvinceName() + mApplyInfo.getCityName() + mApplyInfo.getCountyName()
                + mApplyInfo.getAddress());
        mEtFactoryDesc.setText(mApplyInfo.getIntroduce());

        GlideUtils.loader(mContext,licenceUrl,mImgLicence);

        List<ImageThumb> thumbs = mApplyInfo.getAlbumsList() ;
        if(thumbs != null && thumbs.size() > 0){
            int size = thumbs.size() ;

            for(int x = size - 1 ; x >= 0 ; x--){
                ImageThumb thumb = thumbs.get(x) ;
                String imgUrl = thumb.getThumbImgUrl() ;

                localImageList.add(0, imgUrl);
                mAdapterImageList.add(0, imgUrl);
                imageUploadList.add(0, imgUrl);
            }

            StringBuffer imgBuilder = new StringBuffer() ;
            for(String imgUrl : imageUploadList){
                imgBuilder.append(imgUrl).append(",") ;
            }

            mThumbIds = imgBuilder.toString() ;
            if(mThumbIds.length() > 1){
                mThumbIds = mThumbIds.substring(0 , mThumbIds.length() - 1) ;
            }

            uploadIndex = localImageList.size() ;
            imageSelectorResultAdapter.notifyDataSetChanged();
        }
    }

    private void checkPhoto(int requestCode, int maxSize) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_COMPANY_TYPE) {
            if(RESULT_OK == resultCode && data != null){
                List<UnusedCategory> goodCategory = (ArrayList<UnusedCategory>)
                        data.getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
                mSelectedCategory.clear();
                mCategoryIds = "" ;

                if(goodCategory != null && goodCategory.size() > 0){
                    mSelectedCategory.addAll(goodCategory) ;

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

                    mTvType.setText(title);
                }else{
                    mTvType.setText("请选择") ;
                }
            }
        } else if (requestCode == REQUEST_CODE_FOR_ADDRESS
                && resultCode == SelectedAddressActivity.RESULT_CODE_FOR_SELECTED_ADDRESS) {

            areaId = data.getStringExtra("areaId");
            areaName = data.getStringExtra("areaName");
            provinceId = data.getStringExtra("provinceId");
            provinceName = data.getStringExtra("provinceName");
            cityId = data.getStringExtra("cityId");
            cityName = data.getStringExtra("cityName");
            addressDetail = data.getStringExtra("addressDetail");
            mTvAddress.setText(provinceName + cityName + addressDetail);

        } else if (requestCode == REQUEST_CODE_FOR_LICENCE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                uploadPhoto(photos.get(0));
            }
        }else if(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode){
            if (resultCode == RESULT_OK && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                localImageList.addAll(pathList);
                int index = mAdapterImageList.size() - 1 ;
                if(index < 0){
                    index = 0 ;
                }
                mAdapterImageList.addAll(index ,pathList);
                handler.sendEmptyMessage(UPLOAD_PHOTO);
            }
        }
    }


    private void uploadPhoto(String photoLocalPath) {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(mContext);
        String base64Image = FileUtil.getUploadImageBase64String(photoLocalPath);
        para.put("Base64Image", base64Image);
        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new RequestObjectCallBack<String>("上传图片", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        GlideUtils.loader(mContext, bean, mImgLicence);
                        licenceUrl = bean;
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

    /***
     * 上传图片
     */
    private void requestUploadPhoto(String base64Image, final int size) {
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("base64Image", base64Image);

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false ,para,
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
                            ToastUtils.showErrorToast(mContext, "图片上传失败");
                        }
                    }
                });
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

}