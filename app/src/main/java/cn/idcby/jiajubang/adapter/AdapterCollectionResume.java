package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ResumeDetailActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 *  我的收藏--简历
 *  2018-03-28
 */

public class AdapterCollectionResume extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private List<ResumeList> mDataList;
    private LayoutInflater inflater ;

    public AdapterCollectionResume(Context context , List<ResumeList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.inflater = LayoutInflater.from(context) ;
    }

    @Override
    public int getItemCount() {
        return  null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyColHolder(inflater.inflate(R.layout.adapter_collection_resume_list
                , parent , false)) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyColHolder){
            MyColHolder mHolder = (MyColHolder) holder;
            ResumeList info = mDataList.get(position) ;
            if(info != null){
                final String resumeId = info.getResumeId() ;
                String userName = info.getName() ;
                String userSex = info.getSex() ;
                String userAge = info.getAge() ;
                String userEdu = info.getEducation() ;

                String time = info.getReleaseTime() ;
                String workName = info.getWorkPostName() ;
                String salary = info.getApplySalary() ;
                String imgUrl = info.getUserHeadIcon() ;

                String content = "" ;
                if(!"".equals(userSex)){
                    content += userSex ;
                }
                if(!"".equals(userAge)){
                    content += ("/" + userAge) ;
                }
                if(userEdu != null && !"".equals(userEdu)){
                    content += ("/" + userEdu) ;
                }

                mHolder.mUserNameTv.setText(userName);
                mHolder.mTimeTv.setText(time);
                mHolder.mContentTv.setText(content);
                mHolder.mWorkNameTv.setText("求聘职位：" + workName);
                mHolder.mMoneyTv.setText(salary);
                mHolder.mMoneyTipsTv.setVisibility(info.isFace() ? View.INVISIBLE : View.VISIBLE) ;
                GlideUtils.loaderUser(imgUrl , mHolder.mIv) ;

                boolean isTypeAll = info.isWorkAll() ;
                mHolder.typeTv.setText(info.getWorkTypeName()) ;
                mHolder.typeTv.setTextColor(context.getResources().getColor(isTypeAll
                        ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
                mHolder.typeTv.setBackgroundDrawable(context.getResources().getDrawable(isTypeAll
                        ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

                mHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toDtIt = new Intent(context , ResumeDetailActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,resumeId) ;
                        context.startActivity(toDtIt) ;
                    }
                });
            }
        }
    }

    private static class MyColHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mMainLay ;
        private ImageView mIv;
        private TextView mUserNameTv;
        private TextView typeTv ;
        private TextView mTimeTv ;
        private TextView mContentTv ;
        private TextView mMoneyTv ;
        private TextView mMoneyTipsTv ;
        private TextView mWorkNameTv;

        public MyColHolder(View itemView) {
            super(itemView);

            mMainLay =  itemView.findViewById(R.id.adapter_collection_resume_main_lay);
            mIv = itemView.findViewById(R.id.adapter_collection_resume_iv);
            typeTv = itemView.findViewById(R.id.adapter_collection_resume_list_type_tv);
            mUserNameTv = itemView.findViewById(R.id.adapter_collection_resume_title_tv);
            mContentTv = itemView.findViewById(R.id.adapter_collection_resume_content_tv);
            mWorkNameTv = itemView.findViewById(R.id.adapter_collection_resume_job_name_tv);
            mMoneyTv = itemView.findViewById(R.id.adapter_collection_resume_job_money_tv);
            mMoneyTipsTv = itemView.findViewById(R.id.adapter_collection_resume_job_money_tips_tv);
            mTimeTv = itemView.findViewById(R.id.adapter_collection_resume_time_tv);
        }
    }

}
