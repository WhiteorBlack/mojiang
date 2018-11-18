package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.idcby.commonlibrary.view.LineView;
import cn.idcby.jiajubang.Bean.HelperBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.HelperActivity;

/**
 * Created on 2018/5/2.
 */

public class AdapterHelperParent extends BaseAdapter {
    private Context context ;
    private List<HelperBean> mDataList ;

    public AdapterHelperParent(Context context, List<HelperBean> mDataList) {
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

        HelperBean h5Bean = mDataList.get(i) ;
        if(h5Bean != null){
            final String title = h5Bean.getCategoryTitle() ;
            final String cateId = h5Bean.getCategoryID() ;
            holder.mLineView.setTitleText(title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HelperActivity.launch(context,title,cateId) ;
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
