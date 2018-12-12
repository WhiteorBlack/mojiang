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
    private String Education;
    private String CompanyName;
    private int Authentication;

    public void setRecruitID(String recruitID) {
        RecruitID = recruitID;
    }

    public String getRecruitId() {
        return RecruitId;
    }

    public void setRecruitId(String recruitId) {
        RecruitId = recruitId;
    }

    public void setWorkPostName(String workPostName) {
        WorkPostName = workPostName;
    }

    public void setWorkYears(String workYears) {
        WorkYears = workYears;
    }

    public String getWorkType() {
        return WorkType;
    }

    public void setWorkType(String workType) {
        WorkType = workType;
    }

    public String getIsFace() {
        return IsFace;
    }

    public void setIsFace(String isFace) {
        IsFace = isFace;
    }

    public String getMinAmount() {
        return MinAmount;
    }

    public void setMinAmount(String minAmount) {
        MinAmount = minAmount;
    }

    public String getMaxAmount() {
        return MaxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        MaxAmount = maxAmount;
    }

    public void setCompanyLogoImage(String companyLogoImage) {
        CompanyLogoImage = companyLogoImage;
    }

    public void setReleaseTime(String releaseTime) {
        ReleaseTime = releaseTime;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setWelfareList(List<cn.idcby.jiajubang.Bean.WelfareList> welfareList) {
        WelfareList = welfareList;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public int getAuthentication() {
        return Authentication;
    }

    public void setAuthentication(int authentication) {
        Authentication = authentication;
    }

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
