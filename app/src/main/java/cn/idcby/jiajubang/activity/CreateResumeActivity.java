package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.JobPost;
import cn.idcby.jiajubang.Bean.ResumeDetails;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.Bean.WorkExperience;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterWorkExpEdit;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.StationaryListView;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建简历
 * Created on 2018/3/26.
 *
 * 2018-04-26 10:57:37
 * 客户要求：手机2为非必填项
 *
 * 2018-05-09 16:03:08
 * 调整薪资、工作年限
 */

public class CreateResumeActivity extends BaseMoreStatusActivity implements EasyPermissions.PermissionCallbacks{
    private EditText mNameEv ;
    private TextView mSexTv ;
    private TextView mBirthTv ;
    private EditText mHeightEv ;
    private EditText mWeightEv ;
    private TextView mEduTv ;
    private EditText mAddressEv ;
    private TextView mWorkNameTv ;
    private TextView mWorkLocaTv ;
    private ImageView mWorkTypeAllIv ;
    private ImageView mWorkTypeHalfIv ;
    private TextView mWorkMoneyNoTv ;
    private ImageView mWorkMoneyNoIv ;//面议
    private View mWorkMoneyEditLay ;
    private EditText mWorkMoneyStartEv ;
    private EditText mWorkMoneyEndEv ;
    private TextView mWorkYearTv;
    private EditText mPhoneOneEv ;
    private EditText mPhoneTwoEv ;
    private EditText mWechatEv ;
    private EditText mQQEv ;
    private EditText mEmailEv ;
    private ImageView mAddWorkExpIv ;
    private StationaryListView mWorkExpLv ;
    private EditText mSelfComEv ;
    private RecyclerView mPicLay ;


    private Dialog mSexDialog ;
    private DialogDatePicker dialogDatePicker;//选择年月日

    private ResumeDetails mDetails ;
    private String mResumeId ;

    private int mSex = 0;//性别  1是男 2是女
    private String birthday;//出生日期
    private String mEduName ;
    private String mWorkPostId;
    private String mProvinceId ;
    private String mCityId ;
    private String mAreaId ;
    private int mWorkType = 1 ;// 1 全职 2 兼职
    private String mWorkYearName = "" ;//工作年限
    private boolean mIsMoneyNo = false ;//是否薪资面议

    private String mWorkExpIds = "" ;
    private String mThumbIds = null;
    private List<WorkExperience> mWorkExpList = new ArrayList<>() ;
    private AdapterWorkExpEdit mWorkExpAdapter ;

    private final static int REQUEST_CODE_FOR_EDU = 1001;
    private final static int REQUEST_CODE_FOR_JOB = 1002;
    private final static int REQUEST_CODE_FOR_EXP = 1003;
    private final static int REQUEST_CODE_FOR_AREA = 1004;
    private final static int REQUEST_CODE_FOR_WORK_YEAR = 1005;

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
    private final static int MAX_IMAGE_COUNT = 4 ;

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
        if(!"".equals(StringUtils.convertNull(mResumeId))){
            getResumeDetails() ;
        }else{
            showSuccessPage();
        }
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_create_resume;
    }

    @Override
    public String setTitle() {
        return "创建简历";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mResumeId =getIntent().getStringExtra(SkipUtils.INTENT_RESUME_ID) ;

        loadingDialog = new LoadingDialog(mContext) ;

        mNameEv = findViewById(R.id.acti_create_resume_name_ev) ;
        mSexTv = findViewById(R.id.acti_create_resume_sex_tv) ;
        mBirthTv = findViewById(R.id.acti_create_resume_birthday_tv) ;
        mHeightEv = findViewById(R.id.acti_create_resume_height_ev) ;
        mWeightEv = findViewById(R.id.acti_create_resume_weight_ev) ;
        mEduTv = findViewById(R.id.acti_create_resume_education_tv) ;
        mAddressEv = findViewById(R.id.acti_create_resume_addrress_ev) ;
        mWorkNameTv = findViewById(R.id.acti_create_resume_job_tv) ;
        mWorkLocaTv = findViewById(R.id.acti_create_resume_expect_location_tv) ;
        LinearLayout mWorkTypeHalfLay = findViewById(R.id.acti_create_resume_type_half_lay) ;
        mWorkTypeHalfIv = findViewById(R.id.acti_create_resume_type_half_iv) ;
        LinearLayout mWorkTypeAllLay = findViewById(R.id.acti_create_resume_type_all_lay) ;
        mWorkTypeAllIv = findViewById(R.id.acti_create_resume_type_all_iv) ;
        mWorkMoneyEditLay = findViewById(R.id.acti_create_resume_money_edit_lay) ;
        mWorkMoneyStartEv = findViewById(R.id.acti_create_resume_money_start_ev) ;
        mWorkMoneyEndEv = findViewById(R.id.acti_create_resume_money_end_ev) ;
        mWorkMoneyNoTv = findViewById(R.id.acti_create_resume_money_no_tv) ;
        View mWorkMoneyNoLay = findViewById(R.id.acti_create_resume_money_check_lay) ;
        mWorkMoneyNoIv = findViewById(R.id.acti_create_resume_money_check_iv) ;
        mWorkYearTv = findViewById(R.id.acti_create_resume_year_tv) ;
        mPhoneOneEv = findViewById(R.id.acti_create_resume_phone1_ev) ;
        mPhoneTwoEv = findViewById(R.id.acti_create_resume_phone2_ev) ;
        mWechatEv = findViewById(R.id.acti_create_resume_wechat_ev) ;
        mQQEv = findViewById(R.id.acti_create_resume_qq_ev) ;
        mEmailEv = findViewById(R.id.acti_create_resume_email_ev) ;
        mAddWorkExpIv = findViewById(R.id.acti_create_resume_work_exp_add_iv) ;
        mWorkExpLv = findViewById(R.id.acti_create_resume_work_exp_lv) ;

        mSelfComEv = findViewById(R.id.acti_create_resume_self_com_ev) ;
        mPicLay = findViewById(R.id.acti_create_resume_pic_lay) ;

        TextView mSubTv = findViewById(R.id.acti_create_resume_sub_tv) ;

        mSubTv.setOnClickListener(this) ;
        mWorkMoneyNoLay.setOnClickListener(this);

        mAddWorkExpIv.setOnClickListener(this) ;
        mWorkTypeAllLay.setOnClickListener(this) ;
        mWorkTypeHalfLay.setOnClickListener(this) ;
        mEduTv.setOnClickListener(this) ;
        mWorkLocaTv.setOnClickListener(this) ;
        mWorkNameTv.setOnClickListener(this) ;
        mBirthTv.setOnClickListener(this) ;
        mSexTv.setOnClickListener(this) ;
        mWorkYearTv.setOnClickListener(this) ;

        mWorkExpAdapter = new AdapterWorkExpEdit(mContext , mWorkExpList) ;
        mWorkExpLv.setAdapter(mWorkExpAdapter) ;

        mNameEv.requestFocus() ;

        if(!"".equals(StringUtils.convertNull(mResumeId))){
            setTitleText("编辑简历");
            mSubTv.setText("提交修改");
        }

        initPhotoContainer() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_create_resume_sex_tv == vId){//性别
            showSexDialog() ;
        }else if(R.id.acti_create_resume_birthday_tv == vId){//生日
            datePicker("选择出生日期" , mBirthTv);
        }else if(R.id.acti_create_resume_job_tv == vId){//岗位
            Intent toJpIt = new Intent(CreateResumeActivity.this , JobPostListActivity.class) ;
            startActivityForResult(toJpIt , REQUEST_CODE_FOR_JOB) ;
        }else if(R.id.acti_create_resume_expect_location_tv == vId){//位置

            SelectedProvinceActivity.launch(mActivity,REQUEST_CODE_FOR_AREA);

        }else if(R.id.acti_create_resume_type_half_lay == vId){//兼职

            mWorkType = 2 ;
            changeWorkType() ;

        }else if(R.id.acti_create_resume_type_all_lay == vId){//全职

            mWorkType = 1 ;
            changeWorkType() ;

        }else if(R.id.acti_create_resume_work_exp_add_iv == vId){//添加工作经历
            Intent toWkIt = new Intent(mContext ,AddWorkExpActivity.class) ;
            startActivityForResult(toWkIt,REQUEST_CODE_FOR_EXP) ;
        }else if(R.id.acti_create_resume_year_tv == vId){//工作年限
            SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_WORK_YEAR, REQUEST_CODE_FOR_WORK_YEAR);
        }else if(R.id.acti_create_resume_money_check_lay == vId){//薪资面议
            mIsMoneyNo = !mIsMoneyNo ;
            changeWorkMoneyType() ;
        }else if(R.id.acti_create_resume_education_tv == vId){//学历
            SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_EDUCATION,REQUEST_CODE_FOR_EDU);
        }else if(R.id.acti_create_resume_sub_tv == vId){//提交
            submitNewResume() ;
        }
    }

    /**
     * 切换薪资类型：面议 or not
     */
    private void changeWorkMoneyType(){
        mWorkMoneyNoIv.setImageDrawable(getResources().getDrawable(mIsMoneyNo
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal)) ;
        mWorkMoneyEditLay.setVisibility(mIsMoneyNo ? View.GONE : View.VISIBLE);
        mWorkMoneyNoTv.setVisibility(mIsMoneyNo ? View.VISIBLE : View.GONE);
        if(mIsMoneyNo){
            mPhoneOneEv.requestFocus() ;
            mPhoneOneEv.setSelection(mPhoneOneEv.getText().length()) ;
        }
    }

    /**
     * 初始化图片选择
     */
    private void initPhotoContainer() {
        mAdapterImageList.add(null) ;//添加图片的那个图标

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        mPicLay.setLayoutManager(new GridLayoutManager(this, 5));
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


    /**
     * 更换工作类型
     */
    private void changeWorkType(){
        boolean isAll = 1 == mWorkType ;
        mWorkTypeAllIv.setImageDrawable(getResources().getDrawable(isAll
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal)) ;
        mWorkTypeHalfIv.setImageDrawable(getResources().getDrawable(!isAll
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal)) ;
    }

    /**
     * 选择性别
     */
    private void showSexDialog(){
        if(null == mSexDialog){
            mSexDialog = new Dialog(mContext ,cn.idcby.commonlibrary.R.style.my_custom_dialog) ;
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_check_sex , null) ;
            mSexDialog.setContentView(v) ;

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.6F);

            TextView sexM = v.findViewById(R.id.dialog_check_sex_man_tv) ;
            TextView sexW = v.findViewById(R.id.dialog_check_sex_women_tv) ;

            sexM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSex = 1 ;
                    mSexTv.setText("男");
                    mSexDialog.dismiss() ;
                }
            });
            sexW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSex = 2 ;
                    mSexTv.setText("女");
                    mSexDialog.dismiss() ;
                }
            });
        }

        mSexDialog.show() ;
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
                    birthday = dialogDatePicker.getDate();
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "出生日期不能大于当前时间");
                    return;
                }
            }
        });
        dialogDatePicker.show();
    }


    private void updateDetails(){
        showSuccessPage();

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "网络异常，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        mNameEv.setText(mDetails.getName());
        mSex = mDetails.getSexValue() ;
        if(1 == mSex){
            mSexTv.setText("男");
        }else if(2 == mSex){
            mSexTv.setText("女");
        }else{
            mSexTv.setText("请选择");
        }

        birthday = mDetails.getBirthday() ;
        if(!"".equals(birthday)){
            mBirthTv.setText(birthday) ;
        }

        mHeightEv.setText(mDetails.getHeight());
        mWeightEv.setText(mDetails.getWeight());

        mEduName = mDetails.getEducation() ;
        if(!"".equals(mEduName)){
            mEduTv.setText(mEduName);
        }

        mAddressEv.setText(mDetails.getNowAddress());

        mWorkPostId = mDetails.getWorkPostId() ;
        if(!"".equals(StringUtils.convertNull(mWorkPostId))){
            mWorkNameTv.setText(mDetails.getPname());
        }

        mProvinceId = mDetails.getHopeProvinceId() ;
        mCityId = mDetails.getHopeCityId() ;
        if(!"".equals(mProvinceId) && !"".equals(mCityId)){
            mWorkLocaTv.setText(mDetails.getHopeProvinceName() + mDetails.getHopeCityName());
        }

        mWorkType = "1".equals(mDetails.getWorkType()) ? 1 : 2 ;
        changeWorkType();

        //薪资
        mIsMoneyNo = mDetails.isFace() ;
        changeWorkMoneyType() ;
        if(!mIsMoneyNo){
            mWorkMoneyStartEv.setText(mDetails.getMinAmount());
            mWorkMoneyEndEv.setText(mDetails.getMaxAmount());
        }

        mWorkYearName = mDetails.getWorkYears() ;
        if(!"".equals(mWorkYearName)){
            mWorkYearTv.setText(mWorkYearName);
        }
        mPhoneOneEv.setText(mDetails.getPhone1());
        mPhoneTwoEv.setText(mDetails.getPhone2());
        mWechatEv.setText(mDetails.getWeChat());
        mQQEv.setText(mDetails.getQQ());
        mEmailEv.setText(mDetails.getEmail());

        mSelfComEv.setText(mDetails.getSelfEvaluation());

        List<WorkExperience> workExperienceList = mDetails.getExperienceList() ;
        mWorkExpList.addAll(workExperienceList) ;

        mWorkExpIds = "" ;
        for(WorkExperience ex : mWorkExpList){
            mWorkExpIds += (ex.getWorkExperienceId() +",") ;
        }
        mWorkExpAdapter.notifyDataSetChanged() ;

        mThumbIds = "" ;
        List<ImageThumb> thumbs = mDetails.getAlbumsList() ;
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

    /**
     * 获取职位详细
     */
    private void getResumeDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mResumeId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_DETAILS, paramMap
                , new RequestObjectCallBack<ResumeDetails>("getResumeDetails",mContext ,ResumeDetails.class) {
                    @Override
                    public void onSuccessResult(ResumeDetails bean) {
                        mDetails = bean ;
                        updateDetails() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDetails() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDetails();
                    }
                });
    }

    /**
     * 提交简历的创建
     */
    private void submitNewResume(){
        String name = mNameEv.getText().toString().trim() ;
        if("".equals(name)){
            mNameEv.setText("") ;
            mNameEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext , "请输入姓名");
            return;
        }

        if(0 == mSex){
            ToastUtils.showErrorToast(mContext , "请选择性别");
            return;
        }

        if("".equals(StringUtils.convertNull(mEduName))){
            ToastUtils.showErrorToast(mContext , "请选择学历");
            return ;
        }

        if("".equals(StringUtils.convertNull(mWorkPostId))){
            ToastUtils.showErrorToast(mContext , "请选择求职岗位");
            return ;
        }

        if("".equals(StringUtils.convertNull(mProvinceId))){
            ToastUtils.showErrorToast(mContext , "请选择期望工作位置");
            return ;
        }

        String phone1 = mPhoneOneEv.getText().toString().trim() ;
        if("".equals(phone1)){
            mPhoneOneEv.setText("") ;
            mPhoneOneEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext , "请输入手机号1");
            return;
        }

        String phone2 = mPhoneTwoEv.getText().toString().trim() ;
//        if("".equals(phone2)){
//            mPhoneTwoEv.setText("") ;
//            mPhoneTwoEv.requestFocus() ;
//            ToastUtils.showErrorToast(mContext , "请输入手机号2");
//            return;
//        }

        String workMoneyStart = mWorkMoneyStartEv.getText().toString().trim() ;
        if(!mIsMoneyNo && StringUtils.convertString2Float(workMoneyStart) < 0){
            ToastUtils.showToast(mContext ,"请输入正确的薪资");
            mWorkMoneyStartEv.requestFocus() ;
            mWorkMoneyStartEv.setSelection(mWorkMoneyStartEv.getText().length()) ;
            return ;
        }

        String workMoneyEnd = mWorkMoneyEndEv.getText().toString().trim() ;
        if(!mIsMoneyNo && StringUtils.convertString2Float(workMoneyEnd) < StringUtils.convertString2Float(workMoneyStart)){
            ToastUtils.showToast(mContext ,"请输入正确的薪资");
            mWorkMoneyEndEv.requestFocus() ;
            mWorkMoneyEndEv.setSelection(mWorkMoneyEndEv.getText().length()) ;
            return ;
        }

        String height = mHeightEv.getText().toString().trim() ;
        String weight = mWeightEv.getText().toString().trim() ;
        String address = mAddressEv.getText().toString().trim() ;
        String weChat = mWechatEv.getText().toString().trim() ;
        String qq = mQQEv.getText().toString().trim() ;
        String email = mEmailEv.getText().toString().trim() ;
        String selfCom = mSelfComEv.getText().toString().trim() ;

        String thumbList = "" ;
        if(imageUploadList.size() > 0){
            for(String imgurl : imageUploadList){
                thumbList += (imgurl + ",") ;
            }
            thumbList = thumbList.substring(0,thumbList.length() - 1) ;
        }

        String experienceIds = "" ;
        String experience = "" ;
        if(mWorkExpList.size() > 0){
            StringBuffer expBuf = new StringBuffer() ;
            for(WorkExperience exp : mWorkExpList){
                if(exp != null){
                    experienceIds += (exp.getWorkExperienceId() +",") ;

                    String BeginTime = exp.getBeginTime() ;
                    String OverTime = exp.getOverTime() ;
                    String CompanyName = exp.getCompanyName() ;
                    String PostName = exp.getPostName() ;
                    String Salary = exp.getSalary() ;

                    expBuf.append("{\"BeginTime\":\"").append(BeginTime).append("\",")
                            .append("\"OverTime\":\"").append(OverTime).append("\",")
                            .append("\"CompanyName\":\"").append(CompanyName).append("\",")
                            .append("\"PostName\":\"").append(PostName).append("\",")
                            .append("\"Salary\":\"").append(Salary).append("\"},") ;
                }
            }

            experience = expBuf.substring(0 , expBuf.length() - 1) ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ResumeId" , StringUtils.convertNull(mResumeId)) ;
        paramMap.put("WorkType" , "" + mWorkType) ;
        paramMap.put("Name" , name) ;
        paramMap.put("Sex" , "" + mSex) ;
        paramMap.put("Birthday" , StringUtils.convertNull(birthday)) ;
        paramMap.put("Height" , height) ;
        paramMap.put("Weight" , weight) ;
        paramMap.put("Education" , StringUtils.convertNull(mEduName)) ;//2018-04-27 15:53:19 把选中的学历的 ItemName 给后台
        paramMap.put("NowAddress" , address) ;
        paramMap.put("WorkPostId" , StringUtils.convertNull(mWorkPostId)) ;
        paramMap.put("MinAmount" ,workMoneyStart) ;
        paramMap.put("MaxAmount" ,workMoneyEnd) ;
        paramMap.put("IsFace" ,mIsMoneyNo ? "1" : "0") ;
        paramMap.put("ProvinceId" , StringUtils.convertNull(mProvinceId)) ;
        paramMap.put("CityId" , StringUtils.convertNull(mCityId)) ;
        paramMap.put("CountyId" , StringUtils.convertNull(mAreaId)) ;
        paramMap.put("Phone1" , phone1) ;
        paramMap.put("Phone2" , phone2) ;
        paramMap.put("WeChat" , weChat) ;
        paramMap.put("QQ" , qq) ;
        paramMap.put("Email" , email) ;
        paramMap.put("SelfEvaluation" , selfCom) ;
        paramMap.put("WorkYears" , StringUtils.convertNull(mWorkYearName)) ;
        paramMap.put("Albums" , thumbList) ;
        paramMap.put("WorkExperienceList" , "[" + experience + "]") ;

        boolean isNoChange = true ;
        if(null == mDetails
                || !mDetails.getName().equals(name)
                || !mDetails.getSex().equals("" + mSex)
                || !mDetails.getBirthday().equals(birthday)
                || !mDetails.getHeight().equals(height)
                || !mDetails.getWeight().equals(weight)
                || !mDetails.getEducation().equals(mEduName)
                || !mDetails.getNowAddress().equals(address)
                || !mDetails.getWorkPostId().equals(mWorkPostId)
                || !mDetails.getHopeProvinceId().equals(mProvinceId)
                || !mDetails.getHopeCityId().equals(mCityId)
                || !mDetails.getWorkType().equals("" + mWorkType)
                || !mDetails.getMinAmount().equals(workMoneyStart)
                || !mDetails.getMaxAmount().equals(workMoneyEnd)
                || !mDetails.isFace() == mIsMoneyNo
                || !mDetails.getWorkYears().equals(mWorkYearName)
                || !mDetails.getPhone1().equals(phone1)
                || !mDetails.getPhone2().equals(phone2)
                || !mDetails.getWeChat().equals(weChat)
                || !mDetails.getQQ().equals(qq)
                || !mDetails.getEmail().equals(email)
                || !mWorkExpIds.equals(experienceIds)
                || !mDetails.getSelfEvaluation().equals(selfCom)
                || !mThumbIds.equals(thumbList)){
            isNoChange = false ;
        }

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

        submitCreateRequest(paramMap) ;
    }

    private void submitCreateRequest(Map<String,String> paramMap){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_CREATE, paramMap, new RequestObjectCallBack<String>("" ,mContext ,String.class){

            @Override
            public void onSuccessResult(String bean) {
                loadingDialog.dismiss();
                ToastUtils.showToast(mContext, "简历添加成功");
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
        });
    }

    /**
     * 获取图片base64
     */
    private class GetImageBase64Task extends AsyncTask<Void,Void,String>{
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
            loadingDialog.setCancelable(true) ;
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
        para.put("Base64Image", base64Image);

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, para,
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

                                if (loadingDialog != null && loadingDialog.isShowing())
                                    loadingDialog.dismiss();
                                ToastUtils.showErrorToast(mContext, "图片上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            if (loadingDialog != null && loadingDialog.isShowing())
                                loadingDialog.dismiss();
                            ToastUtils.showErrorToast(mContext, "图片上传失败");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_JOB == requestCode){
            if(RESULT_OK == resultCode && data != null){
                JobPost mChoosedJob = (JobPost) data.getSerializableExtra(SkipUtils.INTENT_JOB_POST);
                mWorkPostId = "" ;
                if(mChoosedJob != null){
                    mWorkPostId = mChoosedJob.getWorkPostID() ;
                    mWorkNameTv.setText(mChoosedJob.getName()) ;
                }else{
                    mWorkNameTv.setText("请选择") ;
                }
            }
        }else if(REQUEST_CODE_FOR_EDU == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<WordType> mChooseEdu = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                mEduName = "" ;

                if(mChooseEdu != null && mChooseEdu.size() > 0){
                    mEduName= mChooseEdu.get(0).getItemName();
                }
                if("".equals(mEduName)){
                    mEduTv.setText("请选择") ;
                }else{
                    mEduTv.setText(mEduName) ;
                }
            }
        }else if(REQUEST_CODE_FOR_AREA == requestCode){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                mAreaId = data.getStringExtra("areaId");
                String provinceName = data.getStringExtra("provinceName");
                String cityName = data.getStringExtra("cityName");
                String areaName = data.getStringExtra("areaName");

                mWorkLocaTv.setText(provinceName + cityName + areaName);
            }
        }else if(REQUEST_CODE_FOR_EXP == requestCode){
            if(resultCode == RESULT_OK && data != null) {
                WorkExperience experience = (WorkExperience) data.getSerializableExtra(SkipUtils.INTENT_WORK_EXP);
                if(experience != null){
                    mWorkExpList.add(experience) ;
                    mWorkExpAdapter.notifyDataSetChanged() ;
                }
            }
        }else if(REQUEST_CODE_FOR_WORK_YEAR == requestCode){
            if(resultCode == RESULT_OK && data != null) {
                ArrayList<WordType> wordType = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                mWorkYearName = "" ;
                if(wordType != null && wordType.size() > 0){
                    mWorkYearName = wordType.get(0).getItemName() ;
                }

                if("".equals(mWorkYearName)){
                    mWorkYearTv.setText("请选择");
                }else{
                    mWorkYearTv.setText(mWorkYearName);
                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(REQUEST_CODE_PERMI_IMAGE == requestCode){
            goCheckPhoto() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(REQUEST_CODE_PERMI_IMAGE == requestCode){
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getResumeDetails") ;
    }
}
