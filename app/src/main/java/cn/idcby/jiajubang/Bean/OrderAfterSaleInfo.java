package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 售后申请
 * Created on 2018/6/12.
 */
public class OrderAfterSaleInfo {
    public String OrderAfterSaleId ;
    public String Name ;
    public String ImageUrl;
    public String ServiceType ; //服务类型 1退款 2退货退款
    public String ServiceTypeName ; //服务类型 退款 退货退款
    public String ReasonId ; //退款原因id 字典项
    public String ReasonText ; //退款原因name 字典项
    public String OrderType ; // 2018-09-14 10:50:30 改：订单类型 1直供订单 2闲置订单 3服务订单
    public String OrderID ;
    public String OrderItemID ;
    public String Description ;
    public String Explain ;// 售后说明
    public String AfterSaleAmount ;// 退款金额
    public String TotalPrice ;// 商品金额
    public List<AfterSaleImage> AlbumsList ;// 图集

    public String ReturnExpressName ;// 退换快递名称
    public String ReturnExpressNO ;// 退换快递单号

    public String Reciever ;// 收货人
    public String RecieverPhone ;// 收货人电话
    public String ProvinceID ;
    public String ProvinceName ;
    public String CityID ;
    public String CityName ;
    public String Address ;

    public String Status ;
    public String StatusName ;

    public String ASConsultativeHistory ;//协商历史
    public String AfterSaleCode ;//售后编号
    public String CreateDate ;//申请时间

    public static class AfterSaleImage{
        public String ImgUrl ;

        public String getImgUrl() {
            return StringUtils.convertNull(ImgUrl);
        }
    }

    public String getOrderAfterSaleId() {
        return StringUtils.convertNull(OrderAfterSaleId);
    }
    public String getStatusName() {
        return StringUtils.convertNull(StatusName);
    }
    public String getASConsultativeHistory() {
        return StringUtils.convertNull(ASConsultativeHistory);
    }
    public String getAfterSaleCode() {
        return StringUtils.convertNull(AfterSaleCode);
    }
    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getImageUrl() {
        return StringUtils.convertNull(ImageUrl);
    }

    public String getServiceType() {
        return StringUtils.convertNull(ServiceType);
    }
    public String getServiceTypeName() {
        return StringUtils.convertNull(ServiceTypeName);
    }

    public String getReasonId() {
        return StringUtils.convertNull(ReasonId);
    }

    public String getReasonText() {
        return StringUtils.convertNull(ReasonText);
    }

    public String getOrderType() {
        return StringUtils.convertNull(OrderType);
    }

    public boolean isGoodOrUnuse(){
        return SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType) ;
    }

    public String getOrderID() {
        return StringUtils.convertNull(OrderID);
    }

    public String getOrderItemID() {
        return StringUtils.convertNull(OrderItemID);
    }

    public String getDescription() {
        return null == Description ? StringUtils.convertNull(Name) : Description;
    }

    public String getExplain() {
        return StringUtils.convertNull(Explain);
    }
    public String getTotalPrice() {
        return StringUtils.convertStringNoPoint(TotalPrice);
    }

    public String getAfterSaleAmount() {
        return StringUtils.convertStringNoPoint(AfterSaleAmount);
    }

    public List<AfterSaleImage> getAlbums() {
        return null == AlbumsList ? new ArrayList<AfterSaleImage>() : AlbumsList;
    }

    public String getReturnExpressName() {
        return StringUtils.convertNull(ReturnExpressName);
    }

    public String getReturnExpressNO() {
        return StringUtils.convertNull(ReturnExpressNO);
    }

    public String getReciever() {
        return StringUtils.convertNull(Reciever);
    }

    public String getRecieverPhone() {
        return StringUtils.convertNull(RecieverPhone);
    }

    public String getProvinceID() {
        return StringUtils.convertNull(ProvinceID);
    }

    public String getProvinceName() {
        return StringUtils.convertNull(ProvinceName);
    }

    public String getCityID() {
        return StringUtils.convertNull(CityID);
    }

    public String getCityName() {
        return StringUtils.convertNull(CityName);
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }
    public String getReceiverAddress(){
        return StringUtils.convertNull(ProvinceName)
                + StringUtils.convertNull(CityName)
                + StringUtils.convertNull(Address) ;
    }


    public boolean canCancel(){
        if(SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType)){
            return "1".equals(Status) || ("2".equals(Status) && !"1".equals(ServiceType)) || "3".equals(Status) ;
        }
        return "1".equals(Status) || "3".equals(Status) ;
    }

    public boolean canExpress(){
        return (SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType))
                && "2".equals(Status) && !"1".equals(ServiceType);
    }


    //卖家
    public boolean canAgreeOrDis(){
        return "1".equals(Status) ;
    }

    public boolean canFinish(){
        //只有商品才有确认收货，而且选了 退货退款
        return (SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType))
                && "5".equals(Status) && !"1".equals(ServiceType) ;
    }
}
