package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static String username;
    EditText usernameEd;
    Button submit,register;
    SQLiteDatabase sqldb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        sqldb = openOrCreateDatabase("accounts",MODE_PRIVATE,null);
        submit = (Button)findViewById(R.id.submitButton);
        usernameEd = (EditText)findViewById(R.id.usernameEditText);
        register = (Button)findViewById(R.id.reset);

        if(tableExists(sqldb, "currentUser")) {
            Cursor c = sqldb.rawQuery("SELECT * FROM currentUser", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                usernameEd.setText(c.getString(0));
            }
            c.close();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                username = usernameEd.getText().toString();
                if (tableExists(sqldb, "allUsers")) {
                    Cursor c = sqldb.rawQuery("SELECT * FROM allUsers", null);
                    c.moveToFirst();
                    int count = c.getCount();
                    for (int i = 0; i < count; i++, c.moveToNext()) {
                        String user = c.getString(0);
                        Log.d("Check", user);
                        if (user.equals(username)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        Toast.makeText(getApplicationContext(), "User not found\nPlease register", Toast.LENGTH_SHORT).show();
                        usernameEd.setText("");
                        c.close();
                    } else {
                        c.close();
                        ContentValues values = new ContentValues();
                        values.put("username", username);
                        sqldb.delete("currentUser", null, null);
                        sqldb.insert("currentUser", null, values);
                        Intent in = new Intent(MainActivity.this, Homepage.class);
                        startActivity(in);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User not found\nPlease register", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, Register.class);
                startActivity(in);
            }
        });
    }
    public static boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
            return false;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task reminder channel";
            String description = "Notification channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("taskRem", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static boolean checkValueExists(SQLiteDatabase db, String tableName, String columnName, String valueToCheck) {
        if(!tableExists(db, tableName))
            return false;
        String selection = columnName + " = ?";
        String[] selectionArgs = { valueToCheck };
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        boolean valueExists = cursor.getCount() > 0;
        cursor.close();
        return valueExists;
    }
}
