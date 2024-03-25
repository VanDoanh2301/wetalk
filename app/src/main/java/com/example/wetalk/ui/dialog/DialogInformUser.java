package com.example.wetalk.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;


import com.example.wetalk.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;


public class DialogInformUser {
    private Dialog dialog;

    @SuppressLint("InflateParams")
    protected DialogInformUser(final Builder builder) {
        dialog = new Dialog(builder.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        dialog.setCancelable(builder.cancelable);
        dialog.setContentView(R.layout.user_dialog);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Button btn_chat = (Button) dialog.findViewById(R.id.bt_chat);
        final Button btn_delete = (Button) dialog.findViewById(R.id.bt_delete);
        final TextView tvName = (TextView) dialog.findViewById(R.id.txt_name);
        final TextView tvDate = (TextView ) dialog.findViewById(R.id.txt_date);
        final TextView  tvAddress = (TextView ) dialog.findViewById(R.id.txt_address);
        final TextView tvPhoneNumber = (TextView ) dialog.findViewById(R.id.txt_phone);
        final TextView tvGender= (TextView ) dialog.findViewById(R.id.txt_gender);

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if (builder.openChat != null) {
                    builder.openChat.onClick();
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if (builder.openDelete != null) {
                    builder.openDelete.onClick();
                }
            }
        });

        if(builder.tvName !=null && !builder.tvName.isEmpty()){
            tvName.setText(builder.tvName);
        }else{

        }
        if(builder.tvDate !=null && !builder.tvDate.isEmpty()){
            tvDate.setText(builder.tvDate);
        }else{

        }
        if(builder.tvAddress !=null && !builder.tvAddress.isEmpty()){
            tvAddress.setText(builder.tvAddress);
        }else{

        }
        if(builder.phoneNumber !=null && !builder.phoneNumber.isEmpty()){
            tvPhoneNumber.setText(builder.phoneNumber);
        }else{

        }
        if(builder.gender !=null && !builder.gender.isEmpty()){
            tvGender.setText(builder.gender);
        }else{

        }
    }

    public void show() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog.show();
        }
    }

    public static class Builder {
        private Context mContext;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private SingleButtonCallback openChat;
        private SingleButtonCallback openDelete;
        private String tvName;
        private String tvDate;
        private String tvAddress;
        private String phoneNumber;
        private String gender;
        private boolean isHaveDone;
        private View customView;

        public Builder(@NonNull Context context) {
            this.mContext = context;
            this.isHaveDone = false;
            this.customView = null;
        }

        public Builder onChat(SingleButtonCallback openChat) {
            this.openChat = openChat;
            return this;
        }

        public Builder onDelete(SingleButtonCallback openDelete) {
            this.openDelete = openDelete;
            return this;
        }

        public Builder onName (String tvName) {
            this.tvName = tvName;
            return this;
        }
        public Builder onDate (String tvDate) {
            this.tvDate = tvDate;
            return this;
        }
        public Builder onAddress(String tvAddress) {
            this.tvAddress = tvAddress;
            return this;
        }
        public Builder onPhone (String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder onGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder setCustomView(View customView) {
            this.customView = customView;
            return this;
        }

        public boolean isHaveDone() {
            return isHaveDone;
        }

        public void setHaveDone(boolean haveDone) {
            isHaveDone = haveDone;
        }

        @UiThread
        public DialogInformUser build() {
            return new DialogInformUser (this);
        }

        @UiThread
        public DialogInformUser  show() {
            DialogInformUser  dialog = build();
            dialog.show();
            return dialog;
        }
    }

    public interface SingleButtonCallback {
        void onClick();
    }
}

