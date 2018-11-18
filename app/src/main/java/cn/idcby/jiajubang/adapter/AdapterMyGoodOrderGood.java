package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodDetailActivity;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/24.
 */

public class AdapterMyGoodOrderGood extends RecyclerView.Adapter<AdapterMyGoodOrderGood.GogHolder> {
    private Context context ;
    private List<GoodOrderGood> mDataList ;
    private boolean mIsUnuse = false ;

    public AdapterMyGoodOrderGood(Context context,boolean mIsUnuse , List<GoodOrderGood> mDataList) {
        this.context = context;
        this.mIsUnuse = mIsUnuse;
        this.mDataList = mDataList;
    }

    @Override
    public GogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GogHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_good_order_good_item ,parent ,false)) ;
    }

    @Override
    public void onBindViewHolder(GogHolder holder, int position) {
        final GoodOrderGood good = mDataList.get(position) ;

        if(good != null){
            String goodName = good.getProductTitle() ;
            String goodPrice = good.getSalePrice() ;
            String goodGuige = good.getSpecText() ;
            String goodCount = good.getQuantity() ;
            String goodImg = good.getImgUrl() ;
            String goodState = good.getAfterSaleStatusText() ;

            holder.mGoodNameTv.setText(goodName);
            holder.mGoodStateTv.setText(goodState);
            holder.mGoodGuigeTv.setText("规格：" + goodGuige);
            holder.mGoodPriceTv.setText("¥" + goodPrice);
            holder.mGoodCountTv.setText("x" + goodCount);
            GlideUtils.loader(goodImg ,holder.mGoodIv) ;

            holder.mGoodStateTv.setVisibility(good.canAfterSale() ? View.INVISIBLE : View.VISIBLE);

            if("".equals(goodGuige)){
                holder.mGoodGuigeTv.setVisibility(View.INVISIBLE);
            }else{
                holder.mGoodGuigeTv.setVisibility(View.VISIBLE);
            }

            final String productId = good.getProductID() ;
            holder.mMainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent();
                    if (!mIsUnuse) {
                        toDtIt.setClass(context, GoodDetailActivity.class);
                        toDtIt.putExtra(SkipUtils.INTENT_GOOD_ID, productId);
                    } else {
                        toDtIt.setClass(context, UnuseGoodDetailsActivity.class);
                        toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID, productId);
                    }
                    context.startActivity(toDtIt);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return  null == mDataList ? 0 : mDataList.size() ;
    }

    static class GogHolder extends RecyclerView.ViewHolder{
        private ImageView mGoodIv ;
        private TextView mGoodNameTv ;
        private TextView mGoodStateTv ;
        private TextView mGoodPriceTv ;
        private TextView mGoodGuigeTv ;
        private TextView mGoodCountTv ;
        private View mMainView ;

        public GogHolder(View itemView) {
            super(itemView);

            mMainView = itemView.findViewById(R.id.adapter_my_good_order_good_main_lay) ;
            mGoodIv = itemView.findViewById(R.id.adapter_my_good_order_good_iv) ;
            mGoodNameTv = itemView.findViewById(R.id.adapter_my_good_order_good_name_tv) ;
            mGoodStateTv = itemView.findViewById(R.id.adapter_my_good_order_good_state_tv) ;
            mGoodPriceTv = itemView.findViewById(R.id.adapter_my_good_order_good_price_tv) ;
            mGoodGuigeTv = itemView.findViewById(R.id.adapter_my_good_order_good_guige_tv) ;
            mGoodCountTv = itemView.findViewById(R.id.adapter_my_good_order_good_count_tv) ;

        }
    }
}
