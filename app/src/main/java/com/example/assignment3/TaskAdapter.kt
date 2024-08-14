package com.example.assignment3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TaskAdapter(context: Context, tasks: List<String>) :
    ArrayAdapter<String>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val taskTextView: TextView = view.findViewById(R.id.taskTextView)
        taskTextView.text = getItem(position)
        return view
    }
}