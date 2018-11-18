package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/10.
 */

public class HomeWorkCount {
    public String recruitCount ;
    public String resumeCount ;
    public String postCount ;
    public String needCount ;
    public String oldProductCount ;

    public String getRecruitCount() {
        return StringUtils.convertString2Count(recruitCount) + "";
    }

    public String getResumeCount() {
        return StringUtils.convertString2Count(resumeCount) + "";
    }

    public String getPostCount() {
        return StringUtils.convertString2Count(postCount) + "";
    }
    public String getNeedCount() {
        return StringUtils.convertString2Count(needCount) + "";
    }
    public String getOldProductCountt() {
        return StringUtils.convertString2Count(oldProductCount) + "";
    }
}
