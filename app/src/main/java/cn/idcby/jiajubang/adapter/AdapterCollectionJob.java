package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.JobsCollectionList;
import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.FlowLayout;

/**
 *  我的收藏--招聘
 *  2018-03-28
 */

public class AdapterCollectionJob extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private List<JobsCollectionList> mDataList;
    private LayoutInflater inflater ;
    private int flTvPadding ;

    public AdapterCollectionJob(Context context , List<JobsCollectionList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.inflater = LayoutInflater.from(context) ;
        this.flTvPadding = ResourceUtils.dip2px(context , 2) ;
    }

    @Override
    public int getItemCount() {
        return  null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyColHolder(inflater.inflate(R.layout.adapter_collection_job_list
                , parent , false)) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyColHolder){
            MyColHolder mHolder = (MyColHolder) holder;
            JobsCollectionList info = mDataList.get(position) ;
            if(info != null){
                final String jobId = info.getRecruitID() ;
                String title = info.getWorkPostName() ;
                String comName = info.getCompanyName() ;
                String time = info.getReleaseTime() ;
                String workYear = StringUtils.convertWorkYearExp(info.getWorkYears()) ;
                String salary = info.getSalary() ;
                String location = info.getAddress() ;
                String imgUrl = info.getCompanyLogoImage() ;

                String content = "" ;
                if(!"".equals(location)){
                    content += location ;
                }
                if(!"".equals(workYear)){
                    if(!"".equals(location)){
                        content += "/" ;
                    }
                    content += workYear ;
                }
                if(salary != null && !"".equals(salary)){
                    content += ("/¥" + salary) ;
                }

                mHolder.mWorkNameTv.setText(title);
                mHolder.mTimeTv.setText(time);
                mHolder.mContentTv.setText(content);
                mHolder.mComNameTv.setText(comName);
                GlideUtils.loader(MyApplication.getInstance() , imgUrl , mHolder.mIv) ;

                boolean isTypeAll = info.isWorkAll() ;
                mHolder.typeTv.setText(info.getWorkTypeName()) ;
                mHolder.typeTv.setTextColor(context.getResources().getColor(isTypeAll
                        ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
                mHolder.typeTv.setBackgroundDrawable(context.getResources().getDrawable(isTypeAll
                        ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

                mHolder.mFuliLay.removeAllViews() ;
                List<WelfareList> welfareLists = info.getWelfareList() ;
                if(welfareLists != null && welfareLists.size() > 0){
                    for(WelfareList welfare : welfareLists){
                        if(welfare != null){
                            TextView tv = new TextView(context) ;
                            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(flTvPadding ,flTvPadding,flTvPadding,flTvPadding);
                            tv.setBackgroundColor(Color.parseColor(welfare.getWelfareColorValue())) ;
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP , 8);
                            tv.setTextColor(context.getResources().getColor(R.color.white));
                            tv.setText(welfare.getWelfareText()) ;

                            mHolder.mFuliLay.addView(tv) ;
                        }
                    }
                }

                mHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toJobDetailActivity(context , jobId);
                    }
                });
            }
        }
    }

    private static class MyColHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mMainLay ;
        private ImageView mIv;
        private TextView mWorkNameTv;
        private TextView typeTv;
        private TextView mContentTv ;
        private FlowLayout mFuliLay;
        private TextView mComNameTv ;
        private TextView mTimeTv ;

        public MyColHolder(View itemView) {
            super(itemView);

            mMainLay =  itemView.findViewById(R.id.adapter_collection_job_main_lay);
            mIv = itemView.findViewById(R.id.adapter_collection_job_com_iv);
            typeTv = itemView.findViewById(R.id.adapter_collection_job_type_tv);
            mWorkNameTv = itemView.findViewById(R.id.adapter_collection_job_title_tv);
            mContentTv = itemView.findViewById(R.id.adapter_collection_job_content_tv);
            mComNameTv = itemView.findViewById(R.id.adapter_collection_job_com_tv);
            mTimeTv = itemView.findViewById(R.id.adapter_collection_job_time_tv);
            mFuliLay = itemView.findViewById(R.id.adapter_collection_job_fuli_lay);
        }
    }

}
