package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;

/**
 * 字典项adapter
 * Created on 2018/4/2.
 */

public class AdapterWordTypeList extends BaseAdapter {
    private Context context ;
    private List<WordType> mDataList ;
    private boolean mIsMoreCheck ;
    private RvItemViewClickListener mClickListener ;

    public AdapterWordTypeList(Context context,boolean mIsMoreCheck, List<WordType> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.mDataList = mDataList;
        this.mIsMoreCheck = mIsMoreCheck;
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

        WlHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_word_type , viewGroup ,false) ;
            holder = new WlHolder() ;
            holder.mNameTv = view.findViewById(R.id.adapter_choose_word_type_name_tv) ;
            holder.mChooseIv = view.findViewById(R.id.adapter_choose_word_type_iv) ;

            view.setTag(holder);
        }else{
            holder = (WlHolder) view.getTag();
        }

        WordType info = mDataList.get(i) ;
        if(info != null){
            String name = info.getItemName() ;
            final boolean isSelected = info.isSelected ;

            holder.mChooseIv.setVisibility(mIsMoreCheck ? View.VISIBLE : View.INVISIBLE);
            holder.mNameTv.setText(name);
            holder.mChooseIv.setImageDrawable(context.getResources().getDrawable(isSelected
                    ? R.mipmap.ic_check_checked_blue
                    : R.mipmap.ic_check_nomal));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition) ;
                    }

                }
            });
        }

        return view;
    }

    private static class WlHolder{
        private TextView mNameTv ;
        private ImageView mChooseIv ;
    }
}
