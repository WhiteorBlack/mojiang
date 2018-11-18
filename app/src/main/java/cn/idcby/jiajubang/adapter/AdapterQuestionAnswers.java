package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.QuestionAnswers;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 行业顾问adapter
 * Created on 2018/4/19.
 */

public class AdapterQuestionAnswers extends RecyclerView.Adapter<AdapterQuestionAnswers.QwHolder> {
    private Context context ;
    private List<QuestionAnswers> mDataList ;
    private boolean mIsHori = false ;
    private RvItemViewClickListener mClickListener ;

    public AdapterQuestionAnswers(Context context, List<QuestionAnswers> mDataList,boolean isHori , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mIsHori = isHori;
        this.mClickListener = mClickListener;
    }

    @Override
    public QwHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QwHolder(LayoutInflater.from(context).inflate(mIsHori
                ? R.layout.adapter_question_answers_hori
                : R.layout.adapter_question_answers,parent ,false),mClickListener);
    }

    @Override
    public void onBindViewHolder(QwHolder holder, int position) {
        QuestionAnswers info = mDataList.get(position) ;
        if(info != null){
            String name = info.getQAMasterUserName() ;
            String imgUrl = info.getHeadIcon() ;

            holder.mNameTv.setText(name);
            GlideUtils.loaderUser(imgUrl ,holder.mIv) ;
        }
    }


    @Override
    public int getItemCount() {
        return  null == mDataList ? 0 : mDataList.size() ;
    }

    static class QwHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv ;
        private ImageView mIv ;

        public QwHolder(View view,final RvItemViewClickListener mClickListener) {
            super(view);

            mNameTv = view.findViewById(R.id.adapter_question_answers_name_tv) ;
            mIv = view.findViewById(R.id.adapter_question_answers_iv) ;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,getAdapterPosition());
                    }
                }
            });

        }
    }

}
