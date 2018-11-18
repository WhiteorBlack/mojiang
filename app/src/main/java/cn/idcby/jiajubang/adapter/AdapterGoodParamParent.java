package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodParam;
import cn.idcby.jiajubang.Bean.GoodParamParent;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;

/**
 * Created on 2018/8/22.
 */

public class AdapterGoodParamParent extends RecyclerView.Adapter<AdapterGoodParamParent.GppHolder> {
    private Context mContext ;
    private List<GoodParamParent> mDataList ;
    private RvMoreItemClickListener mClickListener ;

    public AdapterGoodParamParent(Context mContext, List<GoodParamParent> mDataList,RvMoreItemClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public GppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GppHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_good_param_parent ,parent ,false) ,mContext) ;
    }

    @Override
    public void onBindViewHolder(GppHolder holder, int position) {
        final GoodParamParent paramParent = mDataList.get(position) ;

        if(paramParent != null){
            holder.mTitleTv.setText(paramParent.getTitle());
            final boolean isNotExpand = paramParent.isNoExpand() ;
            holder.mTitleIv.setImageDrawable(mContext.getResources().getDrawable(isNotExpand
                    ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up));
            holder.mRv.setVisibility(isNotExpand ? View.GONE : View.VISIBLE);

            List<GoodParam> paramList = paramParent.getChildList() ;
            AdapterGoodParamChild childAdapter = new AdapterGoodParamChild(mContext ,paramList,position ,mClickListener) ;
            holder.mRv.setAdapter(childAdapter) ;

            holder.mTitleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paramParent.setNoExpand(!isNotExpand);
                    notifyDataSetChanged() ;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    public static class GppHolder extends RecyclerView.ViewHolder{
        private View mTitleLay ;
        private TextView mTitleTv ;
        private ImageView mTitleIv ;
        private RecyclerView mRv ;

        public GppHolder(View itemView,Context context) {
            super(itemView);

            mTitleLay = itemView.findViewById(R.id.adapter_good_param_parent_title_lay) ;
            mTitleIv = itemView.findViewById(R.id.adapter_good_param_parent_title_iv) ;
            mTitleTv = itemView.findViewById(R.id.adapter_good_param_parent_title_tv) ;
            mRv = itemView.findViewById(R.id.adapter_good_param_parent_child_rv) ;

            mRv.setLayoutManager(new GridLayoutManager(context ,3)) ;
            mRv.setFocusable(false);
            mRv.setNestedScrollingEnabled(false);
            mRv.addItemDecoration(new RvGridManagerItemDecoration(context , ResourceUtils.dip2px(context ,8)));
        }
    }

}
