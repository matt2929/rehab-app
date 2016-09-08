package com.example.matthew.rehabfirsttry.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.matthew.rehabfirsttry.R;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutHistoricalData;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryGraph extends AppCompatActivity {

    GraphDataView graphDataView;
    ListView listView;
    ArrayList<String> stringArrlist = new ArrayList<String>();
    ArrayList<Float> intArrlist = new ArrayList<Float>();
    ArrayList<WorkoutHistoricalData.WorkoutSession> sessions = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
    ArrayList<WorkoutHistoricalData.WorkoutSession> temp = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
    TextView textView;
    CheckBox checkBox;
    Button timeDurationButton;
    Button shakeTypeButton;
    int biggest = Integer.MIN_VALUE;
    int smallest = Integer.MAX_VALUE;
    int currentDuration = 0;
    int currentShakeType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        for (int i = 0; i < HistoryList.AllWorkOuts.size(); i++) {
            if (HistoryList.AllWorkOuts.get(i).getWorkoutName().equals(HistoryList.workoutName)) {
                sessions.add(HistoryList.AllWorkOuts.get(i));
            }
        }
        temp = new ArrayList<>(sessions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history_graph);
        graphDataView = (GraphDataView) findViewById(R.id.GraphData);
        listView = (ListView) findViewById(R.id.datalist);
        textView = (TextView) findViewById(R.id.pointInfo);
        checkBox = (CheckBox) findViewById(R.id.line);
        timeDurationButton = (Button) findViewById(R.id.setDataDuration);
        shakeTypeButton = (Button) findViewById(R.id.setDataType);
        setupViews();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                graphDataView.dotOrLine(b);
            }
        });
        timeDurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDuration();
            }
        });
        shakeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeShake();
            }
        });
        graphDataView.setValues(intArrlist);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, stringArrlist));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                graphDataView.selectHighlightedData((intArrlist.size() - i) - 1);
                textView.setText("Val:" + intArrlist.get(i) + " Pos: " + ((intArrlist.size() - i) - 1));
            }
        });
    }

    public void setupViews() {
        if (sessions.size() == 0) {
            textView.setText("There is no data within these parameters.");
        } else {
            textView.setText("");
            graphDataView.selectHighlightedData(-1);
            //
            stringArrlist.clear();
            intArrlist.clear();
            float start = 0;
            int biggest = Integer.MIN_VALUE;
            int smallest = Integer.MAX_VALUE;
            for (int i = 0; i < sessions.size(); i++) {
                WorkoutHistoricalData.WorkoutSession s = sessions.get(i);
                if (currentShakeType == 0) {
                    start = (float) sessions.get(i).getShakeList()[3];
                } else if (currentShakeType == 1) {
                    start = (float) sessions.get(i).getShakeList()[0];
                } else if (currentShakeType == 2) {
                    start = (float) sessions.get(i).getShakeList()[1];
                } else if (currentShakeType == 3) {
                    start = (float) sessions.get(i).getShakeList()[2];
                }

                stringArrlist.add((s.get_Cal().get(Calendar.MONTH) + 1) + "/" + s.get_Cal().get(Calendar.DAY_OF_MONTH)+ "/" + s.get_Cal().get(Calendar.YEAR)
                        + "\nTime: " + s.get_Cal().get(Calendar.HOUR_OF_DAY) + ":" + s.get_Cal().get(Calendar.MINUTE) + ":" + s.get_Cal().get(Calendar.SECOND) + "\nShakes: " + start + "\nHand: " + s.isLeftHand());
                intArrlist.add((float) start);
                if (start + .5 > biggest) {
                    biggest = (int) start;
                }
                if (start - .5 < smallest) {
                    smallest = (int) start;
                }
            }
            graphDataView.setValues(intArrlist);
            graphDataView.invalidate();
            listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, stringArrlist) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);

                    // Generate ListView Item using TextView
                    return view;
                }
            });
            listView.invalidate();
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            listView.invalidate();
        }
    }

    public void changeDuration() {
        sessions = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
        switch (currentDuration) {
            case 0:
                for (int i = 0; i < temp.size(); i++) {
                    WorkoutHistoricalData.WorkoutSession ws = temp.get(i);
                    Calendar cal = Calendar.getInstance();
                    if (ws.get_Cal().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        sessions.add(ws);
                    }
                }
                timeDurationButton.setText("Duration\nYear");
                currentDuration = 1;
                break;
            case 1:
                for (int i = 0; i < temp.size(); i++) {
                    WorkoutHistoricalData.WorkoutSession ws = temp.get(i);
                    Calendar cal = Calendar.getInstance();
                    if (ws.get_Cal().get(Calendar.MONTH) == cal.get(Calendar.MONTH) && ws.get_Cal().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        sessions.add(ws);
                    }
                }
                timeDurationButton.setText("Duration\nMonth");
                currentDuration = 2;
                break;
            case 2:
                for (int i = 0; i < temp.size(); i++) {
                    WorkoutHistoricalData.WorkoutSession ws = temp.get(i);
                    Calendar cal = Calendar.getInstance();

                    if (ws.get_Cal().get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && ws.get_Cal().get(Calendar.MONTH) == cal.get(Calendar.MONTH) && ws.get_Cal().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        sessions.add(ws);
                    }

                }
                timeDurationButton.setText("Duration\nToday");
                currentDuration = 3;
                break;
            case 3:
                timeDurationButton.setText("Duration\nAll");
                currentDuration = 0;
                sessions = new ArrayList<WorkoutHistoricalData.WorkoutSession>(temp);
                break;
        }
        setupViews();
    }

    public void changeShake() {
        switch (currentShakeType) {
            case 0:
                shakeTypeButton.setText("Shake Type\nSmall");
                currentShakeType = 1;
                break;
            case 1:
                shakeTypeButton.setText("Shake Type\nMed");
                currentShakeType = 2;
                break;
            case 2:
                shakeTypeButton.setText("Shake Type\nLarge");
                currentShakeType = 3;
                break;
            case 3:
                shakeTypeButton.setText("Shake Type\nAll");
                currentShakeType = 0;
                break;
        }
        setupViews();
    }
}
