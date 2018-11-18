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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.UnuseDetails;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.utils.FileUtil;
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
 * 闲置
 * Created by Administrator on 2018-04-19.
 * 2018-05-10 19:49:40
 * 去掉主图，取图集第一张传给主图字段（如果传了图集）
 * 2018-05-26 13:24:26
 * 必须至少一张图片（主图） ，隐藏运费的输入
 *
 * 2018-05-30 18:11:09
 * 改：去掉协议相关
 *
 * 2018-07-17 17:03:52
 * 发布闲置需要实名认证
 *
 * 2018-08-07 16:43:46
 * 添加包邮功能
 */

public class UnusedSendActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private TextView classificationtv;
    private EditText titleet;
    private EditText describeet;
    private RecyclerView picrecycleview;
    private TextView locationtv;
    private EditText priceet;
    private EditText pricemacketet;
    private EditText freightet;
    private View freightetTips;
    private ImageView agreeiconiv;
    private TextView agreementtv;
    private TextView submittv;
    private RelativeLayout agreementrl;

    private ImageView mBaoyouIv ;
    private boolean mIsBaoyou = false ;

    private final static int REQUEST_CODE_FOR_CATEGORY = 1001;//分类返回码
    //选择的分类集合，注意：由于是选的二级分类，所以取值时，取selectedCategory
    private ArrayList<UnusedCategory> mUnuesdCategory = new ArrayList<>();

    private String categoryIDstr;
    private String unusedItemId;

    @Override
    public int getLayoutID() {
        return R.layout.activity_unuesd_send;
    }

    @Override
    public void initView() {
        unusedItemId= getIntent().getStringExtra(SkipUtils.INTENT_UNUSE_ID);

        loadingDialog = new LoadingDialog(mContext) ;
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                NetUtils.cancelTag("getRegistTips");
            }
        });

        classificationtv=(TextView)findViewById(R.id.unuesd_cart_msg);
        titleet=(EditText)findViewById(R.id.unuesd_title_msg);
        describeet=(EditText)findViewById(R.id.unuesd_describe);
        picrecycleview=(RecyclerView)findViewById(R.id.unuesd_picture_recycleview);
        locationtv=(TextView)findViewById(R.id.unuesd_location_tv);
        priceet=(EditText)findViewById(R.id.unuesd_price_primsg);
        pricemacketet=(EditText)findViewById(R.id.unuesd_market_price_primsg);
        freightet=(EditText)findViewById(R.id.unuesd_freight_fee);
        freightetTips=findViewById(R.id.unuesd_freight_fee_tips_tv);
        agreeiconiv=(ImageView)findViewById(R.id.unuesd_agreement_icon);
        agreementtv=(TextView)findViewById(R.id.unuesd_agreement);
        submittv=(TextView)findViewById(R.id.unuesd_submit_sure);
        agreementrl=(RelativeLayout)findViewById(R.id.unuesd_agreement_rl);
        View mBaoyouLay = findViewById(R.id.acti_unuse_send_baoyou_lay) ;
        mBaoyouIv = findViewById(R.id.acti_unuse_send_baoyou_iv) ;

        mBaoyouLay.setOnClickListener(this);
        locationtv.setOnClickListener(this);

        View registTips = findViewById(R.id.unuesd_agreement) ;
        registTips.setOnClickListener(this);
        mAgreeCheckIv =(ImageView)findViewById(R.id.unuesd_agreement_icon) ;

        titleet.requestFocus() ;
        titleet.setSelection(0) ;

        mAdapterImageList.add(null) ;//添加图片的那个图标
        initPhotoContainer() ;
        if (!TextUtils.isEmpty(unusedItemId)){
            TextView mTitleTv = findViewById(R.id.unuesd_goods_acti_title_tv) ;
            mTitleTv.setText("编辑闲置");
            submittv.setText("提交修改");
        }

        if(!LoginHelper.isPersonApplyAcross(mContext)){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "请先通过实名认证", null
                    , "去认证", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    SkipUtils.toApplyActivity(mContext) ;
                    finish() ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
        }

    }

    @Override
    public void initData() {
        changeItemStyle();
        startLocations() ;
//        getRegistTipsAndToWeb(false);
        if (!TextUtils.isEmpty(unusedItemId)){
            if(null == mLoadingDialog){
                mLoadingDialog = new LoadingDialog(mContext) ;
            }
            mLoadingDialog.show() ;
            getUnusedDetailData();
        }
    }

    private LoadingDialog mLoadingDialog ;
    private UnuseDetails mDetails ;
    private void getUnusedDetailData() {
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(unusedItemId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_DETAILS, paramMap
                , new RequestObjectCallBack<UnuseDetails>("getUnuseDetails" , mContext , UnuseDetails.class) {
                    @Override
                    public void onSuccessResult(UnuseDetails bean) {
                        mDetails = bean ;
                        updateNeedDetails() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateNeedDetails() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateNeedDetails() ;
                    }
                });
    }
    /**
     * 填充需求详细
     */
    private String mThumbs = "" ;
    private void updateNeedDetails(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss() ;
        }

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "获取详情有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        classificationtv .setText("".equals(mDetails.getCategoryTitle()) ? "请选择" : mDetails.getCategoryTitle());
        titleet.setText(TextUtils.isEmpty(mDetails.getTitle())?"请输入标题":mDetails.getTitle());
        describeet.setText(TextUtils.isEmpty(mDetails.getAbstract())?"请输入商品描述":mDetails.getAbstract());
        priceet.setText(mDetails.getSalePrice());
        pricemacketet.setText(mDetails.getMarketPrice());

        String expressFee = mDetails.getExpressFee() ;
        freightet.setText(expressFee);
        if(StringUtils.convertString2Float(expressFee) <= 0F){
            changeYoufeiStyle() ;
        }

        mThumbs = "" ;
        List<ImageThumb> thumbs = mDetails.getAlbumsList() ;
        if(thumbs != null && thumbs.size() > 0) {
            int size = thumbs.size();

            for (int x = size - 1; x >= 0; x--) {
                ImageThumb thumb = thumbs.get(x);
                String imgUrl = thumb.getThumbImgUrl();

                localImageList.add(0, imgUrl);
                mAdapterImageList.add(0, imgUrl);
                imageUploadList.add(0, imgUrl);
            }

            StringBuffer imgBuilder = new StringBuffer();
            for (String imgUrl : imageUploadList) {
                imgBuilder.append(imgUrl).append(",");
            }

            mThumbs = imgBuilder.toString();
            if (mThumbs.length() > 1) {
                mThumbs = mThumbs.substring(0, mThumbs.length() - 1);
            }

            uploadIndex = localImageList.size() ;

            imageSelectorResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initListener() {
        classificationtv.setOnClickListener(this);
        submittv.setOnClickListener(this);
        agreementrl.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int clickid=view.getId();
        if (R.id.unuesd_cart_msg==clickid){
            ChooseUnuesdCategoryActivity.launch(mActivity ,true ,false,mUnuesdCategory,REQUEST_CODE_FOR_CATEGORY);
        }else if (R.id.unuesd_submit_sure==clickid){
            submitSendUnuesd() ;
        }else if(R.id.unuesd_agreement == clickid){
            getRegistTipsAndToWeb(true) ;
        }else if (R.id.unuesd_agreement_rl==clickid){
            mIsAgree = !mIsAgree ;
            changeItemStyle() ;
        }else if (R.id.unuesd_location_tv==clickid){
            startLocations() ;
        }else if (R.id.acti_unuse_send_baoyou_lay==clickid){
            changeYoufeiStyle() ;
        }
    }

    /**
     * 切换包邮状态
     */
    private void changeYoufeiStyle(){
        mIsBaoyou = !mIsBaoyou ;

        freightet.setEnabled(!mIsBaoyou);
        freightet.setVisibility(mIsBaoyou ? View.INVISIBLE : View.VISIBLE) ;
        freightetTips.setVisibility(mIsBaoyou ? View.INVISIBLE : View.VISIBLE) ;
        mBaoyouIv.setImageDrawable(getResources().getDrawable(mIsBaoyou
                ? R.mipmap.ic_check_checked_blue : R.mipmap.ic_check_nomal));
    }


//按钮点击事件
    private void submitSendUnuesd() {
        if(!mIsAgree){
            ToastUtils.showToast(mContext , "请先同意用户注册协议");
            return ;
        }

        if (!TextUtils.isEmpty(unusedItemId)){
            categoryIDstr=mDetails.CategoryID;
        }

        if("".equals(StringUtils.convertNull(categoryIDstr))){
            ToastUtils.showToast(mContext , "请选择分类");
            return ;
        }

        String title = titleet.getText().toString().trim() ;
        if(TextUtils.isEmpty(title)){
            titleet.requestFocus();
            ToastUtils.showToast(mContext , "请输入标题");
            return ;
        }
        String describemsg = describeet.getText().toString().trim() ;
        if(TextUtils.isEmpty(describemsg)){
            describeet.requestFocus();
            ToastUtils.showToast(mContext , "请输入描述信息");
            return ;
        }

        if(imageUploadList.size() == 0){
            ToastUtils.showToast(mContext , "请至少上传一张图片");
            return ;
        }
        String pricestr = priceet.getText().toString().trim() ;
        if(StringUtils.convertString2Float(pricestr)<= 0F){
            priceet.requestFocus();
            ToastUtils.showToast(mContext , "现价只能是大于零的数字");
            return ;
        }
        String pricemacketstr = pricemacketet.getText().toString().trim() ;
        if(StringUtils.convertString2Float(pricemacketstr)<= 0F){
            pricemacketet.requestFocus();
            ToastUtils.showToast(mContext , "原价只能是大于零的数字");
            return ;
        }

        String freightfee = freightet.getText().toString().trim() ;
        if(mIsBaoyou){
            freightfee = "0" ;
        }

        if(loadingDialog == null){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setCancelable(false) ;
        loadingDialog.setLoadingText("正在发布");
        loadingDialog.show() ;

        String mGoodMainPic = "" ;
        StringBuffer imgBuilder = new StringBuffer() ;
        for(String imgUrl : imageUploadList){
            if(!"".equals(StringUtils.convertNull(imgUrl))){
                if("".equals(mGoodMainPic)){
                    mGoodMainPic = imgUrl ;
                }

                imgBuilder.append(imgUrl).append(",") ;
            }
        }

        String thumbList = imgBuilder.toString() ;
        if(thumbList.length() > 1){
            thumbList = thumbList.substring(0 , thumbList.length() - 1) ;
        }

        if (MyApplication.getCurrentCityType().equals("1")){
            if("".equals(StringUtils.convertNull(mCurSheng))){
                mCurSheng = MyApplication.getCurrentCityName() ;
            }
        }else {
            if("".equals(StringUtils.convertNull(mCurShi))){
                mCurShi = MyApplication.getCurrentCityName() ;
            }
        }
        String proIdstr="";
        if (!TextUtils.isEmpty(unusedItemId)){
            proIdstr=unusedItemId;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("CategoryID" ,categoryIDstr) ;
        paramMap.put("ProductNature" , "2") ;
        paramMap.put("ProductTitle" , title) ;
        paramMap.put("Abstract" , describemsg) ;//摘要，暂时和详情传一个值
        paramMap.put("BodyContent" , describemsg) ;//详情
        paramMap.put("ImgUrl" , StringUtils.convertNull(mGoodMainPic)) ;
        paramMap.put("ImgAlbums" , thumbList) ;
        paramMap.put("SpecIDs" , "") ;
        paramMap.put("ProductSkuModels" , "") ;
        paramMap.put("SpecIDs" ,"") ;
        paramMap.put("SpecText" ,"") ;
        paramMap.put("SalePrice" ,pricestr) ;
        paramMap.put("ProvinceName" ,StringUtils.convertNull(mCurSheng)) ;
        paramMap.put("CityName" ,StringUtils.convertNull(mCurShi)) ;
        paramMap.put("LeavePrice" ,pricestr) ;
        paramMap.put("MarketPrice" ,pricemacketstr) ;
        paramMap.put("ExpressFee" ,freightfee) ;
        paramMap.put("ProId" ,proIdstr) ;
        paramMap.put("Longitude" , mCurLon) ;
        paramMap.put("Latitude" , mCurLat) ;

        boolean isNoChange = true ;
        if(null == mDetails || !mDetails.getCategoryID().equals(categoryIDstr)
                || !mDetails.getTitle().equals(title)
                || !mDetails.getAbstract().equals(describemsg)
                || !mDetails.getSalePrice().equals(pricestr)
                || !mDetails.getMarketPrice().equals(pricemacketstr)
                || !mDetails.getExpressFee().equals(freightfee)
                || !mDetails.getImgUrl().equals(StringUtils.convertNull(mGoodMainPic))
                || !thumbList.equals(mThumbs)){
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


        submitSendRequest(paramMap) ;

    }

    private void submitSendRequest(Map<String, String> paramMap) {
        String urls="";
        if (mDetails==null){
            urls=Urls.UNUSED_ADDPRODUCT;
        }else {
            urls=Urls.UNUSED_EDITPRODUCT;
        }

        NetUtils.getDataFromServerByPost(mContext,urls,paramMap,
                new RequestObjectCallBack<String>("needSend" , mContext , String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();
                        ToastUtils.showToast(mContext , "提交成功") ;
                        if (mDetails!=null){
                            setResult(RESULT_OK);
                        }
                        finish() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();

                        ToastUtils.showErrorToast(mContext , "发布失败") ;
                    }
                }) ;
    }

    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            loadingDialog.setLoadingText("");
            loadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_AGREE_TIPS_UNUSE) ;
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
                            agreementtv.setText(String.format(getResources().getString(R.string.regist_tips_def) ,title)) ;

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



    private ImageView mAgreeCheckIv ;
    private boolean mIsAgree = true ;//是否同意

    private void changeItemStyle(){
        mAgreeCheckIv.setImageDrawable(getResources().getDrawable(mIsAgree
                ? R.mipmap.ic_check_checked_blue
                : R.mipmap.ic_check_nomal));

        if(!mIsAgree){
            submittv.setBackgroundColor(getResources().getColor(R.color.color_grey_b3)) ;
        }else{
            submittv.setBackgroundColor(getResources().getColor(R.color.color_theme)) ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_FOR_CATEGORY == requestCode){
            if(RESULT_OK == resultCode && data != null){
                List<UnusedCategory> retuCategory = (ArrayList<UnusedCategory>) data.getSerializableExtra(SkipUtils.INTENT_UNUSE_CATEGORY_INFO);
                mUnuesdCategory.clear();
                categoryIDstr = "" ;

                if(retuCategory != null && retuCategory.size() > 0){
                    mUnuesdCategory.addAll(retuCategory) ;

                    String title = "" ;
                    for(UnusedCategory category : mUnuesdCategory){
                        List<UnusedCategory> childCateList = category.getSelectedCategory() ;
                        if(childCateList != null && childCateList.size() > 0){
                            for(UnusedCategory childCategory : childCateList){
                                title += (childCategory.getCategoryTitle() + ",") ;
                                categoryIDstr += (childCategory.getCategoryID() + ",") ;
                            }
                        }
                    }
                    if(title.length() > 0){
                        title = title.substring(0 ,title.length() - 1) ;
                    }
                    if(categoryIDstr.length() > 0){
                        categoryIDstr = categoryIDstr.substring(0 ,categoryIDstr.length() - 1) ;
                    }

                    classificationtv.setText(title) ;
                }else{
                    classificationtv.setText("请选择") ;
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

    /**
     * 开始定位
     */

    private final static int REQUEST_CODE_PERMI_LOCATION = 100 ;
    private LocationService mLocationService;
    private String mCurSheng = "";
    private String mCurShi = "";
    private String mCurQu = "";
    private String mCurLon = "0";
    private String mCurLat = "0";

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

                LogUtils.showLog("testLocations" ,"sendNeeds--" + locationDesc) ;

                if(mCurShi != null && mCurSheng != null){
                    mCurLon = location.getLongitude() +"" ;
                    mCurLat = location.getLatitude() +"" ;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            locationtv.setText("当前位置：" + (null == locationDesc ? "点击获取位置" : locationDesc));
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults ,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getRegistTips") ;

        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }

        mLocationService.unregisterListener(mListener); //注销掉监听
        mLocationService.stop(); //停止定位服务
    }


    /**
     * 拍照功能
     */
    private final static int UPLOAD_PHOTO = 23;
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int MAX_IMAGE_COUNT = 9;
    private ImageConfig imageConfig;
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

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        picrecycleview.setLayoutManager(new GridLayoutManager(this, 5));
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
        picrecycleview.setAdapter(imageSelectorResultAdapter);
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
        ImageSelector.open(UnusedSendActivity.this, imageConfig);
    }
}
