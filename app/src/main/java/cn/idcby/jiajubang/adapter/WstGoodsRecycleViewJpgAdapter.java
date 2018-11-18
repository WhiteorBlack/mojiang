package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created by Administrator on 2018-04-21.
 */

public class WstGoodsRecycleViewJpgAdapter extends RecyclerView.Adapter<WstGoodsRecycleViewJpgAdapter.GilViewHolder>{
    private LayoutInflater inflater;
    private List<GoodList> mDataList;
    private RvItemViewClickListener clickListener ;
    private int imgWidHei ;


    //构造方法中添加自定义监听接口
    public WstGoodsRecycleViewJpgAdapter(Context mContext, List<GoodList> refreshbean, RvItemViewClickListener clickListener) {
        this.mDataList = refreshbean;
        this.clickListener = clickListener;
        this.inflater = LayoutInflater.from(mContext);
        this.imgWidHei = (ResourceUtils.getScreenWidth(mContext) - ResourceUtils.dip2px(mContext
                ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height))) / 2 ;
    }

    @Override
    public WstGoodsRecycleViewJpgAdapter.GilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.adapter_good_list_rv_two, parent,false);
            return new GilViewHolder(view,imgWidHei ,clickListener);
    }

    @Override
    public void onBindViewHolder(WstGoodsRecycleViewJpgAdapter.GilViewHolder holder, int position) {
        GoodList info = mDataList.get(position) ;
        if(info != null){
            String title = info.getTitle() ;
            String price = info.getSalePrice() ;
            String count = info.getSaleNumber() ;
            String imgUrl = info.getImgUrl() ;

            holder.goodstitle.setText(title);
            holder.goodsprice.setText(price);
            holder.goodssells.setText(count + "人付款");
            GlideUtils.loader(imgUrl ,holder.goodsimg) ;
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

        public GilViewHolder(View itemView,int imgWidHei , final RvItemViewClickListener clickListener ) {
            super(itemView);
            goodsimg = (ImageView) itemView.findViewById(R.id.goodsimg_items);
            goodstitle=(TextView)itemView.findViewById(R.id.goodstitle_items);
            goodsprice=(TextView)itemView.findViewById(R.id.goodprice_items);
            goodssells=(TextView)itemView.findViewById(R.id.goodssells_items);
            goodsimg.getLayoutParams().width = imgWidHei ;
            goodsimg.getLayoutParams().height = imgWidHei ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
        }


    }
}
