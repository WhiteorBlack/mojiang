package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-04-18.
 */

public class SubClass implements Serializable{
    private boolean isSelected = false ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserTakeCoulmnList() {
        return UserTakeCoulmnList;
    }

    public void setUserTakeCoulmnList(String userTakeCoulmnList) {
        UserTakeCoulmnList = userTakeCoulmnList;
    }

    public int getCoulmn() {
        return Coulmn;
    }

    public void setCoulmn(int coulmn) {
        Coulmn = coulmn;
    }

    public String UserTakeCoulmnList ;
    public int Coulmn ;
}
