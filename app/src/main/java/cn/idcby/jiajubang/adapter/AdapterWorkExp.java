package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.WorkExperience;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/3/27.
 */

public class AdapterWorkExp extends BaseAdapter {
    private Context mContext ;
    private List<WorkExperience> mDataList ;

    public AdapterWorkExp(Context mContext, List<WorkExperience> mDataList) {
        this.mContext = mContext;
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
        WorkHolder holder ;

        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_work_exp,viewGroup ,false) ;
            holder = new WorkHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (WorkHolder) view.getTag();
        }

        WorkExperience experience = mDataList.get(i) ;
        if(experience != null){
            String startTime = experience.getBeginTime() ;
            String endTime = experience.getOverTime() ;
            String workType = experience.getPostName() ;
            String comName = experience.getCompanyName() ;
            String money = experience.getSalary() ;

            holder.mTimeTv.setText(startTime + "至" + endTime);
            holder.mComTv.setText(comName);
            holder.mTypeTv.setText(workType);
            holder.mMoneyTv.setText(money) ;
        }

        return view;
    }

    private static class WorkHolder{
        private TextView mTimeTv ;
        private TextView mMoneyTv ;
        private TextView mComTv ;
        private TextView mTypeTv ;

        public WorkHolder(View view) {
            mTimeTv = view.findViewById(R.id.adapter_work_exp_time_tv) ;
            mMoneyTv = view.findViewById(R.id.adapter_work_exp_money_tv) ;
            mComTv = view.findViewById(R.id.adapter_work_exp_com_tv) ;
            mTypeTv = view.findViewById(R.id.adapter_work_exp_type_tv) ;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false ;
    }
}
