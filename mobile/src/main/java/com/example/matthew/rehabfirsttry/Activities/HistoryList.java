package com.example.matthew.rehabfirsttry.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.matthew.rehabfirsttry.R;
import com.example.matthew.rehabfirsttry.Utilities.Serialize;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutHistoricalData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryList extends Activity {
    private ListView lv;
    ArrayList<String> WorkoutStrings = new ArrayList<String>();
    static String workoutName = "";
    static ArrayList<WorkoutHistoricalData.WorkoutSession> AllWorkOuts = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
    Serialize serialize;
    static int workoutPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        try {
            serialize = new Serialize(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorkoutStrings.add("HistoryList\n");
        AllWorkOuts = serialize.getUsers(getApplicationContext());
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{15,25,35,61},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{13,23,32,61},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{12,22,32,62},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{12,22,32,67},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{10,20,30,80},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{1,2,3,100},"",50,true));

        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{15,25,35,110},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{13,23,32,115},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{12,22,32,120},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{12,22,32,150},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{10,20,30,175},"",50,true));
        AllWorkOuts.add(new WorkoutHistoricalData.WorkoutSession("Pickup",new int[]{1,2,3,200},"",50,true));

        for (WorkoutHistoricalData.WorkoutSession s : AllWorkOuts) {
            String hand = "";
            if (s.isLeftHand()) {
                hand = "LEFT";
            } else {
                hand = "RIGHT";
            }
            WorkoutStrings.add(
                    "Date: " + (s.get_Cal().get(Calendar.MONTH) + 1) + "/" + s.get_Cal().get(Calendar.DAY_OF_MONTH) + "/" + s.get_Cal().get(Calendar.YEAR)
                            + "\nTime: " + s.get_Cal().get(Calendar.HOUR_OF_DAY) + ":" + s.get_Cal().get(Calendar.MINUTE) + ":" + s.get_Cal().get(Calendar.SECOND)
                            + "\nWorkout Name: " + s.getWorkoutName()
                            + "\nHAND: <" + hand + ">"
                            + "\nGRADE: " + s.getGrade() + "%"
                            + "\nShakes---\nS: " + s.getShakeList()[0]
                            + "\nM: " + s.getShakeList()[1]
                            + "\nL: " + s.getShakeList()[2]
                            + "\nTotal: " + s.getShakeList()[3]);
        }

        lv = (ListView) findViewById(R.id.HistoryView);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, WorkoutStrings));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // AllWorkOuts = serialize.getUsers(getApplicationContext());
                workoutPosition = i - 1;
                workoutName = AllWorkOuts.get(workoutPosition).getWorkoutName();
                Intent intent = new Intent(getApplicationContext(), HistoryGraph.class);
                startActivity(intent);
            }
        });
    }
}
