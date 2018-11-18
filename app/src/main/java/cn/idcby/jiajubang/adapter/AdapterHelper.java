package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.idcby.commonlibrary.view.LineView;
import cn.idcby.jiajubang.Bean.NomalH5Bean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/5/2.
 */

public class AdapterHelper extends BaseAdapter {
    private Context context ;
    private List<NomalH5Bean> mDataList ;

    public AdapterHelper(Context context, List<NomalH5Bean> mDataList) {
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
        HpHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_helper_item ,viewGroup ,false) ;
            holder = new HpHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (HpHolder) view.getTag();
        }

        NomalH5Bean h5Bean = mDataList.get(i) ;
        if(h5Bean != null){
            final String title = h5Bean.getTitleText() ;
            final String h5Url = h5Bean.getH5Url() ;
            holder.mLineView.setTitleText(title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toShowWebActivity(context ,title ,h5Url) ;
                }
            });
        }

        return view;
    }

    private static class HpHolder {
        private LineView mLineView ;

        public HpHolder(View view) {
            this.mLineView = view.findViewById(R.id.adapter_helper_item_title_tv);
        }
    }
}
