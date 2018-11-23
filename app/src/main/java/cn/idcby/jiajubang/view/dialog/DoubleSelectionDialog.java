package cn.idcby.jiajubang.view.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.jiajubang.Bean.SiftWorkPost;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.DoubleSelectionAdapter;
import cn.idcby.jiajubang.databinding.DialogDoubleSelectionBinding;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

public class DoubleSelectionDialog extends BottomBaseDialog<DoubleSelectionDialog> {
    private DialogDoubleSelectionBinding binding;
    private DoubleSelectionAdapter firstAdapter, secondAdapter;
    private Map<String, List<SiftWorkPost>> dataMap = new HashMap<>();
    private DoubleSelectionInterface doubleSelectionInterface;

    public DoubleSelectionInterface getDoubleSelectionInterface() {
        return doubleSelectionInterface;
    }

    public void setDoubleSelectionInterface(DoubleSelectionInterface doubleSelectionInterface) {
        this.doubleSelectionInterface = doubleSelectionInterface;
    }

    public DoubleSelectionDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_double_selection, null, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
//        getWindow().setDimAmount(0f);
        heightScale(2f / 5);
        firstAdapter = new DoubleSelectionAdapter(R.layout.item_double_first);
        binding.rvFirst.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFirst.setAdapter(firstAdapter);
        binding.rvFirst.addOnItemTouchListener(firstOnItemListener());
        secondAdapter = new DoubleSelectionAdapter(R.layout.item_double_second);
        binding.rvSecond.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSecond.setAdapter(secondAdapter);
        binding.rvSecond.addOnItemTouchListener(secondOnItemListener());
    }

    @Override
    public void setUiBeforShow() {

    }

    public RecyclerView.OnItemTouchListener firstOnItemListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                SiftWorkPost post = firstAdapter.getData().get(position);
                if (dataMap.get(post.WorkPostID) == null || dataMap.get(post.WorkPostID).isEmpty()) {
                    getSecondData(post.WorkPostID);
                } else {
                    secondAdapter.setNewData(dataMap.get(post.WorkPostID));
                }
                changeSelectState(position, firstAdapter.getData());
            }
        };
    }

    private void changeSelectState(int position, List<SiftWorkPost> data) {
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
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Page", "1");
        paramMap.put("PageSize", "999");
        paramMap.put("ID", "1");

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_POST_LIST, paramMap
                , new RequestListCallBack<SiftWorkPost>("getParentPost", mContext, SiftWorkPost.class) {
                    @Override
                    public void onSuccessResult(List<SiftWorkPost> bean) {
                        if (!bean.isEmpty()) {
                            bean.get(0).setSelected(true);
                            firstAdapter.setNewData(bean);
                            getSecondData(bean.get(0).WorkPostID);

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
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Page", "1");
        paramMap.put("PageSize", "999");
        paramMap.put("ID", "2");
        paramMap.put("Keyword", StringUtils.convertNull(workPostId));

        NetUtils.getDataFromServerByPost(mContext, Urls.JOB_POST_LIST, paramMap
                , new RequestListCallBack<SiftWorkPost>("getChildPost", mContext, SiftWorkPost.class) {
                    @Override
                    public void onSuccessResult(List<SiftWorkPost> bean) {
                        dataMap.put(workPostId, bean);
                        secondAdapter.setNewData(bean);
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
