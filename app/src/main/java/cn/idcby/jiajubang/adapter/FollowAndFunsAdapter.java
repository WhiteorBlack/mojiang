package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.FoFunsBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018-04-26.
 */

public class FollowAndFunsAdapter extends RecyclerView.Adapter<FollowAndFunsAdapter.ViewHolder>{
    private Context mContext;
    private boolean mIsFocus = true ;
    private List<FoFunsBean> listData;
    private RvItemViewClickListener mClickListener ;

    public FollowAndFunsAdapter(Context mContext ,boolean mIsFocus ,List<FoFunsBean> listData ,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mIsFocus = mIsFocus;
        this.mClickListener = mClickListener;
        this.listData = listData;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_follow_and_funs, viewGroup, false);
        return new ViewHolder(view,mClickListener);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FoFunsBean funsBean = listData.get(position) ;

        boolean isFocus = funsBean.isFocused() ;

        viewHolder.mFocusTv.setVisibility(mIsFocus ? View.VISIBLE : View.INVISIBLE);
        viewHolder.mFocusTv.setBackgroundDrawable(mContext.getResources().getDrawable(isFocus
                ? R.drawable.round_focus_bg_focused
                : R.drawable.round_focus_bg_nomal));
        viewHolder.mFocusTv.setTextColor(mContext.getResources().getColor(isFocus
                ? R.color.color_grey_88
                : R.color.color_theme));
        viewHolder.mFocusTv.setText(isFocus ? "已关注" : "+关注");

        String desc = funsBean.getPersonalitySignature() ;

        viewHolder.mNameTv.setText(funsBean.getNickName());
        viewHolder.mDescTv.setText("".equals(desc) ? "暂无简介" : desc);
        viewHolder.mLocationTv.setText(funsBean.getPostion()) ;

        GlideUtils.loaderUser(funsBean.getHeadIcon(), viewHolder.mPortraitIv);
        viewHolder.itemView.setTag(listData.get(position));
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return listData.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPortraitIv;
        private TextView mNameTv;
        private TextView mDescTv;
        private TextView mFocusTv;
        private TextView mLocationTv;

        public ViewHolder(View view , final RvItemViewClickListener mClickListener) {
            super(view);
            mPortraitIv = view.findViewById(R.id.adapter_fans_follow_user_iv);
            mNameTv = view.findViewById(R.id.adapter_fans_follow_name_tv);
            mDescTv = view.findViewById(R.id.adapter_fans_follow_desc_tv);
            mFocusTv = view.findViewById(R.id.adapter_fans_follow_focus_tv);
            mLocationTv = view.findViewById(R.id.adapter_fans_follow_location_tv);

            mFocusTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,getAdapterPosition()) ;
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
