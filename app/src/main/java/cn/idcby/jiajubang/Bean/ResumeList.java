package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 简历列表
 * Created on 2018/3/27.
 */

public class ResumeList implements Serializable{
    public String Name ; //选择简历使用该字段
    public String CreateUserName ;//简历列表使用该字段
    public String Sex ;
    public String Birthday ;
    public String Education ;
    public String Age ;
    public String WorkYear ;
    public String WorkType ;
    public String Address ;
    public String CityName ;
    public String WorkPostName ;
    public String ReleaseTime ;
    public String UserHeadIcon ;
    public String HeadIcon ;
    public int LikeNumber ;
    public String ResumeId ;
    public String IsFace ;
    public String MaxAmount ;
    public String MinAmount ;

    public String EnabledMark ;//我的简历使用字段 1上架 0下架
    public String HopeCityName ;//我的简历使用字段

    public boolean isEnableMark(){
        return "1".equals(EnabledMark) ;
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getSex() {
        return Sex;
    }

    public String getAge() {
        return StringUtils.convertAge(Age);
    }

    public String getWorkYear() {
        return WorkYear;
    }

    public String getAddress() {
        return Address;
    }

    public String getWorkPostName() {
        return StringUtils.convertNull(WorkPostName);
    }

    public String getCityName() {
        return null == HopeCityName ? StringUtils.convertNull(CityName) : HopeCityName;
    }

    public boolean isFace(){
        return "1".equals(IsFace) ;
    }

    public String getApplySalary() {
        if("1".equals(IsFace)){
            return "面议" ;
        }
        return StringUtils.convertNull(MinAmount) + "-" + StringUtils.convertNull(MaxAmount) ;
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public String getUserHeadIcon() {
        return null == HeadIcon ? StringUtils.convertNull(UserHeadIcon) : HeadIcon ;
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public String getResumeId() {
        return ResumeId;
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getBirthday() {
        return Birthday;
    }

    public String getEducation() {
        return Education;
    }
    public String getWorkTypeName(){
        return "1".equals(WorkType) ? "全职" : "兼职" ;
    }

    public boolean isWorkAll(){
        return "1".equals(WorkType) ;
    }
}
