package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 直供商品列表
 * Created on 2018/4/26.
 */

public class GoodListParent {
    private List<GoodList> childGood = new ArrayList<>();

    public List<GoodList> getChildGood() {
        return childGood;
    }

    public void setChildGood(List<GoodList> childGood) {
        this.childGood.addAll(childGood);
    }

    public void addGoodToList(GoodList store){
        this.childGood.add(store) ;
    }
}
