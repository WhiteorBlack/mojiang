package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.BangMoneyDetailsList;
import cn.idcby.jiajubang.R;

/**
 * 账单列表
 * Created on 2018/4/14.
 */

public class AdapterBangMoneyDtList extends BaseAdapter {
    private Context context ;
    private List<BangMoneyDetailsList> mDataList ;

    public AdapterBangMoneyDtList(Context context, List<BangMoneyDetailsList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
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
        MdHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_money_dt_list ,viewGroup ,false) ;
            holder = new MdHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (MdHolder) view.getTag();
        }

        BangMoneyDetailsList info = mDataList.get(i) ;
        if(info != null){
            holder.mDateTv.setText(info.getCreateDate());
            holder.mMoneyTv.setText(info.getOperationAmount());
            holder.mDescTv.setText(info.getOperationAppDescribe());
        }

        return view;
    }

    private static class MdHolder{
        private TextView mDateTv ;
        private TextView mMoneyTv ;
        private TextView mDescTv ;

        public MdHolder(View v) {
            this.mDateTv = v.findViewById(R.id.adapter_my_money_dt_list_data_tv) ;
            this.mMoneyTv = v.findViewById(R.id.adapter_my_money_dt_list_money_tv) ;
            this.mDescTv = v.findViewById(R.id.adapter_my_money_dt_list_desc_tv) ;
        }
    }
}
