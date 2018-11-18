package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderCommentGood;
import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodOrderCommentSend;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
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
 * Created on 2018/9/19.
 *
 * 调整界面样式，仿淘宝，直接一次评价这个订单下的所有商品，
 * 商品默认5星，店铺、服务评分默认5星，
 * 评星必填，商品评价非必填
 *
 * 2018-09-25 10:00:14
 * 如果商品是申请售后了，无论是否处理了售后，都不允许评价
 */

public class GoodOrderCommentSendActivity extends BaseActivity  implements EasyPermissions.PermissionCallbacks{

    private String mOrderId ;

    private StarView mStarDriverView ;
    private StarView mStarServerView ;


    private List<GoodOrderCommentGood> mGoodList = new ArrayList<>();
    private HeaderFooterAdapter<AdapterGoodOrderCommentSend> mAdapter ;

    private int mCurPosition ;
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    public static final int MAX_IMAGE_COUNT = 9 ;
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();

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
                    if (localImageList.size() > 0) {
                        new GetImageBase64Task(localImageList.get(uploadIndex), localImageList.size()).execute() ;
                    }
                    break;
            }
        }
    };


    public static void launch(Activity context ,String orderId
            , List<GoodOrderGood> goodList,int requestCode){
        Intent toCmIt = new Intent(context ,GoodOrderCommentSendActivity.class) ;
        toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID,orderId) ;
        toCmIt.putExtra(SkipUtils.INTENT_ORDER_GOOD_INFO , (Serializable) goodList) ;
        context.startActivityForResult(toCmIt ,requestCode) ;
    }

    public static void launch(Fragment context , String orderId
            , List<GoodOrderGood> goodList, int requestCode){
        Intent toCmIt = new Intent(context.getContext() ,GoodOrderCommentSendActivity.class) ;
        toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID,orderId) ;
        toCmIt.putExtra(SkipUtils.INTENT_ORDER_GOOD_INFO , (Serializable) goodList) ;
        context.startActivityForResult(toCmIt ,requestCode) ;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_good_order_comment_send ;
    }

    @Override
    public void initView() {
        super.initView();

        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;
        List<GoodOrderGood> goodList = (List<GoodOrderGood>) getIntent().getSerializableExtra(SkipUtils.INTENT_ORDER_GOOD_INFO);

        if(goodList != null){
            for(GoodOrderGood good : goodList){
                if(good.canAfterSale()){//只保留未申请售后的商品
                    mGoodList.add(new GoodOrderCommentGood(good)) ;
                }
            }
        }

        RecyclerView mRv = findViewById(R.id.acti_good_order_comment_send_rv) ;
        mRv.setLayoutManager(new LinearLayoutManager(mContext)) ;
        mRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext, 5))) ;

        AdapterGoodOrderCommentSend adapter = new AdapterGoodOrderCommentSend(mActivity
                , mGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {

                mCurPosition = position ;
                if(0 == type){
                    checkPermission() ;
                }


            }
        }) ;
        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mRv.setAdapter(mAdapter) ;

        View footerView = LayoutInflater.from(mContext).inflate(R.layout.header_good_order_comment_send,null) ;
        mAdapter.addFooter(footerView);

        mStarDriverView = footerView.findViewById(R.id.header_good_order_comment_driver_star_view) ;
        mStarServerView = footerView.findViewById(R.id.header_good_order_comment_server_star_view) ;
        View submitTv = footerView.findViewById(R.id.header_good_order_comment_submit_tv) ;
        submitTv.setOnClickListener(this);

        mStarDriverView.setCanClick(true);
        mStarServerView.setCanClick(true);
        mStarDriverView.setShowDesc(true);
        mStarServerView.setShowDesc(true);
        mStarDriverView.setLevel(5);
        mStarServerView.setLevel(5);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.header_good_order_comment_submit_tv == view.getId()){//提交评价

            submitOrderComment() ;

        }
    }


    /**
     * 提交
     */
    private void submitOrderComment(){
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

        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setLoadingText("");
        loadingDialog.show() ;

        StringBuilder infoBui = new StringBuilder() ;
        for(GoodOrderCommentGood good : mGoodList){
            String goodImgStr = "" ;
            List<String> goodImg = good.getCommentImgList() ;
            if(goodImg.size() > 1){
                goodImg.remove(goodImg.size() - 1) ;//最后一个是null
                for(String imgUrl : goodImg){
                    goodImgStr += (imgUrl+",") ;
                }

                goodImgStr = goodImgStr.substring(0,goodImgStr.length() -1) ;
            }

            infoBui.append("{")
                    .append("\"Star\":\"").append(good.getStar()).append("\",")
                    .append("\"Content\":\"").append(good.getCommentContent()).append("\",")
                    .append("\"Albums\":\"").append(goodImgStr).append("\",")
                    .append("\"OrderItemID\":\"").append(good.getOrderItemID()).append("\"")
                    .append("},") ;
        }

        String goodInfo = infoBui.substring(0,infoBui.length() - 1) ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("WuLiuFuWuStar" ,"" + driverStar) ;
        paramMap.put("FuWuTaiDuStar" ,"" + serverStar) ;
        paramMap.put("ItemList" ,"[" + goodInfo + "]") ;
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
     * 选择图片
     */
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

        GoodOrderCommentGood goodInfo = mGoodList.get(mCurPosition) ;
        List<String> goodImageList = goodInfo.getCommentImgList() ;
        int goodSize = goodImageList.size() ;
        int maxSize = MAX_IMAGE_COUNT ;
        if(goodSize > 1){
            maxSize = MAX_IMAGE_COUNT - goodSize + 1 ;
        }

        imageConfig.setMaxSize(maxSize) ;
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

                                if(0 == uploadIndex){
                                    imageUploadList.clear() ;
                                }

                                imageUploadList.add(url);
                                if (uploadIndex == size - 1) {
                                    uploadIndex = 0;

                                    //上传完成
                                    mGoodList.get(mCurPosition).addCommentImgList(imageUploadList) ;
                                    mAdapter.notifyDataSetChanged();

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
                localImageList.clear();
                localImageList.addAll(pathList);
                uploadIndex = 0 ;
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
