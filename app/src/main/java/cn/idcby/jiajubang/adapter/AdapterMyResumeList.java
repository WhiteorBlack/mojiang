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
 *  我的简历
 * Created on 2018/4/13.
 */

public class AdapterMyResumeList extends BaseAdapter {
    private Context context ;
    private boolean mIsBuy = false ;
    private List<ResumeList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyResumeList(Context context,boolean mIsBuy ,List<ResumeList> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mIsBuy = mIsBuy;
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

        CrHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_resume_list , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        ResumeList info = mDataList.get(i) ;
        if(info != null){
            final String resumeId = info.getResumeId() ;
            String name = info.getName() ;
            String city = info.getCityName() ;
            String workName = info.getWorkPostName() ;
            String time = info.getReleaseTime() ;
            String imgUrl = info.getUserHeadIcon() ;
            String money = info.getApplySalary() ;

            holder.mNameTv.setText(name);
            holder.mMoneyTv.setText("¥" + money);
            holder.mLocationTv.setText(city);
            holder.mWorkNameTv.setText(workName);
            holder.mTimeTv.setText(time);
            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;

            holder.mUpdownTv.setText(info.isEnableMark() ? "下架" : "上架");

            holder.mOptionLay.setVisibility(mIsBuy ? View.GONE : View.VISIBLE) ;
            holder.mOptionDv.setVisibility(mIsBuy ? View.GONE : View.VISIBLE) ;

//
//            holder.mUpdownTv.setVisibility(mIsBuy ? View.GONE : View.VISIBLE) ;
//            holder.mRefreshTv.setVisibility(mIsBuy ? View.GONE : View.VISIBLE) ;
//            holder.mEditTv.setVisibility(mIsBuy ? View.GONE : View.VISIBLE) ;

            holder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
            holder.mRefreshTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,realPosition);
                    }
                }
            });
            holder.mUpdownTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3 ,realPosition);
                    }
                }
            });
            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(4 ,realPosition);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(5 ,realPosition);
                    }
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
        private View mRefreshTv ;
        private TextView mUpdownTv ;
        private View mEditTv ;
        private View mDeleteIv ;
        private View mOptionLay ;
        private View mOptionDv ;

        public CrHolder(View view) {
            mRefreshTv = view.findViewById(R.id.adapter_my_resume_refresh_tv) ;
            mUpdownTv = view.findViewById(R.id.adapter_my_resume_updown_tv) ;
            mEditTv = view.findViewById(R.id.adapter_my_resume_edit_tv) ;
            mDeleteIv = view.findViewById(R.id.adapter_my_resume_delete_tv) ;
            mUserIv = view.findViewById(R.id.adapter_my_resume_list_iv) ;
            mNameTv = view.findViewById(R.id.adapter_my_resume_list_name_tv) ;
            mMoneyTv = view.findViewById(R.id.adapter_my_resume_list_money_tv) ;
            mLocationTv = view.findViewById(R.id.adapter_my_resume_list_location_tv) ;
            mWorkNameTv = view.findViewById(R.id.adapter_my_resume_list_work_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_my_resume_list_time_tv) ;
            mOptionLay = view.findViewById(R.id.adapter_my_resume_option_lay) ;
            mOptionDv = view.findViewById(R.id.adapter_my_resume_option_dv) ;
        }
    }

}
