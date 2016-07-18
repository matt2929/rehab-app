package com.example.matthew.rehabfirsttry.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.matthew.rehabfirsttry.R;
import com.example.matthew.rehabfirsttry.Utilities.Serialize;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutHistoricalData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class History extends Activity {
    private ListView lv;
    ArrayList<String> arrlist = new ArrayList<String>();
    Serialize serialize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        try {
            serialize = new Serialize(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ll="";
        for (WorkoutHistoricalData.WorkoutSession s : serialize.getUsers(getApplicationContext())) {
            ll+=
                    "Date: " + (s.get_Cal().get(Calendar.MONTH) + 1) + "/" + s.get_Cal().get(Calendar.DAY_OF_MONTH) + "/" + s.get_Cal().get(Calendar.YEAR)
                            + "\nTime: " + s.get_Cal().get(Calendar.HOUR_OF_DAY) + ":" + s.get_Cal().get(Calendar.MINUTE) + ":" + s.get_Cal().get(Calendar.SECOND)
                            + "\nInfo: " + s.getWorkoutInfo()
                            + "\nAccuracy: " + s.getAccuracy();
        }
        arrlist.add(ll);
        lv = (ListView) findViewById(R.id.HistoryView);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, arrlist));

    }
}
