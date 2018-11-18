package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.QuestionComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/20.
 */

public class AdapterQuestionComment extends BaseAdapter {
    private Context context ;
    private List<QuestionComment> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private boolean mIsFinishQuestion = false ;
    private boolean mIsSelf = false ;

    public void setIsSelf(boolean mIsSelf,boolean isFinishQuestion) {
        this.mIsSelf = mIsSelf;
        this.mIsFinishQuestion = isFinishQuestion;
        notifyDataSetChanged();
    }

    public AdapterQuestionComment(Context context, List<QuestionComment> mDataList
            , RvItemViewClickListener mClickListener) {
        this.context = context;
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
        final int realPosi = i ;

        QcHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_question_comment,viewGroup,false) ;
            holder = new QcHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (QcHolder) view.getTag();
        }

        QuestionComment info = mDataList.get(i) ;
        if(info != null){
            final String userId = info.getCreateUserId() ;
            String userUrl = info.getHeadIcon() ;
            String userName = info.getCreateUserName() ;
            String time = info.getReleaseTime() ;
            String supportCount = info.getLikeNumber() ;
            String content = info.getAnswerContent() ;
            boolean isUseful = info.isOptimum() ;
            boolean isLike = info.isLike() ;

            holder.mNameTv.setText(userName);
            holder.mTimeTv.setText(time);
            holder.mLikeNumTv.setText(supportCount + "人赞同这条回答");
            holder.mContentTv.setText(content);
            holder.mIsUsefulLay.setVisibility(isUseful ? View.VISIBLE : View.INVISIBLE);
            holder.mSupportIv.setImageDrawable(context.getResources().getDrawable(isLike
                    ? R.mipmap.ic_support_checked : R.mipmap.ic_support_nomal));
            GlideUtils.loaderUser(userUrl ,holder.mHeadIv);

            holder.mFinishTv.setVisibility(mIsSelf && !mIsFinishQuestion ? View.VISIBLE : View.GONE);

            //2018-05-20 17:37:21 调整功能，暂时隐藏
            holder.mSubTv.setText(mIsSelf ? "追问" : "回复");
            holder.mSubTv.setVisibility(View.GONE);

            List<QuestionComment> replyList = info.getChildList() ;
            if(replyList != null && replyList.size() > 0){
//                //暂时隐藏，要改界面
//                holder.mReplyLv.setVisibility(View.VISIBLE) ;
                AdapterQuestionCommentReply replyAdapter = new AdapterQuestionCommentReply(replyList , context) ;
                holder.mReplyLv.setAdapter(replyAdapter) ;
            }

            holder.mSupportLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosi);
                    }
                }
            });
            holder.mSubTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosi);
                    }
                }
            });
            holder.mFinishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,realPosi);
                    }
                }
            });

            holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(context ,userId) ;
                }
            });

        }

        return view;
    }


    private static class QcHolder{
        private ImageView mHeadIv ;
        private TextView mNameTv ;
        private TextView mTimeTv ;
        private View mIsUsefulLay ;
        private TextView mLikeNumTv ;
        private TextView mContentTv ;
        private View mSupportLay ;
        private TextView mSupportTv ;
        private ImageView mSupportIv ;
        private TextView mFinishTv ;
        private TextView mSubTv ;
        private ListView mReplyLv ;

        public QcHolder(View v) {
            mHeadIv = v.findViewById(R.id.adapter_question_comment_user_iv) ;
            mNameTv = v.findViewById(R.id.adapter_question_comment_user_name_tv) ;
            mTimeTv = v.findViewById(R.id.adapter_question_comment_time_tv) ;
            mIsUsefulLay = v.findViewById(R.id.adapter_question_comment_useful_lay) ;
            mLikeNumTv = v.findViewById(R.id.adapter_question_comment_support_count_tv) ;
            mContentTv = v.findViewById(R.id.adapter_question_comment_content_tv) ;
            mSupportLay = v.findViewById(R.id.adapter_question_comment_support_lay) ;
            mSupportTv = v.findViewById(R.id.adapter_question_comment_support_tv) ;
            mSupportIv = v.findViewById(R.id.adapter_question_comment_support_iv) ;
            mSubTv = v.findViewById(R.id.adapter_question_comment_sub_tv) ;
            mFinishTv = v.findViewById(R.id.adapter_question_comment_finish_tv) ;
            mReplyLv = v.findViewById(R.id.adapter_question_comment_receive_lv) ;
        }
    }

}
