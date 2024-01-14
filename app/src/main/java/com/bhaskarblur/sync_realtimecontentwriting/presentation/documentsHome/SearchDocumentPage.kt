package com.bhaskarblur.sync_realtimecontentwriting.presentation.documentsHome

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities.DocumentActivity
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.DocumentItem
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.chatBoxColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun SearchDocumentsPage(
    documentViewModel: DocumentViewModel, navController: NavController, context: Context
) {

    val searchText = remember {
        mutableStateOf("")
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorSecondary)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Icon(
                Icons.Filled.Close,
                tint = textColorPrimary,
                contentDescription = "Back",
                modifier = Modifier.clickable {
                    navController.popBackStack()
                })
            Spacer(Modifier.width(12.dp))
            TextField(
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                },
                placeholder = {
                    Text(
                        text = "Search...",
                        fontSize = 14.sp,
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.sp
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = textColorPrimary,
                    focusedTextColor = textColorPrimary,
                    focusedPlaceholderColor = textColorSecondary,
                    unfocusedPlaceholderColor = textColorSecondary,
                    focusedContainerColor = chatBoxColor,
                    unfocusedContainerColor = chatBoxColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(shape = RoundedCornerShape(90.dp), color = colorSecondary)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxSize()
            .padding(horizontal = 18.dp)) {
            Text(
                text = "Search results will be shown below, you can search with title, document code and even the content inside the document.",
                fontSize = 13.sp,
                color = textColorSecondary,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 18.dp)
            ) {
                items(items = documentViewModel.userDocuments.reversed()
                    .filter {
                        it.documentName?.lowercase()?.contains(searchText.value.lowercase()) == true ||
                                it.documentId?.lowercase()?.contains(searchText.value.lowercase()) == true ||
                                it.content?.content?.lowercase()?.contains(searchText.value.lowercase()) == true
                    },
                    key = {
                        it.documentId ?: ""
                    }) { doc ->
                    DocumentItem(documentModel = doc, onDelete = {
                        documentViewModel.deleteDocument(doc.documentId ?: "")
                    }, onItemClick = {
                        val intent = Intent(context, DocumentActivity::class.java)
                        intent.putExtra("documentId", doc.documentId)
                        context.startActivity(intent)
                    }, context = context)
                }
            }
        }
    }
}