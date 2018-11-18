package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.MyServerFragment;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 安服务--相册管理
 * Created on 2018/5/19.
 */

public class MyServerPictureEditActivity extends BaseActivity {

    public static void launch(Activity context ,boolean isInstall){
        Intent toEiIt = new Intent(context ,MyServerPictureEditActivity.class) ;
        toEiIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL,isInstall) ;
        context.startActivity(toEiIt);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_server_pic_edit;
    }

    @Override
    public void initView() {
        FragmentManager manager = getSupportFragmentManager() ;
        manager.beginTransaction().add(R.id.acti_my_server_pic_edit_content_lay
                , MyServerFragment.newInstance(getIntent()
                        .getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL ,true))).commit() ;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }
}
