package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // "globale" variabler, som vi gerne vil have adgang til overalt i klassen
    ArrayList<task_model> currentTasks;
    task_adapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Opsætning af TabLayout og RecyclerView
        tabLayout = findViewById(R.id.tabLayout);

        currentTasks = new ArrayList<>(storage_manager.loadTasks(getApplicationContext()));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new task_adapter(this, currentTasks, tabLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tabLayout.selectTab(tabLayout.getTabAt(1));

        updateRecyclerView();

        Button openDialog = findViewById(R.id.addBtn);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateRecyclerView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.add_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText taskNameInput = dialog.findViewById(R.id.taskNameInput);
                Spinner dialogSpinner = dialog.findViewById(R.id.dialogSpinner);
                ImageButton datePickerBtn = dialog.findViewById(R.id.datePicker);
                EditText dateInputText = dialog.findViewById(R.id.dateTextInput);
                Button submitButton = dialog.findViewById(R.id.submitBtn);

                dialog.show();

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (taskNameInput.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Task name can't be empty!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String dateStr = dateInputText.getText().toString().isEmpty() ? "Not Set" : dateInputText.getText().toString();

                        task_model task = new task_model(
                                taskNameInput.getText().toString(),
                                dateStr,
                                dialogSpinner.getSelectedItem().toString(),
                                false

                        );

                        storage_manager.saveTask(getApplicationContext(), task);
                        updateRecyclerView();

                        Toast.makeText(getApplicationContext(), taskNameInput.getText().toString() + " was added!", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

                datePickerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker(dialog);
                    }
                });

            }
        });
    }

    // Filtrering af de tilgængelige TabItems
    private void updateRecyclerView() {
        currentTasks = new ArrayList<>(storage_manager.loadTasks(getApplicationContext()));
        switch (tabLayout.getSelectedTabPosition()) {
            case 1:
                currentTasks.removeIf(x -> x.getCompleted());
                break;
            case 2:
                currentTasks.removeIf(x -> !x.getCompleted());
                break;
            default:
                break;
        }

        adapter.tasks = currentTasks;
        adapter.notifyDataSetChanged();
    }

    private void showDatePicker(Dialog _dialog) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                EditText dateInputText = _dialog.findViewById(R.id.dateTextInput);
                dateInputText.setText(dateFormat.format(calendar.getTime()));
            }
        });
        datePickerDialog.show();
    }
}