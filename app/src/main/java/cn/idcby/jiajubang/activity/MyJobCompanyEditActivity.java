package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.JobCompanyInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
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
 * 编辑发布招聘时的公司信息
 * Created on 2018/5/20.
 *
 * 2018-08-06 16:00:46
 * 【问题汇总0804】公司简介绍的图片，上传9张，单行滑动展示
 *
 */

public class MyJobCompanyEditActivity extends BaseMoreStatusActivity
        implements EasyPermissions.PermissionCallbacks{
    private EditText mComNameEv ;
    private EditText mComScaleEv ;//规模
    private EditText mComTypeEv ;
    private EditText mComAddressEv ;
    private ImageView mComLogoIv ;
    private EditText mComDescEv ;
    private RecyclerView mComPicRv;

    private TextView mSubmitTv ;

    private JobCompanyInfo mCompanyInfo ;

    private String mThumbs = "" ;

    //图片选择相关
    //公司logo
    private final int PRC_PHOTO_PICKER = 300;
    private final int REQUEST_CODE_FOR_COMPANY_LOGO = 1100;
    private String mComLogoImgUrl = "";
    private static final int MAX_PIC_SIZE = 9 ;

    private LoadingDialog loadingDialog;

    //公司介绍照片
    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private ImageConfig imageConfig;

    private final int PRC_PHOTO_PICKER_DESC = 301;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
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


    @Override
    public void requestData() {
        getJobsCompanyInfo() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_job_com_edit ;
    }

    @Override
    public String setTitle() {
        return "修改公司信息";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        loadingDialog = new LoadingDialog(mContext) ;

        mComNameEv = findViewById(R.id.acti_my_job_com_edit_com_name_ev) ;
        mComScaleEv = findViewById(R.id.acti_my_job_com_edit_com_guimo_ev) ;
        mComTypeEv = findViewById(R.id.acti_my_job_com_edit_com_type_ev) ;
        mComAddressEv = findViewById(R.id.acti_my_job_com_edit_com_address_ev) ;
        mComLogoIv = findViewById(R.id.acti_my_job_com_edit_com_logo_iv) ;
        mComDescEv = findViewById(R.id.acti_my_job_com_edit_com_desc_ev) ;
        mComPicRv = findViewById(R.id.acti_my_job_com_edit_com_pic_lay) ;

        mSubmitTv = findViewById(R.id.acti_my_job_com_edit_sub_tv) ;

        mSubmitTv.setOnClickListener(this);
        mComLogoIv.setOnClickListener(this);

        initPhotoContainer() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_job_com_edit_com_logo_iv == vId){//公司logo
            checkPhoto() ;
        }else if(R.id.acti_my_job_com_edit_sub_tv == vId){//发布
            submitEdit() ;
        }
    }

    /**
     * 填充内容
     */
    private void updateJobDetails(){
        showSuccessPage();

        if(null == mCompanyInfo){
            return ;
        }

        mComNameEv.setText(mCompanyInfo.getCompanyName()) ;
        mComScaleEv.setText(mCompanyInfo.getCompanyScale());
        mComTypeEv.setText(mCompanyInfo.getCompanyType());
        mComAddressEv.setText(mCompanyInfo.getCompanyAddress());
        mComDescEv.setText(mCompanyInfo.getCompanyIntroduce());
        mComLogoImgUrl = mCompanyInfo.getCompanyLogoImage() ;
        if(!"".equals(mComLogoImgUrl)){
            GlideUtils.loader(mComLogoImgUrl,mComLogoIv);
        }

        List<ImageThumb> thumbs = mCompanyInfo.getImageThumb() ;
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

            mThumbs = imgBuilder.toString() ;
            if(mThumbs.length() > 1){
                mThumbs = mThumbs.substring(0 , mThumbs.length() - 1) ;
            }

            uploadIndex = localImageList.size() ;
            imageSelectorResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取列表--公司模版
     */
    private void getJobsCompanyInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_COM_NOMAL_INFO, false, paramMap
                , new RequestObjectCallBack<JobCompanyInfo>("getJobsCompanyInfo", mContext ,JobCompanyInfo.class) {
                    @Override
                    public void onSuccessResult(JobCompanyInfo bean) {
                        mCompanyInfo = bean ;

                        updateJobDetails() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateJobDetails() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateJobDetails() ;
                    }
                });
    }


    private void initPhotoContainer() {
        mAdapterImageList.add(null) ;//添加图片的那个图标

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 4 ;

        mComLogoIv.getLayoutParams().width = itemWidHei ;
        mComLogoIv.getLayoutParams().height = itemWidHei ;

        mComPicRv.setLayoutManager(new GridLayoutManager(this, 4));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei,MAX_PIC_SIZE , new RecyclerViewClickListener() {
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
                    if(position < MAX_PIC_SIZE && position == mAdapterImageList.size() - 1){
                        //选择图片
                        checkPermission();
                    }else{
                        SkipUtils.toImageShowActivity(mActivity , localImageList ,position);
                    }
                }
            }
            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        mComPicRv.setAdapter(imageSelectorResultAdapter);
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    PRC_PHOTO_PICKER_DESC, perms);
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
                    .mutiSelectMaxSize(MAX_PIC_SIZE)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();

        imageConfig.setMaxSize(MAX_PIC_SIZE - localImageList.size()) ;
        ImageSelector.open(MyJobCompanyEditActivity.this, imageConfig);
    }


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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(PRC_PHOTO_PICKER == requestCode){
            checkPhoto() ;
        }else if(PRC_PHOTO_PICKER_DESC == requestCode){
            goCheckPhoto() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(1000 == requestCode){
            ToastUtils.showToast(mContext ,"拒绝了相关权限，会导致定位功能失败");
        }else{
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadLogoPhoto(String photoLocalPath) {
        loadingDialog.setLoadingText("正在上传");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        LogUtils.showLog("上传图片的路径>>>" + photoLocalPath);
        Map<String, String> para = ParaUtils.getPara(mContext);
        String base64Image = FileUtil.getUploadImageBase64String(photoLocalPath);
        para.put("Base64Image", base64Image);
        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new RequestObjectCallBack<String>("uploadLogoPhoto", mContext, String.class) {

                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        GlideUtils.loader(mContext, bean, mComLogoIv);
                        mComLogoImgUrl = bean;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        ToastUtils.showToast(mContext ,"上传失败");
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        ToastUtils.showToast(mContext ,"上传失败");
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

    /**
     * 提交
     */
    private void submitEdit(){
        String companyName = mComNameEv.getText().toString().trim() ;
        if("".equals(companyName)){
            ToastUtils.showToast(mContext ,"请输入公司名称");
            mComNameEv.requestFocus() ;
            mComNameEv.setText("") ;
            return ;
        }

        String companyScale = mComScaleEv.getText().toString().trim() ;
        if("".equals(companyScale)){
            ToastUtils.showToast(mContext ,"请输入公司规模");
            mComScaleEv.requestFocus() ;
            mComScaleEv.setText("") ;
            return ;
        }

        String companyType = mComTypeEv.getText().toString().trim() ;
        if("".equals(companyType)){
            ToastUtils.showToast(mContext ,"请输入公司类型");
            mComTypeEv.requestFocus() ;
            mComTypeEv.setText("") ;
            return ;
        }

        String companyAddress = mComAddressEv.getText().toString().trim() ;
        if("".equals(companyAddress)){
            ToastUtils.showToast(mContext ,"请输入公司地址");
            mComAddressEv.requestFocus() ;
            mComAddressEv.setText("") ;
            return ;
        }

        if(null == mComLogoImgUrl){
            ToastUtils.showToast(mContext ,"请上传公司logo或门头照");
            return ;
        }

        String companyDesc = mComDescEv.getText().toString().trim() ;
        if("".equals(companyDesc)){
            ToastUtils.showToast(mContext ,"请输入公司介绍");
            mComDescEv.requestFocus() ;
            mComDescEv.setText("") ;
            return ;
        }

        String thumbList = "" ;
        if(imageUploadList.size() > 0){
            for(String imgurl : imageUploadList){
                thumbList += (imgurl + ",") ;
            }
            thumbList = thumbList.substring(0,thumbList.length() - 1) ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("CompanyName" ,companyName) ;
        paramMap.put("CompanyScale" ,companyScale) ;
        paramMap.put("CompanyType" ,companyType) ;
        paramMap.put("CompanyAddress" ,companyAddress) ;
        paramMap.put("CompanyLogoImage" ,StringUtils.convertNull(mComLogoImgUrl)) ;
        paramMap.put("CompanyIntroduce" ,companyDesc) ;
        paramMap.put("Albums" ,thumbList) ;

        submitCreateRequest(paramMap) ;
    }

    private void submitCreateRequest(Map<String,String> paramMap){
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show() ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_COM_NOMAL_INFO_EDIT, paramMap
                , new RequestObjectCallBack<String>("sendZhaoping" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        ToastUtils.showToast(mContext ,"提交成功");
                        setResult(RESULT_OK);
                        finish() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();

                        ToastUtils.showToast(mContext ,"发布失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOR_COMPANY_LOGO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                uploadLogoPhoto(photos.get(0));
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }

    }

}
