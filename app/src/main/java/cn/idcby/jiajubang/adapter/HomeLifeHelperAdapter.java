package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.HomeServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.StarView;

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
 * Created by mrrlb on 2018/2/6.
 */

public class HomeLifeHelperAdapter extends RecyclerView.Adapter<HomeLifeHelperAdapter.ViewHolder> {
    private Context mContext;
    private List<HomeServiceList> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;

    public HomeLifeHelperAdapter(Context mContext, List<HomeServiceList> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
    }

    @Override
    public ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.view_item_for_home_life_helper, null);
        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HomeServiceList info = mData.get(position) ;
        if(info != null){
            String imgUrl = info.getHeadIcon() ;
            String name = info.getNickName() ;
            String count = info.getSingleAmount() ;

            holder.nameTv.setText(StringUtils.convertNull(name));
            holder.countTv.setText("接单：" + StringUtils.convertNull(count));
            GlideUtils.loaderUser(imgUrl, holder.headIv);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView headIv ;
        private TextView nameTv ;
        private TextView countTv ;
        private StarView starView ;

        public ViewHolder(View itemView , final RvItemViewClickListener clickListener) {
            super(itemView);

            headIv = itemView.findViewById(R.id.item_for_home_life_helper_head_iv) ;
            nameTv = itemView.findViewById(R.id.item_for_home_life_helper_name_tv) ;
            countTv = itemView.findViewById(R.id.item_for_home_life_helper_count_tv) ;
            starView = itemView.findViewById(R.id.item_for_home_life_helper_star_view) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getAdapterPosition()) ;
                    }
                }
            });

//            headIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(clickListener != null){
//                        clickListener.onItemClickListener(1 ,getAdapterPosition()) ;
//                    }
//                }
//            });
        }
    }
}
