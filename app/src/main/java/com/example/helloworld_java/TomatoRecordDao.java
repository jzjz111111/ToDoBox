package com.example.helloworld_java;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface TomatoRecordDao {
    class DateStat {
        public String date;
        public int count;

        public DateStat() {}
        @Ignore

        public DateStat(String date, int count) {
            this.date = date;
            this.count = count;
        }
    }

    // 类
    class BestDayRecord {
        public String record_date;
        public int count;

        public BestDayRecord() {}
        @Ignore

        public BestDayRecord(String record_date, int count) {
            this.record_date = record_date;
            this.count = count;
        }
    }
    @Insert
    long insert(TomatoRecord record);

    @Update
    void update(TomatoRecord record);

    @Delete
    void delete(TomatoRecord record);

    // 查询方法
    @Query("SELECT * FROM tomato_records ORDER BY created_at DESC")
    LiveData<List<TomatoRecord>> getAllRecords();
    //今日记录
    @Query("SELECT * FROM tomato_records WHERE record_date = :date ORDER BY start_time DESC")
    LiveData<List<TomatoRecord>> getRecordsByDate(String date);

    @Query("SELECT * FROM tomato_records WHERE task_id = :taskId ORDER BY created_at DESC")
    LiveData<List<TomatoRecord>> getRecordsByTaskId(int taskId);

    // 统计方法
    @Query("SELECT COUNT(*) FROM tomato_records WHERE record_date = :date AND status = 0")
    LiveData<Integer> getCompletedCountByDate(String date);
    // 同步查询方法（用于直接获取值）
    @Query("SELECT COUNT(*) FROM tomato_records WHERE record_date = :date AND status = 0")
    int getTodayCountSync(String date);

    @Query("SELECT COUNT(*) FROM tomato_records WHERE record_date = :date")
    LiveData<Integer> getTotalCountByDate(String date);

    @Query("SELECT COUNT(*) FROM tomato_records WHERE status = 0")
    LiveData<Integer> getTotalCompletedCount();

    @Query("SELECT COUNT(*) FROM tomato_records")
    LiveData<Integer> getTotalCount();

    @Query("SELECT SUM(duration) FROM tomato_records WHERE record_date = :date AND status = 0")
    LiveData<Long> getTotalDurationByDate(String date);

    @Query("SELECT SUM(duration) FROM tomato_records WHERE status = 0")
    LiveData<Long> getTotalDuration();

    // 获取最近几天的统计
    @Query("SELECT record_date as date, COUNT(*) as count FROM tomato_records " +
            "WHERE status = 0 AND record_date >= :startDate " +
            "GROUP BY record_date ORDER BY record_date DESC")
    LiveData<List<DateStat>> getDailyStats(String startDate);

    // 获取连续天数
    @Query("SELECT COUNT(DISTINCT record_date) as streak " +
            "FROM tomato_records " +
            "WHERE status = 0 AND record_date >= date('now', '-30 days')")
    LiveData<Integer> getStreakDays();

    // 获取最佳单日记录
    @Query("SELECT record_date, COUNT(*) as count FROM tomato_records " +
            "WHERE status = 0 " +
            "GROUP BY record_date " +
            "ORDER BY count DESC LIMIT 1")
    LiveData<BestDayRecord> getBestDayRecord();
    @Query("SELECT COUNT(DISTINCT record_date) FROM tomato_records WHERE status = 0")
    LiveData<Integer> getDistinctRecordDays();
    //  同步获取总时长
    @Query("SELECT SUM(duration) FROM tomato_records WHERE status = 0")
    Long getTotalDurationSync();

    // 同步获取有记录的去重天数
    @Query("SELECT COUNT(DISTINCT record_date) FROM tomato_records WHERE status = 0")
    Integer getDistinctRecordDaysSync();
}



