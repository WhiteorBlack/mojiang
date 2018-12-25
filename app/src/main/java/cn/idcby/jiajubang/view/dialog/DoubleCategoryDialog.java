package cn.idcby.jiajubang.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.CategoryBean;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.DoubleCategoryAdapter;
import cn.idcby.jiajubang.adapter.DoubleCityAdapter;
import cn.idcby.jiajubang.databinding.DialogDoubleSelectionBinding;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.ScreenUtil;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

public class DoubleCategoryDialog extends BottomBaseDialog<DoubleCategoryDialog> {
    private DialogDoubleSelectionBinding binding;
    private DoubleCategoryAdapter firstAdapter, secondAdapter;
    private Map<String, List<CategoryBean>> dataMap = new HashMap<>();
    private DoubleSelectionInterface doubleSelectionInterface;
    private List<CategoryBean> firstList = new ArrayList<>();

    public DoubleSelectionInterface getDoubleSelectionInterface() {
        return doubleSelectionInterface;
    }

    public void setDoubleSelectionInterface(DoubleSelectionInterface doubleSelectionInterface) {
        this.doubleSelectionInterface = doubleSelectionInterface;
    }

    public DoubleCategoryDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_double_selection, null, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

        firstAdapter = new DoubleCategoryAdapter(R.layout.item_category_first);
        binding.rvFirst.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFirst.setAdapter(firstAdapter);
        binding.rvFirst.addOnItemTouchListener(firstOnItemListener());
        secondAdapter = new DoubleCategoryAdapter(R.layout.item_category_second);
        binding.rvSecond.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSecond.setAdapter(secondAdapter);
        binding.rvSecond.addOnItemTouchListener(secondOnItemListener());
    }

    @Override
    public void setUiBeforShow() {
//        heightScale(0.5f);
        binding.llParent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        binding.llParent.getLayoutParams().height=ScreenUtil.getWidthAndHeight().heightPixels/2;
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText("行业角色");
    }

    Address post;

    public RecyclerView.OnItemTouchListener firstOnItemListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                CategoryBean bean = firstList.get(position);
                for (int i = 0; i < firstList.size(); i++) {
                    firstList.get(i).setSelected(position == i);
                }
                secondAdapter.setNewData(dataMap.get(bean.getItemDetailId()));
            }
        };
    }

    private void changeSelectState(int position, List<Address> data) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelected(i == position);
        }
    }

    public RecyclerView.OnItemTouchListener secondOnItemListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adaptera, View view, int position) {
                if (doubleSelectionInterface != null) {
                    doubleSelectionInterface.onSelection(secondAdapter.getData().get(position));
                }
                dismiss();
            }
        };
    }

    public void getFirstData(String type) {

        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Code", type);

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_TYPE_BY_CODE, paramMap
                , new RequestListCallBack<CategoryBean>("getEduList", mContext, CategoryBean.class) {
                    @Override
                    public void onSuccessResult(List<CategoryBean> bean) {
                        if (bean != null && bean.size() > 0) {
                            dealData(bean);
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showErrorToast(mContext, str);
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    private void dealData(List<CategoryBean> bean) {
        for (CategoryBean bean1 : bean) {
            if (TextUtils.equals(bean1.getParentId(), "0")) {
                firstList.add(bean1);
            }
        }
        firstAdapter.setNewData(firstList);
        for (int i = 0; i < firstList.size(); i++) {
            List<CategoryBean> secondList = new ArrayList<>();
            for (CategoryBean categoryBean : bean) {
                if (TextUtils.equals(firstList.get(i).getItemDetailId(), categoryBean.getParentId())) {
                    secondList.add(categoryBean);
                }
                dataMap.put(firstList.get(i).getItemDetailId(), secondList);
            }
        }
        firstList.get(0).setSelected(true);
        secondAdapter.setNewData(dataMap.get(firstList.get(0).getItemDetailId()));
    }


    @Override
    public void dismiss() {
        super.dismiss();
        NetUtils.cancelTag("城市");
        NetUtils.cancelTag("省份");
    }
}
