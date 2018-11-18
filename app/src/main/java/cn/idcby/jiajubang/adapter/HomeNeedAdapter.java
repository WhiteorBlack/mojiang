package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.StringUtils;

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

public class HomeNeedAdapter extends BaseAdapter {
    private Context context ;
    private List<NeedsList> mData ;

    public HomeNeedAdapter(Context context, List<NeedsList> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.view_item_for_home_need, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NeedsList info = mData.get(position) ;
        if(info != null){
            final String mNeedId = info.NeedId ;
            int typeId = info.TypeId ;
            String title = info.NeedTitle ;
            String endTime = info.ReleaseTime ;
            String location = info.Position ;

            if(1 == typeId){
                holder.tvType.setText("需求") ;
                holder.tvType.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_xq_bg)) ;
            }else if(2 == typeId){
                holder.tvType.setText("招标") ;
                holder.tvType.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.round_needs_zb_bg)) ;
            }

            holder.tvTitle.setText(StringUtils.convertNull(title));
            holder.tvTime.setText(StringUtils.convertNull(endTime));
            holder.tvAddress.setText(StringUtils.convertNull(location));

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent toNdIt = new Intent(context , NeedsDetailsActivity.class) ;
//                    toNdIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
//                    context.startActivity(toNdIt);
//                }
//            });
        }

        return convertView;
    }

    public class ViewHolder {
        private View view;
        private TextView tvType;
        private TextView tvTitle;
        private TextView tvTime;
        private TextView tvAddress;


        public ViewHolder(View view) {
            this.view = view;
            findView(view);
        }

        private void findView(View view) {
            tvType = view.findViewById(R.id.tv_type);
            tvTime = view.findViewById(R.id.tv_time);
            tvAddress = view.findViewById(R.id.tv_address);
            tvTitle = view.findViewById(R.id.tv_title);
        }
    }

}
