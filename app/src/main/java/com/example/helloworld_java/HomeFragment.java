package com.example.helloworld_java;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
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
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(requireContext(), "需要开启通知权限才能接收任务提醒", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        bindButtonClick();
        setupRecyclerView();
        loadTasks();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkNotificationPermission();
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
        updateTask(task);
    }

    private void updateTask(TodoTask task) {
        mExecutor.execute(()->{
            appDatabase.todoTaskDao().update(task);
            mMainHandler.post(this::loadTasks);
        });
    }

    private void showEditTaskDialog(TodoTask task) {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.setTaskToEdit(task);
        dialog.setOnTaskSavedListener(this::updateTask);
        dialog.show(getParentFragmentManager(), "EditTaskDialog");

    }
    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.setOnTaskSavedListener(this::insertTask);
        dialog.show(getParentFragmentManager(), "AddTaskDialog");
    }

    private void insertTask(TodoTask task) {
        mExecutor.execute(()->{
            appDatabase.todoTaskDao().insert(task);
            mMainHandler.post(this::loadTasks);
        });
    }
    private void loadTasks() {
        mExecutor.execute(() -> {
            List<TodoTask> pendingTasks = appDatabase.todoTaskDao().getPendingTasks();
            List<TodoTask> completedTasks = appDatabase.todoTaskDao().getCompletedTasks();
            TaskStats stats = new TaskStats(pendingTasks, completedTasks);
            mMainHandler.post(() -> {
                taskList.clear();
                int startPosition = 0;
                int itemCount =stats.pendingTasks.size() +stats.completedTasks.size();
                taskList.addAll(stats.pendingTasks);
                taskList.addAll(stats.completedTasks);
                todoAdapter.notifyItemRangeChanged(startPosition,itemCount);
                updateStats(stats.pendingTasks.size(), stats.completedTasks.size());
            });
        });
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
        if (pendingCount == 0 && completedCount == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerTasks.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerTasks.setVisibility(View.VISIBLE);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
               requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}

