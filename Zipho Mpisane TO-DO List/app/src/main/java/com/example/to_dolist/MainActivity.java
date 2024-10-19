package com.example.to_dolist;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;
    private static final String PREFS_NAME = "todo_prefs";
    private static final String KEY_TASKS = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextTask = findViewById(R.id.editTextTask);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        ListView listViewTasks = findViewById(R.id.listViewTasks);

        // Initialize tasks list and adapter
        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ListView listView = (ListView) parent;
                listView.setItemChecked(position, isTaskCompleted(tasks.get(position)));
                view.setAlpha(isTaskCompleted(tasks.get(position)) ? 0.5f : 1f);  // Adjust transparency for completed tasks
                return view;
            }
        };

        listViewTasks.setAdapter(adapter);
        loadTasks();  // Load saved tasks

        // Add new task
        buttonAdd.setOnClickListener(v -> {
            String task = editTextTask.getText().toString();
            if (!TextUtils.isEmpty(task)) {
                tasks.add(task);
                adapter.notifyDataSetChanged();
                saveTasks();
                editTextTask.setText("");  // Clear input field
            }
        });

        // Toggle task completion on click
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            String task = tasks.get(position);
            tasks.set(position, isTaskCompleted(task) ? task.replace(" (Completed)", "") : task + " (Completed)");
            adapter.notifyDataSetChanged();
            saveTasks();
        });

        // Remove task on long press
        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            tasks.remove(position);
            adapter.notifyDataSetChanged();
            saveTasks();
            return true;
        });
    }

    // Check if the task is completed
    private boolean isTaskCompleted(String task) {
        return task.endsWith(" (Completed)");
    }

    // Save tasks to SharedPreferences
    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(tasks);  // Convert ArrayList to Set
        editor.putStringSet(KEY_TASKS, set);
        editor.apply();
    }

    // Load tasks from SharedPreferences
    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_TASKS, new HashSet<>());
        tasks.clear();
        tasks.addAll(set);  // Convert Set back to ArrayList
        adapter.notifyDataSetChanged();
    }
}

