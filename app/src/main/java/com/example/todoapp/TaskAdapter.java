package com.example.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    Context context;
    ArrayList<TaskModel> taskList;
    OnItemClickListener listener;

    // Interface to communicate with Main Activity
    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public TaskAdapter(Context context, ArrayList<TaskModel> taskList, OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());

        // Handle Delete Button Click
        holder.btnDelete.setOnClickListener(v -> {
            listener.onDeleteClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}