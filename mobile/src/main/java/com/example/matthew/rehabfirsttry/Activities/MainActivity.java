package com.example.matthew.rehabfirsttry.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.matthew.rehabfirsttry.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends Activity {
    GoogleApiClient mGoogleApiClient;
    TextView phoneText;
    Button phoneButton1, phoneButton2, phoneButton3;
    private Node mNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .build();
        mGoogleApiClient.connect();
        phoneButton1 = (Button) findViewById(R.id.PhoneButton1);
        phoneButton2 = (Button) findViewById(R.id.PhoneButton2);
        phoneButton3 = (Button) findViewById(R.id.PhoneButton3);
        phoneText = (TextView) findViewById(R.id.mobilephonetextfield);
        setPressStartView();
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

                if (dataRaw.equals("phonecanturnoffscreen")) {
                    phoneText.setBackgroundColor(Color.BLACK);
                    Intent intent = new Intent(getApplicationContext(), InsideCup.class);
                    startActivity(intent);
                }
            }

        }
    };

    private void tellWatchPuttingInCup() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null) {
                    byte[] bytes = ("puttingincup".getBytes());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                            "/rehabphonetowatch", bytes).await();
                } else {

                }
            }
        }).start();
    }

    public void setPressStartView() {
        phoneText.setText("Press Start When You Are Ready");
        phoneButton1.setText("Start");
        phoneButton2.setVisibility(View.INVISIBLE);
        phoneButton1.setVisibility(View.VISIBLE);
        phoneButton3.setVisibility(View.INVISIBLE);

        phoneButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChooseWorkoutView();
            }
        });
    }

    public void setChooseWorkoutView() {
        phoneButton2.setVisibility(View.VISIBLE);
        phoneButton1.setVisibility(View.VISIBLE);
//googog
        phoneButton3.setVisibility(View.VISIBLE);
        phoneText.setText("Choose WorkoutSession Type");
        phoneButton1.setText("Manual WorkoutSession");
        phoneButton2.setText("Full WorkoutSession");
        phoneButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPutMeInCupView();
            }
        });
        phoneButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPutMeInCupView();
            }
        });
        phoneButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), History.class);
                startActivity(intent);
            }
        });
    }

    public void setPutMeInCupView() {
        phoneText.setText("Put Me In The Cup Than Press OK On Watch");
        phoneButton1.setVisibility(View.INVISIBLE);
        phoneButton2.setVisibility(View.INVISIBLE);

        phoneButton3.setVisibility(View.INVISIBLE);
        tellWatchPuttingInCup();
    }

    public void setImInCupView() {
        phoneText.setText("I'm in the cup.");
        phoneButton1.setVisibility(View.INVISIBLE);
        phoneButton2.setVisibility(View.INVISIBLE);

        phoneButton3.setVisibility(View.INVISIBLE);

        phoneButton3.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPressStartView();
    }
}
