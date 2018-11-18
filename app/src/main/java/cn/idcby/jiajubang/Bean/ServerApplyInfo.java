package cn.idcby.jiajubang.Bean;

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

public class ServerApplyInfo {
        public String ServiceAuthenticationId;
        public String TypeIds;
        public String TypeNames;
        public String PromiseIds;
        public String PromiseNames;
        public String ProvinceId;
        public String ProvinceName;
        public String CityId;
        public String CityName;
        public String CountyId;
        public String CountyName;
        public String BusinessProvinceId;
        public String BusinessProvinceName;
        public String BusinessCityId;
        public String BusinessCityName;
        public String Explain;

        public String getServiceAuthenticationId() {
                return StringUtils.convertNull(ServiceAuthenticationId);
        }

        public String getBusinessProvinceId() {
                return StringUtils.convertNull(BusinessProvinceId);
        }

        public String getBusinessProvinceName() {
                return StringUtils.convertNull(BusinessProvinceName);
        }

        public String getBusinessCityId() {
                return StringUtils.convertNull(BusinessCityId);
        }

        public String getBusinessCityName() {
                return StringUtils.convertNull(BusinessCityName);
        }

        public String getTypeIds() {
                return StringUtils.convertNull(TypeIds);
        }

        public String getTypeNames() {
                return StringUtils.convertNull(TypeNames);
        }

        public String getPromiseIds() {
                return StringUtils.convertNull(PromiseIds);
        }

        public String getPromiseNames() {
                return StringUtils.convertNull(PromiseNames);
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

        public String getExplain() {
                return StringUtils.convertNull(Explain);
        }
}
