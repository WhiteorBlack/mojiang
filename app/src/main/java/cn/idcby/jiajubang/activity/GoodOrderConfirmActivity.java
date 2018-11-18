package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.GoodOrderConfirmList;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.OrderDetialBean;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodOrderConfirm;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 商品订单--确认订单
 * Created on 2018/4/24.
 *
 * 2018-07-02 16:23:58
 * 下单的时候，选择配送方式
 *
 * 2018-07-05 18:21:21
 * 放弃下单成功之后跳转到付款
 * ，改为弹出一个提示框：等待卖家修改运费，如果未与卖家达成一致
 * ， 可能导致订单失败，稍后请去我的订单》待付款中去支付
 */

public class GoodOrderConfirmActivity extends BaseMoreStatusActivity{
    private RecyclerView mRecyclerView;
    private TextView mAllMoneyTv;

    //header
    private View mNullAddressLay;
    private View mUserNameLay;
    private TextView mUserNameTv;
    private TextView mAddressNameTv;

    private boolean mIsCart = false;//是否是购物车--true 购物车，fasle直接购买
    private boolean mIsUnuse = false;//是否是闲置--true 闲置，fasle直供
    private String mSkuID;//规格id，闲置传空
    private String mQuantity;//数量
    private String mProductNature;//1.直供2.闲置
    private String mProductId;//

    private String mCartId;

    private ReceiveAddress mChooseAddress ;

    private int mCurPosition ;

    private HeaderFooterAdapter<AdapterGoodOrderConfirm> mAdapter;
    private List<GoodOrderConfirmList> mDataList = new ArrayList<>() ;


    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_CHOOSE_ADDRESS = 1001 ;
    private static final int REQUEST_CODE_CHOOSE_DELIVERY = 1002 ;


    public static void launch(Context context, boolean isUnuse
            , String skuId, String quantity, String productId) {
        Intent toCmIt = new Intent(context, GoodOrderConfirmActivity.class);
        toCmIt.putExtra(SkipUtils.INTENT_GOOD_ID, productId);
        toCmIt.putExtra(SkipUtils.INTENT_GOOD_QUANTITY, quantity);
        toCmIt.putExtra(SkipUtils.INTENT_GOOD_SKU_ID, skuId);
        toCmIt.putExtra(SkipUtils.INTENT_IS_UNUSE, isUnuse);
        toCmIt.putExtra(SkipUtils.INTENT_IS_CART, false);
        context.startActivity(toCmIt);
    }

    public static void launchCart(Activity context, String cartId, int requestCode) {
        Intent toCmIt = new Intent(context, GoodOrderConfirmActivity.class);
        toCmIt.putExtra(SkipUtils.INTENT_IS_CART, true);
        toCmIt.putExtra(SkipUtils.INTENT_CART_ID, cartId);
        context.startActivityForResult(toCmIt, requestCode);
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_good_order_confirm;
    }

    @Override
    public String setTitle() {
        return "确认订单";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        mIsCart = intent.getBooleanExtra(SkipUtils.INTENT_IS_CART, mIsCart);
        mIsUnuse = intent.getBooleanExtra(SkipUtils.INTENT_IS_UNUSE, mIsUnuse);
        mProductId = intent.getStringExtra(SkipUtils.INTENT_GOOD_ID);
        mQuantity = intent.getStringExtra(SkipUtils.INTENT_GOOD_QUANTITY);
        mSkuID = intent.getStringExtra(SkipUtils.INTENT_GOOD_SKU_ID);
        mCartId = intent.getStringExtra(SkipUtils.INTENT_CART_ID);
        mProductNature = mIsUnuse ? "2" : "1";
        if (mIsUnuse) {
            mSkuID = "";
        }

        mRecyclerView = findViewById(R.id.acti_good_order_confirm_rv);
        mAllMoneyTv = findViewById(R.id.acti_good_order_confirm_all_money_tv);
        TextView mSubmitTv = findViewById(R.id.acti_good_order_confirm_sub_tv);
        mSubmitTv.setOnClickListener(this);

        initAdapterAndHeader();
    }

    /**
     * header and footer
     */
    private void initAdapterAndHeader() {
        AdapterGoodOrderConfirm orderAdapter = new AdapterGoodOrderConfirm(mContext, mIsUnuse, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){//选择配送方式
                    mCurPosition = position ;

                    SkipUtils.toWordTypeActivity(mActivity ,SkipUtils.WORD_TYPE_DELIVERY_TYPE,REQUEST_CODE_CHOOSE_DELIVERY) ;
                }
            }
        });
        mAdapter = new HeaderFooterAdapter<>(orderAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_good_order_confirm, null);
        View mainLay = headerView.findViewById(R.id.header_good_order_confirm_main_lay);
        mUserNameLay = headerView.findViewById(R.id.header_good_order_confirm_user_name_lay);
        mUserNameTv = headerView.findViewById(R.id.header_good_order_confirm_user_name_tv);
        mNullAddressLay = headerView.findViewById(R.id.header_good_order_confirm_address_null_lay);
        mAddressNameTv = headerView.findViewById(R.id.header_good_order_confirm_user_address_tv);
        mainLay.setOnClickListener(this);

        mAdapter.addHeader(headerView);
    }

    @Override
    public void requestData() {
        showSuccessPage();

        getOrderData();
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();
        if (vId == R.id.acti_good_order_confirm_sub_tv) {
            submitOrder() ;
        }else if (vId==R.id.header_good_order_confirm_main_lay){
            MyAddressActivity.launch(mActivity ,null == mChooseAddress
                    ? "" : mChooseAddress.getAddressId()
                    ,REQUEST_CODE_CHOOSE_ADDRESS);
        }

    }

    /**
     * 更新数据
     */
    private void updateDisplay(OrderDetialBean bean){
        if(bean != null){
            convertGoodByGroup(bean.getCartModelList()) ;

            mAllMoneyTv.setText("¥" + bean.getTotalPrice());
            mChooseAddress = bean.getUaeEntity() ;

            updateAddressDisplay() ;
        }else{
            DialogUtils.showCustomViewDialog(mContext, "信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
        }
    }

    /**
     * 填充地址
     */
    private void updateAddressDisplay(){
        if(mChooseAddress != null && !"".equals(mChooseAddress.getAddressId())){
            mNullAddressLay.setVisibility(View.GONE);
            mUserNameLay.setVisibility(View.VISIBLE);
            mAddressNameTv.setVisibility(View.VISIBLE);

            mUserNameTv.setText(mChooseAddress.getContacts() + "      " + mChooseAddress.getAccount());
            mAddressNameTv.setText(mChooseAddress.getProvinceName()
                    + mChooseAddress.getCityName()
                    + mChooseAddress.getCountyName()
                    + mChooseAddress.getAddress());
        }else{
            mNullAddressLay.setVisibility(View.VISIBLE);
            mUserNameLay.setVisibility(View.GONE);
            mAddressNameTv.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据
     */
    private void getOrderData() {
        Map<String, String> para = ParaUtils.getParaWithToken(mContext);

        if(mIsCart){
            para.put("CartId", StringUtils.convertNull(mCartId)) ;
        }else{
            para.put("SkuID", mSkuID);
            para.put("Quantity", mQuantity);
            para.put("ProductNature", mProductNature);
            para.put("ProductId", mProductId);
        }

        NetUtils.getDataFromServerByPost(mContext, mIsCart ? Urls.CONFIRM_GOOD_ORDER_CART
                        : Urls.DIRECT_SPEC_GOOD_ORDE_CONFIRMORDER, para,
                new RequestObjectCallBack<OrderDetialBean>("订单详情", mContext, OrderDetialBean.class) {
                    @Override
                    public void onSuccessResult(OrderDetialBean bean) {
                        updateDisplay(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay(null);
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateDisplay(null);
                    }
                });
    }

    /**
     * 提交订单
     */
    private void submitOrder(){
        if(null == mChooseAddress
                || "".equals(StringUtils.convertNull(mChooseAddress.getAddressId()))){
            ToastUtils.showToast(mContext ,"请选择收货地址");
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        StringBuffer infoBud = new StringBuffer() ;
        for (GoodOrderConfirmList confirmList : mDataList) {
           String storeId = confirmList.getStoreId() ;

           String deliveryId = "" ;
           WordType deliveryType = confirmList.getDeliveryInfo() ;
           if(deliveryType != null && !"".equals(deliveryType.getItemDetailId())){
               deliveryId = deliveryType.getItemDetailId() ;
           }

           String message = confirmList.getMessageInfo() ;
            infoBud.append("{\"ShopInfoId\":\"").append(storeId).append("\",")
                    .append("\"DeliveryType\":\"").append(deliveryId).append("\",")
                    .append("\"Message\":\"").append(message).append("\"},") ;
        }

        String messageInfo = "" ;
        if(infoBud.length() > 1){
            messageInfo = infoBud.substring(0 ,infoBud.length() - 1) ;
        }

        //注意：SkuId,可以从返回的数据中获取（闲置商品没有传SkuId，但是返回有）
        if(mIsUnuse){
            if(mDataList.size() > 0){
                GoodOrderConfirmList confirmList = mDataList.get(0) ;
                if(confirmList != null && confirmList.getGoodList() != null && confirmList.getGoodList().size() > 0){
                    mSkuID = confirmList.getGoodList().get(0).getSkuID() ;
                }
            }
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("CartID" ,StringUtils.convertNull(mCartId)) ;
        paramMap.put("SkuID" ,StringUtils.convertNull(mSkuID)) ;
        paramMap.put("Quantity" ,StringUtils.convertNull(mQuantity)) ;
        paramMap.put("AddressID" ,StringUtils.convertNull(mChooseAddress.getAddressId())) ;
        paramMap.put("ShopMessageList" ,"[" + messageInfo + "]") ;
        paramMap.put("OrderType" ,mIsUnuse ? "2" : "1") ;//1商品订单 ，2闲置商品订单

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_ORDER_SUBMIT, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("submitOrder" ,mContext ,NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mDialog.dismiss() ;

                        DialogUtils.showCustomViewDialog(mContext,"下单成功!"
                                , "请等待卖家修改运费，如果未与卖家达成一致，可能导致订单失败，稍后请去我的订单->待付款中去支付",null, "我知道了"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish() ;
                            }
                        });

//
//                        if(bean != null){
//                            String moneys = bean.getOrderAmount() ;
//                            //跳转到付款界面
//                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
//                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
//                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,bean.OrderID) ;
//                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
//                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_MALL) ;
//                            startActivity(toPyIt);
//                            finish() ;
//                        }else{
//                            ToastUtils.showErrorToast(mContext , "提交成功，需要付款") ;
//                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss() ;

                        ToastUtils.showToast(mContext,"订单提交失败！");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_ADDRESS){
            if(RESULT_OK == resultCode && data != null){
                mChooseAddress = (ReceiveAddress) data.getSerializableExtra(SkipUtils.INTENT_ADDRESS_INFO);
                updateAddressDisplay();
            }
        }else if (requestCode == REQUEST_CODE_CHOOSE_DELIVERY){
            if(RESULT_OK == resultCode && data != null){
                ArrayList<WordType> mChooseDelivery = (ArrayList<WordType>) data.getSerializableExtra(SkipUtils.INTENT_WORD_TYPE_INFO);

                if(mChooseDelivery != null && mChooseDelivery.size() > 0){
                    mDataList.get(mCurPosition).setDeliveryInfo(mChooseDelivery.get(0)) ;
                }

                mAdapter.notifyDataSetChanged() ;
            }
        }
    }

    /**
     * 分组
     */
    private void convertGoodByGroup(List<OrderDetialBean.CartModelListBean> cartModelList){
        if(cartModelList != null){
            List<String> storeIdList = new ArrayList<>() ;
            for(OrderDetialBean.CartModelListBean model : cartModelList){
                String storeId = model.getMerchantID() ;
                if(!storeIdList.contains(storeId)){
                    storeIdList.add(storeId) ;
                }
            }

            for(String storeId : storeIdList){
                GoodOrderConfirmList confirmList = new GoodOrderConfirmList() ;
                confirmList.setStoreId(storeId);

                List<OrderDetialBean.CartModelListBean> goodList = new ArrayList<>() ;
                String name = "" ;
                for(OrderDetialBean.CartModelListBean model : cartModelList){
                    String ids = model.getMerchantID() ;

                    if(storeId.equals(ids)){
                        if("".equals(name)){
                            name = model.getName() ;
                        }

                        goodList.add(model) ;
                    }
                }

                confirmList.setStoreName(name);
                confirmList.setGoodList(goodList);

                mDataList.add(confirmList) ;
            }

            mAdapter.notifyDataSetChanged() ;
        }
    }

}