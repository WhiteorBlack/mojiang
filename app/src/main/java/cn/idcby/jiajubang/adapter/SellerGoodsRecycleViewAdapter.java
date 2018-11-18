package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.SellerGoodsBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created by Administrator on 2018-04-21.
 */

//public class SellerGoodsRecycleViewAdapter extends RecyclerView.Adapter<SellerGoodsRecycleViewAdapter.Listhoder> {
//    MyItemClickListener mItemClickListener;
//
//    private Context mContext;
//    private List<SellerGoodsBean> mSellerGoodsBean;
//
//
//    public void setOnItemClickListener(MyItemClickListener listener) {
//        this.mItemClickListener = listener;
//    }
//
//    public interface MyItemClickListener {
//        public void onItemClick(View view, int postion);
//    }
//
//    //构造方法中添加自定义监听接口
//    public SellerGoodsRecycleViewAdapter(Context mContext, List<SellerGoodsBean> mSellerGoodsBean) {
//        this.mContext = mContext;
//        this.mSellerGoodsBean = mSellerGoodsBean;
//    }
//
//    @Override
//    public SellerGoodsRecycleViewAdapter.Listhoder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods_classfy, null);
//        return new Listhoder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(final SellerGoodsRecycleViewAdapter.Listhoder holder, int position) {
//        holder.setData(position);
//        holder.textview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mSellerGoodsBean.size();
//    }
//
//    class Listhoder extends RecyclerView.ViewHolder {
//        TextView textview;
//
//        public Listhoder(View itemView) {
//            super(itemView);
//            textview = (TextView) itemView.findViewById(R.id.goodstitle_items);
//        }
//
//        public void setData(int position) {
//            textview.setText(mSellerGoodsBean.get(position).getProductTitle());
//        }
//
//    }
//}


    public class SellerGoodsRecycleViewAdapter   extends RecyclerView.Adapter implements View.OnClickListener {

    private Context mContext;
    private List<SellerGoodsBean> mSellerGoodsBean;


    //构造方法中添加自定义监听接口
    public SellerGoodsRecycleViewAdapter(Context mContext, List<SellerGoodsBean> mSellerGoodsBean) {
        this.mContext = mContext;
        this.mSellerGoodsBean = mSellerGoodsBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//         解决条目显示不全
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_classfy, parent,false);
            //这里 我们可以拿到点击的item的view 对象，所以在这里给view设置点击监听，
            view.setOnClickListener(this);
            MyViewHolder mHolder = new MyViewHolder(view);
            return mHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        SellerGoodsBean mySellergoods = mSellerGoodsBean.get(position);

        myViewHolder.goodsprice.setText(mySellergoods.getShowPrice());
        myViewHolder.goodstitle.setText(mySellergoods.getProductTitle());
        myViewHolder.goodssells.setText(mySellergoods.getPSaleNumber() + "人付款");
        GlideUtils.loader(mContext,mySellergoods.getPImgUrl(),myViewHolder.goodsimg);

        myViewHolder.itemView.setTag(mySellergoods.getProductID());//给view设置tag以作为参数传递到监听回调方法中
    }

    @Override
    public int getItemCount() {
        return mSellerGoodsBean == null ? 0 : mSellerGoodsBean.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((String) v.getTag());
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView goodsimg;
        private TextView goodstitle;
        private TextView goodsprice;
        private TextView goodssells;

        public MyViewHolder(View itemView) {
            super(itemView);
            goodsimg = (ImageView) itemView.findViewById(R.id.goodsimg_items);
            goodstitle=(TextView)itemView.findViewById(R.id.goodstitle_items);
            goodsprice=(TextView)itemView.findViewById(R.id.goodprice_items);
            goodssells=(TextView)itemView.findViewById(R.id.goodssells_items);
        }


    }


    public interface OnItemClickListener{
        void onItemClick(String tags);
    }
    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
