package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/20.
 */

public class QuestionCommentResult {
    public String answerNumber ;

    public String getAnswerNumber() {
        return StringUtils.convertString2Count(answerNumber) + "";
    }
}
