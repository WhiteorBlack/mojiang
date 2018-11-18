package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.utils.FileUtil;
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

public class CircleSendActivity extends BaseMoreStatusActivity implements EasyPermissions.PermissionCallbacks{
    private EditText mContentEv ;
    private TextView mLocationTv ;
    private RecyclerView mRvPhoto;

    private String mCategoryId ;
    private String mCurSheng = "";
    private String mCurShi = "";
    private String mCurQu = "";
    private String mCurLon = "0";
    private String mCurLat = "0";

    private LocationService mLocationService;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;

    private final static int REQUEST_CODE_PERMI_LOCATION = 100 ;
    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;

    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 9;

    private final static int MSG_LOCATION = 10;
    private boolean mIsSending = false ;

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
                case MSG_LOCATION://定位判断

                    if(!"".equals(StringUtils.convertNull(mCurSheng))){
                        handler.removeMessages(MSG_LOCATION);

                        if(mIsSending){
                            sendCircleContent() ;
                        }
                    }else{
                        handler.sendEmptyMessageDelayed(MSG_LOCATION ,500) ;
                    }

                    break;
            }
        }
    };


    @Override
    public void requestData() {
        showSuccessPage();
        startLocations() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_send_circle;
    }

    @Override
    public String setTitle() {
        return "发布圈子";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;

        mAdapterImageList.add(null) ;//添加图片的那个图标

        loadingDialog = new LoadingDialog(mContext) ;

        mContentEv = findViewById(R.id.acti_send_circle_content_ev) ;
        mLocationTv = findViewById(R.id.acti_send_circle_location_tv) ;
        TextView mSendTv = findViewById(R.id.acti_send_circle_send_tv) ;
        mRvPhoto = findViewById(R.id.acti_send_circle_add_rv) ;

        mLocationTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        initPhotoContainer();
    }

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2
                - ResourceUtils.dip2px(mContext , 5) * 4) / 5 ;

        mRvPhoto.setLayoutManager(new GridLayoutManager(this, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei,MAX_IMAGE_COUNT, new RecyclerViewClickListener() {
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
                    if(position < MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1){
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
        mRvPhoto.setAdapter(imageSelectorResultAdapter);
    }
    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_send_circle_send_tv == vId){//发布
            sendCircleContent() ;
        }else if(R.id.acti_send_circle_location_tv == vId){
            startLocations() ;
        }
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
        ImageSelector.open(CircleSendActivity.this, imageConfig);
    }

    /**
     * 发布
     */
    private void sendCircleContent(){
        mIsSending = true ;

        String content = mContentEv.getText().toString() ;
        if("".equals(content.trim()) && imageUploadList.size() == 0){
            ToastUtils.showToast(mContext , "说点什么吧...") ;
            return ;
        }

        if(loadingDialog == null){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.setLoadingText("正在发布");
            loadingDialog.show() ;
        }

        if("".equals(StringUtils.convertNull(mCurSheng))
                || "".equals(StringUtils.convertNull(mCurShi))){
            handler.sendEmptyMessageDelayed(MSG_LOCATION ,500) ;

            startLocations() ;
            return ;
        }

        mIsSending = false ;

        StringBuffer imgBuilder = new StringBuffer() ;
        for(String imgUrl : imageUploadList){
            imgBuilder.append(imgUrl).append(",") ;
        }

        String thumbList = imgBuilder.toString() ;
        if(thumbList.length() > 1){
            thumbList = thumbList.substring(0 , thumbList.length() - 1) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("BodyContent" , content) ;
        paramMap.put("CategoryID" , StringUtils.convertNull(mCategoryId)) ;
        paramMap.put("ProvinceName" , mCurSheng) ;
        paramMap.put("CityName" , mCurShi) ;
        paramMap.put("CountyName" , mCurQu) ;
        paramMap.put("Longitude" , mCurLon) ;
        paramMap.put("Latitude" , mCurLat) ;
        paramMap.put("Albums" , thumbList) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SEND_CIRCLE, false, paramMap,
                new RequestObjectCallBack<String>("sendCircleContent" , mContext , String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        ToastUtils.showToast(mContext , "发布成功") ;
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
                    }
                }) ;
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
                        }
                    }
                });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode){
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


    /**
     * 开始定位
     */
    private void startLocations() {
        if(!EasyPermissions.hasPermissions(mContext
                , Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            EasyPermissions.requestPermissions(mActivity
                    ,"应用需要定位权限来获取当前位置，拒绝会导致部分功能异常",REQUEST_CODE_PERMI_LOCATION
                    , Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    ,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return ;
        }

        // -----------location config ------------
        mLocationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        mLocationService.registerListener(mListener);
        //注册监听
        mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
        mLocationService.start() ;
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                mCurSheng = location.getProvince() ;
                mCurShi = location.getCity() ;
                mCurQu = location.getDistrict() ;

                final String locationDesc ;
                List<Poi> poiList = location.getPoiList() ;
                if(poiList.size() > 0){
                    locationDesc = poiList.get(0).getName() ;
                }else{
                    locationDesc = location.getLocationDescribe() ;
                }

                LogUtils.showLog("testLocations" ,"sendCircle--" + locationDesc) ;

                if(mCurSheng != null && mCurShi != null){
                    mCurLon = location.getLongitude() +"" ;
                    mCurLat = location.getLatitude() +"" ;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLocationTv.setText(null == locationDesc ? "点击获取位置" : locationDesc);
                        }
                    });

                    mLocationService.unregisterListener(mListener); //注销掉监听
                    mLocationService.stop(); //停止定位服务

                    MyApplication.updateCurLocation(location) ;

                    handler.sendEmptyMessage(MSG_LOCATION) ;
                }

            }
        }
    };

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(REQUEST_CODE_PERMI_LOCATION == requestCode){
            startLocations() ;
        }else{
            goCheckPhoto() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext ,"拒绝了相关权限，会导致部分功能失败");

        if(mIsSending){
            mIsSending = false ;
            handler.removeMessages(MSG_LOCATION) ;

            if(loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults ,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }


        if(mLocationService != null){
            mLocationService.unregisterListener(mListener); //注销掉监听
            mLocationService.stop(); //停止定位服务
        }

    }
}