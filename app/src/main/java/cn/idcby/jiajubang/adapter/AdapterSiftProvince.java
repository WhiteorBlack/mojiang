package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.SiftCity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * 筛选省份列表
 * Created on 2018/9/12.
 */

public class AdapterSiftProvince extends RecyclerView.Adapter<AdapterSiftProvince.SiftProHolder> {
    private Context mContext ;
    private List<SiftCity> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterSiftProvince(Context mContext, List<SiftCity> mDataList, RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public SiftProHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SiftProHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_sift_left_item ,parent ,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(SiftProHolder holder, int position) {
        SiftCity info = mDataList.get(position) ;
        if(info != null){
            String name = info.getAreaName() ;
            boolean isSelected = info.isSelected() ;

            holder.mTitleTv.setText(name);
            holder.mTitleTv.setBackgroundColor(mContext.getResources()
                    .getColor(isSelected
                            ? R.color.sift_left_selected
                            : R.color.sift_left_normal));
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    public static class SiftProHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv ;

        public SiftProHolder(View itemView, final RvItemViewClickListener clickListener) {
            super(itemView);
            this.mTitleTv = itemView.findViewById(R.id.adapter_sift_left_title_tv);

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
