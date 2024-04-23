package com.example.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.w3c.dom.Text;

public class StatsActivity extends AppCompatActivity {
    TextView dist1T, dist2T, dist3T, dist4T;
    Button resetB;
    SQLiteDatabase sqldb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        dist1T = (TextView)findViewById(R.id.dist1);
        dist2T = (TextView)findViewById(R.id.dist2);
        dist3T = (TextView)findViewById(R.id.dist3);
        dist4T = (TextView)findViewById(R.id.dist4);
        resetB = (Button)findViewById(R.id.reset);

        setAllText();
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqldb.delete("distractions", null, null);
                ContentValues val = new ContentValues();
                val.put("username", MainActivity.username);
                val.put("d1", 0);
                val.put("d2", 0);
                val.put("d3", 0);
                val.put("d4", 0);
                sqldb.insert("distractions",null,val);
                setAllText();
            }
        });
    }
    void setAllText() {
        sqldb = openOrCreateDatabase("accounts",MODE_PRIVATE,null);
        int c1,c2,c3,c4;
        Cursor c = sqldb.query("distractions", null, "username=?", new String[]{MainActivity.username},null,null,null,null);
        c.moveToFirst();
        c1 = Integer.parseInt(c.getString(1));
        c2 = Integer.parseInt(c.getString(2));
        c3 = Integer.parseInt(c.getString(3));
        c4 = Integer.parseInt(c.getString(4));
        if(Homepage.d1.isEmpty())
            dist1T.setVisibility(View.GONE);
        else
            dist1T.setText(Homepage.d1+" : "+c1);
        if(Homepage.d2.isEmpty())
            dist2T.setVisibility(View.GONE);
        else
            dist2T.setText(Homepage.d2+" : "+c2);
        if(Homepage.d3.isEmpty())
            dist3T.setVisibility(View.GONE);
        else
            dist3T.setText(Homepage.d3+" : "+c3);
        if(Homepage.d4.isEmpty())
            dist4T.setVisibility(View.GONE);
        else
            dist4T.setText(Homepage.d4+" : "+c4);
        c.close();
    }
}