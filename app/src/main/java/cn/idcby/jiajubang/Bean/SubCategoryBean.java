package cn.idcby.jiajubang.Bean;

// FIXME generate failure  field _$Resultdata151

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created by Administrator on 2018-04-18.
 */

public class SubCategoryBean {

    /**
     * Coulmn : 7
     * CoulmnText : 需求
     * TakeCoulmnId : aa718948-0f50-457a-85c8-1725403fb25f
     * CreateUserId : 74b5118a-b921-4d6b-a0da-5ecc569a5e85
     */

    private int Coulmn;
    private String CoulmnText;
    private String TakeCoulmnId;
    private String CreateUserId;

    public int getCoulmn() {
        return Coulmn;
    }

    public void setCoulmn(int Coulmn) {
        this.Coulmn = Coulmn;
    }

    public String getCoulmnText() {
        return StringUtils.convertNull(CoulmnText);
    }

    public void setCoulmnText(String CoulmnText) {
        this.CoulmnText = CoulmnText;
    }

    public String getTakeCoulmnId() {
        return StringUtils.convertNull(TakeCoulmnId);
    }

    public void setTakeCoulmnId(String TakeCoulmnId) {
        this.TakeCoulmnId = TakeCoulmnId;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public void setCreateUserId(String CreateUserId) {
        this.CreateUserId = CreateUserId;
    }
}
