package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Register extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference reference;
    String username, occupation, email,d1,d2,d3,d4;
    EditText D1EditText, D2EditText, D3EditText, D4EditText, occupationEditText, usernameEditText, emailEditText;
    Button submitButton;
    SQLiteDatabase sqldb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        submitButton = (Button)findViewById(R.id.submitButton);
        D1EditText = (EditText)findViewById(R.id.D1EditText);
        D2EditText = (EditText)findViewById(R.id.D2EditText);
        D3EditText = (EditText)findViewById(R.id.D3EditText);
        D4EditText = (EditText)findViewById(R.id.D4EditText);
        occupationEditText = (EditText)findViewById(R.id.occupationEditText);
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        sqldb = openOrCreateDatabase("accounts",MODE_PRIVATE,null);
        sqldb.execSQL("CREATE TABLE IF NOT EXISTS allUsers (username VARCHAR PRIMARY KEY);");
        sqldb.execSQL("CREATE TABLE IF NOT EXISTS currentUser (username VARCHAR PRIMARY KEY);");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                occupation = occupationEditText.getText().toString();
                email = emailEditText.getText().toString();
                d1 = D1EditText.getText().toString();
                d2 = D2EditText.getText().toString();
                d3 = D3EditText.getText().toString();
                d4 = D4EditText.getText().toString();
                Users user = new Users(username,occupation,email,d1,d2,d3,d4);
                ContentValues values = new ContentValues();
                values.put("username",username);
                sqldb.insert("allUsers",null,values);
                sqldb.delete("currentUser",null,null);
                sqldb.insert("currentUser",null,values);

                db = FirebaseDatabase.getInstance();
                reference = db.getReference("Users");

                if(usernameEditText.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please fill in username", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        reference.child(username).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                D1EditText.setText("");
                                D2EditText.setText("");
                                D3EditText.setText("");
                                D4EditText.setText("");
                                occupationEditText.setText("");
                                usernameEditText.setText("");
                                emailEditText.setText("");
                                Log.d("Check", "registered");
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("Check", "Exception caught", e);
                    }
                    MainActivity.username = username;
                    Intent in = new Intent(Register.this, TaskAdd.class);
                    startActivity(in);
                }
            }
        });

    }
}