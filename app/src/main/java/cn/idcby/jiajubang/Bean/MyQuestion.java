package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/3.
 */

public class MyQuestion {
    public String QuestionId ;
    public String Account ;
    public String HeadIcon ;
    public String ReleaseTime ;
    public String Position ;
    public String Reward ;
    public String QuestionTitle ;
    public String QuestionExplain ;
    public String LikeNumber ;
    public String AnswerNumber ;
    public List<QuestionComment> Answer ;
    public List<ImageThumb> AlbumsList ;

    public String getQuestionId() {
        return StringUtils.convertNull(QuestionId);
    }

    public String getAccount() {
        return StringUtils.convertNull(Account);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getReleaseTime() {
        return StringUtils.convertDateToDay(ReleaseTime);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getReward() {
        return StringUtils.convertNull(Reward);
    }

    public String getQuestionTitle() {
        return StringUtils.convertNull(QuestionTitle);
    }

    public String getQuestionExplain() {
        return StringUtils.convertNull(QuestionExplain);
    }

    public String getLikeNumber() {
        return StringUtils.convertNull(LikeNumber);
    }

    public String getAnswerNumber() {
        return StringUtils.convertNull(AnswerNumber);
    }

    public List<QuestionComment> getAnswer() {
        return null == Answer ? new ArrayList<QuestionComment>() : Answer;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }
}
