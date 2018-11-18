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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.MyNeedsOrderList;
import cn.idcby.jiajubang.Bean.NeedsBidDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyNeedBidOrder;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
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

/**
 *  报价
 * Created on 2018/3/30.
 *
 * 判断当前登录的人，是不是当前发布需求的人，如果是，可以确认报价
 *
 * 2018-05-22 14:38:14
 * 改：如果是本人发的需求，而且当前报价就是中标的那个报价
 * ，那么以后的所有相关订单，都是从这里查看和操作
 * 报价时间改为手动输入（单位：天）
 *
 * 2018-05-25 21:10:28
 * 注意：暂时不支持编辑报价
 *
 * 2018-05-30 15:13:38
 * 添加订单完成时 提示语
 *
 * 2018-06-02 13:44:39
 * 需求未选标前，所有报价可以修改
 *
 * 2018-06-30 10:17:52
 * 改：去掉订单展示，只展示报价详细
 *
 * 2018-07-04 11:16:49
 * 如果是自己发的需求，查看所有报价，都应该有  联系TA 按钮，
 *
 * 2018-07-25 14:20:32
 * 确认报价之后，直接进行下一步操作：服务、安装需求直接跳到预约服务界面；闲置、商品需求跳转到这个人的主页
 * 如果自己发的需求，选标之后，是 联系TA 和 立即下单 2个按钮在下面
 */

public class NeedsConfirmBidActivity extends BaseMoreStatusActivity implements EasyPermissions.PermissionCallbacks{
    //header
    private EditText mTimeEv;
    private EditText mPriceEv ;
    private EditText mDescEv ;
    private RecyclerView mPictureRv ;

    private ListView mLv ;
    private TextView mSubmitTv ;
    private View mConfirmTv ;

    //报价订单
    private List<MyNeedsOrderList> mOrderList = new ArrayList<>() ;
    private AdapterMyNeedBidOrder mAdapter ;

    private String mOfferId ;
    private String mNeedId ;
    private boolean mIsSelfNeed = false ;//是否是自己发布的需求，如果是，就可以确认报价
    private boolean mIsBonded = false ;//是否已经确认报价了

    private boolean mIsEdit = true ;//发布、详细
    private boolean mIsNeeds = true ;//是否是需求，招标是false

    private String mHxName ;
    private NeedsBidDetails mDetails ;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;

    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
    private final static int REQUEST_CODE_FOR_OPTION_ITEM = 1000;

    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 9;

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
        if("".equals(StringUtils.convertNull(mOfferId))){
            showSuccessPage() ;
        }else{
            showLoadingPage() ;
            //获取详细
            getBidDetails() ;
        }
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_needs_confirm_bid ;
    }

    @Override
    public String setTitle() {
        return null;
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mIsBonded = getIntent().getBooleanExtra(SkipUtils.INTENT_NEEDS_IS_BONDED,mIsBonded) ;
        mIsNeeds = 1 == getIntent().getIntExtra(SkipUtils.INTENT_NEEDS_TYPE ,1) ;
        mOfferId = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_OFFER_ID) ;
        mNeedId = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_ID) ;
        mIsSelfNeed = getIntent().getBooleanExtra(SkipUtils.INTENT_NEEDS_IS_SELF ,false) ;

        mIsEdit = !mIsBonded && !mIsSelfNeed ;

        mLv = findViewById(R.id.acti_needs_confirm_bid_order_lv) ;
        mConfirmTv = findViewById(R.id.acti_needs_confirm_bid_confirm_tv) ;
        mSubmitTv = findViewById(R.id.acti_needs_confirm_bid_sub_tv) ;
        View mConnectionTv = findViewById(R.id.acti_needs_confirm_bid_connection_tv) ;
        mConfirmTv.setOnClickListener(this) ;
        mSubmitTv.setOnClickListener(this) ;
        mConnectionTv.setOnClickListener(this) ;

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_need_confirm_bid ,null) ;

        mLv.addHeaderView(headerView);

        //之前把需求订单也展示在这了，后来调整了，不存在需求订单，懒得改界面，就保留这个adapter
        mAdapter = new AdapterMyNeedBidOrder(mContext, mOrderList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
            }
        }) ;
        mLv.setAdapter(mAdapter);

        mTimeEv = headerView.findViewById(R.id.acti_needs_confirm_bid_time_tv) ;
        mPriceEv = headerView.findViewById(R.id.acti_needs_confirm_bid_price_ev) ;
        mDescEv = headerView.findViewById(R.id.acti_needs_confirm_bid_desc_ev) ;
        mPictureRv = headerView.findViewById(R.id.acti_needs_confirm_bid_pic_rv) ;

        if(mIsEdit){
            setTitleText("报价");
            mSubmitTv.setVisibility(View.VISIBLE) ;
        }else{
            setTitleText("查看报价");

            if(mIsSelfNeed){
                mConnectionTv.setVisibility(View.VISIBLE);

                if(!mIsBonded){
                    mSubmitTv.setText("设为中标");
                    mSubmitTv.setVisibility(View.VISIBLE);
                }else{
                    mConfirmTv.setVisibility(View.VISIBLE) ;
                }
            }

            mTimeEv.setEnabled(false);
            mPriceEv.setEnabled(false) ;
            mDescEv.setEnabled(false) ;
        }

        initPhotoContainer() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_needs_confirm_bid_sub_tv == vId){
            if(mIsEdit){
                submitBid() ;
            }else {
                if(mIsSelfNeed){
                    DialogUtils.showCustomViewDialog(mContext, "确认报价", "确定选择该报价？", null
                            , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            chooseBid() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        }else if(R.id.acti_needs_confirm_bid_connection_tv == vId){//联系TA
            if(!mIsEdit && mIsSelfNeed){
                SkipUtils.toMessageChatActivity(mActivity ,StringUtils.convertNull(mHxName));
            }
        }else if(R.id.acti_needs_confirm_bid_confirm_tv == vId){//立即下单
            if(mIsSelfNeed && mIsBonded && mDetails != null){//自己发布的需求，而且选标了
                toConfirmOrder() ;
            }
        }
    }

    /**
     * 下单
     */
    private void toConfirmOrder(){
        String userId = mDetails.getCreateUserId() ;
        int needStyle = mDetails.getCategoryStyle() ;

        if(SkipUtils.NEED_STYLE_TYPE_SERVER == needStyle
                || SkipUtils.NEED_STYLE_TYPE_INSTALL == needStyle){//服务需求、安装需求

            ServerConfirmActivity.launch(mActivity ,userId ,SkipUtils.NEED_STYLE_TYPE_INSTALL == needStyle ,1000) ;

        }else if(SkipUtils.NEED_STYLE_TYPE_GOOD == needStyle
                || SkipUtils.NEED_STYLE_TYPE_UNUSE == needStyle){//商品需求、闲置需求

            SkipUtils.toOtherUserInfoActivity(mContext ,userId) ;

        }
    }


    /**
     * 完成order
     * @param orderId orderId
     */
    private void finishNeedOrder(String orderId){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setCancelable(false);
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , orderId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_NEEDS_ORDER_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishNeedOrder",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        getBidDetails() ;
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
     * 填充
     * @param details de
     */
    private void updateBidDetails(NeedsBidDetails details){
        if(null == details){
            showErrorPage() ;
        }else{
            mDetails = details ;

            //注意：要去掉hint的描述，不然看着不对劲
            mPriceEv.setHint("");
            mDescEv.setHint("");

            String workDates = details.getWorkDays() ;
            String price = details.getTotalOffer() ;
            String desc = details.getOfferDescription() ;
            mHxName = details.getHxName() ;

            mTimeEv.setText(workDates);
            mPriceEv.setText(price);
            mDescEv.setText(desc) ;

            mAdapterImageList.clear();

            List<ImageThumb> thumbs = details.AlbumsList ;
            if(thumbs != null && thumbs.size() > 0){
                for(ImageThumb thumb : thumbs){
                    String thumbImg = thumb.getThumbImgUrl() ;
                    String realImg = thumb.getOriginalImgUrl() ;

                    mAdapterImageList.add(thumbImg) ;
                    localImageList.add(realImg) ;
                    imageUploadList.add(realImg) ;
                }
            }

            if(mIsEdit){
                mAdapterImageList.add(null) ;
            }

            uploadIndex = localImageList.size() ;
            imageSelectorResultAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 初始化adapter
     */
    private void initPhotoContainer() {
        if(mIsEdit){//放一个占位图，来选择图片
            mAdapterImageList.add(null) ;
        }

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2
                - ResourceUtils.dip2px(mContext , 10) * 2) / 5 ;

        mPictureRv.setLayoutManager(new GridLayoutManager(this, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei,MAX_IMAGE_COUNT ,mIsEdit , new RecyclerViewClickListener() {
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
                    if(mIsEdit && position < MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1){
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
        mPictureRv.setAdapter(imageSelectorResultAdapter);
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
        ImageSelector.open(NeedsConfirmBidActivity.this, imageConfig);
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

    /**
     * 确定报价
     */
    private void chooseBid(){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("NeedId" , StringUtils.convertNull(mNeedId)) ;
        paramMap.put("OfferId" , StringUtils.convertNull(mOfferId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_BID_COMFIRM, paramMap
                , new RequestObjectCallBack<String>("chooseBid" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交成功") ;
                        toConfirmOrder() ;

                        EventBus.getDefault().post(new BusEvent.MySendRefresh(true));

                        setResult(RESULT_OK) ;
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交失败") ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交失败") ;
                    }
                });
    }

    /**
     * 提交报价
     */
    private void submitBid(){
        String time = mTimeEv.getText().toString().trim() ;
        if(StringUtils.convertString2Count(time) <= 0){
            ToastUtils.showToast(mContext ,"请输入正确的完成周期");
            return ;
        }

        String price = mPriceEv.getText().toString() ;
        if(StringUtils.convertString2Float(price) <= 0){
            mPriceEv.requestFocus() ;
            mPriceEv.setSelection(price.length()) ;
            ToastUtils.showToast(mContext ,"请输入正确的金额");
            return ;
        }

        String desc = mDescEv.getText().toString().trim() ;
        if("".equals(desc)){
            mDescEv.requestFocus() ;
            mDescEv.setText("") ;
            ToastUtils.showToast(mContext ,"请输入描述");
            return ;
        }

        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.setCancelable(false) ;
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
        paramMap.put("WorkDays" , time) ;
        paramMap.put("TotalOffer" , price) ;
        paramMap.put("NeedOfferId" , StringUtils.convertNull(mOfferId)) ;
        paramMap.put("OfferDescription" , desc) ;
        paramMap.put("NeedId" , mNeedId) ;
        paramMap.put("Albums" , thumbList) ;

        NetUtils.getDataFromServerByPost(mContext, mIsNeeds ? Urls.NEEDS_MODIFY_BID_NEED
                        : Urls.NEEDS_MODIFY_BID, paramMap
                , new RequestObjectCallBack<String>("submitBid" ,mActivity ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交成功") ;
                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交失败") ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss() ;
                        }
                        ToastUtils.showToast(mContext , "提交失败") ;
                    }
                });
    }

    /**
     * 获取报价详细
     */
    private void getBidDetails(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mOfferId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_BID_DETAILS, paramMap
                , new RequestObjectCallBack<NeedsBidDetails>("getBidDetails" , mContext , NeedsBidDetails.class) {
                    @Override
                    public void onSuccessResult(NeedsBidDetails bean) {
                        showSuccessPage();
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                        updateBidDetails(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage();

                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage();

                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    @Override
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
        }else if(REQUEST_CODE_FOR_OPTION_ITEM == requestCode){
            if(RESULT_OK == resultCode){
                if(null == loadingDialog){
                    loadingDialog = new LoadingDialog(mContext) ;
                }
                loadingDialog.show() ;
                getBidDetails() ;
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
        goCheckPhoto() ;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 100) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("submitBid");
        NetUtils.cancelTag("getBidDetails");
        NetUtils.cancelTag("chooseBid");

    }
}
