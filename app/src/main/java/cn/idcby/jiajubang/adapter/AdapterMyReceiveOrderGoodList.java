package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemMoreViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/6/8.
 */

public class AdapterMyReceiveOrderGoodList extends RecyclerView.Adapter<AdapterMyReceiveOrderGoodList.RogHolder> {
    private Context context ;
    private List<GoodOrderGood> mDataList ;
    private int mCurPosition ;
    private boolean mAfterSale ;
    private RvItemMoreViewClickListener mClickListener ;

    public AdapterMyReceiveOrderGoodList(Context context,boolean mAfterSale, List<GoodOrderGood> mDataList,int mCurPosition
            ,RvItemMoreViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mAfterSale = mAfterSale;
        this.mCurPosition = mCurPosition;
        this.mClickListener = mClickListener;
    }

    @Override
    public RogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RogHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_receive_order_good_item ,parent ,false),mCurPosition,mClickListener) ;
    }

    @Override
    public void onBindViewHolder(RogHolder holder, int position) {
        final GoodOrderGood good = mDataList.get(position) ;

        if(good != null){
            String goodName = good.getProductTitle() ;
            String goodPrice = good.getSalePrice() ;
            String goodGuige = good.getSpecText() ;
            String goodCount = good.getQuantity() ;
            String goodImg = good.getImgUrl() ;

            holder.mGoodNameTv.setText(goodName);
            holder.mGoodGuigeTv.setText(goodGuige);
            holder.mGoodPriceTv.setText("¥" + goodPrice);
            holder.mGoodCountTv.setText("x" + goodCount);
            GlideUtils.loader(goodImg ,holder.mGoodIv) ;

            //售后状态，只要是申请了，就一直显示
            holder.mGoodAfterSaleStateTv.setText(good.getAfterSaleStatusText()) ;
            holder.mGoodAfterSaleStateTv.setVisibility(good.canAfterSale() ? View.GONE : View.VISIBLE);
            holder.mGoodAfterSaleTv.setVisibility(mAfterSale && good.canAfterSale() ? View.VISIBLE : View.INVISIBLE);

            holder.mGoodGuigeTv.setVisibility(View.INVISIBLE);

            if("".equals(goodGuige)){
                holder.mGoodGuigeTv.setVisibility(View.INVISIBLE);
            }else{
                holder.mGoodGuigeTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class RogHolder extends RecyclerView.ViewHolder{
        private ImageView mGoodIv ;
        private TextView mGoodNameTv;
        private TextView mGoodCountTv ;
        private TextView mGoodGuigeTv ;
        private TextView mGoodPriceTv ;
        private TextView mGoodAfterSaleTv ;
        private TextView mGoodAfterSaleStateTv;

        public RogHolder(View itemView,final int mCurPosition , final RvItemMoreViewClickListener mClickListener) {
            super(itemView);

            mGoodIv = itemView.findViewById(R.id.adapter_my_receive_good_iv) ;
            mGoodNameTv = itemView.findViewById(R.id.adapter_my_receive_good_name_tv) ;
            mGoodPriceTv = itemView.findViewById(R.id.adapter_my_receive_good_price_tv) ;
            mGoodGuigeTv = itemView.findViewById(R.id.adapter_my_receive_good_gg_tv) ;
            mGoodCountTv = itemView.findViewById(R.id.adapter_my_receive_good_count_tv) ;
            mGoodAfterSaleTv = itemView.findViewById(R.id.adapter_my_receive_good_after_sale_tv) ;
            mGoodAfterSaleStateTv = itemView.findViewById(R.id.adapter_my_receive_good_after_sale_finish_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(AdapterMyReceiveOrder.TYPE_OP_ITEM ,mCurPosition) ;
                    }
                }
            });
            mGoodAfterSaleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(AdapterMyReceiveOrder.TYPE_OP_AFTER_SALE_GOOD ,mCurPosition ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
