package com.sikderithub.keyboard.Views;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.inputmethod.utils.GkEngine;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.gson.Gson;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;

public class GkView extends RelativeLayout implements View.OnTouchListener{
    private boolean isAdLoaded = false;
    private static final String TAG = "GkView";

    private static final int AD_INTERVAL_COUNT = 3;
    private static final long GK_INTERVAL = 10 * 1000;
    private static final long COUNTDOWN_INTERVAL = 10 * 1000;
    private final Handler handler;
    private final Runnable runnable;
    private final View lottieCorrect;
    private final View lottieWrong;
    private final AdRequest adRequest;
    private final CountDownTimer countDownTimer;
    private int gkShowed = 0;

    private final LinearLayout mAdView;
    private final RelativeLayout mGkHolder;
    private final LinearLayout mOptionalGkHolder;
    private final LinearLayout mNonOptionalGkHolder;

    private TextView nonOptionGkText;
    private TextView optionGkText;
    private RadioButton option1;
    private RadioButton  option2;
    private GkEngine gkEngine;
    private ImageView addBookMark;

    private SavedGkViews savedGkViews;
    private RadioGroup optionRadioGroup;



    private boolean pause = true;
    private int isWindowVisible;

    private int contentReloadCount = 0;
    private int contentReloadThreshold = 3;

    private enum CURR_CONTENT{
        AD,
        SINGLE_OPTION_GK,
        MULTIPLE_OPTION_GK
    }





    public interface GkViewTouchListener{
        void onTouchStart();
        void onTouchRelease();
    }

    public GkViewTouchListener gkViewTouchListener;
    private CURR_CONTENT currContent = null;
    private GkEngine.CurrentGk currentGk = null;
    PopupWindow popupWindow;
    AdView bannerAd;
    public GkView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gkViewStyle);
    }

    @SuppressLint("MissingInflatedId")
    public GkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
            Drawable bgDrawable = CustomThemeHelper.getKeyboardBackgroundDrawable(context,CustomThemeHelper.selectedCustomTheme);
            Theme theme = CustomThemeHelper.selectedCustomTheme;
            if (bgDrawable!=null && theme!=null){
                this.setBackground(bgDrawable);
            }
        }
        gkViewTouchListener  = new GkViewTouchListener() {
            @Override
            public void onTouchStart() {
                pauseEngine();
            }

            @Override
            public void onTouchRelease() {
                startEngine();
            }
        };
        adRequest = new AdRequest.Builder().build();
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.gk_view, this);


        mGkHolder= findViewById(R.id.gkHolder);
        mNonOptionalGkHolder = findViewById(R.id.nonOptionalGkHolder);
        mOptionalGkHolder = findViewById(R.id.optionalGkHolder);

        nonOptionGkText = findViewById(R.id.nonOptionGkText);
        optionGkText = findViewById(R.id.optionGkText);
        addBookMark = findViewById(R.id.imgSaveGk);


        option1 = findViewById(R.id.txtOption1);
        option2 = findViewById(R.id.option2);
        optionRadioGroup = findViewById(R.id.optionGroup);

        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
            setBackgroundColor(CustomThemeHelper.selectedCustomTheme.dominateColor);
            optionGkText.setTextColor(CustomThemeHelper.selectedCustomTheme.bodyTextColor);
            nonOptionGkText.setTextColor(CustomThemeHelper.selectedCustomTheme.bodyTextColor);
            option1.setTextColor(CustomThemeHelper.selectedCustomTheme.bodyTextColor);
            option2.setTextColor(CustomThemeHelper.selectedCustomTheme.bodyTextColor);

            option1.setButtonTintList(ColorStateList.valueOf(CustomThemeHelper.selectedCustomTheme.bodyTextColor));
            option2.setButtonTintList(ColorStateList.valueOf(CustomThemeHelper.selectedCustomTheme.bodyTextColor));

        }else{
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.GkView, defStyleAttr, R.style.GkView);

            Log.d(TAG, "size typed: "+typedArray.getIndexCount());


            int txtColor = typedArray.getColor(R.styleable.GkView_questIonTextColor, 0);
            Log.d(TAG, "GkView: "+txtColor);
            optionGkText.setTextColor(txtColor);
            nonOptionGkText.setTextColor(txtColor);
            option1.setTextColor(txtColor);
            option2.setTextColor(txtColor);

            option2.setButtonTintList(ColorStateList.valueOf(txtColor));
            option1.setButtonTintList(ColorStateList.valueOf(txtColor));
        }

        View popupView = inflater.inflate(R.layout.dialog_ans_result, null);

        lottieCorrect =  popupView.findViewById(R.id.lottie_correct);
        lottieWrong = popupView.findViewById(R.id.lottie_wrong);


        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        popupWindow = new PopupWindow(popupView, width, height, true);

        mAdView = findViewById(R.id.adView);
        bannerAd = findViewById(R.id.bannerAd);
        bannerAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                isAdLoaded = false;
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isAdLoaded = true;
            }
        });



        countDownTimer = new CountDownTimer( ((long) MyApp.getConfig().content_interval * 60 * 1000) , ((long) MyApp.getConfig().content_interval * 60 * 1000)) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: "+millisUntilFinished);
            }

            @Override
            public void onFinish() {
                showContent();
            }
        };

        addBookMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookMark();
            }
        });

        option1.setOnClickListener(v -> {
            try {
                gkOptionClick(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        option2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gkOptionClick(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        gkEngine = new GkEngine();
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                if(!pause){
                    showContent();

                }
            }
        };

        showContent();


    }

    private void loadBannerAd(){
        bannerAd.loadAd(adRequest);
    }

    public void setSavedGkView(SavedGkViews savedGkView){
        this.savedGkViews = savedGkView;
    }

    private void addBookMark() {
        if(currentGk!=null && currentGk.gk!=null ){
            GkEngine.addToSavedGk(currentGk);
            SavedGkViews.addNewToList(currentGk.gk);
        }

        Toast.makeText(getContext(), "Gk Saved", Toast.LENGTH_SHORT).show();
    }

    private void gkOptionClick(int i) throws InterruptedException {
        option1.setEnabled(false);
        option2.setEnabled(false);
        Log.d(TAG, "gkOptionClick: ");
        pauseEngine();
        if(currContent!=null && currContent==CURR_CONTENT.MULTIPLE_OPTION_GK && currentGk!=null && currentGk.gk!=null && currentGk.gk.has_option==1){
            GkEngine.setGkAsShown(currentGk);
            String selectedOption = (i==1) ? currentGk.gk.option1 : currentGk.gk.option2;
            if(currentGk.gk.answer.equals(selectedOption)){
                //correct ans clicked
                Log.d(TAG, "gkOptionClick: correct");
                showPopupWindow(true);
            }else{
                //wrong ans clicked
                Log.d(TAG, "gkOptionClick: wrong");

                showPopupWindow(false);
            }
        }

        //Thread.sleep(2000);

        //startEngine();
    }

    private void showPopupWindow(boolean correct) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (popupWindow != null && popupWindow.isShowing()) {
                    Log.d(TAG, "run: Dismiss");
                    popupWindow.dismiss();
                    countDownTimer.onFinish();

                }
            }
        };

        if(correct){
            lottieCorrect.setVisibility(View.VISIBLE);
            lottieWrong.setVisibility(View.GONE);
        }else{
            lottieCorrect.setVisibility(View.GONE);
            lottieWrong.setVisibility(View.VISIBLE);
        }

        popupWindow.showAtLocation(this, Gravity.BOTTOM, 0 , 0 );

        handler.postDelayed(runnable, 1000);
    }

    private void startEngine() {
        Log.d(TAG, "startEngine: ");
//        pauseEngine();
//        handler.postDelayed(runnable, GK_INTERVAL);
//        pause = false;
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void pauseEngine() {
        Log.d(TAG, "pauseEngine: ");
//        handler.removeCallbacks(runnable);
//        pause = true;

        countDownTimer.cancel();
    }

    protected void showContent(){

        if(pause || MyApp.getConfig().show_gk_view==0){
            this.setVisibility(GONE);
            invalidate();
            return;
        }


        Log.d(TAG, "showContent: called "+new Gson().toJson(MyApp.getConfig()));
        if(MyApp.getConfig().gk_view_ad_status==1 ){
            if(MyApp.getConfig().gk_view_ad_type==1){

                //admob ad

                if(gkShowed==MyApp.getConfig().gk_ad_interval-1){
                    Log.d(TAG, "showContent: banner ad load called");
                    loadBannerAd();
                }

                if(gkShowed==MyApp.getConfig().gk_ad_interval){
                    Log.d(TAG, "showContent: show ad called");
                    //showAd
                    showAd();
                    return;
                }
            } else if (MyApp.getConfig().gk_view_ad_type==2) {
                //show custom ad
                Log.d(TAG, "showContent: show custom ad");
            }
        }

        if(MyApp.getConfig().show_gk==1){

            if(!showGk()){
                showAd();
            }

        }else{
            Log.d(TAG, "showContent: gk  hidden");
            showAd();
        }

    }

    private void delayAndShowContent(long i) {
        new Handler().postDelayed(this::showContent, i);
    }

    private boolean showAd() {

        if(MyApp.getConfig().show_gk_view==0 ){
            //gk view is hidden from server
            return false;
        }

        if(MyApp.getConfig().gk_view_ad_status==0 || MyApp.getConfig().gk_view_ad_type!=1){
            Log.d(TAG, "showAd: admob ad status is off");

            if(contentReloadCount <= contentReloadThreshold){
                delayAndShowContent(3000);
                contentReloadCount +=1;
                return false;
            }

            this.setVisibility(GONE);
            invalidate();
            return false;
        }





        if(!isAdLoaded){
            Log.d(TAG, "showContent: ad not laoded");
            loadBannerAd();
            delayAndShowContent(2000);
        }

        gkShowed = 0;
        mAdView.setVisibility(VISIBLE);
        mGkHolder.setVisibility(GONE);
        currContent = CURR_CONTENT.AD;



        startEngine();
        return true;
    }

    protected boolean showGk(){

        GkEngine.CurrentGk gk  = gkEngine.getGk();
        Log.d(TAG, "showContent: called");

        if(gk==null){
            Log.d(TAG, "showContent: gk is null");
            return false;
        }
        Log.d(TAG, "showContent: gk is showing");

        gkShowed++;
        mAdView.setVisibility(GONE);
        mGkHolder.setVisibility(VISIBLE);

        currentGk = gk;

        if(gk.gk.has_option==1){
            optionRadioGroup.clearCheck();
            currContent = CURR_CONTENT.MULTIPLE_OPTION_GK;
            mNonOptionalGkHolder.setVisibility(GONE);
            mOptionalGkHolder.setVisibility(VISIBLE);

            optionGkText.setText(gk.gk.question);
            option1.setText(gk.gk.option1);
            option2.setText(gk.gk.option2);

            option1.setEnabled(true);
            option2.setEnabled(true);

        }else {
            currContent = CURR_CONTENT.MULTIPLE_OPTION_GK;
            mNonOptionalGkHolder.setVisibility(VISIBLE);
            mOptionalGkHolder.setVisibility(GONE);
            nonOptionGkText.setText(gk.gk.question);

        }
        startEngine();
        //GkEngine.setGkAsShown(currentGk);
        return true;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: ");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Touch down event
                // Handle any required actions
                gkViewTouchListener.onTouchStart();
                return true;
            case MotionEvent.ACTION_UP:
                // Touch release event
                // Handle any required actions
                gkViewTouchListener.onTouchRelease();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isWindowVisible = visibility;
        if (visibility == View.VISIBLE) {
            // The window is now visible
            // Resume any ongoing operations or refresh data
            //startEngine();
            Log.d(TAG, "onWindowVisibilityChanged: visible");
            pause = false;
        } else {
            Log.d(TAG, "onWindowVisibilityChanged: not visible");

            // The window is no longer visible
            // Pause or cancel ongoing operations if needed
            pause = true;
        }
        Log.d(TAG, "onWindowVisibilityChanged: "+new Gson().toJson(MyApp.getConfig()));
        if(MyApp.getConfig().show_gk_view==1){

            if(contentReloadCount<=contentReloadThreshold){
                this.setVisibility(VISIBLE);
                requestLayout();
            }else{
                this.setVisibility(GONE);
                requestLayout();
            }

        }else{
            this.setVisibility(GONE);
            requestLayout();

        }
    }

    public void startGkView(boolean restarting){
        GkEngine.getGkFromLocal();
        pause = false;
        showContent();
    }

    public void stopGkView(){
        pause = true;
    }

}
