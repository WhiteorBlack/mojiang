package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.PhotoViewPager;


public class MyMultiImgShowActivity extends Activity {
    private PhotoViewPager viewPager;
    private List<String> imgList = new ArrayList<>();
    private PhotoView[] mImageView;
    private TextView numText;
    /**
     * 记录当前页卡
     */
    private int current = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_show_img);
        viewPager = findViewById(R.id.show_img_viewPager);
        numText = (TextView) findViewById(R.id.showimg_text);

        // 图片地址
        List<String> thumbList = getIntent().getStringArrayListExtra("photos") ;
        if(thumbList != null){
            imgList.addAll(thumbList);
        }
        current = getIntent().getIntExtra("position", 0);
        LogUtils.showLog("图片显示的界面的图片集合>>>" + imgList);

        mImageView = new PhotoView[imgList.size()];
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                PhotoView photoView = new PhotoView(MyMultiImgShowActivity.this);
                String path = imgList.get(position);

                GlideUtils.loaderNoDef(path , photoView);

                container.addView(photoView);
                mImageView[position] = photoView;
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                return photoView;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mImageView[position]);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mImageView.length;
            }
        });
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                current = arg0;
                numText.setText(arg0 + 1 + "/" + imgList.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        if(current < imgList.size()){
            //设置当前选中项
            viewPager.setCurrentItem(current, true);
            numText.setText(current + 1 + "/" + imgList.size());
        }
    }




    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.zoom_out);
    }




}
