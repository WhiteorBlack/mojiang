package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NeedsCategory;
import cn.idcby.jiajubang.R;

/**
 *  选择需求分类
 * Created on 2018/3/30.
 *
 * 2018-09-11 14:00:11
 * 暂时置灰 服务需求和安装需求
 */

public class AdapterChooseNeedCategory extends BaseAdapter {
    private Context context ;
    private List<NeedsCategory> mDataList ;

    public AdapterChooseNeedCategory(Context context, List<NeedsCategory> mDataList) {
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
        CrHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_need_category , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        NeedsCategory info = mDataList.get(i) ;
        if(info != null){
            String name = info.getCategoryTitle() ;

            holder.mNameTv.setText(name);

            if(name.contains("服务") || name.contains("安装")){
                holder.mNameTv.setTextColor(context.getResources().getColor(R.color.color_grey_b3));
            }else{
                holder.mNameTv.setTextColor(context.getResources().getColor(R.color.color_nomal_text));
            }
        }

        return view;
    }


    private static class CrHolder{
        private TextView mNameTv ;

        public CrHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_choose_need_category_name_tv) ;
        }
    }

}
