package com.example.assignment3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class SavedTasksActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val taskList = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_tasks)

        listView = findViewById(R.id.taskListView)
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val savedTasks = sharedPreferences.getStringSet("tasks_list", mutableSetOf())?.toMutableList() ?: mutableListOf()
        taskList.addAll(savedTasks)

        val adapter = TaskAdapter(this, taskList)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedTask = taskList[position]
            val intent = Intent(this, TaskDetailActivity::class.java).apply {
                putExtra("TASK_DETAIL", selectedTask)
            }
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_task -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}