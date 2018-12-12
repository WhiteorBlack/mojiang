package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.JobsList;
import cn.idcby.jiajubang.Bean.WelfareList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.FlowLayout;

/**
 * Created on 2018/2/28.
 */

public class AdapterNearJobList extends BaseAdapter {
    private Context mContext;
    private List<JobsList> mDataList;
    private RvItemViewClickListener mClickListener;
    private int flTvPadding;

    public AdapterNearJobList(Context mContext, List<JobsList> mDataList
            , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.flTvPadding = ResourceUtils.dip2px(mContext, 2);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int realPosition = position;

        JobHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_item_for_near_job, viewGroup, false);
            holder = new JobHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (JobHolder) convertView.getTag();
        }

        JobsList info = mDataList.get(position);
        if (info != null) {
            String title = info.getWorkPostName();
            String time = info.getReleaseTime();
            String workYear = StringUtils.convertWorkYearExp(info.getWorkYears());
            String salary = info.getSalary();
            String location = info.getAddress();
            String imgUrl = info.getCompanyLogoImage();

            String content = "";
            if (!"".equals(workYear)) {
                content += workYear;
            }

            if (!TextUtils.isEmpty(info.getEducation())){
                content+=" | "+info.getEducation();
            }
//            if (salary != null && !"".equals(salary)) {
//                content += ("/¥" + salary);
//            }
            holder.tvAuthor.setText(info.getAuthentication()==1?"公司已认证":"公司未认证");
            holder.tvName.setText(info.getCompanyName());
            holder.titleTv.setText(title);
            if (TextUtils.isEmpty(info.MinAmount) || TextUtils.isEmpty(info.MaxAmount)) {
                holder.timeTv.setText("面议");
            } else {
                holder.timeTv.setText(info.MinAmount + "-" + info.MaxAmount);
            }
            holder.contentTv.setText(content);
            holder.locationTv.setText(StringUtils.getDistance(info.getDistance()));
            GlideUtils.loader(MyApplication.getInstance(), imgUrl, holder.companyIv);

            boolean isTypeAll = info.isWorkAll();
            holder.typeTv.setText(info.getWorkTypeName());
//            holder.typeTv.setTextColor(mContext.getResources().getColor(isTypeAll
//                    ? R.color.job_resume_type_all_color : R.color.job_resume_type_half_color));
//            holder.typeTv.setBackgroundDrawable(mContext.getResources().getDrawable(isTypeAll
//                    ? R.drawable.job_resume_type_all_bg : R.drawable.job_resume_type_half_bg));

            holder.fuliLay.removeAllViews();
            List<WelfareList> welfareLists = info.getWelfareList();
            if (welfareLists != null && welfareLists.size() > 0) {
                int size = welfareLists.size();
                if (size > 3) {
                    size = 3;
                }

                for (int x = 0; x < size; x++) {
                    WelfareList welfare = welfareLists.get(x);
                    if (welfare != null) {
                        TextView tv = new TextView(mContext);
                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_server_list_promise_tv));
                        tv.setPadding(flTvPadding * 2, flTvPadding / 2, flTvPadding * 2, flTvPadding);
//                        tv.setfilColor(Color.parseColor(welfare.getWelfareColorValue())).setCornerSize(flTvPadding);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                        tv.setTextColor(mContext.getResources().getColor(R.color.yancy_green500));
                        tv.setText(welfare.getWelfareText());

                        holder.fuliLay.addView(tv);
                    }
                }
            }

            holder.submitTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onItemClickListener(0, realPosition);
                    }
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onItemClickListener(1, realPosition);
                    }
                }
            });
        }

        return convertView;
    }

    private static class JobHolder {
        private ImageView companyIv;
        private TextView typeTv;
        private TextView titleTv;
        private TextView timeTv;
        private TextView contentTv;
        private TextView submitTv;
        private TextView locationTv;
        private FlowLayout fuliLay;
        private TextView tvName;
        private TextView tvAuthor;

        public JobHolder(View view) {
            this.companyIv = view.findViewById(R.id.adapter_give_job_com_iv);
            this.titleTv = view.findViewById(R.id.adapter_give_job_title_tv);
            this.typeTv = view.findViewById(R.id.adapter_give_job_type_tv);
            this.timeTv = view.findViewById(R.id.adapter_give_job_time_tv);
            this.contentTv = view.findViewById(R.id.adapter_give_job_content_tv);
            this.submitTv = view.findViewById(R.id.adapter_give_job_sub_tv);
            this.locationTv = view.findViewById(R.id.adapter_give_job_location_tv);
            this.fuliLay = view.findViewById(R.id.adapter_give_job_fuli_lay);
            tvName = view.findViewById(R.id.tv_name);
            tvAuthor = view.findViewById(R.id.tv_author);
        }
    }
}
