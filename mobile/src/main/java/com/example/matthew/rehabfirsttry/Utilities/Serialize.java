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

    public Serialize(Context c) throws IOException {
        dir = new File(c.getFilesDir() + "/serialisedRehab");

        dir.mkdirs();
        file = new File(dir, "BlockForTest2.txt");
        if (!file.exists()) {
            file.createNewFile();
            wasData = false;
        }
        context = c;
    }

    public void Save(Context context, String name,int shakelist[],int grade,boolean leftHand,String extraString) {
        //UserList ul = new UserList(a);
ArrayList<WorkoutHistoricalData.WorkoutSession> arrayWork = getUsers(context);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            // write something in the file
            //String workoutname, int[] shakelist, String workoutinfo, int grade, boolean leftHand
            arrayWork.add(0,new WorkoutHistoricalData.WorkoutSession(name,shakelist,name+extraString,grade,leftHand));
            oout.writeObject(arrayWork);
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
        ArrayList<WorkoutHistoricalData.WorkoutSession> ul = null;

        try {
            if (ois == null) {
                ul = new ArrayList<WorkoutHistoricalData.WorkoutSession>();

            } else {
                ul = (ArrayList<WorkoutHistoricalData.WorkoutSession>) ois.readObject();
            }
        } catch (ClassNotFoundException e) {
            ul = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
            e.printStackTrace();
        } catch (IOException e) {
            ul = new ArrayList<WorkoutHistoricalData.WorkoutSession>();
            e.printStackTrace();
        }
        if (ul != null) {
            return ul;
        } else {
            Toast.makeText(c, "null", Toast.LENGTH_LONG).show();
            return new ArrayList<WorkoutHistoricalData.WorkoutSession>();
        }

    }
    }
