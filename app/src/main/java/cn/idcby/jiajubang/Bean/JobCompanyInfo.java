package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 招聘公司模版
 */

public class JobCompanyInfo {
    public String CompanyLogoImage ;
    public String CompanyName ;
    public String CompanyType ;
    public String CompanyScale ;
    public String CompanyIntroduce ;
    public String CompanyAddress ;
    public List<ImageThumb> AlbumsList ;

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

    public String getCompanyIntroduce() {
        return StringUtils.convertNull(CompanyIntroduce);
    }

    public String getCompanyAddress() {
        return StringUtils.convertNull(CompanyAddress);
    }

    public List<ImageThumb> getImageThumb(){
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }
}
