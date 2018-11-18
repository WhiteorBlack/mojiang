package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/3/29.
 */

public class AdapterServerCategory extends RecyclerView.Adapter<AdapterServerCategory.NdcHolder> {
    private Context mContext ;
    private List<ServerCategory> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterServerCategory(Context mContext, List<ServerCategory> mDataList,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public NdcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NdcHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_server_category , parent , false),mClickListener);
    }

    @Override
    public void onBindViewHolder(NdcHolder holder, int position) {
        ServerCategory info = mDataList.get(position) ;
        if(info != null){
            String name = info.getCategoryTitle() ;
            String imgUrl = info.getImgUrl() ;

            holder.mNameTv.setText(name);
            GlideUtils.loader(MyApplication.getInstance() ,imgUrl , holder.mIconIv) ;
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }


    static class NdcHolder extends RecyclerView.ViewHolder{
        private ImageView mIconIv ;
        private TextView mNameTv ;

        public NdcHolder(View view,final RvItemViewClickListener clickListener){
            super(view);

            this.mIconIv = view.findViewById(R.id.adapter_server_category_iv) ;
            this.mNameTv = view.findViewById(R.id.adapter_server_category_name_tv) ;
            view.setOnClickListener(new View.OnClickListener() {
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
