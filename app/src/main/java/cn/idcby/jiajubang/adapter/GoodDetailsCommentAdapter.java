package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.StarView;

/**
 * Created on 2018/8/15.
 */

public class GoodDetailsCommentAdapter extends BaseAdapter {
    private Activity context ;
    private List<GoodComment> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public GoodDetailsCommentAdapter(Activity context, List<GoodComment> mDataList, RvItemViewClickListener mClickListener) {
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
        GcHolder holder ;

        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_good_details_comment,viewGroup ,false) ;
            holder = new GcHolder(view,context) ;
            view.setTag(holder);
        }else{
            holder = (GcHolder) view.getTag();
        }

        GoodComment comment = mDataList.get(i) ;
        if(comment != null){
            holder.mNameTv.setText(comment.getCreateUserName());
            holder.mTimeTv.setText(comment.getCreateTimeText());
            holder.mCommentTv.setText(comment.getBodyContent());
            GlideUtils.loaderUser(comment.getCreateUserHeadIcon() ,holder.mIv);

            holder.mGoodStarView.setLevel(comment.getStar());

            holder.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });

        }

        return view;
    }

    private static class GcHolder{
        private ImageView mIv ;
        private TextView mNameTv ;
        private TextView mTimeTv ;
        private TextView mCommentTv ;
        private StarView mGoodStarView ;

        public GcHolder(View view,Context context) {

            mIv = view.findViewById(R.id.adapter_good_comment_iv) ;
            mNameTv = view.findViewById(R.id.adapter_good_comment_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_good_comment_time_tv) ;
            mCommentTv = view.findViewById(R.id.adapter_good_comment_content_tv) ;
            mGoodStarView = view.findViewById(R.id.adapter_good_comment_good_star_view) ;

            int widHei = ResourceUtils.dip2px(context ,13) ;
            mGoodStarView.setWidHei(widHei,13);
        }
    }
}