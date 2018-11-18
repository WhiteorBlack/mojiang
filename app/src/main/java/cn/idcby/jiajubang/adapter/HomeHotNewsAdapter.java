package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/6.
 */

public class HomeHotNewsAdapter extends BaseAdapter {
    private static final int TYPE_SINGLE = 0 ;
    private static final int TYPE_SINGLE_BIG = 1 ;
    private static final int TYPE_MORE = 2 ;

    private Context mContext ;
    private List<NewsList> mData ;
    private LayoutInflater mInflater ;
    private int mSingleWidth = 0 ;
    private int mSingleBigWidth = 0 ;

    public HomeHotNewsAdapter(Context context ,List<NewsList> datas) {
        this.mContext = context ;
        this.mData = datas ;
        this.mInflater = LayoutInflater.from(mContext) ;
        this.mSingleBigWidth = ResourceUtils.getScreenWidth(context) - ResourceUtils.dip2px(context , 15) * 2 ;
        this.mSingleWidth = (ResourceUtils.getScreenWidth(context) - ResourceUtils.dip2px(context , 15) * 2
                - ResourceUtils.dip2px(context , 10) * 2) / 3;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        NewsList newsItem = mData.get(position) ;
        int newsType = newsItem.getAlbumsType() ;
        if(1 == newsType || newsItem.isVideo()){
            return TYPE_SINGLE_BIG ;
        }else
            if(3 == newsType && newsItem.getAlbumsList() != null
                && newsItem.getAlbumsList().size() > 1){
            return TYPE_MORE ;
        }else{
            return TYPE_SINGLE ;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        int viewType = getItemViewType(position) ;
        if(TYPE_MORE == viewType){
            convertView = mInflater.inflate(R.layout.view_item_for_hot_news_three_bottom, viewGroup ,false);

            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvSource = convertView.findViewById(R.id.tv_source);
            TextView tvTime = convertView.findViewById(R.id.tv_time);
            TextView tvComment = convertView.findViewById(R.id.tv_comment);
            ImageView imgPhotoLeft = convertView.findViewById(R.id.img_photo_left);
            ImageView imgPhotoMid = convertView.findViewById(R.id.img_photo_mid);
            ImageView imgPhotoRight = convertView.findViewById(R.id.img_photo_right);

            imgPhotoLeft.getLayoutParams().width = mSingleWidth ;
            imgPhotoLeft.getLayoutParams().height = (int) (mSingleWidth / ImageWidthUtils.getIndexHotNewsImageRote());
            imgPhotoMid.getLayoutParams().width = mSingleWidth ;
            imgPhotoMid.getLayoutParams().height = (int) (mSingleWidth / ImageWidthUtils.getIndexHotNewsImageRote());
            imgPhotoRight.getLayoutParams().width = mSingleWidth ;
            imgPhotoRight.getLayoutParams().height = (int) (mSingleWidth / ImageWidthUtils.getIndexHotNewsImageRote());

            NewsList newsList = mData.get(position);
            tvTitle.setText(newsList.getTitle());
            tvSource.setText(newsList.getArticleSource());
            tvTime.setText(newsList.getReleaseTime());
            tvComment.setText(String.valueOf(newsList.getCommentNumber()));

            List<NewsThumb> thumbList = newsList.getAlbumsList() ;
            if(thumbList != null){
                String leftUrl = "" ;
                String midUrl = "" ;
                String rightUrl = "" ;

                if(thumbList.size() > 2){
                    leftUrl = thumbList.get(0).getThumbImgUrl() ;
                    midUrl = thumbList.get(1).getThumbImgUrl() ;
                    rightUrl = thumbList.get(2).getThumbImgUrl() ;
                }else if(thumbList.size() > 1){
                    leftUrl = thumbList.get(0).getThumbImgUrl() ;
                    midUrl = thumbList.get(1).getThumbImgUrl() ;
                }

                GlideUtils.loader(MyApplication.getInstance()
                        , leftUrl, imgPhotoLeft);
                GlideUtils.loader(MyApplication.getInstance()
                        , midUrl, imgPhotoMid);
                GlideUtils.loader(MyApplication.getInstance()
                        , rightUrl, imgPhotoRight);
            }
        }else if(TYPE_SINGLE_BIG == viewType){
            convertView = mInflater.inflate(R.layout.view_item_for_hot_news_single_bottom, viewGroup ,false);

            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvSource = convertView.findViewById(R.id.tv_source);
            TextView tvTime = convertView.findViewById(R.id.tv_time);
            TextView tvComment = convertView.findViewById(R.id.tv_comment);
            ImageView imgPhoto = convertView.findViewById(R.id.img_photo);
            View videoTips = convertView.findViewById(R.id.adapter_hot_news_video_play_iv);

            imgPhoto.getLayoutParams().width = mSingleBigWidth ;
            imgPhoto.getLayoutParams().height = (int) (mSingleBigWidth / ImageWidthUtils.getIndexHotNewsBigImageRote());

            NewsList newsList = mData.get(position);
            tvTitle.setText(newsList.getTitle());
            tvSource.setText(newsList.getArticleSource());
            tvTime.setText(newsList.getReleaseTime());
            tvComment.setText(String.valueOf(newsList.getCommentNumber()));

            videoTips.setVisibility(newsList.isVideo() ? View.VISIBLE : View.GONE);

            String imgUrl = newsList.getImgUrl() ;

            List<NewsThumb> thumbList = newsList.getAlbumsList() ;
            if(thumbList != null && thumbList.size() > 0){
                imgUrl = thumbList.get(0).getThumbImgUrl() ;
            }

            GlideUtils.loader(MyApplication.getInstance(), imgUrl, imgPhoto);
        }else{
            convertView = mInflater.inflate(R.layout.view_item_for_hot_news_single_right, viewGroup ,false);

            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvSource = convertView.findViewById(R.id.tv_source);
            TextView tvTime = convertView.findViewById(R.id.tv_time);
            TextView tvComment = convertView.findViewById(R.id.tv_comment);
            ImageView imgPhoto = convertView.findViewById(R.id.img_photo);

            imgPhoto.getLayoutParams().width = mSingleWidth ;
            imgPhoto.getLayoutParams().height = (int) (mSingleWidth / ImageWidthUtils.getIndexHotNewsImageRote());

            NewsList newsList = mData.get(position);
            tvTitle.setText(newsList.getTitle());
            tvSource.setText(newsList.getArticleSource());
            tvTime.setText(newsList.getReleaseTime());
            tvComment.setText(String.valueOf(newsList.getCommentNumber()));

            String imgUrl = newsList.getImgUrl() ;

            List<NewsThumb> thumbList = newsList.getAlbumsList() ;
            if(thumbList != null && thumbList.size() > 0){
                imgUrl = thumbList.get(0).getThumbImgUrl() ;
            }

            GlideUtils.loader(MyApplication.getInstance(), imgUrl, imgPhoto);
        }

        final String newsId = mData.get(position).getArticleID() ;
        final boolean isVideo = mData.get(position).isVideo() ;
        final String videoPath = mData.get(position).getLinkUrl() ;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVideo){
                    SkipUtils.toNewsDetailVideoActivity(mContext, newsId, videoPath) ;
                }else{
                    SkipUtils.toNewsDetailActivity(mContext, newsId);
                }
            }
        });

        return convertView;
    }
}