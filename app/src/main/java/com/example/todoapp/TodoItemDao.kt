package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TodoItem)

    @Update
    suspend fun update(item: TodoItem)

    @Delete
    suspend fun delete(item: TodoItem)

    // Get items ordered by ID
    @Query("SELECT * FROM tasks ORDER BY id")
    fun getAllItems(): Flow<List<TodoItem>>

    // Get items by completion status
    @Query("SELECT * FROM tasks WHERE isDone = :isCompleted ORDER BY id")
    fun getItemsByCompletion(isCompleted: Boolean): Flow<List<TodoItem>>
}
