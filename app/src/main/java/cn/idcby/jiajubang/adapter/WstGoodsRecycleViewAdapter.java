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
 *
 */

public class WstGoodsRecycleViewAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<GoodList> mDataList;
    private RvItemViewClickListener mClickListener ;
    private LayoutInflater inflater;
    private int imgWidHei ;


    //构造方法中添加自定义监听接口
    public WstGoodsRecycleViewAdapter(Context mContext, List<GoodList> refreshbean
            , int colum ,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = refreshbean;
        this.mClickListener = mClickListener;
        imgWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext ,1) * (colum-1)) / colum ;

        inflater = LayoutInflater.from(mContext);
    }

    //构造方法中添加自定义监听接口
    public WstGoodsRecycleViewAdapter(Context mContext, List<GoodList> refreshbean
            ,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = refreshbean;
        this.mClickListener = mClickListener;

        imgWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext ,4)) / 2 ;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_goods_classfy, parent,false);
        return new MyViewHolder(view,imgWidHei);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int realPosition = position ;

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        GoodList refreshBeanitem = mDataList.get(position);

        myViewHolder.goodsprice.setText(refreshBeanitem.getSalePrice());
        myViewHolder.goodstitle.setText(refreshBeanitem.getTitle());
        myViewHolder.goodssells.setText(refreshBeanitem.getSaleNumber() + "人付款");
        GlideUtils.loaderGoodImage(refreshBeanitem.getImgUrl(),myViewHolder.goodsimg);

        myViewHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null){
                    mClickListener.onItemClickListener(0 ,realPosition) ;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View mMainLay ;
        private ImageView goodsimg;
        private TextView goodstitle;
        private TextView goodsprice;
        private TextView goodssells;

        public MyViewHolder(View itemView, int imgWidHei) {
            super(itemView);
            goodsimg = (ImageView) itemView.findViewById(R.id.goodsimg_items);
            goodstitle=(TextView)itemView.findViewById(R.id.goodstitle_items);
            goodsprice=(TextView)itemView.findViewById(R.id.goodprice_items);
            goodssells=(TextView)itemView.findViewById(R.id.goodssells_items);

            goodsimg.getLayoutParams().width = imgWidHei ;
            goodsimg.getLayoutParams().height = imgWidHei ;

            mMainLay = itemView ;
        }
    }
}
