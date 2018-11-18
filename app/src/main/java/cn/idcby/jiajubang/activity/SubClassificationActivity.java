package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.SubClass;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created by Administrator on 2018-04-18.
 */

public class SubClassificationActivity extends BaseActivity {
    public static final int SUBS_TYPE_UNUSE = 4 ;
    public static final int SUBS_TYPE_NEED = 7 ;


    private List<SubClass> mSubClass = new ArrayList<>() ;
    private AdapterSubClassList mAdapter ;


    private void getSubClassList() {
        List<SubClass> chooseSub = (List<SubClass>) getIntent().getSerializableExtra(SkipUtils.INTENT_SUBCLASS);

        SubClass subClass1=new SubClass();
        subClass1.setCoulmn(SUBS_TYPE_UNUSE);
        subClass1.setUserTakeCoulmnList("闲置");
        mSubClass.add(subClass1);
        SubClass subClass2=new SubClass();
        subClass2.setCoulmn(SUBS_TYPE_NEED);
        subClass2.setUserTakeCoulmnList("需求");
        mSubClass.add(subClass2);

        if(chooseSub != null && chooseSub.size() > 0){
            for(SubClass subClass : mSubClass){
                int coulmn = subClass.getCoulmn() ;
                for(SubClass cSubClass : mSubClass){
                    int cCoulmn = cSubClass.getCoulmn() ;
                    if(cCoulmn == coulmn){
                        subClass.setSelected(true);
                        break;
                    }
                }
            }
        }

        mAdapter.notifyDataSetChanged() ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_subscription_choose;
    }

    @Override
    public void initView() {
        TextView rightTv = findViewById(R.id.acti_my_subscription_cate_right_tv) ;
        rightTv.setOnClickListener(this);

        ListView lv = findViewById(R.id.acti_my_subscription_cate_list_lv) ;
        mAdapter = new AdapterSubClassList() ;
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SubClass subClass = mSubClass.get(i) ;
                subClass.setSelected(!subClass.isSelected());
                mAdapter.notifyDataSetChanged() ;
            }
        });
    }

    @Override
    public void initData() {
        getSubClassList() ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_my_subscription_cate_right_tv == view.getId()){
            List<SubClass> subClass = new ArrayList<>() ;

            for(SubClass subs : mSubClass){
                if(subs.isSelected()){
                    subClass.add(subs) ;
                }
            }
            Intent reIt = new Intent() ;
            reIt.putExtra(SkipUtils.INTENT_SUBCLASS ,(ArrayList)subClass) ;
            setResult(RESULT_OK , reIt);
            finish() ;
        }
    }

    private class AdapterSubClassList extends BaseAdapter {
        @Override
        public int getCount() {
            return null == mSubClass ? 0 : mSubClass.size() ;
        }

        @Override
        public Object getItem(int position) {
            return mSubClass.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView=LayoutInflater.from(mContext).inflate(R.layout.adapter_choose_subscription_category,parent ,false);
                holder=new ViewHolder();
                holder.text=convertView.findViewById(R.id.adapter_choose_subscription_category_name_tv);
                holder.checkIv=convertView.findViewById(R.id.adapter_choose_subscription_category_check_iv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubClass subClass = mSubClass.get(position) ;
            holder.text.setText(subClass.getUserTakeCoulmnList());
            holder.checkIv.setImageDrawable(mContext.getResources().getDrawable(subClass.isSelected()
                    ? R.mipmap.ic_check_checked_blue
                    : R.mipmap.ic_check_nomal));

            return convertView;
        }
         class ViewHolder{
             ImageView checkIv;
             TextView text;
        }
    }
}
