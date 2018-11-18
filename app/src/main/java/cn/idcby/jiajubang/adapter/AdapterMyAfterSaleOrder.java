package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.MyAfterSaleOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.FragmentMyOrderAfterSale;
import cn.idcby.jiajubang.interf.RvItemMoreViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 我的售后订单列表
 * Created on 2018/5/7.
 */

public class AdapterMyAfterSaleOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private List<MyAfterSaleOrderList> mDataList ;
    private RvItemMoreViewClickListener mClickListener ;
    private boolean mIsReceive = false ; //购买的商品的售后

    public static final int TYPE_OP_ITEM = 0 ;
    public static final int TYPE_OP_EDIT = 1 ;
    public static final int TYPE_OP_CANCEL = 2 ;
    public static final int TYPE_OP_EXPRESS = 3 ;

    public static final int TYPE_OP_AGREE = 4 ;
    public static final int TYPE_OP_DISAGREE = 5 ;
    public static final int TYPE_OP_FINISH = 6 ;

    public static final int TYPE_OP_STORE = 7 ;


    public AdapterMyAfterSaleOrder(Context context,int orderFrom, List<MyAfterSaleOrderList> mDataList, RvItemMoreViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.mIsReceive = FragmentMyOrderAfterSale.ORDER_AFTER_SALE_FROM_RECEIVE == orderFrom ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AfterSaleListHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_after_sale_order_list ,parent ,false) ,mClickListener) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AfterSaleListHolder){
            AfterSaleListHolder mHolder = (AfterSaleListHolder) holder;

            final MyAfterSaleOrderList info = mDataList.get(position) ;
            String orderTitle = info.getName() ;
            String imgUrl = info.getImageUrl() ;
            String desc = info.getDescription() ;
            String statusName = info.getStatusName() ;
            String serverTypeName = info.getServiceTypeName() ;

            mHolder.mTitleTv.setText(orderTitle);
            mHolder.mStatusTv.setText(statusName);
            mHolder.mServiceTypeTv.setText(serverTypeName);
            mHolder.mDescTv.setText(desc);
            if(info.isGood() || info.isUnuse()){
                GlideUtils.loader(imgUrl ,mHolder.mUserIv) ;
            }else{
                GlideUtils.loaderUser(imgUrl ,mHolder.mUserIv) ;
            }

            mHolder.mOpAgreeTv.setVisibility(View.GONE);
            mHolder.mOpDisagreeTv.setVisibility(View.GONE);
            mHolder.mOpFinishTv.setVisibility(View.GONE);

            mHolder.mOpExpressTv.setVisibility(View.GONE);
            mHolder.mOpCancelTv.setVisibility(View.GONE);
            mHolder.mOpEditTv.setVisibility(View.GONE);

            if(mIsReceive){//购买的 编辑、取消、填写物流信息
                if(info.canEdit()){
                    mHolder.mOpEditTv.setVisibility(View.VISIBLE);
                }
                if(info.canCancel()){
                    mHolder.mOpCancelTv.setVisibility(View.VISIBLE);
                }
                if(info.canExpress()){
                    mHolder.mOpExpressTv.setVisibility(View.VISIBLE);
                }
            }else{//提供的 同意、不同意、确认收货
                if(info.canAgreeOrDis()){
                    mHolder.mOpAgreeTv.setVisibility(View.VISIBLE);
                    mHolder.mOpDisagreeTv.setVisibility(View.VISIBLE);
                }
                if(info.canFinish()){
                    mHolder.mOpFinishTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class AfterSaleListHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv;
        private ImageView mUserIv ;
        private TextView mDescTv ;
        private TextView mStatusTv ;
        private TextView mServiceTypeTv ;

        private View mOpDetailsTv;

        //卖家
        private View mOpFinishTv;
        private View mOpAgreeTv;
        private View mOpDisagreeTv;

        //买家
        private View mOpEditTv;
        private View mOpCancelTv;
        private View mOpExpressTv;


        public AfterSaleListHolder(View view , final RvItemMoreViewClickListener mClickListener) {
            super(view);

            mTitleTv = view.findViewById(R.id.adapter_my_after_sale_name_tv) ;
            mStatusTv = view.findViewById(R.id.adapter_my_after_sale_status_tv) ;
            mServiceTypeTv = view.findViewById(R.id.adapter_my_after_sale_service_type_tv) ;

            mOpDetailsTv = view.findViewById(R.id.adapter_my_after_sale_op_details_tv) ;

            mOpFinishTv = view.findViewById(R.id.adapter_my_after_sale_op_finish_tv) ;
            mOpAgreeTv = view.findViewById(R.id.adapter_my_after_sale_op_agree_tv) ;
            mOpDisagreeTv = view.findViewById(R.id.adapter_my_after_sale_op_disagree_tv) ;

            mOpEditTv = view.findViewById(R.id.adapter_my_after_sale_op_edit_tv) ;
            mOpCancelTv = view.findViewById(R.id.adapter_my_after_sale_op_cancel_tv) ;
            mOpExpressTv = view.findViewById(R.id.adapter_my_after_sale_op_express_tv) ;

            mUserIv = view.findViewById(R.id.adapter_my_after_sale_iv) ;
            mDescTv = view.findViewById(R.id.adapter_my_after_sale_desc_tv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_ITEM,getAdapterPosition()) ;
                    }
                }
            });
            mOpDetailsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_ITEM,getAdapterPosition()) ;
                    }
                }
            });
            mOpEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_EDIT,getAdapterPosition()) ;
                    }
                }
            });
            mOpCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_CANCEL,getAdapterPosition()) ;
                    }
                }
            });
            mOpExpressTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_EXPRESS,getAdapterPosition()) ;
                    }
                }
            });

            mOpFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_FINISH,getAdapterPosition()) ;
                    }
                }
            });
            mOpDisagreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_DISAGREE,getAdapterPosition()) ;
                    }
                }
            });
            mOpAgreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_AGREE,getAdapterPosition()) ;
                    }
                }
            });
            mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_STORE,getAdapterPosition()) ;
                    }
                }
            });
        }
    }

}
