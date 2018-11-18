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
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 *  选择简历
 * Created on 2018/3/28.
 */

public class AdapterChooseResume extends BaseAdapter {
    private Context context ;
    private List<ResumeList> mDataList ;

    public AdapterChooseResume(Context context, List<ResumeList> mDataList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_resume , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        ResumeList info = mDataList.get(i) ;
        if(info != null){
            String name = info.getName() ;
            String money = info.getApplySalary() ;
            String sex = info.getSex() ;
            String birthday = info.getBirthday() ;
            String time = info.getReleaseTime() ;
            String imgUrl = info.getUserHeadIcon() ;

            holder.mNameTv.setText(name);
            holder.mMoneyTv.setText(money);
            holder.mSexTv.setText(sex);
            holder.mBirthdayTv.setText(birthday);
            holder.mTimeTv.setText("创建时间：" + time);
            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;
        }

        return view;
    }


    private static class CrHolder{
        private ImageView mUserIv ;
        private TextView mNameTv ;
        private TextView mMoneyTv ;
        private TextView mSexTv ;
        private TextView mBirthdayTv ;
        private TextView mTimeTv ;

        public CrHolder(View view) {
            mUserIv = view.findViewById(R.id.adapter_choose_resume_iv) ;
            mNameTv = view.findViewById(R.id.adapter_choose_resume_name_tv) ;
            mMoneyTv = view.findViewById(R.id.adapter_choose_resume_money_tv) ;
            mSexTv = view.findViewById(R.id.adapter_choose_resume_sex_tv) ;
            mBirthdayTv = view.findViewById(R.id.adapter_choose_resume_age_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_choose_resume_time_tv) ;
        }
    }

}
