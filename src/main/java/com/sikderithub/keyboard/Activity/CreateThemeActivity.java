package com.sikderithub.keyboard.Activity;

import static com.sikderithub.keyboard.Utils.CustomThemeHelper.drawableToBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyboard.Keyboard;
import com.android.inputmethod.keyboard.KeyboardId;
import com.android.inputmethod.keyboard.KeyboardLayoutSet;
import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.latin.RichInputMethodSubtype;
import com.android.inputmethod.latin.settings.Settings;
import com.android.inputmethod.latin.settings.SettingsValues;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.sikderithub.keyboard.CommonMethod;
import com.sikderithub.keyboard.Models.Theme;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.R;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CreateThemeActivity extends AppCompatActivity {

    private static final String TAG = "CreateThemeActivity";

    private static final int REQUEST_SELECT_IMAGE = 10001;
    private MainKeyboardView mainKeyboardView;
    private SettingsValues currentSettingsValues;
    private Keyboard keybaord;
    private CardView cvChangeImage;
    private SeekBar sbOpacity;
    private SwitchCompat sbShowKeyBorder;
    private AppCompatSpinner spOverlay;
    private LinearLayout llAction;
    private Button tvSave;
    private Button tvCancel;
    private String selectedImageUri = null;
    private int dominateColor;
    private boolean showKeyBorder = true;
    private int keyOpacity = 155;
    private String imageOverLay = null;
    private Drawable drawable;
    private int textColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Theme");
        setContentView(R.layout.activity_create_theme);



        mainKeyboardView = findViewById(R.id.keyboardView);
        cvChangeImage = findViewById(R.id.cvChangeImgae);
        sbOpacity = findViewById(R.id.sbBackgroundOpacity);
        sbShowKeyBorder = findViewById(R.id.sbShowKeyBorder);
        spOverlay = findViewById(R.id.spOverlay);
        llAction = findViewById(R.id.llAction);
        tvSave = findViewById(R.id.tvSave);
        tvCancel = findViewById(R.id.tvCancel);

        currentSettingsValues = Settings.getInstance().getCurrent();

        setKeyboard();



        sbOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: "+progress);
                keyOpacityChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PorterDuff.Mode[] porterDuffMode = PorterDuff.Mode.values();

        ArrayList<String> overlayTypes = new ArrayList<>();
        overlayTypes.add("None");
        for (PorterDuff.Mode mode : porterDuffMode) {
            overlayTypes.add(mode.name());
        }



        ArrayAdapter<String> overlayAdapter  = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, overlayTypes);
        spOverlay.setAdapter(overlayAdapter);
        spOverlay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    imageOverLaySelected(null);
                    return;
                }
                String overlaySelected = overlayAdapter.getItem(position);
                imageOverLaySelected(overlaySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sbShowKeyBorder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showKeyBorder = isChecked;
            changeKeyBorderState(isChecked);
        });



        cvChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });


        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTheme();
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void saveTheme() {
        if(drawable==null){
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri contentUri = Uri.parse(selectedImageUri);
        String outputFileName = "theme_bg"+ System.currentTimeMillis()+".jpg";
        boolean isSuccessful = copyFileToPrivateFolder(getApplicationContext(), contentUri, outputFileName);

        Bitmap bitmapBg = drawableToBitmap(drawable);


        Palette.from(bitmapBg)/*.setRegion(0, 0, bitmapBg.getWidth(), 40)*/.generate(palette -> {
            assert palette != null;
            //dominateColor = palette.getVibrantColor(MyApp.getApContext().getResources().getColor(R.color.cardview_dark_background));
            dominateColor = palette.getDominantColor(MyApp.getApContext().getResources().getColor(R.color.highlight_color_lxx_light));
            textColor = palette.getDominantSwatch()!=null ? palette.getDominantSwatch().getBodyTextColor() : MyApp.getApContext().getResources().getColor(R.color.key_text_color_lxx_dark) ;
            Log.d(TAG, "saveTheme: "+textColor);

            textColor = CommonMethod.INSTANCE.getContrastColor(dominateColor);

            llAction.setBackgroundColor(textColor);

            if (isSuccessful) {
                // File copied successfully
                Theme theme = new Theme(outputFileName, showKeyBorder, keyOpacity, dominateColor, textColor, imageOverLay);
                QuestionDatabase.databaseWriteExecutor.execute(() -> {

                    int insertedId = (int) QuestionDatabase.getDatabase(this)
                            .questionDAO()
                            .saveCustomTheme(theme);


                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                    KeyboardTheme.saveKeyboardThemeId(KeyboardTheme.THEME_ID_CUSTOM, pref);
                    KeyboardTheme.saveCustomSelectedThemeId(pref, insertedId);
                    CustomThemeHelper.loadSelectedCustomTheme();

                });


                Toast.makeText(this, "Theme created successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Error occurred during file copying
                Toast.makeText(this, "Image copying failed!", Toast.LENGTH_SHORT).show();
            }

        });







    }

    private void imageOverLaySelected(String overlaySelected) {
        imageOverLay = overlaySelected;
        if (drawable!=null){

            if(imageOverLay!=null){
                drawable.setColorFilter(dominateColor, PorterDuff.Mode.valueOf(imageOverLay));
                mainKeyboardView.changeBackground(drawable);
            }else{
                drawable.clearColorFilter();
                mainKeyboardView.changeBackground(drawable);
            }

        }else{
            Toast.makeText(this, "Keyboard Background Image Not Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void keyOpacityChange(int progress) {
        keyOpacity = progress;
        mainKeyboardView.changeKeyBackgroundOpacity(progress);
    }

    private void changeKeyBorderState(boolean isChecked) {
        sbOpacity.setEnabled(isChecked);
        mainKeyboardView.changeShowKeyBorder(isChecked, keyOpacity);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                // Handle the selected image URI
                // You can display the image or perform any further operations
                cropImage(selectedImageUri);
            }
        }else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                // Handle the selected image URI
                // You can display the image or perform any further operations
                imageSelected(UCrop.getOutput(data));
            }
        }
    }

    private void cropImage(Uri selectedImageUri) {
        String name = "skt_"+System.currentTimeMillis()+".jpeg";
        File cacheFile = new File(getCacheDir(), name);

        Uri cacheUri = Uri.fromFile(cacheFile);

        UCrop.of(selectedImageUri, cacheUri)
                .withAspectRatio(16, 9)
                .withMaxResultSize(1600 ,900 )
                .start(this);


    }

    private void imageSelected(Uri selectedImageUri) {



        this.selectedImageUri = selectedImageUri.toString();

        try {
             drawable = Drawable.createFromStream(
                    getApplicationContext().getContentResolver().openInputStream(selectedImageUri), null);

            Bitmap bitmapBg = drawableToBitmap(drawable);


            Palette.from(bitmapBg)/*.setRegion(0, 0, bitmapBg.getWidth(), 40)*/.generate(palette -> {
                assert palette != null;
                //dominateColor = palette.getVibrantColor(MyApp.getApContext().getResources().getColor(R.color.cardview_dark_background));
                dominateColor = palette.getDominantColor(MyApp.getApContext().getResources().getColor(android.R.color.white));
                textColor = palette.getDominantSwatch()!=null ? palette.getDominantSwatch().getBodyTextColor() : MyApp.getApContext().getResources().getColor(R.color.key_text_color_lxx_dark) ;
                Log.d(TAG, "saveTheme: "+textColor);
                llAction.setBackgroundColor(textColor);
            });


            mainKeyboardView.changeBackground(drawable);

        } catch (FileNotFoundException e) {

        }
    }


    private void setKeyboard() {
        ContextThemeWrapper mThemeContext = new ContextThemeWrapper(getApplicationContext(), KeyboardTheme.getKeyboardTheme(this).mStyleId);

        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(
                mThemeContext, null);


        final Resources res = mThemeContext.getResources();

        float itemMarginDP = res.getDimension(R.dimen.theme_keyboard_margin);
        float pixelValue = itemMarginDP * (res.getDisplayMetrics().densityDpi / 160f);

        int pixelItemMarginValue = (int) pixelValue;





        final int keyboardWidth = ResourceUtils.getDefaultKeyboardWidth(res);
        final int keyboardHeight = ResourceUtils.getKeyboardHeight(res, currentSettingsValues);
        builder.setKeyboardGeometry(keyboardWidth, keyboardHeight-pixelItemMarginValue);
        builder.setSubtype(RichInputMethodSubtype.getNoLanguageSubtype());


        KeyboardLayoutSet mKeyboardLayoutSet = builder.build();

        keybaord = mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET);


        Drawable bgDrawable = getDrawable(R.drawable.keyboard_background_lxx_dark);

        mainKeyboardView.setBackground(bgDrawable);
        mainKeyboardView.setKeyboard(keybaord);
    }



    public static boolean copyFileToPrivateFolder(Context context, Uri contentUri, String outputFileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(contentUri);

            outputStream = new FileOutputStream(new File(context.getFilesDir(), outputFileName));

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}