package com.example.todolist;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Lokal lagring med CRUD, som gemmer data i form af JSON
public class storage_manager {
    private static final String FILENAME = "tasks.json";

    // Public
    public static void saveTask(Context context, task_model task) {
        List<task_model> tasks = loadTasks(context);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        saveTasks(context, tasks);
    }

    public static void updateTask(Context context, String taskId, task_model updatedTask) {
        List<task_model> tasks = loadTasks(context);
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                task_model task = tasks.get(i);
                if (task.getId().toString().equals(taskId)) {
                    tasks.set(i, updatedTask);
                    saveTasks(context, tasks);
                    return;
                }
            }
        }
    }

    public static void deleteTask(Context context, String taskId) {
        List<task_model> tasks = loadTasks(context);
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                task_model task = tasks.get(i);
                if (task.getId().toString().equals(taskId)) {
                    tasks.remove(i);
                    saveTasks(context, tasks);
                    return;
                }
            }
        }
    }

    public static List<task_model> loadTasks(Context context) {
        List<task_model> tasks = new ArrayList<>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists())
                return null;

            fis = context.openFileInput(FILENAME);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                task_model task = new task_model(
                        UUID.fromString(jsonObject.getString("id")),
                        jsonObject.getString("taskName"),
                        jsonObject.getString("date"),
                        jsonObject.getString("priority"),
                        jsonObject.getBoolean("completed"));
                tasks.add(task);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    // Private
    private static void saveTasks(Context context, List<task_model> tasks) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            JSONArray jsonArray = new JSONArray();
            for (task_model task : tasks) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", task.getId());
                jsonObject.put("taskName", task.getTaskName());
                jsonObject.put("date", task.getDate());
                jsonObject.put("priority", task.getPriority());
                jsonObject.put("completed", task.getCompleted());
                jsonArray.put(jsonObject);
            }

            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            bw = new BufferedWriter(new FileWriter(context.getFileStreamPath(FILENAME)));
            bw.write(jsonArray.toString());
            bw.flush();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
