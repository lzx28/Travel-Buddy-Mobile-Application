/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package translateapp.jason.com;

import android.content.DialogInterface;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.translate.MLTranslateLanguage;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;


import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class VoiceTranslation extends AppCompatActivity{

    DatabaseHelper mDatabaseHelper;
    private static final String TAG = VoiceTranslation.class.getSimpleName();
    private static final int ML_ASR_CAPTURE_CODE = 100;
    private static final int AUDIO_PERMISSION_CODE = 1;
    private Intent intent;
    Button bTranslate, asr, bTextSpeak, bTextStopSpeak, bEditSpeak, bEditStopSpeak;
    //EditText mEdit1, mEdit2;
    TextView textView1;
    Spinner fromSpinner, toSpinner;
    TextInputEditText mEdit1;
    MaterialButton switchButton;
    String fromLanguageCode = " ";
    String toLanguageCode = " ";
    String voiceLanguageCode = " ";
    MLTtsEngine mlTtsEngine, mlTtsEngine2;
    MLTtsConfig mlConfigs, mlConfigs2;
    static String saveSourceText = " ";
    static String saveTargetText = " ";
    String[] sourceLanguage = {"English", "Arabic", "Chinese", "French", "German", "Italian", "Malay", "Russian", "Spanish", "Thai"};

    String[] targetLanguage = {"Chinese", "Arabic", "Danish", "German", "Spanish", "Finnish", "French", "Italian", "Japanese",
            "Korean", "Polish", "Portuguese", "Russian", "Swedish", "Thai", "Turkish", "English", "Malay", "Norwegian", "Vietnamese",
            "Czech", "Greek", "Hebrew", "Hindi", "Indonesian", "Romanian", "Serbian", "Tagalog", "Central Khmer", "Burmese", "Tamil", "Hungarian",
            "Dutch", "Persian", "Slovak", "Estonian", "Latvian", "Bulgarian", "Croatian"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_translation);
        bTranslate = findViewById(R.id.translateButton);
        bTextSpeak = findViewById(R.id.btn_speak_to);
        bTextStopSpeak = findViewById(R.id.btn_stop_speak_to);
        bEditSpeak = findViewById(R.id.btn_speak_from);
        bEditStopSpeak = findViewById(R.id.btn_stop_speak_from);
        mEdit1 = findViewById(R.id.sourceText);
        textView1 = findViewById(R.id.targetText);
        fromSpinner = findViewById(R.id.sourceLangSelector);
        toSpinner = findViewById(R.id.targetLangSelector);
        switchButton = findViewById(R.id.buttonSwitchLang);
        asr = findViewById(R.id.voiceButton);
        mDatabaseHelper = new DatabaseHelper(this);

        textView1.setSelected(true);
        textView1.setHorizontallyScrolling(true);
        setApiKey("CwEAAAAAA0Z08GmRcECTz0Tjciv5nSSZOcuhF5n6CeyPOXViRq70BcWxURZZ2pyzMaMBywA5hD5NZQYRR6Q9Ix72U5iF/ZZ4B1w=");

        bEditSpeak.setVisibility(View.GONE);
        bEditStopSpeak.setVisibility(View.GONE);
        bTextSpeak.setVisibility(View.GONE);
        bTextStopSpeak.setVisibility(View.GONE);

        asr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initIntent();
            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(sourceLanguage[position]);
                voiceLanguageCode = getVoiceLanguageCode(sourceLanguage[position]);
                fromSpeakCall(fromLanguageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, sourceLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(sourceLanguage[position]);
                toSpeakCall(toLanguageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, sourceLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        bTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEdit1.getText().toString().isEmpty()) {
                    Toast.makeText(VoiceTranslation.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                } else {
                    remoteTranslator(fromLanguageCode, toLanguageCode, mEdit1.getText().toString());
                    saveSourceText = mEdit1.getText().toString();
                }


            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sourceLangPosition = fromSpinner.getSelectedItemPosition();
                fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
                toSpinner.setSelection(sourceLangPosition);
            }
        });

        bEditSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSpeak();
            }
        });

        bEditStopSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textStopSpeak();
            }
        });


        bTextSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSpeak2();
            }
        });

        bTextStopSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textStopSpeak2();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            this.requestAudioPermission();
        }
    }

    private void requestAudioPermission() {
        final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, permissions, VoiceTranslation.AUDIO_PERMISSION_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != VoiceTranslation.AUDIO_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
    }

    private void setApiKey(String apiKey) {
        MLApplication.getInstance().setApiKey(apiKey);
    }


    public String getLanguageCode(String language) {
        String languageCode = " ";

        if (language.equals("English")) {
            languageCode = "en";
        } else if (language.equals("Arabic")) {
            languageCode = "ar";
        } else if (language.equals("Danish")) {
            languageCode = "da";
        } else if (language.equals("German")) {
            languageCode = "de";
        } else if (language.equals("Spanish")) {
            languageCode = "es";
        } else if (language.equals("Finnish")) {
            languageCode = "fi";
        } else if (language.equals("French")) {
            languageCode = "fr";
        } else if (language.equals("Italian")) {
            languageCode = "it";
        } else if (language.equals("Japanese")) {
            languageCode = "ja";
        } else if (language.equals("Korean")) {
            languageCode = "ko";
        } else if (language.equals("Polish")) {
            languageCode = "pl";
        } else if (language.equals("Portuguese")) {
            languageCode = "pt";
        } else if (language.equals("Russian")) {
            languageCode = "ru";
        } else if (language.equals("Swedish")) {
            languageCode = "sv";
        } else if (language.equals("Thai")) {
            languageCode = "th";
        } else if (language.equals("Turkish")) {
            languageCode = "tr";
        } else if (language.equals("Chinese")) {
            languageCode = "zh";
        } else if (language.equals("Malay")) {
            languageCode = "ms";
        } else if (language.equals("Norwegian")) {
            languageCode = "no";
        } else if (language.equals("Vietnamese")) {
            languageCode = "vi";
        } else if (language.equals("Czech")) {
            languageCode = "cs";
        } else if (language.equals("Greek")) {
            languageCode = "el";
        } else if (language.equals("Hebrew")) {
            languageCode = "he";
        } else if (language.equals("Hindi")) {
            languageCode = "hi";
        } else if (language.equals("Indonesian")) {
            languageCode = "id";
        } else if (language.equals("Romanian")) {
            languageCode = "ro";
        } else if (language.equals("Serbian")) {
            languageCode = "sr";
        } else if (language.equals("Tagalog")) {
            languageCode = "tl";
        } else if (language.equals("Central Khmer")) {
            languageCode = "km";
        } else if (language.equals("Burmese")) {
            languageCode = "my";
        } else if (language.equals("Tamil")) {
            languageCode = "ta";
        } else if (language.equals("Hungarian")) {
            languageCode = "hu";
        } else if (language.equals("Dutch")) {
            languageCode = "nl";
        } else if (language.equals("Persian")) {
            languageCode = "fa";
        } else if (language.equals("Slovak")) {
            languageCode = "sk";
        } else if (language.equals("Estonian")) {
            languageCode = "et";
        } else if (language.equals("Latvian")) {
            languageCode = "lv";
        } else if (language.equals("Bulgarian")) {
            languageCode = "bg";
        } else if (language.equals("Croatian")) {
            languageCode = "hr";
        }

        return languageCode;
    }

    public String getVoiceLanguageCode(String language)
    {
        String voiceLanguageCode = " ";

        if (language.equals("English")) {
            voiceLanguageCode = "en-US";
        } else if (language.equals("Arabic")) {
            voiceLanguageCode = "ar";
        } else if (language.equals("Chinese")) {
            voiceLanguageCode = "zh-CN";
        } else if (language.equals("French")) {
            voiceLanguageCode = "fr-FR";
        } else if (language.equals("German")) {
            voiceLanguageCode = "de-DE";
        } else if (language.equals("Italian")) {
            voiceLanguageCode = "it-IT";
        } else if (language.equals("Malay")) {
            voiceLanguageCode = "ms";
        }else if (language.equals("Russian")) {
            voiceLanguageCode = "ru-RU";
        } else if (language.equals("Spanish")) {
            voiceLanguageCode = "es-ES";
        } else if (language.equals("Thai")) {
            voiceLanguageCode = "th-TH";
        }

        return voiceLanguageCode;
    }

    /**
     * Translation on the cloud. If you want to use cloud remoteTranslator,
     * you need to apply for an agconnect-services.json file in the developer
     * alliance(https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc),
     * replacing the sample-agconnect-services.json in the project.
     */
    private void remoteTranslator(String fromLanguageCode, String toLanguageCode, String sourceText) {
        MLApplication.getInstance().setAccessToken("CwEAAAAAA0Z08GmRcECTz0Tjciv5nSSZOcuhF5n6CeyPOXViRq70BcWxURZZ2pyzMaMBywA5hD5NZQYRR6Q9Ix72U5iF/ZZ4B1w=");
        // Create an analyzer. You can customize the analyzer by creating MLRemoteTranslateSetting
        MLRemoteTranslateSetting setting =
                new MLRemoteTranslateSetting.Factory().setSourceLangCode(fromLanguageCode).setTargetLangCode(toLanguageCode).create();
        MLRemoteTranslator remoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
        // Use default parameter settings.
        // analyzer = MLTranslatorFactory.getInstance().getRemoteTranslator();
        // Read text in edit box.
        Task<String> task = remoteTranslator.asyncTranslate(sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                // Recognition success.

                textView1.setText("Translating");
                textView1.setText(text);
                saveTargetText = text;
                AddData(saveSourceText, saveTargetText);
                stop(remoteTranslator);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Recognition failure.
                Toast.makeText(VoiceTranslation.this, "Translation Failed", Toast.LENGTH_SHORT).show();
                Log.e(VoiceTranslation.TAG, "failed: " + e.getMessage());
                try {
                    stop(remoteTranslator);
                } catch (Exception error) {
                    stop(remoteTranslator);
                }
            }
        });
    }

    private void queryAllLanguages() {
        MLTranslateLanguage.getCloudAllLanguages().addOnSuccessListener(
                new OnSuccessListener<Set<String>>() {
                    @Override
                    public void onSuccess(Set<String> result) {
                        // Languages supported by on-cloud translation are successfully obtained.
                    }
                });
    }

    private void stop(MLRemoteTranslator mlRemoteTranslator) {
        if (mlRemoteTranslator != null) {
            mlRemoteTranslator.stop();
        }
    }

    private void initIntent() {
        // Use Intent for recognition settings.
        this.intent = new Intent(this, MLAsrCaptureActivity.class)
                // Set the language that can be recognized to English. If this parameter is not set,
                // English is recognized by default. Example: "MLAsrConstants.LAN_ZH_CN": Chinese;
                // "MLAsrConstants.LAN_EN_US": English; "MLAsrConstants.LAN_FR_FR": French; "MLAsrConstants.LAN_ES_ES": Spanish;
                // "MLAsrConstants.LAN_DE_DE": German
                .putExtra(MLAsrCaptureConstants.LANGUAGE, voiceLanguageCode)
                // Set whether to display text on the speech pickup UI. MLAsrCaptureConstants.FEATURE_ALLINONE: no;
                // MLAsrCaptureConstants.FEATURE_WORDFLUX: yes.
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        startActivityForResult(intent, ML_ASR_CAPTURE_CODE); //interface

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String text = "";
        if (null == data) {
            // do something
        }
        // ML_ASR_CAPTURE_CODE: request code between the current activity and speech pickup UI activity.
        if (requestCode == ML_ASR_CAPTURE_CODE) {
            switch (resultCode) {
                // MLAsrCaptureConstants.ASR_SUCCESS: Recognition is successful.
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        // Obtain the text information recognized from speech.
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                        if (text == null || "".equals(text)) {
                            Toast.makeText(VoiceTranslation.this, "No voice is detected", Toast.LENGTH_SHORT).show();
                        }
                        // Process the recognized text information.
                        displayResult(text);
                    }
                    break;
                // MLAsrCaptureConstants.ASR_FAILURE: Recognition fails.
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        // Check whether a result code is contained.
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            text = text + bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            // Perform troubleshooting based on the result code.
                        }
                        // Check whether error information is contained.
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                            String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                            // Perform troubleshooting based on the error information.
                            if (errorMsg != null && !"".equals(errorMsg)) {
                                text = "[" + text + "]" + errorMsg;
                            }
                        }
                        // Check whether a sub-result code is contained.
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            // Process the sub-result code.
                            text = "[" + text + "]" + subErrorCode;
                        }
                    }
                    displayResult(text);
                    break;
                default:
                    displayResult("Failure.");
                    break;
            }
        }
    }

    private void displayResult(String info) {
        mEdit1.setText(info);
    }

    MLTtsCallback callback = new MLTtsCallback() {
        @Override
        public void onError(String taskId, MLTtsError err) {
            // Processing logic for TTS failure.
            String str = "TaskID: " + taskId + ", error:" + err;
            //displayResult(str);
        }

        @Override
        public void onWarn(String taskId, MLTtsWarn warn) {
            // Alarm handling without affecting service logic.
            String str = "TaskID: " + taskId + ", warn:" + warn;
            //displayResult(str);
        }

        @Override
        public void onRangeStart(String taskId, int start, int end) {
            // Process the mapping between the currently played segment and text.
            String str = "TaskID: " + taskId + ", onRangeStart [" + start + "，" + end + "]";
            //displayResult(str);
        }

        @Override
        public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment, int i, Pair<Integer, Integer> pair, Bundle bundle) {
            //  Audio stream callback API, which is used to return the synthesized audio data to the app.
            //  taskId: ID of an audio synthesis task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One audio synthesis task corresponds to an audio synthesis queue.
            //  range: text area where the audio segment to be transmitted is located; range.first (included): start position; range.second (excluded): end position.
        }

        @Override
        // Callback method of a TTS event. eventName: event name. The events are as follows:
        // MLTtsConstants.EVENT_PLAY_RESUME: playback resumption.
        // MLTtsConstants.EVENT_PLAY_PAUSE: playback pause.
        // MLTtsConstants.EVENT_PLAY_STOP: playback stop.
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            String str = "TaskID: " + taskId + ", eventName:" + eventId;
            // Callback method of an audio synthesis event. eventId: event name.
            switch (eventId) {
                case MLTtsConstants.EVENT_PLAY_START:
                    //  Called when playback starts.
                    break;
                case MLTtsConstants.EVENT_PLAY_STOP:
                    // Called when playback stops.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED);
                    str += " " + isInterrupted;
                    break;
                case MLTtsConstants.EVENT_PLAY_RESUME:
                    //  Called when playback resumes.
                    break;
                case MLTtsConstants.EVENT_PLAY_PAUSE:
                    // Called when playback pauses.
                    break;

                /*//Pay attention to the following callback events when you focus on only synthesized audio data but do not use the internal player for playback:
                case MLTtsConstants.EVENT_SYNTHESIS_START:
                    //  Called when TTS starts.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_END:
                    // Called when TTS ends.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_COMPLETE:
                    // TTS is complete. All synthesized audio streams are passed to the app.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED);
                    break;*/
                default:
                    break;
            }

            //displayResult(str);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mlTtsEngine != null) {
            this.mlTtsEngine.shutdown();
        }

        if (this.mlTtsEngine2 != null) {
            this.mlTtsEngine2.shutdown();
        }
    }

    public void textSpeak()
    {
        mlTtsEngine = new MLTtsEngine(mlConfigs);
        mlTtsEngine.setTtsCallback(callback);
        String text = mEdit1.getText().toString();
        /**
         *First parameter sourceText: text information to be synthesized. The value can contain a maximum of 500 characters.
         *Second parameter indicating the synthesis mode: The format is configA | configB | configC.
         *configA：
         *    MLTtsEngine.QUEUE_APPEND：After an audio synthesis task is generated, the audio synthesis task is processed as follows: If playback is going on, the task is added to the queue for execution in sequence; if playback pauses, the playback is resumed and the task is added to the queue for execution in sequence; if there is no playback, the audio synthesis task is executed immediately.
         *    MLTtsEngine.QUEUE_FLUSH：The ongoing audio synthesis task and playback are stopped immediately, all audio synthesis tasks in the queue are cleared, and the current audio synthesis task is executed immediately and played.
         *configB：
         *    MLTtsEngine.OPEN_STREAM：The synthesized audio data is output through onAudioAvailable.
         *configC：
         *    MLTtsEngine.EXTERNAL_PLAYBACK：external playback mode. The player provided by the SDK is shielded. You need to process the audio output by the onAudioAvailable callback API. In this case, the playback-related APIs in the callback APIs become invalid, and only the callback APIs related to audio synthesis can be listened.
         */
        // Use the built-in player of the SDK to play speech in queuing mode.
        mlTtsEngine.speak(text, MLTtsEngine.QUEUE_APPEND);
    }

    public void textStopSpeak()
    {
        mlTtsEngine.stop();
    }

    MLTtsCallback callback2 = new MLTtsCallback() {
        @Override
        public void onError(String taskId, MLTtsError err) {
            // Processing logic for TTS failure.
            String str = "TaskID: " + taskId + ", error:" + err;
            //displayResult(str);
        }

        @Override
        public void onWarn(String taskId, MLTtsWarn warn) {
            // Alarm handling without affecting service logic.
            String str = "TaskID: " + taskId + ", warn:" + warn;
            //displayResult(str);
        }

        @Override
        public void onRangeStart(String taskId, int start, int end) {
            // Process the mapping between the currently played segment and text.
            String str = "TaskID: " + taskId + ", onRangeStart [" + start + "，" + end + "]";
            //displayResult(str);
        }

        @Override
        public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment, int i, Pair<Integer, Integer> pair, Bundle bundle) {
            //  Audio stream callback API, which is used to return the synthesized audio data to the app.
            //  taskId: ID of an audio synthesis task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One audio synthesis task corresponds to an audio synthesis queue.
            //  range: text area where the audio segment to be transmitted is located; range.first (included): start position; range.second (excluded): end position.
        }

        @Override
        // Callback method of a TTS event. eventName: event name. The events are as follows:
        // MLTtsConstants.EVENT_PLAY_RESUME: playback resumption.
        // MLTtsConstants.EVENT_PLAY_PAUSE: playback pause.
        // MLTtsConstants.EVENT_PLAY_STOP: playback stop.
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            String str = "TaskID: " + taskId + ", eventName:" + eventId;
            // Callback method of an audio synthesis event. eventId: event name.
            switch (eventId) {
                case MLTtsConstants.EVENT_PLAY_START:
                    //  Called when playback starts.
                    break;
                case MLTtsConstants.EVENT_PLAY_STOP:
                    // Called when playback stops.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED);
                    str += " " + isInterrupted;
                    break;
                case MLTtsConstants.EVENT_PLAY_RESUME:
                    //  Called when playback resumes.
                    break;
                case MLTtsConstants.EVENT_PLAY_PAUSE:
                    // Called when playback pauses.
                    break;

                /*//Pay attention to the following callback events when you focus on only synthesized audio data but do not use the internal player for playback:
                case MLTtsConstants.EVENT_SYNTHESIS_START:
                    //  Called when TTS starts.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_END:
                    // Called when TTS ends.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_COMPLETE:
                    // TTS is complete. All synthesized audio streams are passed to the app.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED);
                    break;*/
                default:
                    break;
            }

            //displayResult(str);
        }
    };


    public void textSpeak2()
    {
        mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
        mlTtsEngine2.setTtsCallback(callback2);
        String text = textView1.getText().toString();
        /**
         *First parameter sourceText: text information to be synthesized. The value can contain a maximum of 500 characters.
         *Second parameter indicating the synthesis mode: The format is configA | configB | configC.
         *configA：
         *    MLTtsEngine.QUEUE_APPEND：After an audio synthesis task is generated, the audio synthesis task is processed as follows: If playback is going on, the task is added to the queue for execution in sequence; if playback pauses, the playback is resumed and the task is added to the queue for execution in sequence; if there is no playback, the audio synthesis task is executed immediately.
         *    MLTtsEngine.QUEUE_FLUSH：The ongoing audio synthesis task and playback are stopped immediately, all audio synthesis tasks in the queue are cleared, and the current audio synthesis task is executed immediately and played.
         *configB：
         *    MLTtsEngine.OPEN_STREAM：The synthesized audio data is output through onAudioAvailable.
         *configC：
         *    MLTtsEngine.EXTERNAL_PLAYBACK：external playback mode. The player provided by the SDK is shielded. You need to process the audio output by the onAudioAvailable callback API. In this case, the playback-related APIs in the callback APIs become invalid, and only the callback APIs related to audio synthesis can be listened.
         */
        // Use the built-in player of the SDK to play speech in queuing mode.
        mlTtsEngine2.speak(text, MLTtsEngine.QUEUE_APPEND);
    }

    public void textStopSpeak2()
    {
        mlTtsEngine2.stop();
    }

    public void fromSpeakCall(String fromCode)
    {
        switch (fromCode) {
            case "en":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_EN_US)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            case "fr":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_FR_FR)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_FR)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            case "de":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_DE_DE)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_DE)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            case "it":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_IT_IT)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_IT)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            case "es":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_ES_ES)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ES)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            case "zh":
                bEditSpeak.setVisibility(View.VISIBLE);
                bEditStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_ZH_HANS)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ZH)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine = new MLTtsEngine(mlConfigs);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine.setTtsCallback(callback);
                break;
            default:
                bEditSpeak.setVisibility(View.GONE);
                bEditStopSpeak.setVisibility(View.GONE);
                break;
        }
    }

    public void toSpeakCall(String toCode)
    {
        switch (toCode) {
            case "en":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_EN_US)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            case "fr":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_FR_FR)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_FR)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            case "de":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_DE_DE)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_DE)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            case "it":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_IT_IT)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_IT)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            case "es":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_LAN_ES_ES)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ES)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            case "zh":
                bTextSpeak.setVisibility(View.VISIBLE);
                bTextStopSpeak.setVisibility(View.VISIBLE);
                mlConfigs2 = new MLTtsConfig()
                        // Set the text converted from speech to English.
                        // MLTtsConstants.TTS_EN_US: converts text to English.
                        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
                        .setLanguage(MLTtsConstants.TTS_ZH_HANS)
                        // Set the English timbre.
                        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
                        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
                        .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ZH)
                        // Set the speech speed. Range: 0.2–1.8. 1.0 indicates 1x speed.
                        .setSpeed(1.0f)
                        // Set the volume. Range: 0.2–1.8. 1.0 indicates 1x volume.
                        .setVolume(1.0f);
                mlTtsEngine2 = new MLTtsEngine(mlConfigs2);
                // Pass the TTS callback to the TTS engine.
                mlTtsEngine2.setTtsCallback(callback2);
                break;
            default:
                bTextSpeak.setVisibility(View.GONE);
                bTextStopSpeak.setVisibility(View.GONE);
                break;
        }
    }

    public void AddData(String sourceText, String targetText)
    {
        boolean insertData = mDatabaseHelper.addData(sourceText, targetText);

        if (insertData)
        {
            Toast.makeText(VoiceTranslation.this, "Translation Record is saved into History", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(VoiceTranslation.this, "Data insert fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void Back(View view){
        //Back to Home
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}
