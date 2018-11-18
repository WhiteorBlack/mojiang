package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.QuestionCategory;
import cn.idcby.jiajubang.R;

/**
 *  选择问题分类
 * Created on 2018/3/30.
 */

public class AdapterChooseQuestionCategory extends BaseAdapter {
    private Context context ;
    private List<QuestionCategory> mDataList ;

    public AdapterChooseQuestionCategory(Context context, List<QuestionCategory> mDataList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_category_nomal , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        QuestionCategory info = mDataList.get(i) ;
        if(info != null){
            holder.mNameTv.setText(info.getCategoryTitle());
        }

        return view;
    }


    private static class CrHolder{
        private TextView mNameTv ;

        public CrHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_choose_category_title_tv) ;
        }
    }

}
