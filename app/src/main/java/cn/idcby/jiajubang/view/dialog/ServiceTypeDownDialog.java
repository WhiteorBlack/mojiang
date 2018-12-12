package cn.idcby.jiajubang.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServiceClassAdapter;
import cn.idcby.jiajubang.anim.BounceTopToBottomEnter;
import cn.idcby.jiajubang.databinding.DialogServiceTypeBinding;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.ScreenUtil;
import cn.idcby.jiajubang.utils.Urls;

public class ServiceTypeDownDialog extends BaseDialog<ServiceTypeDownDialog> {
    private DialogServiceTypeBinding binding;
    private ServiceClassAdapter adapter;
    private DoubleSelectionInterface doubleSelectionInterface;

    public DoubleSelectionInterface getDoubleSelectionInterface() {
        return doubleSelectionInterface;
    }

    public void setDoubleSelectionInterface(DoubleSelectionInterface doubleSelectionInterface) {
        this.doubleSelectionInterface = doubleSelectionInterface;
    }

    public ServiceTypeDownDialog(Context context, boolean isPopupStyle) {
        super(context, isPopupStyle);
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_service_type, null, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        adapter = new ServiceClassAdapter(R.layout.item_service_type);
        binding.rvList.setLayoutManager(new GridLayoutManager(mContext, 3));
        binding.rvList.setAdapter(adapter);
        binding.rvList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapters, View view, int position) {
                for (int i = 0; i < adapter.getData().size(); i++) {
                    adapter.getData().get(i).setSelected(i == position);
                }
                if (doubleSelectionInterface != null) {
                    doubleSelectionInterface.onSelection(adapter.getData().get(position));
                }
                dismiss();
            }
        });
    }

    @Override
    public void setUiBeforShow() {
        dimEnabled(false);
        showAnim(new BounceTopToBottomEnter());
        float height = (ScreenUtil.dip2px(62) + ResourceUtils.getStatusBarHeight(mContext)) * 1.0f / ScreenUtil.getWidthAndHeight().heightPixels;
        heightScale(1 - height);
        setCanceledOnTouchOutside(true);
        binding.tvTitle.setText(title);
    }
    private String title;
    public void setTitle(String title) {
       this.title=title;
    }

    /**
     * 获取分类
     */
    public void getCategoryList(String mCategoryId) {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("code", mCategoryId);
        paramMap.put("id", "2");
        paramMap.put("Layer", "2");

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, paramMap
                , new RequestListCallBack<ServerCategory>("getSecondCategory", mContext, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        adapter.setNewData(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {

                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }

}
