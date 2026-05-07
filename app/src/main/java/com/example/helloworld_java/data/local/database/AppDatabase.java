package com.example.helloworld_java.data.local.database;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.example.helloworld_java.data.local.entity.TodoTask;
import com.example.helloworld_java.data.local.entity.TomatoRecord;
import com.example.helloworld_java.data.converter.Converters;
import com.example.helloworld_java.data.local.dao.TodoTaskDao;
import com.example.helloworld_java.data.local.dao.TomatoRecordDao;

@Database(entities = {TodoTask.class, TomatoRecord.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TodoTaskDao todoTaskDao();
    public abstract TomatoRecordDao tomatoRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "todo_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}