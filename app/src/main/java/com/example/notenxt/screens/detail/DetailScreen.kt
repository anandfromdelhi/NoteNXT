package com.example.notenxt.screens.detail

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notenxt.utils.Utils
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel?,
    noteId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()

    val isFormsNotBlank = detailUiState.note.isNotBlank() && detailUiState.title.isNotBlank()

    val selectedColor by animateColorAsState(
        targetValue = Utils.colors[detailUiState.colorIndex]
    )
    val isNoteIdNotBlank = noteId.isNotBlank()
    val icon = if (isFormsNotBlank) Icons.Default.Refresh else Icons.Default.Check

    LaunchedEffect(key1 = Unit) {
        if (isNoteIdNotBlank) {
            detailViewModel?.getNote(noteId)
        } else {
            detailViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isNoteIdNotBlank) {
                        detailViewModel?.updateNote(noteId)
                    } else {
                        detailViewModel?.addNote()
                    }
                }
            ) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = selectedColor)
                .padding(paddingValues)
        ) {
            if (detailUiState.noteAddedStatus) {

                Toast.makeText(
                    LocalContext.current, "Note added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                detailViewModel?.resetNoteAddedStatus()
                onNavigate.invoke()


            }
            if (detailUiState.updateNoteStatus) {

                Toast.makeText(
                    LocalContext.current, "Note updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                detailViewModel?.resetNoteAddedStatus()
                onNavigate.invoke()
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
            ) {
                itemsIndexed(Utils.colors) { colorIndex, color ->
                    ColorItem(color = color) {
                        detailViewModel?.onColorChange(colorIndex)
                    }
                }
            }
            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = {
                    detailViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.note, onValueChange = {
                    detailViewModel?.onNoteChange(it)
                },
                label = { Text(text = "Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )

        }


    }

}

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = color, shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable { onClick.invoke() },
        border = BorderStroke(2.dp, Color.Black)
    ) {


    }
}