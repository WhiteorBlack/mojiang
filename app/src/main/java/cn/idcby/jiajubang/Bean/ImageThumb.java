package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 */

public class ImageThumb implements Serializable {
    public String OriginalImgUrl ;
    public String ThumbImgUrl ;

    public ImageThumb(String thumbImgUrl) {
        ThumbImgUrl = thumbImgUrl;
        OriginalImgUrl = thumbImgUrl;
    }

    public ImageThumb() {

    }

    public String getOriginalImgUrl() {
        return StringUtils.convertNull(OriginalImgUrl);
    }

    public String getThumbImgUrl() {
        return StringUtils.convertNull(ThumbImgUrl);
    }
}
