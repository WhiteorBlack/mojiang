package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.StarView;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 商品评价
 * Created on 2018/4/28.
 *
 * 2018-08-27 15:01:19
 * 改：调整界面
 *
 * 2018-09-19 14:30:45
 * 调整界面样式，仿淘宝，直接一次评价这个订单下的所有商品，
 * 商品默认5星，店铺、服务评分默认5星，
 * 评星必填，商品评价非必填
 *
 */
@Deprecated
public class GoodOrderCommentEditActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private String mOrderId ;
    private GoodOrderGood mGoodInfo ;

    private ImageView mGoodIv ;
    private TextView mGoodNameTv ;
    private StarView mStarView ;
    private EditText mContentEv ;
    private RecyclerView mImageRv ;
    private StarView mStarDriverView ;
    private StarView mStarServerView ;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private static final int MAX_IMAGE_COUNT = 9 ;
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    private final static int UPLOAD_PHOTO = 23;

    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;


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
    public int getLayoutID() {
        return R.layout.activity_good_order_comment ;
    }

    @Override
    public void initView() {
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;
        mGoodInfo = (GoodOrderGood) getIntent().getSerializableExtra(SkipUtils.INTENT_ORDER_GOOD_INFO);

        mGoodIv = findViewById(R.id.acti_good_order_comment_iv) ;
        mGoodNameTv = findViewById(R.id.acti_good_order_comment_name_tv) ;
        mStarView = findViewById(R.id.acti_good_order_comment_star_view) ;
        mContentEv = findViewById(R.id.acti_good_order_comment_content_ev) ;
        mImageRv = findViewById(R.id.acti_good_order_comment_image_rv) ;
        mStarDriverView = findViewById(R.id.acti_good_order_comment_driver_star_view) ;
        mStarServerView = findViewById(R.id.acti_good_order_comment_server_star_view) ;
        TextView mSubmitTv = findViewById(R.id.acti_good_order_comment_submit_tv) ;
        mSubmitTv.setOnClickListener(this);
        mStarView.setCanClick(true);
        mStarDriverView.setCanClick(true);
        mStarServerView.setCanClick(true);
        mStarView.setShowDesc(true);
        mStarDriverView.setShowDesc(true);
        mStarServerView.setShowDesc(true);

        loadingDialog = new LoadingDialog(mContext) ;

        mAdapterImageList.add(null) ;//添加图片的那个图标

        initBaseView() ;

        initPhotoContainer() ;
    }

    /**
     * 填充数据
     */
    private void initBaseView(){
        if(null == mGoodInfo || null == mOrderId){
            DialogUtils.showCustomViewDialog(mContext, "商品信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

        String goodName = mGoodInfo.getProductTitle() ;
        String goodImg = mGoodInfo.getImgUrl() ;
        mGoodNameTv.setText(goodName);
        GlideUtils.loader(goodImg ,mGoodIv) ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_good_order_comment_submit_tv == view.getId()){
            submitOrderComment() ;
        }
    }

    /**
     * 提交评论
     */
    private void submitOrderComment(){
        String content = mContentEv.getText().toString().trim() ;
        if("".equals(content)){
            mContentEv.requestFocus() ;
            mContentEv.setText("");
            ToastUtils.showToast(mContext ,"请输入商品评价");
            return ;
        }

        int star = mStarView.getLevel() ;
        if(star < 1){
            ToastUtils.showToast(mContext ,"请对描述相符进行评分") ;
            return ;
        }

        int driverStar = mStarDriverView.getLevel() ;
        if(driverStar < 1){
            ToastUtils.showToast(mContext ,"请对物流服务进行评分") ;
            return ;
        }

        int serverStar = mStarServerView.getLevel() ;
        if(serverStar < 1){
            ToastUtils.showToast(mContext ,"请对服务态度进行评分") ;
            return ;
        }

        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show();


        StringBuffer imgBuilder = new StringBuffer() ;
        for(String imgUrl : imageUploadList){
            imgBuilder.append(imgUrl).append(",") ;
        }

        String thumbList = imgBuilder.toString() ;
        if(thumbList.length() > 1){
            thumbList = thumbList.substring(0 , thumbList.length() - 1) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Star" ,"" + star) ;
        paramMap.put("WuLiuFuWuStar" ,"" + driverStar) ;
        paramMap.put("FuWuTaiDuStar" ,"" + serverStar) ;
        paramMap.put("Content" ,content) ;
        paramMap.put("Albums" ,thumbList) ;
        paramMap.put("OrderItemID" , StringUtils.convertNull(mGoodInfo.getOrderItemID())) ;
        paramMap.put("OrderId" ,StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_COMMENT, paramMap
                , new RequestObjectCallBack<String>("submitOrderComment" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        DialogUtils.showCustomViewDialog(mContext, "订单评价成功"
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                                        setResult(RESULT_OK);
                                        finish() ;
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

                        DialogUtils.showCustomViewDialog(mContext, "订单评价失败！"
                                , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
    }

    /**
     * 初始化图片
     */
    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        mImageRv.setLayoutManager(new GridLayoutManager(this, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei,MAX_IMAGE_COUNT , new RecyclerViewClickListener() {
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
        mImageRv.setAdapter(imageSelectorResultAdapter);
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
                        }
                    }
                });
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(REQUEST_CODE_PERMI_IMAGE == requestCode){
            goCheckPhoto() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext ,"拒绝了相关权限，会导致部分功能失败");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults ,this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }

    }
}
