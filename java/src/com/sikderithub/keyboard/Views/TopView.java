package com.sikderithub.keyboard.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.inputmethod.latin.common.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.sikderithub.keyboard.BuildConfig;
import com.sikderithub.keyboard.CommonMethod;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Utils.Common;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;
import com.sikderithub.keyboard.Utils.PrefHelper;
import com.sikderithub.keyboard.Views.NativeAd.TemplateView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TopView extends RelativeLayout {
    private static final String TAG = "TopView";
    private TemplateView mNativeAdView;
    private ImageView imgUpdate;
    private View view;
    private enum ContentType{
        AD,
        UPDATE,
        NONE
    }

    public TopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.suggestionStripViewStyle);
    }

    public TopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_top_view, this);
        view = this;
        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
            setBackgroundColor(CustomThemeHelper.selectedCustomTheme.dominateColor);
        }
        mNativeAdView = findViewById(R.id.templateView);
        imgUpdate = findViewById(R.id.imgUpdate);

        setVisibility(GONE);

    }

    public void onStartInputView(boolean isRestarting){
        Log.d(TAG, "onStartInputView: "+isRestarting);
        if(true){
            ContentType ct = getContentType();
            Log.d(TAG, "onStartInputView: "+ct.name());
            if(ct==ContentType.NONE){
                setVisibility(GONE);
            }else {

                if(ct==ContentType.AD){
                    //show ad content
                    showAd();
                }else {
                    //show update content
                    showUpdateBanner();
                }

            }
        }
    }

    private void showUpdateBanner() {
        loadUpdateBanner();
    }

    private void loadUpdateBanner() {

        mNativeAdView.setVisibility(GONE);

        String updateImgUrl = MyApp.getUpdateInfo().image_url;
        if(updateImgUrl!=null && !updateImgUrl.isEmpty()){
            imgUpdate.setVisibility(VISIBLE);
            setVisibility(VISIBLE);

            Glide.with(getContext())
                    .load(updateImgUrl)
                    .into(imgUpdate);

            if(MyApp.getUpdateInfo().referLink!=null && !MyApp.getUpdateInfo().referLink.isEmpty()){
                imgUpdate.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonMethod.INSTANCE.openLink(getContext(), MyApp.getUpdateInfo().referLink);
                    }
                });
            }
        }else {
            this.setVisibility(GONE);
        }
    }

    private void showAd() {
        imgUpdate.setVisibility(GONE);
        if(!Common.isAdShownAllowed()){
            Log.d(TAG, "showAd: not allowed");
            setVisibility(VISIBLE);
            mNativeAdView.setVisibility(GONE);
            return;

        }
        mNativeAdView.setVisibility(VISIBLE);
        loadNativeAd();
    }

    private ContentType getContentType(){
        if(!isToadyAdShown()){
            return ContentType.AD;
        }else if(isUpdateAvailable()){
            return ContentType.UPDATE;
        }else {
            return ContentType.NONE;
        }

    }


    private int getViewVisibility() {
        if(isUpdateAvailable() || !isToadyAdShown()){
            return VISIBLE;
        }
        return GONE;
    }


    private boolean isUpdateAvailable(){
        return MyApp.getUpdateInfo().version_code > BuildConfig.VERSION_CODE && MyApp.getUpdateInfo().status==1;
    }

    private boolean isToadyAdShown(){
        SimpleDateFormat sdf = new SimpleDateFormat(Common.LAST_AD_DATE_FORMAT,  Locale.getDefault());
        String todayDateInString = sdf.format(new Date());
        return PrefHelper.getPref(todayDateInString, false);
    }

    private void setAdAsShown(){
        SimpleDateFormat sdf = new SimpleDateFormat(Common.LAST_AD_DATE_FORMAT,  Locale.getDefault());
        String todayDateInString = sdf.format(new Date());
        PrefHelper.putPref(todayDateInString, true);
    }

    private void loadNativeAd() {
        AdLoader adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {
//                        NativeTemplateStyle styles = new
//                                NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
//                        mNativeAdView.setStyles(styles);
                    mNativeAdView.setNativeAd(nativeAd);
                    setVisibility(VISIBLE);

                    mNativeAdView.setVisibility(VISIBLE);
                    requestLayout();
                    setAdAsShown();
                    Log.d(TAG, "loadNativeAd: showing");

                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d(TAG, "onAdFailedToLoad: "+loadAdError.getMessage());
                        mNativeAdView.setVisibility(GONE);
                        view.setVisibility(GONE);
                        measure(0, 0);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
