package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 我的报价--发起付款
 * Created on 2018/4/19.
 */
@Deprecated
public class MyNeedsOfferSendPayActivity extends BaseActivity  implements EasyPermissions.PermissionCallbacks{
    private EditText mMoneyEv ;
    private EditText mContentEv ;

    private ImageView mPicOneIv ;
    private View mPicOneDeleteIv ;
    private ImageView mPicTwoIv ;
    private View mPicTwoDeleteIv ;
    private ImageView mPicThreeIv ;
    private View mPicThreeDeleteIv ;

    private String mNeedId ;

    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    private int mCurPictureIndex = 0 ;
    private String mPicUrlLocatOne = null ;
    private String mPicUrlServiceOne = null ;
    private String mPicUrlLocatTwo = null ;
    private String mPicUrlServiceTwo = null ;
    private String mPicUrlLocatThree = null ;
    private String mPicUrlServiceThree = null ;


    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;




    @Override
    public int getLayoutID() {
        return R.layout.activity_my_needs_offer_send_pay ;
    }

    @Override
    public void initView() {
        mNeedId = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_ID) ;

        mMoneyEv = findViewById(R.id.acti_my_needs_offer_send_money_ev) ;
        mContentEv = findViewById(R.id.acti_my_needs_offer_send_content_ev) ;

        mPicOneIv = findViewById(R.id.acti_my_needs_order_send_img_one_iv) ;
        mPicOneDeleteIv = findViewById(R.id.acti_my_needs_order_send_img_one_delete_iv) ;
        mPicTwoIv = findViewById(R.id.acti_my_needs_order_send_img_two_iv) ;
        mPicTwoDeleteIv = findViewById(R.id.acti_my_needs_order_send_img_two_delete_iv) ;
        mPicThreeIv = findViewById(R.id.acti_my_needs_order_send_img_three_iv) ;
        mPicThreeDeleteIv = findViewById(R.id.acti_my_needs_order_send_img_three_delete_iv) ;

        TextView mSubTv = findViewById(R.id.acti_my_needs_offer_send_submit_tv) ;

        mSubTv.setOnClickListener(this);
        mPicOneIv.setOnClickListener(this);
        mPicOneDeleteIv.setOnClickListener(this);
        mPicTwoIv.setOnClickListener(this);
        mPicTwoDeleteIv.setOnClickListener(this);
        mPicThreeIv.setOnClickListener(this);
        mPicThreeDeleteIv.setOnClickListener(this);

    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_needs_order_send_img_one_iv == vId){
            choosePictureByType(0) ;
        }else if(R.id.acti_my_needs_order_send_img_one_delete_iv == vId){
            deletePictureByType(0) ;
        }else if(R.id.acti_my_needs_order_send_img_two_iv == vId){
            choosePictureByType(1) ;
        }else if(R.id.acti_my_needs_order_send_img_two_delete_iv == vId){
            deletePictureByType(1) ;
        }else if(R.id.acti_my_needs_order_send_img_three_iv == vId){
            choosePictureByType(2) ;
        }else if(R.id.acti_my_needs_order_send_img_three_delete_iv == vId){
            deletePictureByType(2) ;
        }else if(R.id.acti_my_needs_offer_send_submit_tv == vId){
            submitSendPay() ;
        }
    }

    private void submitSendPay(){
        if(mPicUrlLocatOne != null && mPicUrlServiceOne == null){
            requestUploadPhoto(mPicUrlLocatTwo ,0) ;
            return ;
        }
        if(mPicUrlLocatTwo != null && mPicUrlServiceTwo == null){
            requestUploadPhoto(mPicUrlLocatTwo ,1) ;
            return ;
        }
        if(mPicUrlLocatThree != null && mPicUrlServiceThree == null){
            requestUploadPhoto(mPicUrlLocatThree ,2) ;
            return ;
        }

        String money = mMoneyEv.getText().toString() ;
        if(!MyUtils.isRightMoney(money.trim())){
            mMoneyEv.setSelection(money.length()) ;
            mMoneyEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext , "请输入正确的金额");
            return;
        }

        String content = mContentEv.getText().toString().trim() ;
        if("".equals(content)){
            mContentEv.setText("") ;
            mContentEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext , "请输入工作内容");
            return ;
        }

        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show();

        List<String> imgList = new ArrayList<>(3) ;
        String imgOne = "" ;
        String imgTwo = "" ;
        String imgThree = "" ;
        if(mPicUrlServiceOne != null){
            imgList.add(mPicUrlServiceOne) ;
        }
        if(mPicUrlServiceTwo != null){
            imgList.add(mPicUrlServiceTwo) ;
        }
        if(mPicUrlServiceThree != null){
            imgList.add(mPicUrlServiceThree) ;
        }

        if(imgList.size() == 3){
            imgOne = imgList.get(0) ;
            imgTwo = imgList.get(1) ;
            imgThree = imgList.get(2) ;
        }else if(imgList.size() == 2){
            imgOne = imgList.get(0) ;
            imgTwo = imgList.get(1) ;
        }else if(imgList.size() == 1){
            imgOne = imgList.get(0) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("NeedId" ,mNeedId) ;
        paramMap.put("OrderAmount" ,money) ;
        paramMap.put("WorkDescribe" ,content) ;
        paramMap.put("WorkImage1" ,imgOne) ;
        paramMap.put("WorkImage2" ,imgTwo) ;
        paramMap.put("WorkImage3" ,imgThree) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_OFFER_SEND_PAY, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("submitSendPay",mContext ,NeedsBondPayResult.class){
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        loadingDialog.dismiss() ;
                        ToastUtils.showToast(mContext ,"提交成功");
                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss() ;

                        ToastUtils.showToast(mContext ,"请求失败，请重试");
                    }
                });
    }


    /**
     * 删除图片
     */
    private void deletePictureByType(int index){
        switch (index){
            case 0:
                mPicUrlLocatOne = null ;
                mPicUrlServiceOne = null ;
                mPicOneDeleteIv.setVisibility(View.GONE);
                mPicOneIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_add_picture)) ;
                break;
            case 1:
                mPicUrlLocatTwo = null ;
                mPicUrlServiceTwo = null ;
                mPicTwoDeleteIv.setVisibility(View.GONE);
                mPicTwoIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_add_picture)) ;
                break;
            case 2:
                mPicUrlLocatThree = null ;
                mPicUrlServiceThree = null ;
                mPicThreeDeleteIv.setVisibility(View.GONE);
                mPicThreeIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_add_picture)) ;
                break;
            default:
                break;
        }
    }

    /**
     * 选择证书
     */
    private void choosePictureByType(int index){
        mCurPictureIndex = index ;
        checkPermission() ;
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    100, perms);
        }
    }

    private void goCheckPhoto() {
        if (imageConfig == null)
            imageConfig = new ImageConfig.Builder(new GlideImageLoader())
                    .steepToolBarColor(Color.BLACK)
                    .titleBgColor(Color.BLACK)
                    .titleSubmitTextColor(getResources().getColor(R.color.white))
                    .titleTextColor(getResources().getColor(R.color.white))
                    // 关闭多选   （默认为多选）
                    .singleSelect()
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();
        ImageSelector.open(mActivity, imageConfig);
    }

    /***
     * 上传图片
     */
    private void requestUploadPhoto(String imgUrl, final int index) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setLoadingText("正在上传");
        loadingDialog.show();

        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        String base64Image = FileUtil.getUploadImageBase64String(imgUrl);
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
                        if (loadingDialog != null && loadingDialog.isShowing())
                            loadingDialog.dismiss();

                        LogUtils.showLog("上传图片成功json>>>" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("type");
                            if (code == 1) {
                                String url = jsonObject.optString("resultdata");

                                switch (index){
                                    case 0:
                                        mPicUrlServiceOne = url ;

                                        if(mPicUrlLocatTwo != null && mPicUrlServiceTwo == null){
                                            requestUploadPhoto(mPicUrlLocatTwo ,1) ;
                                        }
                                        break;
                                    case 1:
                                        mPicUrlServiceTwo = url ;

                                        if(mPicUrlLocatThree != null && mPicUrlServiceThree == null){
                                            requestUploadPhoto(mPicUrlLocatThree ,2) ;
                                        }
                                        break;
                                    case 2:
                                        mPicUrlServiceThree = url ;
                                        break;
                                    default:
                                        break;
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
        goCheckPhoto() ;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 100) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode){
            if (resultCode == RESULT_OK && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                if(pathList != null && pathList.size() > 0){
                    String imgUrl = pathList.get(0) ;
                    switch (mCurPictureIndex){
                        case 0:
                            mPicUrlLocatOne = imgUrl ;
                            mPicOneDeleteIv.setVisibility(View.VISIBLE) ;
                            GlideUtils.loader(MyApplication.getInstance() ,imgUrl ,mPicOneIv) ;
                            break;
                        case 1:
                            mPicUrlLocatTwo = imgUrl ;
                            mPicTwoDeleteIv.setVisibility(View.VISIBLE) ;
                            GlideUtils.loader(MyApplication.getInstance() ,imgUrl ,mPicTwoIv) ;
                            break;
                        case 2:
                            mPicUrlLocatThree = imgUrl ;
                            mPicThreeDeleteIv.setVisibility(View.VISIBLE) ;
                            GlideUtils.loader(MyApplication.getInstance() ,imgUrl ,mPicThreeIv) ;
                            break;
                        default:
                            break;
                    }

                    requestUploadPhoto(imgUrl, mCurPictureIndex);
                }
            }
        }
    }


}
