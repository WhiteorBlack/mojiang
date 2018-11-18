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
import cn.idcby.jiajubang.Bean.BaseCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/5/10.
 * 通用分类展示样式
 */

public class AdapterNomalCategoryHori<T extends BaseCategory> extends RecyclerView.Adapter<AdapterNomalCategoryHori.NchHolder> {
    private Context mContext ;
    private List<T> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int mItemWidth = 0 ;
    private boolean mShowIcon = false ;

    public AdapterNomalCategoryHori(Context mContext, List<T> mDataList, RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }
    public AdapterNomalCategoryHori(Context mContext,boolean showIcon , List<T> mDataList, RvItemViewClickListener mClickListener) {
        this(mContext ,mDataList ,mClickListener) ;
        this.mShowIcon = showIcon;
    }

    public AdapterNomalCategoryHori(Context mContext, List<T> mDataList,float numberColum , RvItemViewClickListener mClickListener) {
        this(mContext,mDataList,mClickListener) ;
        if(numberColum > 0F){
            mItemWidth = (int) (ResourceUtils.getScreenWidth(mContext) / numberColum);
        }
    }
    public AdapterNomalCategoryHori(Context mContext,boolean showIcon, List<T> mDataList,float numberColum , RvItemViewClickListener mClickListener) {
        this(mContext,showIcon ,mDataList,mClickListener) ;
        if(numberColum > 0F){
            mItemWidth = (int) (ResourceUtils.getScreenWidth(mContext) / numberColum);
        }
    }

    @Override
    public NchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NchHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_nomal_category_item , parent , false)
                ,mClickListener ,mItemWidth);
    }

    @Override
    public void onBindViewHolder(NchHolder holder, int position) {
        BaseCategory info = mDataList.get(position) ;
        if(info != null){
            String name = info.getCategoryTitle() ;
            String imgUrl = mShowIcon ? info.getIconUrl() : info.getImgUrl() ;

            holder.mNameTv.setText(name);
            GlideUtils.loader(MyApplication.getInstance() ,imgUrl , holder.mIconIv) ;
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }


    static class NchHolder extends RecyclerView.ViewHolder{
        private ImageView mIconIv ;
        private TextView mNameTv ;

        public NchHolder(View view, final RvItemViewClickListener clickListener,int width){
            super(view);

            if(width > 0){
                view.getLayoutParams().width = width ;
            }

            this.mIconIv = view.findViewById(R.id.adapter_nomal_category_item_iv) ;
            this.mNameTv = view.findViewById(R.id.adapter_nomal_category_item_name_tv) ;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });
        }
    }


}
