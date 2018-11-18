package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodOrderCommentGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodOrderCommentSendActivity;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 商品评价
 * Created on 2018/9/19.
 */

public class AdapterGoodOrderCommentSend extends RecyclerView.Adapter<AdapterGoodOrderCommentSend.GocHolder> {
    private Activity context;
    private List<GoodOrderCommentGood> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int itemWidHei ;

    public AdapterGoodOrderCommentSend(Activity context, List<GoodOrderCommentGood> mDataList
            , RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.itemWidHei = (ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context , 15)* 2) / 5 ;
    }


    @Override
    public GocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GocHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_good_order_comment_send_good, parent, false));
    }

    @Override
    public void onBindViewHolder(final GocHolder holder, int position) {
        final int realPosition = position ;

        final GoodOrderCommentGood goodInfo = mDataList.get(position) ;
        if (goodInfo != null) {

            holder.mGoodNameTv.setText(goodInfo.getProductTitle()) ;
            holder.mStarDescTv.setText(goodInfo.getStarDesc()) ;

            int star = goodInfo.getStar() ;
            holder.mGoodStarOneIv.setSelected(star > 0);
            holder.mGoodStarTwoIv.setSelected(star > 1);
            holder.mGoodStarThreeIv.setSelected(star > 2);
            holder.mGoodStarFourIv.setSelected(star > 3);
            holder.mGoodStarFiveIv.setSelected(star > 4);

            holder.mCommentEv.setTag(goodInfo) ;
            holder.mCommentEv.clearFocus() ;

            holder.mCommentEv.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    GoodOrderCommentGood eCt = (GoodOrderCommentGood) holder.mCommentEv.getTag();
                    if(eCt != null){
                        eCt.setCommentContent(arg0+"") ;
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });

            holder.mCommentEv.setText(StringUtils.convertNull(goodInfo.getCommentContent())) ;

            final List<String> mAdapterImageList = goodInfo.getCommentImgList() ;
            holder.mImageRv.setLayoutManager(new GridLayoutManager(context, 5));

            ImageSelectorResultAdapter imageSelectorResultAdapter =
                    new ImageSelectorResultAdapter(context, mAdapterImageList
                    , itemWidHei, itemWidHei, GoodOrderCommentSendActivity.MAX_IMAGE_COUNT , new RecyclerViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){//删除
                        mAdapterImageList.remove(position) ;
                        notifyDataSetChanged() ;
                    }else if(1 == type){//原图
                        if(position < GoodOrderCommentSendActivity.MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1){
                            if(mClickListener != null){
                                mClickListener.onItemClickListener(0 ,realPosition);
                            }
                        }else{
                            SkipUtils.toImageShowActivity(context , mAdapterImageList ,position);
                        }
                    }
                }
                @Override
                public void onItemLongClickListener(int type, int position) {
                }
            });
            holder.mImageRv.setAdapter(imageSelectorResultAdapter);


            holder.mGoodStarOneIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodInfo.setStar(1) ;
                    notifyDataSetChanged() ;
                }
            });
            holder.mGoodStarTwoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodInfo.setStar(2) ;
                    notifyDataSetChanged() ;
                }
            });
            holder.mGoodStarThreeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodInfo.setStar(3) ;
                    notifyDataSetChanged() ;
                }
            });
            holder.mGoodStarFourIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodInfo.setStar(4) ;
                    notifyDataSetChanged() ;
                }
            });
            holder.mGoodStarFiveIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodInfo.setStar(5) ;
                    notifyDataSetChanged() ;
                }
            });

            GlideUtils.loaderGoodImage(goodInfo.getImgUrl() ,holder.mGoodIv) ;
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }


    static class GocHolder extends RecyclerView.ViewHolder {
        private ImageView mGoodIv;
        private TextView mGoodNameTv;
        private ImageView mGoodStarOneIv ;
        private ImageView mGoodStarTwoIv ;
        private ImageView mGoodStarThreeIv ;
        private ImageView mGoodStarFourIv ;
        private ImageView mGoodStarFiveIv ;
        private TextView mStarDescTv;
        private EditText mCommentEv;
        private RecyclerView mImageRv;

        public GocHolder(View itemView) {
            super(itemView);

            mGoodIv = itemView.findViewById(R.id.adapter_good_order_comment_iv);
            mGoodNameTv = itemView.findViewById(R.id.adapter_good_order_comment_name_tv);
            mGoodStarOneIv = itemView.findViewById(R.id.adapter_good_order_comment_star_one_iv);
            mGoodStarTwoIv = itemView.findViewById(R.id.adapter_good_order_comment_star_two_iv);
            mGoodStarThreeIv = itemView.findViewById(R.id.adapter_good_order_comment_star_three_iv);
            mGoodStarFourIv = itemView.findViewById(R.id.adapter_good_order_comment_star_four_iv);
            mGoodStarFiveIv = itemView.findViewById(R.id.adapter_good_order_comment_star_five_iv);
            mStarDescTv = itemView.findViewById(R.id.adapter_good_order_comment_star_desc_tv);
            mCommentEv = itemView.findViewById(R.id.adapter_good_order_comment_content_ev);
            mImageRv = itemView.findViewById(R.id.adapter_good_order_comment_image_rv);
        }
    }
}
