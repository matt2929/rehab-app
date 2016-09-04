package com.example.matthew.rehabfirsttry.Utilities;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by Matthew on 7/14/2016.
 */
public class SaveData {
    File _myFile;
    FileOutputStream outputStream;
    FileInputStream inputStream;
    Context _context;
    public static String _fileName;
    File _file;
    int count = 0;
    String _workoutName="";
SampleAverage sampleAverage = new SampleAverage();
    public SaveData(Context context,String workoutName) {
        FileOutputStream outputStream;
        FileInputStream inputStream;
        _workoutName=workoutName;
        _context = context;

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int milli = cal.get(Calendar.MILLISECOND);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        _fileName = "RehabInfo_"+(month + 1)+"-"+day+"-"+year+"_["+hour+"h~"+minute+"m~"+second+"s].csv";
    }
    public void saveData(String saveString) {

            try {

                String string = "";
                string += load();
                string += saveString;

                outputStream = _context.openFileOutput(_fileName, Context.MODE_WORLD_READABLE);
                outputStream.write(string.getBytes());
                outputStream.close();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), _fileName);
                PrintWriter csvWriter;

                if (!file.exists()) {
                    try {

                        file.createNewFile();
                        csvWriter = new PrintWriter(new FileWriter(file, true));
                        int last = 0;
                        int count = 0;
                        csvWriter.print(_workoutName+"\nTime,AccX,AccY,AccZ,AccT,GyroX,GyroY,GyroZ");
                        csvWriter.append('\n');
                        for (int i = 0; i < string.length(); i++) {
                            if (string.charAt(i) == ';') {
                                csvWriter.print(string.substring(last + 1, i));
                                csvWriter.append('\n');
                                last = i;
                            }


                        }
                        csvWriter.print(string.substring(last, string.length() - 1));
                        csvWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    file.delete();
                    try {
                        file.createNewFile();
                        csvWriter = new PrintWriter(new FileWriter(file, true));
                        csvWriter.print(_workoutName+"\nTime,AccX,AccY,AccZ,AccT");
                        csvWriter.append('\n');
                        int last = 0;

                        for (int i = 0; i < string.length(); i++) {
                            if (string.charAt(i) == ';') {
                                csvWriter.print(string.substring(last + 1, i));
                                csvWriter.append('\n');
                                last = i;
                            }
                        }
                        csvWriter.print(string.substring(last, string.length() - 1));
                        csvWriter.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

            }

    }


    public String load() {
        String line1 = "";
        try {
            inputStream = _context.openFileInput(_fileName);
            if (inputStream != null) {
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                try {
                    while ((line = buffreader.readLine()) != null) {
                        line1 += line;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

            String error = "";
            error = e.getMessage();
        }
        return line1;
    }
}