package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/20.
 */

public class QuestionComment {
    public String QuestionAnswerID ;
    public String QuestionID ;
    public String AnswerContent ;
    public String AnswerLevel ;
    public String ReleaseTime ;
    public String LikeNumber ;
    public String CreateUserId ;
    public String CreateUserName ;
    public String IsLike ;
    public String HeadIcon ;
    public String IsOptimum ;
    public List<QuestionComment> ChildList ;

    public String getQuestionAnswerID() {
        return StringUtils.convertNull(QuestionAnswerID);
    }

    public String getQuestionID() {
        return StringUtils.convertNull(QuestionID);
    }

    public String getAnswerContent() {
        return StringUtils.convertNull(AnswerContent);
    }

    public String getAnswerLevel() {
        return StringUtils.convertNull(AnswerLevel);
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public String getLikeNumber() {
        return StringUtils.convertString2Count(LikeNumber)+"";
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public boolean isLike() {
        return "1".equals(IsLike);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public boolean isOptimum() {
        return "1".equals(IsOptimum);
    }

    public List<QuestionComment> getChildList() {
        return ChildList;
    }
}
