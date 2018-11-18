package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.MyNeedsBidSeller;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.NeedsDetailsActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 我的发布--需求
 * Created on 2018/3/29.
 *
 * 2018-05-07 04:23:03
 * 需求
 *
 * 2018-05-27 18:37:27
 * 跟苹果一致，方图
 * 已选标的话，不能 下架
 */

public class AdapterMySendNeedsList extends BaseAdapter {
    private Activity mContext ;
    private List<NeedsList> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int mOffset = 0 ;

    public AdapterMySendNeedsList(Activity mContext, List<NeedsList> mDataList,RvItemViewClickListener mClickListener ) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.mOffset = ResourceUtils.dip2px(mContext , 15) + ResourceUtils.dip2px(mContext , 5) * 2;
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

        NdHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_send_needs_list , viewGroup , false) ;
            holder = new NdHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (NdHolder) view.getTag();
        }

        NeedsList info = mDataList.get(i) ;
        if(info != null){
            final String needId = info.getNeedId() ;
            int typeId = info.TypeId ;
            String title = info.NeedTitle ;
            String statusText = info.getOrderStatusText();
            String content = info.NeedExplain ;
            final boolean isBonded = info.isBonded() ;

            holder.mUpdownTv.setText(info.isEnabledMark() ? "下架" : "上架");
            holder.mOptionsLay.setVisibility(View.GONE);
            holder.mOptionsTv.setVisibility(View.GONE);
            holder.mEditTv.setVisibility(View.GONE);
            holder.mDeleteTv.setVisibility(View.GONE);
            holder.mFinishTv.setVisibility(View.GONE);
            holder.mUpdownTv.setVisibility(View.GONE);

            if(info.isUndownNeed()){
                holder.mOptionsLay.setVisibility(View.VISIBLE);
                holder.mUpdownTv.setVisibility(View.VISIBLE);
            }

            if(info.isFinishNeed()){
                holder.mOptionsLay.setVisibility(View.VISIBLE);
                holder.mFinishTv.setVisibility(View.VISIBLE);
            }

            if(1 == typeId){
                if(info.isHaveOffer()){
                    holder.mOptionsLay.setVisibility(View.VISIBLE);
                    holder.mEditTv.setVisibility(View.VISIBLE);
                    holder.mDeleteTv.setVisibility(View.VISIBLE);
                }

                holder.mTypeTv.setText("需求") ;
                holder.mTypeTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.round_needs_xq_bg)) ;
            }else if(2 == typeId){
                if(info.isHaveOffer() || info.isPayBond()){
                    holder.mOptionsLay.setVisibility(View.VISIBLE);

                    if(info.isPayBond()){
                        holder.mOptionsTv.setVisibility(View.VISIBLE);
                    }

                    if(info.isHaveOffer()){
                        holder.mEditTv.setVisibility(View.VISIBLE);
                        holder.mDeleteTv.setVisibility(View.VISIBLE);
                    }
                }

                holder.mTypeTv.setText("招标") ;
                holder.mTypeTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.round_needs_zb_bg)) ;
            }

            holder.mTitleTv.setText(StringUtils.convertNull(title));
            holder.mStateTv.setText(statusText);
            holder.mContentTv.setText(StringUtils.convertNull(content));

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mPicLay.setLayoutManager(layoutManager) ;
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                    ,ResourceUtils.dip2px(mContext ,5)
                    , mContext.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mPicLay.getItemDecorationCount() == 0){
                holder.mPicLay.addItemDecoration(itemDecoration);
            }
            AdapterNomalImage imageAdapter = new AdapterNomalImage(mContext ,info.getAlbumsList(),mOffset,3.3F) ;
            holder.mPicLay.setAdapter(imageAdapter);

            List<MyNeedsBidSeller> mSellerList = info.getNeedOfferList() ;
            AdapterMyNeedBidSeller sellerAdapter = new AdapterMyNeedBidSeller(mContext,needId ,mSellerList,isBonded) ;
            holder.mSellerLv.setAdapter(sellerAdapter) ;

            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,realPosition);
                    }
                }
            });
            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1,realPosition);
                    }
                }
            });
            holder.mOptionsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2,realPosition);
                    }
                }
            });
            holder.mFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3,realPosition);
                    }
                }
            });
            holder.mRefreshTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(4,realPosition);
                    }
                }
            });
            holder.mUpdownTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(5,realPosition);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(needId)){
                        Intent intent=new Intent(mContext, NeedsDetailsActivity.class);
                        intent.putExtra(SkipUtils.INTENT_NEEDS_ID,needId);
                        intent.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED,isBonded);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        return view;
    }

    private static class NdHolder {
        private TextView mTypeTv ;
        private TextView mTitleTv ;
        private TextView mStateTv;
        private TextView mContentTv ;
        private RecyclerView mPicLay ;
        private View mOptionsLay;
        private TextView mOptionsTv;
        private TextView mEditTv;
        private TextView mDeleteTv;
        private TextView mFinishTv;
        private TextView mRefreshTv;
        private TextView mUpdownTv;
        private ListView mSellerLv ;

        public NdHolder(View view) {
            mTypeTv = view.findViewById(R.id.adapter_my_send_need_type_tv) ;
            mTitleTv = view.findViewById(R.id.adapter_my_send_need_title_tv) ;
            mStateTv = view.findViewById(R.id.adapter_my_send_need_state_tv) ;
            mContentTv = view.findViewById(R.id.adapter_my_send_need_content_tv) ;
            mEditTv = view.findViewById(R.id.adapter_my_send_need_edit_tv) ;
            mDeleteTv = view.findViewById(R.id.adapter_my_send_need_delete_tv) ;
            mFinishTv = view.findViewById(R.id.adapter_my_send_need_finish_tv) ;
            mOptionsTv = view.findViewById(R.id.adapter_my_send_need_comment_tv) ;
            mOptionsLay = view.findViewById(R.id.adapter_my_send_need_comment_lay) ;
            mPicLay = view.findViewById(R.id.adapter_my_send_need_pic_lay) ;
            mRefreshTv = view.findViewById(R.id.adapter_my_send_need_refresh_tv) ;
            mUpdownTv = view.findViewById(R.id.adapter_my_send_need_updown_tv) ;
            mSellerLv = view.findViewById(R.id.adapter_my_send_need_seller_rv) ;
        }
    }
}
