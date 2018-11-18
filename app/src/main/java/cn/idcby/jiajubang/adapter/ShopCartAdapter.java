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

import cn.idcby.jiajubang.Bean.ShopCartBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;

/**
 * Created on 2018-04-24.
 */

public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.CtViewHolder> {
    private Context context ;
    private List<ShopCartBean> mDataList ;
    private RvMoreItemClickListener mClickListener ;

    public static final int TYPE_STORE_ALL = 0 ;
    public static final int TYPE_STORE_LAY = 1 ;
    public static final int TYPE_GOOD_ALL = 2 ;
    public static final int TYPE_GOOD_LAY = 3 ;
    public static final int TYPE_GOOD_ADD = 4 ;
    public static final int TYPE_GOOD_REDUCE = 5 ;
    public static final int TYPE_GOOD_COUNT = 6 ;

    public ShopCartAdapter(Context context, List<ShopCartBean> mDataList, RvMoreItemClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public CtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CtViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_cart_item ,parent ,false) ,mClickListener);
    }

    @Override
    public void onBindViewHolder(CtViewHolder holder, int position) {
        final int parentPosition = position ;
        ShopCartBean cartBean = mDataList.get(position) ;
        if(cartBean != null){
            String storeName = cartBean.getStoreName() ;
            boolean isSelected = cartBean.isSelected() ;

            holder.mNameTv.setText(storeName);
            holder.mAllCheckIv.setImageDrawable(context.getResources().getDrawable(isSelected
                    ? R.mipmap.ic_check_checked_cart : R.mipmap.ic_check_nomal_cart));

            holder.mGoodRv.setLayoutManager(new LinearLayoutManager(context));
            ShopCartGoodAdapter goodAdapter = new ShopCartGoodAdapter(context
                    ,cartBean.getCartGoodList() ,parentPosition ,mClickListener) ;
            holder.mGoodRv.setAdapter(goodAdapter) ;
            holder.mGoodRv.setNestedScrollingEnabled(false);
            holder.mGoodRv.setFocusable(false);
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class CtViewHolder extends RecyclerView.ViewHolder{
        private ImageView mAllCheckIv ;
        private TextView mNameTv ;
        private RecyclerView mGoodRv ;

        public CtViewHolder(View itemView, final RvMoreItemClickListener mClickListener ) {
            super(itemView);

            this.mAllCheckIv = itemView.findViewById(R.id.adapter_cart_all_check_iv) ;
            this.mNameTv = itemView.findViewById(R.id.adapter_cart_store_name_tv) ;
            this.mGoodRv = itemView.findViewById(R.id.adapter_cart_good_rv) ;
            View storeLay =itemView.findViewById(R.id.adapter_cart_store_lay) ;

            mAllCheckIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_STORE_ALL ,getAdapterPosition()) ;
                    }
                }
            });
            storeLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_STORE_LAY ,getAdapterPosition()) ;
                    }
                }
            });

        }
    }

}