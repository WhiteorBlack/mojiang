package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NomalGoodInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodDetailActivity;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/24.
 */

public class AdapterGoodOrderImage extends RecyclerView.Adapter<AdapterGoodOrderImage.GoiHolder> {
    private Context context ;
    private List<NomalGoodInfo> mGoodList;
    private boolean mIsClickable = false ;
    private boolean mIsUnuse = false ;

    public void setItemClickable() {
        this.mIsClickable = true;
    }

    public AdapterGoodOrderImage(Context context,boolean mIsUnuse , List<NomalGoodInfo> goodList) {
        this.context = context;
        this.mIsUnuse = mIsUnuse;
        this.mGoodList = goodList ;
    }

    @Override
    public GoiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoiHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_good_order_confirm_good_item,parent ,false));
    }

    @Override
    public void onBindViewHolder(GoiHolder holder, int position) {

        NomalGoodInfo goodInfo = mGoodList.get(position) ;
        if (goodInfo != null){
            GlideUtils.loader(goodInfo.getImgUrl(),holder.imageView);

            final String productId = goodInfo.getProductID() ;
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mIsClickable) {
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
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mGoodList ? 0 : mGoodList.size() ;
    }

    static class GoiHolder extends RecyclerView.ViewHolder{
        private ImageView imageView ;

        public GoiHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.adapter_good_confirm_good_item_iv) ;
        }
    }
}
