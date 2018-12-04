package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.PublishClass;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.PublishClassAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/2.
 */

public class PublishActivity extends BaseActivity {
    private RecyclerView rvList;
    private PublishClassAdapter adapter;
    private List<PublishClass> classList = new ArrayList<>();

    @Override
    public int getLayoutID() {
        return R.layout.activity_publish;
    }

    @Override
    public void initView() {
        overridePendingTransition(R.anim.push_bottom_in, 0);

        View cjzgLay = findViewById(R.id.acti_publish_cjzg_lay);
        View fbjlLay = findViewById(R.id.acti_publish_fbjl_lay);
        View yjzpLay = findViewById(R.id.acti_publish_hyzp_lay);
        View azdtLay = findViewById(R.id.acti_publish_azdt_lay);
        View hyxzLay = findViewById(R.id.acti_publish_hyxz_lay);
        View hyfwLay = findViewById(R.id.acti_publish_hyfw_lay);
        View hyxqLay = findViewById(R.id.acti_publish_hyxq_lay);
        View hywdLay = findViewById(R.id.acti_publish_hywd_lay);
        View closeLay = findViewById(R.id.acti_publish_close_lay);

        cjzgLay.setOnClickListener(this);
        fbjlLay.setOnClickListener(this);
        yjzpLay.setOnClickListener(this);
        azdtLay.setOnClickListener(this);
        hyxzLay.setOnClickListener(this);
        hyfwLay.setOnClickListener(this);
        hyxqLay.setOnClickListener(this);
        hywdLay.setOnClickListener(this);
        closeLay.setOnClickListener(this);

        adapter = new PublishClassAdapter(R.layout.item_publish_class);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
        rvList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapters, View view, int position) {
                PublishClass publishClass=adapter.getData().get(position);
                switch (publishClass.getPos()){
                    case 0:

                        break;
                    case 1:
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mActivity, 1002);
                        } else {
                            Intent toCrIt = new Intent(mContext, CreateResumeActivity.class);
                            startActivity(toCrIt);
                        }
                        break;
                    case 2:
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mActivity, 1003);
                        } else {
                            Intent toCrIt = new Intent(mContext, CreateZhaopinActivity.class);
                            startActivity(toCrIt);
                        }
                        break;
                    case 3:
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mActivity, 1005);
                        } else {
                            Intent toCtIt = new Intent(mContext, UnusedSendActivity.class);
                            startActivity(toCtIt);
                        }
                        break;
                    case 4:

                        break;
                    case 5:
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mActivity, 1008);
                        } else {
                            Intent toCtIt = new Intent(mContext, QuestionCreateActivity.class);
                            startActivity(toCtIt);
                        }
                        break;
                    case 6:
                        if (LoginHelper.isNotLogin(mContext)) {
                            SkipUtils.toLoginActivityForResult(mActivity, 1007);
                        } else {
                            Intent toCtIt = new Intent(mContext, NeedsSendActivity.class);
                            startActivity(toCtIt);
                        }
                        break;
                    case 7:
                        SkipUtils.toSendCircleActivity(mContext);
                        break;

                }
            }
        });
    }

    @Override
    public void initData() {
        adapter.setNewData(getClassData());
    }

    private List<PublishClass> getClassData() {
        classList.add(new PublishClass("发布产品", R.mipmap.icon_product, 0));
        classList.add(new PublishClass("发布简历", R.mipmap.icon_resume, 1));
        classList.add(new PublishClass("发布招聘", R.mipmap.icon_recurit, 2));
        classList.add(new PublishClass("发布闲置", R.mipmap.icon_unsued, 3));
        classList.add(new PublishClass("发布服务", R.mipmap.icon_fuwu, 4));
        classList.add(new PublishClass("发布问答", R.mipmap.icon_wenda, 5));
        classList.add(new PublishClass("发布需求", R.mipmap.icon_demand, 6));
        classList.add(new PublishClass("发布圈子", R.mipmap.icon_circle, 7));
        return classList;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_publish_close_lay == vId) {
            finish();
        } else if (R.id.acti_publish_cjzg_lay == vId) {//厂家直供
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1001);
            } else {
                Intent toCrIt = new Intent(mContext, OpenStoreActivity.class);
                startActivity(toCrIt);
            }
        } else if (R.id.acti_publish_fbjl_lay == vId) {//发布简历
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1002);
            } else {
                Intent toCrIt = new Intent(mContext, CreateResumeActivity.class);
                startActivity(toCrIt);
            }
        } else if (R.id.acti_publish_hyzp_lay == vId) {//行业招聘
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1003);
            } else {
                Intent toCrIt = new Intent(mContext, CreateZhaopinActivity.class);
                startActivity(toCrIt);
            }
        } else if (R.id.acti_publish_azdt_lay == vId) {//成为安装工
            if (MyApplication.isServerHidden()) {
                DialogUtils.showCustomViewDialog(mContext,
                        getResources().getString(R.string.server_hidden_tips)
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                return;
            }

            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1004);
            } else {
                Intent toCtIt = new Intent(mContext, InstallApplyActivity.class);
                startActivity(toCtIt);
            }
        } else if (R.id.acti_publish_hyxz_lay == vId) {//bg_index_hyxz
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1005);
            } else {
                Intent toCtIt = new Intent(mContext, UnusedSendActivity.class);
                startActivity(toCtIt);
            }
        } else if (R.id.acti_publish_hyfw_lay == vId) {//成为服务工
            if (MyApplication.isServerHidden()) {
                DialogUtils.showCustomViewDialog(mContext,
                        getResources().getString(R.string.server_hidden_tips)
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                return;
            }

            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1006);
            } else {
                Intent toCtIt = new Intent(mContext, ServerApplyActivity.class);
                startActivity(toCtIt);
            }
        } else if (R.id.acti_publish_hyxq_lay == vId) {//行业需求
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1007);
            } else {
                Intent toCtIt = new Intent(mContext, NeedsSendActivity.class);
                startActivity(toCtIt);
            }
        } else if (R.id.acti_publish_hywd_lay == vId) {//bg_index_hywd
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1008);
            } else {
                Intent toCtIt = new Intent(mContext, QuestionCreateActivity.class);
                startActivity(toCtIt);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.push_bottom_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            if (1001 == requestCode) {
                Intent toCrIt = new Intent(mContext, OpenStoreActivity.class);
                startActivity(toCrIt);
            } else if (1002 == requestCode) {
                Intent toCrIt = new Intent(mContext, CreateResumeActivity.class);
                startActivity(toCrIt);
            } else if (1003 == requestCode) {
                Intent toCrIt = new Intent(mContext, CreateZhaopinActivity.class);
                startActivity(toCrIt);
            } else if (1004 == requestCode) {
                Intent toCtIt = new Intent(mContext, InstallApplyActivity.class);
                startActivity(toCtIt);
            } else if (1005 == requestCode) {
                Intent toCtIt = new Intent(mContext, UnusedSendActivity.class);
                startActivity(toCtIt);
            } else if (1006 == requestCode) {
                Intent toCtIt = new Intent(mContext, ServerApplyActivity.class);
                startActivity(toCtIt);
            } else if (1007 == requestCode) {
                Intent toCtIt = new Intent(mContext, NeedsSendActivity.class);
                startActivity(toCtIt);
            } else if (1008 == requestCode) {
                Intent toCtIt = new Intent(mContext, QuestionCreateActivity.class);
                startActivity(toCtIt);
            }
        }

    }
}
