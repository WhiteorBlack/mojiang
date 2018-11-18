package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.SubListBeans;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.view.TagsCloudsLayout;

/**
 * Created by Administrator on 2018-04-18.
 */

public class SubscriptionListAdapter extends BaseAdapter {
    private FragmentActivity mActiivity;
    private List<SubListBeans> mListBean;

    public SubscriptionListAdapter(FragmentActivity activity, List<SubListBeans> bean) {
        this.mActiivity = activity;
        this.mListBean = bean;
    }

    @Override
    public int getCount() {
        return mListBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mListBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActiivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_subitem_list, null);
            holder = new ViewHolder();
            holder.imgsub = (ImageView) convertView.findViewById(R.id.img_sub);//小图标
            holder.subtitle = (TextView) convertView.findViewById(R.id.sub_tv_title);//标题
            holder.submsg = (TextView) convertView.findViewById(R.id.sub_tv_msg);//miaoshuxinxi
            holder.subtag = (TextView) convertView.findViewById(R.id.sub_tv_tag);//评论信息
            holder.tagclouds = (TagsCloudsLayout) convertView.findViewById(R.id.sub_list_tags);//标签卡
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.subtitle.setText(mListBean.get(position).getNeedTitle());
        holder.submsg.setText(mListBean.get(position).getNeedExplain()
                + "/"
                + mListBean.get(position).getPosition() + "/"
                + mListBean.get(position).getEndTime()
        );
        holder.subtag.setText(mListBean.get(position).getLikeNumber()+"条");

        int[] colors = {mActiivity.getResources().getColor(R.color.yellowBotton),
                mActiivity.getResources().getColor(R.color.yellowBotton2),
                mActiivity.getResources().getColor(R.color.pressed),
                mActiivity.getResources().getColor(R.color.yancy_green500),
                mActiivity.getResources().getColor(R.color.notice_apply_inherit),
                mActiivity.getResources().getColor(R.color.lightBlue1),
                mActiivity.getResources().getColor(R.color.blueName),
                mActiivity.getResources().getColor(R.color.greenlight2),
                mActiivity.getResources().getColor(R.color.titleBlue2),
                mActiivity.getResources().getColor(R.color.redBorder),
                mActiivity.getResources().getColor(R.color.yancy_green500),
                mActiivity.getResources().getColor(R.color.yellowBotton)
        };

        String mNames[] = {"实木家具", "实木家具", "实木家具",
                "实木家具", "实木家具", "实木家具",
                "实木家具", "实木家具", "实木家具", "实木家具"
        };
//        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        for (int i = 0; i < mNames.length; i++) {
//            TextView view = new TextView(mActiivity);
//            view.setText(mNames[i]);
//            view.setTextColor(Color.WHITE);
////            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_bg));
//            view.setBackgroundColor(colors[i]);
//            holder.tagclouds.addView(view,lp);
//        }

        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;

        holder.tagclouds.removeAllViews() ;
        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(mActiivity);
            textView.setText(mNames[i]);
            textView.setTextColor(Color.WHITE);
//            textView.setBackgroundResource(R.drawable.round_square_blue);
            textView.setBackgroundColor(colors[i]);
            holder.tagclouds.addView(textView, lp);
        }

        return convertView;
    }

    class ViewHolder {
        TextView subtitle, submsg, subtag;
        ImageView imgsub;
        TagsCloudsLayout tagclouds;


    }


}
