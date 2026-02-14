package com.example.helloworld_java;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EfficiencyFragment extends Fragment {

    private Button btnStartFocus;
    private TextView tvTodayTomatoCount, tvTodayDuration;
    private TextView tvTotalTomatoCount, tvTotalDuration, tvAverageDailyDuration,tvStreakDays;
    private TextView tvBestRecord, tvTodayRecords;

    private Runnable timerTask;
    private int secondsElapsed = 0;
    private boolean isRunning = false;
    private static final int TOMATO_TIME = 25 * 60; // 25分钟

    // ViewModel
    private EfficiencyViewModel viewModel;

    // 线程池
    private final Executor executor = Executors.newSingleThreadExecutor();

    // 日期格式
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_efficiency, container, false);
        initViews(view);
        initViewModel();
        setupButton();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void initViews(View view) {
        btnStartFocus = view.findViewById(R.id.btn_start_focus);
        tvTodayTomatoCount = view.findViewById(R.id.tv_today_tomato_count);
        tvTodayDuration = view.findViewById(R.id.tv_today_duration);
        tvTotalTomatoCount = view.findViewById(R.id.tv_total_tomato_count);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        tvStreakDays = view.findViewById(R.id.tv_streak_days);
        tvBestRecord = view.findViewById(R.id.tv_best_record);
        tvTodayRecords = view.findViewById(R.id.tv_today_records);
        tvAverageDailyDuration=view.findViewById(R.id.tv_avg_daily_duration);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(EfficiencyViewModel.class);
        viewModel.setDatabase(AppDatabase.getInstance(requireContext()));

        // 观察数据变化
        observeData();
    }

    private void observeData() {
        viewModel.getTodayCompletedCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) tvTodayTomatoCount.setText(String.valueOf(count));
        });
        viewModel.getTodayDuration().observe(getViewLifecycleOwner(), duration -> {
            if (duration != null) tvTodayDuration.setText(formatDuration(duration));
        });
        viewModel.getTotalCompletedCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) tvTotalTomatoCount.setText(String.valueOf(count));
        });
        viewModel.getTotalDuration().observe(getViewLifecycleOwner(), duration -> {
            if (duration != null) tvTotalDuration.setText(formatDuration(duration));
        });
        viewModel.getStreakDays().observe(getViewLifecycleOwner(), days -> {
            if (days != null) tvStreakDays.setText(days + "天");
        });
        viewModel.getTodayRecords().observe(getViewLifecycleOwner(), this::updateTodayRecords);
        viewModel.getBestDayRecord().observe(getViewLifecycleOwner(), best -> {
            if (best != null && best.count > 0) {
                tvBestRecord.setText(best.count + "个");
            }
        });
        viewModel.getAverageDailyDuration().observe(getViewLifecycleOwner(), averageDuration -> {
            if (averageDuration != null) {
                tvAverageDailyDuration.setText("" + formatDuration(averageDuration));
            } else {
                tvAverageDailyDuration.setText("0分钟");
            }
        });
    }

    private void setupButton() {
        btnStartFocus.setOnClickListener(v -> {
            if (isRunning) {
                stopTomato();
            } else {
                startTomato();
            }
        });
    }

    private void startTomato() {
        isRunning = true;
        secondsElapsed = 0;
        btnStartFocus.setText("00:00");
        btnStartFocus.setBackgroundColor(0xFFFF5722); // 红色


        timerTask = new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                updateTimerText();
                if (secondsElapsed >= TOMATO_TIME) {
                    completeTomato();
                } else {
                    btnStartFocus.postDelayed(this, 1000);
                }
            }
        };
        btnStartFocus.post(timerTask);

        Toast.makeText(getContext(), "专注开始", Toast.LENGTH_SHORT).show();
    }

    private void stopTomato() {
        if (!isRunning) return;
        isRunning = false;

        if (timerTask != null) {
            btnStartFocus.removeCallbacks(timerTask);
            timerTask = null;
        }

        int minutes = secondsElapsed / 60;
        if (minutes >= 5) {
            saveRecord(minutes, false);
            Toast.makeText(getContext(), "专注" + minutes + "分钟", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "时间太短", Toast.LENGTH_SHORT).show();
        }

        resetButton();
        loadStatistics();
    }

    private void completeTomato() {
        if (!isRunning) return;
        isRunning = false;

        if (timerTask != null) {
            btnStartFocus.removeCallbacks(timerTask);
            timerTask = null;
        }

        saveRecord(25, true);
        Toast.makeText(getContext(), "完成一个番茄钟！", Toast.LENGTH_LONG).show();
        resetButton();
        loadStatistics();
    }
    private void saveRecord(int minutes, boolean completed) {
        executor.execute(() -> {
            Context context=getContext();
            try {
                TomatoRecord record = new TomatoRecord();
                record.setRecordDate(dateFormat.format(new Date()));
                record.setStartTime(timeFormat.format(new Date(System.currentTimeMillis() - minutes * 60 * 1000L)));
                record.setEndTime(timeFormat.format(new Date()));
                record.setDuration(minutes * 60 * 1000L);
                record.setStatus(completed ? TomatoRecord.STATUS_COMPLETED : TomatoRecord.STATUS_INTERRUPTED);
                AppDatabase.getInstance(context).tomatoRecordDao().insert(record);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void resetButton() {
        btnStartFocus.setText("开始专注");
        btnStartFocus.setBackgroundColor(0xFF4CAF50); // 绿色
    }
    private void updateTimerText() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        btnStartFocus.setText(String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds));
    }


//今日记录，保存记录时的线程切换

    private void loadStatistics() {
        viewModel.loadStatistics();
    }

    private void updateTodayRecords(List<TomatoRecord> records) {
        if (records == null||records.isEmpty()) {
            tvTodayRecords.setText("暂无记录");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (TomatoRecord record : records) {
            String emoji = record.isCompleted() ? "✅" : "⏸️";
            sb.append(emoji)
                    .append(" ")
                    .append(record.getTimeRange())
                    .append(" - ")
                    .append(record.getDurationMinutes())
                    .append("分钟")
                    .append("\n");
        }
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        tvTodayRecords.setText(sb.toString());
    }

    private String formatDuration(long millis) {
        long minutes = millis / (60 * 1000);
        if (minutes < 60) {
            return minutes + "分钟";
        } else {
            long hours = minutes / 60;
            long mins = minutes % 60;
            return hours + "小时" + mins + "分钟";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btnStartFocus != null && timerTask != null) {
            btnStartFocus.removeCallbacks(timerTask);
        }
        timerTask = null;
        isRunning = false;
    }
}