package com.bhaskarblur.sync_realtimecontentwriting.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.DocumentItem
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun MyDocumentsPage(
    userViewModel: SignUpViewModel,
    documentViewModel: DocumentViewModel
) {

    val searchText = remember {
        mutableStateOf("")
    }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(62.dp)
                .background(colorSecondary)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "My Documents", style = TextStyle(
                    textColorPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                )
            )
        }

        Column(Modifier.padding(18.dp)) {

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Access the documents made by you here.",
                fontSize = 18.sp,
                color = textColorPrimary,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(1f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (documentViewModel.eventFlow.collectAsState(null)
                    .value == UIEvents.ShowCreateLoading("1")
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = primaryColor, modifier = Modifier.size(36.dp),
                    )
                }
            }
            else {
                Button(modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .border(2.dp, color = textColorPrimary, RoundedCornerShape(90.dp))
                    .background(color = Color.Transparent, RoundedCornerShape(90.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    onClick = documentViewModel::createDocument,
                    content = {

                        Text(
                            "+  Create new document", color = textColorPrimary, fontSize = 15.sp
                        )
                    })

            }

            Spacer(modifier = Modifier.height(18.dp))

            TextField(
                shape = RoundedCornerShape(10.dp),
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                },
                placeholder = {
                    Text(
                        text = "Search for documents...",
                        fontSize = 14.sp,
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.sp
                ),
                suffix = {
                    Icon(
                        Icons.Filled.Search, contentDescription = "",
                        tint = textColorPrimary, modifier = Modifier.size(24.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = textColorPrimary,
                    focusedTextColor = textColorPrimary,
                    focusedPlaceholderColor = textColorSecondary,
                    unfocusedPlaceholderColor = textColorSecondary,
                    focusedContainerColor = colorSecondary,
                    unfocusedContainerColor = colorSecondary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(shape = RoundedCornerShape(90.dp), color = colorSecondary)
            )


            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 18.dp)
            ) {
                items(items = when (searchText.value.isNotEmpty()) {
                    true -> documentViewModel.userDocuments.reversed()
                        .filter {
                            it.documentName?.lowercase()
                                ?.contains(searchText.value.lowercase()) == true ||
                                    it.documentId?.lowercase()
                                        ?.contains(searchText.value.lowercase()) == true ||
                                    it.content?.content?.lowercase()
                                        ?.contains(searchText.value.lowercase()) == true || "No Title".lowercase()
                                .contains(searchText.value.lowercase())
                        }

                    false -> documentViewModel.userDocuments.reversed()
                },
                    key = {
                        it.documentId ?: ""
                    }) { doc ->
                    DocumentItem(documentModel = doc, onDelete = {
                        documentViewModel.deleteDocument(doc.documentId ?: "")
                    }, onItemClick = {
                        documentViewModel.emitUIEvent(
                            UIEvents.DocumentCodeApplied(
                                doc.documentId ?: ""
                            )
                        )
                    }, onShare = {
                        documentViewModel.emitUIEvent(UIEvents.ShareDocument(doc.documentId ?: ""))
                    },   isMyDocument = documentViewModel.
                    isMyDocument(doc.createdBy),
                        context = LocalContext.current)
                }
                item{
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

        }
    }
}