package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 收货地址
 * Created on 2018/3/24.
 */

public class ReceiveAddress implements Serializable{
    public String AddressId ;
    public String Contacts ;
    public String Account ;
    public String ProvinceId ;
    public String ProvinceName ;
    public String CityId ;
    public String CityName ;
    public String CountyId ;
    public String CountyName ;
    public String Address ;
    public int IsDefault = 0 ;

    public ReceiveAddress() {
    }

    public ReceiveAddress(String contacts, String account, String provinceId
            , String provinceName, String cityId, String cityName, String address) {
        Contacts = contacts;
        Account = account;
        ProvinceId = provinceId;
        ProvinceName = provinceName;
        CityId = cityId;
        CityName = cityName;
        Address = address;
    }

    public String getAddressId() {
        return StringUtils.convertNull(AddressId);
    }

    public String getContacts() {
        return StringUtils.convertNull(Contacts);
    }

    public String getAccount() {
        return StringUtils.convertNull(Account);
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
        return CountyId;
    }

    public String getCountyName() {
        return StringUtils.convertNull(CountyName);
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }

    public boolean isDefault() {
        return 1 == IsDefault;
    }
}
