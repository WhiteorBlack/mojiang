package cn.idcby.commonlibrary.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.idcby.commonlibrary.R;

public class MyCustomDialog extends Dialog {
  
    public MyCustomDialog(Context context) {  
        super(context);  
        setCancelable(false); 
    }  
  
    public MyCustomDialog(Context context, int theme) {  
        super(context, theme); 
        setCancelable(false); 
    }  
  
    public static class Builder {
        private Context context;
        private String title;  
        private String message;  
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param message message
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title  message
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title  title
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }  
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText positive
         * @param listener  listener
         * @return builder
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                OnClickListener listener) {
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                OnClickListener listener) {
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public MyCustomDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final MyCustomDialog dialog = new MyCustomDialog(context, R.style.my_custom_dialog);
            View layout = inflater.inflate(R.layout.my_custom_dialog, null);
            dialog.addContentView(layout, new LayoutParams(  
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title  
            ((TextView) layout.findViewById(R.id.my_cus_title)).setText(title);  
            // set the confirm button  
            if (positiveButtonText != null) {  
                ((Button) layout.findViewById(R.id.my_cus_positiveButton))  
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.my_cus_positiveButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.my_cus_positiveButton).setVisibility(  
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(R.id.my_cus_negativeButton))  
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.my_cus_negativeButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.my_cus_negativeButton).setVisibility(  
                        View.GONE);  
            }  
            // set the content message  
            if (message != null) {  
            	TextView tv = ((TextView) layout.findViewById(R.id.my_cus_message)) ;
            	tv.setText(message);  
            	tv.setOnLongClickListener(new View.OnLongClickListener() {
					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public boolean onLongClick(View v) {
                        // 得到剪贴板管理器
						ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
						cmb.setText(message.trim());
                        Toast.makeText(context, "文本已经复制到剪切板！", Toast.LENGTH_SHORT).show();
						return false;
					}
				});
//                ((TextView) layout.findViewById(R.id.my_cus_message)).setText(message);  
                
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
                TextView tv = ((TextView) layout.findViewById(R.id.my_cus_message)) ;
                tv.setVisibility(View.GONE);
                ((LinearLayout) layout.findViewById(R.id.my_cus_message_layout))  
                        .addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));  
            }  
            dialog.setContentView(layout);  
            return dialog;  
        }  
    } 
}