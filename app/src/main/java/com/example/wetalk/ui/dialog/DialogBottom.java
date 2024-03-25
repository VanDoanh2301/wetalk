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
import com.rey.material.widget.ImageView;
import com.rey.material.widget.RelativeLayout;
import com.rey.material.widget.TextView;


public class DialogBottom {
    private Dialog dialog;
    @SuppressLint("InflateParams")
    protected DialogBottom(final Builder builder) {
        dialog = new Dialog(builder.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        dialog.setCancelable(builder.cancelable);
        dialog.setContentView(R.layout.more_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final RelativeLayout btn_more = (RelativeLayout) dialog.findViewById(R.id.bt_more_tips);
        final RelativeLayout btn_report = (RelativeLayout) dialog.findViewById(R.id.bt_report);
        final TextView tvReport  = (TextView) dialog.findViewById(R.id.tv_dialog_2);
        final TextView tvMore = (TextView) dialog.findViewById(R.id.tv_dialog_1);
        final ImageView imgMore = (ImageView) dialog.findViewById(R.id.img_dialog_1);
        final ImageView imgReport = (ImageView) dialog.findViewById(R.id.img_dialog_2);

        if(builder.imgMore !=null){
            imgMore.setVisibility(View.VISIBLE);
            imgMore.setImageResource(builder.imgMore);
        }else{
            imgMore.setVisibility(View.GONE);
        }
        if(builder.imgReport !=null){
            imgReport.setVisibility(View.VISIBLE);
            imgReport.setImageResource(builder.imgMore);
        }else{
            imgReport.setVisibility(View.GONE);
        }

        if(builder.tvMore !=null && !builder.tvMore.isEmpty()){
            btn_more.setVisibility(View.VISIBLE);
            tvMore.setText(builder.tvMore);
        }else{
            btn_more.setVisibility(View.GONE);
        }
        if(builder.tvReport !=null && !builder.tvReport.isEmpty()){
            btn_report.setVisibility(View.VISIBLE);
            tvReport.setText(builder.tvReport);
        }else{
            btn_report.setVisibility(View.GONE);
        }

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if (builder.moreTipClick != null) {
                    builder.moreTipClick.onClick();
                }
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if (builder.reportClick != null) {
                    builder.reportClick.onClick();
                }
            }
        });


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
        private String tvMore;
        private String tvReport;
        private Integer imgMore;
        private Integer imgReport;
        private SingleButtonCallback moreTipClick;
        private SingleButtonCallback reportClick;
        private boolean isHaveDone;
        private View customView;

        public Builder(@NonNull Context context) {
            this.mContext = context;
            this.isHaveDone = false;
            this.customView = null;
        }
        public Builder setImageMore(Integer imgMore) {
            this.imgMore = imgMore;
            return this;
        }
        public Builder setImageReport(Integer imgReport) {
            this.imgReport= imgReport;
            return this;
        }
        public Builder setTextMore(String tvMore) {
            this.tvMore = tvMore;
            return this;
        }
        public Builder setTextReport(String tvReport) {
            this.tvReport= tvReport;
            return this;
        }
        public Builder onMoreTip(SingleButtonCallback moreTipClick) {
            this.moreTipClick = moreTipClick;
            return this;
        }

        public Builder onReport(SingleButtonCallback reportClick) {
            this.reportClick = reportClick;
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
        public DialogBottom build() {
            return new  DialogBottom (this);
        }

        @UiThread
        public  DialogBottom  show() {
            DialogBottom dialog = build();
            dialog.show();
            return dialog;
        }
    }

    public interface SingleButtonCallback {
        void onClick();
    }
}

