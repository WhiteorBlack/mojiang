package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.R;

/**
 * 公司福利
 * Created on 2018/4/2.
 */

public class AdapterWelfareList extends BaseAdapter {
    private Context context ;
    private List<WelfareList> mDataList ;

    public AdapterWelfareList(Context context, List<WelfareList> mDataList) {
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
        final int realPosition = i ;

        WlHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_welfare , viewGroup ,false) ;
            holder = new WlHolder() ;
            holder.mNameTv = view.findViewById(R.id.adapter_choose_welfare_name_tv) ;
            holder.mChooseIv = view.findViewById(R.id.adapter_choose_welfare_iv) ;

            view.setTag(holder);
        }else{
            holder = (WlHolder) view.getTag();
        }

        WelfareList info = mDataList.get(i) ;
        if(info != null){
            String name = info.getWelfareText() ;
            final boolean isSelected = info.isSelected ;

            holder.mNameTv.setText(name);
            holder.mChooseIv.setImageDrawable(context.getResources().getDrawable(isSelected
                    ? R.mipmap.ic_check_checked_blue
                    : R.mipmap.ic_check_nomal));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isSelected){
                        mDataList.get(realPosition).isSelected = false ;
                    }else{
                        mDataList.get(realPosition).isSelected = true ;
                    }
                    notifyDataSetChanged() ;
                }
            });
        }

        return view;
    }

    private static class WlHolder{
        private TextView mNameTv ;
        private ImageView mChooseIv ;
    }
}
