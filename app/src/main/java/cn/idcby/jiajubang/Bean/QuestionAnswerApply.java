package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/19.
 */

public class QuestionAnswerApply {
    public String QAMasterAuthenticationId ;
    public String ApplyReason ;
    public String ApplyUserPhone ;
    public String QACategoryId ;
    public String QACategoryName ;

    public String getQAMasterAuthenticationId() {
        return StringUtils.convertNull(QAMasterAuthenticationId);
    }

    public String getApplyReason() {
        return StringUtils.convertNull(ApplyReason);
    }

    public String getApplyUserPhone() {
        return StringUtils.convertNull(ApplyUserPhone);
    }

    public String getQACategoryId() {
        return StringUtils.convertNull(QACategoryId);
    }

    public String getQACategoryName() {
        return StringUtils.convertNull(QACategoryName);
    }
}
