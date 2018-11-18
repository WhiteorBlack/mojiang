package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.CircleCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 圈子分类
 */

public class AdapterCircleCategory extends RecyclerView.Adapter<AdapterCircleCategory.HsViewHolder> {
    private Context mContext;
    private List<CircleCategory> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;
    private int mItemWidth ;

    public AdapterCircleCategory(Context mContext, List<CircleCategory> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
        this.mItemWidth = (int) ((ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext ,40)) / 4.5F);
    }

    @Override
    public AdapterCircleCategory.HsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_circle_category_item, parent ,false);
        return new HsViewHolder(view ,mClickListener,mItemWidth);
    }

    @Override
    public void onBindViewHolder(HsViewHolder holder, int position) {

        CircleCategory info = mData.get(position) ;
        if(info != null){
            String imgUrl = info.getImgUrl() ;
            String name = info.getCategoryTitle() ;

            holder.mNameTv.setText(StringUtils.convertNull(name));
            GlideUtils.loaderRound(imgUrl, holder.mHeadIv, 10);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mHeadIv;
        private TextView mNameTv;

        public HsViewHolder(View itemView ,final RvItemViewClickListener clickListener,int itemWidth) {
            super(itemView);

            mHeadIv = itemView.findViewById(R.id.adapter_circle_category_bg_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_circle_category_name_tv) ;

            mHeadIv.getLayoutParams().width = itemWidth ;
            mHeadIv.getLayoutParams().height = itemWidth ;

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
