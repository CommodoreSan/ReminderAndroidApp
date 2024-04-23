package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;


public class Homepage extends AppCompatActivity {

    ListView taskList;
    FloatingActionButton addTask;
    ImageButton prevDay, nextDay;
    TextView heading;
    LocalDate systemdate = LocalDate.now();
    LocalDate currentdate = LocalDate.now();
    Button stats, timer;
    static SQLiteDatabase sqldb;
    static String d1,d2,d3,d4;
    static int c1,c2,c3,c4;
    ArrayList<String> tasksForTimer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        taskList = (ListView)findViewById(R.id.taskList);
        addTask = (FloatingActionButton)findViewById(R.id.addTask);
        prevDay = (ImageButton)findViewById(R.id.prevDay);
        nextDay = (ImageButton)findViewById(R.id.nextDay);
        heading = (TextView)findViewById(R.id.heading);
        stats = (Button)findViewById(R.id.statsButton);
        timer = (Button)findViewById(R.id.timerButton);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        loadPageByDate(systemdate.format(formatter));
        initializeDist();
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Homepage.this, TaskAdd.class);
                startActivity(in);
            }
        });
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Homepage.this, StatsActivity.class);
                startActivity(in);
            }
        });
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Homepage.this, TimerActivity.class);
                in.putStringArrayListExtra("tasksList",tasksForTimer);
                startActivity(in);
            }
        });

        prevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                currentdate = currentdate.minusDays(1);
                loadPageByDate(currentdate.format(formatter));
            }
        });
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                currentdate = currentdate.plusDays(1);
                loadPageByDate(currentdate.format(formatter));
            }
        });
        heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Homepage.this,"Heading clicked",Toast.LENGTH_SHORT).show();

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dom = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Homepage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal =  Calendar.getInstance();
                                cal.set(year,month,dayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String date = formatter.format(cal.getTime());
                                currentdate = LocalDate.parse(date);
                                Log.d("Check",currentdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                loadPageByDate(date);
                            }
                        }, year, month, dom);
                datePickerDialog.show();
            }
        });
    }
    void loadPageByDate(String date) {
        String username = MainActivity.username;
        try {
            ArrayList<String> tasks = new ArrayList<>();
            ArrayList<String> timeRange = new ArrayList<>();
            CustomAdapter adapter = new CustomAdapter(this, tasks, timeRange);
            taskList.setAdapter(adapter);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String todayDate = systemdate.format(formatter);
            String currentDate = currentdate.format(formatter);

            if(todayDate.equals(currentDate))
                heading.setText("Tasks for today");
            else {
                String dateDisplay = date.substring(8) + "/" + date.substring(5,7);
                heading.setText("Tasks for " + dateDisplay);
            }

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Tasks");

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tasks.clear();
                    timeRange.clear();
                    for(DataSnapshot snap : snapshot.getChildren()) {
                        Tasks task = snap.getValue(Tasks.class);
                        assert task != null;
                        if(isValid(task, date)) { //checks if task should be done today
                            String item = task.getTaskname();
                            String times = task.getStarttime().getTime() + " - " + task.getEndtime().getTime();
                            tasks.add(item);
                            timeRange.add(times);
                            if(todayDate.equals(currentDate)) {
                                scheduleNotification(task);
                                if (task.getType() == 2)
                                    tasksForTimer.add(task.getTaskname());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Check", "Error", error.toException());
                }
            });

        }
        catch (Exception e){
            Log.e("Check", "Exception found "+username, e);
        }
    }

    boolean isValid(Tasks task, String date) {
        Log.d("notif","today: "+date);
        Log.d("notif",task.getTaskname()+" : "+task.getDate());
        if(task.isDaily()) //daily task
            return true;
            //weekly task with same day as today
        else if(task.isWeekly() && task.getDay() == Tasks.getDayOfWeek(date))
            return true;
        else if(task.getDate().equals(date)) //same date as today
            return true;
        else
            return false;
    }
    private void scheduleNotification(@NonNull Tasks task) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, task.getStarttime().getHour());
        cal.set(Calendar.MINUTE, task.getStarttime().getMin());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTimeInMillis = cal.getTimeInMillis();
        long currentTimeInMillis = System.currentTimeMillis();
        long delayInMillis = startTimeInMillis - currentTimeInMillis;
        Log.d("notif",delayInMillis+" "+task.getStarttime().getHour());
        if(delayInMillis < 0)
            return;
        // Schedule notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("title", task.getTaskname());
        notificationIntent.putExtra("message", task.getTaskname()+" starts at "+task.getStarttime().getTime());
        int requestCode = task.getStarttime().hour*100 + task.getStarttime().min;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_MUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pendingIntent);
        Log.d("notif", "scheduling..."+task.getTaskname());
    }
    void initializeDist() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("d1");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    d1 = snapshot.getValue(String.class);
                }
                else {
                    Log.d("Check", "d1 does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Check", "Error: " + error.getMessage());
            }
        });
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("d2");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    d2 = snapshot.getValue(String.class);
                }
                else {
                    Log.d("Check", "d2 does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Check", "Error: " + error.getMessage());
            }
        });
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("d3");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    d3 = snapshot.getValue(String.class);
                }
                else {
                    Log.d("Check", "d3 does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Check", "Error: " + error.getMessage());
            }
        });
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("d4");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    d4 = snapshot.getValue(String.class);
                }
                else {
                    Log.d("Check", "d3 does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Check", "Error: " + error.getMessage());
            }
        });

        sqldb = openOrCreateDatabase("accounts",MODE_PRIVATE,null);
        sqldb.execSQL("CREATE TABLE IF NOT EXISTS distractions (username VARCHAR PRIMARY KEY," +
                "d1 NUMBER,d2 NUMBER, d3 NUMBER, d4 NUMBER);");
        if(!MainActivity.checkValueExists(sqldb,"distractions","username",MainActivity.username)) {
            ContentValues val = new ContentValues();
            val.put("username", MainActivity.username);
            val.put("d1", 0);
            val.put("d2", 0);
            val.put("d3", 0);
            val.put("d4", 0);
            sqldb.insert("distractions",null,val);
        }
    }
    static void incrementDist(int d) {
        Cursor c = sqldb.query("distractions", null, "username=?", new String[]{MainActivity.username},null,null,null,null);
        c.moveToFirst();
        c1 = Integer.parseInt(c.getString(1));
        c2 = Integer.parseInt(c.getString(2));
        c3 = Integer.parseInt(c.getString(3));
        c4 = Integer.parseInt(c.getString(4));
        c.close();
        switch(d) {
            case 1: c1++; break;
            case 2: c2++; break;
            case 3: c3++; break;
            case 4: c4++; break;
        }
        ContentValues val = new ContentValues();
        val.put("username", MainActivity.username);
        val.put("d1", c1);
        val.put("d2", c2);
        val.put("d3", c3);
        val.put("d4", c4);
        sqldb.update("distractions",val,"username=?",new String[]{MainActivity.username});
    }
}