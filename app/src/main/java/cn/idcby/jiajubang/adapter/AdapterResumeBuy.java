package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ResumeBuyList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * 可购买简历列表
 * Created on 2018/4/12.
 */

public class AdapterResumeBuy extends BaseAdapter {
    private Context context ;
    private List<ResumeBuyList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterResumeBuy(Context context, List<ResumeBuyList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
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
        final int realPosi = i ;
        ByHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_resume_buy_list ,viewGroup , false) ;
            holder = new ByHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (ByHolder) view.getTag();
        }

        ResumeBuyList info = mDataList.get(i) ;
        if(info != null){
            String number = info.getNumber() ;
            String money = info.getAmount() ;
            String time = info.getWhenLong() ;

            holder.mTitleTv.setText(number + "份简历");
            holder.mTimeTv.setText(" / " + time + "天");
            holder.mMoneyTv.setText("¥" + money);
            holder.mOptionTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosi);
                    }
                }
            });
        }

        return view;
    }

    private static class ByHolder{
        private TextView mTitleTv ;
        private TextView mMoneyTv ;
        private TextView mTimeTv ;
        private TextView mOptionTv ;

        public ByHolder(View v) {
            mTitleTv = v.findViewById(R.id.adapter_resume_buy_list_title_tv) ;
            mTimeTv = v.findViewById(R.id.adapter_resume_buy_list_time_tv) ;
            mMoneyTv = v.findViewById(R.id.adapter_resume_buy_list_money_tv) ;
            mOptionTv = v.findViewById(R.id.adapter_resume_buy_list_buy_tv) ;
        }
    }

}
