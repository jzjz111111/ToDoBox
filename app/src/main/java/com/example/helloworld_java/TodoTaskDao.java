package com.example.helloworld_java;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.Date;
import java.util.List;

@Dao
public interface TodoTaskDao {
    @Insert
    void insert(TodoTask task);

    @Update
    void update(TodoTask task);

    @Delete
    void delete(TodoTask task);

    @Query("SELECT * FROM todo_tasks WHERE is_completed = 0 ORDER BY " +
            "CASE priority WHEN 3 THEN 1 WHEN 2 THEN 2 WHEN 1 THEN 3 ELSE 4 END, due_date ASC")
    List<TodoTask> getPendingTasks();

    @Query("SELECT * FROM todo_tasks WHERE is_completed = 1 ORDER BY due_date DESC")
    List<TodoTask> getCompletedTasks();

    @Query("SELECT * FROM todo_tasks WHERE due_date BETWEEN :startTimestamp AND :endTimestamp")
    List<TodoTask> getTasksByDateRange(Long startTimestamp, Long endTimestamp);

    @Query("SELECT * FROM todo_tasks WHERE id = :taskId")
    TodoTask getTaskById(int taskId);

    @Query("UPDATE todo_tasks SET is_completed = :completed WHERE id = :taskId")
    void updateTaskStatus(int taskId, boolean completed);
    @Query("SELECT * FROM todo_tasks WHERE has_reminder = 1 AND reminder_time > :currentTime")
    List<TodoTask> getFutureReminders(Date currentTime);


}