package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 简历详细
 * Created on 2018/3/27.
 */

public class ResumeDetails {

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public String H5Url;

    public String Name ;
    public String Sex ;
    public String Age ;
    public String Birthday ;
    public String Height ;
    public String Weight ;
    public String Education ;
    public String NowAddress ;
    public String Pname ;
    public String WorkPostId ;
    public String HopeProvinceId ;
    public String HopeProvinceName ;
    public String HopeCityId ;
    public String HopeCityName ;
    public String WorkType ;
    public String IsFace ;
    public String MinAmount ;
    public String MaxAmount ;

    public String WorkYears ;
    public String Phone1 ;
    public String Phone2 ;
    public String WeChat ;
    public String QQ ;
    public String Email ;
    public String SelfEvaluation ;
    public String UserHeadIcon ;
    public String CreateUserId ;
    public String CreateDate ;
    public String HxName ;
    public int IsCollection ;
    public int IsLike ;
    public String ReleaseTime ;
    public String Position ;
    public List<WorkExperience> ExperienceList ;
    public List<ImageThumb> AlbumsList ;

    public boolean isFace(){
        return "1".equals(IsFace) ;
    }

    public String getMinAmount() {
        return StringUtils.convertNull(MinAmount);
    }

    public String getMaxAmount() {
        return StringUtils.convertNull(MaxAmount);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getApplySalary() {
        if("1".equals(IsFace)){
            return "面议" ;
        }
        return StringUtils.convertNull(MinAmount) + "-" + StringUtils.convertNull(MaxAmount) ;
    }

    public String getPname() {
        return StringUtils.convertNull(Pname);
    }

    public String getWorkPostId() {
        return StringUtils.convertNull(WorkPostId);
    }

    public String getEducation() {
        return StringUtils.convertNull(Education);
    }

    public String getWorkYears() {
        return StringUtils.convertNull(WorkYears);
    }

    public String getSelfEvaluation() {
        return StringUtils.convertNull(SelfEvaluation);
    }

    public String getUserHeadIcon() {
        return StringUtils.convertNull(UserHeadIcon);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public boolean isCollection() {
        return 1 == IsCollection;
    }

    public boolean isLike() {
        return 1 == IsLike;
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getSex() {
        if("1".equals(Sex)){
            return "男" ;
        }
        if("2".equals(Sex)){
            return "女" ;
        }
        return StringUtils.convertNull(Sex);
    }

    public int getSexValue(){
        return StringUtils.convertString2Count(Sex) ;
    }

    public String getAge() {
        return StringUtils.convertAge(Age);
    }

    public String getBirthday() {
        return StringUtils.convertDateToDay(Birthday);
    }

    public String getHeight() {
        return StringUtils.convertStringNoPoint(Height);
    }

    public String getWeight() {
        return StringUtils.convertStringNoPoint(Weight);
    }

    public String getNowAddress() {
        return StringUtils.convertNull(NowAddress);
    }

    public String getHopeProvinceId() {
        return StringUtils.convertNull(HopeProvinceId);
    }

    public String getHopeProvinceName() {
        return StringUtils.convertNull(HopeProvinceName);
    }

    public String getHopeCityId() {
        return StringUtils.convertNull(HopeCityId);
    }

    public String getHopeCityName() {
        return StringUtils.convertNull(HopeCityName);
    }

    public String getWorkType() {
        return StringUtils.convertNull(WorkType);
    }

    public String getPhone1() {
        return StringUtils.convertNull(Phone1);
    }

    public String getPhone2() {
        return StringUtils.convertNull(Phone2);
    }

    public String getWeChat() {
        return StringUtils.convertNull(WeChat);
    }

    public String getQQ() {
        return StringUtils.convertNull(QQ);
    }

    public String getEmail() {
        return StringUtils.convertNull(Email);
    }

    public List<WorkExperience> getExperienceList() {
        return null == ExperienceList ? new ArrayList<WorkExperience>() : ExperienceList;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }


    public String getWorkTypeName(){
        return "1".equals(WorkType) ? "全职" : "兼职" ;
    }

    public boolean isWorkAll(){
        return "1".equals(WorkType) ;
    }
}
