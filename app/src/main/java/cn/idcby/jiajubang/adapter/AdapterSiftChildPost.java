package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.SiftWorkPost;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * 筛选职位下的二级职位列表
 * Created on 2018/9/12.
 */

public class AdapterSiftChildPost extends RecyclerView.Adapter<AdapterSiftChildPost.SiftProHolder> {
    private Context mContext ;
    private List<SiftWorkPost> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterSiftChildPost(Context mContext, List<SiftWorkPost> mDataList, RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public SiftProHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SiftProHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_sift_right_item ,parent ,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(SiftProHolder holder, int position) {
        SiftWorkPost info = mDataList.get(position) ;
        if(info != null){
            String name = info.getName() ;
            boolean isSelected = info.isSelected() ;

            holder.mTitleTv.setText(name);
            holder.mTitleTv.setTextColor(mContext.getResources()
                    .getColor(isSelected
                            ? R.color.sift_right_tv_selected
                            : R.color.sift_right_tv_normal));
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
            this.mTitleTv = itemView.findViewById(R.id.adapter_sift_right_title_tv);

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
