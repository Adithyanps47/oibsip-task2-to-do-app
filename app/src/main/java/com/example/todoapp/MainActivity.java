package com.example.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences; // Import added
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.DatabaseHelper;
import com.example.todoapp.R;
import com.example.todoapp.TaskAdapter;
import com.example.todoapp.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    DatabaseHelper db;
    ArrayList<TaskModel> taskList;
    TaskAdapter adapter;
    TextView tvEmpty; // Moved here to be accessible globally
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get User ID
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        // Security check
        if (currentUserId == -1) {
            finish();
            return;
        }

        db = new DatabaseHelper(this);
        taskList = new ArrayList<>();

        // Setup UI
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmptyState); // Initialize Empty View here

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Adapter
        adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteTask(position);
            }
        });
        recyclerView.setAdapter(adapter);

        // Load Data
        loadTasks();

        // Setup FAB
        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });
    }

    // --- HELPER METHODS ---

    private void loadTasks() {
        taskList.clear();
        Cursor cursor = db.getTasksForUser(currentUserId);

        // 1. Loop through cursor and add data
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("ID");
                int titleIndex = cursor.getColumnIndex("TITLE");
                int descIndex = cursor.getColumnIndex("DESCRIPTION");

                if (idIndex != -1 && titleIndex != -1 && descIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String desc = cursor.getString(descIndex);
                    taskList.add(new TaskModel(id, title, desc));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // 2. Check if list is empty AFTER loading data
        if (taskList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        // 3. Notify Adapter
        adapter.notifyDataSetChanged();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.etNewTaskTitle);
        final EditText etDesc = dialogView.findViewById(R.id.etNewTaskDesc);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString();
                String desc = etDesc.getText().toString();

                if (!title.isEmpty()) {
                    db.addTask(title, desc, currentUserId);
                    loadTasks();
                } else {
                    Toast.makeText(MainActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void deleteTask(int position) {
        TaskModel task = taskList.get(position);
        db.deleteTask(String.valueOf(task.getId()));
        taskList.remove(position);

        // We re-check empty state here manually or reload tasks
        if (taskList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
    }

    // --- MENU LOGIC (LOGOUT) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

            // --- NEW LOGOUT LOGIC (Clear Session) ---
            SharedPreferences prefs = getSharedPreferences("ToDoAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Clears the 'isLoggedIn' flag
            editor.apply();
            // ----------------------------------------

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}