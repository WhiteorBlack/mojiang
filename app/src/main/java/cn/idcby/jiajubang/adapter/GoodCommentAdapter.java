package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Context;
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
import cn.idcby.jiajubang.Bean.GoodComment;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.StarView;

/**
 * Created on 2018/8/15.
 */

public class GoodCommentAdapter extends BaseAdapter {
    private Activity context ;
    private List<GoodComment> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int mImageWidHei ;

    public GoodCommentAdapter(Activity context, List<GoodComment> mDataList, RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.mImageWidHei = (int) ((ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context , 15) - ResourceUtils.dip2px(context , 2) * 2) / 3.5F);
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_good_comment,viewGroup ,false) ;
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
            holder.mSpecTv.setText(comment.getSpecText());
            GlideUtils.loaderUser(comment.getCreateUserHeadIcon() ,holder.mIv);

            holder.mGoodStarView.setLevel(comment.getStar());
            holder.mDriverStarView.setLevel(comment.getWuLiuFuWuStar());
            holder.mServerStarView.setLevel(comment.getFuWuTaiDuStar());

            holder.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });

            List<ImageThumb> goodImageList = comment.getImgList() ;
            if(goodImageList.size() == 0){
                holder.mPictureLay.setVisibility(View.GONE);
            }else{
                holder.mPictureLay.setVisibility(View.VISIBLE);

                if(holder.mPictureLay.getItemDecorationCount() == 0){
                    RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                            , ResourceUtils.dip2px(context ,2)
                            , context.getResources().getDrawable(R.drawable.drawable_white_trans)
                            ,RvLinearManagerItemDecoration.HORIZONTAL_LIST) ;
                    holder.mPictureLay.addItemDecoration(itemDecoration);
                }

                AdapterNomalImage imageAdapter = new AdapterNomalImage(context ,goodImageList ,mImageWidHei) ;
                holder.mPictureLay.setAdapter(imageAdapter);
            }

            //追评
            GoodComment childInfo = comment.getChildInfo() ;
            if(childInfo != null){
                holder.mCommentResendTimeTv.setVisibility(View.VISIBLE);
                holder.mCommentResendTv.setVisibility(View.VISIBLE);

                String time = comment.getChildCommentTime() ;
                String childComment = childInfo.getBodyContent() ;

                holder.mCommentResendTimeTv.setText(time);
                holder.mCommentResendTv.setText(childComment);

                //图集
                List<ImageThumb> childImgList = comment.getChildAlbumsList() ;
                if(childImgList.size() == 0){
                    holder.mPictureResendLay.setVisibility(View.GONE);
                }else{
                    holder.mPictureResendLay.setVisibility(View.VISIBLE);

                    if(holder.mPictureResendLay.getItemDecorationCount() == 0){
                        RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                                , ResourceUtils.dip2px(context ,2)
                                , context.getResources().getDrawable(R.drawable.drawable_white_trans)
                                ,RvLinearManagerItemDecoration.HORIZONTAL_LIST) ;
                        holder.mPictureResendLay.addItemDecoration(itemDecoration);
                    }

                    AdapterNomalImage imageAdapter = new AdapterNomalImage(context ,childImgList ,mImageWidHei) ;
                    holder.mPictureResendLay.setAdapter(imageAdapter);
                }
            }else{
                holder.mCommentResendTimeTv.setVisibility(View.GONE);
                holder.mPictureResendLay.setVisibility(View.GONE);
                holder.mCommentResendTv.setVisibility(View.GONE);
            }


        }

        return view;
    }

    private static class GcHolder{
        private ImageView mIv ;
        private TextView mNameTv ;
        private TextView mTimeTv ;
        private TextView mSpecTv ;
        private TextView mCommentTv ;
        private RecyclerView mPictureLay ;
        private StarView mGoodStarView ;
        private StarView mDriverStarView ;
        private StarView mServerStarView ;

        //追评
        private TextView mCommentResendTimeTv ;
        private TextView mCommentResendTv ;
        private RecyclerView mPictureResendLay ;

        public GcHolder(View view,Context context) {

            mIv = view.findViewById(R.id.adapter_good_comment_iv) ;
            mNameTv = view.findViewById(R.id.adapter_good_comment_name_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_good_comment_time_tv) ;
            mSpecTv = view.findViewById(R.id.adapter_good_comment_spec_tv) ;
            mCommentTv = view.findViewById(R.id.adapter_good_comment_content_tv) ;
            mPictureLay = view.findViewById(R.id.adapter_good_comment_picture_rv) ;
            mGoodStarView = view.findViewById(R.id.adapter_good_comment_good_star_view) ;
            mDriverStarView = view.findViewById(R.id.adapter_good_comment_driver_star_view) ;
            mServerStarView = view.findViewById(R.id.adapter_good_comment_server_star_view) ;
            mGoodStarView.setShowDesc(true);
            mDriverStarView.setShowDesc(true);
            mServerStarView.setShowDesc(true);

            mCommentResendTimeTv = view.findViewById(R.id.adapter_good_comment_content_resend_time_tv) ;
            mCommentResendTv = view.findViewById(R.id.adapter_good_comment_content_resend_tv) ;
            mPictureResendLay = view.findViewById(R.id.adapter_good_comment_picture_resend_rv) ;

            int widHei = ResourceUtils.dip2px(context ,13) ;
            mGoodStarView.setWidHei(widHei,13);
            mDriverStarView.setWidHei(widHei,13);
            mServerStarView.setWidHei(widHei,13);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL) ;
            mPictureLay.setLayoutManager(layoutManager) ;
            mPictureLay.setFocusable(false);
            mPictureLay.setNestedScrollingEnabled(false);

            LinearLayoutManager layoutManager2 = new LinearLayoutManager(context) ;
            layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL) ;
            mPictureResendLay.setLayoutManager(layoutManager2) ;
            mPictureResendLay.setFocusable(false);
            mPictureResendLay.setNestedScrollingEnabled(false);
        }
    }
}