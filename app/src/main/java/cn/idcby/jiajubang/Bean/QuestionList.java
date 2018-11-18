package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * bg_index_hywd
 * Created on 2018/4/19.
 */

public class QuestionList {
    public String QuestionId ;
    public String CreateUserName ;
    public String HeadIcon ;
    public String ReleaseTime ;
    public String Position ;
    public String Reward ;
    public String QuestionTitle ;
    public String QuestionExplain ;
    public String LikeNumber ;
    public String AnswerNumber ;
    public List<ImageThumb> AlbumsList ;

    public String getQuestionId() {
        return StringUtils.convertNull(QuestionId);
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
    public String getQuestionTitle() {
        return StringUtils.convertNull(QuestionTitle);
    }

    public String getLikeNumber() {
        return StringUtils.convertString2Count(LikeNumber) + "";
    }

    public String getAnswerNumber() {
        return StringUtils.convertString2Count(AnswerNumber) +"";
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }
}
