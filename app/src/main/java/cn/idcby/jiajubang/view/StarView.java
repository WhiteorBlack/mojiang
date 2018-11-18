package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.idcby.jiajubang.R;


/**
 * Created by Administrator on 2016/9/4.
 */
public class StarView extends LinearLayout implements View.OnClickListener {

    private ImageView mImgStar1;
    private ImageView mImgStar2;
    private ImageView mImgStar3;
    private ImageView mImgStar4;
    private ImageView mImgStar5;
    private TextView mStarDescTv ;

    private int mLevel = 0;
    private boolean isClick = false;
    private boolean isShowDesc = false;

    public StarView(Context context) {
        super(context);
        init();
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        initView();

    }


    public int getLevel() {
        return mLevel;
    }

    public void setCanClick(boolean isClick) {
        this.isClick = isClick ;
    }

    public void setShowDesc(boolean showDesc) {
        isShowDesc = showDesc;
    }

    private void initView() {

        View view = View.inflate(getContext(), R.layout.view_star, null);
        addView(view);
        mImgStar1 = view.findViewById(R.id.img_star_1);
        mImgStar2 = view.findViewById(R.id.img_star_2);
        mImgStar3 = view.findViewById(R.id.img_star_3);
        mImgStar4 = view.findViewById(R.id.img_star_4);
        mImgStar5 = view.findViewById(R.id.img_star_5);
        mStarDescTv = view.findViewById(R.id.start_desc_tv);

        mImgStar1.setOnClickListener(this);
        mImgStar2.setOnClickListener(this);
        mImgStar3.setOnClickListener(this);
        mImgStar4.setOnClickListener(this);
        mImgStar5.setOnClickListener(this);
    }

    public void setWidHei(int widHei,int textSize){
        mImgStar1.getLayoutParams().width = widHei ;
        mImgStar1.getLayoutParams().height = widHei ;
        mImgStar2.getLayoutParams().width = widHei ;
        mImgStar2.getLayoutParams().height = widHei ;
        mImgStar3.getLayoutParams().width = widHei ;
        mImgStar3.getLayoutParams().height = widHei ;
        mImgStar4.getLayoutParams().width = widHei ;
        mImgStar4.getLayoutParams().height = widHei ;
        mImgStar5.getLayoutParams().width = widHei ;
        mImgStar5.getLayoutParams().height = widHei ;
        mStarDescTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize) ;
    }

    public void setLevel(int level) {
        mLevel = level ;

        switch (level) {
            case 0:
                setStarShow(false, false, false, false, false);
                break;
            case 1:
                setStarShow(true, false, false, false, false);
                break;
            case 2:
                setStarShow(true, true, false, false, false);
                break;
            case 3:
                setStarShow(true, true, true, false, false);
                break;
            case 4:
                setStarShow(true, true, true, true, false);
                break;
            case 5:
                setStarShow(true, true, true, true, true);
                break;
        }

        updateStarDesc() ;
    }

    private void setStarShow(boolean flag1, boolean flag2, boolean flag3, boolean flag4,
                             boolean flag5) {
        mImgStar1.setSelected(flag1);
        mImgStar2.setSelected(flag2);
        mImgStar3.setSelected(flag3);
        mImgStar4.setSelected(flag4);
        mImgStar5.setSelected(flag5);
    }

    @Override
    public void onClick(View view) {
        if(!isClick){
            return ;
        }
        switch (view.getId()) {
            case R.id.img_star_1:
                showStar1();
                break;
            case R.id.img_star_2:
                showStar2();
                break;
            case R.id.img_star_3:
                showStar3();
                break;
            case R.id.img_star_4:
                showStar4();
                break;
            case R.id.img_star_5:
                showStar5();
                break;
        }
    }


    /***
     * 显示星星5
     */
    private void showStar5() {

        if (!mImgStar5.isSelected()) {
            mImgStar1.setSelected(true);
            mImgStar2.setSelected(true);
            mImgStar3.setSelected(true);
            mImgStar4.setSelected(true);
            mImgStar5.setSelected(true);
        }

        mLevel = 5;
        updateStarDesc() ;
    }

    /***
     * 显示星星4
     */
    private void showStar4() {

        if (mImgStar4.isSelected()) {

            mImgStar5.setSelected(false);

        } else {

            mImgStar1.setSelected(true);
            mImgStar2.setSelected(true);
            mImgStar3.setSelected(true);
            mImgStar4.setSelected(true);
        }
        mLevel = 4;
        updateStarDesc() ;
    }

    /***
     * 显示星星3
     */
    private void showStar3() {

        if (mImgStar3.isSelected()) {

            mImgStar5.setSelected(false);
            mImgStar4.setSelected(false);

        } else {
            mImgStar1.setSelected(true);
            mImgStar2.setSelected(true);
            mImgStar3.setSelected(true);
        }

        mLevel = 3;
        updateStarDesc() ;
    }

    /**
     * 显示星星2
     */
    private void showStar2() {

        if (mImgStar2.isSelected()) {

            mImgStar5.setSelected(false);
            mImgStar4.setSelected(false);
            mImgStar3.setSelected(false);


        } else {

            mImgStar1.setSelected(true);
            mImgStar2.setSelected(true);

        }

        mLevel = 2;
        updateStarDesc() ;
    }

    /***
     * 显示星星1
     */
    private void showStar1() {

        if (mImgStar1.isSelected()) {
            //被选中了
            mImgStar5.setSelected(false);
            mImgStar4.setSelected(false);
            mImgStar3.setSelected(false);
            mImgStar2.setSelected(false);


        } else {

            mImgStar1.setSelected(true);

        }
        mLevel = 1;
        updateStarDesc() ;
    }

    private void updateStarDesc(){
        mStarDescTv.setVisibility(isShowDesc ? VISIBLE : GONE);

        if(isShowDesc){
            String desc = "" ;
            switch (mLevel){
                case 1:
                    desc = "非常差" ;
                    break;
                case 2:
                    desc = "差" ;
                    break;
                case 3:
                    desc = "一般" ;
                    break;
                case 4:
                    desc = "好" ;
                    break;
                case 5:
                    desc = "非常好" ;
                    break;
                default:
                    break;
            }
            mStarDescTv.setText(desc) ;
        }
    }
}
