package com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.documentsHome

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.Constants
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities.DocumentActivity
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.documentsHome.widgets.DocumentItem
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.TabScreens
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DocumentsList(
    userViewModel: SignUpViewModel, documentViewModel: DocumentViewModel, context: Context,
    navController: NavController
) {

    val isLoading = rememberSaveable {
        mutableStateOf(true)
    }

    val documentCode = remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(documentViewModel.userDocuments) {
        if (documentViewModel.userDocuments.size < 1) {
            documentViewModel.getUserDocuments()
            delay(2500)
        }
        isLoading.value = false
    }

    Column(Modifier.fillMaxSize()) {
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
                "Hi ${userViewModel.userState.value.fullName} 👋", style = TextStyle(
                    textColorPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                )
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {

            if (isLoading.value) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = primaryColor, modifier = Modifier.size(42.dp)
                    )
                }
            } else {

                TextField(
                    shape = RoundedCornerShape(10.dp),
                    value = documentCode.value,
                    onValueChange = {
                        documentCode.value = it
                    },
                    placeholder = {
                        Text(
                            text = "Enter document code you want to access...",
                            fontSize = 15.sp
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 15.sp
                    ),
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
                        .background(shape = RoundedCornerShape(90.dp), color = colorSecondary),
                    trailingIcon = {
                        if (documentViewModel.eventFlow.collectAsState(null)
                                .value == UIEvents.ShowCodeLoading("1")
                        ) {
                            CircularProgressIndicator(
                                color = primaryColor, modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                Icons.Filled.Send,
                                contentDescription = "",
                                tint = when (documentCode.value) {
                                    "" -> Color.Gray
                                    else -> {
                                        Color.White
                                    }
                                },
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        if (documentCode.value.isNotEmpty()) {
                                            scope.launch {
                                                val isValidCode =
                                                    documentViewModel.getDocumentById(documentCode.value)

                                                if (isValidCode) {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            DocumentActivity::class.java
                                                        )
                                                    intent.putExtra(
                                                        "documentId",
                                                        documentCode.value
                                                    )
                                                    context.startActivity(intent)
                                                } else {
                                                    userViewModel.event.value =
                                                        "Incorrect document code"
                                                }
                                            }
                                        }
                                    }
                            )
                        }
                    })

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Or you can",
                    textAlign = TextAlign.Center, color = textColorSecondary, fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))
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
                } else {
                    Button(modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(2.dp, color = textColorPrimary, RoundedCornerShape(90.dp))
                        .background(color = Color.Transparent, RoundedCornerShape(90.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        onClick = {
                            documentViewModel.createDocument()
                        },
                        content = {

                            Text(
                                "+  Create new document", color = textColorPrimary, fontSize = 15.sp
                            )
                        })

                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Your documents",
                        color = textColorPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(Icons.Filled.Search,
                        contentDescription = "Search", tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigate(TabScreens.SearchDocumentPage.route)
                            })

                }

                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(items = documentViewModel.userDocuments.reversed(),
                        key = {
                            it.documentId ?: ""
                        }) { doc ->
                        DocumentItem(documentModel = doc, onDelete = {
                            documentViewModel.deleteDocument(doc.documentId ?: "")
                        }, onItemClick = {
                            val intent = Intent(context, DocumentActivity::class.java)
                            intent.putExtra("documentId", doc.documentId)
                            context.startActivity(intent)
                        }, onShare = {
                            documentViewModel.emitUIEvent(
                                UIEvents.ShareDocument(
                                    doc.documentId ?: ""
                                )
                            )
                        }, context = context)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))

            }
        }
    }

}