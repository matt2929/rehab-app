package com.example.matthew.rehabfirsttry.Activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matthew.rehabfirsttry.R;
import com.example.matthew.rehabfirsttry.UserWorkouts.PickUpCount;
import com.example.matthew.rehabfirsttry.UserWorkouts.PickUpHold;
import com.example.matthew.rehabfirsttry.UserWorkouts.WalkWithCup;
import com.example.matthew.rehabfirsttry.UserWorkouts.WorkoutSession;
import com.example.matthew.rehabfirsttry.Utilities.MessagingValues;
import com.example.matthew.rehabfirsttry.Utilities.SampleAverage;
import com.example.matthew.rehabfirsttry.Utilities.SaveData;
import com.example.matthew.rehabfirsttry.Utilities.Serialize;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InsideCup extends Activity implements SensorEventListener, TextToSpeech.OnInitListener {
    SaveData saveData;
    GoogleApiClient mGoogleApiClient;
    private Node mNode;
    private SensorManager mSensorManager;
    private float accX = 0, accY = 0, accZ = 0;
    private float gyroX = 0, gyroY = 0, gyroZ = 0;
    private Sensor mAcc, mGyro;
    private TextView textView;
    AudioManager mgr = null;
    private WorkoutSession currentWorkout;
    public boolean workoutInProgress = false;
    private int sendWorkoutStringToWatchCount = 0, getSendWorkoutStringToWatchMax = 10;
    private float lastValue = 10;
    private SampleAverage sampleAverage = new SampleAverage();

    TextToSpeech tts;
    String saveString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_cup);
        mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, 5, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        tts = new TextToSpeech(this, this);
        textView = (TextView) findViewById(R.id.insidecup);
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);

        } else {

        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mSensorManager.registerListener(this, mGyro, Sensor.TYPE_GRAVITY);
        } else {

        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .build();
        mGoogleApiClient.connect();

    }

    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new
            GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NodeApi.GetConnectedNodesResult result = Wearable.NodeApi
                                    .getConnectedNodes(mGoogleApiClient).await();
                            List<Node> nodes = result.getNodes();
                            if (nodes.size() > 0) {
                                mNode = nodes.get(0);
                            }
                        }
                    }).start();
                    Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            };
    MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {

            if (messageEvent.getPath().equals("/rehabwatchtophone")) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(messageEvent.getData());
                messageEvent.getData();
                String dataRaw = new String(byteBuffer.array());
                if (dataRaw.equals(MessagingValues.PICKUPCOUNT)) {

                    currentWorkout = new PickUpCount();
                    tts.speak("Please hold the cup correctly.", TextToSpeech.QUEUE_ADD, null);
                    saveData = new SaveData(getApplicationContext(), currentWorkout.getWorkoutName());
                } else if (dataRaw.equals(MessagingValues.PICKUPHOLD)) {
                    currentWorkout = new PickUpHold();
                    tts.speak("Please hold the cup correctly.", TextToSpeech.QUEUE_ADD, null);
                    saveData = new SaveData(getApplicationContext(), currentWorkout.getWorkoutName());
                } else if (dataRaw.equals(MessagingValues.WALKWITHCUP)) {
                    currentWorkout = new WalkWithCup();
                    tts.speak("Please hold the cup at your side.", TextToSpeech.QUEUE_ADD, null);
                    saveData = new SaveData(getApplicationContext(), currentWorkout.getWorkoutName());
                } else if (dataRaw.split("\\,")[0].equals(MessagingValues.SENDGRAVITYDATA)) {
                    float gravX = Float.parseFloat(dataRaw.split("\\,")[1]);
                    float gravY = Float.parseFloat(dataRaw.split("\\,")[2]);
                    float gravZ = Float.parseFloat(dataRaw.split("\\,")[3]);
                    textView.setText("Watch Data\nX: " + gravX + "\nY: " + gravY + "\nZ: " + gravZ);
                    if (currentWorkout != null) {
                        if (currentWorkout.goodHold(gravX, gravY, gravZ)) {
                            tts.speak("You held the cup correctly. Begin the workout.", TextToSpeech.QUEUE_ADD, null);

                            sendAMessageToWatch(MessagingValues.HOLDINGISACCURATE);
                            workoutInProgress = true;
                        }
                    }
                }
            }
        }
    };


    private void sendAMessageToWatch(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null) {
                    byte[] bytes = (message.getBytes());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                            "/rehabphonetowatch", bytes).await();
                } else {

                }
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];
        } else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];
        }
        if (workoutInProgress) {

            Calendar cal = Calendar.getInstance();


            sendWorkoutStringToWatchCount++;
            //Send Textual information to watch
            if (sendWorkoutStringToWatchCount == getSendWorkoutStringToWatchMax) {

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                float magval = (float) Math.pow(Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2), .5);
                float magDiff = Math.abs(magval - lastValue);
                lastValue = magval;


                currentWorkout.dataIn(accX, accY, accZ, gyroX, gyroY, gyroZ);
                //Should App Talk?
                if (currentWorkout.shouldISaySomething()) {
                    tts.speak(currentWorkout.whatToSay(), TextToSpeech.QUEUE_ADD, null);
                }


                sampleAverage.addSmoothAverage(magDiff);

                saveString +=
                        +hour + ":" + minute + ":" + second
                                + "," + accX + "," + accY + "," + accZ + ","
                                + sampleAverage.getMedianAverage() + ";";
                sendAMessageToWatch(MessagingValues.WORKOUTDISPLAYDATA + ",\n\n\n\n" + currentWorkout.result());
                sendWorkoutStringToWatchCount = 0;
            }
            //Save Game
            if (currentWorkout.workoutFinished()) {
                tts.speak("Workout Complete.", TextToSpeech.QUEUE_ADD, null);
                saveData.saveData(saveString);
                try {
                    Serialize serialize = new Serialize(getApplicationContext());
                    serialize.Save(getApplicationContext(), currentWorkout.getWorkoutName(), currentWorkout.howAccurate());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveString = "";
                sendAMessageToWatch(MessagingValues.WORKOUTOVER);
                workoutInProgress = false;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            // Setting speech language
            int result = tts.setLanguage(Locale.ENGLISH);
            // If your device doesn't support language you set above
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Cook simple toast message with message
                Toast.makeText(getApplicationContext(), "Language not supported",
                        Toast.LENGTH_LONG).show();
                Log.e("TTS", "Language is not supported");
            }
            // Enable the button - It was disabled in main.xml (Go back and
            // Check it)
            else {

            }
            // TTS is not initialized properly
        } else {
            Toast.makeText(this, "TTS Initilization Failed", Toast.LENGTH_LONG)
                    .show();
            Log.e("TTS", "Initilization Failed");
        }
    }
}

