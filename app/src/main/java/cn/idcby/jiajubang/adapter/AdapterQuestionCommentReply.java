package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.QuestionComment;
import cn.idcby.jiajubang.R;

/**
 * Create on 2018-03-29
 */

public class AdapterQuestionCommentReply extends BaseAdapter {
    private Context context ;
    private List<QuestionComment> mDataList ;

    public AdapterQuestionCommentReply(List<QuestionComment> datas, Context context) {
        this.mDataList = datas ;
        this.context = context ;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        CommentHolder holder ;
        if(null == convertView){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.view_item_for_news_detail_comment_reply , viewGroup , false) ;
            holder = new CommentHolder(convertView) ;
            convertView.setTag(holder) ;
        }else{
            holder = (CommentHolder) convertView.getTag();
        }

        final QuestionComment bean = mDataList.get(position);
        if(bean != null){
            holder.tvName.setText(bean.getCreateUserName() + "：") ;
            holder.tvCommentContent.setText(bean.getCreateUserName() + "：" + bean.getAnswerContent());
        }

        return convertView;
    }

    static class CommentHolder {
        private TextView tvName ;
        private TextView tvCommentContent ;

        public CommentHolder(View view) {
            this.tvName = view.findViewById(R.id.adapter_news_comment_reply_name);
            this.tvCommentContent = view.findViewById(R.id.adapter_news_comment_reply_content) ;
        }
    }

}
