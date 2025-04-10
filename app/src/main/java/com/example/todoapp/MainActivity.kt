package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
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

enum class Filters {
    ALL, TODO, COMPLETED
}

@Composable
fun MainScreen(viewModel: MainActivityViewModel) {
    val coroutineScope = rememberCoroutineScope()

    var filter by remember { mutableStateOf(Filters.ALL) }

    val todoItems by when (filter) {
        Filters.ALL -> viewModel.allItems
        Filters.TODO -> viewModel.getItemsByCompletion(false)
        Filters.COMPLETED -> viewModel.getItemsByCompletion(true)
    }.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
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
                            filter = Filters.ALL
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Pending") },
                        onClick = {
                            filter = Filters.TODO
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Completed") },
                        onClick = {
                            filter = Filters.COMPLETED
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

        Text(
            text = "TASKS",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

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
                    },
                    onUpdate = { updated ->
                        coroutineScope.launch { viewModel.update(updated) }
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
    onDelete: (TodoItem) -> Unit,
    onUpdate: (TodoItem) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(item.title) }
    var editDesc by remember { mutableStateOf(item.description ?: "") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { isEditing = true },
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                    )
                } else {
                    Text(
                        text = item.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Checkbox(
                    checked = item.isDone,
                    onCheckedChange = {
                        onToggle(item.copy(isDone = it))
                    }
                )
            }

            if (isEditing) {
                OutlinedTextField(
                    value = editDesc,
                    onValueChange = { editDesc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (!item.description.isNullOrBlank()) {
                Text(text = item.description!!)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { isEditing = false }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (editTitle.isNotBlank()) {
                                onUpdate(item.copy(title = editTitle, description = editDesc))
                                isEditing = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            } else {
                Button(
                    onClick = { onDelete(item) },
                    modifier = Modifier
                        .align(Alignment.End)
                        .offset(y = (-15).dp)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}