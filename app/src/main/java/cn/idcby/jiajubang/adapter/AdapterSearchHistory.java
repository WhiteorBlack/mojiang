package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.SearchHistory;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/4/18.
 */

public class AdapterSearchHistory extends BaseAdapter {
    private Context context ;
    private List<SearchHistory> mList ;

    public AdapterSearchHistory(Context context, List<SearchHistory> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return null == mList ? 0 : mList.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HisHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_search_history ,viewGroup ,false) ;

            holder = new HisHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (HisHolder) view.getTag();
        }

        SearchHistory history = mList.get(i) ;
        if(history != null){
            holder.mKeyTv.setText(history.getKeyword()) ;
        }

        return view;
    }

    private static class HisHolder{
        private TextView mKeyTv ;

        public HisHolder(View v) {
            mKeyTv = v.findViewById(R.id.adapter_search_history_tv) ;
        }
    }

}
