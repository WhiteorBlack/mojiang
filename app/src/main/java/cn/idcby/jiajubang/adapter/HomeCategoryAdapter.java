package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.jiajubang.R;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/5.
 */

public class HomeCategoryAdapter extends BaseAdapter {
    private Context mContext ;
    private int[] icons = {
            R.mipmap.hyzx, R.mipmap.cjzg, R.mipmap.hyzp, R.mipmap.hyxz,
            R.mipmap.azfw, R.mipmap.hyfw, R.mipmap.zxxq, R.mipmap.hywd};

    private String[] titles = {
            "行业资讯", "bg_index_cjzg", "bg_index_hyzp", "bg_index_hyxz",
            "安装服务", "行业服务", "最新需求", "bg_index_hywd"};

    public HomeCategoryAdapter(Context context) {
         this.mContext = context ;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        ViewHolder holder = null;
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.view_item_for_home_categoty, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.imgCategory.setImageResource(icons[position]);
        holder.tvTitle.setText(titles[position]);

        return view;
    }

    public class ViewHolder {
        private View view;
        private TextView tvTitle;
        private ImageView imgCategory;

        public ViewHolder(View view) {
            this.view = view;
            findView(view);
        }

        private void findView(View view) {
            tvTitle = view.findViewById(R.id.tv_title);
            imgCategory = view.findViewById(R.id.img_category);
        }
    }

}
