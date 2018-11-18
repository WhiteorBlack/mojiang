package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ServerOrderList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyServerOrderDetailsActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 服务订单列表
 * Created on 2018/4/12.
 */

public class AdapterServerOrder extends BaseAdapter {
    private Context context ;
    private List<ServerOrderList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public static final int TYPE_OP_START = 1 ;
    public static final int TYPE_OP_EDIT = 2 ;

    public AdapterServerOrder(Context context, List<ServerOrderList> mDataList, RvItemViewClickListener mClickListener) {
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

        SerOrHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_server_order , viewGroup ,false) ;
            holder  = new SerOrHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (SerOrHolder) view.getTag();
        }

        ServerOrderList info = mDataList.get(i) ;
        if(info != null){
            final String orderId = info.getServiceOrderId() ;
            String name = info.getServiceUserName() ;
            String phoneOne = info.getServiceUserAccount() ;
            String statusName = info.getOrderStatusName() ;
            int orderStatus = info.getOrderStatus() ;
            String desc = info.getServiceintroduce() ;
            String address = info.getServiceDetailAddress() ;
            String time = info.getCreateDate() ;
            String contact = info.getContacts() ;
            String phone = info.getContactsPhone() ;
            String money = info.getServiceAmount() ;

            holder.mNameTv.setText("服务师傅：" + name);
            holder.mPhoneOneTv.setText(phoneOne);
            holder.mStatusTv.setText(statusName);
            holder.mDescTv.setText(desc);
            holder.mAddressTv.setText(address);
            holder.mTimeTv.setText(time);
            holder.mCusNameTv.setText(contact);
            holder.mCusPhoneTv.setText(phone);
            holder.mMoneyTv.setText(money+"元");

            holder.mOpStartTv.setVisibility(View.GONE);
            holder.mOpEditTv.setVisibility(View.GONE);

            if(1 == orderStatus){
                holder.mOpEditTv.setVisibility(View.VISIBLE);
            }else if(2 == orderStatus){
                holder.mOpStartTv.setVisibility(View.VISIBLE);
            }

            holder.mOpStartTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_START ,realPosition) ;
                    }
                }
            });
            holder.mOpEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(TYPE_OP_EDIT ,realPosition) ;
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent(context , MyServerOrderDetailsActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_ORDER_ID ,orderId) ;
                    context.startActivity(toDtIt) ;
                }
            });
        }

        return view;
    }

    private static class SerOrHolder{
        private TextView mNameTv ;
        private TextView mPhoneOneTv ;
        private TextView mStatusTv ;
        private TextView mDescTv ;
        private TextView mAddressTv ;
        private TextView mTimeTv ;
        private TextView mCusNameTv ;
        private TextView mCusPhoneTv;
        private TextView mMoneyTv ;
        private TextView mOpStartTv ;
        private TextView mOpEditTv ;

        public SerOrHolder(View view) {
            this.mNameTv = view.findViewById(R.id.adapter_server_order_name_tv) ;
            this.mPhoneOneTv = view.findViewById(R.id.adapter_server_order_one_phone_tv) ;
            this.mStatusTv = view.findViewById(R.id.adapter_server_order_status_tv) ;
            this.mDescTv = view.findViewById(R.id.adapter_server_order_desc_tv) ;
            this.mAddressTv = view.findViewById(R.id.adapter_server_order_address_tv) ;
            this.mTimeTv = view.findViewById(R.id.adapter_server_order_time_tv) ;
            this.mCusNameTv = view.findViewById(R.id.adapter_server_order_cus_name_tv) ;
            this.mCusPhoneTv = view.findViewById(R.id.adapter_server_order_cus_phone_tv) ;
            this.mMoneyTv = view.findViewById(R.id.adapter_server_order_money_tv) ;
            this.mOpStartTv = view.findViewById(R.id.adapter_server_order_op_start_server) ;
            this.mOpEditTv = view.findViewById(R.id.adapter_server_order_op_edit_server) ;
        }
    }
}
