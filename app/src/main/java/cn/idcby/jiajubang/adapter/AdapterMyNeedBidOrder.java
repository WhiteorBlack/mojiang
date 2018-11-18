package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.MyNeedsOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/22.
 */

public class AdapterMyNeedBidOrder extends BaseAdapter {
    private Context context ;
    private List<MyNeedsOrderList> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private boolean mIsSelf = false;

    public AdapterMyNeedBidOrder(Context context, List<MyNeedsOrderList> mDataList
            , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    public void setIsNeedOp(boolean isSelf){
        this.mIsSelf = isSelf ;
        notifyDataSetChanged() ;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        MyNboHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_need_bid_order ,viewGroup ,false) ;
            holder = new MyNboHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (MyNboHolder) view.getTag();
        }

        final int realPosition = position ;
        MyNeedsOrderList info = mDataList.get(position) ;
        if(info != null){
            String name = info.getCreateUserName() ;
            String time = info.getCreateDate() ;
            String money = info.getOrderAmount() ;
            String state = info.getOrderStatusName() ;
            String imgUrl = info.getCreateHeadIcon() ;

            if(!"".equals(money)){
                money = money + "元" ;
            }

            holder.mNameTv.setText(name);
            holder.mTimeTv.setText(StringUtils.convertDateToDay(time)) ;
            holder.mMoneyTv.setText(money) ;
            holder.mStateTv.setText(state);
            GlideUtils.loaderUser(imgUrl,holder.mIconIv) ;

            holder.mOpLay.setVisibility(View.GONE) ;
            holder.mOpCancelTv.setVisibility(View.GONE);
            holder.mOpFinishTv.setVisibility(View.GONE);
            holder.mOpPayTv.setVisibility(View.GONE);

            if(mIsSelf){
                int orderStatus = info.getOrderStatus() ;
                if(1 == orderStatus){//待付款
                    holder.mOpLay.setVisibility(View.VISIBLE) ;
                    holder.mOpPayTv.setVisibility(View.VISIBLE);
                }else if(2 == orderStatus){//待完成
                    holder.mOpLay.setVisibility(View.VISIBLE) ;
                    holder.mOpCancelTv.setVisibility(View.VISIBLE);
                    holder.mOpFinishTv.setVisibility(View.VISIBLE);
                }
            }

            holder.mOpDetaisTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition) ;
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition) ;
                    }
                }
            });
            holder.mOpPayTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,realPosition) ;
                    }
                }
            });
            holder.mOpCancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,realPosition) ;
                    }
                }
            });
            holder.mOpFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(4 ,realPosition) ;
                    }
                }
            });
        }

        return view;
    }

    static class MyNboHolder{
        private ImageView mIconIv ;
        private TextView mNameTv ;
        private TextView mTimeTv ;
        private TextView mMoneyTv ;
        private TextView mStateTv ;
        private TextView mOpDetaisTv ;
        private TextView mOpPayTv ;
        private TextView mOpFinishTv ;
        private TextView mOpCancelTv ;
        private View mOpLay ;

        public MyNboHolder(View view){
            this.mIconIv = view.findViewById(R.id.adapter_my_need_bid_order_iv) ;
            this.mNameTv = view.findViewById(R.id.adapter_my_need_bid_order_name_tv) ;
            this.mTimeTv = view.findViewById(R.id.adapter_my_need_bid_order_time_tv) ;
            this.mMoneyTv = view.findViewById(R.id.adapter_my_need_bid_order_money_tv) ;
            this.mStateTv = view.findViewById(R.id.adapter_my_need_bid_order_state_tv) ;
            this.mOpDetaisTv = view.findViewById(R.id.adapter_my_need_bid_order_dt_tv) ;
            this.mOpPayTv = view.findViewById(R.id.adapter_my_need_bid_order_pay_tv) ;
            this.mOpFinishTv = view.findViewById(R.id.adapter_my_need_bid_order_finish_tv) ;
            this.mOpCancelTv = view.findViewById(R.id.adapter_my_need_bid_order_cancel_tv) ;
            this.mOpLay = view.findViewById(R.id.adapter_my_need_bid_order_option_lay) ;
        }
    }

}
