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
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.NeedsCategory;
import cn.idcby.jiajubang.Bean.NeedsDetails;
import cn.idcby.jiajubang.Bean.NewsDetail;
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
 * 发布需求、招标
 * Created on 2018/3/30.
 *
 * 2018-04-17 16:34:19
 * 中标说明去掉
 *
 * 2018-05-30 18:11:52
 * 改：协议去掉
 *
 * 2018-06-01 14:51:00
 * 结束日期改为 有效期，输入 1 - 31 天
 *
 * 2018-08-06 18:00:44
 * 【问题汇总0804】加上 中标说明 必填，底部加上保证金说明
 *
 * 2018-09-11 13:56:55
 * 暂时把服务和安装需求屏蔽，不允许发布；置灰不可点击
 */

public class NeedsSendActivity extends BaseActivity  implements EasyPermissions.PermissionCallbacks{
    private TextView mTypeNeedTv ;
    private TextView mTypeBidTv ;

    private TextView mCategoryTv;
    private EditText mTitleEv ;
    private EditText mEndTimeEv;
    private EditText mNeedDescEv ;
    private RecyclerView mNeedPicRv ;

    //中标说明
    private View mNeedTipsLay ;
    private EditText mNeedTipsEv ;
    //保证金说明
    private View mNeedBzjTipsLay ;

    private TextView mLocationTv ;

    private ImageView mAgreeCheckIv ;
    private TextView mAgreeTv ;

    private TextView mSubmitTv ;

    private Dialog mPayBondDialog ;
    private EditText mPayBondEv ;

    private int mSendType = 1 ;//1需求 2招标
    private boolean mIsAgree = true ;//是否同意
    private String mNeedCategoryId ;
    private String mThumbs = "" ;

    private String mNeedId ;
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
    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 9 ;

    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int REQUEST_CODE_PERMI_LOCATION = 100 ;
    private final static int REQUEST_CODE_FOR_CATEGORY = 1005;
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
    private String needItemsIDstr;


    @Override
    public int getLayoutID() {
        return R.layout.activity_needs_send ;
    }

    @Override
    public void initView() {
        needItemsIDstr = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_ID);
        mSendType = getIntent().getIntExtra(SkipUtils.INTENT_NEEDS_TYPE,mSendType);

        loadingDialog = new LoadingDialog(mContext) ;
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                NetUtils.cancelTag("getRegistTips") ;
            }
        });

        View mTypeLay = findViewById(R.id.acti_needs_send_type_lay) ;
        TextView mTitleTv = findViewById(R.id.acti_needs_send_title_tv) ;

        mTypeNeedTv = findViewById(R.id.acti_needs_send_type_need_tv) ;
        mTypeBidTv = findViewById(R.id.acti_needs_send_type_bid_tv) ;

        mCategoryTv = findViewById(R.id.acti_needs_send_category_tv) ;
        mTitleEv = findViewById(R.id.acti_needs_send_title_ev) ;
        mEndTimeEv = findViewById(R.id.acti_needs_send_time_ev) ;

        mNeedDescEv = findViewById(R.id.acti_needs_send_content_ev) ;
        mNeedPicRv = findViewById(R.id.acti_needs_send_pic_lay) ;

        mLocationTv = findViewById(R.id.acti_needs_send_location_tv) ;
        mAgreeTv = findViewById(R.id.acti_needs_send_regis_tip_tv) ;
        mAgreeCheckIv = findViewById(R.id.acti_needs_send_regis_tip_iv) ;
        mAgreeTv.setOnClickListener(this);
        mLocationTv.setOnClickListener(this);

        mNeedTipsLay = findViewById(R.id.acti_needs_send_tips_lay) ;
        mNeedTipsEv = findViewById(R.id.acti_needs_send_tips_ev) ;

        mNeedBzjTipsLay = findViewById(R.id.acti_need_send_bzj_lay) ;
        TextView mNeedBzjTipsTv = findViewById(R.id.acti_need_send_bzj_title_tv);
        mNeedBzjTipsTv.setText(Html.fromHtml(getResources().getString(R.string.need_send_tips_title))) ;

        mNeedBzjTipsLay.setVisibility(2 == mSendType ? View.VISIBLE : View.GONE);
        mNeedTipsLay.setVisibility(2 == mSendType ? View.VISIBLE : View.GONE);
        mNeedTipsEv.setEnabled(2 == mSendType);


        mSubmitTv = findViewById(R.id.acti_needs_send_submit_tv) ;

        mTitleEv.requestFocus() ;

        initPhotoContainer() ;

        if (!TextUtils.isEmpty(needItemsIDstr)){
            mSubmitTv.setText("提交修改");
            mTitleTv.setText(1 == mSendType ? "需求编辑" : "招标编辑");
            mTitleTv.setVisibility(View.VISIBLE);
            mTypeLay.setVisibility(View.GONE);

            getRequestedData(needItemsIDstr);
        }

        startLocations() ;
//        getRegistTipsAndToWeb(false);


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

    /**
     * 获取已经有的需求的数据
     * @param needItemsIDstr
     */
    private NeedsDetails mDetails ;
    private void getRequestedData(String needItemsIDstr) {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();


        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(needItemsIDstr)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_DETAILS, paramMap
                , new RequestObjectCallBack<NeedsDetails>("getNeedsDetails" , mContext , NeedsDetails.class) {
                    @Override
                    public void onSuccessResult(NeedsDetails bean) {
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

    private void updateNeedDetails() {
        if(loadingDialog != null){
            loadingDialog.dismiss() ;
        }

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

        mNeedCategoryId = mDetails.getCategoryID() ;
        mCategoryTv.setText("".equals(mDetails.getCategoryName()) ? "请选择" : mDetails.getCategoryName());
        mTitleEv.setText(mDetails.getNeedTitle()) ;
        mNeedDescEv.setText(mDetails.getNeedExplain()) ;
        mNeedTipsEv.setText(mDetails.getBidDescription()) ;

        String endTime = mDetails.getEndTime() ;
        mEndTimeEv.setText(endTime);

        mThumbs = "" ;
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

            mThumbs = imgBuilder.toString() ;
            if(mThumbs.length() > 1){
                mThumbs = mThumbs.substring(0 , mThumbs.length() - 1) ;
            }

            uploadIndex = localImageList.size() ;
            imageSelectorResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initListener() {
        mTypeNeedTv.setOnClickListener(this) ;
        mTypeBidTv.setOnClickListener(this) ;
        mCategoryTv.setOnClickListener(this) ;
        mAgreeCheckIv.setOnClickListener(this) ;
        mSubmitTv.setOnClickListener(this) ;

        mEndTimeEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ct = StringUtils.convertString2Count(String.valueOf(charSequence)) ;
                if(ct > 30 || ct <= 0){
                    ToastUtils.showToast(mContext ,"有效期为0-30天");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_needs_send_type_need_tv == vId){
            changeSendType(1) ;
        }else if(R.id.acti_needs_send_type_bid_tv == vId){
            changeSendType(2) ;
        }else if(R.id.acti_needs_send_category_tv == vId){
            Intent toCtIt = new Intent(mContext ,ChooseNeedCategoryActivity.class) ;
            NeedsSendActivity.this.startActivityForResult(toCtIt , REQUEST_CODE_FOR_CATEGORY) ;
        }else if(R.id.acti_needs_send_regis_tip_tv == vId){
            getRegistTipsAndToWeb(true) ;
        }else if(R.id.acti_needs_send_regis_tip_iv == vId){
            mIsAgree = !mIsAgree ;
            changeItemStyle() ;
        }else if(R.id.acti_needs_send_submit_tv == vId){
            submitSendNeeds() ;
        }else if(R.id.acti_needs_send_location_tv == vId){
            startLocations() ;
        }
    }

    /**
     * 显示缴纳保证金dialog
     */
    private void showPayBondDialog(){
        if(null == mPayBondDialog){
            mPayBondDialog = new Dialog(mContext,cn.idcby.commonlibrary.R.style.my_custom_dialog) ;
            View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_bond , null) ;
            mPayBondDialog.setContentView(v);

            v.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.7F);

            mPayBondEv = v.findViewById(R.id.dialog_pay_bond_money_ev) ;
            TextView mOkTv = v.findViewById(R.id.dialog_pay_bond_ok_tv) ;
            TextView mCancelTv = v.findViewById(R.id.dialog_pay_bond_cancel_tv) ;
            mOkTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String money = mPayBondEv.getText().toString().trim() ;
                    if("".equals(money) || StringUtils.convertString2Float(money) <= 0){
                        ToastUtils.showToast(mContext ,"请输入正确的金额") ;
                        return ;
                    }
                    mPayBondDialog.dismiss();

                    showOrHiddenKeyBoard(false) ;
                    payBondMoney(money) ;
                }
            });
            mCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPayBondDialog.dismiss();
                    showOrHiddenKeyBoard(false) ;

                    finish() ;
                }
            });
        }

        showOrHiddenKeyBoard(true) ;

        mPayBondDialog.show() ;
    }

    private void showOrHiddenKeyBoard(boolean isShow){
        InputMethodManager manager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        if(manager == null){
            return ;
        }

        if(isShow){
            manager.showSoftInput(mPayBondEv , InputMethodManager.SHOW_FORCED) ;
        }else{
            manager.hideSoftInputFromWindow(mPayBondEv.getWindowToken() , 0) ;
        }
    }

    private void initPhotoContainer() {
        mAdapterImageList.add(null) ;//添加图片的那个图标

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        mNeedPicRv.setLayoutManager(new GridLayoutManager(this, 5));
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
        mNeedPicRv.setAdapter(imageSelectorResultAdapter);
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
     *  切换发布类型
     *  @param index index
     */
    private void changeSendType(int index){
        if(index == mSendType){
            return ;
        }

        if(1 == mSendType){
            mTypeNeedTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeNeedTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeBidTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeBidTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(1 == index){
            mTypeNeedTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeNeedTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeBidTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeBidTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mSendType = index ;

        mNeedBzjTipsLay.setVisibility(2 == mSendType ? View.VISIBLE : View.GONE);
        mNeedTipsLay.setVisibility(2 == mSendType ? View.VISIBLE : View.GONE);
        mNeedTipsEv.setEnabled(2 == mSendType);

//        getRegistTipsAndToWeb(false) ;

        changeItemStyle() ;
    }

    /**
     * 根据sendType改变填写内容
     */
    private void changeItemStyle(){
        mAgreeCheckIv.setImageDrawable(getResources().getDrawable(mIsAgree
                ? R.mipmap.ic_check_checked_blue
                : R.mipmap.ic_check_nomal));

        if(!mIsAgree){
            mSubmitTv.setBackgroundColor(getResources().getColor(R.color.color_grey_b3)) ;
        }else{
            mSubmitTv.setBackgroundColor(getResources().getColor(R.color.color_theme)) ;
        }
    }

    /**
     * 缴纳保证金
     * @param money 保证金
     */
    private void payBondMoney(final String money){
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.setCancelable(false);
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("NeedId" , mNeedId) ;
        paramMap.put("BidBond" , money) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_PAY_BOND, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("payBondMoney" , mContext , NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        loadingDialog.dismiss();

                        if(bean != null){
                            String moneys = bean.PayableAmount ;
                            if("".equals(StringUtils.convertNull(moneys))){
                                moneys = money ;
                            }
                            //跳转到付款界面
                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,bean.OrderID) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_BOND_BID) ;
                            startActivity(toPyIt);
                        }else{
                            ToastUtils.showErrorToast(mContext , "缴纳失败，请到我的招标进行缴纳") ;
                        }

                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();

                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();

                        ToastUtils.showErrorToast(mContext , "缴纳失败") ;

                        setResult(RESULT_OK);
                        finish() ;
                    }
                });
    }


    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            loadingDialog.setLoadingText("") ;
            loadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,1 == mSendType
                ? ParaUtils.PARAM_AGREE_TIPS_NEED
                : ParaUtils.PARAM_AGREE_TIPS_BID) ;
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
                            mAgreeTv.setText(String.format(getResources().getString(R.string.regist_tips_def) ,title)) ;

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
     * 提交
     */
    private void submitSendNeeds(){
        if(!mIsAgree){
            return ;
        }

        if("".equals(StringUtils.convertNull(mNeedCategoryId))){
            ToastUtils.showToast(mContext , "请选择分类");
            return ;
        }

        String title = mTitleEv.getText().toString().trim() ;
        if("".equals(title)){
            mTitleEv.requestFocus();
            mTitleEv.setText("") ;
            ToastUtils.showToast(mContext , "请输入标题");
            return ;
        }

        String endTime = mEndTimeEv.getText().toString() ;
        if(StringUtils.convertString2Count(endTime) <= 0
                || StringUtils.convertString2Count(endTime) > 30){
            ToastUtils.showToast(mContext , "有效期为0-30天");
            mEndTimeEv.requestFocus() ;
            mEndTimeEv.setSelection(endTime.length());
            return ;
        }

        String needsDesc = mNeedDescEv.getText().toString().trim() ;
        if("".equals(needsDesc)){
            mNeedDescEv.requestFocus();
            mNeedDescEv.setText("") ;
            ToastUtils.showToast(mContext , "请输入需求说明");
            return ;
        }

        String tipsDesc = mNeedTipsEv.getText().toString().trim() ;
        if("".equals(tipsDesc) && mSendType == 2){
            mNeedTipsEv.requestFocus();
            mNeedTipsEv.setText("") ;
            ToastUtils.showToast(mContext , "请输入中标说明");
            return ;
        }

        loadingDialog.setCancelable(false) ;
        loadingDialog.setLoadingText("正在发布");
        loadingDialog.show() ;

        StringBuffer imgBuilder = new StringBuffer() ;
        for(String imgUrl : imageUploadList){
            imgBuilder.append(imgUrl).append(",") ;
        }

        String thumbList = imgBuilder.toString() ;
        if(thumbList.length() > 1){
            thumbList = thumbList.substring(0 , thumbList.length() - 1) ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" , StringUtils.convertNull(needItemsIDstr)) ;
        paramMap.put("CategoryID" , StringUtils.convertNull(mNeedCategoryId)) ;
        paramMap.put("TypeId" , "" + mSendType) ;
        paramMap.put("NeedTitle" , title) ;
        paramMap.put("EndTime" , endTime) ;
        paramMap.put("NeedExplain" , needsDesc) ;
        paramMap.put("BidDescription" , tipsDesc) ;
        paramMap.put("Albums" , thumbList) ;
        paramMap.put("CreateProvinceName" , StringUtils.convertNull(mCurSheng)) ;
        paramMap.put("CreateCityName" , StringUtils.convertNull(mCurShi)) ;
        paramMap.put("CreateCountyName" , StringUtils.convertNull(mCurQu)) ;
        paramMap.put("Longitude" , mCurLon) ;
        paramMap.put("Latitude" , mCurLat) ;

        boolean isNoChange = true ;
        if(null == mDetails || !mDetails.getCategoryID().equals(mNeedCategoryId)
                || !mDetails.getNeedTitle().equals(title)
                || !mDetails.getEndTime().equals(endTime)
                || !mDetails.getNeedExplain().equals(needsDesc)
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

    private void submitSendRequest(Map<String,String> paramMap){

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_SEND,paramMap,
                new RequestObjectCallBack<String>("needSend" , mContext , String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();
                        ToastUtils.showToast(mContext , "发布成功") ;

                        if(1 == mSendType){
                            setResult(RESULT_OK);
                            finish() ;
                        }else{
                            //交纳保证金
                            mNeedId = bean ;
                            if(!"".equals(StringUtils.convertNull(mNeedId))){
                                showPayBondDialog() ;
                            }else{
                                setResult(RESULT_OK);
                                finish() ;
                            }
                        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_CATEGORY == requestCode){
            if(RESULT_OK == resultCode && data != null){
                NeedsCategory mNeedCategory = (NeedsCategory) data.getSerializableExtra(SkipUtils.INTENT_NEEDS_CATEGORY_INFO);
                mNeedCategoryId = "" ;

                if(mNeedCategory != null){
                    String title = mNeedCategory.getCategoryTitle() ;
                    mNeedCategoryId = mNeedCategory.getNeedCategoryID() ;

                    mCategoryTv.setText(title) ;
                }else{
                    mCategoryTv.setText("请选择") ;
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
                            mLocationTv.setText("当前位置：" + (null == locationDesc ? "点击获取位置" : locationDesc));
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

        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }


        if(mLocationService != null){
            mLocationService.unregisterListener(mListener); //注销掉监听
            mLocationService.stop(); //停止定位服务
        }

    }
}
