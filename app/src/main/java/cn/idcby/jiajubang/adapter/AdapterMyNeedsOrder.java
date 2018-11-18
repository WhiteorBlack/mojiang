package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.MyNeedsOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.FragmentNeedsOrder;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * Created on 2018/4/16.
 */

public class AdapterMyNeedsOrder extends RecyclerView.Adapter<AdapterMyNeedsOrder.NeOrHolder> {
    private Activity context ;
    private List<MyNeedsOrderList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    private boolean mIsSend = false ; //我的需求--付款、取消、完成三个操作

    public AdapterMyNeedsOrder(Activity context, int orderType ,List<MyNeedsOrderList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mIsSend = FragmentNeedsOrder.NEED_ORDER_TYPE_MY == orderType;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public NeOrHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NeOrHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_needs_order ,parent ,false) , mClickListener) ;
    }

    @Override
    public void onBindViewHolder(NeOrHolder holder, int position) {
        MyNeedsOrderList info = mDataList.get(position) ;
        if(info != null){
            String typeId = info.getTypeId() ;
            String title = info.getNeedTitle() ;
            String content = info.getWorkDescribe() ;
            String time = info.getCreateDate() ;
            String money = info.getOrderAmount() ;

            if("1".equals(typeId)){
                holder.mTypeTv.setText("需求") ;
                holder.mTypeTv.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_xq_bg)) ;
            }else{
                holder.mTypeTv.setText("招标") ;
                holder.mTypeTv.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_zb_bg)) ;
            }

            holder.mTimeTv.setText("下单时间：" + time) ;
            holder.mMoneyTv.setText(money + "元") ;
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

            AdapterMyNeedsImage imageAdapter = new AdapterMyNeedsImage(context ,info.getThumbs()) ;
            holder.mPicRv.setAdapter(imageAdapter);

            int orderStatus = info.getOrderStatus() ;
            holder.mOptionsLay.setVisibility(View.GONE);

            if(mIsSend){
                if(1 == orderStatus){//待付款
                    holder.mOptionsLay.setVisibility(View.VISIBLE);
                    holder.mPayTv.setVisibility(View.VISIBLE);
                    holder.mCancelTv.setVisibility(View.GONE);
                    holder.mFinishTv.setVisibility(View.GONE);
                }else if(2 == orderStatus){//待完成
                    holder.mOptionsLay.setVisibility(View.VISIBLE);
                    holder.mPayTv.setVisibility(View.GONE);
                    holder.mCancelTv.setVisibility(View.VISIBLE);
                    holder.mFinishTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class NeOrHolder extends RecyclerView.ViewHolder{
        private TextView mTypeTv ;
        private TextView mTitleTv ;
        private TextView mContentTv ;
        private RecyclerView mPicRv ;
        private TextView mTimeTv ;
        private TextView mMoneyTv ;
        private TextView mFinishTv ;
        private TextView mCancelTv ;
        private TextView mPayTv ;
        private View mOptionsLay ;

        public NeOrHolder(View itemView , final RvItemViewClickListener mClickListener) {
            super(itemView);

            mTypeTv = itemView.findViewById(R.id.adapter_my_needs_order_type_tv) ;
            mTitleTv = itemView.findViewById(R.id.adapter_my_needs_order_title_tv) ;
            mContentTv = itemView.findViewById(R.id.adapter_my_needs_order_content_tv) ;
            mPicRv = itemView.findViewById(R.id.adapter_my_needs_order_pic_lay) ;
            mTimeTv = itemView.findViewById(R.id.adapter_my_needs_order_time_tv) ;
            mMoneyTv = itemView.findViewById(R.id.adapter_my_needs_order_money_tv) ;
            mFinishTv = itemView.findViewById(R.id.adapter_my_needs_order_options_finish_tv) ;
            mCancelTv = itemView.findViewById(R.id.adapter_my_needs_order_options_cancel_tv) ;
            mPayTv = itemView.findViewById(R.id.adapter_my_needs_order_options_pay_tv) ;
            mOptionsLay = itemView.findViewById(R.id.adapter_my_needs_order_options_lay) ;

            mPicRv.setFocusable(false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,getAdapterPosition()) ;
                    }
                }
            });
            mPayTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,getAdapterPosition()) ;
                    }
                }
            });
            mCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,getAdapterPosition()) ;
                    }
                }
            });
            mFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(4 ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
