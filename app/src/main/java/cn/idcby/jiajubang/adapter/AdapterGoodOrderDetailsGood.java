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
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 订单详细--商品列表
 * Created on 2018/6/11.
 */

public class AdapterGoodOrderDetailsGood extends RecyclerView.Adapter<AdapterGoodOrderDetailsGood.GoiHolder> {
    private Context context ;
    private boolean mCanAfterSale;
    private List<GoodOrderGood> mGoodList;
    private RvItemViewClickListener mClickListener ;

    public AdapterGoodOrderDetailsGood(Context context,boolean canAfterSale , List<GoodOrderGood> goodList
            ,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mCanAfterSale = canAfterSale;
        this.mGoodList = goodList ;
        this.mClickListener = mClickListener ;
    }

    @Override
    public GoiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoiHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_good_order_details_good_item,parent ,false),mClickListener);
    }

    @Override
    public void onBindViewHolder(GoiHolder holder, int position) {
        GoodOrderGood goodInfo = mGoodList.get(position) ;
        if (goodInfo != null){
            GlideUtils.loader(goodInfo.getImgUrl(),holder.imageView);

            String goodName = goodInfo.getProductTitle() ;
            String goodPrice = goodInfo.getSalePrice() ;
            String goodCount = goodInfo.getQuantity() ;
            String goodGg = goodInfo.getSpecText() ;
            String afterState = goodInfo.getAfterSaleStatusText() ;
            holder.mGoodNameTv.setText(goodName);
            holder.mGoodPriceTv.setText("¥" + goodPrice);
            holder.mGoodCountTv.setText("x" + goodCount);
            holder.mGoodGgTv.setText(goodGg) ;
            holder.mGoodAfterSaleStateTv.setText(afterState);

            holder.mGoodAfterSaleTv.setVisibility(mCanAfterSale && goodInfo.canAfterSale()
                    ? View.VISIBLE : View.INVISIBLE);
            holder.mGoodAfterSaleStateTv.setVisibility(goodInfo.canAfterSale() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == mGoodList ? 0 : mGoodList.size() ;
    }

    static class GoiHolder extends RecyclerView.ViewHolder{
        private ImageView imageView ;
        private TextView mGoodNameTv ;
        private TextView mGoodAfterSaleTv ;
        private TextView mGoodAfterSaleStateTv ;
        private TextView mGoodGgTv ;
        private TextView mGoodCountTv ;
        private TextView mGoodPriceTv ;

        public GoiHolder(View itemView , final RvItemViewClickListener mClickListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.adapter_good_order_details_good_iv) ;
            mGoodNameTv = itemView.findViewById(R.id.adapter_good_order_details_good_name_tv) ;
            mGoodAfterSaleTv = itemView.findViewById(R.id.adapter_good_order_details_good_after_sale_tv) ;
            mGoodAfterSaleStateTv = itemView.findViewById(R.id.adapter_good_order_details_good_after_sale_state_tv) ;
            mGoodGgTv = itemView.findViewById(R.id.adapter_good_order_details_good_guige_tv) ;
            mGoodCountTv = itemView.findViewById(R.id.adapter_good_order_details_good_count_tv) ;
            mGoodPriceTv = itemView.findViewById(R.id.adapter_good_order_details_good_price_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
            mGoodAfterSaleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
