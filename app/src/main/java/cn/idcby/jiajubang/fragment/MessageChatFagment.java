package cn.idcby.jiajubang.fragment;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import java.util.List;

import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.hxmsg.VideoCallActivity;
import cn.idcby.jiajubang.hxmsg.VoiceCallActivity;
import cn.idcby.jiajubang.utils.SkipUtils;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created on 2018/4/21.
 */

public class MessageChatFagment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper
        ,EasyPermissions.PermissionCallbacks {

    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;


    @Override
    protected void setUpView() {
        setChatFragmentHelper(this);
        hideTitleBar();

        super.setUpView();


    }

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        //extend menu items
        inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
    }


    @Override
    public void onSetMessageAttributes(EMMessage message) {

    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {
        SkipUtils.toOtherUserInfoActivity(getContext() ,username ,true);
    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            String[] permisses = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE} ;
            if(EasyPermissions.hasPermissions(getContext() ,permisses)){
                startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                        .putExtra("isComingCall", false));

                inputMenu.hideExtendMenuContainer();
            }else{
                EasyPermissions.requestPermissions(this,"语音通话需要麦克风权限和存储权限",100,permisses) ;
            }
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            String[] permisses = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            if(EasyPermissions.hasPermissions(getContext() ,permisses)){
                startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                        .putExtra("isComingCall", false));
                // videoCallBtn.setEnabled(false);
                inputMenu.hideExtendMenuContainer();
            }else{
                EasyPermissions.requestPermissions(this,"语音通话需要麦克风权限和存储权限",101,permisses) ;
            }
        }
    }


    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(100 == requestCode){
            startVoiceCall() ;
        }else if(101 == requestCode){
            startVideoCall() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(getContext() ,"拒绝了相关权限，会导致部分功能异常") ;
    }
}
