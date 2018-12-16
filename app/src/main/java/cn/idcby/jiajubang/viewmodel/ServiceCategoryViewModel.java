package cn.idcby.jiajubang.viewmodel;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.activity.BaseBindActivity;
import cn.idcby.jiajubang.adapter.ServiceClassAdapter;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;

public class ServiceCategoryViewModel implements ViewModel {
    private BaseBindActivity activity;
    private ServiceClassAdapter firstAdapter;
    private ServiceClassAdapter secondeAdapter;
    private Map<String, List<ServerCategory>> cateMap = new HashMap<>();
    private LoadingDialog loadingDialog;
    private ArrayList<ServerCategory> selectList = new ArrayList<>();

    public ServiceCategoryViewModel(BaseBindActivity activity, ServiceClassAdapter firstAdapter, ServiceClassAdapter secondeAdapter) {
        this.activity = activity;
        this.firstAdapter = firstAdapter;
        this.secondeAdapter = secondeAdapter;
    }

    public void commitData(){

    }

    public RecyclerView.OnItemTouchListener firstListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                ServerCategory category = firstAdapter.getData().get(position);
                for (int i = 0; i < firstAdapter.getData().size(); i++) {
                    firstAdapter.getData().get(i).setSelected(i == position);
                }
                if (cateMap.get(category.getServiceCategoryID()) == null) {
                    getSecond(category.getServiceCategoryID());
                } else {
                    secondeAdapter.setNewData(cateMap.get(category.getServiceCategoryID()));
                }
            }
        };
    }

    public RecyclerView.OnItemTouchListener secondListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                ServerCategory category = secondeAdapter.getData().get(position);
                category.setSelected(!category.isSelected());
            }
        };
    }


    /**
     * 服务分类
     */
    public void getFirst() {
        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("id", "2");
        paramMap.put("Layer", "1");
        NetUtils.getDataFromServerByPost(activity, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getServerCategory", activity, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        firstAdapter.setNewData(bean);
                        if (bean != null && bean.size() > 0) {
                            getSecond(bean.get(0).getServiceCategoryID());
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 二级分类
     *
     * @param code
     */
    private void getSecond(String code) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(activity);
        }
        loadingDialog.show();
        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("id", "2");
        paramMap.put("Layer", "2");
        paramMap.put("code", code);
        NetUtils.getDataFromServerByPost(activity, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getServerCategory", activity, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        secondeAdapter.setNewData(bean);
                        cateMap.put(code, bean);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void destory() {

    }
}
