package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/3.
 */

public class UserInfo implements Serializable {
    public String UserId;
    public String RealName;
    public String Gender;
    public String Birthday;
    public String Email;
    public String HeadIcon;
    public String Mobile;//无实际用途
    public String NickName;
    public String OICQ;
    public String WeChat;
    public String ProvinceId;
    public String CityId;
    public String CountyId;
    public String ProvinceName;
    public String CityName;
    public String CountyName;
    public String Address;
    public String TypeId;
    public String TypeName;
    public String IndustryId;
    public String IndustryName;
    public String PostText;
    public String Account;
    public String HxName;
    public String HxPwd;
    public String ShopId;
    public String PersonalitySignature;
    public int IndustryVAuthentication;
    public int InstallAuthentication;
    public int PersonalAuthentication;
    public int QAMasterAuthentication;
    public int ServiceAuthentication;
    public int FactoryAuthentication;
    public int CompanyAuthentication;
    public int ShopAuthentication;
    public int CollectNumber;
    public int LikeNumber;
    public int followMan;
    public int followShop;
    public int followfans;

    public String IndustryIds;
    public String IndustryNames;
    public String CompanyName;

    //获取其他人信息有用
    public String IsFollow ;

    public String getIndustryIds() {
        return StringUtils.convertNull(IndustryIds);
    }

    public String getIndustryNames() {
        return StringUtils.convertNull(IndustryNames);
    }

    public String getCompanyName() {
        return StringUtils.convertNull(CompanyName);
    }

    public boolean isFollow(){
        return "1".endsWith(IsFollow) ;
    }

    public int getIsHavePayPassWord() {
        return IsHavePayPassWord;
    }

    public void setIsHavePayPassWord(int isHavePayPassWord) {
        IsHavePayPassWord = isHavePayPassWord;
    }

    public int IsHavePayPassWord;
    public String Balance;
    public String integral;

    public String getOICQ() {
        return StringUtils.convertNull(OICQ);
    }

    public String getWeChat() {
        return StringUtils.convertNull(WeChat);
    }

    public String getProvinceId() {
        return StringUtils.convertNull(ProvinceId);
    }

    public String getCityId() {
        return StringUtils.convertNull(CityId);
    }

    public String getCountyId() {
        return StringUtils.convertNull(CountyId);
    }

    public String getProvinceName() {
        return StringUtils.convertNull(ProvinceName);
    }

    public String getCityName() {
        return StringUtils.convertNull(CityName);
    }

    public String getCountyName() {
        return StringUtils.convertNull(CountyName);
    }

    public String getAddress() {
        return StringUtils.convertNull(Address);
    }

    public String getTypeId() {
        return StringUtils.convertNull(TypeId);
    }

    public String getTypeName() {
        return StringUtils.convertNull(TypeName);
    }

    public String getIndustryId() {
        return StringUtils.convertNull(IndustryId);
    }

    public String getIndustryName() {
        return StringUtils.convertNull(IndustryName);
    }

    public String getPostText() {
        return StringUtils.convertNull(PostText);
    }

    public String getPersonalitySignature() {
        return StringUtils.convertNull(PersonalitySignature);
    }

    public String getAccount() {
        return StringUtils.convertNull(Account);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public int getCollectNumber() {
        return CollectNumber;
    }

    public int getFollowMan() {
        return followMan;
    }

    public int getFollowShop() {
        return followShop;
    }

    public int getFollowfans() {
        return followfans;
    }

    public String getBalance() {
        return MyUtils.isEmpty(Balance) ? "0" : StringUtils.convertStringNoPoint(Balance);
    }

    public String getIntegral() {
        return MyUtils.isEmpty(integral) ? "0" : StringUtils.convertStringNoPoint(integral);
    }

    public String getBirthday() {
        return StringUtils.convertDateToDay(Birthday);
    }

    public String getUserId() {
        return StringUtils.convertNull(UserId);
    }

    public String getRealName() {
        return StringUtils.convertNull(RealName);
    }

    public String getGender() {
        return StringUtils.convertNull(Gender);
    }

    public String getEmail() {
        return StringUtils.convertNull(Email);
    }

    public String getMobile() {
        return StringUtils.convertNull(Mobile);
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public String getHxPwd() {
        return StringUtils.convertNull(HxPwd);
    }

    public String getShopId() {
        return StringUtils.convertNull(ShopId);
    }

    //待缴纳保证金也属于认证通过
    public boolean isInstall() {
        return SkipUtils.APPLY_PAY_BOND_STATE == InstallAuthentication
                || SkipUtils.APPLY_ACCESS_STATE == InstallAuthentication;
    }
    public boolean isServices() {
        return SkipUtils.APPLY_PAY_BOND_STATE == ServiceAuthentication
                || SkipUtils.APPLY_ACCESS_STATE == ServiceAuthentication;
    }
    public boolean isShop() {
        return SkipUtils.APPLY_PAY_BOND_STATE == ShopAuthentication
                || SkipUtils.APPLY_ACCESS_STATE == ShopAuthentication;
    }

    public boolean isPersonal() {
        return SkipUtils.APPLY_ACCESS_STATE == PersonalAuthentication;
    }

    public boolean isFactory() {
        return SkipUtils.APPLY_ACCESS_STATE == FactoryAuthentication;
    }

    public boolean isCompany() {
        return SkipUtils.APPLY_ACCESS_STATE == CompanyAuthentication;
    }

    public boolean isIndusV() {
        return SkipUtils.APPLY_ACCESS_STATE == IndustryVAuthentication;
    }
    public boolean isMaster() {
        return SkipUtils.APPLY_ACCESS_STATE == QAMasterAuthentication;
    }

}
