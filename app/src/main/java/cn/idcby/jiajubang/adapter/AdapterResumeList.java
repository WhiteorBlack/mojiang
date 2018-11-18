package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/3/27.
 */

public class AdapterResumeList extends BaseAdapter {
    private Context mContext ;
    private List<ResumeList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterResumeList(Context mContext, List<ResumeList> mDataList ,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
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
        final int realPosition = i ;
        ReHolder holder ;

        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_resume_list , viewGroup , false) ;
            holder = new ReHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (ReHolder) view.getTag();
        }

        ResumeList info = mDataList.get(i) ;
        if(info != null){
            String imgUrl = info.getUserHeadIcon() ;
            String name = info.getCreateUserName() ;
            String time = info.getReleaseTime() ;
            String supportCount = info.getLikeNumber()+"" ;
            String sex = info.getSex() ;
            String age = info.getAge() ;
            String year = info.getWorkYear() ;
            String address = info.getAddress() ;
            String workName = info.getWorkPostName() ;
            String money = info.getApplySalary() ;

            String content = sex ;
            if(age != null && !"".equals(age)){
                content += (" / " + age) ;
            }
            if(year != null && !"".equals(year)){
                content += (" / " + year) ;
            }
            if(address != null && !"".equals(address)){
                content += (" / " + address) ;
            }

            holder.mNameTv.setText(name);
            holder.mTimeTv.setText(time);
            holder.mContentTv.setText(content);
            holder.mWorkNameTv.setText(workName);
            if(!"".equals(money)){
                holder.mWorkMoneyTv.setVisibility(View.VISIBLE);
                holder.mWorkMoneyTv.setText("¥" + money);
            }else{
                holder.mWorkMoneyTv.setVisibility(View.INVISIBLE);
            }
            holder.mSupportCount.setText(supportCount) ;

            GlideUtils.loaderUser(imgUrl , holder.mIv) ;

            boolean isTypeAll = info.isWorkAll() ;
            holder.typeTv.setText(info.getWorkTypeName()) ;
            holder.typeTv.setTextColor(mContext.getResources().getColor(isTypeAll
                    ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
            holder.typeTv.setBackgroundDrawable(mContext.getResources().getDrawable(isTypeAll
                    ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
        }

        return view;
    }


    private static class ReHolder{
        private ImageView mIv ;
        private TextView mNameTv;
        private TextView mTimeTv ;
        private TextView typeTv ;
        private TextView mSupportCount ;
        private TextView mContentTv ;
        private TextView mWorkNameTv ;
        private TextView mWorkMoneyTv ;

        public ReHolder(View view) {
            mIv = view.findViewById(R.id.adapter_resume_list_iv) ;
            mNameTv = view.findViewById(R.id.adapter_resume_list_title_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_resume_list_time_tv) ;
            typeTv = view.findViewById(R.id.adapter_resume_list_type_tv) ;
            mSupportCount = view.findViewById(R.id.adapter_resume_list_support_count_tv) ;
            mContentTv = view.findViewById(R.id.adapter_resume_list_content_tv) ;
            mWorkNameTv = view.findViewById(R.id.adapter_resume_list_job_name_tv) ;
            mWorkMoneyTv = view.findViewById(R.id.adapter_resume_list_job_money_tv) ;
        }
    }

}
