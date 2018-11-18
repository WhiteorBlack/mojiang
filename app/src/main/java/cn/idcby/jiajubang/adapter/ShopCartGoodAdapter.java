package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.CartList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 购物车商品
 * Created on 2018-04-24.
 */

public class ShopCartGoodAdapter extends RecyclerView.Adapter<ShopCartGoodAdapter.CtgViewHolder> {
    private Context context ;
    private List<CartList> mDataList ;
    private RvMoreItemClickListener mClickListener ;
    private int mParentPosition ;

    public ShopCartGoodAdapter(Context context, List<CartList> mDataList
            ,int parentPosi , RvMoreItemClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mParentPosition = parentPosi;
        this.mClickListener = mClickListener;
    }

    @Override
    public CtgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CtgViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_cart_good_item ,parent ,false) ,mParentPosition,mClickListener);
    }

    @Override
    public void onBindViewHolder(CtgViewHolder holder, int position) {
        CartList cartBean = mDataList.get(position) ;
        if(cartBean != null){
            String imgUrl = cartBean.getImgUrl() ;
            String goodName = cartBean.getTitle() ;
            String guige = cartBean.getSpecText() ;
            String money = cartBean.getSalePrice() ;
            String count = cartBean.getQuantity() ;

            holder.mNameTv.setText(goodName);
            holder.mGuigeTv.setText(guige);
            holder.mMoneyTv.setText("¥" + money);
            holder.mCountEditTv.setText(count);
            GlideUtils.loader(imgUrl ,holder.mImageIv) ;

            boolean isSelected = cartBean.isSelected() ;
            holder.mAllCheckIv.setImageDrawable(context.getResources().getDrawable(isSelected
                    ? R.mipmap.ic_check_checked_cart : R.mipmap.ic_check_nomal_cart));
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class CtgViewHolder extends RecyclerView.ViewHolder{
        private ImageView mAllCheckIv ;
        private ImageView mImageIv ;
        private TextView mNameTv ;
        private TextView mGuigeTv ;
        private TextView mMoneyTv ;
        private TextView mCountEditTv ;
        private View mCountEditLay ;

        public CtgViewHolder(View itemView,final int parentPositin ,final RvMoreItemClickListener mClickListener ) {
            super(itemView);

            this.mAllCheckIv = itemView.findViewById(R.id.adapter_cart_good_all_iv) ;
            this.mImageIv = itemView.findViewById(R.id.adapter_cart_good_iv) ;
            this.mNameTv = itemView.findViewById(R.id.adapter_cart_good_name_tv) ;
            this.mGuigeTv = itemView.findViewById(R.id.adapter_cart_good_guige_tv) ;
            this.mMoneyTv = itemView.findViewById(R.id.adapter_cart_good_money_tv) ;
            this.mCountEditTv = itemView.findViewById(R.id.adapter_cart_good_count_edit_tv) ;
            this.mCountEditLay = itemView.findViewById(R.id.adapter_cart_good_count_lay) ;
            View reduceTv = itemView.findViewById(R.id.adapter_cart_good_count_reduce_tv) ;
            View addTv = itemView.findViewById(R.id.adapter_cart_good_count_add_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(ShopCartAdapter.TYPE_GOOD_LAY ,parentPositin ,getAdapterPosition()) ;
                    }
                }
            });
            mAllCheckIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(ShopCartAdapter.TYPE_GOOD_ALL ,parentPositin ,getAdapterPosition()) ;
                    }
                }
            });
            reduceTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(ShopCartAdapter.TYPE_GOOD_REDUCE ,parentPositin ,getAdapterPosition()) ;
                    }
                }
            });
            mCountEditLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(ShopCartAdapter.TYPE_GOOD_COUNT,parentPositin ,getAdapterPosition()) ;
                    }
                }
            });
            addTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(ShopCartAdapter.TYPE_GOOD_ADD ,parentPositin ,getAdapterPosition()) ;
                    }
                }
            });

        }
    }

}