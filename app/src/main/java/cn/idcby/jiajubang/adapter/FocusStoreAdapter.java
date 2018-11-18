package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.FoStoreBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created  on 2018-04-26.
 */

public class FocusStoreAdapter extends RecyclerView.Adapter<FocusStoreAdapter.ViewHolder>{
    private List<FoStoreBean> listData;
    private Context mContext;
    private RvItemViewClickListener mClickListener ;

    public FocusStoreAdapter(Context mContext,RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        listData = new ArrayList<>();
        this.mClickListener = mClickListener ;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_focus_store, viewGroup, false);
        ViewHolder vh = new ViewHolder(view,mClickListener);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FoStoreBean storeBean = listData.get(position) ;
        if(storeBean != null){
            String name = storeBean.getName() ;
            String img = storeBean.getShopImg() ;
            String count = storeBean.getCount() ;

            GlideUtils.loader(img, viewHolder.mStorelogoIV);
            viewHolder.mStorenameTV.setText(name);
            viewHolder.mMonthsellTV.setText(count + "人关注");
        }

    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return listData.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mStorelogoIV;
        private TextView mStorenameTV;
        private TextView mMonthsellTV;
        private TextView mHelpvelueTV;
        private TextView mDeleteTV;

        public ViewHolder(View view,final RvItemViewClickListener mClickListener) {
            super(view);
            mStorelogoIV = (ImageView) view.findViewById(R.id.store_logo_iv);
            mStorenameTV = (TextView) view.findViewById(R.id.sotre_name_tv);
            mMonthsellTV = (TextView) view.findViewById(R.id.month_sell_tv);
            mHelpvelueTV = (TextView) view.findViewById(R.id.hlep_velue_tv);
            mDeleteTV = (TextView) view.findViewById(R.id.delete_store_tv);

            //将创建的View注册点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0,getAdapterPosition()) ;
                    }
                }
            });
            mDeleteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1,getAdapterPosition()) ;
                    }
                }
            });
            mDeleteTV.setTag(getLayoutPosition() +"");
            itemView.setTag(getLayoutPosition() +"");
        }
    }

    public void setDataRefresh(List<FoStoreBean> bean, boolean isClear) {
        if (isClear) {
            listData.clear();
        }
        listData.addAll(bean);
        notifyDataSetChanged();
    }

    public List<FoStoreBean> getListData(){
        return listData ;
    }

}
