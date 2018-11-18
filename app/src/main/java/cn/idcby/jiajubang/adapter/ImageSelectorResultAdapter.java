package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;


/**
 * Created by mrrlb on 2017/3/9.
 * 选择图片展示
 */

public class ImageSelectorResultAdapter extends RecyclerView.Adapter<ImageSelectorResultAdapter.MyViewHolder> {

    private RecyclerViewClickListener myItemClickListener = null;
    private Context mContext;
    private List<String> data;
    private int itemWidth = 0 ;
    private int itemHeight = 0 ;
    private int itemMaxSize = 9 ;
    private boolean itemCanDelete = true ;

    public ImageSelectorResultAdapter(Context mContext, List<String> data
            , int width , int height, int maxSize,RecyclerViewClickListener myItemClickListener) {
        this.mContext = mContext;
        this.data = data;
        this.myItemClickListener = myItemClickListener;
        this.itemWidth = width;
        this.itemHeight = height;
        this.itemMaxSize = maxSize;
    }

    public ImageSelectorResultAdapter(Context mContext, List<String> data
            , int width , int height,int maxSize,boolean canDelete,RecyclerViewClickListener myItemClickListener) {
        this(mContext ,data ,width , height ,maxSize , myItemClickListener) ;
        this.itemCanDelete = canDelete;
    }

    @Override
    public ImageSelectorResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.view_item_image_selector_result, null);
        return new MyViewHolder(view, myItemClickListener,itemWidth ,itemHeight);

    }

    @Override
    public void onBindViewHolder(ImageSelectorResultAdapter.MyViewHolder holder, final int position) {
        String imgUrl = data.get(position) ;

        holder.iv_del.setVisibility((!itemCanDelete || null == imgUrl) ? View.INVISIBLE : View.VISIBLE) ;

        if(position == data.size() - 1 && null == imgUrl){//最后一个
            GlideUtils.loader(MyApplication.getInstance()
                    ,R.mipmap.addpic  , holder.imageView) ;
        }else{
            GlideUtils.loaderAddPic(data.get(position) , holder.imageView) ;
        }
    }

    @Override
    public int getItemCount() {
        int size = null == data ? 0 : data.size();
        if(size > itemMaxSize){
            size = itemMaxSize ;
        }
        return size;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View iv_del;

        public MyViewHolder(View itemView, final RecyclerViewClickListener listener , int width , int height) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            iv_del = itemView.findViewById(R.id.iv_del);

            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onItemClickListener(0 ,getAdapterPosition());
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onItemClickListener(1 ,getAdapterPosition());
                    }
                }
            });

            if(width > 0 && height > 0){
                imageView.getLayoutParams().width = width ;
                imageView.getLayoutParams().height = height ;
            }
        }


    }
}
