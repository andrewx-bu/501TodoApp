package com.example.todoapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") var title: String,
    @ColumnInfo(name = "desc") var description: String?,
    @ColumnInfo(name = "isDone") var isDone: Boolean = false
)
