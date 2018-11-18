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
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;

/**
 * 我的发布--闲置
 * Created on 2018/4/28.
 */

public class AdapterMySendUnuse extends BaseAdapter {
    private Activity context ;
    private List<UnuseGoodList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterMySendUnuse(Activity context, List<UnuseGoodList> mDataList,RvItemViewClickListener mClickListener ) {
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        UnsHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_my_send_unuse_list ,viewGroup ,false) ;
            holder = new UnsHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (UnsHolder) view.getTag();
        }

        final int realPosition = position ;
        UnuseGoodList goodList = mDataList.get(position) ;
        if(goodList != null){
            String time = goodList.getCreateDate() ;
            String money = goodList.getShowPrice() ;
            String title = goodList.getTitle() ;
            String showContent = goodList.getTitle() ;

            holder.mTitleTv.setText(title);
            holder.mContentTv.setText(showContent);
            holder.mTimeTv.setText(StringUtils.convertDateToDay(time));
            holder.mMoneyTv.setText(money);
            holder.mUpdownTv.setText(goodList.isEnabledMark() ? "下架" : "上架") ;
            holder.mStateTv.setText(goodList.isEnabledMark() ? "展示中" : "已下架") ;
            holder.mStateTv.setBackgroundDrawable(context.getResources().getDrawable(goodList.isEnabledMark()
                    ? R.drawable.round_theme_small_bg
                    : R.drawable.round_orange_small_bg));

            LinearLayoutManager layoutManager = new LinearLayoutManager(context) ;
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mImageRv.setLayoutManager(layoutManager) ;

            RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(context
                    , ResourceUtils.dip2px(context ,5)
                    , context.getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST);
            if(holder.mImageRv.getItemDecorationCount() == 0){
                holder.mImageRv.addItemDecoration(itemDecoration);
            }

            AdapterUnuseGoodImage imageAdapter = new AdapterUnuseGoodImage(context ,goodList.getAlbumsList()) ;
            holder.mImageRv.setAdapter(imageAdapter);

            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,realPosition);
                    }
                }
            });
            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1,realPosition);
                    }
                }
            });
            holder.mRefreshTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(2,realPosition);
                    }
                }
            });
            holder.mUpdownTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(3,realPosition);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UnuseGoodList goodList = mDataList.get(realPosition) ;
                    if(goodList != null){
                        Intent toDtIt = new Intent(context ,UnuseGoodDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID,goodList.getProductID()) ;
                        context.startActivity(toDtIt) ;
                    }
                }
            });
        }

        return view;
    }
    //** /519

    private static class UnsHolder{
        private TextView mStateTv ;
        private TextView mTimeTv ;
        private TextView mMoneyTv ;
        private RecyclerView mImageRv ;
        private TextView mTitleTv ;
        private TextView mContentTv ;
        private TextView mEditTv;
        private TextView mDeleteTv;
        private TextView mRefreshTv;
        private TextView mUpdownTv;

        public UnsHolder(View itemView) {
            mStateTv = itemView.findViewById(R.id.adapter_my_send_unuse_state_tv) ;
            mTimeTv = itemView.findViewById(R.id.adapter_my_send_unuse_time_tv) ;
            mMoneyTv = itemView.findViewById(R.id.adapter_my_send_unuse_spec_money_tv) ;
            mImageRv = itemView.findViewById(R.id.adapter_my_send_unuse_spec_img_rv) ;
            mTitleTv = itemView.findViewById(R.id.adapter_my_send_unuse_spec_title_tv) ;
            mContentTv = itemView.findViewById(R.id.adapter_my_send_unuse_spec_content_tv) ;
            mEditTv = itemView.findViewById(R.id.adapter_my_send_unuse_edit_tv);
            mDeleteTv = itemView.findViewById(R.id.adapter_my_send_unuse_delete_tv);
            mRefreshTv = itemView.findViewById(R.id.adapter_my_send_unuse_refresh_tv);
            mUpdownTv = itemView.findViewById(R.id.adapter_my_send_unuse_updown_tv);

            mImageRv.setNestedScrollingEnabled(false);
        }
    }

}
