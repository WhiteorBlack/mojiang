package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.Bean.MyReceiveOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemMoreViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RecyclerViewFocusble;

/**
 * 我的订单列表
 * Created on 2018/5/7.
 *
 * 2018-07-03 15:25:06
 * 订单只有 商品订单（包括闲置）和服务订单了，去掉了需求订单
 *
 * 当前是针对买家的：
 * 订单状态
 * 待付款 取消
 * 待发货 发起售后
 * 待收货 确认收货  发起售后
 * 待评价 评价 删除 发起售后
 * 已取消 删除
 * 已完成 删除 发起售后
 *
 * 服务订单
 * 待付款 付款 取消
 * 待服务 发起售后
 * 服务中 完成 发起售后
 * 待评价 评价 删除 发起售后
 * 已取消 删除
 * 已完成 删除 发起售后
 *
 * 2018-08-28 19:33:12
 * 闲置商品没有评价
 */

public class AdapterMyReceiveOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private List<MyReceiveOrderList> mDataList ;
    private RvItemMoreViewClickListener mClickListener ;

    public static final int TYPE_OP_ITEM = 0 ;
    public static final int TYPE_OP_COMMENT_SERVER = 1 ;
    public static final int TYPE_OP_FINISH_SERVER = 2 ;
    public static final int TYPE_OP_PAY_SERVER = 3 ;
    public static final int TYPE_OP_CANCEL_SERVER = 14 ;
    public static final int TYPE_OP_EDIT_SERVER = 15 ;

    public static final int TYPE_OP_COMMENT_GOOD = 4 ;
    public static final int TYPE_OP_FINISH_GOOD = 5 ;
    public static final int TYPE_OP_PAY_GOOD = 6 ;
    public static final int TYPE_OP_CANCEL_GOOD = 7 ;

    public static final int TYPE_OP_PAY_NEED = 8 ;
    public static final int TYPE_OP_CANCEL_NEED = 9 ;
    public static final int TYPE_OP_FINISH_NEED = 10 ;

    public static final int TYPE_OP_DELETE = 11 ;
    public static final int TYPE_OP_AFTER_SALE_OTHER = 12 ;//服务、需求的售后
    public static final int TYPE_OP_AFTER_SALE_GOOD = 13 ;//商品的售后

    public static final int ITEM_TYPE_ONE = 0 ;
    public static final int ITEM_TYPE_TWO = 1 ;

    public AdapterMyReceiveOrder(Context context, List<MyReceiveOrderList> mDataList, RvItemMoreViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        MyReceiveOrderList orderList = mDataList.get(position) ;

        if(orderList.isGood() || orderList.isUnuse()){
            return ITEM_TYPE_TWO ;
        }

        return ITEM_TYPE_ONE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(ITEM_TYPE_ONE == viewType){
            return new ReceiveOneHolder(LayoutInflater.from(context)
                    .inflate(R.layout.adapter_my_receive_order ,parent ,false) ,mClickListener) ;
        }
        return new ReceiveTwoHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_receive_order_good ,parent ,false) ,mClickListener) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReceiveOneHolder){
            ReceiveOneHolder mHolder = (ReceiveOneHolder) holder;

            final MyReceiveOrderList info = mDataList.get(position) ;
            String typeName = info.getOrderTypeName() ;
            String orderTitle = info.getName() ;
            String time = info.getCreateDate() ;
            String orderStatusName = info.getStatusName() ;
            String imgUrl = info.getImageUrl() ;
            String desc = info.getDescription() ;
            String money = info.getOrderAmount() ;

            mHolder.mTypeTv.setText(typeName) ;
            if(info.isGood() || info.isUnuse()){
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_good_bg)) ;
            }else if(info.isServer()){
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_server_bg)) ;
            }else {
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_need_bg)) ;
            }

            mHolder.mTitleTv.setText(orderTitle);
            mHolder.mStatusTv.setText(orderStatusName);
            mHolder.mTimeTv.setText(time);
            mHolder.mPriceTv.setText(money);
            mHolder.mDescTv.setText(desc);
            GlideUtils.loaderUser(imgUrl ,mHolder.mUserIv) ;
            mHolder.mOpCancelGoodTv.setVisibility(View.GONE);
            mHolder.mOpPayGoodTv.setVisibility(View.GONE);
            mHolder.mOpFinishGoodTv.setVisibility(View.GONE);
            mHolder.mOpCommentGoodTv.setVisibility(View.GONE);

            mHolder.mOpPayServerTv.setVisibility(View.GONE);
            mHolder.mOpFinishServerTv.setVisibility(View.GONE);
            mHolder.mOpCommentServerTv.setVisibility(View.GONE);
            mHolder.mOpCancelServerTv.setVisibility(View.GONE);
            mHolder.mOpEditServerTv.setVisibility(View.GONE);

            mHolder.mOpPayNeedTv.setVisibility(View.GONE);
            mHolder.mOpCancelNeedTv.setVisibility(View.GONE);
            mHolder.mOpFinishNeedTv.setVisibility(View.GONE);

            mHolder.mOpDeleteTv.setVisibility(info.canDelete() ? View.VISIBLE : View.GONE);
            mHolder.mOpAfterSaleTv.setVisibility(info.canAfterSale() ? View.VISIBLE : View.GONE) ;

            String status = info.getOrderStatus() ;

            if(info.isServer()){
                if("1".equals(status)){//待付款 -- 付款、取消、编辑
                    mHolder.mOpEditServerTv.setVisibility(View.VISIBLE);
                    mHolder.mOpPayServerTv.setVisibility(View.VISIBLE);
                    mHolder.mOpCancelServerTv.setVisibility(View.VISIBLE);
                }else if("3".equals(status)){//服务中 -- 完成
                    mHolder.mOpFinishServerTv.setVisibility(View.VISIBLE);
                }else if("4".equals(status)){//待评价 -- 评价
                    mHolder.mOpCommentServerTv.setVisibility(View.VISIBLE);
                }
            }
        }else if(holder instanceof ReceiveTwoHolder){//商品订单
            ReceiveTwoHolder mHolder = (ReceiveTwoHolder) holder;

            final MyReceiveOrderList info = mDataList.get(position) ;
            String typeName = info.getOrderTypeName() ;
            String orderTitle = info.getName() ;
            String time = info.getCreateDate() ;
            String orderStatusName = info.getStatusName() ;
            String orderAmount = info.getOrderAmount() ;

            mHolder.mTypeTv.setText(typeName) ;
            if(info.isGood() || info.isUnuse()){
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_good_bg)) ;
            }else if(info.isServer()){
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_server_bg)) ;
            }else {
                mHolder.mTypeTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_type_need_bg)) ;
            }

            mHolder.mTitleTv.setText(orderTitle);
            mHolder.mStatusTv.setText(orderStatusName);
            mHolder.mTimeTv.setText(time);

            int count = StringUtils.convertString2Count(info.getOrderItemCount()) ;
            mHolder.mGoodAmountTv.setText(orderAmount);
            mHolder.mGoodCountTv.setText("共" + count + "件商品，合计");

            boolean canAfterSale = info.canAfterSale() ;
            boolean isGood = info.isGood() ;

            List<GoodOrderGood> goodList = info.getOrderItem() ;
            AdapterMyReceiveOrderGoodList goodAdapter = new AdapterMyReceiveOrderGoodList(context,canAfterSale
                    ,goodList,position ,mClickListener) ;
            mHolder.mRv.setLayoutManager(new LinearLayoutManager(context));
            mHolder.mRv.setAdapter(goodAdapter);

            String status = info.getOrderStatus() ;

            mHolder.mOpCancelGoodTv.setVisibility(View.GONE);
            mHolder.mOpPayGoodTv.setVisibility(View.GONE);
            mHolder.mOpFinishGoodTv.setVisibility(View.GONE);
            mHolder.mOpCommentGoodTv.setVisibility(View.GONE);

            mHolder.mOpPayServerTv.setVisibility(View.GONE);
            mHolder.mOpFinishServerTv.setVisibility(View.GONE);
            mHolder.mOpCommentServerTv.setVisibility(View.GONE);
            mHolder.mOpCancelServerTv.setVisibility(View.GONE);
            mHolder.mOpEditServerTv.setVisibility(View.GONE);

            mHolder.mOpDeleteTv.setVisibility(info.canDelete() ? View.VISIBLE : View.GONE);

            if("1".equals(status)){//待付款
                mHolder.mOpCancelGoodTv.setVisibility(View.VISIBLE);
                mHolder.mOpPayGoodTv.setVisibility(View.VISIBLE);
            }else if("3".equals(status)){//待收货
                mHolder.mOpFinishGoodTv.setVisibility(View.VISIBLE);
            }else if("4".equals(status) && isGood){//待评价--只有商品才有评价，闲置么有评价
                mHolder.mOpCommentGoodTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class ReceiveOneHolder extends RecyclerView.ViewHolder{
        private TextView mTypeTv;
        private TextView mTitleTv;
        private TextView mStatusTv ;
        private TextView mTimeTv ;

        private View mOpFinishGoodTv;
        private View mOpCancelGoodTv;
        private View mOpPayGoodTv;
        private View mOpCommentGoodTv;

        //服务操作
        private View mOpCommentServerTv;
        private View mOpFinishServerTv;
        private View mOpPayServerTv;
        private View mOpCancelServerTv;
        private View mOpEditServerTv;

        //需求操作
        private TextView mOpFinishNeedTv;
        private TextView mOpCancelNeedTv;
        private TextView mOpPayNeedTv;

        private View mOpDeleteTv;
        private View mOpAfterSaleTv;
        private View mOpAfterSaleFinishTv;

        private ImageView mUserIv ;
        private TextView mDescTv ;
        private TextView mPriceTv ;

        public ReceiveOneHolder(View view , final RvItemMoreViewClickListener mClickListener) {
            super(view);

            mTypeTv = view.findViewById(R.id.adapter_my_receive_order_type_tv) ;
            mTitleTv = view.findViewById(R.id.adapter_my_receive_order_title_tv) ;
            mStatusTv = view.findViewById(R.id.adapter_my_receive_order_status_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_my_receive_order_time_tv) ;

            mOpFinishGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_finish_tv) ;
            mOpCancelGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_cancel_tv) ;
            mOpPayGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_pay_tv) ;
            mOpCommentGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_comment_tv) ;

            mOpCommentServerTv = view.findViewById(R.id.adapter_my_receive_order_op_comment_server) ;
            mOpFinishServerTv = view.findViewById(R.id.adapter_my_receive_order_op_finish_server) ;
            mOpPayServerTv = view.findViewById(R.id.adapter_my_receive_order_op_pay_server) ;
            mOpCancelServerTv = view.findViewById(R.id.adapter_my_receive_order_op_cancel_server) ;
            mOpEditServerTv = view.findViewById(R.id.adapter_my_receive_order_op_edit_server) ;

            mOpFinishNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_finish_need_tv) ;
            mOpCancelNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_cancel_need_tv) ;
            mOpPayNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_pay_need_tv) ;

            mOpDeleteTv = view.findViewById(R.id.adapter_my_receive_order_options_delete_tv) ;

            mOpAfterSaleTv = view.findViewById(R.id.adapter_my_receive_order_after_sale_tv) ;
            mOpAfterSaleFinishTv = view.findViewById(R.id.adapter_my_receive_order_after_sale_finish_tv) ;

            mUserIv = view.findViewById(R.id.adapter_my_receive_order_user_iv) ;
            mDescTv = view.findViewById(R.id.adapter_my_receive_order_desc_tv) ;
            mPriceTv = view.findViewById(R.id.adapter_my_receive_order_money_tv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_ITEM,getAdapterPosition()) ;
                    }
                }
            });
            mOpCommentServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_COMMENT_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpPayServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpEditServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_EDIT_SERVER,getAdapterPosition()) ;
                    }
                }
            });

            mOpCommentGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_COMMENT_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpPayGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_DELETE,getAdapterPosition()) ;
                    }
                }
            });

            mOpPayNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_NEED ,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_NEED ,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_NEED ,getAdapterPosition()) ;
                    }
                }
            });
            mOpAfterSaleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_AFTER_SALE_OTHER ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }

    static class ReceiveTwoHolder extends RecyclerView.ViewHolder{
        private TextView mTypeTv;
        private TextView mTitleTv;
        private TextView mStatusTv ;
        private TextView mTimeTv ;

        private View mOpFinishGoodTv;
        private View mOpCancelGoodTv;
        private View mOpPayGoodTv;
        private View mOpCommentGoodTv;

        //服务操作
        private View mOpCommentServerTv;
        private View mOpFinishServerTv;
        private View mOpPayServerTv;
        private View mOpCancelServerTv;
        private View mOpEditServerTv;

        //需求操作
        private TextView mOpFinishNeedTv;
        private TextView mOpCancelNeedTv;
        private TextView mOpPayNeedTv;

        private View mOpDeleteTv;

        private RecyclerViewFocusble mRv ;
        private TextView mGoodCountTv ;
        private TextView mGoodAmountTv ;

        public ReceiveTwoHolder(View view ,final RvItemMoreViewClickListener mClickListener) {
            super(view);

            mTypeTv = view.findViewById(R.id.adapter_my_receive_order_type_tv) ;
            mTitleTv = view.findViewById(R.id.adapter_my_receive_order_title_tv) ;
            mStatusTv = view.findViewById(R.id.adapter_my_receive_order_status_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_my_receive_order_time_tv) ;

            mOpFinishGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_finish_tv) ;
            mOpCancelGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_cancel_tv) ;
            mOpPayGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_pay_tv) ;
            mOpCommentGoodTv = view.findViewById(R.id.adapter_my_receive_order_options_comment_tv) ;

            mOpCommentServerTv = view.findViewById(R.id.adapter_my_receive_order_op_comment_server) ;
            mOpFinishServerTv = view.findViewById(R.id.adapter_my_receive_order_op_finish_server) ;
            mOpPayServerTv = view.findViewById(R.id.adapter_my_receive_order_op_pay_server) ;
            mOpCancelServerTv = view.findViewById(R.id.adapter_my_receive_order_op_cancel_server) ;
            mOpEditServerTv = view.findViewById(R.id.adapter_my_receive_order_op_edit_server) ;

            mOpDeleteTv = view.findViewById(R.id.adapter_my_receive_order_options_delete_tv) ;

            mOpFinishNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_finish_need_tv) ;
            mOpCancelNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_cancel_need_tv) ;
            mOpPayNeedTv = itemView.findViewById(R.id.adapter_my_receive_order_options_pay_need_tv) ;

            mGoodCountTv = view.findViewById(R.id.adapter_my_receive_order_good_count_tv) ;
            mGoodAmountTv = view.findViewById(R.id.adapter_my_receive_order_good_amount_tv) ;
            mRv = view.findViewById(R.id.adapter_my_receive_order_good_rv) ;
            mRv.setFocusable(false);
            mRv.setNestedScrollingEnabled(false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_ITEM,getAdapterPosition()) ;
                    }
                }
            });
            mOpCommentServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_COMMENT_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpPayServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_SERVER,getAdapterPosition()) ;
                    }
                }
            });
            mOpEditServerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_EDIT_SERVER,getAdapterPosition()) ;
                    }
                }
            });

            mOpCommentGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_COMMENT_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpPayGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelGoodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_GOOD,getAdapterPosition()) ;
                    }
                }
            });
            mOpDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_DELETE,getAdapterPosition()) ;
                    }
                }
            });
            mOpPayNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_PAY_NEED ,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL_NEED ,getAdapterPosition()) ;
                    }
                }
            });
            mOpFinishNeedTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH_NEED ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
