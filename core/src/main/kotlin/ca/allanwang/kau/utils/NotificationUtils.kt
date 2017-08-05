package ca.allanwang.kau.utils

import android.content.Context
import android.support.v4.app.NotificationManagerCompat


/**
 * Created by Allan Wang on 2017-08-04.
 */
fun Context.cancelNotification(notifId: Int)
        = NotificationManagerCompat.from(this).cancel(notifId)