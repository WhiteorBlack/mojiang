package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.NewsList;
import cn.idcby.jiajubang.Bean.NewsThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/5/10.
 * 首页热门资讯
 */

public class AdapterHomeHotNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SINGLE = 0 ;
    private static final int TYPE_SINGLE_BIG = 1 ;
    private static final int TYPE_MORE = 2 ;

    private Context mContext ;
    private List<NewsList> mData ;
    private int mSingleWidth = 0 ;
    private int mSingleBigWidth = 0 ;

    public AdapterHomeHotNews(Context mContext, List<NewsList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mSingleBigWidth = ResourceUtils.getScreenWidth(mContext) - ResourceUtils.dip2px(mContext , 15) * 2 ;
        this.mSingleWidth = (ResourceUtils.getScreenWidth(mContext) - ResourceUtils.dip2px(mContext , 15) * 2
                - ResourceUtils.dip2px(mContext , 10) * 2) / 3;
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        NewsList newsItem = mData.get(position) ;

        if(newsItem.isVideo()){
            return TYPE_SINGLE_BIG ;
        }else{
            int newsType = newsItem.getAlbumsType() ;
            if(1 == newsType){
                return TYPE_SINGLE_BIG ;
            }else
            if(3 == newsType && newsItem.getAlbumsList() != null
                    && newsItem.getAlbumsList().size() > 1){
                return TYPE_MORE ;
            }else{
                return TYPE_SINGLE ;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(TYPE_MORE == viewType){
            return new NewsMoreHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.view_item_for_hot_news_three_bottom, parent ,false),mSingleWidth);
        }else if(TYPE_SINGLE_BIG == viewType){
            return new NewsSingleBigHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.view_item_for_hot_news_single_bottom, parent ,false),mSingleBigWidth);
        }
        return new NewsSingleHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.view_item_for_hot_news_single_right, parent ,false),mSingleWidth);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder pHolder, int position) {
        final NewsList newsList = mData.get(position);
        if(newsList != null){
            final String newsId = newsList.getArticleID() ;
            String title = newsList.getTitle() ;
            String source = newsList.getArticleSource() ;
            String time = newsList.getReleaseTime() ;
            String comment = newsList.getCommentNumber()+"" ;

            final boolean isVideo = newsList.isVideo() ;

            if(pHolder instanceof HotNewsBaseHolder){
                HotNewsBaseHolder parentHolder = (HotNewsBaseHolder) pHolder;

                parentHolder.mTitleTv.setText(title);
                parentHolder.mSourceTv.setText(source);
                parentHolder.mTimeTv.setText(time);
                parentHolder.mCommentTv.setText(comment);

                final String videoPath = newsList.getLinkUrl() ;

                parentHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isVideo){
                            SkipUtils.toNewsDetailVideoActivity(mContext, newsId, videoPath) ;
                        }else{
                            SkipUtils.toNewsDetailActivity(mContext, newsId);
                        }
                    }
                });
            }

            if(pHolder instanceof NewsSingleHolder){
                NewsSingleHolder holder = (NewsSingleHolder) pHolder;

                String imgUrl = newsList.getImgUrl() ;

                List<NewsThumb> thumbList = newsList.getAlbumsList() ;
                if(thumbList != null && thumbList.size() > 0){
                    imgUrl = thumbList.get(0).getThumbImgUrl() ;
                }

                GlideUtils.loader(MyApplication.getInstance(), imgUrl, holder.mThumbIv) ;
            }else if(pHolder instanceof NewsSingleBigHolder){
                NewsSingleBigHolder holder = (NewsSingleBigHolder) pHolder;

                holder.mVideoIv.setVisibility(isVideo ? View.VISIBLE : View.GONE) ;

                String imgUrl = newsList.getImgUrl() ;

                List<NewsThumb> thumbList = newsList.getAlbumsList() ;
                if(thumbList != null && thumbList.size() > 0){
                    imgUrl = thumbList.get(0).getThumbImgUrl() ;
                }

                GlideUtils.loader(MyApplication.getInstance(), imgUrl, holder.mThumbIv) ;
            }else if(pHolder instanceof NewsMoreHolder){
                NewsMoreHolder holder = (NewsMoreHolder) pHolder;

                String leftUrl = "" ;
                String midUrl = "" ;
                String rightUrl = "" ;
                List<NewsThumb> thumbList = newsList.getAlbumsList() ;
                if(thumbList != null){
                    if(thumbList.size() > 2){
                        leftUrl = thumbList.get(0).getThumbImgUrl() ;
                        midUrl = thumbList.get(1).getThumbImgUrl() ;
                        rightUrl = thumbList.get(2).getThumbImgUrl() ;
                    }else if(thumbList.size() > 1){
                        leftUrl = thumbList.get(0).getThumbImgUrl() ;
                        midUrl = thumbList.get(1).getThumbImgUrl() ;
                    }
                }

                GlideUtils.loader(leftUrl, holder.mThumbLeftIv);
                GlideUtils.loader(midUrl, holder.mThumbMiddleIv);
                GlideUtils.loader(rightUrl, holder.mThumbRightIv);
            }
        }
    }

    static class NewsSingleHolder extends HotNewsBaseHolder{
        private ImageView mThumbIv ;

        public NewsSingleHolder(View itemView,int width) {
            super(itemView);

            mThumbIv = itemView.findViewById(R.id.img_photo);

            mThumbIv.getLayoutParams().width = width ;
            mThumbIv.getLayoutParams().height = (int) (width / ImageWidthUtils.getIndexHotNewsImageRote());
        }
    }

    static class NewsSingleBigHolder extends HotNewsBaseHolder{
        private ImageView mThumbIv ;
        private View mVideoIv ;

        public NewsSingleBigHolder(View itemView,int width) {
            super(itemView);

            mThumbIv = itemView.findViewById(R.id.img_photo);
            mVideoIv = itemView.findViewById(R.id.adapter_hot_news_video_play_iv);

            mThumbIv.getLayoutParams().width = width ;
            mThumbIv.getLayoutParams().height = (int) (width / ImageWidthUtils.getIndexHotNewsBigImageRote());
        }
    }

    static class NewsMoreHolder extends HotNewsBaseHolder{
        private ImageView mThumbLeftIv ;
        private ImageView mThumbMiddleIv ;
        private ImageView mThumbRightIv ;

        public NewsMoreHolder(View itemView, int width) {
            super(itemView);

            mThumbLeftIv = itemView.findViewById(R.id.img_photo_left);
            mThumbMiddleIv = itemView.findViewById(R.id.img_photo_mid);
            mThumbRightIv = itemView.findViewById(R.id.img_photo_right);

            mThumbLeftIv.getLayoutParams().width = width ;
            mThumbLeftIv.getLayoutParams().height = (int) (width / ImageWidthUtils.getIndexHotNewsImageRote());
            mThumbMiddleIv.getLayoutParams().width = width ;
            mThumbMiddleIv.getLayoutParams().height = (int) (width / ImageWidthUtils.getIndexHotNewsImageRote());
            mThumbRightIv.getLayoutParams().width = width ;
            mThumbRightIv.getLayoutParams().height = (int) (width / ImageWidthUtils.getIndexHotNewsImageRote());
        }
    }

    static class HotNewsBaseHolder extends RecyclerView.ViewHolder{
        View mMainLay ;
        TextView mTitleTv ;
        TextView mSourceTv ;
        TextView mTimeTv ;
        TextView mCommentTv ;

        HotNewsBaseHolder(View itemView) {
            super(itemView);

            mMainLay = itemView.findViewById(R.id.adapter_home_hot_news_main_lay);
            mTitleTv = itemView.findViewById(R.id.tv_title);
            mSourceTv = itemView.findViewById(R.id.tv_source);
            mTimeTv = itemView.findViewById(R.id.tv_time);
            mCommentTv = itemView.findViewById(R.id.tv_comment);
        }
    }

}
