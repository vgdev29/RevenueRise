package com.apc.revenuerise

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apc.revenuerise.ui.theme.RevenueRiseTheme
import dagger.hilt.android.AndroidEntryPoint
import android.provider.CallLog
import android.content.ContentResolver
import android.database.Cursor
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RevenueRiseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG), 100)
        }
        else{
            val startCalendar = Calendar.getInstance()
            startCalendar.set(2024, Calendar.NOVEMBER, 1, 0, 0, 0)
            val startDate = startCalendar.timeInMillis

// Set end date to October 31, 2023
            val endCalendar = Calendar.getInstance()
            endCalendar.set(2024, Calendar.NOVEMBER, 12, 23, 59, 59)
            val endDate = endCalendar.timeInMillis
// List of numbers to filter by
            val numbers = listOf("+919212033808", "01140777777")

// Call the function to get filtered call logs by date range and numbers
            val callLogsEntries = getFilteredCallLogsByNumbers(contentResolver, startDate, endDate, numbers)
            for (entry in callLogsEntries) {
                println("Number: ${entry.number}, Type: ${entry.type}, Date: ${entry.date}, Duration: ${entry.duration}")
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


fun getFilteredCallLogsByNumbers(
    contentResolver: ContentResolver,
    startDate: Long,
    endDate: Long,
    numbers: List<String>
): List<CallLogEntry> {
    val callLogEntries = mutableListOf<CallLogEntry>()

    // Prepare placeholders for the numbers list, e.g., (?, ?, ?)
    val numberPlaceholders = numbers.joinToString(",") { "?" }
    val selection = "${CallLog.Calls.TYPE} = ? AND ${CallLog.Calls.DATE} BETWEEN ? AND ? AND ${CallLog.Calls.NUMBER} IN ($numberPlaceholders)"

    // Combine the type, date range, and numbers into selectionArgs
    val selectionArgs = mutableListOf<String>().apply {
        add(CallLog.Calls.OUTGOING_TYPE.toString())
        add(startDate.toString())
        add(endDate.toString())
        addAll(numbers)
    }.toTypedArray()

    val cursor: Cursor? = contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        null,
        selection,
        selectionArgs,
        CallLog.Calls.DATE + " DESC"
    )

    cursor?.use {
        val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
        val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
        val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

        while (it.moveToNext()) {
            val number = it.getString(numberIndex)
            val type = it.getInt(typeIndex)
            val date = it.getLong(dateIndex)
            val duration = it.getLong(durationIndex)

            callLogEntries.add(CallLogEntry(number, type, date, duration))
        }
    }

    return callLogEntries
}

data class CallLogEntry(val number: String, val type: Int, val date: Long, val duration: Long)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RevenueRiseTheme {
        Greeting("Android")
    }
}