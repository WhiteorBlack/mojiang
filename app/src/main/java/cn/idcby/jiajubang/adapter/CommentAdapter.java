package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NewsCommentList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Create on 2018-03-22
 */

public class CommentAdapter extends BaseAdapter {
    private Activity context ;
    private List<NewsCommentList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public CommentAdapter(List<NewsCommentList> datas, Activity context,RvItemViewClickListener mClickListener) {
        this.mDataList = datas ;
        this.context = context ;
        this.mClickListener = mClickListener ;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        CommentHolder holder ;
        if(null == convertView){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.view_item_for_news_detail_comment , viewGroup , false) ;
            holder = new CommentHolder(convertView) ;
            convertView.setTag(holder) ;
        }else{
            holder = (CommentHolder) convertView.getTag();
        }

        holder.lvSecondComment.setVisibility(View.GONE);

        final NewsCommentList bean = mDataList.get(position);
        if(bean != null){
            holder.tvName.setText(bean.CreateUserName);
            holder.tvTime.setText(bean.CreateDate);
            holder.tvCommentContent.setText(bean.CommentContent);
            GlideUtils.loaderUser(bean.getCreateUserHeadIcon() , holder.imgHeadIcon) ;

            List<NewsCommentList> replyList = bean.getChildList() ;
            if(replyList != null && replyList.size() > 0){
                holder.lvSecondComment.setVisibility(View.VISIBLE) ;
                CommentReplyAdapter replyAdapter = new CommentReplyAdapter(replyList , context) ;
                holder.lvSecondComment.setAdapter(replyAdapter) ;
            }

            holder.imgHeadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(context ,bean.getCreateUserId()) ;
                }
            });

            holder.tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,position) ;
                    }
                }
            });
        }

        return convertView;
    }

    static class CommentHolder {
        private ImageView imgHeadIcon ;
        private TextView tvName ;
        private TextView tvTime ;
        private TextView tvReply ;
        private TextView tvCommentContent ;
        private ListView lvSecondComment ;

        public CommentHolder(View view) {
            this.imgHeadIcon = view.findViewById(R.id.img_head_icon) ;
            this.tvName = view.findViewById(R.id.tv_name);
            this.tvTime = view.findViewById(R.id.tv_time);
            this.tvCommentContent = view.findViewById(R.id.tv_comment_content) ;
            this.lvSecondComment = view.findViewById(R.id.lv_second_comment);
            this.tvReply = view.findViewById(R.id.tv_reply);
        }
    }

}
