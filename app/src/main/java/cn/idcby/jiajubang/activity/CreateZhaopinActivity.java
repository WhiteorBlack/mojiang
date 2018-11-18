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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
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
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.JobCompanyInfo;
import cn.idcby.jiajubang.Bean.JobDetails;
import cn.idcby.jiajubang.Bean.JobPost;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.FlowLayout;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created on 2018/4/2.
 *
 * 2018-05-09 14:08:29
 * 添加职位详情
 * 调整薪资、工作年限
 *
 * 2018-05-30 18:09:30
 * 去掉 协议相关
 *
 */

public class CreateZhaopinActivity extends BaseMoreStatusActivity
        implements EasyPermissions.PermissionCallbacks{
    private TextView mWorkNameTv;//招聘职位
    private TextView mWorkEduTv;//学历
    private TextView mWorkYearTv;
    private TextView mWorkMoneyNoTv ;
    private ImageView mWorkMoneyNoIv ;//面议
    private View mWorkMoneyEditLay ;
    private EditText mWorkMoneyStartEv ;
    private EditText mWorkMoneyEndEv ;
    private FlowLayout mFuliLay ;
    private EditText mWorkDescEv;//职位描述
    private TextView mWorkAreaTv ;
    private EditText mWorkAddressEv;//工作地址
    private TextView mPhoneEv ;
    private ImageView mWorkTypeAllIv ;
    private ImageView mWorkTypeHalfIv ;

    private EditText mComNameEv ;
    private EditText mComScaleEv ;//规模
    private EditText mComTypeEv ;
    private EditText mComAddressEv ;
    private ImageView mComLogoIv ;
    private EditText mComDescEv ;
    private RecyclerView mComPicRv;

    private TextView mCurLocationTv ;
    private TextView mRegTv ;
    private ImageView mRegIv ;

    private TextView mSubmitTv ;

    private String mJobId ;
    private JobDetails mDetails ;

    private String mThumbs = "" ;
    private String mWelfareId = "" ;
    private String mWorkYearName = "" ;//工作年限
    private String mChooseJobPostId ;
    private String mChooseEduName ;
    private String mProvinceId ;
    private String mCityId ;
    private String mAreaId ;
    private int mWorkType = 1 ;// 1 全职 2 兼职
    private boolean mIsAgree = true ;//是否同意
    private boolean mIsMoneyNo = false ;//是否薪资面议

    private JobCompanyInfo mCompanyInfo ;

    //公司福利
    private List<WelfareList> mWelfareList = new ArrayList<>();

    //位置相关
    private String mCurSheng = "";
    private String mCurShi = "";
    private String mCurQu = "";
    private String mCurLon = "0";
    private String mCurLat = "0";

    private LocationService mLocationService;

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
        if(!"".equals(StringUtils.convertNull(mJobId))){
            getJobDetails() ;
        }else{
            showSuccessPage() ;
        }
        getJobsCompanyInfo() ;
//        getRegistTipsAndToWeb(false);
        startLocations() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_create_zhaopin ;
    }

    @Override
    public String setTitle() {
        return "发布招聘";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
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

        mJobId = getIntent().getStringExtra(SkipUtils.INTENT_JOB_ID) ;

        loadingDialog = new LoadingDialog(mContext) ;
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                NetUtils.cancelTag("getRegistTips") ;
            }
        });

        mWorkNameTv = findViewById(R.id.acti_create_zhaopin_position_tv) ;
        mWorkEduTv = findViewById(R.id.acti_create_zhaopin_edu_tv) ;
        mWorkYearTv = findViewById(R.id.acti_create_zhaopin_year_tv) ;
        mWorkMoneyEditLay = findViewById(R.id.acti_create_zhaopin_money_edit_lay) ;
        mWorkMoneyStartEv = findViewById(R.id.acti_create_zhaopin_money_start_ev) ;
        mWorkMoneyEndEv = findViewById(R.id.acti_create_zhaopin_money_end_ev) ;
        mWorkMoneyNoTv = findViewById(R.id.acti_create_zhaopin_money_no_tv) ;
        View mWorkMoneyNoLay = findViewById(R.id.acti_create_zhaopin_money_check_lay) ;
        mWorkMoneyNoIv = findViewById(R.id.acti_create_zhaopin_money_check_iv) ;
        View fuliLay = findViewById(R.id.acti_create_zhaopin_fuli_main_lay) ;
        mFuliLay = findViewById(R.id.acti_create_zhaopin_fuli_lay) ;
        mWorkDescEv = findViewById(R.id.acti_create_zhaopin_job_dt_ev) ;
        mWorkAreaTv = findViewById(R.id.acti_create_zhaopin_location_tv) ;
        mWorkAddressEv = findViewById(R.id.acti_create_zhaopin_other_ev) ;
        mPhoneEv = findViewById(R.id.acti_create_zhaopin_phone_ev) ;
        View mWorkTypeAllLay = findViewById(R.id.acti_create_zhaopin_type_all_lay) ;
        mWorkTypeAllIv = findViewById(R.id.acti_create_zhaopin_type_all_iv) ;
        View mWorkTypeHalfLay = findViewById(R.id.acti_create_zhaopin_type_half_lay) ;
        mWorkTypeHalfIv = findViewById(R.id.acti_create_zhaopin_type_half_iv) ;

        mComNameEv = findViewById(R.id.acti_create_zhaopin_com_name_ev) ;
        mComScaleEv = findViewById(R.id.acti_create_zhaopin_com_guimo_ev) ;
        mComTypeEv = findViewById(R.id.acti_create_zhaopin_com_type_ev) ;
        mComAddressEv = findViewById(R.id.acti_create_zhaopin_com_address_ev) ;
        mComLogoIv = findViewById(R.id.acti_create_zhaopin_com_logo_iv) ;
        mComDescEv = findViewById(R.id.acti_create_zhaopin_com_desc_ev) ;
        mComPicRv = findViewById(R.id.acti_create_zhaopin_com_pic_lay) ;

        mCurLocationTv = findViewById(R.id.acti_create_zhaopin_cur_location_tv) ;
        mRegTv = findViewById(R.id.acti_create_zhaopin_regis_tip_tv) ;
        mRegIv = findViewById(R.id.acti_create_zhaopin_regis_tip_iv) ;

        mSubmitTv = findViewById(R.id.acti_create_zhaopin_sub_tv) ;

        mCurLocationTv.setOnClickListener(this);
        mWorkYearTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        mRegIv.setOnClickListener(this);
        mRegTv.setOnClickListener(this);
        mComLogoIv.setOnClickListener(this);
        mWorkTypeHalfLay.setOnClickListener(this);
        mWorkTypeAllLay.setOnClickListener(this);
        mWorkAreaTv.setOnClickListener(this);
        fuliLay.setOnClickListener(this);
        mWorkEduTv.setOnClickListener(this);
        mWorkNameTv.setOnClickListener(this);
        mWorkMoneyNoLay.setOnClickListener(this);

        initPhotoContainer() ;

        if(!"".equals(StringUtils.convertNull(mJobId))){
            setTitleText("编辑招聘");
            mSubmitTv.setText("提交修改");
        }
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_create_zhaopin_position_tv == vId){//招聘职位
            Intent toJpIt = new Intent(mContext , JobPostListActivity.class) ;
            startActivityForResult(toJpIt , 1001) ;
        }else if(R.id.acti_create_zhaopin_edu_tv == vId){//学历
            SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_EDUCATION ,1002) ;
        }else if(R.id.acti_create_zhaopin_fuli_main_lay == vId){//福利
            Intent toWlIt = new Intent(mContext , ChooseWelfareActivity.class) ;
            toWlIt.putExtra(SkipUtils.INTENT_WELFARE_INFO , (ArrayList)mWelfareList) ;
            startActivityForResult(toWlIt , 1004) ;
        }else if(R.id.acti_create_zhaopin_location_tv == vId){//工作位置
            SelectedProvinceActivity.launch(mActivity,1003);
        }else if(R.id.acti_create_zhaopin_type_all_lay == vId){//全职
            changeWorkType(1) ;
        }else if(R.id.acti_create_zhaopin_type_half_lay == vId){//兼职
            changeWorkType(2) ;
        }else if(R.id.acti_create_zhaopin_com_logo_iv == vId){//公司logo
            checkPhoto() ;
        }else if(R.id.acti_create_zhaopin_regis_tip_iv == vId){//同意协议
            mIsAgree = !mIsAgree ;
            changeItemStyle() ;
        }else if(R.id.acti_create_zhaopin_regis_tip_tv == vId){//协议
            getRegistTipsAndToWeb(true) ;
        }else if(R.id.acti_create_zhaopin_money_check_lay == vId){//薪资面议
            mIsMoneyNo = !mIsMoneyNo ;
            changeWorkMoneyType() ;
        }else if(R.id.acti_create_zhaopin_year_tv == vId){//工作年限
            SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_WORK_YEAR ,1005) ;
        }else if(R.id.acti_create_zhaopin_sub_tv == vId){//发布
            submitZhaopinCreate() ;
        }else if(R.id.acti_create_zhaopin_cur_location_tv == vId){
            startLocations() ;
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
            mWorkDescEv.requestFocus() ;
            mWorkDescEv.setSelection(mWorkDescEv.getText().length()) ;
        }
    }

    /**
     * 更新公司福利
     */
    private void updateWelfareLayout(){
        mFuliLay.removeAllViews() ;
        mWelfareId ="" ;
        for(WelfareList welfare : mWelfareList){
            mWelfareId += (welfare.getWelfare() + ",") ;

            TextView tv = new TextView(mContext) ;
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT) ;
            tv.setLayoutParams(lp);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,13);
            tv.setTextColor(getResources().getColor(R.color.color_grey_text));
            tv.setText(welfare.getWelfareText()) ;

            mFuliLay.addView(tv) ;
        }

        if(mWelfareId.length() > 0){
            mWelfareId = mWelfareId.substring(0 ,mWelfareId.length() - 1) ;
        }
    }

    /**
     * 更换工作类型
     */
    private void changeWorkType(int index){
        if(index == mWorkType){
            return ;
        }

        mWorkType = index ;

        boolean isAll = 1 == mWorkType ;
        mWorkTypeAllIv.setImageDrawable(getResources().getDrawable(isAll
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal)) ;
        mWorkTypeHalfIv.setImageDrawable(getResources().getDrawable(!isAll
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal)) ;
    }

    /**
     * 根据sendType改变填写内容
     */
    private void changeItemStyle(){
        mRegIv.setImageDrawable(getResources().getDrawable(mIsAgree
                ? R.mipmap.ic_check_checked_blue
                : R.mipmap.ic_check_nomal));

        if(!mIsAgree){
            mSubmitTv.setBackgroundColor(getResources().getColor(R.color.color_grey_b3)) ;
        }else{
            mSubmitTv.setBackgroundColor(getResources().getColor(R.color.color_theme)) ;
        }
    }

    /**
     * 填充内容
     */
    private void updateJobDetails(){
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

        mChooseJobPostId = mDetails.getWorkPostId() ;
        if(!"".equals(mChooseJobPostId)){
            mWorkNameTv.setText(mDetails.getName()) ;
        }

        mChooseEduName = mDetails.getEducation() ;
        if(!"".equals(mChooseEduName)){
            mWorkEduTv.setText(mChooseEduName);
        }

        mWorkYearName = mDetails.getWorkYears() ;
        if(!"".equals(mWorkYearName)){
            mWorkYearTv.setText(mWorkYearName);
        }

        mWelfareList = mDetails.getWelfareList() ;
        updateWelfareLayout() ;

        //工作位置 省 市id、名字
        mProvinceId = mDetails.getWorkProvinceId() ;
        mCityId = mDetails.getWorkCityId() ;
        if(!"".equals(mProvinceId) && !"".equals(mCityId)){
            mWorkAreaTv.setText(mDetails.getWorkProvinceName() + mDetails.getWorkCityName());
        }

        //工作地址
        mWorkAddressEv.setText(mDetails.getWorkAddress());
        mWorkDescEv.setText(mDetails.getPostDescription());

        //薪资
        mIsMoneyNo = mDetails.isFace() ;
        changeWorkMoneyType() ;
        if(!mIsMoneyNo){
            mWorkMoneyStartEv.setText(mDetails.getMinAmount());
            mWorkMoneyEndEv.setText(mDetails.getMaxAmount());
        }

        //联系电话
        mPhoneEv.setText(mDetails.getPhone());

        //工作类型
        int workType = "1".equals(mDetails.getWorkType()) ? 1 : 2 ;
        changeWorkType(workType) ;

//
//        mComNameEv.setText(mDetails.getCompanyName()) ;
//        mComScaleEv.setText(mDetails.getCompanyScale());
//        mComTypeEv.setText(mDetails.getCompanyType());
//        mComAddressEv.setText(mDetails.getAddress());//但是位置返回有问题，多了2个省
//        mComDescEv.setText(mDetails.getCompanyIntroduce());
//        mComLogoImgUrl = mDetails.getCompanyLogoImage() ;
//        if(!"".equals(mComLogoImgUrl)){
//            GlideUtils.loader(mComLogoImgUrl,mComLogoIv);
//        }
//
//        List<ImageThumb> thumbs = mDetails.getAlbumsList() ;
//        if(thumbs != null && thumbs.size() > 0){
//            int size = thumbs.size() ;
//
//            for(int x = size - 1 ; x >= 0 ; x--){
//                ImageThumb thumb = thumbs.get(x) ;
//                String imgUrl = thumb.getThumbImgUrl() ;
//
//                localImageList.add(0, imgUrl);
//                mAdapterImageList.add(0, imgUrl);
//                imageUploadList.add(0, imgUrl);
//            }
//
//            StringBuffer imgBuilder = new StringBuffer() ;
//            for(String imgUrl : imageUploadList){
//                imgBuilder.append(imgUrl).append(",") ;
//            }
//
//            mThumbs = imgBuilder.toString() ;
//            if(mThumbs.length() > 1){
//                mThumbs = mThumbs.substring(0 , mThumbs.length() - 1) ;
//            }
//
//            uploadIndex = localImageList.size() ;
//            imageSelectorResultAdapter.notifyDataSetChanged();
//        }

    }

    /**
     * 获取职位详细
     */
    private void getJobDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mJobId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_DETAILS, paramMap
                , new RequestObjectCallBack<JobDetails>("getJobDetails",mContext ,JobDetails.class) {
                    @Override
                    public void onSuccessResult(JobDetails bean) {
                        mDetails = bean ;
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


    /**
     * 填充内容
     */
    private void updateCompanyInfo(){
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

                        updateCompanyInfo() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateCompanyInfo() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateCompanyInfo() ;
                    }
                });
    }

    private void initPhotoContainer() {
        mAdapterImageList.add(null) ;//添加图片的那个图标

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 4 ;
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
        ImageSelector.open(CreateZhaopinActivity.this, imageConfig);
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
        }else if(1000 == requestCode){
            startLocations() ;
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
     * 发布
     */
    private void submitZhaopinCreate(){
        if("".equals(StringUtils.convertNull(mChooseJobPostId))){
            ToastUtils.showToast(mContext ,"请选择招聘职位");
            return ;
        }

        String workMoneyStart = mWorkMoneyStartEv.getText().toString().trim() ;
        if(!mIsMoneyNo && StringUtils.convertString2Float(workMoneyStart) <= 0){
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

        String phone = mPhoneEv.getText().toString().trim() ;
        if("".equals(phone)){
            ToastUtils.showToast(mContext ,"请输入正确的联系电话");
            mPhoneEv.requestFocus() ;
            mPhoneEv.setText("") ;
            return ;
        }

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

        String welfareId = "" ;
        if(mWelfareList.size() > 0){
            StringBuilder welfIdBuf = new StringBuilder() ;
            for(WelfareList welfare : mWelfareList){
                welfIdBuf.append(welfare.getWelfare()).append(",") ;
            }
            if(welfIdBuf.length() > 0){
                welfareId = welfIdBuf.substring(0 ,welfIdBuf.length() - 1) ;
            }
        }

        String workAddress = mWorkAddressEv.getText().toString().trim() ;
        String postDesc = mWorkDescEv.getText().toString().trim() ;

        String thumbList = "" ;
        if(imageUploadList.size() > 0){
            for(String imgurl : imageUploadList){
                thumbList += (imgurl + ",") ;
            }
            thumbList = thumbList.substring(0,thumbList.length() - 1) ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("RecruitId" ,StringUtils.convertNull(mJobId)) ;
        paramMap.put("WorkPostId" ,StringUtils.convertNull(mChooseJobPostId)) ;
        paramMap.put("Education" ,StringUtils.convertNull(mChooseEduName)) ;
        paramMap.put("WorkYears" ,StringUtils.convertNull(mWorkYearName)) ;
        paramMap.put("MinAmount" ,workMoneyStart) ;
        paramMap.put("MaxAmount" ,workMoneyEnd) ;
        paramMap.put("IsFace" ,mIsMoneyNo ? "1" : "0") ;
        paramMap.put("WorkAddress" ,workAddress) ;
        paramMap.put("PostDescription" ,postDesc) ;
        paramMap.put("Albums" ,thumbList) ;
        paramMap.put("Welfare" ,welfareId) ;
        paramMap.put("ProvinceId" , StringUtils.convertNull(mProvinceId)) ;
        paramMap.put("CityId" ,StringUtils.convertNull(mCityId)) ;
        paramMap.put("CountyId" ,StringUtils.convertNull(mAreaId)) ;
        paramMap.put("Phone" ,phone) ;
        paramMap.put("CreateCountyName" ,StringUtils.convertNull(mCurQu)) ;
        paramMap.put("CreateCityName" ,StringUtils.convertNull(mCurShi)) ;
        paramMap.put("CreateProvinceName" ,StringUtils.convertNull(mCurSheng)) ;
        paramMap.put("Longitude" ,StringUtils.convertNull(mCurLon)) ;
        paramMap.put("Latitude" ,StringUtils.convertNull(mCurLat)) ;
        paramMap.put("WorkType" ,mWorkType+"") ;
        paramMap.put("CompanyName" ,companyName) ;
        paramMap.put("CompanyScale" ,companyScale) ;
        paramMap.put("CompanyType" ,companyType) ;
        paramMap.put("CompanyAddress" ,companyAddress) ;
        paramMap.put("CompanyLogoImage" ,StringUtils.convertNull(mComLogoImgUrl)) ;
        paramMap.put("CompanyIntroduce" ,companyDesc) ;

        boolean isNoChange = true ;
        if(null == mDetails
                || !mDetails.getWorkPostId().equals(mChooseJobPostId)
                || !mDetails.getEducation().equals(mChooseEduName)
                || !mDetails.getWorkYears().equals(mWorkYearName)
                || !mDetails.getMinAmount().equals(workMoneyStart)
                || !mDetails.getMaxAmount().equals(workMoneyEnd)
                || !mDetails.getWorkAddress().equals(workAddress)
                || !mDetails.getPostDescription().equals(postDesc)
                || !mDetails.isFace() == mIsMoneyNo
                || !mWelfareId.equals(welfareId)
                || !mDetails.getWorkProvinceId().equals(mProvinceId)
                || !mDetails.getWorkCityId().equals(mCityId)
                || !mDetails.getPhone().equals(phone)
                || !mDetails.getWorkType().equals("" + mWorkType)
                || !mDetails.getCompanyName().equals(companyName)
                || !mDetails.getCompanyScale().equals(companyScale)
                || !mDetails.getCompanyType().equals(companyType)
                || !mDetails.getAddress().equals(companyAddress)
                || !mDetails.getCompanyLogoImage().equals(mComLogoImgUrl)
                || !mDetails.getCompanyIntroduce().equals(companyDesc)
                || !mThumbs.equals(thumbList)){
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
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show() ;

        NetUtils.getDataFromServerByPost(mContext, Urls.JOBS_CREATE, paramMap
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

    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            loadingDialog.setLoadingText("");
            loadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_AGREE_TIPS_USER) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL_BY_CODE, paramMap
                , new RequestObjectCallBack<NewsDetail>("getRegistTips" ,mContext ,NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                        if(bean != null){
                            String title = bean.getTitle() ;
                            if("".equals(title)){
                                title = "用户协议" ;
                            }
                            mRegTv.setText(String.format(getResources().getString(R.string.send_tips_def) ,title)) ;

                            if(show){
                                String contentUrl = bean.getContentH5Url() ;
                                SkipUtils.toShowWebActivity(mContext ,title ,contentUrl);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }
                    }
                });
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
                    ,"应用需要定位权限来获取当前位置，拒绝会导致部分功能异常",1000
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

                LogUtils.showLog("testLocations" ,"sendZhaopin--" + locationDesc) ;

                if(mCurSheng != null && mCurShi != null){
                    mCurLon = location.getLongitude() +"" ;
                    mCurLat = location.getLatitude() +"" ;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurLocationTv.setText("当前位置：" + (null == locationDesc ? "点击获取位置" : locationDesc));
                        }
                    });

                    mLocationService.unregisterListener(mListener); //注销掉监听
                    mLocationService.stop(); //停止定位服务

                    MyApplication.updateCurLocation(location) ;
                }

            }
        }
    };

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
        }else if(1001 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                JobPost mChoosedJob = (JobPost) data.getSerializableExtra(SkipUtils.INTENT_JOB_POST);
                mChooseJobPostId = "" ;
                if(mChoosedJob != null){
                    mChooseJobPostId = mChoosedJob.getWorkPostID() ;
                    String jobName = mChoosedJob.getName() ;
                    mWorkNameTv.setText(jobName) ;
                }else{
                    mWorkNameTv.setText("请选择") ;
                }
            }
        }else if(1002 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<WordType> mChooseEdu = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);

                mChooseEduName = "" ;
                if(mChooseEdu != null && mChooseEdu.size() > 0){
                    mChooseEduName = mChooseEdu.get(0).getItemName();
                }
                if(!"".equals(mChooseEduName)){
                    mWorkEduTv.setText(mChooseEduName) ;
                }else{
                    mWorkEduTv.setText("请选择");
                }
            }
        }else if(1003 == requestCode){
            if(resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                mAreaId = data.getStringExtra("areaId");
                String provinceName = data.getStringExtra("provinceName");
                String cityName = data.getStringExtra("cityName");
                String areaName = data.getStringExtra("areaName");

                mWorkAreaTv.setText(provinceName + cityName + areaName);
            }
        }else if(1004 == requestCode){
            if(resultCode == RESULT_OK && data != null) {
                List<WelfareList> welfareLists = (List<WelfareList>) data.getSerializableExtra(SkipUtils.INTENT_WELFARE_INFO);

                mWelfareList.clear();
                mWelfareList.addAll(welfareLists) ;

                updateWelfareLayout() ;
            }
        }else if(1005 == requestCode){
            if(resultCode == RESULT_OK && data != null) {
                List<WordType> wordTypes = (List<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                mWorkYearName = "" ;
                if(wordTypes != null && wordTypes.size() > 0){
                    mWorkYearName = wordTypes.get(0).getItemName() ;
                }

                if("".equals(mWorkYearName)){
                    mWorkYearTv.setText("请选择");
                }else{
                    mWorkYearTv.setText(mWorkYearName);
                }
            }
        }
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

        NetUtils.cancelTag("getRegistTips");
    }

}
