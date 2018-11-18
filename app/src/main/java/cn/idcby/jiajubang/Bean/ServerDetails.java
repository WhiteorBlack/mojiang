package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 服务详细
 * Created on 2018/4/11.
 */

public class ServerDetails {
    public String IsCollection ;
    public String IsLike ;
    public String CreateUserId ;
    public String ServiceId ;
    public String HxName ;

    public String CarSideImg ;
    public String CarPositiveImg ;

    public String getCarSideImg() {
        return StringUtils.convertNull(CarSideImg);
    }

    public String getCarPositiveImg() {
        return StringUtils.convertNull(CarPositiveImg);
    }

    public String getHeadIcon() {
        return StringUtils.convertHttpUrl(HeadIcon);
    }

    public String HeadIcon ;

    public String getRealName() {
        return RealName;
    }

    public String RealName ;

    public String CategoryId ;
    public String CategoryName ;
    public String ServiceDescription ;
    public List<ImageThumb> AlbumsList ;

    public String getHxName() {
        return HxName;
    }

    public boolean isCollection() {
        return "1".equals(IsCollection);
    }

    public boolean isLike() {
        return "1".equals(IsLike);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public String H5Url;

    public String getServiceId() {
        return StringUtils.convertNull(ServiceId);
    }

    public String getCategoryId() {
        return StringUtils.convertNull(CategoryId);
    }

    public String getCategoryName() {
        return StringUtils.convertNull(CategoryName);
    }

    public String getServiceDescription() {
        return null == Explain ? StringUtils.convertNull(ServiceDescription) : Explain ;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList ;
    }

    //服务修改
    public String isFollow ;
    public String PayMoney ;
    public String PraiseRate ;
    public String ServiceTime ;
    public String Postion ;
    public String SingleAmount ;
    public String BussinessPostion ;
    public String NickName ;
    public String AuthenticationText ;
    public String Explain ;
    public String Mobile ;
    public String EvaluateListCount ;
    public List<WordType> TypeList ;
    public List<WordType> PromiseList ;
    public List<ServerComment> EvaluateList ;

    public boolean isFollow(){
        return "1".equals(isFollow) ;
    }

    public String getPayMoney() {
        return StringUtils.convertNull(PayMoney);
    }

    public String getPraiseRate() {
        return StringUtils.convertNull(PraiseRate);
    }

    public String getServiceTime() {
        return StringUtils.convertNull(ServiceTime);
    }

    public String getPostion() {
        return StringUtils.convertNull(Postion);
    }

    public String getBussinessPostion() {
        return StringUtils.convertNull(BussinessPostion);
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName);
    }

    public String getSingleAmount() {
        return StringUtils.convertNull(SingleAmount);
    }

    public String getAuthenticationText() {
        return StringUtils.convertNull(AuthenticationText);
    }

    public String getUserMobile(){
        return StringUtils.convertNull(Mobile) ;
    }

    public String getEvaluateListCount() {
        return "" +StringUtils.convertString2Count(EvaluateListCount);
    }

    public List<WordType> getTypeList() {
        return null == TypeList ? new ArrayList<WordType>() : TypeList;
    }

    public List<WordType> getPromiseList() {
        return null == PromiseList ? new ArrayList<WordType>() : PromiseList ;
    }
    public List<ServerComment> getEvaluateList() {
        return null == EvaluateList ? new ArrayList<ServerComment>() : EvaluateList ;
    }
}
