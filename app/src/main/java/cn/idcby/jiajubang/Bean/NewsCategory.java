package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

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
 * Created by mrrlb on 2018/2/9.
 */

public class NewsCategory implements Serializable{
        private boolean isSelelcted = false ;

        public boolean isSelelcted() {
                return isSelelcted;
        }

        public void setSelelcted(boolean selelcted) {
                isSelelcted = selelcted;
        }

        public String CategoryCode;
        public String CategoryID;
        public String CategoryTitle;
        public String ChanneCode;
        public String ChannelID;
        public String ChannelTitle;
        public String CreateDate;
        public String CreateUserId;
        public String CreateUserName;
        public double DeleteMark;
        public String Description;
        public double EnabledMark;
        public String IconUrl;
        public String ImgUrl;
        public double Layer;
        public double SortCode;

        public NewsCategory() {
        }

        public NewsCategory(String categoryID, String categoryTitle) {
                CategoryID = categoryID;
                CategoryTitle = categoryTitle;
        }

        public String getCategoryTitle() {
                return StringUtils.convertNull(CategoryTitle);
        }

        public String getCategoryID() {
                return StringUtils.convertNull(CategoryID);
        }
}
