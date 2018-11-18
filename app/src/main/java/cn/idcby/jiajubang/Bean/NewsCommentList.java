package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/22.
 */

public class NewsCommentList extends BaseComment{
    public String ArticleCommentID ;
    public List<NewsCommentList> ChildList ;

    public String getArticleCommentID() {
        return StringUtils.convertNull(ArticleCommentID);
    }

    public List<NewsCommentList> getChildList() {
        return ChildList;
    }

}
