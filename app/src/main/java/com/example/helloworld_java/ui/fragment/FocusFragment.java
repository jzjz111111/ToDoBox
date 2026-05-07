package com.example.helloworld_java.ui.fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.helloworld_java.data.local.database.AppDatabase;
import com.example.helloworld_java.ui.widget.CircleProgressView;
import com.example.helloworld_java.R;
import com.example.helloworld_java.data.local.entity.TomatoRecord;

public class FocusFragment extends Fragment {
    private TextView tvStatus, tvTodayCount;
    private Button btnStart, btnPause, btnReset;
    private CircleProgressView circleProgress;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private boolean isWorkTime = true;

    private long timeLeftInMillis = 25 * 60 * 1000; // 25分钟工作
    private final long workTime = 25 * 60 * 1000;
    private final long breakTime = 5 * 60 * 1000;

    private int todayTomatoCount = 0;

    private long timerStartTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus, container, false);
        initViews(view);
        setupClickListeners();
        updateTimerDisplay();
        loadTodayTomatoCount();
        return view;
    }

    private void initViews(View view) {
        tvStatus = view.findViewById(R.id.tv_status);
        tvTodayCount = view.findViewById(R.id.tv_today_count);
        btnStart = view.findViewById(R.id.btn_start);
        btnPause = view.findViewById(R.id.btn_pause);
        btnReset = view.findViewById(R.id.btn_reset);
        circleProgress=view.findViewById(R.id.circle_progress);
        circleProgress.setMaxProgress(100);
        circleProgress.setProgress(0);
        circleProgress.setText(getString(R.string.time_limit));
        circleProgress.setProgressColor(0xFFFF5722); // 橙色
        tvTodayCount.setText(String.valueOf(todayTomatoCount));
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnReset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (!isTimerRunning) {
            timerStartTime = System.currentTimeMillis();
            countDownTimer = new CountDownTimer(timeLeftInMillis, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    if (isWorkTime) {
                        todayTomatoCount++;
                        saveCompletedTomato();
                        tvTodayCount.setText(String.valueOf(todayTomatoCount));
                        isWorkTime = false;
                        timeLeftInMillis = breakTime;
                        tvStatus.setText("休息时间到！");
                        circleProgress.setProgressColor(0xFF4CAF50);
                    } else {
                        isWorkTime = true;
                        timeLeftInMillis = workTime;
                        tvStatus.setText("准备开始新的番茄钟");
                        circleProgress.setProgressColor(0xFFFF5722); // 橙色
                    }
                    updateButtonStates();
                    updateTimerDisplay();
                }
            }.start();

            isTimerRunning = true;
            updateButtonStates();
            tvStatus.setText(isWorkTime ? "专注工作中..." : "休息中...");
        }
    }

    private void saveCompletedTomato() {
        Context context = getContext();
        if (context == null) {
            Log.w("TomatoRecord", "上下文为空，跳过保存番茄钟记录");
            return;
        }
        new Thread(() -> {

            try {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String endTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String startTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timerStartTime));
                TomatoRecord record = new TomatoRecord();
                record.setRecordDate(date);
                record.setStartTime(startTime);
                record.setEndTime(endTime);
                record.setDuration(workTime);
                record.setStatus(0); // STATUS_COMPLETED
                AppDatabase.getInstance(context).tomatoRecordDao().insert(record);

            } catch (Exception e) {
              Log.e("TomatoRecord", "保存番茄钟记录失败", e);
            }
        }).start();
    }

    private void loadTodayTomatoCount() {
        Context context=getContext();
        FragmentActivity activity=getActivity();
        if (context == null || activity == null) {
            Log.w("TomatoLoader", "上下文/Activity为空，跳过加载今日番茄数");
            return;
        }
        new Thread(() -> {
            try {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                AppDatabase db = AppDatabase.getInstance(context);
                int count = db.tomatoRecordDao().getTodayCountSync(today);
                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                    if (!activity.isFinishing() && !activity.isDestroyed()) {
                        todayTomatoCount = count;
                        tvTodayCount.setText(String.valueOf(todayTomatoCount));
                    }
                });
            } catch (Exception e) {
               Log.e("TomatoLoader", "加载今日番茄钟数量失败", e);
            }
        }).start();
    }

    private void saveInterruptedTomato() {
        if (timerStartTime > 0 && timeLeftInMillis < workTime) {
            Context context=getContext();
            if(context==null){
                Log.w("TomatoInterrupted","context为空，跳过记录打断的番茄钟");
                return;
            }
            new Thread(() -> {
                try {
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String endTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    String startTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timerStartTime));
                    long actualDuration = workTime - timeLeftInMillis;
                    TomatoRecord record = new TomatoRecord();
                    record.setRecordDate(date);
                    record.setStartTime(startTime);
                    record.setEndTime(endTime);
                    record.setDuration(actualDuration);
                    record.setStatus(1); // STATUS_INTERRUPTED
                    AppDatabase.getInstance(context).tomatoRecordDao().insert(record);
                } catch (Exception e) {
                    Log.e("TomatoInterrupted","保存已被打断的番茄钟失败",e);
                }
            }).start();
        }
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            updateButtonStates();
            tvStatus.setText("已暂停");
            saveInterruptedTomato();
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            saveInterruptedTomato();
        }
        isTimerRunning = false;
        isWorkTime = true;
        timeLeftInMillis = workTime;
        circleProgress.setProgressColor(0xFFFF5722); // 工作颜色
        timerStartTime = 0;//重置之前瞬时的时间戳，下次获得的会是新的时间戳
        updateButtonStates();
        updateTimerDisplay();
        tvStatus.setText("准备开始");
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        circleProgress.setText(timeLeft);
        long totalTime = isWorkTime ? workTime : breakTime;
        int progress = (int) ((totalTime - timeLeftInMillis) * 100 / totalTime);
        circleProgress.setProgress(progress);
    }

    private void updateButtonStates() {
        btnStart.setEnabled(!isTimerRunning);
        btnPause.setEnabled(isTimerRunning);
        btnStart.setText(isTimerRunning ? "继续" : "开始");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        loadTodayTomatoCount();
    }
}