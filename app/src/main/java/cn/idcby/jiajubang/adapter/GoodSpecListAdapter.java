package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodDetails;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.MyCornerTextView;

/**
 * Created by Administrator on 2018-04-21.
 *
 * 专场商品列表
 */

public class GoodSpecListAdapter extends RecyclerView.Adapter<GoodSpecListAdapter.GilViewHolder>{
    private Context mContext ;
    private LayoutInflater inflater;
    private List<GoodList> mDataList;
    private RvItemViewClickListener clickListener ;
    private int flTvPadding ;
    private int borderWidth ;


    //构造方法中添加自定义监听接口
    public GoodSpecListAdapter(Context mContext, List<GoodList> refreshbean, RvItemViewClickListener clickListener) {
        this.mContext = mContext;
        this.mDataList = refreshbean;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(mContext);
        this.flTvPadding = ResourceUtils.dip2px(mContext , 1) ;
        this.borderWidth = ResourceUtils.dip2px(mContext , 1) ;
    }

    @Override
    public GoodSpecListAdapter.GilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.adapter_good_spec_list, parent,false);
            return new GilViewHolder(view,clickListener);
    }

    @Override
    public void onBindViewHolder(GoodSpecListAdapter.GilViewHolder holder, int position) {
        GoodList info = mDataList.get(position) ;
        if(info != null){
            String title = info.getTitle() ;
            String price = info.getSalePrice() ;
            String count = info.getSaleNumber() ;
            String imgUrl = info.getImgUrl() ;
            String storeName = info.getStoreName() ;
            String storeLocation = info.getStorePositon() ;

            holder.goodstitle.setText(title);
            holder.goodsprice.setText(price);
            holder.goodStoreNameTv.setText(storeName);
            holder.goodStoreLocationTv.setText(storeLocation);
            holder.goodssells.setText(count + "人付款");
            GlideUtils.loaderRound(imgUrl ,holder.goodsimg,5) ;

            holder.goodServerLay.removeAllViews();

            List<GoodDetails.GoodService> serverList = info.getServerList() ;
            if(serverList.size() > 0){
                int typeSize = serverList.size() ;
//                if(typeSize > 3){
//                    typeSize = 3 ;
//                }

                for(int x = 0 ; x < typeSize ; x ++){
                    GoodDetails.GoodService  wordType = serverList.get(x) ;

                    if(wordType != null){
                        int color = Color.parseColor(wordType.getColorValue()) ;

                        MyCornerTextView tv = new MyCornerTextView(mContext) ;
                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setPadding(flTvPadding * 2 ,flTvPadding / 2 ,flTvPadding * 2,flTvPadding);
                        tv.setBorderWidth(borderWidth).setfilColor(color).setCornerSize(flTvPadding);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,9) ;
                        tv.setTextColor(color) ;
                        tv.setText(wordType.getServiceTitle());
                        tv.setBackgroundColor(mContext.getResources().getColor(R.color.color_trans));

                        holder.goodServerLay.addView(tv) ;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    class GilViewHolder extends RecyclerView.ViewHolder {
        public ImageView goodsimg;
        private TextView goodstitle;
        private TextView goodsprice;
        private TextView goodssells;
        private TextView goodStoreNameTv;
        private TextView goodStoreLocationTv;
        private FlowLayout goodServerLay;

        public GilViewHolder(View itemView, final RvItemViewClickListener clickListener ) {
            super(itemView);
            goodsimg = itemView.findViewById(R.id.goodsimg_items);
            goodstitle= itemView.findViewById(R.id.goodstitle_items);
            goodsprice= itemView.findViewById(R.id.goodprice_items);
            goodssells= itemView.findViewById(R.id.goodssells_items);
            goodStoreNameTv= itemView.findViewById(R.id.good_store_name);
            goodStoreLocationTv = itemView.findViewById(R.id.adapter_good_spec_store_location_tv);
            goodServerLay= itemView.findViewById(R.id.item_good_spec_server_lay);
            View storeLay =itemView.findViewById(R.id.item_good_bottom_lay) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
            storeLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(1 ,getAdapterPosition()) ;
                    }
                }
            });
        }


    }
}
