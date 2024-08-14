package com.example.assignment3

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var addTaskButton: Button
    private lateinit var taskListView: ListView
    private lateinit var taskAdapter: ArrayAdapter<String>
    private val tasks: MutableList<String> = mutableListOf()

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private val channelId = "task_notification_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskInput = findViewById(R.id.taskInput)
        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)
        addTaskButton = findViewById(R.id.addTaskButton)
        taskListView = findViewById(R.id.taskListView)

        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        taskListView.adapter = taskAdapter

        createNotificationChannel()

        dateInput.setOnClickListener {
            showDatePickerDialog()
        }

        timeInput.setOnClickListener {
            showTimePickerDialog()
        }

        addTaskButton.setOnClickListener {
            val task = taskInput.text.toString()
            if (task.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                val taskDetails = "Task: $task\nDate: $selectedDate\nTime: $selectedTime"
                tasks.add(taskDetails)
                taskAdapter.notifyDataSetChanged()

                taskInput.text.clear()
                dateInput.text.clear()
                timeInput.text.clear()
                selectedDate = ""
                selectedTime = ""

                sendNotification(taskDetails)

            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = tasks[position]
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_DETAIL", selectedTask)
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
                dateInput.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                selectedTime = timeFormat.format(selectedCalendar.time)
                timeInput.setText(selectedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notification Channel"
            val descriptionText = "Channel for task notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(taskDetails: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Update with your own icon
            .setContentTitle("New Task Added")
            .setContentText(taskDetails)
            .setStyle(NotificationCompat.BigTextStyle().bigText(taskDetails))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Use the context 'this' correctly here
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions
            return
        }
        notificationManager.notify(1, builder.build())
    }
}