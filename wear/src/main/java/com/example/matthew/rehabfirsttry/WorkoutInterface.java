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
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;


public class WorkoutInterface extends Activity implements SensorEventListener {
    GoogleApiClient mGoogleApiClient;
    private Node mNode;
    private SensorManager mSensorManager;
    Button pickUpCup, holdUpCup, walkWithCup, somthingButton;
    private float gyroX = 0, gyroY = 0, gyroZ = 0;
    private Sensor mGyro;
    private TextView mTextView;
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
                somthingButton = (Button) stub.findViewById(R.id.somethingbutton);
                pickUpCup = (Button) stub.findViewById(R.id.wearwatchpickup);
                holdUpCup = (Button) stub.findViewById(R.id.wearholdup);
                walkWithCup = (Button) stub.findViewById(R.id.wearwalk);
                mTextView = (TextView) stub.findViewById(R.id.watchworkouttext);
                pickUpCup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendPhoneAMessage(MessagingValues.PICKUPCOUNT);
                        phoneWantsMyData = true;
                        howToHoldCupView("good");
                    }
                });
                holdUpCup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendPhoneAMessage(MessagingValues.PICKUPHOLD);
                        phoneWantsMyData = true;
                        howToHoldCupView("good");

                    }
                });
                walkWithCup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendPhoneAMessage(MessagingValues.WALKWITHCUP);
                        phoneWantsMyData = true;
                        howToHoldCupView("good");

                    }
                });
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
                    mTextView.setText("X: " + gyroX + "\nY: " + gyroY + "\nZ: " + gyroZ);
                    sendPhoneAMessage(MessagingValues.SENDGRAVITYDATA + "," + gyroX + "," + gyroY + "," + gyroZ);

                }
                phoneSendSensorDataCount = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void selectWorkoutView() {
        mTextView.setText("");
        pickUpCup.setVisibility(View.VISIBLE);
        holdUpCup.setVisibility(View.VISIBLE);
        walkWithCup.setVisibility(View.VISIBLE);
        somthingButton.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);

    }

    public void workoutInfoView() {
        mTextView.setText("");
        pickUpCup.setVisibility(View.GONE);
        holdUpCup.setVisibility(View.GONE);
        walkWithCup.setVisibility(View.GONE);
        somthingButton.setVisibility(View.GONE);
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

    public void howToHoldCupView(String message) {
        mTextView.setText("Hold It like This");
        pickUpCup.setVisibility(View.GONE);
        holdUpCup.setVisibility(View.GONE);
        walkWithCup.setVisibility(View.GONE);
        somthingButton.setVisibility(View.GONE);
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
}
