package com.android.inputmethod.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sikderithub.keyboard.Models.Gk;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Views.SavedGkViews;

import java.util.concurrent.TimeUnit;

public class GkView extends RelativeLayout implements View.OnTouchListener{
    private static final String TAG = "GkView";

    private static final int AD_INTERVAL_COUNT = 3;
    private static final long GK_INTERVAL = 10 * 1000;
    private final Handler handler;
    private final Runnable runnable;
    private final View lottieCorrect;
    private final View lottieWrong;
    private final AdRequest adRequest;
    private int gkShowed = 0;

    private final LinearLayout mAdView;
    private final RelativeLayout mGkHolder;
    private final LinearLayout mOptionalGkHolder;
    private final LinearLayout mNonOptionalGkHolder;

    private TextView nonOptionGkText;
    private TextView optionGkText;
    private TextView option1;
    private TextView option2;
    private GkEngine gkEngine;
    private ImageView addBookMark;

    private SavedGkViews savedGkViews;



    private boolean pause = false;

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
        this(context, attrs, R.attr.suggestionStripViewStyle);
    }

    @SuppressLint("MissingInflatedId")
    public GkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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

        View popupView = inflater.inflate(R.layout.dialog_ans_result, null);

        lottieCorrect =  popupView.findViewById(R.id.lottie_correct);
        lottieWrong = popupView.findViewById(R.id.lottie_wrong);


        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, true);

        mAdView = findViewById(R.id.adView);
        bannerAd = findViewById(R.id.bannerAd);

        mGkHolder= findViewById(R.id.gkHolder);
        mNonOptionalGkHolder = findViewById(R.id.nonOptionalGkHolder);
        mOptionalGkHolder = findViewById(R.id.optionalGkHolder);

        nonOptionGkText = findViewById(R.id.nonOptionGkText);
        optionGkText = findViewById(R.id.optionGkText);
        addBookMark = findViewById(R.id.imgSaveGk);


        option1 = findViewById(R.id.txtOption1);
        option2 = findViewById(R.id.option2);

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
        if(currentGk!=null && currentGk.gk!=null){
            GkEngine.addToSavedGk(currentGk);
            savedGkViews.addNewToList(currentGk.gk);
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
            String selectedOption = "option"+i;
            if(currentGk.gk.answer.equals(selectedOption)){
                //correct ans clicked
                Log.d(TAG, "gkOptionClick: correct");
                showPopupWindow(true);
            }else{
                //wrong ans clicked
                Log.d(TAG, "gkOptionClick: wrong");

                Toast.makeText(getContext(), "Wrong ans", Toast.LENGTH_SHORT).show();
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
                    startEngine();

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
        pauseEngine();
        handler.postDelayed(runnable, GK_INTERVAL);
        pause = false;
    }

    private void pauseEngine() {
        Log.d(TAG, "pauseEngine: ");
        handler.removeCallbacks(runnable);
        pause = true;
    }

    protected void showContent(){
        Log.d(TAG, "showContent: ");
        startEngine();
        if(gkShowed==AD_INTERVAL_COUNT-1){
            loadBannerAd();
        }
        if(gkShowed==AD_INTERVAL_COUNT){
            //showAd
            showAd();
            return;
        }

        if(!showGk()){
            showAd();
        }
    }

    private void showAd() {
        gkShowed = 0;
        mAdView.setVisibility(VISIBLE);
        mGkHolder.setVisibility(GONE);
        currContent = CURR_CONTENT.AD;
    }

    protected boolean showGk(){
        GkEngine.CurrentGk gk  = gkEngine.getGk();

        if(gk==null){
            return false;
        }
        gkShowed++;
        mAdView.setVisibility(GONE);
        mGkHolder.setVisibility(VISIBLE);

        currentGk = gk;

        if(gk.gk.has_option==1){
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
}
