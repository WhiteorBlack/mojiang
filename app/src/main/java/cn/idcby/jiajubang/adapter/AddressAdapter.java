package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.Address;
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
 * Created by mrrlb on 2018/2/10.
 */

public class AddressAdapter extends DefaultBaseAdapter<Address> {

    private boolean isShowArrow = true;

    public AddressAdapter(List<Address> datas, Context context) {
        super(datas, context);
    }
    public AddressAdapter(List<Address> datas, Context context ,boolean isShowArrow) {
        super(datas, context);
        this.isShowArrow = isShowArrow ;
    }

    public void setIsShowArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        ViewHolder holder = null;

        if (convertView == null) {
            view = View.inflate(context, R.layout.view_item_for_only_text_with_arrow, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Address address = datas.get(position);
        holder.tvDesc.setText(address.AreaName);
        holder.imgArrow.setVisibility(isShowArrow ? View.VISIBLE : View.GONE);

        return view;
    }

    public class ViewHolder {
        private View view;
        private TextView tvDesc;
        private ImageView imgArrow;

        public ViewHolder(View view) {
            this.view = view;
            findView(view);
        }

        private void findView(View view) {
            tvDesc = view.findViewById(R.id.tv_desc);
            imgArrow = view.findViewById(R.id.img_arrow);
        }

    }
}
