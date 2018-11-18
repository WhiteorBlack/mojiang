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
import cn.idcby.jiajubang.Bean.UnuseCommentList;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * Created on 2018/4/23.
 */

public class AdapterUnuseLvGood extends BaseAdapter {
    private Activity context ;
    private List<UnuseGoodList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterUnuseLvGood(Activity context, List<UnuseGoodList> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @Override
    public long getItemId(int i) {
        return i;
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
    public View getView(int position, View view, ViewGroup parent) {
        final int realPosition = position ;

        UnsHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_unuse_spec_good_list ,parent ,false) ;

            holder = new UnsHolder(view) ;

            view.setTag(holder) ;
        }else{
            holder = (UnsHolder) view.getTag();
        }


        UnuseGoodList goodList = mDataList.get(position) ;
        if(goodList != null){
            String userImg = goodList.getHeadIcon() ;
            String userName = goodList.getNickName() ;
            String time = goodList.getUserOnLine() ;
            String money = goodList.getShowPrice() ;
            String location = goodList.getPostion() ;
            int likeCount = StringUtils.convertString2Count(goodList.getLikeNumber()) ;
            int commentCount = StringUtils.convertString2Count(goodList.getCommentNumber()) ;
            String title = goodList.getTitle() ;

            //2018-06-20 09:50:02，暂时不展示描述，只显示标题（仿咸鱼）
//            String showContent = goodList.getBodyContent() ;
//            holder.mContentTv.setText(showContent);

            holder.mTitleTv.setText(title);
            holder.mNameTv.setText(userName);
            holder.mTimeTv.setText(time);
            holder.mMoneyTv.setText(money);
            holder.mLocationTv.setText(!"".equals(location) ? ("来自" + location) : "") ;

            holder.mCommentCountTv.setText("" + commentCount);
            holder.mLikeCountTv.setText("" + likeCount);
            GlideUtils.loaderUser(userImg ,holder.mHeadIv);

            List<UnuseCommentList> commentLists = goodList.getCommentList() ;
            if(commentLists != null && commentLists.size() > 0){
                holder.mCommentLay.setVisibility(View.VISIBLE);
                holder.mCommentContentTv.setVisibility(View.VISIBLE);

                UnuseCommentList commentInfo = commentLists.get(0) ;
                String commentUser = commentInfo.getCreateUserName() ;
                String commentContent = commentInfo.getCommentContent() ;
                holder.mCommentContentTv.setText(commentUser + ": " + commentContent) ;

                List<UnuseCommentList> childCommentList = commentInfo.getChildList() ;
                if(childCommentList != null && childCommentList.size() > 0){
                    holder.mCommentReplyTv.setVisibility(View.VISIBLE) ;

                    UnuseCommentList replyInfo = childCommentList.get(0) ;
                    String replyContent = replyInfo.getCommentContent() ;
                    holder.mCommentReplyTv.setText(replyInfo.getCreateUserName() + "回复: " + replyContent) ;
                }else{
                    holder.mCommentReplyTv.setVisibility(View.GONE) ;
                }
            }else{
                holder.mCommentLay.setVisibility(View.GONE);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL) ;
            holder.mImageRv.setLayoutManager(layoutManager) ;
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                    ,ResourceUtils.dip2px(context ,5)
                    , context.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mImageRv.getItemDecorationCount() == 0){
                holder.mImageRv.addItemDecoration(itemDecoration);
            }
            AdapterUnuseGoodImage imageAdapter = new AdapterUnuseGoodImage(context ,goodList.getAlbumsList()) ;
            holder.mImageRv.setAdapter(imageAdapter);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition) ;
                    }
                }
            });
            holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition) ;
                    }
                }
            });
            holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosition) ;
                    }
                }
            });
        }

        return view;
    }


    static class UnsHolder{
        private ImageView mHeadIv ;
        private TextView mNameTv;
        private TextView mTimeTv ;
        private TextView mMoneyTv ;
        private RecyclerView mImageRv ;
        private TextView mTitleTv ;
        private TextView mContentTv ;
        private TextView mLocationTv ;
        private TextView mLikeCountTv ;
        private TextView mCommentCountTv ;

        private View mCommentLay ;
        private TextView mCommentContentTv ;
        private TextView mCommentReplyTv ;

        public UnsHolder(View itemView) {
            mHeadIv = itemView.findViewById(R.id.adapter_unuse_spec_user_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_unuse_spec_user_name_tv) ;
            mTimeTv = itemView.findViewById(R.id.adapter_unuse_spec_time_tv) ;
            mMoneyTv = itemView.findViewById(R.id.adapter_unuse_spec_money_tv) ;
            mImageRv = itemView.findViewById(R.id.adapter_unuse_spec_img_rv) ;
            mTitleTv = itemView.findViewById(R.id.adapter_unuse_spec_title_tv) ;
            mContentTv = itemView.findViewById(R.id.adapter_unuse_spec_content_tv) ;
            mLocationTv = itemView.findViewById(R.id.adapter_unuse_spec_location_tv) ;
            mLikeCountTv = itemView.findViewById(R.id.adapter_unuse_spec_like_count_tv) ;
            mCommentCountTv = itemView.findViewById(R.id.adapter_unuse_spec_comment_count_tv) ;
            mCommentLay = itemView.findViewById(R.id.adapter_unuse_spec_comment_lay) ;
            mCommentContentTv = itemView.findViewById(R.id.adapter_unuse_spec_comment_content_tv) ;
            mCommentReplyTv = itemView.findViewById(R.id.adapter_unuse_spec_comment_reply_tv) ;
            mImageRv.setFocusable(false);
        }
    }
}
