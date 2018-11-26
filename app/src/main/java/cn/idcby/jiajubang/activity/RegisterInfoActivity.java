package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.SiftWorkPost;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.dialog.DoubleSelectionDialog;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人信息
 * Created on 2018/4/9.
 * <p>
 * 2018-05-05 16:46:04
 * 去掉姓名、行业、类型、职位，所有内容非必填
 * <p>
 * 2018-05-30 16:15:06
 * 添加 行业 公司名称 职位 位置
 * <p>
 * 2018-07-31 17:26:25
 * 个性签名非必填
 * <p>
 * 2018-09-15 17:07:01
 * 昵称限制10个字符
 */

public class RegisterInfoActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private ImageView mUserIv;
    private EditText mNickNameEv;
    private TextView mSexTv;
    private TextView mBirthdayTv;
    private EditText mQQEv;
    private EditText mWeChatEv;
    private EditText mEmailEv;
    private TextView mAreaTv;
    private EditText mDescEv;
    private EditText mWorkNameEv;
    private TextView mWorkTypeTv;
    private EditText mCompanyNameEv;
    private TextView mSubmitTv;
    private TextView tvWorkName;

    private LoadingDialog mDialog;

    private String mHeadUrl;
    private int mSex = 0;
    private String mBirthday;

    private String mProvinceId;
    private String mCityId;
    private String mAreaId;

    private UserInfo mUserInfo;

    private boolean mIsHasChild = false;
    private boolean mIsMoreCheck = true;
    private String mCategoryIds = "";
    private ArrayList<UnusedCategory> mSelectedCategory = new ArrayList<>();


    private Dialog mSexDialog;
    private DialogDatePicker dialogDatePicker;//选择年月日

    private static final int REQUEST_CODE_AREA = 1001;
    private static final int REQUEST_CODE_IMAGE = 1004;
    private static final int REQUEST_CODE_FOR_CATEGORY = 1000;

    private ImageConfig imageConfig;


    @Override
    public int getLayoutID() {
        return R.layout.activity_register_my_info;
    }

    @Override
    public void initView() {
//        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        mUserInfo = (UserInfo) getIntent().getSerializableExtra(SkipUtils.INTENT_USER_INFO);
        View statusView = findViewById(R.id.view_status);
        statusView.getLayoutParams().height = ResourceUtils.getStatusBarHeight(mContext);
//        View topLay = findViewById(R.id.acti_user_info_head_lay) ;
//        topLay.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext) / 2.1F);

        mUserIv = findViewById(R.id.acti_user_info_head_iv);
        mNickNameEv = findViewById(R.id.acti_user_info_nickName_ev);
        mSexTv = findViewById(R.id.acti_user_info_sex_tv);
        mBirthdayTv = findViewById(R.id.acti_user_info_birthday_tv);
        mQQEv = findViewById(R.id.acti_user_info_qq_ev);
        mWeChatEv = findViewById(R.id.acti_user_info_weChat_ev);
        mEmailEv = findViewById(R.id.acti_user_info_email_ev);
        mAreaTv = findViewById(R.id.acti_user_info_area_tv);
        mDescEv = findViewById(R.id.acti_user_info_desc_ev);

        mWorkNameEv = findViewById(R.id.acti_user_info_work_name_ev);
        mWorkTypeTv = findViewById(R.id.acti_user_info_category_tv);
        mCompanyNameEv = findViewById(R.id.acti_user_info_company_name_ev);
        mSubmitTv = findViewById(R.id.acti_user_info_submit_tv);
        tvWorkName = findViewById(R.id.acti_user_info_work_name_tv);
        tvWorkName.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getUserInfo();
    }

    @Override
    public void initListener() {
        mUserIv.setOnClickListener(this);
        mSexTv.setOnClickListener(this);
        mBirthdayTv.setOnClickListener(this);
        mAreaTv.setOnClickListener(this);
        mWorkTypeTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_user_info_sex_tv == vId) {
            showSexDialog();
        } else if (R.id.acti_user_info_head_iv == vId) {
            checkPhoto();
        } else if (R.id.acti_user_info_birthday_tv == vId) {
            datePicker("选择出生日期", mBirthdayTv);
        } else if (R.id.acti_user_info_area_tv == vId) {//区域
            SelectedProvinceActivity.launch(mActivity, REQUEST_CODE_AREA);
        } else if (vId == R.id.acti_user_info_category_tv) {
            ChooseUnuesdCategoryActivity.launch(mActivity, mIsHasChild, mIsMoreCheck
                    , mSelectedCategory, REQUEST_CODE_FOR_CATEGORY);

        } else if (R.id.acti_user_info_submit_tv == vId) {//提交
            submitModify();
        } else if (R.id.acti_user_info_work_name_tv == vId) {
            showWordDialog();
        }
    }

    private DoubleSelectionDialog workDialog;

    private void showWordDialog() {
        if (workDialog == null) {
            workDialog = new DoubleSelectionDialog(this);
            workDialog.getFirstData();
            workDialog.setDoubleSelectionInterface(new DoubleSelectionInterface() {
                @Override
                public void onSelection(Object post) {
                    if (!(post instanceof SiftWorkPost)) {
                        return;
                    }
                    SiftWorkPost workPost = (SiftWorkPost) post;
                    tvWorkName.setText(workPost.getName());
                }
            });
        }

        workDialog.show();
    }

    /**
     * 填充数据
     */
    private void updateDisplay() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (null == mUserInfo) {
            DialogUtils.showCustomViewDialog(mContext, "获取个人信息失败"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            return;
        }

        LoginHelper.saveUserInfoToLocal(mContext, mUserInfo);

        mHeadUrl = mUserInfo.getHeadIcon();
        String nickName = mUserInfo.getNickName();
        String gender = mUserInfo.getGender();//1男2女
        if ("2".equals(gender)) {
            mSex = 2;
        } else if ("1".equals(gender)) {
            mSex = 1;
        }
        mBirthday = mUserInfo.getBirthday();
        String qq = mUserInfo.getOICQ();
        String weChat = mUserInfo.getWeChat();
        String email = mUserInfo.getEmail();
        String areaProvince = mUserInfo.getProvinceName();
        String areaCity = mUserInfo.getCityName();
        String areaQu = mUserInfo.getCountyName();
        mProvinceId = mUserInfo.getProvinceId();
        mCityId = mUserInfo.getCityId();
        mAreaId = mUserInfo.getCountyId();

        mCategoryIds = mUserInfo.getIndustryIds();
        if (!"".equals(mCategoryIds)) {
            mWorkTypeTv.setText(mUserInfo.getIndustryNames());
        }
        mWorkNameEv.setText(mUserInfo.getPostText());
        mCompanyNameEv.setText(mUserInfo.getCompanyName());

        String desc = mUserInfo.getPersonalitySignature();

        GlideUtils.loaderRound(StringUtils.convertNull(mHeadUrl), mUserIv, 3);
        mNickNameEv.setText(StringUtils.convertNull(nickName));
        mSexTv.setText(1 == mSex ? "男" : (2 == mSex ? "女" : "请选择"));
        mBirthdayTv.setText("".equals(StringUtils.convertNull(mBirthday)) ? "请选择" : mBirthday);
        mQQEv.setText(StringUtils.convertNull(qq));
        mWeChatEv.setText(StringUtils.convertNull(weChat));
        mEmailEv.setText(StringUtils.convertNull(email));
        if ("".equals(StringUtils.convertNull(mProvinceId))
                || "".equals(StringUtils.convertNull(mCityId))) {
            mAreaTv.setText("请选择");
        } else {
            mAreaTv.setText(StringUtils.convertNull(areaProvince)
                    + StringUtils.convertNull(areaCity)
                    + StringUtils.convertNull(areaQu));
        }
        mDescEv.setText(StringUtils.convertNull(desc));

        mNickNameEv.requestFocus();
        mNickNameEv.setSelection(mNickNameEv.getText().length());
    }

    /**
     * 选择性别
     */
    private void showSexDialog() {
        if (null == mSexDialog) {
            mSexDialog = new Dialog(mContext, cn.idcby.commonlibrary.R.style.my_custom_dialog);
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_check_sex, null);
            mSexDialog.setContentView(v);

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.6F);

            TextView sexM = v.findViewById(R.id.dialog_check_sex_man_tv);
            TextView sexW = v.findViewById(R.id.dialog_check_sex_women_tv);

            sexM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSex = 1;
                    mSexTv.setText("男");
                    mSexDialog.dismiss();
                }
            });
            sexW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSex = 2;
                    mSexTv.setText("女");
                    mSexDialog.dismiss();
                }
            });
        }

        mSexDialog.show();
    }


    //日期选择器
    private void datePicker(String str, final TextView view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(this, false);
        dialogDatePicker.setTitle(str);
        dialogDatePicker.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(true);
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化时间
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sDateFormat.format(new java.util.Date());
                if (DateCompareUtils.compareDay(currentDate, dialogDatePicker.getDate())) {
                    view.setEnabled(true);
                    view.setText(dialogDatePicker.getDate());
                    mBirthday = dialogDatePicker.getDate();
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "出生日期不能大于当前时间");
                    return;
                }
            }
        });
        dialogDatePicker.show();
    }

    private void checkPhoto() {
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
                    .singleSelect()
                    .crop(1, 1, ResourceUtils.dip2px(mContext, 100), ResourceUtils.dip2px(mContext, 100))
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_IMAGE)
                    .build();

        ImageSelector.open(mActivity, imageConfig);
    }

    /**
     * 提交修改
     */
    private void submitModify() {
        if ("".equals(StringUtils.convertNull(mHeadUrl))) {
            ToastUtils.showToast(mContext, "请先上传头像");
            return;
        }

        String nickName = mNickNameEv.getText().toString().trim();
        if ("".equals(nickName)) {
            ToastUtils.showToast(mContext, "昵称不能为空");
            mNickNameEv.setText("");
            mNickNameEv.requestFocus();
            return;
        }

        int nickNameLength = nickName.length();
        if (nickNameLength > 10) {
            ToastUtils.showToast(mContext, "昵称最多10个字符");
            mNickNameEv.requestFocus();
            mNickNameEv.setSelection(nickNameLength);
            return;
        }

        if ("".equals(mBirthday)) {
            ToastUtils.showToast(mContext, "请选择生日");
            return;
        }

        if (0 == mSex) {
            ToastUtils.showToast(mContext, "请选择性别");
            return;
        }

        if (TextUtils.isEmpty(mProvinceId) || TextUtils.isEmpty(mCityId)) {
            ToastUtils.showToast(mContext, "请选择位置");
            return;
        }

        if (TextUtils.isEmpty(mCategoryIds)) {
            ToastUtils.showToast(mContext, "请选择行业类别");
            return;
        }

        String postName = mWorkNameEv.getText().toString().trim();
        if ("".equals(postName)) {
            ToastUtils.showToast(mContext, "职位不能为空");
            mWorkNameEv.setText("");
            mWorkNameEv.requestFocus();
            return;
        }

        String desc = mDescEv.getText().toString().trim();

        String qq = mQQEv.getText().toString().trim();
        String weChat = mWeChatEv.getText().toString().trim();
        String email = mEmailEv.getText().toString().trim();
        String companyName = mCompanyNameEv.getText().toString().trim();

        if (mDialog == null) {
            mDialog = new LoadingDialog(mContext);
        }
        mDialog.show();


        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Gender", "" + mSex);
        paramMap.put("Birthday", StringUtils.convertNull(mBirthday));
        paramMap.put("HeadIcon", StringUtils.convertNull(mHeadUrl));
        paramMap.put("NickName", nickName);
        paramMap.put("OICQ", qq);
        paramMap.put("WeChat", weChat);
        paramMap.put("Email", email);
        paramMap.put("ProvinceId", StringUtils.convertNull(mProvinceId));
        paramMap.put("CityId", StringUtils.convertNull(mCityId));
        paramMap.put("CountyId", StringUtils.convertNull(mAreaId));
        paramMap.put("PersonalitySignature", desc);
        paramMap.put("IndustryIds", StringUtils.convertNull(mCategoryIds));
        paramMap.put("PostText", postName);
        paramMap.put("CompanyName", companyName);

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO_UPDATE, paramMap
                , new RequestObjectCallBack<String>("submitModify", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }

                        ToastUtils.showToast(mContext, "修改成功");
                        if (!mActivity.isFinishing()) {
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }

                        DialogUtils.showCustomViewDialog(mContext, "温馨提示", "修改失败", null
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }

                        DialogUtils.showCustomViewDialog(mContext, "温馨提示", "修改失败", null
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
     * 获取信息
     */
    private void getUserInfo() {
        if (null == mDialog) {
            mDialog = new LoadingDialog(mContext);
        }
        mDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo", mContext, UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if (bean != null) {
                            mUserInfo = bean;
                        }
                        updateDisplay();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay();
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateDisplay();
                    }
                });
    }

    /**
     * 上传头像
     *
     * @param photoLocalPath localPath
     */
    private void uploadPhoto(String photoLocalPath) {
        if (mDialog == null)
            mDialog = new LoadingDialog(mContext);
        mDialog.show();

        LogUtils.showLog("上传图片的路径>>>" + photoLocalPath);
        Map<String, String> para = ParaUtils.getPara(mContext);

        String base64Image = FileUtil.getUploadImageBase64String(photoLocalPath);
        if (null == base64Image) {
            if (mDialog != null)
                mDialog.dismiss();

            ToastUtils.showToast(mContext, "图片上传失败");
            return;
        }
        para.put("Base64Image", base64Image);
        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new RequestObjectCallBack<String>("上传图片", mContext, String.class) {

                    @Override
                    public void onSuccessResult(String bean) {
                        if (mDialog != null)
                            mDialog.dismiss();

                        GlideUtils.loaderUser(bean, mUserIv);
                        mHeadUrl = bean;

                        ToastUtils.showToast(mContext, "图片上传成功");
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mDialog != null)
                            mDialog.dismiss();

                        ToastUtils.showToast(mContext, "图片上传失败");
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mDialog != null)
                            mDialog.dismiss();

                        ToastUtils.showToast(mContext, "图片上传失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_AREA == requestCode) {
            if (resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                mAreaId = data.getStringExtra("areaId");
                String provinceName = data.getStringExtra("provinceName");
                String cityName = data.getStringExtra("cityName");
                String areaName = data.getStringExtra("areaName");

                mAreaTv.setText(provinceName + cityName + areaName);
            }
        } else if (REQUEST_CODE_IMAGE == requestCode) {//头像
            if (RESULT_OK == resultCode && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                if (pathList != null && pathList.size() > 0) {
                    uploadPhoto(pathList.get(0));
                }
            }
        } else if (REQUEST_CODE_FOR_CATEGORY == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                List<UnusedCategory> serverCategory = (ArrayList<UnusedCategory>)
                        data.getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
                mSelectedCategory.clear();
                mCategoryIds = "";

                if (serverCategory != null && serverCategory.size() > 0) {
                    mSelectedCategory.addAll(serverCategory);

                    List<UnusedCategory> titleCategory = new ArrayList<>();
                    for (UnusedCategory category : mSelectedCategory) {
                        if (mIsHasChild) {
                            titleCategory.addAll(category.getSelectedCategory());
                        } else {
                            titleCategory.add(category);
                        }
                    }

                    String title = "";
                    for (UnusedCategory category : titleCategory) {
                        title += (category.getCategoryTitle() + ",");
                        mCategoryIds += (category.getCategoryID() + ",");
                    }
                    if (title.length() > 0) {
                        title = title.substring(0, title.length() - 1);
                    }
                    if (mCategoryIds.length() > 0) {
                        mCategoryIds = mCategoryIds.substring(0, mCategoryIds.length() - 1);
                    }

                    mWorkTypeTv.setText(title);
                } else {
                    mWorkTypeTv.setText("请选择");
                }
            }
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
        checkPhoto();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 100) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

}
