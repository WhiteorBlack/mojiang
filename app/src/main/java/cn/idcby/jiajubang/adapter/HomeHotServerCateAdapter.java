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
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 首页热门服务
 * 2018-05-04 15:54:43
 * 4个平分界面
 * 2018-05-07 15:33:42
 * 改回设计图样式
 * 2018-07-25 14:58:24
 * 只要图片，不要文字了
 */

public class HomeHotServerCateAdapter extends RecyclerView.Adapter<HomeHotServerCateAdapter.HsViewHolder> {
    private Context mContext;
    private List<ServerCategory> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;
    private int mItemWidth ;

    public HomeHotServerCateAdapter(Context mContext, List<ServerCategory> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
        this.mItemWidth = (int) ((ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext ,8) * 4) / 3.5F);
    }

    @Override
    public HomeHotServerCateAdapter.HsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_item_for_home_hot_server, parent ,false);
        return new HsViewHolder(view ,mClickListener,mItemWidth);
    }

    @Override
    public void onBindViewHolder(HsViewHolder holder, int position) {

        ServerCategory info = mData.get(position) ;
        if(info != null){
            String imgUrl = info.getImgUrl() ;
            String name = info.getCategoryTitle() ;

            holder.mNameTv.setText(name);
            GlideUtils.loaderRound(imgUrl, holder.mHeadIv,10);
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

            mHeadIv = itemView.findViewById(R.id.view_item_home_server_bg_iv) ;
            mNameTv = itemView.findViewById(R.id.view_item_home_server_name_tv) ;

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
