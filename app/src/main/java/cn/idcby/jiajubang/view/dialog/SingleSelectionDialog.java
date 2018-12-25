package cn.idcby.jiajubang.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.DialogSingleCategoryAdapter;
import cn.idcby.jiajubang.databinding.DialogSingleSelectionBinding;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;

public class SingleSelectionDialog extends BottomBaseDialog<SingleSelectionDialog> {
    private DialogSingleSelectionBinding binding;
    private DialogSingleCategoryAdapter adapter;

    private DoubleSelectionInterface doubleSelectionInterface;

    public DoubleSelectionInterface getDoubleSelectionInterface() {
        return doubleSelectionInterface;
    }

    public void setDoubleSelectionInterface(DoubleSelectionInterface doubleSelectionInterface) {
        this.doubleSelectionInterface = doubleSelectionInterface;
    }

    public SingleSelectionDialog(Context context) {
        super(context);

    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_single_selection, null, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        adapter = new DialogSingleCategoryAdapter(R.layout.item_single_selection);
        binding.rvFirst.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvFirst.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        binding.rvFirst.setAdapter(adapter);
        binding.rvFirst.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapters, View view, int position) {
                UnusedCategory category = adapter.getData().get(position);
                if (doubleSelectionInterface != null) {
                    doubleSelectionInterface.onSelection(category);
                }
                for (int i = 0; i < adapter.getData().size(); i++) {
                    adapter.getData().get(i).setSelected(i == position);
                }
                dismiss();
            }
        });
    }

    @Override
    public void setUiBeforShow() {
        heightScale(0.5f);
    }

    /**
     * 获取列表
     */
    public void getCategory() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Keyword", "");
        paramMap.put("Id", "1");

        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<UnusedCategory>("getCategory", mContext, UnusedCategory.class) {
                    @Override
                    public void onSuccessResult(List<UnusedCategory> bean) {
                        adapter.setNewData(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showToast(mContext, str);
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

}
