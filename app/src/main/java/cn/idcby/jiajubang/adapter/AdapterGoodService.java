package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.GoodDetails;
import cn.idcby.jiajubang.R;

/**
 * 商品服务
 * Created on 2018/8/17.
 */

public class AdapterGoodService extends BaseAdapter {
    private Context mContext ;
    private List<GoodDetails.GoodService> mDataList ;

    public AdapterGoodService(Context mContext, List<GoodDetails.GoodService> mDataList) {
        this.mContext = mContext;
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
        GsHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_good_details_service,viewGroup ,false) ;
            holder = new GsHolder(view) ;

            view.setTag(holder) ;
        }else{
            holder = (GsHolder) view.getTag();
        }

        GoodDetails.GoodService info = mDataList.get(i) ;
        if(info != null){
            String title = info.getServiceTitle() ;
            String desc = info.getServiceDescribe() ;

            holder.mTitleTv.setText(title);
            holder.mDescTv.setText(desc);
            holder.mDescTv.setVisibility("".equals(desc) ? View.GONE : View.VISIBLE) ;
        }

        return view;
    }


    private static class GsHolder{
        private TextView mTitleTv ;
        private TextView mDescTv ;

        public GsHolder(View view) {
            mTitleTv = view.findViewById(R.id.adapter_good_details_service_title_tv) ;
            mDescTv = view.findViewById(R.id.adapter_good_details_service_desc_tv) ;
        }
    }
}
