package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.ViewUtil;

/**
 * Created on 2018/4/11.
 */

public class AdapterChooseMyAddress extends BaseAdapter {
    private Context mContext ;
    private List<ReceiveAddress> mData ;
    private RvItemViewClickListener mClickListener ;

    public AdapterChooseMyAddress(Context mContext, List<ReceiveAddress> mData
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
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_choose_my_address , viewGroup , false) ;
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

            String realAddress = province + city + area + address ;

            holder.mNameTv.setText(name);
            holder.mPhoneTv.setText(phone);
            if(isDef){
                realAddress = ("[默认地址]" + realAddress) ;
                ViewUtil.convertDiffrentColorToTextView(holder.mAddressTv ,0 ,6 ,realAddress
                        ,mContext.getResources().getColor(R.color.red));
            }else{
                holder.mAddressTv.setText(realAddress);
                holder.mAddressTv.setTextColor(mContext.getResources().getColor(R.color.color_grey_5c));
            }

            holder.mMainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 , realPosition) ;
                    }
                }
            });
        }

        return view;
    }

    static class MyAdrHolder{
        private View mMainView ;
        private TextView mNameTv ;
        private TextView mPhoneTv ;
        private TextView mAddressTv ;

        public MyAdrHolder(View view) {
            mMainView = view.findViewById(R.id.adapter_choose_my_address_main_lay) ;
            mNameTv = view.findViewById(R.id.adapter_choose_my_address_name_tv) ;
            mPhoneTv = view.findViewById(R.id.adapter_choose_my_address_phone_tv) ;
            mAddressTv = view.findViewById(R.id.adapter_choose_my_address_address_tv) ;
        }
    }
}
