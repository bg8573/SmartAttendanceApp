package com.example.pantattendanceapp;
import java.util.List;

public class Attendance {
    public String courseName;
    public List<String> attendance;

    public  Attendance(){

    }

    public Attendance(String courseName, List<String> attendance) {
        this.courseName = courseName;
        this.attendance = attendance;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<String> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<String> attendance) {
        this.attendance = attendance;
    }
}
