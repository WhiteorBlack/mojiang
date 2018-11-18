package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/27.
 */

public class GoodOrderConfirmList {
    private String storeId ;
    private String storeName ;
    private List<OrderDetialBean.CartModelListBean> goodList ;

    private WordType deliveryInfo ;//配送方式，需要动态改变的，非接口返回
    private String messageInfo ;//留言，非接口返回

    public String getMessageInfo() {
        return StringUtils.convertNull(messageInfo);
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    public WordType getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(WordType deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<OrderDetialBean.CartModelListBean> getGoodList() {
        return null == goodList ? new ArrayList<OrderDetialBean.CartModelListBean>() : goodList;
    }

    public void setGoodList(List<OrderDetialBean.CartModelListBean> goodList) {
        this.goodList = goodList;
    }
}
