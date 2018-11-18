package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * Created on 2018/8/17.
 */

public class AdapterGoodCategoryList extends RecyclerView.Adapter<AdapterGoodCategoryList.GcHolder> {
    private Context context ;
    private List<UnusedCategory> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterGoodCategoryList(Context context, List<UnusedCategory> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public GcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GcHolder(LayoutInflater.from(context).inflate(R.layout.adapter_good_category_list,parent ,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(GcHolder holder, int position) {

        UnusedCategory category = mDataList.get(position) ;
        if(category != null){
            holder.mTitleTv.setText(category.getCategoryTitle());
        }

    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    public static class GcHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv ;

        public GcHolder(View itemView, final RvItemViewClickListener mClickListener) {
            super(itemView);

            mTitleTv = itemView.findViewById(R.id.adapter_good_category_title_tv) ;
            mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,getAdapterPosition()) ;
                    }
                }
            });
        }
    }

}
