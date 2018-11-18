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
 * 商品参数
 * Created on 2018/8/17.
 */

public class AdapterGoodParam extends BaseAdapter {
    private Context mContext ;
    private List<GoodDetails.GoodParam> mDataList ;

    public AdapterGoodParam(Context mContext, List<GoodDetails.GoodParam> mDataList) {
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
        GpHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_good_details_param,viewGroup ,false) ;
            holder = new GpHolder(view) ;

            view.setTag(holder) ;
        }else{
            holder = (GpHolder) view.getTag();
        }

        GoodDetails.GoodParam info = mDataList.get(i) ;
        if(info != null){
            String title = info.getParentParaTitle() ;
            String desc = info.getParaTitle() ;

            holder.mTitleTv.setText(title);
            holder.mDescTv.setText(desc);
        }

        return view;
    }


    private static class GpHolder {
        private TextView mTitleTv ;
        private TextView mDescTv ;

        public GpHolder(View view) {
            mTitleTv = view.findViewById(R.id.adapter_good_details_param_title_tv) ;
            mDescTv = view.findViewById(R.id.adapter_good_details_param_desc_tv) ;
        }
    }
}
