package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * bg_index_hywd--详细
 * Created on 2018/4/19.
 */

public class QuestionDetails {
    public String QuestionId ;
    public String HeadIcon ;
    public String CreateUserName ;
    public String CreateUserId ;
    public String ReleaseTime ;
    public String Position ;
    public String Reward ;
    public String QuestionType ;
    public String QuestionTitle ;
    public String QuestionExplain ;
    public String AnswerNumber ;
    public String LikeNumber ;
    public String ApplyText ;
    public String CategoryName ;
    public String CategoryId ;
    public String IsAdopt ;
    public List<ImageThumb> AlbumsList ;

    public String getCategoryName() {
        return StringUtils.convertNull(CategoryName);
    }

    public String getCategoryId() {
        return StringUtils.convertNull(CategoryId);
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public String getQuestionTitle() {
        return QuestionTitle;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
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

    public String getReward() {
        return StringUtils.convertNull(Reward);
    }

    public String getQuestionExplain() {
        return StringUtils.convertNull(QuestionExplain);
    }

    public String getLikeNumber() {
        return StringUtils.convertString2Count(LikeNumber) + "";
    }

    public String getApplyText() {
        return StringUtils.convertNull(ApplyText);
    }

    public String getAnswerNumber() {
        return StringUtils.convertString2Count(AnswerNumber) +"";
    }

    public boolean isAdopt(){
        return "1".equals(IsAdopt) ;
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }
}
