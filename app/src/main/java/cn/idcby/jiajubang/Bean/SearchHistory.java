package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 首页搜索历史
 * Created on 2018/4/18.
 */

public class SearchHistory {
    public String Coulmn ;
    public String Keyword ;
    public String SearchId ;

    public int getCoulmn() {
        return StringUtils.convertString2Count(Coulmn);
    }

    public String getKeyword() {
        return Keyword;
    }

    public String getSearchId() {
        return SearchId;
    }
}
