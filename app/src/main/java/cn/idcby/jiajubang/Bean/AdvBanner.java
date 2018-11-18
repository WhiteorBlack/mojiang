package cn.idcby.jiajubang.Bean;

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
 * Created by mrrlb on 2018/2/27.
 */

public class AdvBanner {
    public String Title;
    public String ImgUrl;
    public String LinkInResourceID;
    public String LinkOutResourceUrl;
    public String BodyContent;
    public String LinkType;

    public String getImgUrl() {
        return ImgUrl;
    }

    public String getTitle() {
        return Title;
    }

    public String getLinkInResourceID() {
        return LinkInResourceID;
    }

    public String getLinkOutResourceUrl() {
        return LinkOutResourceUrl;
    }

    public String getBodyContent() {
        return BodyContent;
    }

    public boolean isInnerResouce() {
        return "1".equals(LinkType);
    }
}
