package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/27.
 */

public class JobPost implements Serializable{
    public String WorkPostID ;
    public String Name ;
    public int IsHaveChilder ;

    public String getWorkPostID() {
        return StringUtils.convertNull(WorkPostID) ;
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public boolean isHaveChilder() {
        return 1 == IsHaveChilder;
    }
}
