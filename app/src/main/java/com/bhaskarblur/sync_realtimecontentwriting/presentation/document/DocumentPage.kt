package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.ContentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor

@Composable
fun DocumentPage(viewModel: DocumentViewModel) {
    val configuration = LocalConfiguration.current
    val data by viewModel.documentData

    viewModel.initDocument("Playground")
    val contributorScrollState = rememberLazyListState()
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Row(
                Modifier
                    .padding(horizontal = 32.dp)
                    .height(64.dp)
                    .clickable { }
                    .background(Color(0xFF151516), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.undo_icon),
                    contentDescription = "Undo changes",
                    tint = Color.White,
                    modifier =  Modifier.height(22.dp)
                )

                Spacer(modifier = Modifier.width(18.dp))
                Icon(
                    painter = painterResource(id = R.drawable.redo_icon),
                    contentDescription = "Undo changes",
                    tint = Color.White,
                   modifier =  Modifier.height(22.dp)
                )

                Spacer(modifier = Modifier.width(18.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                    ),
                    onClick = {

                    },
                    modifier = Modifier
                        .background(Color(0xFF6105E2), RoundedCornerShape(80.dp))
                ) {

                    Text(text = "✨ Write with AI!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
    ) { it
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xff1b1b1c)),
        ) {

            if(!data.documentId.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .background(Color(0xFF151516))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            data.documentId ?: "Document",
                            color = Color.White, fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        LazyRow(
                            Modifier
                                .scrollable(contributorScrollState, Orientation.Horizontal)
                                .widthIn(max = configuration.screenWidthDp.dp / 3)
                        ) {
                            items(
                                key = { user ->
                                    user.userDetails?.id!!
                                },
                                items = data.liveCollaborators
                                    ?: listOf<UserModelCursor>()
                            ) {
                                ContributorsItems(item = it)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    TextField(

                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        value = data.content?.content ?: "",
                        onValueChange = { value ->
                            data.content?.content = value
                            viewModel.updateContent(value, value.length)
                        },
                        placeholder = {
                            Text(
                                "Write what you want or use help of AI ✨",
                                fontSize = 16.sp, fontWeight = FontWeight.Medium,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White
                        ),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            else {
                Column(Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.then(Modifier.size(42.dp))
                    )
                }
            }
        }
    }
}