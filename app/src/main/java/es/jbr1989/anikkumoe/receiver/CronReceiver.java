package es.jbr1989.anikkumoe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import es.jbr1989.anikkumoe.service.NotificationService;

/**
 * Created by jbr1989 on 09/11/2017.
 */

public class CronReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent NotificationServiceIntent = new Intent(context, NotificationService.class);
        context.startService(NotificationServiceIntent);
    }
}
