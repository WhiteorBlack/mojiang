package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 服务评论
 * Created on 2018/5/24.
 */

public class ServerComment implements Serializable {
    public String OrderEvaluateId ;
    public String EvaluateContent ;
    public String EvaluateLevel ;
    public String CreateDate ;
    public String CreateUserId ;
    public String CreateUserName ;
    public String CreateUserHeadIcon ;

    public String getEvaluateLevel() {
        return StringUtils.convertNull(EvaluateLevel);
    }

    public String getOrderEvaluateId() {
        return StringUtils.convertNull(OrderEvaluateId);
    }

    public String getEvaluateContent() {
        return StringUtils.convertNull(EvaluateContent);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getCreateUserHeadIcon() {
        return StringUtils.convertNull(CreateUserHeadIcon);
    }
}
