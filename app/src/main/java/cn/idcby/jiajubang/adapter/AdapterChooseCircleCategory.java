package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.CircleCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 *  选择圈子分类
 * Created on 2018/3/30.
 */

public class AdapterChooseCircleCategory extends BaseAdapter {
    private Context context ;
    private List<CircleCategory> mDataList ;

    public AdapterChooseCircleCategory(Context context, List<CircleCategory> mDataList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_circle_category , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        CircleCategory info = mDataList.get(i) ;
        if(info != null){
            String name = info.getCategoryTitle() ;
            holder.mNameTv.setText(StringUtils.convertNull(name));
        }

        return view;
    }


    private static class CrHolder{
        private TextView mNameTv ;

        public CrHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_choose_circle_category_name_tv) ;
        }
    }

}
