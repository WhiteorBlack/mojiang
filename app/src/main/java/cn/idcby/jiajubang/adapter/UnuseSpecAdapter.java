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
import cn.idcby.jiajubang.Bean.UnuseSpecList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/4/23.
 */

public class UnuseSpecAdapter extends RecyclerView.Adapter<UnuseSpecAdapter.UspHolder> {
    private Context context ;
    private List<UnuseSpecList> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int mImageWidth ;

    public UnuseSpecAdapter(Context context, List<UnuseSpecList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.mImageWidth = (ResourceUtils.getScreenWidth(context) - ResourceUtils.dip2px(context ,40)) / 2;
    }

    @Override
    public UspHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UspHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_unuse_spec_list ,parent ,false)
                ,mImageWidth , mClickListener);
    }

    @Override
    public void onBindViewHolder(UspHolder holder, int position) {
        UnuseSpecList specList = mDataList.get(position) ;
        if(specList != null){
            String name = specList.getTitle() ;
            String imgUrl = specList.getImgUrl() ;

            holder.mNameTv.setText(name);
            GlideUtils.loader(imgUrl ,holder.mBgIv);
        }

    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    public static class UspHolder extends RecyclerView.ViewHolder{
        private ImageView mBgIv ;
        private TextView mNameTv ;

        public UspHolder(View itemView, int width , final RvItemViewClickListener clickListener) {
            super(itemView);

            mBgIv = itemView.findViewById(R.id.adapter_unuse_spec_bg_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_unuse_spec_name_tv) ;

            mBgIv.getLayoutParams().width = width ;
            mBgIv.getLayoutParams().height = (int) (width * 0.45F);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getLayoutPosition()) ;
                    }
                }
            });
        }
    }

}
