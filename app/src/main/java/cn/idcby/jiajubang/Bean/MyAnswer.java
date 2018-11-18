package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/3.
 */

public class MyAnswer {
    public String QuestionID ;
    public String QAcreateUserName ;
    public String HeadIcon ;
    public String ReleaseTime ;
    public String Position ;
    public String Reward ;
    public String QuestionTitle ;
    public String QuestionExplain ;
    public String CreateHeadIcon ;
    public String CreateUserName ;
    public String AnswerReleaseTime ;
    public String IsOptimum ;
    public String LikeNumber ;
    public String AnswerContent ;

    public String getQuestionID() {
        return StringUtils.convertNull(QuestionID);
    }

    public String getQAcreateUserName() {
        return StringUtils.convertNull(QAcreateUserName);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getQuestionTitle() {
        return StringUtils.convertNull(QuestionTitle);
    }

    public String getQuestionExplain() {
        return StringUtils.convertNull(QuestionExplain);
    }

    public String getCreateHeadIcon() {
        return StringUtils.convertNull(CreateHeadIcon);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getAnswerReleaseTime() {
        return StringUtils.convertNull(AnswerReleaseTime);
    }

    public String getReward() {
        return StringUtils.convertNull(Reward);
    }

    public boolean isOptimum() {
        return "1".equals(IsOptimum);
    }

    public String getLikeNumber() {
        return StringUtils.convertNull(LikeNumber);
    }

    public String getAnswerContent() {
        return StringUtils.convertNull(AnswerContent);
    }
}
