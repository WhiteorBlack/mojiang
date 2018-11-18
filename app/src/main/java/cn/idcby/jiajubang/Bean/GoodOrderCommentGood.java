package cn.idcby.jiajubang.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/27.
 */

public class GoodOrderCommentGood implements Serializable{
    public String OrderItemID ;
    public String ImgUrl ;
    public String ProductTitle ;
    public int Star = 5 ;//商品评分--商品评价使用，默认5星
    public String CommentContent ;
    public List<String> CommentImgList = new ArrayList<>() ;

    public GoodOrderCommentGood() {
    }
    public GoodOrderCommentGood(GoodOrderGood good) {
        OrderItemID = good.getOrderItemID() ;
        ImgUrl = good.getImgUrl() ;
        ProductTitle = good.getProductTitle() ;
    }

    public String getOrderItemID() {
        return StringUtils.convertNull(OrderItemID);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }


    public String getProductTitle() {
        return StringUtils.convertNull(ProductTitle);
    }

    public int getStar() {
        return Star;
    }

    public void setStar(int star) {
        Star = star;
    }

    public String getCommentContent() {
        return StringUtils.convertNull(CommentContent);
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public String getStarDesc(){
        String desc = "" ;
        if (Star == 1) {
            desc = "非常差";
        } else if (Star == 2) {
            desc = "差";
        } else if (Star == 3) {
            desc = "一般";
        } else if (Star == 4) {
            desc = "好";
        } else if (Star == 5) {
            desc = "非常好";
        }
        return desc ;
    }

    public List<String> getCommentImgList() {
        if(CommentImgList.size() == 0){
            CommentImgList.add(null) ;
        }
        return CommentImgList ;
    }

    public void addCommentImgList(List<String> commentImg) {
        CommentImgList.addAll(0,commentImg) ;
    }
}
