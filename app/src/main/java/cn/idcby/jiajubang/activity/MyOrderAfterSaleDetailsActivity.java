package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.OrderAfterSaleInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 退款详细
 * Created on 2018/8/9.
 */

public class MyOrderAfterSaleDetailsActivity extends BaseActivity {
    private TextView mAfResultTypeTv ;
    private TextView mAfResultStateTv ;
    private TextView mAfAllMoneyTv ;
    private ImageView mAfIv ;
    private TextView mAfNameTv ;
    private TextView mAfTypeTv ;
    private TextView mAfReasonTv ;
    private TextView mAfDescTv ;
    private TextView mAfMoneyTv ;
    private TextView mAfTimeTv ;
    private TextView mAfNumberTv ;

    private String mAfterSaleId ;
    private OrderAfterSaleInfo mDetails ;
    private LoadingDialog mDialog ;


    public static void launch(Context context ,String afterId){
        Intent toDiIt = new Intent(context ,MyOrderAfterSaleDetailsActivity.class) ;
        toDiIt.putExtra(SkipUtils.INTENT_ORDER_AFTER_SALE_ID ,afterId) ;
        context.startActivity(toDiIt) ;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_order_after_sale_details;
    }

    @Override
    public void initView() {
        super.initView();

        mAfterSaleId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_AFTER_SALE_ID) ;
        if(TextUtils.isEmpty(mAfterSaleId)){
            DialogUtils.showCustomViewDialog(mContext, "内容有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    finish() ;
                }
            });
            return ;
        }

        mAfResultTypeTv = findViewById(R.id.acti_order_after_sale_result_type_tv) ;
        mAfResultStateTv = findViewById(R.id.acti_order_after_sale_result_tv) ;
        mAfAllMoneyTv = findViewById(R.id.acti_order_after_sale_all_money_tv) ;
        mAfIv = findViewById(R.id.acti_order_after_sale_iv) ;
        mAfNameTv = findViewById(R.id.acti_order_after_sale_name_tv) ;
        mAfTypeTv = findViewById(R.id.acti_order_after_sale_type_tv) ;
        mAfReasonTv = findViewById(R.id.acti_order_after_sale_reason_tv) ;
        mAfDescTv = findViewById(R.id.acti_order_after_sale_desc_tv) ;
        mAfMoneyTv = findViewById(R.id.acti_order_after_sale_money_tv) ;
        mAfTimeTv = findViewById(R.id.acti_order_after_sale_time_tv) ;
        mAfNumberTv = findViewById(R.id.acti_order_after_sale_number_tv) ;
        View mHistoryLay = findViewById(R.id.acti_order_after_sale_history_lay) ;
        mHistoryLay.setOnClickListener(this);

        mDialog = new LoadingDialog(mContext) ;
        getAfterSaleDetails() ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_order_after_sale_history_lay == vId){//协商历史
            SkipUtils.toShowWebActivity(mContext,"协商历史" ,mDetails.getASConsultativeHistory());
        }

    }

    /**
     * 填充数据
     */
    private void updateDisplay(){
        if(null == mDetails){
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

        String statusName = mDetails.getStatusName() ;
        String allMoney = mDetails.getAfterSaleAmount() ;

        String name = mDetails.getDescription() ;
        String goodImgUrl = mDetails.getImageUrl() ;

        String serviceName = mDetails.getServiceTypeName() ;
        String reasonName = mDetails.getReasonText() ;
        String desc = mDetails.getExplain() ;

        String time = mDetails.getCreateDate() ;
        String afCode = mDetails.getAfterSaleCode() ;

        mAfResultTypeTv.setText(serviceName);
        mAfResultStateTv.setText(statusName);
        mAfAllMoneyTv.setText("¥" + allMoney);

        mAfNameTv.setText(name);
        GlideUtils.loader(goodImgUrl,mAfIv) ;

        mAfTypeTv.setText("服务类型：" + serviceName);
        mAfReasonTv.setText("退款原因：" + reasonName);
        mAfDescTv.setText(desc);
        mAfMoneyTv.setText("退款金额：¥" + allMoney);
        mAfTimeTv.setText("申请时间：" + time);
        mAfNumberTv.setText("退款编号：" +  afCode);

        if(mDetails.isGoodOrUnuse()){
            mAfTypeTv.setVisibility(View.VISIBLE) ;
            mAfReasonTv.setVisibility(View.VISIBLE);
        }else{
            mAfTypeTv.setVisibility(View.GONE);
            mAfReasonTv.setVisibility(View.GONE) ;
        }
    }

    /**
     * 获取详细
     */
    private void getAfterSaleDetails(){
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mAfterSaleId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.ORDER_AFTER_SALE_DETAILS, paramMap
                , new RequestObjectCallBack<OrderAfterSaleInfo>("getAfterSaleDetails"
                        ,mContext ,OrderAfterSaleInfo.class) {
                    @Override
                    public void onSuccessResult(OrderAfterSaleInfo bean) {
                        mDialog.dismiss();
                        mDetails = bean ;
                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                        updateDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        updateDisplay() ;
                    }
                });
    }


}
