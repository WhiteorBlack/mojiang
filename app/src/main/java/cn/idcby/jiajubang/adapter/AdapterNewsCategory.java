package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NewsCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * Created on 2018/6/26.
 */

public class AdapterNewsCategory extends RecyclerView.Adapter<AdapterNewsCategory.NcHolder> {
    private Context mContext ;
    private List<NewsCategory> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterNewsCategory(Context mContext, List<NewsCategory> mDataList, RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public NcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NcHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_news_category,parent ,false),mClickListener);
    }

    @Override
    public void onBindViewHolder(NcHolder holder, int position) {
        NewsCategory category = mDataList.get(position) ;
        if(category != null){
            holder.mNameTv.setText(category.getCategoryTitle());
            holder.mNameTv.setTextColor(mContext.getResources().getColor(category.isSelelcted()
                    ? R.color.color_theme : R.color.color_nomal_text));
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }

    static class NcHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv ;

        public NcHolder(View itemView, final RvItemViewClickListener mClickListener) {
            super(itemView);

            mNameTv = itemView.findViewById(R.id.adapter_news_category_name_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
