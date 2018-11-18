package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.Bean.GoodOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.FragmentGoodOrder;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RecyclerViewFocusble;

/**
 * Created on 2018/4/24.
 * 2018-07-03 17:21:13
 * 删除不相干的代码，只保留了我提供的订单相关操作
 */

public class AdapterMyGoodOrder extends RecyclerView.Adapter<AdapterMyGoodOrder.GoOrHolder> {
    private Context context ;
    private List<GoodOrderList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    private boolean mIsUnuse = false ; //闲置商品

    public AdapterMyGoodOrder(Context context, int orderType, List<GoodOrderList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mIsUnuse = FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE == orderType;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public GoOrHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoOrHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_my_good_order ,parent ,false) , mClickListener) ;
    }

    @Override
    public void onBindViewHolder(GoOrHolder holder, int position) {
        GoodOrderList orderList = mDataList.get(position) ;

        if(orderList != null){
            String time = orderList.getCreateDate() ;
            String state = orderList.getStatusText() ;
            String money = orderList.getPayableAmount() ;

            holder.mTimeTv.setText("下单时间：" + time);
            holder.mTypeTv.setText(state);
            holder.mMoneyTv.setText("¥" + money);

            holder.mOptionsLay.setVisibility(View.GONE);
            holder.mEditTv.setVisibility(View.GONE);
            holder.mSendTv.setVisibility(View.GONE);
            holder.mDeleteIv.setVisibility(orderList.canDelete() ? View.VISIBLE : View.GONE);
            holder.mOptionsLay.setVisibility(orderList.canDelete() ? View.VISIBLE : View.GONE);

            String status = orderList.getStatus() ;

            if("1".equals(status)){
                holder.mOptionsLay.setVisibility(View.VISIBLE);
                holder.mEditTv.setVisibility(View.VISIBLE);
            }else if("2".equals(status)){
                holder.mOptionsLay.setVisibility(View.VISIBLE);
                holder.mSendTv.setVisibility(View.VISIBLE);
            }

            List<GoodOrderGood> goodList = orderList.getOrderItem() ;
            List<GoodOrderGood> mGoodList = new ArrayList<>() ;
            if(goodList != null && goodList.size() > 0){
                mGoodList.addAll(goodList) ;
            }

            int count = 0 ;
            for(GoodOrderGood good : mGoodList){
                count += StringUtils.convertString2Count(good.getQuantity()) ;
            }

            holder.mCountTv.setText("共" + count + "件商品");

            AdapterMyGoodOrderGood goodAdapter = new AdapterMyGoodOrderGood(context ,mIsUnuse ,mGoodList) ;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            holder.mPicRv.setLayoutManager(layoutManager) ;
            holder.mPicRv.setAdapter(goodAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class GoOrHolder extends RecyclerView.ViewHolder{
        private TextView mTypeTv ;
        private TextView mTimeTv ;
        private RecyclerViewFocusble mPicRv ;
        private TextView mMoneyTv ;
        private TextView mCountTv ;
        private View mOptionsLay ;
        private TextView mSendTv ;
        private TextView mEditTv ;
        private View mDeleteIv ;

        public GoOrHolder(View itemView , final RvItemViewClickListener mClickListener) {
            super(itemView);

            mTypeTv = itemView.findViewById(R.id.adapter_my_good_order_type_tv) ;
            mTimeTv = itemView.findViewById(R.id.adapter_my_good_order_time_tv) ;
            mPicRv = itemView.findViewById(R.id.adapter_my_good_order_good_rv) ;
            mMoneyTv = itemView.findViewById(R.id.adapter_my_good_order_money_tv) ;
            mCountTv = itemView.findViewById(R.id.adapter_my_good_order_count_tv) ;
            mSendTv = itemView.findViewById(R.id.adapter_my_good_order_options_send_tv) ;
            mEditTv = itemView.findViewById(R.id.adapter_my_good_order_options_edit_tv) ;
            mDeleteIv = itemView.findViewById(R.id.adapter_my_good_order_options_delete_tv) ;
            mOptionsLay = itemView.findViewById(R.id.adapter_my_good_order_options_lay) ;

            mPicRv.setClickable(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,getAdapterPosition()) ;
                    }
                }
            });
            mSendTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,getAdapterPosition()) ;
                    }
                }
            });
            mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,getAdapterPosition()) ;
                    }
                }
            });
            mDeleteIv.setOnClickListener(new View.OnClickListener() {
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
