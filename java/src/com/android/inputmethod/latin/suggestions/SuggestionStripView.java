/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.latin.suggestions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.inputmethod.accessibility.AccessibilityUtils;
import com.android.inputmethod.keyboard.Keyboard;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.keyboard.MoreKeysPanel;
import com.android.inputmethod.latin.AudioAndHapticFeedbackManager;
import com.android.inputmethod.latin.RichInputMethodManager;
import com.android.inputmethod.latin.permissions.PermissionsManager;
import com.android.inputmethod.utils.LanguageSwitcher;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.sikderithub.keyboard.BuildConfig;
import com.sikderithub.keyboard.CommonMethod;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.android.inputmethod.latin.SuggestedWords;
import com.android.inputmethod.latin.SuggestedWords.SuggestedWordInfo;
import com.android.inputmethod.latin.common.Constants;
import com.android.inputmethod.latin.define.DebugFlags;
import com.android.inputmethod.latin.settings.Settings;
import com.android.inputmethod.latin.settings.SettingsValues;
import com.android.inputmethod.latin.suggestions.MoreSuggestionsView.MoreSuggestionsListener;
import com.android.inputmethod.latin.utils.ImportantNoticeUtils;
import com.sikderithub.keyboard.Activity.ThemeActivity;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;

import java.util.ArrayList;

public final class SuggestionStripView extends RelativeLayout implements OnClickListener,
        OnLongClickListener, PermissionsManager.PermissionsResultCallback {
    private static final String TAG = "SuggestionStripView";
    private final ImageView update_icon;
    private final ImageView message_icon;
    private final RichInputMethodManager mRichIME;
    private final RelativeLayout tutorial_layout;
    private final Animation xlargeAnim;
    private final View mVoiceStrip;
    private TextView txtVoiceRecordingStatus;
    private final SpeechRecognizer speechRecognizer;
    private boolean isVoiceRecording = false;
    private int RECORD_AUDIO_PERMISSION_CODE = 100001;


    public interface Listener {
        public void showImportantNoticeContents();
        public void pickSuggestionManually(SuggestedWordInfo word);
        public void onCodeInput(int primaryCode, int x, int y, boolean isKeyRepeat);


    }

    static final boolean DBG = DebugFlags.DEBUG_ENABLED;
    private static final float DEBUG_INFO_TEXT_SIZE_IN_DIP = 6.0f;

    private final ViewGroup mSuggestionsStrip;
    private final ImageButton mVoiceKey;
    private final ImageView mSettingsKey;
    private final ImageView mBackIcon;
    private final View mImportantNoticeStrip;
    MainKeyboardView mMainKeyboardView;

    private final View mMoreSuggestionsContainer;
    private final MoreSuggestionsView mMoreSuggestionsView;
    private final MoreSuggestions.Builder mMoreSuggestionsBuilder;

    private final ArrayList<TextView> mWordViews = new ArrayList<>();
    private final ArrayList<TextView> mDebugInfoViews = new ArrayList<>();
    private final ArrayList<View> mDividerViews = new ArrayList<>();

    Listener mListener;
    private SuggestedWords mSuggestedWords = SuggestedWords.getEmptyInstance();
    private int mStartIndexOfMoreSuggestions;

    private final SuggestionStripLayoutHelper mLayoutHelper;
    private final StripVisibilityGroup mStripVisibilityGroup;
    private LabeledSwitch langSwitch;



    private static class StripVisibilityGroup {
        private final View mSuggestionStripView;
        private final View mSuggestionsStrip;
        private final View mImportantNoticeStrip;
        private final View mVoceRecordingStrip;

        public StripVisibilityGroup(final View suggestionStripView,
                final ViewGroup suggestionsStrip, final View importantNoticeStrip, final View voceRecordingStrip) {
            mSuggestionStripView = suggestionStripView;
            mSuggestionsStrip = suggestionsStrip;
            mImportantNoticeStrip = importantNoticeStrip;
            mVoceRecordingStrip = voceRecordingStrip;
            showSuggestionsStrip();
        }

        public void setLayoutDirection(final boolean isRtlLanguage) {
            final int layoutDirection = isRtlLanguage ? ViewCompat.LAYOUT_DIRECTION_RTL
                    : ViewCompat.LAYOUT_DIRECTION_LTR;
            ViewCompat.setLayoutDirection(mSuggestionStripView, layoutDirection);
            ViewCompat.setLayoutDirection(mSuggestionsStrip, layoutDirection);
            ViewCompat.setLayoutDirection(mImportantNoticeStrip, layoutDirection);
            ViewCompat.setLayoutDirection(mVoceRecordingStrip, layoutDirection);
        }

        public void showSuggestionsStrip() {
            mSuggestionsStrip.setVisibility(VISIBLE);
            mImportantNoticeStrip.setVisibility(INVISIBLE);
            mVoceRecordingStrip.setVisibility(INVISIBLE);
        }

        public void showImportantNoticeStrip() {
            mSuggestionsStrip.setVisibility(INVISIBLE);
            mImportantNoticeStrip.setVisibility(VISIBLE);
            mVoceRecordingStrip.setVisibility(INVISIBLE);
        }

         public void showVoiceRecordingStrip() {
            mSuggestionsStrip.setVisibility(INVISIBLE);
            mImportantNoticeStrip.setVisibility(INVISIBLE);
            mVoceRecordingStrip.setVisibility(VISIBLE);
        }

        public boolean isShowingImportantNoticeStrip() {
            return mImportantNoticeStrip.getVisibility() == VISIBLE;
        }
    }



    /**
     * Construct a {@link SuggestionStripView} for showing suggestions to be picked by the user.
     * @param context
     * @param attrs
     */


    public SuggestionStripView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.suggestionStripViewStyle);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public SuggestionStripView(final Context context, final AttributeSet attrs,
                               final int defStyle) {
        super(context, attrs, defStyle);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.suggestions_strip, this);

        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
           setBackgroundColor(CustomThemeHelper.selectedCustomTheme.dominateColor);
        }
        speechRecognizer =  SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(recognitionListener);

        mSuggestionsStrip = (ViewGroup)findViewById(R.id.suggestions_strip);
        mRichIME = RichInputMethodManager.getInstance();


        mVoiceKey = (ImageButton)findViewById(R.id.suggestions_strip_voice_key);
        mSettingsKey = (ImageView) findViewById(R.id.suggestions_strip_settings_key);
        mBackIcon = (ImageView) findViewById(R.id.action_back);

        mImportantNoticeStrip = findViewById(R.id.important_notice_strip);
        txtVoiceRecordingStatus = findViewById(R.id.txtVoiceRecordingStatus);
        mVoiceStrip = findViewById(R.id.voice_strip);


        mStripVisibilityGroup = new StripVisibilityGroup(this, mSuggestionsStrip,
                mImportantNoticeStrip, mVoiceStrip);

        ImageView savedGkIcon = (ImageView) findViewById(R.id.saved_gk_icon);
        ImageView emoji_icon = (ImageView) findViewById(R.id.emoji_icon);
        message_icon = (ImageView) findViewById(R.id.message_icon);
        ImageView theme_icon = (ImageView) findViewById(R.id.theme_icon);
        update_icon = (ImageView) findViewById(R.id.update_icon);
        tutorial_layout = (RelativeLayout) findViewById(R.id.tutorial_layout);
        langSwitch = findViewById(R.id.langSwitch);

        xlargeAnim = AnimationUtils.loadAnimation(context, R.anim.view_xlarge);
        

        TypedArray ss = context.obtainStyledAttributes(attrs, R.styleable.SuggestionStripView);

        int textColor = ss.getColor(R.styleable.SuggestionStripView_colorTypedWord, 0);


        Drawable savedIconRes = ss.getDrawable(R.styleable.SuggestionStripView_savedGkIcon);


        final Drawable iconBack = ss.getDrawable(R.styleable.SuggestionStripView_backIcon);
        Drawable emojiIconRes = ss.getDrawable(R.styleable.SuggestionStripView_emojiIcon);
        Drawable themeIconRes = ss.getDrawable(R.styleable.SuggestionStripView_themeIcon);


        int emojiColor = 0;

        if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
            emojiColor = CustomThemeHelper.selectedCustomTheme.bodyTextColor;
        }else{
            emojiColor = textColor;
        }

        txtVoiceRecordingStatus.setTextColor(textColor);

//        langSwitch.setColorOn(textColor);
//        langSwitch.setColorBorder(textColor);
//        langSwitch.setColorOff(CommonMethod.INSTANCE.getOppositeColor(textColor));


        DrawableCompat.setTint(
                DrawableCompat.wrap(iconBack),
                emojiColor
        );DrawableCompat.setTint(
                DrawableCompat.wrap(emojiIconRes),
                emojiColor
        );DrawableCompat.setTint(
                DrawableCompat.wrap(iconBack),
                emojiColor
        );DrawableCompat.setTint(
                DrawableCompat.wrap(savedIconRes),
                emojiColor
        );;DrawableCompat.setTint(
                DrawableCompat.wrap(themeIconRes),
                emojiColor
        );


        savedGkIcon.setImageDrawable(savedIconRes);
        mBackIcon.setImageDrawable(iconBack);

        emoji_icon.setImageDrawable(emojiIconRes);
        Drawable messgeIconRes = ss.getDrawable(R.styleable.SuggestionStripView_messageIcon);

        message_icon.setImageDrawable(messgeIconRes);

        Drawable updateIconRes = ss.getDrawable(R.styleable.SuggestionStripView_updateIcon);
        update_icon.setImageDrawable(updateIconRes);

        theme_icon.setImageDrawable(themeIconRes);

       

        emoji_icon.setOnClickListener(this);
        savedGkIcon.setOnClickListener(this);
        mBackIcon.setOnClickListener(this);
        theme_icon.setOnClickListener(this);
        update_icon.setOnClickListener(this);


        langSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                Log.d(TAG, "onSwitched: "+isOn);
                if(isOn){
                    mListener.onCodeInput(Constants.CODE_ACTION_SWITCH_TO_AVRO,Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                            false);
                }else{
                    mListener.onCodeInput(Constants.CODE_ACTION_SWITCH_TO_ENGLISH, Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                            false);
                }
            }
        });



        





        for (int pos = 0; pos < SuggestedWords.MAX_SUGGESTIONS; pos++) {
            final TextView word = new TextView(context, null, R.attr.suggestionWordStyle);


            if(CustomThemeHelper.isCustomThemeApplicable(getContext()) && CustomThemeHelper.selectedCustomTheme!=null){
                //word.setTextColor(getContext().getColor(R.color.colorAccent));
            }

            word.setContentDescription(getResources().getString(R.string.spoken_empty_suggestion));
            word.setOnClickListener(this);
            word.setOnLongClickListener(this);
            mWordViews.add(word);
            final View divider = inflater.inflate(R.layout.suggestion_divider, null);
            mDividerViews.add(divider);
            final TextView info = new TextView(context, null, R.attr.suggestionWordStyle);
            info.setTextColor(Color.WHITE);
            info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEBUG_INFO_TEXT_SIZE_IN_DIP);
            mDebugInfoViews.add(info);
        }

        mLayoutHelper = new SuggestionStripLayoutHelper(
                context, attrs, defStyle, mWordViews, mDividerViews, mDebugInfoViews);

        mMoreSuggestionsContainer = inflater.inflate(R.layout.more_suggestions, null);
        mMoreSuggestionsView = (MoreSuggestionsView)mMoreSuggestionsContainer
                .findViewById(R.id.more_suggestions_view);
        mMoreSuggestionsBuilder = new MoreSuggestions.Builder(context, mMoreSuggestionsView);

        final Resources res = context.getResources();
        mMoreSuggestionsModalTolerance = res.getDimensionPixelOffset(
                R.dimen.config_more_suggestions_modal_tolerance);
        mMoreSuggestionsSlidingDetector = new GestureDetector(
                context, mMoreSuggestionsSlidingListener);

        final TypedArray keyboardAttr = context.obtainStyledAttributes(attrs,
                R.styleable.Keyboard, defStyle, R.style.SuggestionStripView);
        final Drawable iconVoice = keyboardAttr.getDrawable(R.styleable.Keyboard_iconShortcutKey);
        mVoiceKey.setImageDrawable(iconVoice);

        keyboardAttr.recycle();
        mVoiceKey.setOnClickListener(this);
        mSettingsKey.setOnClickListener(this::onClick);


    }

    /**
     * A connection back to the input method.
     * @param listener
     */
    public void setListener(final Listener listener, final View inputView) {
        mListener = listener;
        mMainKeyboardView = (MainKeyboardView)inputView.findViewById(R.id.keyboard_view);
    }

    public void updateVisibility(final boolean shouldBeVisible, final boolean isFullscreenMode) {
        final int visibility = shouldBeVisible ? VISIBLE : (isFullscreenMode ? GONE : INVISIBLE);
        setVisibility(visibility);
        final SettingsValues currentSettingsValues = Settings.getInstance().getCurrent();
        mVoiceKey.setVisibility(currentSettingsValues.mShowsVoiceInputKey ? VISIBLE : INVISIBLE);
        //mVoiceKey.setVisibility(GONE);


    }

    public void setSuggestions(final SuggestedWords suggestedWords, final boolean isRtlLanguage) {
        clear();
        mStripVisibilityGroup.setLayoutDirection(isRtlLanguage);
        mSuggestedWords = suggestedWords;
        mStartIndexOfMoreSuggestions = mLayoutHelper.layoutAndReturnStartIndexOfMoreSuggestions(
                getContext(), mSuggestedWords, mSuggestionsStrip, this);
        mStripVisibilityGroup.showSuggestionsStrip();
    }

    public void setMoreSuggestionsHeight(final int remainingHeight) {
        mLayoutHelper.setMoreSuggestionsHeight(remainingHeight);
    }

    // This method checks if we should show the important notice (checks on permanent storage if
    // it has been shown once already or not, and if in the setup wizard). If applicable, it shows
    // the notice. In all cases, it returns true if it was shown, false otherwise.
    public boolean maybeShowImportantNoticeTitle() {
        final SettingsValues currentSettingsValues = Settings.getInstance().getCurrent();
        if (!ImportantNoticeUtils.shouldShowImportantNotice(getContext(), currentSettingsValues)) {
            return false;
        }
        if (getWidth() <= 0) {
            return false;
        }
        final String importantNoticeTitle = ImportantNoticeUtils.getSuggestContactsNoticeTitle(
                getContext());
        if (TextUtils.isEmpty(importantNoticeTitle)) {
            return false;
        }
        if (isShowingMoreSuggestionPanel()) {
            dismissMoreSuggestionsPanel();
        }
        //mLayoutHelper.layoutImportantNotice(mImportantNoticeStrip, importantNoticeTitle);


        mStripVisibilityGroup.showImportantNoticeStrip();
        //mImportantNoticeStrip.setOnClickListener(this);



        
        return true;
    }

    public void clear() {
        mSuggestionsStrip.removeAllViews();
        removeAllDebugInfoViews();
        mStripVisibilityGroup.showSuggestionsStrip();
        dismissMoreSuggestionsPanel();
    }

    private void removeAllDebugInfoViews() {
        // The debug info views may be placed as children views of this {@link SuggestionStripView}.
        for (final View debugInfoView : mDebugInfoViews) {
            final ViewParent parent = debugInfoView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(debugInfoView);
            }
        }
    }

    private final MoreSuggestionsListener mMoreSuggestionsListener = new MoreSuggestionsListener() {
        @Override
        public void onSuggestionSelected(final SuggestedWordInfo wordInfo) {
            mListener.pickSuggestionManually(wordInfo);
            dismissMoreSuggestionsPanel();
        }

        @Override
        public void onCancelInput() {
            dismissMoreSuggestionsPanel();
        }
    };

    private final MoreKeysPanel.Controller mMoreSuggestionsController =
            new MoreKeysPanel.Controller() {
        @Override
        public void onDismissMoreKeysPanel() {
            mMainKeyboardView.onDismissMoreKeysPanel();
        }

        @Override
        public void onShowMoreKeysPanel(final MoreKeysPanel panel) {
            mMainKeyboardView.onShowMoreKeysPanel(panel);
        }

        @Override
        public void onCancelMoreKeysPanel() {
            dismissMoreSuggestionsPanel();
        }
    };

    public boolean isShowingMoreSuggestionPanel() {
        return mMoreSuggestionsView.isShowingInParent();
    }

    public void dismissMoreSuggestionsPanel() {
        mMoreSuggestionsView.dismissMoreKeysPanel();
    }

    @Override
    public boolean onLongClick(final View view) {
        AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(
                Constants.NOT_A_CODE, this);
        return showMoreSuggestions();
    }

    boolean showMoreSuggestions() {
        final Keyboard parentKeyboard = mMainKeyboardView.getKeyboard();
        if (parentKeyboard == null) {
            return false;
        }
        final SuggestionStripLayoutHelper layoutHelper = mLayoutHelper;
        if (mSuggestedWords.size() <= mStartIndexOfMoreSuggestions) {
            return false;
        }
        final int stripWidth = getWidth();
        final View container = mMoreSuggestionsContainer;
        final int maxWidth = stripWidth - container.getPaddingLeft() - container.getPaddingRight();
        final MoreSuggestions.Builder builder = mMoreSuggestionsBuilder;
        builder.layout(mSuggestedWords, mStartIndexOfMoreSuggestions, maxWidth,
                (int)(maxWidth * layoutHelper.mMinMoreSuggestionsWidth),
                layoutHelper.getMaxMoreSuggestionsRow(), parentKeyboard);
        mMoreSuggestionsView.setKeyboard(builder.build());
        container.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final MoreKeysPanel moreKeysPanel = mMoreSuggestionsView;
        final int pointX = stripWidth / 2;
        final int pointY = -layoutHelper.mMoreSuggestionsBottomGap;
        moreKeysPanel.showMoreKeysPanel(this, mMoreSuggestionsController, pointX, pointY,
                mMoreSuggestionsListener);
        mOriginX = mLastX;
        mOriginY = mLastY;
        for (int i = 0; i < mStartIndexOfMoreSuggestions; i++) {
            mWordViews.get(i).setPressed(false);
        }
        return true;
    }

    // Working variables for {@link onInterceptTouchEvent(MotionEvent)} and
    // {@link onTouchEvent(MotionEvent)}.
    private int mLastX;
    private int mLastY;
    private int mOriginX;
    private int mOriginY;
    private final int mMoreSuggestionsModalTolerance;
    private boolean mNeedsToTransformTouchEventToHoverEvent;
    private boolean mIsDispatchingHoverEventToMoreSuggestions;
    private final GestureDetector mMoreSuggestionsSlidingDetector;
    private final GestureDetector.OnGestureListener mMoreSuggestionsSlidingListener =
            new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent down, MotionEvent me, float deltaX, float deltaY) {
            final float dy = me.getY() - down.getY();
            if (deltaY > 0 && dy < 0) {
                return showMoreSuggestions();
            }
            return false;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent me) {
        if (mStripVisibilityGroup.isShowingImportantNoticeStrip()) {
            return false;
        }
        // Detecting sliding up finger to show {@link MoreSuggestionsView}.
        if (!mMoreSuggestionsView.isShowingInParent()) {
            mLastX = (int)me.getX();
            mLastY = (int)me.getY();
            return mMoreSuggestionsSlidingDetector.onTouchEvent(me);
        }
        if (mMoreSuggestionsView.isInModalMode()) {
            return false;
        }

        final int action = me.getAction();
        final int index = me.getActionIndex();
        final int x = (int)me.getX(index);
        final int y = (int)me.getY(index);
        if (Math.abs(x - mOriginX) >= mMoreSuggestionsModalTolerance
                || mOriginY - y >= mMoreSuggestionsModalTolerance) {
            // Decided to be in the sliding suggestion mode only when the touch point has been moved
            // upward. Further {@link MotionEvent}s will be delivered to
            // {@link #onTouchEvent(MotionEvent)}.
            mNeedsToTransformTouchEventToHoverEvent =
                    AccessibilityUtils.getInstance().isTouchExplorationEnabled();
            mIsDispatchingHoverEventToMoreSuggestions = false;
            return true;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            // Decided to be in the modal input mode.
            mMoreSuggestionsView.setModalMode();
        }
        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        // Don't populate accessibility event with suggested words and voice key.
        return true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent me) {
        if (!mMoreSuggestionsView.isShowingInParent()) {
            // Ignore any touch event while more suggestions panel hasn't been shown.
            // Detecting sliding up is done at {@link #onInterceptTouchEvent}.
            return true;
        }
        // In the sliding input mode. {@link MotionEvent} should be forwarded to
        // {@link MoreSuggestionsView}.
        final int index = me.getActionIndex();
        final int x = mMoreSuggestionsView.translateX((int)me.getX(index));
        final int y = mMoreSuggestionsView.translateY((int)me.getY(index));
        me.setLocation(x, y);
        if (!mNeedsToTransformTouchEventToHoverEvent) {
            mMoreSuggestionsView.onTouchEvent(me);
            return true;
        }
        // In sliding suggestion mode with accessibility mode on, a touch event should be
        // transformed to a hover event.
        final int width = mMoreSuggestionsView.getWidth();
        final int height = mMoreSuggestionsView.getHeight();
        final boolean onMoreSuggestions = (x >= 0 && x < width && y >= 0 && y < height);
        if (!onMoreSuggestions && !mIsDispatchingHoverEventToMoreSuggestions) {
            // Just drop this touch event because dispatching hover event isn't started yet and
            // the touch event isn't on {@link MoreSuggestionsView}.
            return true;
        }
        final int hoverAction;
        if (onMoreSuggestions && !mIsDispatchingHoverEventToMoreSuggestions) {
            // Transform this touch event to a hover enter event and start dispatching a hover
            // event to {@link MoreSuggestionsView}.
            mIsDispatchingHoverEventToMoreSuggestions = true;
            hoverAction = MotionEvent.ACTION_HOVER_ENTER;
        } else if (me.getActionMasked() == MotionEvent.ACTION_UP) {
            // Transform this touch event to a hover exit event and stop dispatching a hover event
            // after this.
            mIsDispatchingHoverEventToMoreSuggestions = false;
            mNeedsToTransformTouchEventToHoverEvent = false;
            hoverAction = MotionEvent.ACTION_HOVER_EXIT;
        } else {
            // Transform this touch event to a hover move event.
            hoverAction = MotionEvent.ACTION_HOVER_MOVE;
        }
        me.setAction(hoverAction);
        mMoreSuggestionsView.onHoverEvent(me);
        return true;
    }

    @Override
    public void onClick(final View view) {
        AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(
                Constants.CODE_UNSPECIFIED, this);
        if (view == mImportantNoticeStrip) {
            mListener.showImportantNoticeContents();
            return;
        }
        if (view == mVoiceKey) {
//            mListener.onCodeInput(Constants.CODE_SHORTCUT,
//                    Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
//                    false /* isKeyRepeat */);
            voiceKeyClick();
            return;
        }

        if (view == mSettingsKey) {
            mListener.onCodeInput(Constants.CODE_SETTINGS,
                    Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                    false /* isKeyRepeat */);
            return;
        }

        if(view.getId()==R.id.emoji_icon){
            mListener.onCodeInput(Constants.CODE_EMOJI,Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                    false);
            return;
        }
        if(view.getId()==R.id.saved_gk_icon){
            mListener.onCodeInput(Constants.CODE_SAVED_GK,Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                    false);
            return;
        }
        if(view.getId()==R.id.update_icon){
            CommonMethod.INSTANCE.openAppLink(getContext());
            return;
        }

        if(view.getId()==R.id.action_back){
            mListener.onCodeInput(Constants.CODE_ACTION_BACK,Constants.SUGGESTION_STRIP_COORDINATE, Constants.SUGGESTION_STRIP_COORDINATE,
                    false);
            return;
        }
        if(view.getId()==R.id.theme_icon){
            getContext().startActivity(new Intent(getContext(), ThemeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        final Object tag = view.getTag();
        // {@link Integer} tag is set at
        // {@link SuggestionStripLayoutHelper#setupWordViewsTextAndColor(SuggestedWords,int)} and
        // {@link SuggestionStripLayoutHelper#layoutPunctuationSuggestions(SuggestedWords,ViewGroup}
        if (tag instanceof Integer) {
            final int index = (Integer) tag;
            if (index >= mSuggestedWords.size()) {
                return;
            }
            final SuggestedWordInfo wordInfo = mSuggestedWords.getInfo(index);
            mListener.pickSuggestionManually(wordInfo);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissMoreSuggestionsPanel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(TAG, "onAttachedToWindow: ");

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(MyApp.getUpdateInfo().version_code > BuildConfig.VERSION_CODE && MyApp.getUpdateInfo().status==1){
            // Inside your activity or fragment
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
            update_icon.startAnimation(animation);
            update_icon.setVisibility(VISIBLE);

        }else{
            Log.d("TAG", "SuggestionStripView: version code grater");
            update_icon.setVisibility(GONE);
        }



        if(MyApp.getConfig().tutorial_link!=null && !MyApp.getConfig().tutorial_link.isEmpty()){
            tutorial_layout.setVisibility(VISIBLE);
            tutorial_layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonMethod.INSTANCE.openLink(getContext(), MyApp.getConfig().tutorial_link);
                }
            });

        }else{
            tutorial_layout.setVisibility(GONE);
        }

        if(MyApp.cachedNotification!=null){
            message_icon.setVisibility(VISIBLE);
            message_icon.startAnimation(xlargeAnim);

            message_icon.setOnClickListener(v -> {
                if(MyApp.cachedNotification!=null){
                    MyApp.cachedNotification.onNotificationAction(getContext());
                    MyApp.removeNotification(MyApp.cachedNotification.id);
                }else {
                    message_icon.setVisibility(GONE);
                    message_icon.setAnimation(null);

                }

            });
        }else{
            message_icon.setVisibility(GONE);
            message_icon.setAnimation(null);
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        // Called by the framework when the size is known. Show the important notice if applicable.
        // This may be overriden by showing suggestions later, if applicable.
        if (oldw <= 0 && w > 0) {
            maybeShowImportantNoticeTitle();
        }
    }

    public void changeLangSwitchKey(InputMethodSubtype richInputMethodSubtype){
        langSwitch.setOn(richInputMethodSubtype.getExtraValue().contains("bengali_phonetic") || richInputMethodSubtype.getExtraValue().contains("bengali_akkhor"));
    }

    private enum VoiceRecordingStatus{
        LISTENING,
        READY_TO_LISTEN,
        PERMISSION_NEEDED
    }

    private void voiceKeyClick() {
        mStripVisibilityGroup.showVoiceRecordingStrip();
        if(isRecordAudioPermissionGranted()){
            startListening();
        }else{
            setVoiceRecordingStatus(VoiceRecordingStatus.PERMISSION_NEEDED);
            requestRecordAudioPermission();
        }
    }


    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


        String lang = LanguageSwitcher.instance.getInputLanguage();

        lang = lang.replace("_", "-");


        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
        // intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, Locale("bn-BD"));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);



        // Make sure you have initialized the SpeechRecognizer before calling startListening()
        if (speechRecognizer != null && !isVoiceRecording) {
            speechRecognizer.startListening(intent);
        }


    }

    private void setVoiceRecordingStatus(VoiceRecordingStatus voiceRecordingStatus){
        String statusText = "";
        switch (voiceRecordingStatus){
            case LISTENING:
                statusText = "Listening...";
                break;
            case READY_TO_LISTEN:
                statusText = "Speak Now";
                break;
            case PERMISSION_NEEDED:
                statusText = "Record Voice Permission missing";
                break;
            default:

        }
        txtVoiceRecordingStatus.setText(statusText);
    }

    private Boolean isRecordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getContext(),
                android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                PermissionsManager.get(getContext()).requestPermissions(
                        this /* PermissionsResultCallback */,
                        null /* activity */, Manifest.permission.RECORD_AUDIO);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(boolean allGranted) {
        if(allGranted){
            startListening();
        }else{

        }
    }


    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            isVoiceRecording =true;
            setVoiceRecordingStatus(VoiceRecordingStatus.READY_TO_LISTEN);
        }

        @Override
        public void onBeginningOfSpeech() {
            setVoiceRecordingStatus(VoiceRecordingStatus.LISTENING);
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            isVoiceRecording =false;
            mStripVisibilityGroup.showImportantNoticeStrip();
        }

        @Override
        public void onError(int error) {
            isVoiceRecording =false;

            String errorMessage;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMessage = "Audio error: Microphone not working or unavailable.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMessage = "Client error: Invalid parameters or incorrect usage of SpeechRecognizer API.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMessage = "Insufficient permissions: RECORD_AUDIO permission not granted.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMessage = "Network error: No internet connection or network issues.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMessage = "Network timeout: Slow network response or server issues.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMessage = "No match: No recognition results found.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMessage = "Recognizer busy: Unable to process the request due to another ongoing process.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMessage = "Server error: Speech recognition server issues.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMessage = "Speech timeout: No speech input detected within the allowed time.";
                    break;
                default:
                    errorMessage = "Unknown error occurred.";
                    break;
            }

            txtVoiceRecordingStatus.setText(errorMessage);

            new Handler().postDelayed(() -> {
                mStripVisibilityGroup.showImportantNoticeStrip();
            }, 1000);

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (resultList != null && !resultList.isEmpty()) {
                String recognizedText = resultList.get(0);
                // Do something with the recognizedText.

                Log.d("SpeechToText", "onResults: " + recognizedText);

                mListener.pickSuggestionManually(new SuggestedWordInfo(recognizedText, "", 0, 0, null, 0, 0));
            }
        }

        @Override
        public void onPartialResults(Bundle results) {
            ArrayList<String> resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (resultList != null && !resultList.isEmpty()) {
                String recognizedText = resultList.get(0);
                // Do something with the recognizedText.

                Log.d("SpeechToText", "onResults: " + recognizedText);
            }
        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };



}
