package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 */

public class JobsCollectionList extends JobsList{
    public String CompanyName ;
    public String ResumeCount ;

    public String EnabledMark ;//上下架--我发布的招聘--使用：1 上架  0 下架
    public String CompanyTop ;//置顶（推广）--我发布的招聘--使用：1 是  0 否

    public String getResumeCount() {
        return "" + StringUtils.convertString2Count(ResumeCount);
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public boolean isEnableMark(){
        return "1".equals(EnabledMark) ;
    }

    public boolean isCompanyTop(){
        return "1".equals(CompanyTop) ;
    }

}
