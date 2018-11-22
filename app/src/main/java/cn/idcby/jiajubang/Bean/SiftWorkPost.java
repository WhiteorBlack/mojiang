package cn.idcby.jiajubang.Bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 筛选职位
 * Created on 2018/9/12.
 */

public class SiftWorkPost extends BaseObservable {
    private boolean isSelected = false;

    public String Name;
    public String WorkPostID;

    public SiftWorkPost() {
    }

    public SiftWorkPost(String name, String workPostID) {
        Name = name;
        WorkPostID = workPostID;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getWorkPostID() {
        return StringUtils.convertNull(WorkPostID);
    }
}
