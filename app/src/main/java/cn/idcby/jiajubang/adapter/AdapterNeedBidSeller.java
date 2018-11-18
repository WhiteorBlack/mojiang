package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NeedsBidSeller;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class AdapterNeedBidSeller extends BaseAdapter {
    private Context context ;
    private List<NeedsBidSeller> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private boolean mIsSelf = false ;

    public void setIsSelf(boolean mIsSelf) {
        this.mIsSelf = mIsSelf;
        notifyDataSetChanged() ;
    }

    public AdapterNeedBidSeller(Context context, List<NeedsBidSeller> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int realPosition = i ;

        SlHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_need_seller_list , viewGroup , false) ;
            holder = new SlHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (SlHolder) view.getTag();
        }

        NeedsBidSeller info = mDataList.get(i) ;
        if(info != null){
            String name = StringUtils.convertNull(info.RealName) ;
            String location = StringUtils.convertNull(info.Position) ;
            String time = StringUtils.convertDateToDay(info.ReleaseTime) ;
            String imgUrl = StringUtils.convertNull(info.HeadIcon) ;

            holder.mSellerTipsTv.setVisibility(info.isBid() ? View.VISIBLE : View.GONE);
            holder.mSellerNameTv.setText(name);
            holder.mLocationTv.setText(location);
            holder.mTimeTv.setText(time);

            holder.mOptionTv.setVisibility(mIsSelf ? View.VISIBLE : View.INVISIBLE) ;

            GlideUtils.loaderUser(imgUrl ,holder.mSellerIv) ;

            holder.mSellerIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
        }

        return view;
    }

    private static class SlHolder{
        private ImageView mSellerIv ;
        private TextView mSellerTipsTv ;
        private TextView mSellerNameTv ;
        private TextView mLocationTv ;
        private TextView mTimeTv ;
        private TextView mOptionTv ;

        public SlHolder(View v) {
            mSellerIv = v.findViewById(R.id.adapter_need_seller_iv) ;
            mSellerTipsTv = v.findViewById(R.id.adapter_need_seller_tips_tv) ;
            mSellerNameTv = v.findViewById(R.id.adapter_need_seller_tv) ;
            mLocationTv = v.findViewById(R.id.adapter_need_seller_location_tv) ;
            mTimeTv = v.findViewById(R.id.adapter_need_seller_time_tv) ;
            mOptionTv = v.findViewById(R.id.adapter_need_seller_option_tv) ;
        }
    }

}
