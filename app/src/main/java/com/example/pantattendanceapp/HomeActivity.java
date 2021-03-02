package com.example.pantattendanceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private TextView userNameTextView;
    private ListView courseListView,dateListView;
    private List<String> courseName,dateList;
    private String cName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        courseListView = findViewById(R.id.course_list_view);
        dateListView = findViewById(R.id.student_list_view);
        dateList = new ArrayList<>();
        courseName = new ArrayList<>();
        courseDetails();
        drawerLayout = findViewById(R.id.home_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.home_navigation_view);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.home_nav_add_student :
                        Intent addStudentIntent = new Intent(HomeActivity.this,AddStudentActivity.class);
                        startActivity(addStudentIntent);
                        return true;

                    case R.id.home_nav_logout :
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Logout Successful",Toast.LENGTH_SHORT).show();
                        Intent logoutIntent = new Intent(HomeActivity.this,MainActivity.class);
                        startActivity(logoutIntent);
                        return true;

                    default:
                        return true;
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                            dateListView.setAdapter(adapter);
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
                            courseListView.setAdapter(adapter);
                            courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    showDates(courseName.get(i));
                                    cName = courseName.get(i);
                                    dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Intent intent = new Intent(HomeActivity.this,DetailedAttendanceActivity.class);
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
                        Intent intent = new Intent((Intent.ACTION_MAIN));
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).create().show();
    }
}