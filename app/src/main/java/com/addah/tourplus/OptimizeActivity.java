package com.addah.tourplus;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by choxmi on 6/24/17.
 */

public class OptimizeActivity extends AppCompatActivity implements AsyncResponse{

    ArrayList<LocationDetails> ld;
    ArrayList<Integer> gl;
    Spinner spinner;
    int arrrayPosi,timeAvail,notiTimes;
    Button proceedBtn,analyzeBtn;
    EditText timeLimit;
    OptimizeConnector connector;
    ArrayList<Integer> timeGaps;
    List<String> items;
    ArrayAdapter<String> adapter;
    ProgressDialog pd;
    ListView rec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimize);
        proceedBtn = (Button)findViewById(R.id.optimusBtn);
        analyzeBtn = (Button)findViewById(R.id.anaBtn);
        timeLimit = (EditText)findViewById(R.id.timeSpnd);
        final Intent intent = getIntent();
        ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("path");
        gl = (ArrayList<Integer>)intent.getIntegerArrayListExtra("Gaps");

        spinner = (Spinner)findViewById(R.id.currLoc);
        items = new ArrayList<>();
        timeGaps = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,items);
        rec = (ListView) findViewById(R.id.suggestionList);
        rec.setAdapter(adapter);

        List<String> list = new ArrayList<>();
        for(LocationDetails lds : ld){
            list.add(lds.getLoc_name());
        }

        for(int i=0;i<gl.size()-1;i++){
            Log.e("Scheduling",""+gl.get(i)+" Location "+list.get(i));
            scheduleNotifications(gl.get(i),list.get(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrrayPosi = position;
                Log.e("posi",""+arrrayPosi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = (arrrayPosi+1);i<ld.size();i++){
                    try {
                        connector = new OptimizeConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=" + ld.get(i-1).getLat() + "%2C" + ld.get(i-1).getLng() + "&destination=" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw");
                        connector.delegate = OptimizeActivity.this;
                        connector.execute();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        analyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleNotification(OptimizeActivity.this,3,12);
                items.clear();
                timeAvail = Integer.parseInt(timeLimit.getText().toString())*60;
                int timeDirect=0,fullTime = 0;
                for(int i=0;i<timeGaps.size();i++){
                    timeDirect += timeGaps.get(i);
                    fullTime += timeDirect+ld.get(arrrayPosi+i).getWaiting_time();
                }
                Log.e("direct",""+timeDirect);
                Log.e("time avail",""+timeAvail);
                items.add("Time to Complete journey as planned : "+(fullTime/60)+" min");
                items.add("Time to go Directly to destination : "+(timeDirect/60)+" min");
                adapter.notifyDataSetChanged();
                if(timeAvail>=fullTime){
                    Toast.makeText(OptimizeActivity.this,"Still have time",Toast.LENGTH_SHORT).show();
                }else if(timeAvail>=timeDirect){
                    Toast.makeText(OptimizeActivity.this,"Don't stop. Go directly",Toast.LENGTH_SHORT).show();
                }else{
                    int timeDir=0;
                    suggest: {
                        for(int i=0;i<timeGaps.size();i++){
                            timeDir += timeGaps.get(i);
                            if(timeDir>timeAvail){
                                Toast.makeText(OptimizeActivity.this,"You have to stay @ "+ld.get(arrrayPosi+i).getLoc_name(),Toast.LENGTH_SHORT).show();
                                items.add("You have to stay @ "+ld.get(arrrayPosi+i).getLoc_name());
                                adapter.notifyDataSetChanged();
                                break suggest;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void processFinish(String response) {
        int time = Integer.parseInt(response);
        timeGaps.add(time);
        Log.e("Res",response);
        if(timeGaps.size()==(ld.size()-arrrayPosi)){
            Toast.makeText(OptimizeActivity.this,"Retrieving complete",Toast.LENGTH_LONG).show();
        }
        notiTimes += time;
    }

    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Title")
                .setContentText("Content")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_dialog_close_dark)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_dialog_close_dark)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, OptimizeActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);
        Log.e("Noti","Sending");
        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra("NotiID", notificationId);
        notificationIntent.putExtra("NotiPub", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void showNotification(String place){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("Notification!")
                .setContentText("You have only 5 mins left @ "+place)
                .setAutoCancel(true);
        Intent intent = new Intent(this, OptimizeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void scheduleNotifications(int delay,String place) {
        final String loc = place;
        final Runnable beeper = new Runnable() {
            public void run() {
                showNotification(loc);
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.schedule(beeper,delay,SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, 60 * 60, SECONDS);
    }
}