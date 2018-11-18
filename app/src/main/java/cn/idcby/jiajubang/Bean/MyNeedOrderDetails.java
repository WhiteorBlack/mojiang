package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/22.
 */

public class MyNeedOrderDetails {
    public String OrderID;
    public String OrderNO;
    public String OrderAmount ;
    public String NeedTitle ;
    public int OrderStatus ;
    public String OrderStatusName ;
    public String TypeId ;
    public String WorkDescribe ;
    public String CreateDate ;
    public String WorkImage1 ;
    public String WorkImage2 ;
    public String WorkImage3 ;

    public String PayUserId ;//需要付款的人的id

    public String getPayUserId() {
        return StringUtils.convertNull(PayUserId);
    }

    public String getOrderID() {
        return StringUtils.convertNull(OrderID);
    }

    public String getOrderNO() {
        return StringUtils.convertNull(OrderNO);
    }

    public String getNeedTitle() {
        return NeedTitle;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public String getOrderStatusName() {
        return OrderStatusName;
    }

    public String getTypeId() {
        return TypeId;
    }

    public String getWorkDescribe() {
        return StringUtils.convertNull(WorkDescribe);
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getWorkImage1() {
        return StringUtils.convertNull(WorkImage1);
    }

    public String getWorkImage2() {
        return StringUtils.convertNull(WorkImage2);
    }

    public String getWorkImage3() {
        return StringUtils.convertNull(WorkImage3);
    }

    public String getOrderAmount() {
        return StringUtils.convertNull(OrderAmount);
    }

    public List<ImageThumb> getThumbs(){
        List<ImageThumb> imgList = new ArrayList<>() ;
        if(WorkImage1 != null){
            imgList.add(new ImageThumb(WorkImage1)) ;
        }
        if(WorkImage2 != null){
            imgList.add(new ImageThumb(WorkImage2)) ;
        }
        if(WorkImage3 != null){
            imgList.add(new ImageThumb(WorkImage3)) ;
        }

        return imgList ;
    }
}
