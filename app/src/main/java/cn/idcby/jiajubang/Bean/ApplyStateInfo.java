package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/3.
 */

public class ApplyStateInfo {
    public String UserId ;
    public String RealName ;
    public String IndustryVAuthenticationText ;
    public String InstallAuthenticationText ;
    public String PersonalAuthenticationText ;
    public String QAMasterAuthenticationText ;
    public String ServiceAuthenticationText ;
    public String FactoryAuthenticationText ;
    public String CompanyAuthenticationText ;
    public String ShopAuthenticationText ;

    public String PersonalAuthentication ;

    public boolean isPersonalFall(){
        return !(""+ SkipUtils.APPLY_ACCESS_STATE).equals(PersonalAuthentication) ;
    }

    public String getUserId() {
        return StringUtils.convertNull(UserId);
    }

    public String getRealName() {
        return StringUtils.convertNull(RealName);
    }

    public String getIndustryVAuthenticationText() {
        return StringUtils.convertNull(IndustryVAuthenticationText);
    }

    public String getInstallAuthenticationText() {
        return StringUtils.convertNull(InstallAuthenticationText);
    }

    public String getPersonalAuthenticationText() {
        return StringUtils.convertNull(PersonalAuthenticationText);
    }

    public String getQAMasterAuthenticationText() {
        return StringUtils.convertNull(QAMasterAuthenticationText);
    }

    public String getServiceAuthenticationText() {
        return StringUtils.convertNull(ServiceAuthenticationText);
    }

    public String getFactoryAuthenticationText() {
        return StringUtils.convertNull(FactoryAuthenticationText);
    }

    public String getCompanyAuthenticationText() {
        return StringUtils.convertNull(CompanyAuthenticationText);
    }

    public String getShopAuthenticationText() {
        return StringUtils.convertNull(ShopAuthenticationText);
    }
}
