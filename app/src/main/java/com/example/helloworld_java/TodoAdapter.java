package com.example.helloworld_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskViewHolder> {

    private final List<TodoTask> taskList;
    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);

    public interface OnTaskClickListener {
        void onTaskClick(TodoTask task);
        void onTaskComplete(TodoTask task, boolean isCompleted);
    }

    public TodoAdapter(List<TodoTask> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TodoTask task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbCompleted;
        private final TextView tvTaskTitle;
        private final TextView tvTaskDescription;
        private final TextView tvDueDate;
        private final TextView tvTaskCategory;
        private final ImageView ivPriority;
        private final ImageView ivReminder;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCompleted = itemView.findViewById(R.id.cb_completed);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskCategory=itemView.findViewById(R.id.tv_task_category);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            ivPriority = itemView.findViewById(R.id.iv_priority);
            ivReminder = itemView.findViewById(R.id.iv_reminder);
            setupClickListeners();
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(taskList.get(getBindingAdapterPosition()));
                }
            });
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskComplete(taskList.get(getBindingAdapterPosition()), isChecked);
                }
            });
        }
        public void bind(TodoTask task) {
            tvTaskTitle.setText(task.getTitle());
            tvTaskDescription.setText(task.getDescription());
            if (task.getDueDate() != null) {
                tvDueDate.setText(dateFormat.format(task.getDueDate()));
            } else {
                tvDueDate.setText("无截止日期");
            }
            cbCompleted.setChecked(task.isCompleted());
            setPriorityColor(task.getPriority());
            ivReminder.setVisibility(task.isHasReminder() ? View.VISIBLE : View.GONE);
            if (task.getCategory() != null && !task.getCategory().isEmpty()) {
                tvTaskCategory.setText(task.getCategory());
                tvTaskCategory.setVisibility(View.VISIBLE);
            } else {
                tvTaskCategory.setVisibility(View.GONE);
            }
            if (task.isCompleted()) {
                tvTaskTitle.setAlpha(0.5f);
                tvTaskDescription.setAlpha(0.5f);
                tvDueDate.setAlpha(0.5f);
                tvTaskCategory.setAlpha(0.5f);
            } else {
                tvTaskTitle.setAlpha(1.0f);
                tvTaskDescription.setAlpha(1.0f);
                tvDueDate.setAlpha(1.0f);
                tvTaskCategory.setAlpha(1.0f);
            }
        }

        private void setPriorityColor(int priority) {
            switch (priority) {
                case 3: // 高优先级
                    ivPriority.setColorFilter(itemView.getContext().getColor(android.R.color.holo_red_light));
                    break;
                case 2: // 中优先级
                    ivPriority.setColorFilter(itemView.getContext().getColor(android.R.color.holo_orange_light));
                    break;
                case 1: // 低优先级
                default:
                    ivPriority.setColorFilter(itemView.getContext().getColor(android.R.color.holo_green_light));
                    break;
            }
        }
    }
}