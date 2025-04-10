package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoItemRepository
    val allItems: Flow<List<TodoItem>>

    init {
        val dao = TodoItemDatabase.getDatabase(application).todoItemDao()
        repository = TodoItemRepository(dao)
        allItems = repository.allItems
    }

    fun getItemsByCompletion(b: Boolean): Flow<List<TodoItem>> {
        return repository.getItemsByCompletion(b)
    }

    fun insert(item: TodoItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: TodoItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: TodoItem) = viewModelScope.launch {
        repository.delete(item)
    }
}
