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
 * D:多图布局样式--9宫格样式
 * Created on 2018/3/23.
 */

public class AdapterNomalNineImage extends
        RecyclerView.Adapter<AdapterNomalNineImage.NineImageHolder> {
    private Activity context ;
    private List<ImageThumb> mImageList ;
    private int mImageWidHei;
    private LayoutInflater inflater ;

    public AdapterNomalNineImage(Activity context , List<ImageThumb> mImageList) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mImageWidHei = (int) ((ResourceUtils.getScreenWidth(context)
                        - ResourceUtils.dip2px(context , 15) * 2 - ResourceUtils.dip2px(context , 2) * 2) / 3F);
    }

    @Override
    public NineImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NineImageHolder(inflater.inflate(
                R.layout.adapter_nomal_nine_image, parent , false)
                , mImageWidHei) ;
    }

    @Override
    public void onBindViewHolder(NineImageHolder holder
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

    static class NineImageHolder extends RecyclerView.ViewHolder{
        private ImageView mImageIv;

        public NineImageHolder(View itemView , int imageWidHei) {
            super(itemView);

            mImageIv = itemView.findViewById(R.id.adapter_nomal_nine_image_iv);

            mImageIv.getLayoutParams().width = imageWidHei ;
            mImageIv.getLayoutParams().height = imageWidHei ;
        }
    }
}
