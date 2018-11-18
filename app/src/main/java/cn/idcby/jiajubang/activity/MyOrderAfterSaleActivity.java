package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import cn.idcby.jiajubang.Bean.OrderAfterSaleInfo;
import cn.idcby.jiajubang.Bean.WordType;
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
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 订单售后
 * Created on 2018/6/7.
 *
 * 2018-09-25 11:46:06
 * 仅退款，退款金额 不允许修改
 */

public class MyOrderAfterSaleActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private ImageView mGoodIv ;
    private TextView mGoodNameTv ;
    private TextView mServiceTypeTv ;
    private TextView mReasonTv ;
    private EditText mMoneyEv ;
    private EditText mDescEv ;
    private RecyclerView mImageRv ;
    private View mImageLay ;

    private View mReceiverNameLay ;
    private TextView mReceiverNameTv ;
    private View mReceiverPhoneLay ;
    private TextView mReceiverPhoneTv ;
    private View mReceiverAddressLay ;
    private TextView mReceiverAddressTv ;

    private View mExpressNameLay ;
    private TextView mExpressNameTv ;
    private View mExpressNumLay ;
    private TextView mExpressNumTv ;

    private View mServiceTypeLay ;
    private View mReasonLay ;

    //卖家
    private View mOpFinishTv;
    private View mOpAgreeTv;
    private View mOpDisagreeTv;

    //买家
    private View mOpCancelTv;
    private View mOpExpressTv;

    private View mOpLay;

    private Dialog mEditExpressDialog ;//填写物流信息
    private EditText mEditExpressNameEv ;
    private EditText mEditExpressNumEv ;

    private String mOrderId ;
    private String mOrderItemId ;
    private String mOrderType ;
    private String mOrderAmount ;

    private String mServiceType ;
    private String mReasonTypeId ;

    private boolean mIsReceive = false ;
    private boolean mIsEdit = true ;
    private String mAfterSaleId ;
    private OrderAfterSaleInfo mAfterSaleInfo ;

    private static final int REQUEST_CODE_CHOOSE_REASON = 1002 ;
    private static final int REQUEST_CODE_CHOOSE_SERVICE = 1004 ;
    private static final int REQUEST_CODE_AGREE = 1003 ;

    /***图片相关***/
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


    public static void launch(Context context ,boolean isEdit , String afterSaleId, String orderType, String orderId , String itemId
            , String name , String img, String amount,boolean isReceive){
        Intent toAfIt = new Intent(context , MyOrderAfterSaleActivity.class) ;
        toAfIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
        toAfIt.putExtra("orderEditState" ,isEdit) ;
        toAfIt.putExtra("afterSaleId" ,afterSaleId) ;
        toAfIt.putExtra("orderType" ,orderType) ;
        toAfIt.putExtra("orderItemId" ,itemId) ;
        toAfIt.putExtra("orderGoodName" ,name) ;
        toAfIt.putExtra("orderGoodImg" ,img) ;
        toAfIt.putExtra("orderItemAmount" ,amount) ;
        toAfIt.putExtra("orderFromReceive" ,isReceive) ;
        context.startActivity(toAfIt) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_order_after_sale;
    }

    @Override
    public void initView() {
        super.initView();

        mIsReceive = getIntent().getBooleanExtra("orderFromReceive" ,mIsReceive) ;
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;
        mAfterSaleId = getIntent().getStringExtra("afterSaleId") ;
        mOrderType = getIntent().getStringExtra("orderType") ;
        mOrderItemId = getIntent().getStringExtra("orderItemId") ;
        String goodName = getIntent().getStringExtra("orderGoodName") ;
        String goodImg = getIntent().getStringExtra("orderGoodImg") ;
        mOrderAmount = getIntent().getStringExtra("orderItemAmount") ;
        mIsEdit = getIntent().getBooleanExtra("orderEditState" ,mIsEdit) ;

        TextView mTitleTv = findViewById(R.id.acti_order_after_sale_title_tv) ;
        mGoodIv = findViewById(R.id.acti_order_after_sale_good_iv) ;
        mGoodNameTv = findViewById(R.id.acti_order_after_sale_good_name_tv) ;

        mServiceTypeLay = findViewById(R.id.acti_order_after_sale_service_type_lay) ;
        mServiceTypeTv = findViewById(R.id.acti_order_after_sale_service_type_tv) ;
        View mServiceTypeIv = findViewById(R.id.acti_order_after_sale_service_type_iv) ;
        mReasonLay = findViewById(R.id.acti_order_after_sale_reason_lay) ;
        mReasonTv = findViewById(R.id.acti_order_after_sale_reason_tv) ;
        View mReasonIv = findViewById(R.id.acti_order_after_sale_reason_iv) ;
        View mMoneyEditIv = findViewById(R.id.acti_order_after_sale_money_edit_iv) ;
        mMoneyEv = findViewById(R.id.acti_order_after_sale_money_tv) ;
        mDescEv = findViewById(R.id.acti_order_after_sale_desc_ev) ;
        TextView mSubmitTv = findViewById(R.id.acti_good_order_after_sale_submit_tv) ;
        mSubmitTv.setOnClickListener(this);
        mServiceTypeTv.setOnClickListener(this);
        mReasonTv.setOnClickListener(this);
        mMoneyEditIv.setOnClickListener(this);

        mReceiverNameLay = findViewById(R.id.acti_order_after_sale_receiver_name_lay) ;
        mReceiverNameTv = findViewById(R.id.acti_order_after_sale_receiver_name_tv) ;
        mReceiverPhoneLay = findViewById(R.id.acti_order_after_sale_receiver_phone_lay) ;
        mReceiverPhoneTv = findViewById(R.id.acti_order_after_sale_receiver_phone_tv) ;
        mReceiverAddressLay = findViewById(R.id.acti_order_after_sale_receiver_address_lay) ;
        mReceiverAddressTv = findViewById(R.id.acti_order_after_sale_receiver_address_tv) ;

        mExpressNameLay = findViewById(R.id.acti_order_after_sale_express_name_lay) ;
        mExpressNameTv = findViewById(R.id.acti_order_after_sale_express_name_tv) ;
        mExpressNumLay = findViewById(R.id.acti_order_after_sale_express_num_lay) ;
        mExpressNumTv = findViewById(R.id.acti_order_after_sale_express_num_tv) ;

        mOpFinishTv = findViewById(R.id.acti_order_after_sale_op_finish_tv) ;
        mOpAgreeTv = findViewById(R.id.acti_order_after_sale_op_agree_tv) ;
        mOpDisagreeTv = findViewById(R.id.acti_order_after_sale_op_disagree_tv) ;

        mOpCancelTv = findViewById(R.id.acti_order_after_sale_op_cancel_tv) ;
        mOpExpressTv = findViewById(R.id.acti_order_after_sale_op_express_tv) ;
        mOpLay = findViewById(R.id.acti_order_after_sale_option_lay) ;

        mOpFinishTv.setOnClickListener(this);
        mOpAgreeTv.setOnClickListener(this);
        mOpDisagreeTv.setOnClickListener(this);
        mOpCancelTv.setOnClickListener(this);
        mOpExpressTv.setOnClickListener(this);

        loadingDialog = new LoadingDialog(mContext) ;


        if(!mIsEdit){
            mTitleTv.setText("售后详情");

            mServiceTypeTv.setText("");
            mServiceTypeIv.setVisibility(View.GONE);
            mReasonTv.setText("") ;
            mReasonIv.setVisibility(View.GONE);
            mDescEv.setEnabled(false);
            mMoneyEv.setEnabled(false);
            mMoneyEditIv.setVisibility(View.GONE);
            mSubmitTv.setVisibility(View.GONE);

            getAfterSaleDetails() ;
        }else{

            if(!"".equals(StringUtils.convertNull(mAfterSaleId))){
                getAfterSaleDetails() ;
            }else{
                if(SkipUtils.ORDER_TYPE_GOOD.equals(mOrderType)
                        || SkipUtils.ORDER_TYPE_UNUSE.equals(mOrderType)){
                    mServiceTypeLay.setVisibility(View.VISIBLE);
                    mReasonLay.setVisibility(View.VISIBLE) ;
                }else{
                    mServiceTypeLay.setVisibility(View.GONE);
                    mReasonLay.setVisibility(View.GONE) ;
                }

                //先填充之前界面传过来的基本信息
                mGoodNameTv.setText(StringUtils.convertNull(goodName));
                mMoneyEv.setText(StringUtils.convertNull(mOrderAmount));

                if(SkipUtils.ORDER_TYPE_GOOD.equals(mOrderType)
                        || SkipUtils.ORDER_TYPE_UNUSE.equals(mOrderType)){
                    GlideUtils.loader(goodImg ,mGoodIv) ;
                }else{
                    GlideUtils.loaderUser(goodImg,mGoodIv) ;
                }
            }
        }

        mImageLay = findViewById(R.id.acti_order_after_sale_image_lay) ;
        mImageRv = findViewById(R.id.acti_order_after_sale_image_rv) ;
        initPhotoContainer() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_good_order_after_sale_submit_tv == vId){

            if(mIsEdit){
                submitAfterSale() ;
            }

        }else if(R.id.acti_order_after_sale_service_type_tv == vId){//服务类型

            if(mIsEdit){
                SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_AFTER_SALE_SERVICE,REQUEST_CODE_CHOOSE_SERVICE);
            }

        }else if(R.id.acti_order_after_sale_reason_tv == vId){//退款原因

            if(mIsEdit){
                SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_AFTER_SALE_REASON,REQUEST_CODE_CHOOSE_REASON);
            }

        }else if(R.id.acti_order_after_sale_op_finish_tv == vId){//卖家--确认收货
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "确认收货？", null
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    finishAfterSale() ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(R.id.acti_order_after_sale_op_agree_tv == vId){//卖家--同意申请

            if((SkipUtils.ORDER_TYPE_GOOD.equals(mOrderType)
                    || SkipUtils.ORDER_TYPE_UNUSE.equals(mOrderType))){
                MyOrderAfterSaleEditReceiveAddressActivity.launch(mActivity ,mAfterSaleId ,REQUEST_CODE_AGREE);
            }else{
                DialogUtils.showCustomViewDialog(mContext, "温馨提示", "同意该申请？", null
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                agreeServerAfterSale() ;
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }

        }else if(R.id.acti_order_after_sale_op_disagree_tv == vId){//卖家--不同意申请
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "不同意该申请？", null
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            disagreeAfterSale() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_order_after_sale_op_cancel_tv == vId){//买家--取消申请
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "取消该申请？", null
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            cancelAfterSale() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.acti_order_after_sale_op_express_tv == vId){//买家--填写物流信息
            showEditExpressDialog() ;
        }else if(R.id.acti_order_after_sale_money_edit_iv == vId){
            mMoneyEv.requestFocus() ;
            mMoneyEv.setSelection(mMoneyEv.getText().length()) ;
        }
    }

    /**
     * 填写物流信息dialog
     */
    private void showEditExpressDialog(){
        if(null == mEditExpressDialog){
            mEditExpressDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;

            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_receive_order_express ,null) ;
            mEditExpressDialog.setContentView(view);

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F);

            mEditExpressNameEv = view.findViewById(R.id.dialog_receive_order_express_name_ev) ;
            mEditExpressNumEv = view.findViewById(R.id.dialog_receive_order_express_num_ev) ;
            View okTv = view.findViewById(R.id.dialog_receive_order_express_edit_ok_tv) ;
            View cancelTv = view.findViewById(R.id.dialog_receive_order_express_edit_cancel_tv) ;
            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mEditExpressNameEv.getText().toString().trim() ;
                    if("".equals(name)){
                        ToastUtils.showToast(mContext ,"请输入快递或物流公司名称");
                        return ;
                    }
                    String num = mEditExpressNumEv.getText().toString().trim() ;
                    if("".equals(num)){
                        ToastUtils.showToast(mContext ,"请输入快递或物流单号");
                        return ;
                    }

                    mEditExpressDialog.dismiss();

                    addExpressInfo(name,num) ;
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditExpressDialog.dismiss() ;
                }
            });
        }

        mEditExpressNumEv.setText("") ;
        mEditExpressNameEv.setText("") ;

        mEditExpressDialog.show() ;
    }


    /**
     * 填充内容
     */
    private void updateDisplay(){
        if(null == mAfterSaleInfo){
            DialogUtils.showCustomViewDialog(mContext, "售后信息有误，请返回重试", "确定"
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            return ;
        }

        String name = mAfterSaleInfo.getDescription() ;
        String goodImgUrl = mAfterSaleInfo.getImageUrl() ;

        mServiceType = mAfterSaleInfo.getServiceType() ;
        String serviceName = mAfterSaleInfo.getServiceTypeName() ;
        mReasonTypeId = mAfterSaleInfo.getReasonId() ;
        String reasonName = mAfterSaleInfo.getReasonText() ;
        String afterMoney = mAfterSaleInfo.getAfterSaleAmount() ;
        String desc = mAfterSaleInfo.getExplain() ;
        mOrderAmount = mAfterSaleInfo.getTotalPrice() ;

        mOrderType = mAfterSaleInfo.getOrderType() ;
        mOrderId = mAfterSaleInfo.getOrderID() ;
        mOrderItemId = mAfterSaleInfo.getOrderItemID() ;

        String receiver = mAfterSaleInfo.getReciever() ;
        String receiverPhone = mAfterSaleInfo.getRecieverPhone() ;
        String receiverAddress = mAfterSaleInfo.getReceiverAddress() ;

        String expressName = mAfterSaleInfo.getReturnExpressName() ;
        String expressNum = mAfterSaleInfo.getReturnExpressNO() ;

        mGoodNameTv.setText(name);
        if(mAfterSaleInfo.isGoodOrUnuse()){
            GlideUtils.loader(goodImgUrl ,mGoodIv);
        }else{
            GlideUtils.loaderUser(goodImgUrl ,mGoodIv) ;
        }

        if("".equals(mServiceType)){
            if(mIsEdit){
                mServiceTypeTv.setText("请选择");
            }
        }else{
            mServiceTypeTv.setText(serviceName);
        }

        if("".equals(StringUtils.convertNull(mReasonTypeId))){
            if(mIsEdit){
                mReasonTv.setText("请选择");
            }
        }else{
            mReasonTv.setText(reasonName);
        }

        mMoneyEv.setText(afterMoney);
        mDescEv.setText(desc);

        if(!"".equals(receiver) && !"".equals(receiverAddress)){
            mReceiverNameLay.setVisibility(View.VISIBLE);
            mReceiverPhoneLay.setVisibility(View.VISIBLE);
            mReceiverAddressLay.setVisibility(View.VISIBLE);

            mReceiverNameTv.setText(receiver);
            mReceiverPhoneTv.setText(receiverPhone);
            mReceiverAddressTv.setText(receiverAddress);
        }

        if(!"".equals(expressName) && !"".equals(expressNum)){
            mExpressNameLay.setVisibility(View.VISIBLE);
            mExpressNumLay.setVisibility(View.VISIBLE);

            mExpressNameTv.setText(expressName);
            mExpressNumTv.setText(expressNum);
        }

        List<OrderAfterSaleInfo.AfterSaleImage> thumbs = mAfterSaleInfo.getAlbums() ;
        if(thumbs != null && thumbs.size() > 0){
            int size = thumbs.size() ;

            for(int x = size - 1 ; x >= 0 ; x--){
                OrderAfterSaleInfo.AfterSaleImage thumb = thumbs.get(x) ;
                String imgUrl = thumb.getImgUrl() ;

                localImageList.add(0, imgUrl);
                mAdapterImageList.add(0, imgUrl);
                imageUploadList.add(0, imgUrl);
            }

            StringBuffer imgBuilder = new StringBuffer() ;
            for(String imgUrl : imageUploadList){
                imgBuilder.append(imgUrl).append(",") ;
            }

            uploadIndex = localImageList.size() ;
            imageSelectorResultAdapter.notifyDataSetChanged();
        }

        if(!mIsEdit){
            mImageLay.setVisibility(mAdapterImageList.size() == 0 ? View.GONE : View.VISIBLE) ;
        }

        if(!mIsEdit){
            if(mIsReceive){//购买的 编辑、取消、填写物流信息
                if(mAfterSaleInfo.canCancel()){
                    mOpLay.setVisibility(View.VISIBLE);
                    mOpCancelTv.setVisibility(View.VISIBLE);
                }
                if(mAfterSaleInfo.canExpress()){
                    mOpLay.setVisibility(View.VISIBLE);
                    mOpExpressTv.setVisibility(View.VISIBLE);
                }
            }else{//提供的 同意、不同意、确认收货
                if(mAfterSaleInfo.canAgreeOrDis()){
                    mOpLay.setVisibility(View.VISIBLE);
                    mOpAgreeTv.setVisibility(View.VISIBLE);
                    mOpDisagreeTv.setVisibility(View.VISIBLE);
                }

                if(mAfterSaleInfo.canFinish()){
                    mOpLay.setVisibility(View.VISIBLE);
                    mOpFinishTv.setVisibility(View.VISIBLE);
                }
            }
        }

        if(SkipUtils.ORDER_TYPE_GOOD.equals(mOrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(mOrderType)){
            mServiceTypeLay.setVisibility(View.VISIBLE);
            mReasonLay.setVisibility(View.VISIBLE) ;
        }else{
            mServiceTypeLay.setVisibility(View.GONE);
            mReasonLay.setVisibility(View.GONE) ;
        }

    }

    /**
     * 获取详细
     */
    private void getAfterSaleDetails(){
        loadingDialog.setLoadingText("正在加载数据") ;
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_DETAILS, paramMap
                , new RequestObjectCallBack<OrderAfterSaleInfo>("getAfterSaleDetails"
                        ,mContext ,OrderAfterSaleInfo.class) {
                    @Override
                    public void onSuccessResult(OrderAfterSaleInfo bean) {
                        loadingDialog.dismiss();
                        mAfterSaleInfo = bean ;
                        updateDisplay() ;
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
     * 不同意售后申请--确认收货
     */
    private void disagreeAfterSale(){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_DISAGREE, paramMap
                , new RequestObjectCallBack<String>("disagreeAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                setResult(RESULT_OK) ;
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
                    }
                });
    }

    /**
     * 添加物流信息
     */
    private void addExpressInfo(String name ,String num){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(mAfterSaleId)) ;
        paramMap.put("ReturnExpressName" ,StringUtils.convertNull(name)) ;
        paramMap.put("ReturnExpressNO" ,StringUtils.convertNull(num)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_SEND_EXPRESS, paramMap
                , new RequestObjectCallBack<String>("addExpressInfo" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                setResult(RESULT_OK) ;
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
                    }
                });
    }

    /**
     * 完成售后申请--确认收货
     */
    private void finishAfterSale(){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                setResult(RESULT_OK) ;
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
                    }
                });
    }

    /**
     * 同意申请--针对服务、需求
     */
    private void agreeServerAfterSale(){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(mAfterSaleId)) ;
        paramMap.put("ProvinceID" ,"") ;
        paramMap.put("CityID" ,"") ;
        paramMap.put("Address" ,"") ;
        paramMap.put("Reciever" ,"") ;
        paramMap.put("RecieverPhone" ,"") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_AGREE, paramMap
                , new RequestObjectCallBack<String>("agreeAfterSale",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "提交成功"
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
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 取消售后申请
     */
    private void cancelAfterSale(){
        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("code" ,StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_CANCEL, paramMap
                , new RequestObjectCallBack<String>("cancelAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "操作成功", "确定"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                setResult(RESULT_OK) ;
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
                    }
                });
    }

    /**
     * 提交售后申请/修改
     */
    private void submitAfterSale(){
        boolean isGood = SkipUtils.ORDER_TYPE_GOOD.equals(mOrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(mOrderType) ;

        if(isGood && "".equals(StringUtils.convertNull(mServiceType))){
            ToastUtils.showToast(mContext ,"请选择服务类型") ;
            return ;
        }
        if(isGood && "".equals(StringUtils.convertNull(mReasonTypeId))){
            ToastUtils.showToast(mContext ,"请选择退款原因") ;
            return ;
        }

        float money = StringUtils.convertString2Float(mMoneyEv.getText().toString()) ;
        if(money <= 0F){
            mMoneyEv.setSelection(mMoneyEv.getText().length()) ;
            mMoneyEv.requestFocus() ;
            ToastUtils.showToast(mContext ,"请输入正确的退款金额") ;
            return ;
        }
        if(money > StringUtils.convertString2Float(mOrderAmount)){
            mMoneyEv.setSelection(mMoneyEv.getText().length()) ;
            mMoneyEv.requestFocus() ;
            ToastUtils.showToast(mContext ,"退款金额不能大于订单金额") ;
            return ;
        }

        String explain = mDescEv.getText().toString().trim() ;
//        if(isGood && "".equals(explain)){
//            mDescEv.setText("");
//            mDescEv.requestFocus() ;
//            ToastUtils.showToast(mContext ,"请输入退款说明") ;
//            return ;
//        }

        loadingDialog.setLoadingText("正在提交") ;
        loadingDialog.setCancelable(false);
        loadingDialog.show() ;

        String thumbList = "" ;
        if(imageUploadList.size() > 0){
            for(String imgurl : imageUploadList){
                thumbList += (imgurl + ",") ;
            }
            thumbList = thumbList.substring(0,thumbList.length() - 1) ;
        }


        String afterSaleId = "" ;
        if(mAfterSaleInfo != null){
            afterSaleId = mAfterSaleInfo.getOrderAfterSaleId() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("OrderAfterSaleId" ,StringUtils.convertNull(afterSaleId)) ;
        paramMap.put("OrderType" ,StringUtils.convertNull(mOrderType)) ;
        paramMap.put("OrderID" ,StringUtils.convertNull(mOrderId)) ;
        paramMap.put("OrderItemID" ,StringUtils.convertNull(mOrderItemId)) ;
        paramMap.put("ReasonId" ,StringUtils.convertNull(mReasonTypeId)) ;
        paramMap.put("Explain" ,explain) ;
        paramMap.put("AfterSaleAmount" ,"" + money) ;
        paramMap.put("Albums" ,thumbList) ;
        if(!"".equals(mServiceType)){
            paramMap.put("ServiceType" ,mServiceType) ;
        }

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_SUBMIT, paramMap
                , new RequestObjectCallBack<String>("submitAfterSale" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "申请提交成功", "确定"
                                , new DialogInterface.OnClickListener() {
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
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 初始化图片
     */
    private void initPhotoContainer() {
        if(mIsEdit){
            mAdapterImageList.add(null) ;//默认图
        }

        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext , 15)* 2) / 5 ;

        mImageRv.setLayoutManager(new GridLayoutManager(this, 5));
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
                    if(mIsEdit){
                        if(position < MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1){
                            //选择图片
                            checkPermission();
                        }else{
                            SkipUtils.toImageShowActivity(mActivity , localImageList ,position);
                        }
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
        }else if(REQUEST_CODE_CHOOSE_REASON == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<WordType> wordType = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                mReasonTypeId = "" ;
                String name = "" ;
                if(wordType != null && wordType.size() > 0){
                    mReasonTypeId = wordType.get(0).getItemDetailId() ;
                    name = wordType.get(0).getItemName() ;
                }

                if("".equals(name)){
                    mReasonTv.setText("请选择");
                }else{
                    mReasonTv.setText(name);
                }
            }
        }else if(REQUEST_CODE_CHOOSE_SERVICE == requestCode){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<WordType> wordType = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);
                mServiceType = "" ;
                String name = "" ;
                if(wordType != null && wordType.size() > 0){
                    mServiceType = wordType.get(0).getItemValue() ;
                    name = wordType.get(0).getItemName() ;
                }

                if("".equals(name)){
                    mServiceTypeTv.setText("请选择");
                }else{
                    mServiceTypeTv.setText(name);
                }

                if(SkipUtils.AFTER_SERVER_TYPE_MONEY.equals(mServiceType)){//仅退款
                    mMoneyEv.setEnabled(false);
                    mMoneyEv.setText(StringUtils.convertNull(mOrderAmount)) ;
                }else{//退货退款
                    mMoneyEv.setEnabled(true);
                }
            }
        }else if(REQUEST_CODE_AGREE == requestCode){
            if(RESULT_OK == resultCode){
                setResult(RESULT_OK);
                finish() ;
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
