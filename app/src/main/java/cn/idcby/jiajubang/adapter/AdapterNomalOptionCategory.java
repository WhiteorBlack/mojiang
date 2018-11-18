package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NomalRvCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * Created on 2018/4/19.
 */

public class AdapterNomalOptionCategory extends RecyclerView.Adapter<AdapterNomalOptionCategory.QaCtHolder> {
    private Context context ;
    private List<NomalRvCategory> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterNomalOptionCategory(Context context, List<NomalRvCategory> mDataList
            , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public QaCtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QaCtHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_question_category_item ,parent ,false) ,mClickListener);
    }

    @Override
    public void onBindViewHolder(QaCtHolder holder, int position) {
        NomalRvCategory category = mDataList.get(position) ;
        if(category != null){
            String title = category.getCategoryTitle() ;
            boolean isSelected = category.isSelected() ;

            holder.mNameTv.setText(title);
            holder.mNameTv.setTextColor(context.getResources().getColor(isSelected
                    ? R.color.color_nav_checked_two
                    : R.color.color_nav_normal_two));
            holder.mInditorView.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class QaCtHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv ;
        private View mInditorView ;

        public QaCtHolder(View itemView, final RvItemViewClickListener mClickListener) {
            super(itemView);

            mNameTv = itemView.findViewById(R.id.adapter_question_category_item_name_tv) ;
            mInditorView = itemView.findViewById(R.id.adapter_question_category_item_inditor_iv) ;

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
