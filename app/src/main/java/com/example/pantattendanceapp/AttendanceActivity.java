package com.example.pantattendanceapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class AttendanceActivity extends AppCompatActivity {

    private TextView courseName,studentName,studentID;
    private Button present,absent,uploadBtn;
    private ArrayList<String> studentNameList,studentIDList;
    private String[] attendanceList;
    private String saveCurrentDate,saveCurrentTime;
    private int counter=0;
    private Students students;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        db =FirebaseFirestore.getInstance();
        courseName = findViewById(R.id.take_attendance_course_name);
        courseName.setText(getIntent().getStringExtra("course"));
        studentName = findViewById(R.id.student_name);
        studentID = findViewById(R.id.student_ID);
        present = findViewById(R.id.presentBtn);
        absent = findViewById(R.id.absentBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        studentNameList = new ArrayList<>();
        studentIDList = new ArrayList<>();
        studentDetails();
        Log.i("abcdef",studentNameList.toString());
        /*
        studentNameList.add("A");
        studentNameList.add("B");
        studentNameList.add("C");
        studentIDList.add("1");
        studentIDList.add("2");
        studentIDList.add("3");
        */

    }
    public void uploadAttendnace() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        HashMap<String, Object> attendnace = new HashMap<>();
        attendnace.put("course", courseName.getText());
        attendnace.put("date",saveCurrentDate+saveCurrentTime);
        attendnace.put("attendance", Arrays.asList(attendanceList));
        Log.i("abc",attendnace.toString());
        db.collection("attendance")
                .document(courseName.getText().toString().trim())
                .collection("date")
                .document(saveCurrentDate+saveCurrentTime)
                .set(attendnace)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AttendanceActivity.this, "Attendance Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void studentDetails(){
      db.collection("courses")
              .document(courseName.getText().toString().trim())
              .get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                      if (task.isSuccessful()){
                          DocumentSnapshot document = task.getResult();
                          studentNameList.clear();
                          studentIDList.clear();
                          if (document.exists()){
                              students = document.toObject(Students.class);
                              studentNameList.addAll(students.getName());
                              studentIDList.addAll(students.getId());
                              Log.i("bhuvi",studentNameList.toString());
                              attendance();
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

    private void attendance() {
        counter=0;
        attendanceList = new String[studentNameList.size()];
        if (counter<studentNameList.size()) {
            studentName.setText(studentNameList.get(counter));
            studentID.setText(studentIDList.get(counter));
        }else{
            studentName.setText("No more student");
            studentID.setText("No more student");
        }
        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //presentList.add(studentNameList.get(counter));
                attendanceList[counter] = "Present";
                if (counter < studentNameList.size()-1) {
                    counter++;
                    studentName.setText(studentNameList.get(counter));
                    studentID.setText(studentIDList.get(counter));
                } else {
                    Toast.makeText(AttendanceActivity.this, "No more students in this course", Toast.LENGTH_LONG).show();
                }
            }
        });
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //absentList.(studentNameList.get(counter));
                attendanceList[counter] = "Absent";
                if (counter < studentNameList.size()-1) {
                    counter++;
                    studentName.setText(studentNameList.get(counter));
                    studentID.setText(studentIDList.get(counter));
                } else {
                    Toast.makeText(AttendanceActivity.this, "No more students in this course", Toast.LENGTH_LONG).show();
                }
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAttendnace();
            }
        });
    }
}