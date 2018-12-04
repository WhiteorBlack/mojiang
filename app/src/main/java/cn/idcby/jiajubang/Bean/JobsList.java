package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 */

public class JobsList {
    public String RecruitID ;
    public String RecruitId ;
    public String WorkPostName ;
    public String WorkYears ;
    public String WorkType ;
    public String IsFace ;
    public String MinAmount ;
    public String MaxAmount ;
    public String CompanyLogoImage ;
    public String ReleaseTime ;
    public String Address ;
    public List<WelfareList> WelfareList ;
    private double Distance=0.00;

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public String getRecruitID() {
        return null == RecruitId ? StringUtils.convertNull(RecruitID) : RecruitId;
    }

    public String getWorkPostName() {
        return WorkPostName;
    }

    public String getWorkYears() {
        return WorkYears;
    }

    public String getSalary() {
        return "1".equals(IsFace) ? "面议"
                : (StringUtils.convertNull(MinAmount) + "-" + StringUtils.convertNull(MaxAmount));
    }

    public String getCompanyLogoImage() {
        return CompanyLogoImage;
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }

    public String getWorkTypeName(){
        return "1".equals(WorkType) ? "全职" : "兼职" ;
    }

    public boolean isWorkAll(){
        return "1".equals(WorkType) ;
    }

    public List<WelfareList> getWelfareList() {
        return WelfareList;
    }
}
