package cn.idcby.jiajubang.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.FlipEnter.FlipTopEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.DoubleCityAdapter;
import cn.idcby.jiajubang.anim.BounceTopToBottomEnter;
import cn.idcby.jiajubang.databinding.DialogDoubleDownSelectionBinding;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.ScreenUtil;
import cn.idcby.jiajubang.utils.Urls;

public class DoubleCityDownDialog extends BaseDialog<DoubleCityDownDialog> {
    private DialogDoubleDownSelectionBinding binding;
    private DoubleCityAdapter firstAdapter, secondAdapter;
    private Map<String, List<Address>> dataMap = new HashMap<>();
    private DoubleSelectionInterface doubleSelectionInterface;

    public DoubleSelectionInterface getDoubleSelectionInterface() {
        return doubleSelectionInterface;
    }

    public void setDoubleSelectionInterface(DoubleSelectionInterface doubleSelectionInterface) {
        this.doubleSelectionInterface = doubleSelectionInterface;
    }

    public DoubleCityDownDialog(Context context) {
        super(context);
    }

    public DoubleCityDownDialog(Context context, boolean isPopupStyle) {
        super(context, isPopupStyle);
        this.binding = binding;
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_double_down_selection, null, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {
        firstAdapter = new DoubleCityAdapter(R.layout.item_city_first);
        binding.rvFirst.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFirst.setAdapter(firstAdapter);
        binding.rvFirst.addOnItemTouchListener(firstOnItemListener());
        secondAdapter = new DoubleCityAdapter(R.layout.item_city_second);
        binding.rvSecond.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSecond.setAdapter(secondAdapter);
        binding.rvSecond.addOnItemTouchListener(secondOnItemListener());
    }

    @Override
    public void setUiBeforShow() {
        dimEnabled(false);
        showAnim(new BounceTopToBottomEnter());
        float height = (ScreenUtil.dip2px(82) + ResourceUtils.getStatusBarHeight(mContext)) * 1.0f / ScreenUtil.getWidthAndHeight().heightPixels;
        heightScale(1 - height);
        setCanceledOnTouchOutside(true);
    }

    public RecyclerView.OnItemTouchListener firstOnItemListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Address post = firstAdapter.getData().get(position);
                if (dataMap.get(post.AreaId) == null || dataMap.get(post.AreaId).isEmpty()) {
                    getSecondData(post.AreaId);
                } else {
                    secondAdapter.setNewData(dataMap.get(post.AreaId));
                }
                changeSelectState(position, firstAdapter.getData());
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

    public void getFirstData() {

        Map<String, String> para = ParaUtils.getPara(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_PROVINCE, false, para,
                new RequestListCallBack<Address>("省份", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        if (!bean.isEmpty()) {
                            bean.get(0).setSelected(true);
                            firstAdapter.setNewData(bean);
                            getSecondData(bean.get(0).getAreaId());
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

    private void getSecondData(final String workPostId) {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", workPostId);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_CITY, false, para,
                new RequestListCallBack<Address>("城市", mContext,
                        Address.class) {
                    @Override
                    public void onSuccessResult(List<Address> bean) {
                        secondAdapter.setNewData(bean);
                        dataMap.put(workPostId, bean);
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
