package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/5.
 */
public abstract class DefaultBaseAdapter<T> extends BaseAdapter {


    public List<T> datas;
    public Context context;

    public DefaultBaseAdapter(List<T> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup viewGroup);


    public void deleteItem(int position) {

        datas.remove(position);
        notifyDataSetChanged();
    }
}
