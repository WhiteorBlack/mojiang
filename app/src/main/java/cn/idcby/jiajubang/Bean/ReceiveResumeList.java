package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 简历列表--招聘下的简历
 * Created on 2018/3/27.
 */

public class ReceiveResumeList implements Serializable{
    public String ResumeUserHeadIcon ;
    public String ResumeName ;
    public String ResumeIsFace ;
    public String ResumeMaxAmount ;
    public String ResumeMinAmount ;
    public String ReleaseTime  ;
    public String ResumeId ;
    public String ResumeWorkPostId ;
    public String ResumeWorkPostName ;
    public String HopeCityId ;
    public String HopeCityName ;

    public String getResumeUserHeadIcon() {
        return StringUtils.convertNull(ResumeUserHeadIcon);
    }

    public String getResumeId() {
        return StringUtils.convertNull(ResumeId);
    }

    public String getResumeName() {
        return StringUtils.convertNull(ResumeName);
    }

    public String getWorkPostName() {
        return StringUtils.convertNull(ResumeWorkPostName);
    }

    public String getCityName() {
        return StringUtils.convertNull(HopeCityName);
    }

    public String getApplySalary() {
        if("1".equals(ResumeIsFace)){
            return "面议" ;
        }
        return StringUtils.convertNull(ResumeMinAmount) + "-" + StringUtils.convertNull(ResumeMaxAmount) ;
    }

    public String getReleaseTime() {
        return StringUtils.convertDateToDay(ReleaseTime);
    }
}
