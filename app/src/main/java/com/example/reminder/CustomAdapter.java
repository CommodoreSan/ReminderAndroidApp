package com.example.reminder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    private ArrayList<String> items;
    private ArrayList<String> timeRange;
    private Context mContext;
    //String d1,d2,d3,d4;

    public CustomAdapter(Context context, ArrayList<String> items, ArrayList<String> timeRange) {
        super(context, 0, items);
        mContext = context.getApplicationContext();
        this.items = items;
        this.timeRange = timeRange;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView taskName = convertView.findViewById(R.id.taskname);
        TextView time = convertView.findViewById(R.id.time);
        Button deleteTask = convertView.findViewById(R.id.deletetask);
        Button editTask = convertView.findViewById(R.id.edittask);
        Button distraction = convertView.findViewById(R.id.distraction);


        //error here


        taskName.setText(items.get(position));
        time.setText(timeRange.get(position));
        String taskname = taskName.getText().toString();
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click

                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.username).child("Tasks").child(taskname);
                db.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Node deleted successfully
                                Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "Node deleted successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete node
                                Log.w("TAG", "Failed to delete node", e);
                            }
                        });
            }
        });

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext,EditTask.class);
                in.putExtra("taskname", taskname);
                mContext.startActivity(in);
            }
        });

        PopupMenu popupMenu = new PopupMenu(getContext(), distraction);
        popupMenu.getMenuInflater().inflate(R.menu.distraction_menu, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();
        MenuItem D1 = menu.findItem(R.id.d1);
        MenuItem D2 = menu.findItem(R.id.d2);
        MenuItem D3 = menu.findItem(R.id.d3);
        MenuItem D4 = menu.findItem(R.id.d4);

        distraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                D1.setTitle(Homepage.d1);
                D2.setTitle(Homepage.d2);
                D3.setTitle(Homepage.d3);
                D4.setTitle(Homepage.d4);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if(menuItem.getItemId() == R.id.d1)
                            Homepage.incrementDist(1);
                        else if(menuItem.getItemId() == R.id.d2)
                            Homepage.incrementDist(2);
                        else if(menuItem.getItemId() == R.id.d3)
                            Homepage.incrementDist(3);
                        else if(menuItem.getItemId() == R.id.d4)
                            Homepage.incrementDist(4);

                        Toast.makeText(getContext(), "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        return convertView;
    }
}

