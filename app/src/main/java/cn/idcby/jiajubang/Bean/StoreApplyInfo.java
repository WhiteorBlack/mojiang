package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/2/10.
 */

public class StoreApplyInfo {
        public String ShopAuthenticationId;
        public String Name;
        public String ScopeOperationIds;
        public String ScopeOperationNames;
        public String ManageUserAccount;
        public String ManageUserIDNumber;

        public String ProvinceId;
        public String ProvinceName;
        public String CityId;
        public String CityName;
        public String CountyId;
        public String CountyName;
        public String Longitude;
        public String Latitude;
        public String Address;
        public String Introduce;
        public String ShopImg;
        public String BusinessLicenseImg;
        public String Img1;
        public String Img2;
        public String Img3;

        public String getShopAuthenticationId() {
                return StringUtils.convertNull(ShopAuthenticationId);
        }

    public String getManageUserAccount() {
        return StringUtils.convertNull(ManageUserAccount);
    }

    public String getManageUserIDNumber() {
        return StringUtils.convertNull(ManageUserIDNumber);
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

        public String getName() {
                return StringUtils.convertNull(Name);
        }

    public String getLongitude() {
        return StringUtils.convertNull(Longitude);
    }

    public String getLatitude() {
        return StringUtils.convertNull(Latitude);
    }

    public String getScopeOperationId() {
                return StringUtils.convertNull(ScopeOperationIds);
        }

        public String getScopeOperationName() {
                return StringUtils.convertNull(ScopeOperationNames);
        }

        public String getAddress() {
                return StringUtils.convertNull(Address);
        }

        public String getIntroduce() {
                return StringUtils.convertNull(Introduce);
        }

        public String getShopImg() {
                return StringUtils.convertNull(ShopImg);
        }

        public String getBusinessLicenseImg() {
                return StringUtils.convertNull(BusinessLicenseImg);
        }

        public String getImg1() {
                return StringUtils.convertNull(Img1);
        }

        public String getImg2() {
                return StringUtils.convertNull(Img2);
        }

        public String getImg3() {
                return StringUtils.convertNull(Img3);
        }
}
