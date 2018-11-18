package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.NeedsDetailsActivity;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 我的收藏的需求
 * Created on 2018/3/29.
 */

public class AdapterMyCollectNeedsList extends BaseAdapter {
    private Activity mContext ;
    private List<NeedsList> mDataList ;
    private int mOffset = 0 ;

    public AdapterMyCollectNeedsList(Activity mContext, List<NeedsList> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mOffset = ResourceUtils.dip2px(mContext , 15) + ResourceUtils.dip2px(mContext , 5) * 2;
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
        NdHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_collection_needs_list , viewGroup , false) ;
            holder = new NdHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (NdHolder) view.getTag();
        }

        NeedsList info = mDataList.get(i) ;
        if(info != null){
            final String needId = info.getNeedId() ;
            int typeId = info.TypeId ;
            String title = info.NeedTitle ;
            String endTime = info.EndTime ;
            String content = info.NeedExplain ;
            String location = info.Position ;
            String suppoerCount = "" + info.LikeNumber ;
            String commentCount = "" + info.CommentNumber ;

            if(1 == typeId){
                holder.mTypeTv.setText("需求") ;
                holder.mTypeTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.round_needs_xq_bg)) ;
            }else if(2 == typeId){
                holder.mTypeTv.setText("招标") ;
                holder.mTypeTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.round_needs_zb_bg)) ;
            }

            holder.mTitleTv.setText(StringUtils.convertNull(title));
            holder.mTimeTv.setText(StringUtils.convertNull(endTime) + "结束");
            holder.mContentTv.setText(StringUtils.convertNull(content));
            holder.mLocationTv.setText(StringUtils.convertNull(location));
            holder.mSupportTv.setText(suppoerCount);
            holder.mCommentTv.setText(commentCount);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mPicLay.setLayoutManager(layoutManager) ;
            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                    ,ResourceUtils.dip2px(mContext ,5)
                    , mContext.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mPicLay.getItemDecorationCount() == 0){
                holder.mPicLay.addItemDecoration(itemDecoration);
            }
            AdapterNomalImage imageAdapter = new AdapterNomalImage(mContext ,info.getAlbumsList(),mOffset,3.3F) ;
            holder.mPicLay.setAdapter(imageAdapter);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toNdIt = new Intent(mContext , NeedsDetailsActivity.class) ;
                    toNdIt.putExtra(SkipUtils.INTENT_NEEDS_ID , needId) ;
                    mContext.startActivity(toNdIt);
                }
            });
        }

        return view;
    }

    private static class NdHolder {
        private TextView mTypeTv ;
        private TextView mTitleTv ;
        private TextView mTimeTv ;
        private TextView mContentTv ;
        private RecyclerView mPicLay ;
        private TextView mLocationTv ;
        private TextView mSupportTv ;
        private TextView mCommentTv ;

        public NdHolder(View view) {
            mTypeTv = view.findViewById(R.id.adapter_collection_needs_list_type_tv) ;
            mTitleTv = view.findViewById(R.id.adapter_collection_needs_list_title_tv) ;
            mTimeTv = view.findViewById(R.id.adapter_collection_needs_list_endTime_tv) ;
            mContentTv = view.findViewById(R.id.adapter_collection_needs_list_content_tv) ;
            mLocationTv = view.findViewById(R.id.adapter_collection_needs_list_location_tv) ;
            mSupportTv = view.findViewById(R.id.adapter_collection_needs_list_support_tv) ;
            mCommentTv = view.findViewById(R.id.adapter_collection_needs_list_comment_tv) ;
            mPicLay = view.findViewById(R.id.adapter_collection_needs_list_pic_lay) ;
        }
    }
}
