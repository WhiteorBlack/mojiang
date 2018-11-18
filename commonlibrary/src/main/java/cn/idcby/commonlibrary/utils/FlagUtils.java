package cn.idcby.commonlibrary.utils;

/**
 * Created by mrrlb on 2016/9/28.
 */
public class FlagUtils {


    public final static int FIRST_OK = 4;
    public final static int FIRST_ERROR = 5;
    public final static int FIRST_NO_DATA = 6;

    public final static int REFRESH_OK = 7;
    public final static int REFRESH_ERROR = 8;
    public final static int REFRESH_NO_DATA = 9;

    public final static int LOAD_MORE_OK = 10;
    public final static int LOAD_MORE_ERROR = 11;
    public final static int LOAD_MORE_NO_DATA = 12;


    public final static String FLAG_FIRST = "first";
    public final static String FLAG_REFRESH = "refresh";
    public final static String FLAG_LOADING_MORE = "loadingMore";
    public final static String FLAG_MSG_COUNT = "messageCount";
    public final static String FLAG_BROADCAST_LOGIN_OR_LOGIN_OUT_SUCCESS
            = "cn.idcby.login.or.login.out.success";
    public final static String FLAG_BROADCAST_MSG_COUNT_CHANGE
            = "cn.idcby.message.change.count";

}
