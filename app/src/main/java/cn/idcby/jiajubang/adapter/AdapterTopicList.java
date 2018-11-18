package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.TopicList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.TopicDetailActivity;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/3/23.
 */

public class AdapterTopicList extends BaseAdapter {
    private Context mContext ;
    private List<TopicList> mDataList ;
    private int mImageWid ;

    public AdapterTopicList(Context mContext, List<TopicList> mDataList , int imageWid) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mImageWid = imageWid;
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
        TopicHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_topic_list , viewGroup , false) ;
            holder = new TopicHolder(view,mImageWid) ;
            view.setTag(holder) ;
        }else{
            holder = (TopicHolder) view.getTag();
        }

        TopicList topics = mDataList.get(i) ;

        if(topics != null){
            final String postId = topics.getPostID() ;

            String title = topics.getTitle() ;
            String imgUrl = topics.getImgUrl() ;
            String categoryName = topics.getCategoryTitle() ;

            holder.mTitleTv.setText(title) ;
            holder.mCategoryTv.setText(categoryName) ;
            GlideUtils.loader(MyApplication.getInstance(),imgUrl , holder.mBgIv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toDtIt = new Intent(mContext , TopicDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , postId) ;
                    mContext.startActivity(toDtIt) ;
                }
            });
        }
        return view;
    }

    static class TopicHolder{
        private TextView mCategoryTv ;
        private TextView mTitleTv ;
        private ImageView mBgIv ;

        public TopicHolder(View view ,int mImageWid) {
            this.mCategoryTv = view.findViewById(R.id.adapter_topic_category_tv) ;
            this.mTitleTv = view.findViewById(R.id.adapter_topic_title_tv) ;
            this.mBgIv = view.findViewById(R.id.adapter_topic_bg_iv) ;

            int itemHeight = (int) (mImageWid / ImageWidthUtils.getTopicImageRote());
            this.mBgIv.getLayoutParams().width = mImageWid ;
            this.mBgIv.getLayoutParams().height = itemHeight ;
        }
    }


}
