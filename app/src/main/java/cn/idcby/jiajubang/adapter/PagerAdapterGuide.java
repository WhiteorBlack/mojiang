package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MainActivity;

/**
 * 首次启动导航
 * Created on 2018/6/6.
 */

public class PagerAdapterGuide extends PagerAdapter {
    private Activity context ;
    private int[] mDataList = new int[]{R.drawable.ic_start_nav_one
            ,R.drawable.ic_start_nav_two
            ,R.drawable.ic_start_nav_three
            ,R.drawable.ic_start_nav_four
            ,R.drawable.ic_start_nav_five} ;

    public PagerAdapterGuide(Activity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.length ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object ;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_first_guide_img ,container,false) ;
        ImageView iv = view.findViewById(R.id.adapter_first_guide_iv) ;
        TextView tv = view.findViewById(R.id.adapter_first_guide_tv) ;

        iv.setBackgroundDrawable(context.getResources().getDrawable(mDataList[position])) ;

        tv.setVisibility(mDataList.length == position + 1 ? View.VISIBLE : View.GONE);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMiIt = new Intent(context , MainActivity.class) ;
                context.startActivity(toMiIt) ;
                context.finish() ;
            }
        });

        container.addView(view) ;
        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
