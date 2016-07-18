package com.example.matthew.rehabfirsttry.Utilities;

import android.content.Context;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Matthew on 6/2/2016.
 */
public class Serialize implements Serializable {

    Context context;
    File dir;
    File file;
    public static boolean wasData = true;
    private WorkoutHistoricalData workoutHistoricalData;

    public Serialize(Context c) throws IOException {
        dir = new File(c.getFilesDir() + "/flappyBreath");
        dir.mkdirs();
        file = new File(dir, "rehabdata.txt");

        if (!file.exists()) {
            file.createNewFile();
            wasData = false;
        }
        context = c;
        workoutHistoricalData = new WorkoutHistoricalData(getUsers(c));

    }

    public void Save(Context context, String infoAboutActivity, double accuracy) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            // write something in the file
            workoutHistoricalData = new WorkoutHistoricalData(getUsers(context));
            workoutHistoricalData.addWorkout(new WorkoutHistoricalData.WorkoutSession(accuracy, infoAboutActivity));
            oout.writeObject(workoutHistoricalData);
            oout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<WorkoutHistoricalData.WorkoutSession> getUsers(Context c) {

        ObjectInputStream ois =
                null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // UserList ul = null;
        WorkoutHistoricalData ul = null;

        try {
            if (ois == null) {
                ul = new WorkoutHistoricalData(new ArrayList<WorkoutHistoricalData.WorkoutSession>());

            } else {
                ul = (WorkoutHistoricalData) ois.readObject();
            }
        } catch (ClassNotFoundException e) {
            ul = new WorkoutHistoricalData(new ArrayList<WorkoutHistoricalData.WorkoutSession>());
            e.printStackTrace();
        } catch (IOException e) {
            ul = new WorkoutHistoricalData(new ArrayList<WorkoutHistoricalData.WorkoutSession>());
            e.printStackTrace();
        }
        if (ul != null) {
            return ul.get_history();

        } else {
            Toast.makeText(c, "null", Toast.LENGTH_LONG).show();
            return new ArrayList<WorkoutHistoricalData.WorkoutSession>();
        }


    }


}
