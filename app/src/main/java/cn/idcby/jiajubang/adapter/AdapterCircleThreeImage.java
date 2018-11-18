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
 * D:多图布局样式
 * Created on 2018/3/23.
 */

public class AdapterCircleThreeImage extends
        RecyclerView.Adapter<AdapterCircleThreeImage.NewsListHeadLineHolderImage> {
    private Activity context ;
    private List<ImageThumb> mImageList ;
    private int mMoreImageWidHei ;
    private LayoutInflater inflater ;

    public AdapterCircleThreeImage(Activity context , List<ImageThumb> mImageList) {
        this.context = context ;
        this.mImageList = mImageList ;
        this.inflater = LayoutInflater.from(context) ;
        this.mMoreImageWidHei = (ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context , 15) * 2 - ResourceUtils.dip2px(context , 2) * 2) / 3 ;
    }

    @Override
    public NewsListHeadLineHolderImage onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsListHeadLineHolderImage(inflater.inflate(
                R.layout.adapter_circle_item_three_image, parent , false)
                , mMoreImageWidHei) ;
    }

    @Override
    public void onBindViewHolder(NewsListHeadLineHolderImage holder
            , int position) {
        GlideUtils.loaderNoType(MyApplication.getInstance()
                , mImageList.get(position).getThumbImgUrl() , holder.mImageIv) ;

        final int curPosi = position ;
        holder.mImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipUtils.toImageShowActivityWithThumb(context ,mImageList ,curPosi); ;
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mImageList ? 0 : mImageList.size() ;
    }

    static class NewsListHeadLineHolderImage extends RecyclerView.ViewHolder{
        private ImageView mImageIv;

        public NewsListHeadLineHolderImage(View itemView , int imageWidHei) {
            super(itemView);

            mImageIv = itemView.findViewById(R.id.adapter_item_newslist_headline_three_image_iv);

            mImageIv.getLayoutParams().width = imageWidHei ;
            mImageIv.getLayoutParams().height = imageWidHei ;
        }
    }
}
