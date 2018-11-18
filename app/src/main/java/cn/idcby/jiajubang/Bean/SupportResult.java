package cn.idcby.jiajubang.Bean;

/**
 * Created on 2018/3/24.
 */

public class SupportResult {
    public int LikeNumber = 0 ;
    public int AddOrDelete = 1;

    public boolean isAddOrDelete(){
        return 1 == AddOrDelete ;
    }
}
