package com.example.pantattendanceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    private List<String> courseName,dateList;
    private ListView listView,courseNameListView;
    private String cName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        listView = findViewById(R.id.list_view);
        dateList = new ArrayList<>();
        courseNameListView = findViewById(R.id.course_name_list_view);
        courseName = new ArrayList<>();
        courseDetails();
        //courseName.add("A1");
        //courseName.add("B1");
        //courseName.add("C1");

            }

    public void showDates(String courseName){
        FirebaseFirestore.getInstance().collection("attendance")
                .document(courseName)
                .collection("date")
                .orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dateList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                dateList.add(document.getString("date"));
                                Log.d("abc", document.getId() + " => " + document.getData());
                                Log.d("abc", document.getId() + " => " + document.getString("date"));
                                Log.i("aaa",dateList.get(0));
                            }
                            Log.i("bcd",dateList.toString());
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_selectable_list_item,dateList);
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("abc", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void courseDetails(){
        FirebaseFirestore.getInstance().collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            courseName.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                courseName.add(document.getId());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_selectable_list_item,courseName);
                            adapter.notifyDataSetChanged();
                            courseNameListView.setAdapter(adapter);
                            courseNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    showDates(courseName.get(i));
                                    cName = courseName.get(i);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Intent intent = new Intent(ViewAttendanceActivity.this,DetailedAttendanceActivity.class);
                                            intent.putExtra("date",dateList.get(i));
                                            intent.putExtra("course",cName);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(0);
                    }
                }).create().show();
    }
}