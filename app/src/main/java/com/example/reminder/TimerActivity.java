package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TimerActivity extends AppCompatActivity {

    private static final long COUNTDOWN_INTERVAL = 1000; // Countdown interval in milliseconds
    private static long COUNTDOWN_TIME; // Total countdown time in milliseconds
    private static final long BREAK_TIME = 60000 * 5;
    private TextView countdownTimer, taskSelector,breakTimer;
    Button startB;
    private long timeRemaining;
    private long timeElapsed = 0;
    private long timeRemainingB = BREAK_TIME;

    private Handler handler = new Handler();
    private Handler handlerB = new Handler();
    private boolean work_break = true;

    private Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (work_break) {
                if (timeRemaining > 0) {
                    timeRemaining -= COUNTDOWN_INTERVAL;
                    timeElapsed += COUNTDOWN_INTERVAL;
                    if(timeElapsed % (25*60000) == 0)
                        toggleWorkBreak();
                    updateCountdownText(countdownTimer, timeRemaining);
                    handler.postDelayed(this, COUNTDOWN_INTERVAL);
                } else {
                    countdownTimer.setText("Time's up!");
                }
            }
        }
    };
    private Runnable countdownRunnableB = new Runnable() {
        @Override
        public void run() {
            if(!work_break) {
                if (timeRemainingB > 0) {
                    timeRemainingB -= COUNTDOWN_INTERVAL;
                    updateCountdownText(breakTimer, timeRemainingB);
                    handlerB.postDelayed(this, COUNTDOWN_INTERVAL);
                } else {
                    breakTimer.setText("05:00");
                    toggleWorkBreak();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Intent in = getIntent();
        ArrayList<String> tasksList = in.getStringArrayListExtra("tasksList");

        countdownTimer = findViewById(R.id.countdown_timer);
        breakTimer = findViewById(R.id.break_timer);
        taskSelector = findViewById(R.id.taskSelect);
        startB = findViewById(R.id.start);



        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), taskSelector);
        popupMenu.getMenuInflater().inflate(R.menu.menu_tasks, popupMenu.getMenu());
        int taskCount = tasksList.size();
        for (int i = 0; i < taskCount; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, tasksList.get(i));
        }
        taskSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        taskSelector.setText(tasksList.get(itemId));
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("Tasks").child(tasksList.get(itemId)).child("durmin");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    int dur = snapshot.getValue(Integer.class);
                                    COUNTDOWN_TIME = 60000 * (long) dur;
                                    timeRemaining = COUNTDOWN_TIME;
                                    countdownTimer.setText(dur+":00");
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
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdownTimer();
            }
        });
    }

    private void startCountdownTimer() {
        handler.postDelayed(countdownRunnable, COUNTDOWN_INTERVAL);
    }
    private void startCountdownTimerB() {
        timeRemainingB = BREAK_TIME;
        handlerB.postDelayed(countdownRunnableB, COUNTDOWN_INTERVAL);
    }

    private void updateCountdownText(TextView textView, long timeRemaining) {
        // Convert milliseconds to seconds and format the time as minutes:seconds
        int minutes = (int) (timeRemaining / 1000) / 60;
        int seconds = (int) (timeRemaining / 1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        // Update the TextView with the formatted time
        textView.setText(timeString);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(countdownRunnable); // Stop the countdown when the activity is destroyed
        handlerB.removeCallbacks(countdownRunnableB);
    }
    private void toggleWorkBreak() {
        work_break = !work_break; // Toggle the pause state
        if (!work_break) {
            // If the timer is unpaused, resume countdown
            startCountdownTimerB();
        }
        if(work_break) {
            handler.postDelayed(countdownRunnable, COUNTDOWN_INTERVAL);
        }
    }
    private void switchWorkBreak() {
        toggleWorkBreak(); // Pause the timer
        // Do something else while the timer is paused



        // To resume the timer, call toggleTimerPause() again
    }



}