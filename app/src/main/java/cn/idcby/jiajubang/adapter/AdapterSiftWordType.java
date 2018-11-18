package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;

/**
 *
 * Created on 2018/9/12.
 */

public class AdapterSiftWordType extends BaseAdapter {
    private Context mContext ;
    private List<WordType> mDataList ;

    public AdapterSiftWordType(Context mContext, List<WordType> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size();
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
        WdHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_sift_word_type_item ,viewGroup ,false) ;
            holder = new WdHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (WdHolder) view.getTag();
        }

        final WordType info = mDataList.get(i) ;
        if(info != null){
            final boolean isChecked = info.isSelected() ;
            holder.mNameTv.setText(info.getItemName());
            holder.mNameTv.setTextColor(mContext.getResources().getColor(isChecked
                    ? R.color.color_good_param_theme : R.color.color_nomal_text));
            holder.mNameTv.setBackgroundDrawable(mContext.getResources().getDrawable(isChecked
                    ? R.drawable.bg_good_param_checked : R.drawable.bg_good_param_nomal));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isChecked){
                        for(WordType info : mDataList){
                            if(info.isSelected()){
                                info.setSelected(false);
                                break;
                            }
                        }
                        info.setSelected(true);
                        notifyDataSetChanged() ;
                    }else{
                        info.setSelected(false);
                        notifyDataSetChanged() ;
                    }
                }
            });
        }

        return view;
    }

    private static class WdHolder{
        private TextView mNameTv ;

        public WdHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_sift_word_type_name_tv) ;
        }
    }

}
