package cn.idcby.jiajubang.events;

/**
 * Created on 2018/4/20.
 */

public class BusEvent {

    /**
     * 有未读消息
     */
    public static class UnreadMsgEvent{
        private boolean isHas = false ;

        public boolean isHas() {
            return isHas;
        }

        public UnreadMsgEvent(boolean isHas) {
            this.isHas = isHas;
        }
    }

    /**
     * 更新app结果
     */
    public static class UpdateEvent{
        private boolean isNext = false ;

        public UpdateEvent(boolean isNext){
            this.isNext = isNext ;
        }

        public boolean isNext(){
            return isNext ;
        }
    }

    public static class LocationUpdate{
        private boolean isUpdate = false ;

        public boolean isUpdate() {
            return isUpdate;
        }

        public LocationUpdate(boolean isRefresh) {
            this.isUpdate = isRefresh;
        }
    }

    public static class HxLoginStateEvent{
        private boolean isLogin = false ;

        public boolean isReLogin() {
            return isLogin;
        }

        public HxLoginStateEvent(boolean isLogin) {
            this.isLogin = isLogin;
        }
    }

    public static class NeedOrderRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public NeedOrderRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }

    public static class GoodOrderRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public GoodOrderRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }

    public static class ServerOrderRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public ServerOrderRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }

    public static class ReceiveOrderRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public ReceiveOrderRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }
    public static class MySendRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public MySendRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }

    public static class OrderCountRefresh{
        private boolean isRefresh = false ;

        public boolean isRefresh() {
            return isRefresh;
        }

        public OrderCountRefresh(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }
    }

    public static class LoginOutEvent{
        private boolean isOut = false ;

        public boolean isOut() {
            return isOut;
        }

        public LoginOutEvent(boolean isOut) {
            this.isOut = isOut;
        }
    }

    public static class ServerPicChangedEvent{
        private boolean isChanged = false ;

        public boolean isChanged() {
            return isChanged;
        }

        public ServerPicChangedEvent(boolean isOut) {
            this.isChanged = isOut;
        }
    }

    public static class OtherLoginEvent{
        private boolean isLogin = false ;

        public boolean isLogin() {
            return isLogin;
        }

        public OtherLoginEvent(boolean isLogin) {
            this.isLogin = isLogin;
        }
    }

    public static class WxPayEvent{
        private int errorCode ;

        public WxPayEvent(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    public static class StoreSupportEvent{
        private boolean isSupport ;

        public StoreSupportEvent(boolean isSupport) {
            this.isSupport = isSupport;
        }
        public boolean isSupport() {
            return isSupport;
        }
    }

    public static class DirectSellingLatestEvent{
        private boolean isClicked ;

        public DirectSellingLatestEvent(boolean isClicked) {
            this.isClicked = isClicked;
        }

        public boolean isClicked() {
            return isClicked;
        }
    }

}
