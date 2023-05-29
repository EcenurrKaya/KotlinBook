package com.example.kotlinbook

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.kotlinbook.databinding.ActivityBildirimBinding
import java.util.*



class Bildirim : AppCompatActivity() {
    private lateinit var binding : ActivityBildirimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBildirimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannnel()
        binding.buton.setOnClickListener { scheduleNotification() }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = binding.titleEt.text.toString()
        val message = binding.messageEt.text.toString()
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmmanager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmmanager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent)
        showAlert(time,title,message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date=Date(time)
        val dateformat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeformat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this).setTitle("Notification Schuled").setMessage(
            "Title" + title + "\nMessage" + message + "\nAt:" + dateformat.format(date) + " "+ timeformat.format(date)
        ).setPositiveButton("Okay"){_,_ ->}.show()
    }

    private fun getTime(): Long {
        val minute = binding.time.minute
        val hour = binding.time.hour
        val day = binding.date.dayOfMonth
        val month = binding.date.month
        val year = binding.date.year

        val calendar = Calendar.getInstance()
        calendar.set(year,month,day,hour,minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannnel() {
        val name = "Notif Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID,name,importance)
        channel.description=desc
        val notmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notmanager.createNotificationChannel(channel)
    }

}