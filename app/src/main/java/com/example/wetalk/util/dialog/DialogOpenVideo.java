package com.example.wetalk.util.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.example.wetalk.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.rey.material.widget.TextView;


public class DialogOpenVideo {
    private Dialog dialog;

    @SuppressLint("InflateParams")
    protected DialogOpenVideo(final Builder builder) {
        dialog = new Dialog(builder.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        dialog.setCancelable(builder.cancelable);
        dialog.setContentView(R.layout.ko_exit_dialog);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView btn_quit = (TextView) dialog.findViewById(R.id.btn_cancel);
        final TextView  btn_ok = (TextView ) dialog.findViewById(R.id.btn_ok);
        final TextView btn_done = (TextView) dialog.findViewById(R.id.btn_done);
        final TextView btn_title = (TextView) dialog.findViewById(R.id.title_dialog);
        final PlayerView playerView = (PlayerView) dialog.findViewById(R.id.videoPlayerView);
        final LinearLayout ln_done = (LinearLayout) dialog.findViewById(R.id.ln_done);
        final RelativeLayout ln_ok_cancel = (RelativeLayout) dialog.findViewById(R.id.lnOKCancel);
        final LinearLayout lnCustomContent = dialog.findViewById(R.id.lnCustomContent);
        btn_title.setText(builder.title);
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(dialog.getContext()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(builder.urlVideo);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        if(builder.positiveText!=null && !builder.positiveText.isEmpty()){
            btn_ok.setVisibility(View.VISIBLE);
            btn_ok.setText(builder.positiveText);
        }else{
            btn_ok.setVisibility(View.INVISIBLE);
        }

        if(builder.negativeText!=null && !builder.negativeText.isEmpty()){
            btn_quit.setVisibility(View.VISIBLE);
            btn_quit.setText(builder.negativeText);
        }else{
            btn_quit.setVisibility(View.INVISIBLE);
        }

        if(builder.doneText!=null && !builder.doneText.isEmpty()){
            btn_done.setVisibility(View.VISIBLE);
            btn_done.setText(builder.doneText);
        }else{
            btn_done.setVisibility(View.INVISIBLE);
        }

        if(builder.isHaveDone()){
            ln_done.setVisibility(View.VISIBLE);
            ln_ok_cancel.setVisibility(View.GONE);
        }else{
            ln_ok_cancel.setVisibility(View.VISIBLE);
            ln_done.setVisibility(View.GONE);
        }

        if(builder.customView!=null){
            lnCustomContent.setVisibility(View.VISIBLE);
            lnCustomContent.addView(builder.customView,new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }else {
            lnCustomContent.setVisibility(View.GONE);
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if(builder.positiveClick!=null){
                    builder.positiveClick.onClick();
                }
            }
        });

        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if(builder.negativeClick!=null){
                    builder.negativeClick.onClick();
                }
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }
                if(builder.doneClick!=null){
                    builder.doneClick.onClick();
                }
            }
        });
    }

    public void show(){
        if(dialog!=null){
            if(dialog.isShowing())
                dialog.dismiss();
            dialog.show();
        }
    }

    public static class Builder {
        private Context mContext;
        private String title;
        private String urlVideo;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private String positiveText; //text phải
        private String negativeText; //text trái
        private SingleButtonCallback positiveClick;
        private SingleButtonCallback negativeClick;
        private SingleButtonCallback doneClick;
        private String doneText;
        private boolean isHaveDone;
        private View customView;

        public Builder(@NonNull Context context) {
            this.mContext = context;
            this.positiveText = context.getResources().getString(R.string.ok);
            this.negativeText = context.getResources().getString(R.string.cancel);
            this.doneText = context.getResources().getString(R.string.ok);
            this.isHaveDone = false;
            this.customView = null;
        }

        public Builder urlVideo(@NonNull String urlVideo) {
            this.urlVideo = urlVideo;
            return this;
        }
        public Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }

        public Builder content(@NonNull String content) {
            this.urlVideo = content;
            return this;
        }

        public Builder cancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside){
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder positiveText(String positiveText){
            this.positiveText = positiveText;
            return this;
        }

        public Builder negativeText(String negativeText){
            this.negativeText = negativeText;
            return this;
        }

        public Builder doneText(String doneText){
            this.isHaveDone = true;
            this.doneText = doneText;
            return this;
        }

        public Builder onPositive(SingleButtonCallback positiveClick){
            this.positiveClick = positiveClick;
            return this;
        }

        public Builder onNegative(SingleButtonCallback negativeClick){
            this.negativeClick = negativeClick;
            return this;
        }

        public Builder onDone(SingleButtonCallback doneClick){
            this.doneClick = doneClick;
            return this;
        }

        public Builder setCustomView(View customView){
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
        public DialogOpenVideo build() {
            return new DialogOpenVideo(this);
        }

        @UiThread
        public DialogOpenVideo show() {
            DialogOpenVideo dialog = build();
            dialog.show();
            return dialog;
        }
    }

    public interface SingleButtonCallback {
        void onClick();
    }
}
