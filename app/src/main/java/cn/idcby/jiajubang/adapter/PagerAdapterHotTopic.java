package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.TopicList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.TopicDetailActivity;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 热门话题
 * Created on 2018/6/6.
 */

public class PagerAdapterHotTopic extends PagerAdapter {
    private Activity context ;
    private List<TopicList> mDataList ;
    private int mImageWid ;
    public static final int MAX_SLIP = 50 ;

    public PagerAdapterHotTopic(Activity context, List<TopicList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.mImageWid = (ResourceUtils.getScreenWidth(context) - ResourceUtils.dip2px(context ,50) * 2 );
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size() *  MAX_SLIP;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object ;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_topic_list ,container,false) ;
        TextView mCategoryTv = view.findViewById(R.id.adapter_topic_category_tv) ;
        TextView mTitleTv = view.findViewById(R.id.adapter_topic_title_tv) ;
        ImageView mBgIv = view.findViewById(R.id.adapter_topic_bg_iv) ;
        int itemHeight = (int) (mImageWid / ImageWidthUtils.getTopicImageRote());
        mBgIv.getLayoutParams().width = mImageWid ;
        mBgIv.getLayoutParams().height = itemHeight ;

        int select = position < mDataList.size() ? position : (position % mDataList.size());
        TopicList topics = mDataList.get(select) ;

        if(topics != null){
            final String postId = topics.getPostID() ;

            String title = topics.getTitle() ;
            String imgUrl = topics.getImgUrl() ;
            String categoryName = topics.getCategoryTitle() ;

            mTitleTv.setText(title) ;
            mCategoryTv.setText(categoryName) ;
            GlideUtils.loader(MyApplication.getInstance(),imgUrl , mBgIv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent(context , TopicDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , postId) ;
                    context.startActivity(toDtIt) ;
                }
            });
        }


        container.addView(view) ;
        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
