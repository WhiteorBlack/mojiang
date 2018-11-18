package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.MyAnswer;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.QuestionDetailsActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.ViewUtil;

/**
 *  我的问答--我的回答
 * Created on 2018/5/3.
 */

public class AdapterMyQuestionAnswerList extends BaseAdapter {
    private Context context ;
    private List<MyAnswer> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMyQuestionAnswerList(Context context, List<MyAnswer> mDataList , RvItemViewClickListener mClickListener) {
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
        final int realPosition = i ;

        CrHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_question_answer_list , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        MyAnswer info = mDataList.get(i) ;
        if(info != null){
            final String questionId = info.getQuestionID() ;
            String name = info.getQAcreateUserName() ;
            String time = info.getReleaseTime() ;
            String imgUrl = info.getHeadIcon() ;
            String content = info.getQuestionExplain() ;
            String reword = info.getReward() ;

            holder.mRewordTv.setText("悬赏" + reword);
            holder.mUserNameTv.setText(name);
            holder.mTimeTv.setText(time);
            holder.mContentTv.setText(content);
            holder.mContentTv.setVisibility(!"".equals(content) ? View.VISIBLE : View.GONE);
            GlideUtils.loaderUser(imgUrl ,holder.mUserIv) ;

            final boolean isOptimum = info.isOptimum() ;
            String answerContent = info.getAnswerContent() ;

            ViewUtil.convertTextViewStyle(holder.mAnswerTv ,"我的回答：" + answerContent ,0 ,5 ,true);

            holder.mAnswerIv.setVisibility(isOptimum ? View.VISIBLE : View.GONE) ;
            holder.mDeleteTv.setVisibility(View.GONE) ;

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
                    if(isOptimum){
                        ToastUtils.showToast(context ,"该回答已被采纳，不能删除该回答");
                    }else{
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(0 ,realPosition);
                        }
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
        private TextView mAnswerTv ;
        private View mAnswerIv ;
        private TextView mDeleteTv;

        public CrHolder(View view) {
            mUserIv = view.findViewById(R.id.adapter_my_answer_question_list_user_iv) ;
            mUserNameTv = view.findViewById(R.id.adapter_my_answer_question_list_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_my_answer_question_list_time_tv) ;
            mRewordTv = view.findViewById(R.id.adapter_my_answer_question_list_reward_tv) ;
            mContentTv = view.findViewById(R.id.adapter_my_answer_question_list_content_tv) ;
            mPicRv = view.findViewById(R.id.adapter_my_answer_question_list_pic_rv) ;
            mAnswerTv = view.findViewById(R.id.adapter_my_answer_question_list_answer_tv);
            mAnswerIv = view.findViewById(R.id.adapter_my_answer_question_list_answer_iv);
            mDeleteTv = view.findViewById(R.id.adapter_my_answer_question_delete_tv) ;

            mPicRv.setFocusable(false);
            mPicRv.setNestedScrollingEnabled(false);
        }
    }

}
