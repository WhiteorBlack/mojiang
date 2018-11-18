package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.MyJobsFragment;
import cn.idcby.jiajubang.fragment.MyResumeBuyFragment;
import cn.idcby.jiajubang.fragment.MyResumeFragment;

/**
 * 行业招聘--我的
 * Created on 2018/5/19.
 * 2018-05-30 16:42:11
 * 添加我购买的简历选项卡，目前是  简历管理、招聘管理、购买的简历 3个
 */

public class MyCenterJobsActivity extends BaseActivity {
    private TextView mRightTv ;
    private TextView mTypeResumeTv;
    private TextView mTypeJobTv;
    private TextView mTypeBuyResumeTv;

    private int mType = JOBS_TYPE_RESUME ;//1 发布的简历 2 发布的招聘  3购买的简历
    private static final int JOBS_TYPE_RESUME = 1 ;
    private static final int JOBS_TYPE_JOBS = 2 ;
    private static final int JOBS_TYPE_RESUME_BUY = 3 ;

    private FragmentManager mManager ;
    private MyResumeFragment mResumeFrag ;
    private MyJobsFragment mJobFrag ;
    private MyResumeBuyFragment mResumeBuyFrag ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_center_jobs;
    }

    @Override
    public void initView() {
        mRightTv = findViewById(R.id.acti_my_center_jobs_right_tv) ;
        mTypeResumeTv = findViewById(R.id.acti_my_center_type_left_tv) ;
        mTypeJobTv = findViewById(R.id.acti_my_center_type_middle_tv) ;
        mTypeBuyResumeTv = findViewById(R.id.acti_my_center_type_right_tv) ;
        mTypeBuyResumeTv.setOnClickListener(this);
        mTypeResumeTv.setOnClickListener(this);
        mTypeJobTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);

        initResumeJob() ;
    }

    private void initResumeJob(){
        mResumeFrag = new MyResumeFragment() ;
        mJobFrag = new MyJobsFragment() ;
        mResumeBuyFrag = new MyResumeBuyFragment() ;

        mManager = getSupportFragmentManager() ;
        mManager.beginTransaction()
                .add(R.id.acti_my_center_job_content_lay ,mResumeFrag)
                .add(R.id.acti_my_center_job_content_lay ,mJobFrag)
                .add(R.id.acti_my_center_job_content_lay ,mResumeBuyFrag)
                .commit() ;

        mManager.beginTransaction().hide(mJobFrag).hide(mResumeBuyFrag).commit() ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_center_type_left_tv == vId){
            changeSendType(JOBS_TYPE_RESUME) ;
        }else if(R.id.acti_my_center_type_middle_tv == vId){
            changeSendType(JOBS_TYPE_JOBS) ;
        }else if(R.id.acti_my_center_type_right_tv == vId){
            changeSendType(JOBS_TYPE_RESUME_BUY) ;
        }else if(R.id.acti_my_center_jobs_right_tv == vId){
            Intent toCtIt = new Intent() ;
            if(JOBS_TYPE_RESUME == mType){
                toCtIt.setClass(mContext ,CreateResumeActivity.class) ;
            }else if(JOBS_TYPE_JOBS == mType){
                toCtIt.setClass(mContext ,CreateZhaopinActivity.class) ;
            }else{
                toCtIt.setClass(mContext ,ResumeBuyActivity.class) ;
            }
            startActivityForResult(toCtIt ,1000) ;
        }
    }


    /**
     *  切换类型
     *  @param type type
     */
    private void changeSendType(int type){
        if(type == mType){
            return ;
        }

        switch (mType){
            case JOBS_TYPE_RESUME:
                mTypeResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
                mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case JOBS_TYPE_JOBS:
                mTypeJobTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
                mTypeJobTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case JOBS_TYPE_RESUME_BUY:
                mTypeBuyResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
                mTypeBuyResumeTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            default:
                break;
        }

        switch (type){
            case JOBS_TYPE_RESUME:
                mRightTv.setText("创建");
                mTypeResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
                mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_white)) ;
                break;
            case JOBS_TYPE_JOBS:
                mRightTv.setText("发布");
                mTypeJobTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
                mTypeJobTv.setTextColor(getResources().getColor(R.color.color_white)) ;
                break;
            case JOBS_TYPE_RESUME_BUY:
                mRightTv.setText("购买");
                mTypeBuyResumeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
                mTypeBuyResumeTv.setTextColor(getResources().getColor(R.color.color_white)) ;
                break;
            default:
                break;
        }

        mType = type ;

        switch (mType){
            case JOBS_TYPE_RESUME:
                mManager.beginTransaction()
                        .show(mResumeFrag)
                        .hide(mJobFrag)
                        .hide(mResumeBuyFrag)
                        .commit() ;
                break;
            case JOBS_TYPE_JOBS:
                mManager.beginTransaction()
                        .show(mJobFrag)
                        .hide(mResumeFrag)
                        .hide(mResumeBuyFrag)
                        .commit() ;
                break;
            case JOBS_TYPE_RESUME_BUY:
                mManager.beginTransaction()
                        .show(mResumeBuyFrag)
                        .hide(mJobFrag)
                        .hide(mResumeFrag)
                        .commit() ;
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode){
                switch (mType){
                    case JOBS_TYPE_RESUME:
                        mResumeFrag.refreshList() ;
                        break;
                    case JOBS_TYPE_JOBS:
                        mJobFrag.refreshList() ;
                        break;
                    case JOBS_TYPE_RESUME_BUY:
                        mResumeBuyFrag.refreshList() ;
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
