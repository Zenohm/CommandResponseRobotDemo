package com.example.fabio.commandresponserobotdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * This demo recognizes voice commands and sends them to the robot unit.
 *
 * @author fiorfe01
 */
public class MainActivity extends Activity {

    //test 2
    // speech input
    private SpeechRecognizer speechRecognizer;

    // intent for speech recognition
    Intent speechIntent;

    // response output (I called her moira!)
    private TextToSpeech moira;

    // robot object for command execution
    private Robot rob;

    // valid commands
    private ArrayList<String> validCommands;

    // responses to valid commands
    private ArrayList<String> responses;

    // response text view
    TextView responseText;

    // speak button
    FloatingActionButton btnSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // force application to perform network IO on the UI thread
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

        // instantiate and populate valid commands
        validCommands = new ArrayList<String>();
        validCommands.add("go forward");
        validCommands.add("go backward");
        validCommands.add("turn left");
        validCommands.add("turn right");
        validCommands.add("good night");

        // instantiate and populate responses
        responses = new ArrayList<String>();
        responses.add("OK, I will go forward");
        responses.add("OK, I will go backward");
        responses.add("OK, I will turn left");
        responses.add("OK, I will turn right");
        responses.add("Good night");

        // instantiate ui objects
        responseText = (TextView) findViewById(R.id.responseText);
        btnSpeak = (FloatingActionButton) findViewById(R.id.speak);

        // set on click listener for speak button
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speechRecognizer.startListening(speechIntent);
            }
        });
    }

    @Override
    protected void onStart() {

        // instantiate speechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        SpeechListener recognitionListener = new SpeechListener();
        speechRecognizer.setRecognitionListener(recognitionListener);

        // instantiate speech intent
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 600000000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        // instantiate moira
        moira = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            public void onInit(int status) {

                if (status != TextToSpeech.ERROR) {

                    // set language to US
                    moira.setLanguage(Locale.US);
                }
            }
        });

        // instantiate rob
        rob = new Robot();

        super.onStart();
    }

    @Override
    protected void onPause() {

        // kill speech recognizer
        if (speechRecognizer != null) {

            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        // see you later, moira
        if (moira != null) {

            moira.stop();
            moira.shutdown();
        }

        super.onPause();
    }

    /**
     * Processes a voice command.
     *
     * @param matchStrings
     */
    private void processCommand(ArrayList<String> matchStrings) {

        int maxStrings = matchStrings.size();

        // find matching command
        for (int i = 0; i < validCommands.size(); i++) {

            for (int j = 0; j < maxStrings; j++) {

                // use the Levenshtein distance to measure the distance between input and valid command
                if (StringUtils.getLevenshteinDistance(matchStrings.get(j), validCommands.get(i)) < (validCommands.get(i).length() / 3)) {

                    // moira responds
                    moira.speak(responses.get(i), TextToSpeech.QUEUE_FLUSH, null, null);

                    // response text is displayed
                    responseText.setText(responses.get(i));

                    sendCommand(i);

                    return;
                }
            }
        }

        // command not recognized
        moira.speak("What did you say?", TextToSpeech.QUEUE_FLUSH, null, null);

        responseText.setText("What did you say?");
    }

    /**
     * Sends commands to robot unit.
     *
     * @param command
     */
    private void sendCommand(int command) {

        switch (command) {

            case 0:

                rob.forward();

                break;

            case 1:

                rob.backward();

                break;

            case 2:

                rob.left();

                break;

            case 3:

                rob.right();

                break;

            case 4:

                rob.beep();
                rob.dock();

                finish();

                break;
        }
    }

    /**
     * Speech listener for speech recognizer. All methods (except for onResults)
     * are intentionally left empty for future customization.
     */
    class SpeechListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {

            // stores command matches
            ArrayList<String> matches = null;

            if (results != null) {

                // set matches from speech recognizer
                matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {

                    processCommand(matches);
                }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }
}