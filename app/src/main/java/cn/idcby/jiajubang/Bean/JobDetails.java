package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 */

public class JobDetails {

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public String H5Url;
    public String RecruitId ;

    public String CompanyLogoImage ;
    public String CompanyName ;
    public String CompanyType ;
    public String CompanyScale ;
    public String CompanyIntroduce ;
    public String Address ;

    public String WorkPostId ;
    public String Name ;
    public String CreareAddress ;
    public String WorkYears ;
    public String Education ;
    public String PostIntroduce ;
    public String HxName ;

    public int IsCollection ;
    public int IsLike ;
    public int LikeNumber ;

    public String ReleaseTime ;
    public String IsFace ;
    public String MinAmount ;
    public String MaxAmount ;
    public String WorkAddress ;
    public String PostDescription ;
    public String Salary ;
    public String WorkProvinceId ;
    public String WorkProvinceName ;
    public String WorkCityId ;
    public String WorkCityName ;
    public String OtherRequirement ;
    public String WorkType ;
    public String Phone ;
    public String CreateUserId ;
    public String AuthenticationText ;

    public List<WelfareList> WelfareList ;
    public List<ImageThumb> AlbumsList ;
    public List<SupportUser> LikeList ;

    public String getAuthenticationText() {
        return StringUtils.convertNull(AuthenticationText);
    }

    public String getRecruitId() {
        return RecruitId;
    }

    public String getCompanyLogoImage() {
        return StringUtils.convertNull(CompanyLogoImage);
    }

    public String getCompanyName() {
        return StringUtils.convertNull(CompanyName);
    }

    public String getCompanyType() {
        return StringUtils.convertNull(CompanyType);
    }

    public String getCompanyScale() {
        return StringUtils.convertNull(CompanyScale);
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }

    public String getWorkPostId() {
        return StringUtils.convertNull(WorkPostId);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public String getCreareAddress() {
        return StringUtils.convertNull(CreareAddress);
    }

    public String getWorkYears() {
        return StringUtils.convertNull(WorkYears);
    }

    public String getEducation() {
        return StringUtils.convertNull(Education);
    }

    public String getPostIntroduce() {
        return StringUtils.convertNull(PostIntroduce);
    }

    public String getCompanyIntroduce() {
        return StringUtils.convertNull(CompanyIntroduce);
    }

    public int getIsCollection() {
        return IsCollection;
    }

    public int getIsLike() {
        return IsLike;
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public String getWorkProvinceId() {
        return StringUtils.convertNull(WorkProvinceId);
    }

    public String getWorkProvinceName() {
        return StringUtils.convertNull(WorkProvinceName);
    }

    public String getWorkCityId() {
        return StringUtils.convertNull(WorkCityId);
    }

    public String getWorkCityName() {
        return StringUtils.convertNull(WorkCityName);
    }

    public String getOtherRequirement() {
        return StringUtils.convertNull(OtherRequirement);
    }

    public String getWorkType() {
        return StringUtils.convertNull(WorkType);
    }

    public String getWorkTypeName(){
        return "1".equals(WorkType) ? "全职" : "兼职" ;
    }

    public boolean isWorkAll(){
        return "1".equals(WorkType) ;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getPhone() {
        return StringUtils.convertNull(Phone);
    }

    public boolean isFace() {
        return "1".equals(IsFace);
    }

    public String getMinAmount() {
        return StringUtils.convertNull(MinAmount);
    }

    public String getMaxAmount() {
        return StringUtils.convertNull(MaxAmount);
    }

    public String getWorkAddress() {
        return StringUtils.convertNull(WorkAddress);
    }

    public String getPostDescription() {
        return StringUtils.convertNull(PostDescription);
    }

    public List<WelfareList> getWelfareList() {
        return null == WelfareList ? new ArrayList<WelfareList>() : WelfareList;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }

    public List<SupportUser> getLikeList() {
        return null == LikeList ? new ArrayList<SupportUser>() : LikeList;
    }
}
