package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * D:订单详情图片
 * Created on 2018/5/22.
 */

public class AdapterNomalImage extends
        RecyclerView.Adapter<AdapterNomalImage.OrderDtImgHolder> {
    private Activity context ;
    private List<ImageThumb> mImageList ;
    private int mMoreImageWidHei ;
    private LayoutInflater inflater ;

    public AdapterNomalImage(Activity context , List<ImageThumb> mImageList) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mMoreImageWidHei = (int) ((ResourceUtils.getScreenWidth(context)
                        - ResourceUtils.dip2px(context , 15) * 2 - ResourceUtils.dip2px(context , 10) * 2) / 3.5F);
    }

    public AdapterNomalImage(Activity context , List<ImageThumb> mImageList
            , int offset , float column) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mMoreImageWidHei = (int) ((ResourceUtils.getScreenWidth(context) - offset) / column);
    }
    public AdapterNomalImage(Activity context , List<ImageThumb> mImageList , int imageWidHei) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mMoreImageWidHei = imageWidHei;
    }

    @Override
    public OrderDtImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderDtImgHolder(inflater.inflate(
                R.layout.adapter_order_details_image, parent , false)
                , mMoreImageWidHei) ;
    }

    @Override
    public void onBindViewHolder(OrderDtImgHolder holder
            , int position) {
        GlideUtils.loaderNoType(MyApplication.getInstance()
                , mImageList.get(position).getThumbImgUrl() , holder.mImageIv) ;

        final int curPosi = position ;
        holder.mImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipUtils.toImageShowActivityWithThumb(context , mImageList ,curPosi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mImageList ? 0 : mImageList.size() ;
    }

    static class OrderDtImgHolder extends RecyclerView.ViewHolder{
        private ImageView mImageIv;

        public OrderDtImgHolder(View itemView , int imageWidHei) {
            super(itemView);

            mImageIv = itemView.findViewById(R.id.adapter_order_details_image_iv);

            mImageIv.getLayoutParams().width = imageWidHei ;
            mImageIv.getLayoutParams().height = imageWidHei ;
        }
    }
}
