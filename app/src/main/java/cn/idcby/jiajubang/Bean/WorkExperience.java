package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/27.
 */

public class WorkExperience implements Serializable {
    public String WorkExperienceId ;
    public String BeginTime ;
    public String OverTime ;
    public String DepartmentName ;
    public String CompanyName ;
    public String PostName ;
    public String Salary ;

    public WorkExperience() {
    }

    public WorkExperience(String beginTime, String overTime, String companyName, String postName, String salary) {
        BeginTime = beginTime;
        OverTime = overTime;
        CompanyName = companyName;
        PostName = postName;
        Salary = salary;
    }

    public String getWorkExperienceId() {
        return StringUtils.convertNull(WorkExperienceId);
    }

    public String getBeginTime() {
        return StringUtils.convertDateToDay(BeginTime);
    }

    public String getOverTime() {
        return StringUtils.convertDateToDay(OverTime);
    }

    public String getDepartmentName() {
        return StringUtils.convertNull(DepartmentName);
    }

    public String getPostName() {
        return StringUtils.convertNull(PostName);
    }

    public String getSalary() {
        return StringUtils.convertNull(Salary);
    }

    public String getCompanyName() {
        return StringUtils.convertNull(CompanyName);
    }
}
