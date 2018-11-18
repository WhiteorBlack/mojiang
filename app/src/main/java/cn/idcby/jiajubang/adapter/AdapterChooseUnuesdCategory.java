package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 *  选择分类
 * Created on 2018/3/30.
 */

public class AdapterChooseUnuesdCategory extends BaseAdapter {
    private Context context ;
    private List<UnusedCategory> mDataList ;
    private boolean mIsHasChild = false ;
    private boolean mIsMoreCheck = false ;

    public AdapterChooseUnuesdCategory(Context context,boolean isHasChild ,boolean isMoreCheck, List<UnusedCategory> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.mIsHasChild = isHasChild;
        this.mIsMoreCheck = isMoreCheck;
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
        CrHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_good_category , viewGroup , false) ;
            holder = new CrHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (CrHolder) view.getTag();
        }

        UnusedCategory info = mDataList.get(i) ;
        if(info != null){
            String name = info.CategoryTitle ;

            holder.mNameTv.setText(StringUtils.convertNull(name));
            holder.mMoreIv.setVisibility(mIsHasChild ? View.VISIBLE : View.INVISIBLE);
            holder.mCheckIv.setVisibility(!mIsMoreCheck || mIsHasChild ? View.GONE : View.VISIBLE);
            if(mIsMoreCheck && !mIsHasChild){
                holder.mCheckIv.setImageDrawable(context.getResources().getDrawable(!info.isSelected()
                        ? R.mipmap.ic_check_nomal : R.mipmap.ic_check_checked_blue));
            }

            if(mIsHasChild){
                List<UnusedCategory> selectedCate = info.getSelectedCategory() ;
                if(selectedCate != null && selectedCate.size() > 0){
                    String cateName = "" ;
                    for(UnusedCategory category : selectedCate){
                        cateName += (category.getCategoryTitle() + ",") ;
                    }
                    if(cateName.length() > 1){
                        cateName =cateName.substring(0,cateName.length() - 1) ;
                    }
                    holder.mSelectedTv.setText(cateName) ;
                }else{
                    holder.mSelectedTv.setText("") ;
                }
            }
        }

        return view;
    }


    private static class CrHolder{
        private TextView mNameTv ;
        private TextView mSelectedTv ;
        private ImageView mMoreIv ;
        private ImageView mCheckIv ;

        public CrHolder(View view) {
            mNameTv = view.findViewById(R.id.adapter_choose_good_category_name_tv) ;
            mSelectedTv = view.findViewById(R.id.adapter_choose_good_category_choose_tv) ;
            mMoreIv = view.findViewById(R.id.adapter_choose_good_category_more_iv) ;
            mCheckIv = view.findViewById(R.id.adapter_choose_good_category_check_iv) ;
        }
    }

}
