package cn.idcby.jiajubang.Bean;

public class CheckPhone {
    /**
     * {"type":1,"errorcode":0,"message":"手机号可以使用！","resultdata":null}
     */
    public int errorcode;
    public int type;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
