package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/4/13.
 */

public class AdapterSortProvinceCity extends BaseAdapter {
    private Context context ;
    private List<Address> mDataList ;

    public AdapterSortProvinceCity(List<Address> datas, Context context) {
        this.context = context ;
        this.mDataList = datas ;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_sort_province_city, viewGroup ,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Address address = mDataList.get(position);
        if(address != null){
            holder.tvDesc.setText(address.AreaName);
        }

        return convertView;
    }

    public class ViewHolder {
        private View view;
        private TextView tvDesc;

        public ViewHolder(View view) {
            this.view = view;
            findView(view);
        }

        private void findView(View view) {
            tvDesc = view.findViewById(R.id.adapter_sort_province_city_name_tv);
        }

    }
}
