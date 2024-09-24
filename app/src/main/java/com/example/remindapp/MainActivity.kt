package com.example.remindapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.remindapp.ui.theme.RemindAppTheme
import androidx.compose.foundation.layout.Row // Row
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.ui.platform.LocalContext // For accessing the current Context
import java.util.Calendar // For working with dates and times
import androidx.compose.foundation.layout.fillMaxWidth
// Need for snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemindAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReminderApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun ReminderApp(modifier: Modifier = Modifier) {
    // Saving reminder, date, and time
    var reminderText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("0/00/20XX") }
    var selectedTime by remember { mutableStateOf("00:00") }
    var isReminderSet by remember { mutableStateOf(false) } // State to track if the reminder is set

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Need to initialize context to get access to calendar
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // DatePickerDialog initialization
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "${month + 1}/$dayOfMonth/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() // Restrict to future dates
    }

    // TimePickerDialog initialization
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hourOfDay: Int, minute: Int ->
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            if (hourOfDay < currentHour || (hourOfDay == currentHour && minute <= currentMinute)) {
                selectedTime = "Time is in the past!"
            } else {
                selectedTime = "$hourOfDay:$minute"
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    // Scaffold to hold the layout and Snackbar
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }  // Add SnackbarHost to Scaffold
    ) { innerPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Reminder TextField
            TextField(
                value = reminderText,
                onValueChange = { reminderText = it },
                label = { Text("Enter Reminder:") },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row for date selection
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { datePickerDialog.show() }) {
                    Text(text = "Select Date")
                }
                //Text(text = "Selected Date: $selectedDate")
            }

            // Row for time selection
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { timePickerDialog.show() }) {
                    Text(text = "Select Time")
                }
                //Text(text = "Selected Time: $selectedTime")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to set the reminder
            Button(
                onClick = {
                    if (reminderText.isNotEmpty() && selectedDate != "0/00/20XX" && selectedTime != "00:00") {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Reminder set for $selectedDate at $selectedTime!"
                            )
                        }
                        isReminderSet = true // Mark the reminder as set
                    }
                }
            ) {
                Text(text = "Set Reminder")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conditionally display the reminder details after the reminder is set
            if (isReminderSet) {
                Text(
                    text = "Reminder Message: $reminderText",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Date: $selectedDate",
                    fontSize = 18.sp
                )

                Text(
                    text = "Time: $selectedTime",
                    fontSize = 18.sp
                )
            }

            // Clear button for reminder
            Button(
                onClick = {
                    // Clear the reminder details
                    reminderText = ""
                    selectedDate = "0/00/20XX"
                    selectedTime = "00:00"
                    isReminderSet = false  // Reset the reminder state

                    // Show Snackbar when the reminder is cleared
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Reminder cleared"
                        )
                    }
                }
            ) {
                Text("Clear Reminder")
            }


        }
    }
}









@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RemindAppTheme {

    }
}