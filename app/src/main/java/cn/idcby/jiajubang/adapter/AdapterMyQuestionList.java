package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.MyQuestion;
import cn.idcby.jiajubang.Bean.QuestionComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.QuestionDetailsActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 *  我的问答--我的问题
 * Created on 2018/5/3.
 */

public class AdapterMyQuestionList extends BaseAdapter {
    private Activity context ;
    private List<MyQuestion> mDataList ;
    private boolean mIsSelf = true ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyQuestionList(Activity context, List<MyQuestion> mDataList , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    public AdapterMyQuestionList(Activity context, List<MyQuestion> mDataList,boolean isSelf , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mIsSelf = mIsSelf;
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_question_list , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        MyQuestion info = mDataList.get(i) ;
        if(info != null){
            final String questionId = info.getQuestionId() ;
            String name = info.getAccount() ;
            String time = info.getReleaseTime() ;
            String location = info.getPosition() ;
            String imgUrl = info.getHeadIcon() ;
            String content = info.getQuestionExplain() ;
            String reword = info.getReward() ;

            holder.mRewordTv.setText("悬赏" + reword);
            holder.mUserNameTv.setText(name);
            holder.mTimeTv.setText(time + " " + location);
            holder.mContentTv.setText(content);
            holder.mContentTv.setVisibility(!"".equals(content) ? View.VISIBLE : View.GONE);
            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL) ;
            holder.mPicRv.setLayoutManager(layoutManager) ;
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                    , ResourceUtils.dip2px(context ,5)
                    , context.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mPicRv.getItemDecorationCount() == 0){
                holder.mPicRv.addItemDecoration(itemDecoration);
            }

            AdapterUnuseGoodImage imageAdapter = new AdapterUnuseGoodImage(context ,info.getAlbumsList()) ;
            holder.mPicRv.setAdapter(imageAdapter);

            List<QuestionComment> replyList = info.getAnswer() ;
            if(replyList != null && replyList.size() > 0){
                holder.mAnswerLay.setVisibility(View.VISIBLE) ;
                AdapterQuestionCommentReply replyAdapter = new AdapterQuestionCommentReply(replyList , context) ;
                holder.mAnswerLay.setAdapter(replyAdapter) ;
            }else{
                holder.mAnswerLay.setVisibility(View.GONE) ;
            }

            holder.mOptionsLay.setVisibility(mIsSelf ? View.VISIBLE : View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent(context , QuestionDetailsActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_QUESTION_ID ,questionId) ;
                    context.startActivity(toDtIt) ;
                }
            });
            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition);
                    }
                }
            });

            holder.mRefreshLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2 ,realPosition);
                    }
                }
            });

        }

        return view;
    }


    private static class CrHolder{
        private ImageView mUserIv ;
        private TextView mUserNameTv ;
        private TextView mTimeTv ;
        private TextView mRewordTv ;
        private TextView mContentTv ;
        private RecyclerView mPicRv ;
        private ListView mAnswerLay ;
        private View mOptionsLay;
        private TextView mRefreshLay ;
        private TextView mEditTv;
        private TextView mDeleteTv;

        public CrHolder(View view) {
            mUserIv = view.findViewById(R.id.adapter_my_question_list_user_iv) ;
            mUserNameTv = view.findViewById(R.id.adapter_my_question_list_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_my_question_list_time_tv) ;
            mRewordTv = view.findViewById(R.id.adapter_my_question_list_reward_tv) ;
            mContentTv = view.findViewById(R.id.adapter_my_question_list_content_tv) ;
            mPicRv = view.findViewById(R.id.adapter_my_question_list_pic_rv) ;
            mAnswerLay = view.findViewById(R.id.adapter_my_question_list_answer_lay) ;
            mEditTv = view.findViewById(R.id.adapter_my_question_edit_tv) ;
            mRefreshLay = view.findViewById(R.id.adapter_my_question_refresh_tv) ;
            mDeleteTv = view.findViewById(R.id.adapter_my_question_delete_tv) ;
            mOptionsLay = view.findViewById(R.id.adapter_my_question_comment_lay) ;

            mPicRv.setFocusable(false);
        }
    }

}
