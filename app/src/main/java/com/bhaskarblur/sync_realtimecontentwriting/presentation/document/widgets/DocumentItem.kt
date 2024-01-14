package com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.UiUtils
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.AlertDialogComponent
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.chatBoxColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary

@Composable
fun DocumentItem(documentModel: DocumentModel, onItemClick : () -> Unit, onDelete : () -> Unit,
                 context:Context) {

    val showDeleteDialog = remember {
        mutableStateOf(false)
    }

    val clipBoard = LocalClipboardManager.current

    if(showDeleteDialog.value) {
        AlertDialogComponent(
            context = context,
            title = "Delete document?",
            bodyMsg = "Are you sure, you want to delete this document?",
            yesLabel = "Delete",
            onYesPressed = {
                onDelete()
            },
            onClose = {
                showDeleteDialog.value = false
            })
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(chatBoxColor, RoundedCornerShape(14.dp))
            .clickable {
                onItemClick()
            }
            .padding(16.dp)
            ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text (
                modifier = Modifier.fillMaxWidth(0.8f),
                text =
                when(documentModel.documentName) {
                    "" -> "No Title"
                    else -> {documentModel.documentName.toString()}
                }, color = textColorPrimary,
                fontSize = 17.sp, fontWeight = FontWeight.SemiBold
            )

            Icon(Icons.Filled.Delete,
                contentDescription = "Delete", tint = Color.Red,
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        showDeleteDialog.value = true
                    } )

        }
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(text = "Document code: ${documentModel.documentId.toString()}",
                color = textColorPrimary, fontSize = 13.sp)

            Spacer(modifier = Modifier.width(8.dp))

            Icon(Icons.Filled.ContentCopy, "Copy code",
                tint = textColorPrimary, modifier = Modifier.size(16.dp)
                .clickable {
                    clipBoard.setText(AnnotatedString(documentModel.documentId.toString()))
                    Toast.makeText(context, "Code Copied", Toast.LENGTH_SHORT).show()
                })
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Created on: ${UiUtils.getDate(documentModel.creationDateTime.toString())}",
            color = textColorPrimary, fontSize = 13.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))

}