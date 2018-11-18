package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/6/15.
 */

public class DirectGoodList {
    public String ProductSpecialID ;
    public String Title ;
    public List<GoodList> specialGoodList ;

    public String getProductSpecialID() {
        return StringUtils.convertNull(ProductSpecialID);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public List<GoodList> getSpecialGoodList() {
        return null == specialGoodList ? new ArrayList<GoodList>() : specialGoodList;
    }
}
