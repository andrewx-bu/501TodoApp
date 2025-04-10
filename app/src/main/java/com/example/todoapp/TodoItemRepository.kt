package com.example.todoapp

import kotlinx.coroutines.flow.Flow

class TodoItemRepository(private val dao: TodoItemDao) {

    val allItems: Flow<List<TodoItem>> = dao.getAllItems()

    fun getItemsByCompletion(isCompleted: Boolean): Flow<List<TodoItem>> {
        return dao.getItemsByCompletion(isCompleted)
    }

    suspend fun insert(item: TodoItem) {
        dao.insert(item)
    }

    suspend fun update(item: TodoItem) {
        dao.update(item)
    }

    suspend fun delete(item: TodoItem) {
        dao.delete(item)
    }
}
