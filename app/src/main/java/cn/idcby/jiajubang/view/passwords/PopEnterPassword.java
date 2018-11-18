package cn.idcby.jiajubang.view.passwords;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PopEnterPassword  extends PopupWindow {

    private PasswordView pwdView;

    private View mMenuView;

    private Activity mContext;
    private String moneyStr;
    private String tipsMsg;

    public PopEnterPassword(final Activity context, String moneyStr, String tipsMsg) {

        super(context);

        this.mContext = context;
        this.moneyStr = moneyStr;
        this.tipsMsg = tipsMsg;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMenuView = inflater.inflate(R.layout.pop_enter_password, null);

        pwdView = (PasswordView) mMenuView.findViewById(R.id.pwd_view);

        //添加密码输入完成的响应
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String password) {
//                Log.i("###",password);
//                test.getTest(password);
                mPassPopInterImps.getPassPopInterImps(SkipUtils.PAY_PASSWORD_RIGHT,password);
                dismiss();

            }
        });

        // 监听X关闭按钮
        pwdView.getImgCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 监听键盘上方的返回
        pwdView.getVirtualKeyboardView().getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //
        pwdView.getTvMoney().setText(moneyStr);
        pwdView.getTvMessage().setText(tipsMsg);
        pwdView.getTvMissPassWord().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPassPopInterImps.getPassPopInterImps(SkipUtils.PAY_PASSWORD_RECALL,"找回密码");
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_add_ainm);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }


    private PassPopInterImps mPassPopInterImps;

    public void setPassPopInterImps(PassPopInterImps mPassPopInterImps) {
        this.mPassPopInterImps = mPassPopInterImps;
    }

    public interface PassPopInterImps {

        void getPassPopInterImps(int which,String str);
    }
}
