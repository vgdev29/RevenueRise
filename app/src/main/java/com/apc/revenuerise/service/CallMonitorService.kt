package com.apc.revenuerise.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.apc.revenuerise.dispatchers.DispatcherTypes
import com.apc.revenuerise.repository.home.HomeDefRepo
import com.apc.solarsuvidha.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CallMonitorService : Service() {
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var telephonyCallback: TelephonyCallback // Store the callback reference
    private var isConnected=false
    @Inject
    lateinit var repository: HomeDefRepo

    @Inject
    lateinit var dispatchers: DispatcherTypes

    private val serviceScope by lazy {
        CoroutineScope(SupervisorJob() + (dispatchers.io ?: Dispatchers.IO))
    }

    override fun onCreate() {
        super.onCreate()
        // Start as a foreground service with the notification
        startForeground(1, createNotification("Call monitoring is active"))

        Log.d("CallMonitorService", "Foreground Service Started")

        // Initialize TelephonyManager and register the callback
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    handleCallState(state)
                }
            }

            telephonyManager.registerTelephonyCallback(
                mainExecutor,
                telephonyCallback
            )
        } else {
            telephonyManager.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    handleCallState(state)
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun handleCallState(state: Int) {
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                isConnected=true
                sendAlert("Call Connected")
            }

            TelephonyManager.CALL_STATE_IDLE -> {
            //    fetchCallLogs()
                if(isConnected){
                    Handler(Looper.getMainLooper()).postDelayed({
                        fetchLastCallRecord()
                    }, 2000)
                    //    fetchLastCallRecord()
                    sendAlert("Call Disconnected")
                    isConnected=false
                }

            }

            TelephonyManager.CALL_STATE_RINGING -> {
                sendAlert("Incoming Call")
            }
        }
    }

    private fun sendAlert(message: String) {
        Log.d("CallMonitorService", "Alert: $message")
    }
    private fun fetchLastCallRecord() {
        serviceScope.launch {
            try {
                val result = repository.getLastCallDetails(contentResolver)
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { callLogEntry ->
                            val noti = createNotification(
                                "Last Call: ${callLogEntry.number}, Duration: ${callLogEntry.duration} s"
                            )
                            notificationManager.notify(2, noti)

                            repository.postCallRecord(callLogEntry.number,callLogEntry.duration.toString(),getDate(callLogEntry.date,"yyyy-MM-dd HH:mm:ssZ"))
                            Log.d(
                                "CallMonitorService",
                                "Last Call Details - Number: ${callLogEntry.number}, " +
                                        "Duration: ${callLogEntry.duration}s"
                            )
                        } ?: Log.e("CallMonitorService", "No call data found in Success result!")
                    }
                    is Resource.Error -> {
                        Log.e("CallMonitorService", "Error fetching call logs: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CallMonitorService", "Unexpected error fetching call logs", e)
            }
        }
    }
    private fun getDate(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter: SimpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
    private fun fetchCallLogs() {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR))  // Set the current year
            set(Calendar.MONTH, get(Calendar.MONTH)) // Set the current month
            set(Calendar.DAY_OF_MONTH, 1)            // Set to the first day of the month
            set(Calendar.HOUR_OF_DAY, 0)             // Set to midnight (00:00:00)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR))  // Set the current year
            set(Calendar.MONTH, get(Calendar.MONTH)) // Set the current month
            set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH)) // Set to today
            set(Calendar.HOUR_OF_DAY, 23)            // Set to the last hour of the day (23:59:59)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val numbers = listOf("+919212033808", "01140777777", "9212033811", "+919212033811")
        Log.d("DATE>>", startCalendar.timeInMillis.toString())
        Log.d("DATE>>", endCalendar.timeInMillis.toString())

        serviceScope.launch {
            try {
                val result = repository.getCallLogs(
                    contentResolver,
                    startCalendar.timeInMillis,
                    endCalendar.timeInMillis,
                    numbers
                )
                if (result.data?.isNotEmpty() == true) {
                    val noti =
                        createNotification("Last Call: ${result.data[0].number}, Duration: ${result.data[0].duration} s")
                    notificationManager.notify(2, noti)
                    Log.d("CallMonitorService", "Data fetched: ${result.data.size}")
                } else {
                    Log.e("CallMonitorService", "No data found!")
                }
            } catch (e: Exception) {
                Log.e("CallMonitorService", "Error fetching call logs", e)
            }
        }
    }

    // Method to create notification for foreground service
    private fun createNotification(message: String): Notification {
        val channelId = "call_monitor_service_channel"
        val channelName = "Call Monitor Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Monitoring Active")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false) // Makes it ongoing and not dismissible
            .build()
    }

    // Override onDestroy to handle service cleanup
    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        }
        serviceScope.cancel()
    }

    // Return null since this is not a bound service
    override fun onBind(intent: Intent?): IBinder? = null
}
