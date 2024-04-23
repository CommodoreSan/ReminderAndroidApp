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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import  static com.example.reminder.MainActivity.username;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditTask extends AppCompatActivity {
    String date;
    Timetype starttime,endtime;
    Tasks.Day day;
    int duration;
    boolean weekly, daily;
    Button updateTaskButton, setStart, setEnd;
    EditText tasknameEditText,setDate;
    CheckBox weeklyCheckBox, dailyCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        updateTaskButton = (Button)findViewById(R.id.updateButton);
        tasknameEditText = (EditText)findViewById(R.id.taskNameEditText);
        setDate = (EditText)findViewById(R.id.pickdate);
        setStart = (Button)findViewById(R.id.pickstart);
        setEnd = (Button)findViewById(R.id.pickend);
        weeklyCheckBox = (CheckBox)findViewById(R.id.weeklyCheckBox);
        dailyCheckBox = (CheckBox)findViewById(R.id.dailyCheckBox);
        FirebaseApp.initializeApp(this);

        starttime = new Timetype(0,0);
        endtime = new Timetype(0, 0);
        String taskname = getIntent().getStringExtra("taskname");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("Tasks").child(taskname);
        tasknameEditText.setText(taskname);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Tasks task = snapshot.getValue(Tasks.class);
                    if(task != null) {
                        setDate.setText(task.getDate());
                        starttime = task.getStarttime();
                        endtime = task.getEndtime();
                        dailyCheckBox.setChecked(task.isDaily());
                        weeklyCheckBox.setChecked(task.isWeekly());
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Error: Could not load task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = starttime.hour;
                int minute = starttime.min;
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditTask.this,
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
                int hour = endtime.hour;
                int minute = endtime.min;
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditTask.this,
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
                    String date = setDate.getText().toString();
                    int year = Integer.parseInt(date.substring(0, 4));
                    int month = Integer.parseInt(date.substring(5,7));
                    int dom = Integer.parseInt(date.substring(8));

                    DatePickerDialog datePickerDialog = new DatePickerDialog(EditTask.this,
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

        updateTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtaskname = tasknameEditText.getText().toString();
                date = setDate.getText().toString();
                weekly = weeklyCheckBox.isChecked();
                daily = dailyCheckBox.isChecked();
                Tasks task = new Tasks(newtaskname, date, starttime, endtime, weekly, daily);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Tasks").child(newtaskname);
                DatabaseReference dbold = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("Tasks").child(taskname);
                try {
                    dbold.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Node deleted successfully
                                    db.setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Check", "inside onComplete");
                                            Toast.makeText(getApplicationContext(), "Task updated", Toast.LENGTH_SHORT).show();
                                            Intent in = new Intent(EditTask.this, Homepage.class);
                                            startActivity(in);
                                        }
                                    });
                                    Log.d("Check", "Node updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to delete node
                                    Toast.makeText(getApplicationContext(), "Failed to update task", Toast.LENGTH_SHORT).show();
                                    Log.e("Check", "Failed to delete node", e);
                                }
                            });

                }
                catch (Exception e) {
                    Log.e("Check", "exception caught", e);
                }
            }
        });
    }
}
