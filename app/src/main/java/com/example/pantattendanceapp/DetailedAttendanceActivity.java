package com.example.pantattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedAttendanceActivity extends AppCompatActivity {

    private List<String> studentNameList,attendanceList;
    private ListView nameListView,AttendanceListView;
    private Attendance attendance;
    private Students students;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attendance);
        //nameListView = findViewById(R.id.name_list_view);
        AttendanceListView = findViewById(R.id.detailed_attendance_list_view);
        studentNameList = new ArrayList<>();
        attendanceList = new ArrayList<>();
        //studentNameList.add("A");
        //studentNameList.add("B");
        //studentNameList.add("C");
        studentDetails();

    }
    public void studentDetails(){
        FirebaseFirestore.getInstance().collection("courses")
                .document(getIntent().getStringExtra("course"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                students = document.toObject(Students.class);
                                studentNameList.addAll(students.getName());
                                //ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_selectable_list_item,studentNameList);
                                //adapter.notifyDataSetChanged();
                                //nameListView.setAdapter(adapter);
                                attendanceView();
                                Log.i("bhuvi",studentNameList.toString());
                            }
                            else {
                                Log.i("aaaaaa","eerror");
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("bhuvi",e.getMessage());
            }
        });
    }

    private void attendanceView() {
        FirebaseFirestore.getInstance().collection("attendance")
                .document(getIntent().getStringExtra("course"))
                .collection("date")
                .document(getIntent().getStringExtra("date"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                attendance = document.toObject(Attendance.class);
                                attendanceList.addAll(attendance.getAttendance());
                                Log.i("aaaa",attendanceList.toString());
                                List<HashMap<String, String>> fillMaps = new ArrayList<>();
                                for(int i = 0; i < studentNameList.toArray().length; i++){
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("rowid", "" + studentNameList.toArray()[i]);
                                    map.put("col_1", "" + attendanceList.toArray()[i]);
                                    fillMaps.add(map);
                                }
                                String[] from = new String[] {"rowid", "col_1"};
                                int[] to = new int[] { R.id.name, R.id.id};

                                SimpleAdapter adapter1 = new SimpleAdapter(getApplicationContext(),fillMaps,R.layout.list_item,from,to);
                                adapter1.notifyDataSetChanged();
                                AttendanceListView.setAdapter(adapter1);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailedAttendanceActivity.this, "Error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}