package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/10.
 */

public class FactoryApplyInfo {
    public String FactoryAuthenticationId;
    public String Name;
    public String IndustryId;
    public String IndustryName;
    public String TypeId;
    public String TypeName;
    public String ProvinceId;
    public String ProvinceName;
    public String CityId;
    public String CityName;
    public String CountyId;
    public String CountyName;
    public String Address;
    public String ScopeOperationIds;
    public String ScopeOperationNames;
    public String Introduce;
    public String BusinessLicenseImg;
    public String Img1;
    public String Img2;
    public String Img3;
    public String Img4;
    public String Img5;

    public String getFactoryAuthenticationId() {
        return FactoryAuthenticationId;
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getIndustryId() {
        return StringUtils.convertNull(IndustryId);
    }

    public String getIndustryName() {
        return StringUtils.convertNull(IndustryName);
    }

    public String getTypeId() {
        return StringUtils.convertNull(TypeId);
    }

    public String getTypeName() {
        return StringUtils.convertNull(TypeName);
    }

    public String getProvinceId() {
        return StringUtils.convertNull(ProvinceId);
    }

    public String getProvinceName() {
        return StringUtils.convertNull(ProvinceName);
    }

    public String getCityId() {
        return StringUtils.convertNull(CityId);
    }

    public String getCityName() {
        return StringUtils.convertNull(CityName);
    }

    public String getCountyId() {
        return StringUtils.convertNull(CountyId);
    }

    public String getCountyName() {
        return StringUtils.convertNull(CountyName);
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }

    public String getIntroduce() {
        return StringUtils.convertNull(Introduce);
    }

    public String getScopeOperationIds() {
        return StringUtils.convertNull(ScopeOperationIds);
    }

    public String getScopeOperationName() {
        return StringUtils.convertNull(ScopeOperationNames);
    }

    public String getBusinessLicenseImg() {
        return StringUtils.convertNull(BusinessLicenseImg);
    }


    public List<ImageThumb> getAlbumsList(){
        List<ImageThumb> thumbs = new ArrayList<>() ;
        if(Img1 != null){
            thumbs.add(new ImageThumb(Img1)) ;
        }
        if(Img2 != null){
            thumbs.add(new ImageThumb(Img2)) ;
        }
        if(Img3 != null){
            thumbs.add(new ImageThumb(Img3)) ;
        }
        if(Img4 != null){
            thumbs.add(new ImageThumb(Img4)) ;
        }
        if(Img5 != null){
            thumbs.add(new ImageThumb(Img5)) ;
        }
        return thumbs ;
    }
}
