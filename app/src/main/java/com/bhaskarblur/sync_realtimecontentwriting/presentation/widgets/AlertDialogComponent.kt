package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun AlertDialogComponent(context: Context,title : String, bodyMsg : String, yesLabel: String,onYesPressed: () -> Unit,
                         onClose : () -> Unit) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {

        AlertDialog(

            onDismissRequest = {
                openDialog.value = false
                onClose()
            },
            title = { Text(text = title, color = Color.White) },

            text = {
                Text(
                    bodyMsg,
                    color = Color.White
                )
            },


            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onYesPressed()
                        onClose()
                    }
                ) {
                    Text(yesLabel, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onClose()
                    }
                ) {
                    // adding text to our button.
                    Text("No", color = Color.White)
                }
            },
            containerColor = Color(0xFF191A1B),
            textContentColor = Color.White
        )

    }
}