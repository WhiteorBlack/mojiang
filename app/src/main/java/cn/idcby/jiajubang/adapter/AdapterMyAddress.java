package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * Created on 2018/3/24.
 */

public class AdapterMyAddress extends BaseAdapter {
    private Context mContext ;
    private List<ReceiveAddress> mData ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyAddress(Context mContext, List<ReceiveAddress> mData
            , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int realPosition = i ;
        MyAdrHolder holder ;

        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_address , viewGroup , false) ;
            holder = new MyAdrHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (MyAdrHolder) view.getTag();
        }

        ReceiveAddress info = mData.get(i) ;
        if(info != null){
            String name = info.getContacts() ;
            String phone = info.getAccount() ;
            String province = info.getProvinceName() ;
            String city = info.getCityName() ;
            String area = info.getCountyName() ;
            String address = info.getAddress() ;
            boolean isDef = info.isDefault() ;

            holder.mNameTv.setText(name);
            holder.mPhoneTv.setText(phone);
            holder.mAddressTv.setText(province + city + area + address);
//            holder.mIsDefaultTv.setText(isDef ? "默认地址" : "设为默认");
            holder.mIsDefaultIv.setImageDrawable(mContext.getResources().getDrawable(isDef
                    ? R.mipmap.ic_check_checked_blue
                    : R.mipmap.ic_check_nomal));

            holder.mIsDefaultLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
            holder.mEditLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition);
                    }
                }
            });
            holder.mDeleteLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,realPosition);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,realPosition);
                    }
                }
            });

        }

        return view;
    }

    static class MyAdrHolder{
        private TextView mNameTv ;
        private TextView mPhoneTv ;
        private TextView mAddressTv ;
        private TextView mIsDefaultTv ;
        private ImageView mIsDefaultIv ;
        private LinearLayout mIsDefaultLay ;
        private View mEditLay ;
        private View mDeleteLay ;

        public MyAdrHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_my_address_name_tv) ;
            mPhoneTv = view.findViewById(R.id.adapter_my_address_phone_tv) ;
            mAddressTv = view.findViewById(R.id.adapter_my_address_address_tv) ;
            mIsDefaultTv = view.findViewById(R.id.adapter_my_address_default_tv) ;
            mIsDefaultIv = view.findViewById(R.id.adapter_my_address_default_iv) ;
            mIsDefaultLay = view.findViewById(R.id.adapter_my_address_default_lay) ;
            mEditLay = view.findViewById(R.id.adapter_my_address_edit_lay) ;
            mDeleteLay = view.findViewById(R.id.adapter_my_address_delete_lay) ;
        }
    }


}
