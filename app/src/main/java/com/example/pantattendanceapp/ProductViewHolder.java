package com.example.pantattendanceapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantattendanceapp.Interface.ItemClickListener;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

public TextView txtStudentName, txtStudentID,txtStudentAttendance;
public ItemClickListener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        txtStudentName =itemView.findViewById(R.id.student_name_textView);
        txtStudentID =itemView.findViewById(R.id.student_id_textView);
        txtStudentAttendance =itemView.findViewById(R.id.student_attendance_textView);

    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener= listener;
    }
    @Override
    public void onClick(View view) {
     listener.onClick(view,getAdapterPosition(),false);
    }
}
