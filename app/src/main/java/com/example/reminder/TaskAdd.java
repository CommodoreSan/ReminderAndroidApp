package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskAdd extends AppCompatActivity {
    String taskname, date;
    Timetype starttime,endtime;
    Tasks.Day day;
    int duration;
    boolean weekly, daily;
    Button addTaskButton,homeButton, setStart, setEnd;
    EditText tasknameEditText,setDate;
    CheckBox weeklyCheckBox, dailyCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        addTaskButton = (Button)findViewById(R.id.updateButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        tasknameEditText = (EditText)findViewById(R.id.taskNameEditText);
        setDate = (EditText)findViewById(R.id.pickdate);
        setStart = (Button)findViewById(R.id.pickstart);
        setEnd = (Button)findViewById(R.id.pickend);
        weeklyCheckBox = (CheckBox)findViewById(R.id.weeklyCheckBox);
        dailyCheckBox = (CheckBox)findViewById(R.id.dailyCheckBox);
        FirebaseApp.initializeApp(this);

        starttime = new Timetype(0,0);
        endtime = new Timetype(0, 0);

        setStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskAdd.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                starttime.setTime(hour,minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        setEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskAdd.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                endtime.setTime(hour,minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        try {
            setDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int dom = c.get(Calendar.DAY_OF_MONTH);
                    //day = Tasks.Day.values()[c.get(Calendar.DAY_OF_WEEK)];

                    DatePickerDialog datePickerDialog = new DatePickerDialog(TaskAdd.this,
                            new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar cal =  Calendar.getInstance();
                            cal.set(year,month,dayOfMonth);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String date = formatter.format(cal.getTime());
                            setDate.setText(date);
                        }
                    }, year, month, dom);
                    datePickerDialog.show();
                }
            });
        } catch(Exception e) {
            Log.e("Check", "Exception caught: ", e);
        }



        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = MainActivity.username;
                taskname = tasknameEditText.getText().toString();
                date = setDate.getText().toString();
                weekly = weeklyCheckBox.isChecked();
                daily = dailyCheckBox.isChecked();
                Tasks task = new Tasks(taskname, date, starttime, endtime, weekly, daily);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                try {
                    db.child("Users").child(username).child("Tasks").child(taskname).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Check", "inside onComplete");
                            tasknameEditText.setText("");
                            setDate.setText("");
                            weeklyCheckBox.setChecked(false);
                            dailyCheckBox.setChecked(false);
                            Toast.makeText(getApplicationContext(),"Storing task", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("Check", "exception caught", e);
                }
            }
        });
        try {
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(TaskAdd.this, Homepage.class);
                    startActivity(in);
                }
            });
        }
        catch (Exception e) {
            Log.e("test", String.valueOf(e));
        }

    }
}