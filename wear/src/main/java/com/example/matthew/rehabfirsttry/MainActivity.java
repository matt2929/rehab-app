package com.example.matthew.rehabfirsttry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
    private Node mNode;
    TextView dataField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                dataField = (TextView) stub.findViewById(R.id.weartext);
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)

                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                .build();
        mGoogleApiClient.connect();


    }
GoogleApiClient.OnConnectionFailedListener failedListener = new GoogleApiClient.OnConnectionFailedListener() {
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_LONG).show();
    }
};
    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new
            GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    dataField.setText("Connected");
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
                dataField.setText("(" + dataRaw + ")");
                if (dataRaw.equals("puttingincup")) {
                    sendPhoneConformation();
                    amInCupView();
                }
                if (dataRaw.equals("workoutactivity")) {

                    Intent intent = new Intent(getApplicationContext(), WorkoutInterface.class);
                    startActivity(intent);
                }
            }
        }
    };

    private void tellPhoneTurnOffScreen() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null) {
                    byte[] bytes = ("phonecanturnoffscreen".getBytes());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                            "/rehabwatchtophone", bytes).await();
                } else {

                }
            }
        }).start();
    }
    private void sendPhoneConformation() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null) {
                    byte[] bytes = ("conformation".getBytes());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                            "/rehabwatchtophone", bytes).await();
                } else {

                }
            }
        }).start();
    }
    public void lookAtPhoneView() {
        dataField.setText("See Phone");
    }

x
    public void amInCupView() {
        this.getCurrentFocus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkoutInterface.class);
                tellPhoneTurnOffScreen();
                startActivity(intent);
            }
        });
        dataField.setText("When phone is in cup touch screen");

        dataField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkoutInterface.class);
                tellPhoneTurnOffScreen();
                startActivity(intent);
            }
        });
    }

}
