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
 * Created on 2018/4/24.
 */

public class AdapterMyGoodOrderCommentGood extends RecyclerView.Adapter<AdapterMyGoodOrderCommentGood.GogHolder> {
    private Context context ;
    private List<GoodOrderGood> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyGoodOrderCommentGood(Context context, List<GoodOrderGood> mDataList,RvItemViewClickListener clickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = clickListener;
    }

    @Override
    public GogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GogHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_good_order_comment_good_item ,parent ,false)) ;
    }

    @Override
    public void onBindViewHolder(GogHolder holder, int position) {
        final int realPosition = position ;

        GoodOrderGood good = mDataList.get(position) ;

        if(good != null){
            String goodName = good.getProductTitle() ;
            String goodPrice = good.getSalePrice() ;
            String goodGuige = good.getSpecText() ;
            String goodImg = good.getImgUrl() ;

            holder.mGoodNameTv.setText(goodName);
            holder.mGoodGuigeTv.setText(goodGuige);
            holder.mGoodPriceTv.setText("¥" + goodPrice);
            GlideUtils.loader(goodImg ,holder.mGoodIv) ;

            boolean isCommented = good.isGoodComment() ;
            if(isCommented){
                holder.mGoodCommentTv.setText("已评价");
                holder.mGoodCommentTv.setTextColor(context.getResources().getColor(R.color.color_grey_text)) ;
                holder.mGoodCommentTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_grey_ee_bg));
            }else{
                holder.mGoodCommentTv.setText("去评价");
                holder.mGoodCommentTv.setTextColor(context.getResources().getColor(R.color.color_red)) ;
                holder.mGoodCommentTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_border_red_white_1dp));
            }

            holder.mGoodCommentTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
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
        private TextView mGoodPriceTv ;
        private TextView mGoodGuigeTv ;
        private TextView mGoodCommentTv;

        public GogHolder(View itemView) {
            super(itemView);

            mGoodIv = itemView.findViewById(R.id.adapter_good_order_comment_good_iv) ;
            mGoodNameTv = itemView.findViewById(R.id.adapter_good_order_comment_good_name_tv) ;
            mGoodPriceTv = itemView.findViewById(R.id.adapter_good_order_comment_good_price_tv) ;
            mGoodGuigeTv = itemView.findViewById(R.id.adapter_good_order_comment_good_guige_tv) ;
            mGoodCommentTv = itemView.findViewById(R.id.adapter_good_order_comment_good_comment_tv) ;

        }
    }
}
