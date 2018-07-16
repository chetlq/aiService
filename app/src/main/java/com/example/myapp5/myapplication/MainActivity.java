package com.example.myapp5.myapplication;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import ai.api.AIListener;

import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener{
    public static  TextToSpeech t1;

    private AIService aiService;

    private ImageButton listenButton;

    private TextView resultTextView;

    private Timer mTimer1;
    private TimerTask mTt1;
    private int i=1;



    private Handler mTimerHandler1 = new Handler();

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }



    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler1.post(new Runnable() {
                    public void run(){
                        try {
                            if(!t1.isSpeaking()){

                               if(i==0)
                                   aiService.startListening();
                               i=1;
                                Log.i("TAG2", "mTimer1 START");
                            }else{aiService.stopListening();
                            i=0;
                                Log.i("TAG2", "mTimer1 STOP");}
                        }catch (Exception e){}

                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 3000, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    t1.setLanguage(Locale.getDefault());
                    int min = 0;
                    int max = 2;

                    Random r = new Random();
                    int i1 = r.nextInt(max - min + 1) + min-1;
                    switch (i1) {
                        case 0:  t1.speak("Приветствую, чем могу помочь? ", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                        case 1:  t1.speak("Привет, чем могу помочь? ", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                        case 2:  t1.speak("Здравствуйте, чем могу помочь?", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                        default: t1.speak("Привет, чем могу помочь? ", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                    }



                } else {
                    Log.e("MainActivity", "Initilization Failed!");
                }

            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        listenButton =(ImageButton) findViewById(R.id.btnSpeak);


        resultTextView =  (TextView) findViewById(R.id.txtSpeechInput);
        startTimer();


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//                ;
//            }
//        }, 5000);

        final AIConfiguration config = new AIConfiguration("043275d5428249b99d867c94f1ff410c",
                AIConfiguration.SupportedLanguages.Russian,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiService.startListening();
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiService.startListening();
            }
        });

    //listenButton.performClick();

    }



    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Result result = response.getResult();
                ResponseMessage.ResponseSpeech responseMessage = null;
                int messageCount = result.getFulfillment().getMessages().size();
                if (messageCount > 1) {
                    for (int i = 0; i < messageCount; i++) {
                        responseMessage = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(i);

                    }
                }

         //       JSONObject jsonObj = new JSONObject((Map) result);

                // Get parameters
                // result.


                JsonElement str=null;
                String val =null;
                final String speech = result.getFulfillment().getSpeech();
                t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            t1.setLanguage(Locale.getDefault());
                            t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                        } else {
                            Log.e("MainActivity", "Initilization Failed!");
                        }
//                        if(status != TextToSpeech.ERROR) {
//                            t1.setLanguage(Locale.getDefault());
//                            t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
//
//                        }
                    }

                });


                if (result.getParameters() != null && !result.getParameters().isEmpty()) {


                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                      //  parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";

                    }
                }
                val =result.getStringParameter("number");
                // Show results in TextView.
//                if (str!=null) {
                    resultTextView.setText(speech);//"Query:" + result.getResolvedQuery() +
//                            "\nspeech: " + speech +
//                            "\nParameters: " + parameterString);
                if(speech.equals("До свиданья!")||speech.equals("Всего хорошего!")) {
                    stopTimer();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    }, 2000);

                }


//                }
            }
        });
    };


    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //resultTextView.setText(error.toString());
            }
        });
        Log.i("TAG2", "onError!");

i=0;
    }

    @Override
    public void onAudioLevel(float level) {
        Log.i("TAG2", "onAutoLevel!");

    }

    @Override
    public void onListeningStarted() {
        Log.i("TAG2", "onListeningStarted!");

    }

    @Override
    public void onListeningCanceled() {
        Log.i("TAG2", "onListeningCanceled!");
    }

    @Override
    public void onListeningFinished() {
        Log.i("TAG2", "onListeningFinished!");
        i=0;
    }


}
