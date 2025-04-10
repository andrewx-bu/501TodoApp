package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.todoapp.ui.theme.TodoAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainActivityViewModel) {
    val coroutineScope = rememberCoroutineScope()

    var filterOption by remember { mutableStateOf(FilterOption.ALL) }

    val todoItems by when (filterOption) {
        FilterOption.ALL -> viewModel.allItems
        FilterOption.TODO -> viewModel.getItemsByCompletion(false)
        FilterOption.COMPLETED -> viewModel.getItemsByCompletion(true)
    }.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(15.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
            )
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Filter By")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            filterOption = FilterOption.ALL
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Pending") },
                        onClick = {
                            filterOption = FilterOption.TODO
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Completed") },
                        onClick = {
                            filterOption = FilterOption.COMPLETED
                            expanded = false
                        }
                    )
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) return@Button

                    val item = TodoItem(title = title, description = desc)
                    coroutineScope.launch {
                        viewModel.insert(item)
                        title = ""
                        desc = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "TASKS:", fontSize = 30.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

        HorizontalDivider()

        LazyColumn {
            items(todoItems) { item ->
                TodoItemCard(
                    item = item,
                    onToggle = { updated ->
                        coroutineScope.launch {
                            viewModel.update(updated)
                        }
                    },
                    onDelete = {
                        coroutineScope.launch {
                            viewModel.delete(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItemCard(
    item: TodoItem,
    onToggle: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Checkbox(
                    checked = item.isDone,
                    onCheckedChange = {
                        onToggle(item.copy(isDone = it))
                    }
                )
            }

            if (!item.description.isNullOrBlank()) {
                Text(text = item.description ?: "", fontSize = 16.sp)
            }

            Button(
                onClick = { onDelete(item) },
                modifier = Modifier.align(Alignment.End).offset(y = (-8).dp)
            ) {
                Text("Delete")
            }
        }
    }
}

enum class FilterOption {
    ALL, TODO, COMPLETED
}
