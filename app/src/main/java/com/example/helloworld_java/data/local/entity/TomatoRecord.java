package com.example.helloworld_java.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "tomato_records")
public class TomatoRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "record_date")
    private String recordDate;        // 日期：yyyy-MM-dd

    @ColumnInfo(name = "start_time")
    private String startTime;         // 开始时间：HH:mm:ss

    @ColumnInfo(name = "end_time")
    private String endTime;           // 结束时间：HH:mm:ss

    @ColumnInfo(name = "duration")
    private long duration;            // 持续时间（毫秒）

    @ColumnInfo(name = "status")
    private int status;               // 状态：0-完成，1-中断

    @ColumnInfo(name = "created_at")
    private long createdAt;           // 创建时间戳

    @ColumnInfo(name = "task_id")
    private Integer taskId;           // 关联的任务ID（可为空）

    @Ignore
    public static final int STATUS_COMPLETED = 0;
    @Ignore
    public static final int STATUS_INTERRUPTED = 1;

    public TomatoRecord() {
        this.createdAt = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.recordDate = sdf.format(new Date());
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    @Ignore
    public int getDurationMinutes() {
        return (int) (duration / (60 * 1000));
    }

    @Ignore
    public String getTimeRange() {
        if (startTime != null && endTime != null && startTime.length() >= 5 && endTime.length() >= 5) {
            return startTime.substring(0, 5) + " - " + endTime.substring(0, 5);
        }
        return startTime + " - " + endTime;
    }
    //status=0完成，1中断
    @Ignore
    public boolean isCompleted() {
        return status == STATUS_COMPLETED;
    }
}