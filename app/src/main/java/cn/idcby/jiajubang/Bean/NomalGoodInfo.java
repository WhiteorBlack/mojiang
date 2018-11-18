package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/23.
 */

public class NomalGoodInfo {
    public String SkuID;
    public String ProductID;
    public String ImgUrl;

    public NomalGoodInfo() {
    }

    public NomalGoodInfo(String imgUrl) {
        ImgUrl = imgUrl;
    }
    public NomalGoodInfo(String skuId ,String productId ,String imgUrl) {
        SkuID = skuId;
        ProductID = productId;
        ImgUrl = imgUrl;
    }

    public String getSkuID() {
        return StringUtils.convertNull(SkuID);
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }
}
