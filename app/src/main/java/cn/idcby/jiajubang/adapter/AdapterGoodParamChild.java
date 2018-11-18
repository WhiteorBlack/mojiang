package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.GoodParam;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;

/**
 * Created on 2018/8/22.
 */

public class AdapterGoodParamChild extends RecyclerView.Adapter<AdapterGoodParamChild.GpcHolder> {
    private Context mContext ;
    private List<GoodParam> mDataList ;
    private int mParentPosition ;
    private RvMoreItemClickListener mClickListener ;

    public AdapterGoodParamChild(Context mContext, List<GoodParam> mDataList,int position ,RvMoreItemClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mParentPosition = position;
        this.mClickListener = mClickListener;
    }

    @Override
    public GpcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GpcHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_good_param_child ,parent ,false)) ;
    }

    @Override
    public void onBindViewHolder(GpcHolder holder, int position) {
        GoodParam goodParam = mDataList.get(position) ;
        final int realPosition = position ;
        if(goodParam != null){
            boolean isSelected = goodParam.isSelected() ;


            holder.mTitleTv.setText(goodParam.getTitle());
            holder.mTitleTv.setTextColor(mContext.getResources().getColor(isSelected
                    ? R.color.color_good_param_theme : R.color.color_nomal_text));
            holder.mTitleTv.setBackgroundDrawable(mContext.getResources().getDrawable(isSelected
                    ? R.drawable.bg_good_param_checked : R.drawable.bg_good_param_nomal));

            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,mParentPosition ,realPosition) ;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class GpcHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv ;

        public GpcHolder(View itemView) {
            super(itemView);

            mTitleTv = itemView.findViewById(R.id.adapter_good_param_child_title_tv) ;
        }
    }

}
