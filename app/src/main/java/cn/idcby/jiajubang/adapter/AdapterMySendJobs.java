package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.JobsCollectionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyReceiveResumeActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 我的招聘
 * Created on 2018/4/16.
 */

public class AdapterMySendJobs extends BaseAdapter {
    private Context context ;
    private List<JobsCollectionList> mDataList;
    private RvItemViewClickListener mClickListener ;
    private LayoutInflater inflater ;

    public AdapterMySendJobs(Context context, List<JobsCollectionList> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.inflater = LayoutInflater.from(context) ;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final int realPosition = position ;
        JobHolder holder ;
        if(null == view){
            view = inflater.inflate(R.layout.adapter_my_send_job_list ,viewGroup ,false) ;
            holder = new JobHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (JobHolder) view.getTag();
        }

        JobsCollectionList info = mDataList.get(position) ;
        if(info != null){
            final String jobId = info.getRecruitID() ;
            String title = info.getWorkPostName() ;
            String resumeCount = info.getResumeCount() ;
            String workTypeName = info.getWorkTypeName() ;

            holder.mCountTv.setText("" + resumeCount);
            holder.mWorkNameTv.setText(title);
            holder.mWorkTypeTv.setText(workTypeName) ;
            holder.mUpdownTv.setText(info.isEnableMark() ? "下架" : "上架") ;
            holder.mWorkStateTv.setText(info.isEnableMark() ? "展示中" : "已下架") ;
            holder.mWorkTipsTv.setVisibility(info.isCompanyTop() ? View.VISIBLE : View.GONE);

            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,realPosition);
                    }
                }
            });
            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1,realPosition);
                    }
                }
            });
            holder.mRefreshTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2,realPosition);
                    }
                }
            });
            holder.mUpTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3,realPosition);
                    }
                }
            });
            holder.mUpdownTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(5,realPosition);
                    }
                }
            });
            holder.mCountLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toLiIt = new Intent(context , MyReceiveResumeActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_JOB_ID ,jobId) ;
                    context.startActivity(toLiIt) ;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toJobDetailActivity(context , jobId);
                }
            });
        }

        return view;
    }

    private static class JobHolder {
        private View mCountLay;
        private TextView mCountTv;
        private TextView mWorkNameTv;
        private TextView mWorkStateTv;
        private TextView mWorkTipsTv;
        private TextView mWorkTypeTv;
        private TextView mSeeCountTv;
        private TextView mUpTv;
        private TextView mRefreshTv;
        private TextView mUpdownTv;
        private TextView mEditTv;
        private TextView mDeleteTv;

        public JobHolder(View itemView) {
            mCountLay = itemView.findViewById(R.id.adapter_my_send_job_count_lay) ;
            mCountTv = itemView.findViewById(R.id.adapter_my_send_job_receive_count_tv) ;
            mWorkNameTv = itemView.findViewById(R.id.adapter_my_send_job_title_tv);
            mWorkStateTv = itemView.findViewById(R.id.adapter_my_send_job_state_tv);
            mWorkTipsTv = itemView.findViewById(R.id.adapter_my_send_job_tips_tv);
            mWorkTypeTv = itemView.findViewById(R.id.adapter_my_send_job_type_tv);
            mSeeCountTv = itemView.findViewById(R.id.adapter_my_send_job_see_count_tv);
            mUpTv = itemView.findViewById(R.id.adapter_my_send_job_up_tv);
            mRefreshTv = itemView.findViewById(R.id.adapter_my_send_job_refresh_tv);
            mUpdownTv = itemView.findViewById(R.id.adapter_my_send_job_updown_tv);
            mEditTv = itemView.findViewById(R.id.adapter_my_send_job_edit_tv);
            mDeleteTv = itemView.findViewById(R.id.adapter_my_send_job_delete_tv);
        }
    }
}
