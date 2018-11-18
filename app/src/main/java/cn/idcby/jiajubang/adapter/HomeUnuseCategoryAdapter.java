package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 首页闲置
 */

public class HomeUnuseCategoryAdapter extends RecyclerView.Adapter<HomeUnuseCategoryAdapter.HsViewHolder> {
    private Context mContext;
    private List<UnusedCategory> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;
    private int mItemWidth ;

    public HomeUnuseCategoryAdapter(Context mContext, List<UnusedCategory> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
        this.mItemWidth = (int) ((ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext ,8) * 4) / 3.5F);
    }

    @Override
    public HomeUnuseCategoryAdapter.HsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_item_for_home_spec_unuse, parent ,false);
        return new HsViewHolder(view ,mClickListener,mItemWidth);
    }

    @Override
    public void onBindViewHolder(HsViewHolder holder, int position) {

        UnusedCategory info = mData.get(position) ;
        if(info != null){
            String imgUrl = info.getImgUrl() ;
            GlideUtils.loaderRound(imgUrl, holder.mHeadIv,10);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mHeadIv;

        public HsViewHolder(View itemView ,final RvItemViewClickListener clickListener,int itemWidth) {
            super(itemView);

            mHeadIv = itemView.findViewById(R.id.view_item_home_spec_unuse_bg_iv) ;

            mHeadIv.getLayoutParams().width = itemWidth ;
            mHeadIv.getLayoutParams().height = (int) (itemWidth * 3 / 4F);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 , getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
