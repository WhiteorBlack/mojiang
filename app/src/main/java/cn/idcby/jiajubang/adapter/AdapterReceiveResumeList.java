package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ReceiveResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ResumeDetailActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 *  我的收到的简历
 * Created on 2018/5/11.
 */

public class AdapterReceiveResumeList extends BaseAdapter {
    private Context context ;
    private List<ReceiveResumeList> mDataList ;

    public AdapterReceiveResumeList(Context context, List<ReceiveResumeList> mDataList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_receive_resume_list , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        ReceiveResumeList info = mDataList.get(i) ;
        if(info != null){
            final String resumeId = info.getResumeId() ;
            String name = info.getResumeName() ;
            String city = info.getCityName() ;
            String workName = info.getWorkPostName() ;
            String time = info.getReleaseTime() ;
            String imgUrl = info.getResumeUserHeadIcon() ;
            String money = info.getApplySalary() ;

            holder.mNameTv.setText(name);
            holder.mMoneyTv.setText("¥" + money);
            holder.mLocationTv.setText(city);
            holder.mWorkNameTv.setText(workName);
            holder.mTimeTv.setText(time);
            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent(context , ResumeDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_RESUME_ID ,resumeId) ;
                    context.startActivity(toDtIt) ;
                }
            });
        }

        return view;
    }


    private static class CrHolder{
        private ImageView mUserIv ;
        private TextView mNameTv ;
        private TextView mMoneyTv ;
        private TextView mLocationTv;
        private TextView mWorkNameTv;
        private TextView mTimeTv ;

        public CrHolder(View view) {
            mUserIv = view.findViewById(R.id.adapter_receive_resume_list_iv) ;
            mNameTv = view.findViewById(R.id.adapter_receive_resume_list_name_tv) ;
            mMoneyTv = view.findViewById(R.id.adapter_receive_resume_list_money_tv) ;
            mLocationTv = view.findViewById(R.id.adapter_receive_resume_list_location_tv) ;
            mWorkNameTv = view.findViewById(R.id.adapter_receive_resume_list_work_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_receive_resume_list_time_tv) ;
        }
    }

}
