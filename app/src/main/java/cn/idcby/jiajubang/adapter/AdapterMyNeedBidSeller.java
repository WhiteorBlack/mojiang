package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.MyNeedsBidSeller;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.NeedsConfirmBidActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/3/29.
 */

public class AdapterMyNeedBidSeller extends BaseAdapter {
    private Context context ;
    private String needId ;
    private List<MyNeedsBidSeller> mDataList ;
    private boolean mIsBonded = false ;

    public AdapterMyNeedBidSeller(Context context, String needId ,List<MyNeedsBidSeller> mDataList,boolean mIsBonded) {
        this.context = context;
        this.needId = needId;
        this.mDataList = mDataList;
        this.mIsBonded = mIsBonded;
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
        SlHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_need_seller_list , viewGroup , false) ;
            holder = new SlHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (SlHolder) view.getTag();
        }

        MyNeedsBidSeller info = mDataList.get(i) ;
        if(info != null){
            final String offerId = info.getOfferId() ;
            final String userId = info.getCreateUserId() ;
            String name = info.getCreateUserName() ;
            String location = info.getPosition() ;
            String time = info.getCreateDate() ;
            String imgUrl = info.getHeadIcon() ;

            holder.mSellerTipsTv.setVisibility(info.isBid() ? View.VISIBLE : View.GONE);
            holder.mSellerNameTv.setText(name);
            holder.mLocationTv.setText(location);
            holder.mTimeTv.setText(time);
            GlideUtils.loaderUser(imgUrl ,holder.mSellerIv) ;

            holder.mSellerIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(context ,userId) ;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toCtIt = new Intent(context ,NeedsConfirmBidActivity.class) ;
                    toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , needId) ;
                    toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , offerId) ;
                    toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_SELF , true) ;
                    toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED , mIsBonded) ;
                    context.startActivity(toCtIt) ;
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

        public SlHolder(View v) {
            mSellerIv = v.findViewById(R.id.adapter_need_seller_iv) ;
            mSellerTipsTv = v.findViewById(R.id.adapter_need_seller_tips_tv) ;
            mSellerNameTv = v.findViewById(R.id.adapter_need_seller_tv) ;
            mLocationTv = v.findViewById(R.id.adapter_need_seller_location_tv) ;
            mTimeTv = v.findViewById(R.id.adapter_need_seller_time_tv) ;
        }
    }

}
