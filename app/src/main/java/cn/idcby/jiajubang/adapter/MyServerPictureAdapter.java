package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;


/**
 * 我的服务--照片墙--选择图片展示
 * Created by mrrlb on 2017/3/9.
 */

public class MyServerPictureAdapter extends RecyclerView.Adapter<MyServerPictureAdapter.MyViewHolder> {
    private RecyclerViewClickListener myItemClickListener = null;
    private Context mContext;
    private List<String> data;
    private int itemWidHei = 0 ;
    private boolean mIsEdit = false ;

    public void setEditState(boolean edit){
        mIsEdit = edit ;
        notifyDataSetChanged() ;
    }

    public MyServerPictureAdapter(Context mContext, List<String> data,int itemWidHei , RecyclerViewClickListener myItemClickListener) {
        this.mContext = mContext;
        this.data = data;
        this.myItemClickListener = myItemClickListener;
        this.itemWidHei = itemWidHei;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_my_server_pic, null);
        return new MyViewHolder(view, myItemClickListener, itemWidHei);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String imgUrl = data.get(position) ;
        holder.iv_del.setVisibility((!mIsEdit || null == imgUrl) ? View.INVISIBLE : View.VISIBLE) ;
        GlideUtils.loader(MyApplication.getInstance()
                , data.get(position) , holder.imageView) ;
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View iv_del;

        public MyViewHolder(View itemView, final RecyclerViewClickListener listener , int width) {
            super(itemView);
            imageView = itemView.findViewById(R.id.adapter_my_server_pic_iv);
            iv_del = itemView.findViewById(R.id.adapter_my_server_pic_delete_iv);

            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onItemClickListener(1 ,getAdapterPosition());
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onItemClickListener(2 ,getAdapterPosition());
                    }
                }
            });

            if(width > 0){
                imageView.getLayoutParams().width = width ;
                imageView.getLayoutParams().height = width ;
            }
        }
    }
}
