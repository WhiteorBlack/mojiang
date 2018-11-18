package cn.idcby.jiajubang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mrrlb on 2016/9/22.
 */
public class MyCommonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MyItemClickListener mListener;

    public interface MyItemClickListener {
        public void onItemClick(View view, int position);
    }


    public MyCommonViewHolder(View itemView, MyItemClickListener listener) {
        super(itemView);
        this.mListener = listener;

        itemView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }


}
