package com.example.matthew.rehabfirsttry;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class WorkoutInterface extends Activity implements SensorEventListener {
    GoogleApiClient mGoogleApiClient;
    private Node mNode;
    private SensorManager mSensorManager;
    Button pickUpCup, holdUpCup, walkWithCup, somthing1Button, somthing2Button, somthing3Button;
    ArrayList<Button> AllButtons = new ArrayList<Button>();
    private float gyroX = 0, gyroY = 0, gyroZ = 0;
    private int targetX = 0, targetY = 0;
    private Sensor mGyro;
    ballView accXView, accYView, accZView;
    public static boolean leftHand = false;
    private ProgressBar progressBar;
    private TextView mTextView;
    ballView ballViewW;
    Button leftButton, rightButton;
    private boolean phoneWantsMyData = false;
    private int phoneSendSensorDataCount = 0, getPhoneSendSensorDataRate = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_interface);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                leftButton = (Button) stub.findViewById(R.id.lefthandbutt);
                rightButton = (Button) stub.findViewById(R.id.righthandbutt);
                accXView = (ballView) stub.findViewById(R.id.AccX);
                somthing1Button = (Button) stub.findViewById(R.id.twistbutton);
                somthing2Button = (Button) stub.findViewById(R.id.moveupanddownbutton);
                somthing3Button = (Button) stub.findViewById(R.id.pourwaterbutton);
                pickUpCup = (Button) stub.findViewById(R.id.wearwatchpickup);
                holdUpCup = (Button) stub.findViewById(R.id.wearholdup);
                walkWithCup = (Button) stub.findViewById(R.id.wearwalk);
                mTextView = (TextView) stub.findViewById(R.id.watchworkouttext);
                progressBar = (ProgressBar) stub.findViewById(R.id.accprog);
                AllButtons.add(somthing1Button);
                AllButtons.add(somthing2Button);
                AllButtons.add(somthing3Button);
                AllButtons.add(pickUpCup);
                AllButtons.add(holdUpCup);
                AllButtons.add(walkWithCup);


                ballViewW = (ballView) stub.findViewById(R.id.AccX);
                selectWorkoutView();
            }
        });
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
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
            if (messageEvent.getPath().equals("/rehabphonetowatch")) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(messageEvent.getData());
                messageEvent.getData();
                String dataRaw = new String(byteBuffer.array());
                if (dataRaw.equals(MessagingValues.HOLDINGISACCURATE)) {
                    phoneWantsMyData = false;
                    workoutInfoView();
                } else if (dataRaw.split("\\,")[0].equals(MessagingValues.WORKOUTDISPLAYDATA)) {
                    mTextView.setText(dataRaw.split("\\,")[1]);
                } else if (dataRaw.equals(MessagingValues.WORKOUTOVER)) {
                    selectWorkoutView();
                } else if (dataRaw.split("\\,")[0].equals(MessagingValues.SENDPOSITIONAL)) {
                    targetX = Integer.parseInt(dataRaw.split("\\,")[1]);
                    targetY = Integer.parseInt(dataRaw.split("\\,")[2]);

                } else if (dataRaw.split("\\,")[0].equals(MessagingValues.SENDPOSITIONALPROG)) {
                    progressBar.setProgress((int) (Double.parseDouble(dataRaw.split("\\,")[1]) * 100));
                    if (!dataRaw.split("\\,")[1].equals("0.0")) {
                        ballViewW.isInPostion(true);
                    } else {
                        ballViewW.isInPostion(false);
                    }

                }
            }
        }
    };

    private void sendPhoneAMessage(final String ActivityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null) {
                    byte[] bytes = (ActivityName.getBytes());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                            "/rehabwatchtophone", bytes).await();

                } else {

                }
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (phoneWantsMyData) {
            phoneSendSensorDataCount++;
            if (phoneSendSensorDataCount == getPhoneSendSensorDataRate) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_GRAVITY) {
                    gyroX = event.values[0];
                    gyroY = event.values[1];
                    gyroZ = event.values[2];
                    if (progressBar.getProgress() != 0) {
                        accXView.updateDrawing(gyroX, gyroY, targetX, targetY);

                    } else {
                        accXView.updateDrawing(gyroX, gyroY, targetX, targetY);

                    }

                    mTextView.setText("\nTartget | Actual \nX: " + targetX + " | " + gyroX + "\nY: " + targetY + " | " + gyroY);
                    sendPhoneAMessage(MessagingValues.SENDGRAVITYDATA + "," + gyroX + "," + gyroY + "," + gyroZ);

                }
                phoneSendSensorDataCount = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void leftOrRightHand() {
        hideAllButtons();
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftHand = true;
                sendPhoneAMessage(MessagingValues.LEFTHAND);
                selectWorkoutView();
                phoneWantsMyData = true;
                sendPhoneAMessage(MessagingValues.PICKUPHOLD);
                howToHoldCupView("Please Dont Move");

            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendPhoneAMessage(MessagingValues.RIGHTHAND);
                leftHand = false;
                selectWorkoutView();
                phoneWantsMyData = true;
                sendPhoneAMessage(MessagingValues.PICKUPHOLD);
                howToHoldCupView("Please Dont Move");

            }
        });
    }

    public void selectWorkoutView() {

        ballViewW.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        mTextView.setText("");
        showAllButtons();
        mTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        pickUpCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneAMessage(MessagingValues.PICKUPCOUNT);
                leftOrRightHand();
            }
        });
        holdUpCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneAMessage(MessagingValues.PICKUPHOLD);
                leftOrRightHand();
            }
        });
        walkWithCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      sendPhoneAMessage(MessagingValues.WALKWITHCUP);
               leftOrRightHand();
            }
        });
        somthing1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendPhoneAMessage(MessagingValues.TWISTCUP);
               //leftOrRightHand();
            }
        });
        somthing2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendPhoneAMessage(MessagingValues.UPANDDOWN);
                  }
        });
        somthing3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneAMessage(MessagingValues.POURWATER);
                leftOrRightHand();
            }
        });
    }

    public void workoutInfoView() {
        ballViewW.setVisibility(View.INVISIBLE);
        mTextView.setText("");
        hideAllButtons();
        mTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        pickUpCup.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
            }
        });
        holdUpCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        walkWithCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void howToHoldCupView(String message) {
        mTextView.setText("Hold It like This");
        hideAllButtons();
        progressBar.setVisibility(View.VISIBLE);
        ballViewW.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);

        pickUpCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        holdUpCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        walkWithCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    public void hideAllButtons() {
        for (Button b : AllButtons) {
            b.setVisibility(View.GONE);
        }
    }

    public void showAllButtons() {
        for (Button b : AllButtons) {
            b.setVisibility(View.VISIBLE);
        }
    }
}
