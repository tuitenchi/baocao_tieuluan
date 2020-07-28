package thienchi.dmt.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    private final int REQ_CODE = 100;
    private TextView mSourceLang;
    private TextView mSourceText;
    private ImageView mSwapTextBtn;
    private ImageView mMicroBtn,mClearbtn,mCopybtn,mClearbtnT,mCopybtnT;
    private TextView mTranslatedText;
    private String sourceText;
//    private TextView translateLang;
    private Spinner spinnerLang;
    private ImageView convert,convertT;
    TextToSpeech textToSpeech;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSourceLang = findViewById(R.id.sourceLang);
        mSourceText = findViewById(R.id.sourceText);
        mSwapTextBtn = findViewById(R.id.swapText);
        mMicroBtn = findViewById(R.id.micro);
        mTranslatedText = findViewById(R.id.TranslatedText);
//        translateLang = findViewById(R.id.translateLang);
        spinnerLang = findViewById(R.id.spinnerLang);
        mClearbtn = findViewById(R.id.clear);
        mCopybtn = findViewById(R.id.copy);
        mClearbtnT = findViewById(R.id.clearT);
        mCopybtnT = findViewById(R.id.copyT);
        convert = findViewById(R.id.convert);
        convertT = findViewById(R.id.convertT);

        ////Btn Speech To Text
        mMicroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });
        ////Btn Copy and Btn Clear For SourceText
        mCopybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Value = mSourceText.getText().toString();
                if (Value.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Insert Data !!! ",Toast.LENGTH_SHORT).show();

                }
                else {
                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",Value);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(),"Copied To Clipboard !!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mClearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Text = mSourceText.getText().toString();
                if (Text.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Already Empty !!!",Toast.LENGTH_SHORT).show();
                }
                else {
                    mSourceText.setText("");
                }
            }
        });
        //Btn Copy and Btn Clear For TranslateText
        mCopybtnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Value = mTranslatedText.getText().toString();
                if (Value.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Insert Data !!! ",Toast.LENGTH_SHORT).show();

                }
                else {
                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",Value);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(),"Copied To Clipboard !!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mClearbtnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Text = mTranslatedText.getText().toString();
                if (Text.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Already Empty !!!",Toast.LENGTH_SHORT).show();
                }
                else {
                    mTranslatedText.setText("");
                }
            }
        });



        ////Text To Speech Btn
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onInit(int status) {
                if (status !=TextToSpeech.ERROR){
                    try
                    {
                        if (spinnerLang.getSelectedItemId()==0){
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        }
                        else if (spinnerLang.getSelectedItemId()==1){
                            textToSpeech.setLanguage(Locale.UK);
                        }
                        else if (spinnerLang.getSelectedItemId()==2){
                            textToSpeech.setLanguage(Locale.CHINESE);
                        }
                        else if (spinnerLang.getSelectedItemId()==3){
                            textToSpeech.setLanguage(Locale.KOREA);
                        }
                        else if (spinnerLang.getSelectedItemId()==4){
                            textToSpeech.setLanguage(Locale.FRANCE);
                        }
                    }
                    catch(Exception e)
                    {
                        Log.i("Error in translation.........",e.toString());
                    }
                }

            }
        });
        //Btn Speech on Source
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = mSourceText.getText().toString();
                Toast.makeText(getApplicationContext(),"Speech: " + toSpeak,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        //Btn Speech on Translate
        convertT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = mTranslatedText.getText().toString();
                Toast.makeText(getApplicationContext(),"Speech: " + toSpeak,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
            }
        });



        //Combobox selectedItem
        final Spinner spinner = findViewById(R.id.spinnerLang);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerLang, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                Toast.makeText
                        (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                        .show();
                    if(!mSourceLang.getText().equals("Detecting...")){
                        identifyLanguage();
                    }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //Nút chuyển đổi text
        mSwapTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) mSourceLang.getText();
                String t = (String) spinnerLang.getSelectedItem();
                int selectionPosition = adapter.getPosition(s);
                spinnerLang.setSelection(selectionPosition);
                mSourceLang.setText(t);
                mSourceText.setText(mTranslatedText.getText());

          }
        });


        //Nhập text và dịch cùng lúc
        mSourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTranslatedText.setText(s);
                identifyLanguage();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
    public void onPause(){
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();

    }
    //Text vừa ra là dịch
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mSourceText.setText(result.get(0));
                    identifyLanguage();
                }
                break;
            }
        }
    }
    //Hàm nhận dạng ngôn ngữ
    private void identifyLanguage() {

        sourceText = mSourceText.getText().toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        mSourceLang.setText("Detecting...");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language Not Identified",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    getLanguegeCode(s);
                }
            }
        });
    }
    //Hàm nhận ngôn ngữ
    private void getLanguegeCode(String language) {
        int langCode;
        switch (language){
            case "vi":
                langCode = VI;
                mSourceLang.setText("Vietnam");
                break;
            case "ko":
                langCode = KO;
                mSourceLang.setText("Korea");
                break;
            case "fr":
                langCode = FR;
                mSourceLang.setText("France");
                break;
            case "en":
                langCode = EN;
                mSourceLang.setText("English");
                break;
            case "zh":
                langCode = ZH;
                mSourceLang.setText("China");
                break;
                default:
                    langCode = 0;
        }
        translatedText(langCode);
    }

    //Hàm dịch ngôn ngữ theo source > target
    private void translatedText(final int sourceLangCode){
        mTranslatedText.setText("Translating..");
        // Create an Multiple Languages translator:
        int targetLangCode = 0;
        if (spinnerLang.getSelectedItemId()==0){
            targetLangCode = FirebaseTranslateLanguage.EN;
                }
        else if (spinnerLang.getSelectedItemId()==1){
            targetLangCode = FirebaseTranslateLanguage.VI;
                }
        else if (spinnerLang.getSelectedItemId()==2){
            targetLangCode = FirebaseTranslateLanguage.ZH;
                }
        else if (spinnerLang.getSelectedItemId()==3){
            targetLangCode = FirebaseTranslateLanguage.KO;
                }
        else if (spinnerLang.getSelectedItemId()==4){
            targetLangCode = FirebaseTranslateLanguage.FR;
                }
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(sourceLangCode)
                        .setTargetLanguage(targetLangCode)
                        .build();

        final FirebaseTranslator translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        //Check for model
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
        translator.translate(sourceText)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                mTranslatedText.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mTranslatedText.setText("An Error Occurred !!!");
                            }
                        });

    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
