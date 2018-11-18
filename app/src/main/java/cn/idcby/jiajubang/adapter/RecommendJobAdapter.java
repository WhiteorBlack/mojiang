package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.RecommendJobsPostList;
import cn.idcby.jiajubang.R;

/**
 * Created on 2018/2/28.
 */

public class RecommendJobAdapter extends DefaultBaseAdapter<RecommendJobsPostList> {

    public RecommendJobAdapter(List<RecommendJobsPostList> datas, Context context) {
        super(datas, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.view_item_for_recommend_job,null);
        TextView tvJob = view.findViewById(R.id.tv_job);
        String job = datas.get(position).getWorkPostName() ;
        tvJob.setText(job);
        return view;
    }





}
