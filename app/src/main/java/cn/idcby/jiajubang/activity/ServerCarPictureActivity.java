package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 货车照片
 * Created on 2018/7/25.
 */

public class ServerCarPictureActivity extends BaseActivity {
    private String mForegroundImgUrl ;
    private String mBackgroundImgUrl ;
    private List<String> mPicList = new ArrayList<>(2) ;

    public static void launch(Context context ,String foreground ,String background){
        Intent toPiIt = new Intent(context ,ServerCarPictureActivity.class) ;
        toPiIt.putExtra("foregroundUrl" ,foreground) ;
        toPiIt.putExtra("backgroundUrl" ,background) ;
        context.startActivity(toPiIt) ;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_server_car_picture ;
    }

    @Override
    public void initView() {
        super.initView();

        mForegroundImgUrl = getIntent().getStringExtra("foregroundUrl") ;
        mBackgroundImgUrl = getIntent().getStringExtra("backgroundUrl") ;

        mPicList.add(mForegroundImgUrl) ;

        ImageView mForegroundIv = findViewById(R.id.acti_server_car_pic_foreground_iv) ;
        ImageView mBackgroundIv = findViewById(R.id.acti_server_car_pic_background_iv) ;

        mForegroundIv.setOnClickListener(this);
        mBackgroundIv.setOnClickListener(this);


        GlideUtils.loader(StringUtils.convertNull(mForegroundImgUrl),mForegroundIv) ;
        GlideUtils.loader(StringUtils.convertNull(mBackgroundImgUrl),mBackgroundIv) ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

        int vId = view.getId() ;

        if(R.id.acti_server_car_pic_foreground_iv == vId){
            SkipUtils.toImageShowActivity(mActivity ,mForegroundImgUrl,0);
        }else if(R.id.acti_server_car_pic_background_iv == vId){
            SkipUtils.toImageShowActivity(mActivity ,mBackgroundImgUrl,1);
        }
    }
}
