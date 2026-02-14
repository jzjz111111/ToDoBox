package com.example.helloworld_java;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Date;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class AddTaskDialog extends DialogFragment {
    private EditText etTitle, etDescription;
    private Spinner spinnerPriority, spinnerCategory;
    private TodoTask taskToEdit;
    private OnTaskSavedListener listener;
    private Button btnSetReminder;
    private Button btnSetDueDate;
    private Date dueDate;
    private Date reminderTime;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat datetimeFormatter = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());


    public interface OnTaskSavedListener {
        void onTaskSaved(TodoTask task);
    }

    public void setOnTaskSavedListener(OnTaskSavedListener listener) {
        this.listener = listener;
    }

    public void setTaskToEdit(TodoTask task) {
        this.taskToEdit = task;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);
        initViews(view);
        bindButtonClick();
        setupSpinners();
        populateData();
        builder.setView(view)
                .setTitle(taskToEdit != null ? "编辑任务" : "添加任务")
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", (dialog, id) -> dismiss());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> attemptSaveTask());
        });
        return dialog;
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        spinnerPriority = view.findViewById(R.id.spinner_priority);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        btnSetDueDate = view.findViewById(R.id.btn_set_due_date);
        btnSetReminder = view.findViewById(R.id.btn_set_reminder);
    }
    private void bindButtonClick(){
        btnSetDueDate.setOnClickListener(v -> showDatePicker());
        btnSetReminder.setOnClickListener(v -> showReminderPicker());
    }
    //下拉框
    private void setupSpinners() {
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.task_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void populateData() {
        if (taskToEdit != null) {
            etTitle.setText(taskToEdit.getTitle());
            etDescription.setText(taskToEdit.getDescription());
            spinnerPriority.setSelection(taskToEdit.getPriority() - 1);
            if (taskToEdit.getCategory() != null) {
                for (int i = 0; i < spinnerCategory.getCount(); i++) {
                    if (spinnerCategory.getItemAtPosition(i).equals(taskToEdit.getCategory())) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
            dueDate = taskToEdit.getDueDate();
            reminderTime = taskToEdit.getReminderTime();
            updateButtonTexts();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
            updateButtonTexts();
        }
    }
    private void attemptSaveTask() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("请输入任务标题");
            return;
        }
        saveTask();
        dismiss();
    }
    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int priority = spinnerPriority.getSelectedItemPosition() + 1;
        String category = spinnerCategory.getSelectedItem().toString();
        TodoTask task;
        if (taskToEdit != null) {
            task = taskToEdit;
            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            task.setCategory(category);
            task.setDueDate(dueDate);
            task.setReminderTime(reminderTime);
            task.setHasReminder(reminderTime != null);
            if (taskToEdit.getReminderTime() != null) {
                NotificationHelper.cancelNotification(requireContext(), taskToEdit.getId());
            }
        } else {
            task = new TodoTask(title, description, dueDate, priority, category);
            task.setReminderTime(reminderTime);
            task.setHasReminder(reminderTime != null);
        }
        if (reminderTime != null) {
            NotificationHelper.scheduleNotification(requireContext(), task);
        }
        if (listener != null) {
            listener.onTaskSaved(task);
        }
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (dueDate != null) {
            calendar.setTime(dueDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); //月份从 0 开始（0=1月，11=12月）
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    dueDate = calendar.getTime();
                    updateButtonTexts();},
                    year,
                    month,
                    day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // 今天及以后
        datePickerDialog.show();
    }
    private void showReminderPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNullElseGet(reminderTime, () -> dueDate != null ? dueDate : new Date()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (dateView, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
                    int minute = calendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (timeView, selectedHour, selectedMinute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                calendar.set(Calendar.MINUTE, selectedMinute);
                                calendar.set(Calendar.SECOND, 0); // 秒设为 0，避免精度问题
                                reminderTime = calendar.getTime();
                                if (reminderTime.before(new Date())) {
                                    Toast.makeText(requireContext(), "提醒时间不能早于当前时间", Toast.LENGTH_SHORT).show();
                                    reminderTime = null;
                                    updateButtonTexts();
                                    return;
                                }
                                updateButtonTexts();
                            },
                            hour,
                            minute,
                            true // 是否使用 24 小时制（true=24小时制，false=12小时制）
                    );
                    timePickerDialog.show();
                },
                year,
                month,
                day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
    private void updateButtonTexts() {
        if (dueDate != null) {
            btnSetDueDate.setText(String.format("截止日期：%s", dateFormatter.format(dueDate)));
        } else {
            btnSetDueDate.setText(R.string.set_due_date);
        }
        if (reminderTime != null) {
            btnSetReminder.setText(String.format("提醒时间：%s", datetimeFormatter.format(reminderTime)));
        } else {
            btnSetReminder.setText(R.string.set_reminder_time);
        }
    }
}