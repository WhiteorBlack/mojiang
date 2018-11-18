package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.MessageListComment;
import cn.idcby.jiajubang.Bean.MessageListSystem;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 消息列表
 * Created on 2018/4/21.
 */

public class AdapterMessageList extends BaseAdapter {
    private Context context ;
    private List<MessageListSystem> mSystemList ;
    private List<MessageListComment> mCommentList ;
    private RvItemViewClickListener mClickListener ;
    private boolean mIsCircle = false ;

    public AdapterMessageList(Context context,boolean isCircle , List<MessageListSystem> mSystemList
            , List<MessageListComment> mCommentList ,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mIsCircle = isCircle;
        this.mSystemList = mSystemList;
        this.mCommentList = mCommentList;
        this.mClickListener = mClickListener;
    }

    public AdapterMessageList(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return null == mSystemList ? (null == mCommentList ? 0 : mCommentList.size()) : mSystemList.size() ;
    }

    @Override
    public Object getItem(int i) {
        if(mSystemList != null){
            return mSystemList.get(i) ;
        }
        if(mCommentList != null){
            return mCommentList.get(i) ;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int realPosition = i ;

        MlHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_message_list , viewGroup ,false) ;
            holder = new MlHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (MlHolder) view.getTag();
        }

        if(mSystemList != null){
            MessageListSystem systemMsg = mSystemList.get(i) ;
            if(systemMsg != null){
                String title = systemMsg.getFullHead() ;
                String time = systemMsg.getCreateDate() ;

                holder.mTitleTv.setText(title);
                holder.mTimeTv.setText(time);
                holder.mIv.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_msg_ct_sys));
            }
        }else if(mCommentList != null){
            MessageListComment commentMsg = mCommentList.get(i) ;
            if(commentMsg != null){
                String title = commentMsg.getCreateUserName() ;
                String content = commentMsg.getCommentContent() ;
                String time = commentMsg.getCreateDate() ;

                holder.mTitleTv.setText(title);
                holder.mContentTv.setText(content);
                holder.mTimeTv.setText(time);
                GlideUtils.loaderUser(commentMsg.getCreateUserHeadIcon(),holder.mIv) ;
//
//                holder.mIv.setImageDrawable(context.getResources().getDrawable(mIsCircle
//                        ? R.mipmap.ic_msg_ct_circle
//                        : R.mipmap.ic_msg_ct_comment));
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null){
                    mClickListener.onItemClickListener(0 ,realPosition);
                }
            }
        });

        return view;
    }

    private static class MlHolder{
        private TextView mTitleTv ;
        private TextView mContentTv ;
        private TextView mTimeTv ;
        private ImageView mIv ;

        public MlHolder(View v) {
            mTitleTv = v.findViewById(R.id.adapter_message_list_title_tv) ;
            mContentTv = v.findViewById(R.id.adapter_message_list_content_tv) ;
            mTimeTv = v.findViewById(R.id.adapter_message_list_time_tv) ;
            mIv = v.findViewById(R.id.adapter_message_list_iv) ;
        }
    }

}
