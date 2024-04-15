package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class task_adapter extends RecyclerView.Adapter<task_adapter.MyViewHolder> {

    Context context;
    ArrayList<task_model> tasks;
    TabLayout tabLayout;

    // Constructor til vores adapter, som vi bruger til vores RecyclerView.
    // Jeg har sendt Tablayout med over, så vi også kan agere på filtrering på hver Card.
    public task_adapter(Context context, ArrayList<task_model> tasks, TabLayout tabLayout) {
        this.context = context;
        this.tasks = tasks;
        this.tabLayout = tabLayout;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_model, parent, false);
        return new MyViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull task_adapter.MyViewHolder holder, int position) {
        holder.cardTitle.setText(tasks.get(position).getTaskName());
        holder.cardDate.setText(tasks.get(position).getDate());
        holder.cardPriority.setText(tasks.get(position).getPriority());
        holder.cardCompleted.setChecked(tasks.get(position).getCompleted());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private task_adapter adapter;
        TextView cardTitle, cardDate, cardPriority;
        CheckBox cardCompleted;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDate = itemView.findViewById(R.id.cardDate);
            cardPriority = itemView.findViewById(R.id.cardPriority);
            cardCompleted = itemView.findViewById(R.id.cardCompleted);

            ImageButton deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task_model taskToDelete = adapter.tasks.get(getAdapterPosition());

                    adapter.tasks.remove(taskToDelete);
                    storage_manager.deleteTask(adapter.context, taskToDelete.id.toString());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(adapter.context, taskToDelete.taskName + " was removed", Toast.LENGTH_LONG).show();
                }
            });

            cardCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task_model taskToUpdate = adapter.tasks.get(getAdapterPosition());

                    taskToUpdate.setCompleted(!taskToUpdate.getCompleted());
                    storage_manager.updateTask(adapter.context, taskToUpdate.id.toString(), taskToUpdate);
                    ArrayList<task_model> currentTasks = new ArrayList<>(storage_manager.loadTasks(adapter.context));
                    switch (adapter.tabLayout.getSelectedTabPosition()) {
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
            });
        }

        public MyViewHolder linkAdapter(task_adapter adapter) {
            this.adapter = adapter;
            return this;
        }
    }
}
