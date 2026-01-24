package com.example.helloworld_java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("task_id", 0);
        String title = intent.getStringExtra("task_title");
        String description = intent.getStringExtra("task_description");
        NotificationHelper.sendNotification(context, title, description, taskId);
    }
}
