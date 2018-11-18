package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.JobPost;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/3/27.
 */

public class AdapterJobPostList extends BaseAdapter {
    private Context mContext ;
    private List<JobPost> mDataList ;

    public AdapterJobPostList(Context mContext, List<JobPost> mDataList) {
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
        JpHolder holder ;

        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_job_post_list,viewGroup , false) ;
            holder = new JpHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (JpHolder) view.getTag();
        }

        JobPost info = mDataList.get(i) ;
        if(info != null){
            String name = info.getName() ;
            holder.mTv.setText(name) ;
            holder.mIv.setVisibility(info.isHaveChilder() ? View.VISIBLE : View.GONE);
        }

        return view;
    }

    private static class JpHolder{
        private ImageView mIv ;
        private TextView mTv ;

        public JpHolder(View view) {
            this.mTv = view.findViewById(R.id.adapter_job_post_list_tv) ;
            this.mIv = view.findViewById(R.id.adapter_job_post_list_iv) ;
        }
    }

}
