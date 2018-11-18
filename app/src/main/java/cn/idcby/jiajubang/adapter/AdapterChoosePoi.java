package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/6/6.
 */

public class AdapterChoosePoi extends BaseAdapter {
    private Context context ;
    private List<PoiInfo> mDataList ;

    public AdapterChoosePoi(Context context, List<PoiInfo> mDataList) {
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
        PoiHolder holder ;

        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_map_location_poi ,viewGroup ,false) ;
            holder = new PoiHolder(view) ;
            view.setTag(holder );
        }else{
            holder = (PoiHolder) view.getTag();
        }

        PoiInfo info = mDataList.get(i) ;
        if(info != null){
            String name = info.name ;
            String address = info.address ;

            holder.mNameTv.setText(StringUtils.convertNull(name));
            holder.mAddressTv.setText(StringUtils.convertNull(address));

        }

        return view;
    }

    private static  class PoiHolder{
        private TextView mNameTv ;
        private TextView mAddressTv ;

        public PoiHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_choose_map_location_poi_name_tv) ;
            mAddressTv = view.findViewById(R.id.adapter_choose_map_location_poi_address_tv) ;
        }
    }
}
