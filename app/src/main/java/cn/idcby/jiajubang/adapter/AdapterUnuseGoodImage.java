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
 * D:多图布局样式--闲置
 * Created on 2018/3/23.
 */

public class AdapterUnuseGoodImage extends
        RecyclerView.Adapter<AdapterUnuseGoodImage.UnuseGoodHolderImage> {
    private Activity context ;
    private List<ImageThumb> mImageList ;
    private int mMoreImageWidHei ;//显示3个多点儿
    private LayoutInflater inflater ;

    public AdapterUnuseGoodImage(Activity context , List<ImageThumb> mImageList) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mMoreImageWidHei = (int) ((ResourceUtils.getScreenWidth(context)
                        - ResourceUtils.dip2px(context , 15) - ResourceUtils.dip2px(context , 5) * 3) / 3.3F);
    }

    @Override
    public UnuseGoodHolderImage onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UnuseGoodHolderImage(inflater.inflate(
                R.layout.adapter_my_needs_offer_image, parent , false)
                , mMoreImageWidHei) ;
    }

    @Override
    public void onBindViewHolder(UnuseGoodHolderImage holder
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

    static class UnuseGoodHolderImage extends RecyclerView.ViewHolder{
        private ImageView mImageIv;

        public UnuseGoodHolderImage(View itemView , int imageWidHei) {
            super(itemView);

            mImageIv = itemView.findViewById(R.id.adapter_my_needs_offer_image_iv);

            mImageIv.getLayoutParams().width = imageWidHei ;
            mImageIv.getLayoutParams().height = imageWidHei ;
        }
    }
}
