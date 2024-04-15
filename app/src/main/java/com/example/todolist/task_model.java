package com.example.todolist;

import java.util.UUID;

// Model til en task
public class task_model {

    UUID id;
    String taskName;
    String date;
    String priority;
    Boolean completed;

    // Constructor til oprettelse af en task. Her angiver vi ikke ID, og den bliver derfor genereret
    public task_model(String taskName, String date, String priority, Boolean completed) {
        this.taskName = taskName;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
        this.id = UUID.randomUUID();
    }

    // Constructor til task. Nu med id, så den bruger vi typisk til at "læse" og sammenligne med
    public task_model(UUID id, String taskName, String date, String priority, Boolean completed) {
        this.id = id;
        this.taskName = taskName;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    public UUID getId() { return id; }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
