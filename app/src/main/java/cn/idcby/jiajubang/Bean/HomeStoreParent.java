package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2018/5/5.
 */

public class HomeStoreParent {
    private List<HomeStore> storeList = new ArrayList<>();

    public List<HomeStore> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<HomeStore> storeList) {
        this.storeList.addAll(storeList);
    }

    public void addStoreToList(HomeStore store){
        this.storeList.add(store) ;
    }
}
