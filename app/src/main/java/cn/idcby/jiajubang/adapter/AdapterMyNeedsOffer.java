package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.MyNeedsOfferList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * Created on 2018/4/16.
 */

public class AdapterMyNeedsOffer extends RecyclerView.Adapter<AdapterMyNeedsOffer.NeOfHolder> {
    private Activity context ;
    private List<MyNeedsOfferList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyNeedsOffer(Activity context, List<MyNeedsOfferList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public NeOfHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NeOfHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_needs_offer_list ,parent ,false) , mClickListener) ;
    }

    @Override
    public void onBindViewHolder(NeOfHolder holder, int position) {
        MyNeedsOfferList info = mDataList.get(position) ;
        if(info != null){
            String typeId = info.getTypeId() ;
            String title = info.getNeedTitle() ;
            String stateText = info.getOrderStatusName() ;
            String content = info.getNeedExplain() ;

            if("1".equals(typeId)){
                holder.mTypeTv.setText("需求") ;
                holder.mTypeTv.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_xq_bg)) ;
            }else{
                holder.mTypeTv.setText("招标") ;
                holder.mTypeTv.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_zb_bg)) ;
            }

            holder.mOptionFinishTv.setVisibility(info.canFinishNeed() ? View.VISIBLE : View.GONE);
            holder.mOpLay.setVisibility(info.canFinishNeed() ? View.VISIBLE : View.GONE);
            holder.mOpDv.setVisibility(info.canFinishNeed() ? View.VISIBLE : View.GONE);

//            if(info.canOrderStartPay()){
//                holder.mOptionTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_theme_small_bg));
//            }else{
//                holder.mOptionTv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_grey_aa_bg));
//            }

            holder.mStateTv.setText(stateText) ;
            holder.mTitleTv.setText(title);
            holder.mContentTv.setText(content);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mPicRv.setLayoutManager(layoutManager) ;

            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                    , ResourceUtils.dip2px(context ,5)
                    , context.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mPicRv.getItemDecorationCount() == 0){
                holder.mPicRv.addItemDecoration(itemDecoration);
            }

            AdapterMyNeedsImage imageAdapter = new AdapterMyNeedsImage(context ,info.getAlbumsList()) ;
            holder.mPicRv.setAdapter(imageAdapter);

            String userName = info.getCreateUserName() ;
            String time = info.getCreateDate() ;
            String imgUrl = info.getHeadIcon() ;
            String money = info.getTotalOffer() ;

            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;
            holder.mUserNameTv.setText(userName) ;
            holder.mUserDateTv.setText(time) ;
            holder.mUserMoneyTv.setText(money+"元") ;
            holder.mUserBidTipTv.setVisibility(info.isBid() ? View.VISIBLE : View.GONE) ;
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class NeOfHolder extends RecyclerView.ViewHolder{
        private TextView mTypeTv ;
        private TextView mTitleTv ;
        private TextView mStateTv ;
        private TextView mContentTv ;
        private RecyclerView mPicRv ;
        private View mOpDv ;
        private View mOpLay ;
        private TextView mOptionTv ;
        private TextView mOptionFinishTv ;
        private ImageView mUserIv ;
        private TextView mUserNameTv ;
        private TextView mUserDateTv ;
        private TextView mUserMoneyTv ;
        private TextView mUserBidTipTv ;
        private TextView mBidDtTv ;
        private TextView mDeleteTv ;
        private TextView mUpdownTv ;

        public NeOfHolder(View itemView , final RvItemViewClickListener mClickListener) {
            super(itemView);

            mTypeTv = itemView.findViewById(R.id.adapter_my_needs_offer_type_tv) ;
            mTitleTv = itemView.findViewById(R.id.adapter_my_needs_offer_title_tv) ;
            mStateTv = itemView.findViewById(R.id.adapter_my_needs_offer_state_tv) ;
            mContentTv = itemView.findViewById(R.id.adapter_my_needs_offer_content_tv) ;
            mOptionTv = itemView.findViewById(R.id.adapter_my_needs_offer_option_tv) ;
            mOptionFinishTv = itemView.findViewById(R.id.adapter_my_needs_offer_finish_tv) ;
            mOpDv = itemView.findViewById(R.id.adapter_my_needs_offer_option_dv_one) ;
            mOpLay = itemView.findViewById(R.id.adapter_my_needs_offer_option_lay) ;
            mPicRv = itemView.findViewById(R.id.adapter_my_needs_offer_pic_lay) ;

            mUserIv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_iv) ;
            mUserNameTv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_name_tv) ;
            mUserDateTv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_time_tv) ;
            mUserMoneyTv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_money_tv) ;
            mUserBidTipTv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_bid_tv) ;
            mBidDtTv = itemView.findViewById(R.id.adapter_my_needs_offer_seller_dt_tv) ;
            mDeleteTv = itemView.findViewById(R.id.adapter_my_needs_offer_delete_tv) ;
            mUpdownTv = itemView.findViewById(R.id.adapter_my_needs_offer_updown_tv) ;

            mOptionTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,getAdapterPosition()) ;
                    }
                }
            });
            mBidDtTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,getAdapterPosition()) ;
                    }
                }
            });
            mUpdownTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,getAdapterPosition()) ;
                    }
                }
            });
            mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(4 ,getAdapterPosition()) ;
                    }
                }
            });
            mOptionFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(5 ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
