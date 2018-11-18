package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.QuestionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 行业问答adapter
 * Created on 2018/4/19.
 *
 * 2018-05-31 17:54:28
 * 与ios一致，暂时隐藏点赞数量
 */

public class AdapterQuestionList extends BaseAdapter {
    private Activity context ;
    private List<QuestionList> mDataList ;
    private RvItemViewClickListener mClickListener ;


    public AdapterQuestionList(Activity context, List<QuestionList> mDataList, RvItemViewClickListener mClickListener) {
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

        QaHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_question_list,viewGroup ,false) ;
            holder = new QaHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (QaHolder) view.getTag();
        }

        QuestionList info = mDataList.get(i) ;
        if(info != null){
            String userImg = info.getHeadIcon() ;
            String userName = info.getCreateUserName() ;
            String reward = info.getReward() ;
            String time = info.getReleaseTime() ;
            String title = info.getQuestionTitle() ;
            String commentCount = info.getAnswerNumber() ;
            String collectionCount = info.getLikeNumber() ;

            holder.mUserNameTv.setText(userName);
            holder.mRewardTv.setText("悬赏" + reward);
            holder.mTimeTv.setText(time);
            holder.mContentTv.setText(title);
            holder.mCountCollectionTv.setText(collectionCount);
            holder.mCountCommentTv.setText(commentCount);
            GlideUtils.loaderUser(userImg ,holder.mUserIv);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL) ;
            holder.mPicLay.setLayoutManager(layoutManager) ;
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                    ,ResourceUtils.dip2px(context ,5)
                    , context.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mPicLay.getItemDecorationCount() == 0){
                holder.mPicLay.addItemDecoration(itemDecoration);
            }

            AdapterUnuseGoodImage imageAdapter = new AdapterUnuseGoodImage(context ,info.getAlbumsList()) ;
            holder.mPicLay.setAdapter(imageAdapter);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosi);
                    }
                }
            });

        }

        return view;
    }

    private static class QaHolder {
        private TextView mUserNameTv ;
        private ImageView mUserIv ;
        private TextView mTimeTv ;
        private TextView mRewardTv ;
        private TextView mContentTv ;
        private RecyclerView mPicLay ;
        private TextView mCountCollectionTv ;
        private TextView mCountCommentTv ;

        public QaHolder(View view) {
            mUserIv = view.findViewById(R.id.adapter_question_list_user_iv) ;
            mUserNameTv = view.findViewById(R.id.adapter_question_list_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_question_list_time_tv) ;
            mRewardTv = view.findViewById(R.id.adapter_question_list_reward_tv) ;
            mContentTv = view.findViewById(R.id.adapter_question_list_content_tv) ;
            mPicLay = view.findViewById(R.id.adapter_question_list_pic_lay) ;
            mCountCollectionTv = view.findViewById(R.id.adapter_question_list_collection_count_tv) ;
            mCountCommentTv = view.findViewById(R.id.adapter_question_list_comment_count_tv) ;

            mPicLay.setFocusable(false);
        }
    }


}
