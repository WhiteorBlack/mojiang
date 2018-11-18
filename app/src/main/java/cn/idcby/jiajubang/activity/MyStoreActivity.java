package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/04/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyStoreActivity extends BaseActivity{
    private TextView mOpenStoreTV;
    @Override
    public int getLayoutID() {
        return R.layout.activity_my_store;
    }

    @Override
    public void initView() {
mOpenStoreTV=(TextView)findViewById(R.id.acti_mystore_right_tv);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
mOpenStoreTV.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
if (view.getId()==R.id.acti_mystore_right_tv){
    goNextActivity(OpenStoreActivity.class);
}
    }
}
