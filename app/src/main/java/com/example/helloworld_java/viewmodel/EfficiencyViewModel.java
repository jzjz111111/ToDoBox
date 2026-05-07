package com.example.helloworld_java.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helloworld_java.data.local.database.AppDatabase;
import com.example.helloworld_java.data.local.entity.TomatoRecord;
import com.example.helloworld_java.data.local.dao.TomatoRecordDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EfficiencyViewModel extends ViewModel {
    private static final String TAG ="DayAverageTime";
    private AppDatabase database;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final MutableLiveData<String> todayDate = new MutableLiveData<>();
    // 线程池（用于子线程计算平均时长，避免阻塞主线程）
    private final Executor executor = Executors.newSingleThreadExecutor();
    // 存储平均每天时长的 LiveData
    private final MutableLiveData<Long> averageDailyDurationLiveData = new MutableLiveData<>(0L);
    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public void loadStatistics() {
        // 触发 LiveData 更新
        todayDate.setValue(dateFormat.format(new Date()));
        loadAverageDailyDuration();
    }

    private String getToday() {

        return  dateFormat.format(new Date());
    }

    // LiveData 获取器
    public LiveData<Integer> getTodayCompletedCount() {
        if (database == null) return new MutableLiveData<>(0);
        return database.tomatoRecordDao().getCompletedCountByDate(getToday());
    }

    public LiveData<Long> getTodayDuration() {
        if (database == null) return new MutableLiveData<>(0L);
        return database.tomatoRecordDao().getTotalDurationByDate(getToday());
    }

    public LiveData<Integer> getTotalCompletedCount() {
        if (database == null) return new MutableLiveData<>(0);
        return database.tomatoRecordDao().getTotalCompletedCount();
    }

    public LiveData<Long> getTotalDuration() {
        if (database == null) return new MutableLiveData<>(0L);
        return database.tomatoRecordDao().getTotalDuration();
    }

    public LiveData<Integer> getStreakDays() {
        if (database == null) return new MutableLiveData<>(0);
        return database.tomatoRecordDao().getStreakDays();
    }

    public LiveData<List<TomatoRecord>> getTodayRecords() {
        if (database == null) return new MutableLiveData<>();
        return database.tomatoRecordDao().getRecordsByDate(getToday());
    }

    public LiveData<TomatoRecordDao.BestDayRecord> getBestDayRecord() {
        if (database == null) return new MutableLiveData<>();
        return database.tomatoRecordDao().getBestDayRecord();
    }
    //对外暴露
    public LiveData<Long> getAverageDailyDuration() {
        // 首次调用时主动加载数据
        Long currentValue = averageDailyDurationLiveData.getValue();
        if (currentValue != null && currentValue == 0L && database != null) {
            loadAverageDailyDuration();
        }
        return averageDailyDurationLiveData;
    }
    private void loadAverageDailyDuration() {
        if (database == null) {
            averageDailyDurationLiveData.postValue(0L);
            return;
        }
        executor.execute(() -> {
            try {
                Long totalDuration = database.tomatoRecordDao().getTotalDurationSync();
                totalDuration = (totalDuration == null) ? 0L : totalDuration;
                Integer distinctRecordDays = database.tomatoRecordDao().getDistinctRecordDaysSync();
                // 无记录时按 1 天算，避免除以 0
                distinctRecordDays = (distinctRecordDays == null || distinctRecordDays == 0) ? 1 : distinctRecordDays;
                long averageDuration = totalDuration / distinctRecordDays;
                // 通知主线程更新 LiveData
                averageDailyDurationLiveData.postValue(averageDuration);
            } catch (Exception e) {
                Log.e(TAG,"日均时长计算失败",e);
                averageDailyDurationLiveData.postValue(0L); // 异常时默认 0
            }
        });
    }
}
