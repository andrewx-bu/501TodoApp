package com.example.todoapp

import kotlinx.coroutines.flow.Flow

class TodoItemRepository(private val todoItemDao: TodoItemDao) {
    // Basic operations
    suspend fun insert(item: TodoItem) = todoItemDao.insert(item)
    suspend fun update(item: TodoItem) = todoItemDao.update(item)
    suspend fun delete(item: TodoItem) = todoItemDao.delete(item)

    // Get operations
    fun getAllItems(): Flow<List<TodoItem>> = todoItemDao.getAllItems()
    fun getItemsByCompletion(isCompleted: Boolean): Flow<List<TodoItem>> = todoItemDao.getItemsByCompletion(isCompleted)
}
