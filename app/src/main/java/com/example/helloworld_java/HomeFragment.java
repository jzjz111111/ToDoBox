package com.example.helloworld_java;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeFragment extends Fragment implements TodoAdapter.OnTaskClickListener {

    private RecyclerView recyclerTasks;
    private TodoAdapter todoAdapter;
    private List<TodoTask> taskList;
    private ImageButton btnAddTask;
    private Button btnAddFirstTask;
    private TextView tvPendingCount, tvCompletedCount;
    private View layoutEmpty;
    private AppDatabase appDatabase;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        bindButtonClick();
        setupRecyclerView();
        loadTasks();
        return view;
    }
    private void initViews(View view) {
        recyclerTasks = view.findViewById(R.id.recycler_tasks);
        btnAddTask = view.findViewById(R.id.btn_add_task);
        btnAddFirstTask=view.findViewById(R.id.btn_add_first_task);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        appDatabase = AppDatabase.getInstance(requireContext());
        taskList = new ArrayList<>();
    }
    private void bindButtonClick(){
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
        btnAddFirstTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter(taskList, this);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTasks.setAdapter(todoAdapter);
    }



    @Override
    public void onTaskClick(TodoTask task) {
        showEditTaskDialog(task);
    }

    @Override
    public void onTaskComplete(TodoTask task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        new UpdateTaskAsync().execute(task);

        // 更新统计
        loadTasks();
    }
    private void showEditTaskDialog(TodoTask task) {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.setTaskToEdit(task);
        dialog.setOnTaskSavedListener(updatedTask -> new UpdateTaskAsync().execute(updatedTask));
        dialog.show(getParentFragmentManager(), "EditTaskDialog");

    }
    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.setOnTaskSavedListener(task -> new InsertTaskAsync().execute(task));
        dialog.show(getParentFragmentManager(), "AddTaskDialog");
    }
    private class UpdateTaskAsync extends AsyncTask<TodoTask, Void, Void> {
        @Override
        protected Void doInBackground(TodoTask... tasks) {
            appDatabase.todoTaskDao().update(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadTasks();
        }
    }

    private class InsertTaskAsync extends AsyncTask<TodoTask, Void, Void> {
        @Override
        protected Void doInBackground(TodoTask... tasks) {
            appDatabase.todoTaskDao().insert(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadTasks();
        }
    }

    private class LoadTasksAsync extends AsyncTask<Void, Void, TaskStats> {
        @Override
        protected TaskStats doInBackground(Void... voids) {
            List<TodoTask> pendingTasks = appDatabase.todoTaskDao().getPendingTasks();
            List<TodoTask> completedTasks = appDatabase.todoTaskDao().getCompletedTasks();
            return new TaskStats(pendingTasks, completedTasks);
        }

        @Override
        protected void onPostExecute(TaskStats stats) {
            taskList.clear();
            taskList.addAll(stats.pendingTasks);
            taskList.addAll(stats.completedTasks);
            todoAdapter.notifyDataSetChanged();
            updateStats(stats.pendingTasks.size(), stats.completedTasks.size());
        }
    }
    private static class TaskStats {
        List<TodoTask> pendingTasks;
        List<TodoTask> completedTasks;
        TaskStats(List<TodoTask> pendingTasks, List<TodoTask> completedTasks) {
            this.pendingTasks = pendingTasks;
            this.completedTasks = completedTasks;
        }
    }
    private void updateStats(int pendingCount, int completedCount) {
        tvPendingCount.setText(String.valueOf(pendingCount));
        tvCompletedCount.setText(String.valueOf(completedCount));
        // 显示/隐藏空状态
        if (pendingCount == 0 && completedCount == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerTasks.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerTasks.setVisibility(View.VISIBLE);
        }
    }
    private void loadTasks() {new LoadTasksAsync().execute();}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkNotificationPermission();
    }
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }
    // 权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(requireContext(), "需要开启通知权限才能接收任务提醒", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

