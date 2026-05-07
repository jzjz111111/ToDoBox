package com.example.helloworld_java.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.helloworld_java.data.local.database.AppDatabase;
import com.example.helloworld_java.data.local.entity.TodoTask;
import com.example.helloworld_java.utils.NotificationHelper;

import java.util.Date;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())||"android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {

            // 在后台线程重新设置所有提醒
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                List<TodoTask> tasks = db.todoTaskDao().getFutureReminders(new Date());
                for (TodoTask task : tasks) {
                    if (task.getReminderTime() != null && task.getReminderTime().after(new Date())) {
                        NotificationHelper.scheduleNotification(context, task);
                    }
                }
            }).start();
        }
    }
}