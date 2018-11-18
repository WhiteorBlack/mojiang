package cn.idcby.jiajubang.Bean;

/**
 * Created on 2018/4/18.
 */

public class VersionBean {
    public Integer Code = 0;
    public String Msg;
    public boolean Success;
    public String DevIdentity;
    public Version JsonData;

    public Integer getCode() {
        return Code;
    }

    public String getMsg() {
        return Msg;
    }

    public boolean isSuccess() {
        return Success;
    }

    public String getDevIdentity() {
        return DevIdentity;
    }

    public Version getJsonData() {
        return JsonData;
    }
}
