package com.example.pantattendanceapp;

import java.util.List;

public class Students {
    public List<String> name, id;

    public Students() {
    }

    public Students(List<String> name, List<String> id) {
        this.name = name;
        this.id = id;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }
}
