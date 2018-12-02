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
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.PersonApplyInfo;
import cn.idcby.jiajubang.Bean.ResultBean;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人认证
 *
 * 2018-05-05 16:47:30
 * 从个人资料里面把 行业 和 职位 移动到个人认证，行业使用和店铺认证一个类型（商品类型，一级多选）
 *
 * 2018-05-30 16:12:06
 * 实名认证，只留 姓名 身份证号 身份证照片 三个内容
 */
public class PersonApplyActivity extends BaseMoreStatusActivity
        implements EasyPermissions.PermissionCallbacks {

    private final int PRC_PHOTO_PICKER = 300;
    private final int PHOTO_MAX_SIZE = 1;
    private final int REQUEST_CODE_FOR_ID_CARD_FRONT = 9987;
    private final int REQUEST_CODE_FOR_ID_CARD_BACK = 9989;
    private final int REQUEST_CODE_FOR_ID_CARD_WITH_HAND = 9990;
    private final int REQUEST_CODE_FOR_CATEGORY = 1000;

    private int uploadFlag = 0;
    private String idCardFrontUrl = "";
    private String idCardBackUrl = "";
    private String idCardWithHandUrl = "";

    private LoadingDialog loadingDialog;

    private LinearLayout mLLIdCardFront;
    private ImageView mImgIdCardFront;
    private LinearLayout mLLIdCardBack;
    private ImageView mImgIdCardBack;
    private LinearLayout mLLIdCardWithHand;
    private ImageView mImgIdCardWithHand;
    private EditText mTilNameEv;
    private EditText mTilIdCardEv;
    private EditText mTilAlipayEv;
    private EditText mWorkNameEv;
    private TextView mWorkTypeTv ;
    private TextView mTvApply;

    private boolean mIsHasChild = false ;
    private boolean mIsMoreCheck = true ;
    private String mCategoryIds = "";
    private ArrayList<UnusedCategory> mSelectedCategory = new ArrayList<>();

    private PersonApplyInfo mApplyInfo ;

    @Override
    public void requestData() {
        showSuccessPage();
        requestApplyInfo();
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_person_apply;
    }

    @Override
    public String setTitle() {
        return getResources().getString(R.string.apply_person_str);
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
        mLLIdCardFront = findViewById(R.id.ll_id_card_front);
        mImgIdCardFront = findViewById(R.id.img_id_card_front);
        mLLIdCardBack = findViewById(R.id.ll_id_card_back);
        mImgIdCardBack = findViewById(R.id.img_id_card_back);
        mLLIdCardWithHand = findViewById(R.id.ll_id_card_with_hand);
        mImgIdCardWithHand = findViewById(R.id.img_id_card_with_hand);
        mTilNameEv = findViewById(R.id.acti_person_apply_name_ev);
        mTilIdCardEv = findViewById(R.id.acti_person_apply_card_ev);
        mTilAlipayEv = findViewById(R.id.acti_person_apply_alipay_ev);
        mWorkNameEv = findViewById(R.id.acti_person_apply_work_name_ev);
        mWorkTypeTv = findViewById(R.id.acti_person_apply_category_tv);
        mTvApply = findViewById(R.id.tv_apply);
    }

    private void initListenner() {
        mLLIdCardFront.setOnClickListener(this);
        mLLIdCardBack.setOnClickListener(this);
        mLLIdCardWithHand.setOnClickListener(this);
        mTvApply.setOnClickListener(this);
        mWorkTypeTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.ll_id_card_front) {
            checkPhoto(REQUEST_CODE_FOR_ID_CARD_FRONT);
        } else if (i == R.id.ll_id_card_back) {
            checkPhoto(REQUEST_CODE_FOR_ID_CARD_BACK);
        } else if (i == R.id.ll_id_card_with_hand) {
            checkPhoto(REQUEST_CODE_FOR_ID_CARD_WITH_HAND);
        } else if (i == R.id.acti_person_apply_category_tv) {
            ChooseUnuesdCategoryActivity.launch(mActivity ,mIsHasChild ,mIsMoreCheck
                    , mSelectedCategory,REQUEST_CODE_FOR_CATEGORY);

        } else if (i == R.id.tv_apply) {
            requstPersonApply();
        }
    }

    private void checkPhoto(int requestCode) {
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
                    .maxChooseCount(PHOTO_MAX_SIZE)
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOR_ID_CARD_FRONT
                || requestCode == REQUEST_CODE_FOR_ID_CARD_BACK
                || requestCode == REQUEST_CODE_FOR_ID_CARD_WITH_HAND) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                uploadPhoto(photos.get(0));
            }
        }else if (REQUEST_CODE_FOR_CATEGORY == requestCode) {
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

                    mWorkTypeTv.setText(title);
                }else{
                    mWorkTypeTv.setText("请选择") ;
                }
            }
        }

    }

    private void uploadPhoto(String photoLocalPath) {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
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
                        if (uploadFlag == REQUEST_CODE_FOR_ID_CARD_FRONT) {
                            GlideUtils.loader(mContext, bean, mImgIdCardFront);
                            idCardFrontUrl = bean;
                        } else if (uploadFlag == REQUEST_CODE_FOR_ID_CARD_BACK) {
                            GlideUtils.loader(mContext, bean, mImgIdCardBack);
                            idCardBackUrl = bean;
                        } else if (uploadFlag == REQUEST_CODE_FOR_ID_CARD_WITH_HAND) {
                            GlideUtils.loader(mContext, bean, mImgIdCardWithHand);
                            idCardWithHandUrl = bean;
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

    private void requestApplyInfo() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_PERSON_APPLY_INFO, false, para,
                new RequestObjectCallBack<PersonApplyInfo>("获取个人认证信息", mContext,
                        PersonApplyInfo.class) {
                    @Override
                    public void onSuccessResult(PersonApplyInfo bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (bean != null)
                            updateUI(bean);
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

    private void updateUI(PersonApplyInfo bean) {
        mTvApply.setText(mContext.getResources().getString(R.string.apply_modify_text)) ;

        mApplyInfo = bean ;

        mCategoryIds = bean.getIndustryIds();
        if(!"".equals(mCategoryIds)){
            mWorkTypeTv.setText(bean.getIndustryNames());
        }

        idCardFrontUrl = bean.getIdPositiveImg() ;
        idCardBackUrl = bean.getIdOppositeImg() ;
        idCardWithHandUrl = bean.getHoldIDImg() ;

        GlideUtils.loader(mContext,idCardFrontUrl, mImgIdCardFront);
        GlideUtils.loader(mContext, idCardBackUrl, mImgIdCardBack);
        GlideUtils.loader(mContext, idCardWithHandUrl, mImgIdCardWithHand);
        mTilNameEv.setText(bean.getName());
        mTilAlipayEv.setText(bean.getAliNumber());
        mTilIdCardEv.setText(bean.getiDNumber());
        mWorkNameEv.setText(bean.getPostText());
    }

    private void requstPersonApply() {
        String name = mTilNameEv.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mTilNameEv.setText("");
            mTilNameEv.requestFocus() ;
            ToastUtils.showToast(mContext ,"请输入姓名");
            return;
        }

        String idCard = mTilIdCardEv.getText().toString().trim();
        if (!MyUtils.isRigghtIDCard(idCard)) {
            mTilIdCardEv.requestFocus() ;
            mTilIdCardEv.setSelection(idCard.length());
            ToastUtils.showToast(mContext ,"请输入正确的身份证号");
            return;
        }

//        String alipay = mTilAlipayEv.getText().toString().trim();


//        if (TextUtils.isEmpty(alipay)) {
//            mTilAlipayEv.setText("");
//            mTilAlipayEv.requestFocus() ;
//            ToastUtils.showToast(mContext ,"请输入支付宝账号");
//            return;
//        }

//        if("".equals(StringUtils.convertNull(mCategoryIds))){
//            ToastUtils.showToast(mContext, "请选择行业");
//           return ;
//        }

        if (TextUtils.isEmpty(idCardFrontUrl)) {
            ToastUtils.showErrorToast(mContext, "请上传身份证正面照片");
            return;
        }

        if (TextUtils.isEmpty(idCardBackUrl)) {
            ToastUtils.showToast(mContext, "请上传身份证反面照片");
            return;
        }

        if (TextUtils.isEmpty(idCardWithHandUrl)) {
            ToastUtils.showToast(mContext, "请上传手持身份证照片");
            return;
        }

//        String postName = mWorkNameEv.getText().toString().trim() ;

        boolean isNoChange = true ;
        if(null == mApplyInfo || !name.equals(mApplyInfo.getName())
                || !idCard.equals(mApplyInfo.getiDNumber())
//                || !alipay.equals(mApplyInfo.getAliNumber())
//                || !mApplyInfo.getIndustryIds().equals(mCategoryIds)
//                || !postName.equals(mApplyInfo.getPostText())
                || !StringUtils.convertNull(idCardFrontUrl).equals(mApplyInfo.getIdPositiveImg())
                || !StringUtils.convertNull(idCardBackUrl).equals(mApplyInfo.getIdOppositeImg())
                || !StringUtils.convertNull(idCardWithHandUrl).equals(mApplyInfo.getHoldIDImg())){
            isNoChange = false ;
        }

        final Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getPersonalAuthenticationId() : "");
        para.put("Name", name);
        para.put("IDNumber", idCard);
//        para.put("AliNumber", alipay);
//        para.put("IndustryIds", StringUtils.convertNull(mCategoryIds));
//        para.put("PostText", postName);
        para.put("IDPositiveImg", idCardFrontUrl);
        para.put("IDOppositeImg", idCardBackUrl);
        para.put("HoldIDImg", idCardWithHandUrl);

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

        NetUtils.getDataFromServerByPost(mContext, Urls.PERSON_APPLY, false, para,
                new RequestObjectCallBack<ResultBean>("个人认证申请", mContext, ResultBean.class) {
                    @Override
                    public void onSuccessResult(ResultBean bean) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("上传图片") ;

    }
}