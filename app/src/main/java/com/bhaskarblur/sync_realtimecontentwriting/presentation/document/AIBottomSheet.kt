package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.PromptItem
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary
import kotlinx.coroutines.launch

@Composable
fun AIBottomSheet(
    viewModel: DocumentViewModel, data: DocumentModel, onAddMessage: (msg: String) -> Unit,
    onClearHistory: () -> Unit
) {

    val ctnScope = rememberCoroutineScope()
    val promptScrollState = rememberLazyListState()
    val promptFieldText = remember {
        mutableStateOf("")
    }
    val ignoreOldHistory = remember { mutableStateOf(false) }
    val promptList = remember {
        mutableStateOf(viewModel.documentData.value.promptsList)
    }

    LaunchedEffect(key1 = data.promptsList) {
        Log.d("newPromptAdded","yes")
        promptList.value = data.promptsList?: arrayListOf()
        promptScrollState.animateScrollToItem(promptList.value!!.size)
    }

    val configuration = LocalConfiguration.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorSecondary)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Collaborative content with AI", color = textColorPrimary,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Use AI to generate content & use it in your board, you & other users can see each other's prompts ",
            color = textColorSecondary,
            style = TextStyle(
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Prompts history",
                color = textColorPrimary,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            )

            Text(
                text = "Clear history",
                color = textColorPrimary,
                textAlign = TextAlign.End,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    onClearHistory()
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(state = promptScrollState,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(max = configuration.screenHeightDp.dp / 2)) {
            items(promptList.value?: listOf(),
                key = {
                    it.timeStamp
                }) { msgModel ->
                PromptItem(msgModel) { msg ->
                    if (msg.isNotEmpty()) {
                        onAddMessage(msg)
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = ignoreOldHistory.value,
                onCheckedChange = { ignoreOldHistory.value = it },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = textColorPrimary,
                    uncheckedColor = textColorPrimary,
                    checkedColor =  primaryColor,
                )
            )
            Text(
                text = "Ignore chat history (checking this will send your new prompts without chat history.)",
                color = textColorPrimary,
                style = TextStyle(
                    fontSize = 13.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            shape = RoundedCornerShape(10.dp),
            value = promptFieldText.value,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = textColorPrimary,
                focusedTextColor = textColorPrimary,
                focusedPlaceholderColor = textColorSecondary,
                unfocusedPlaceholderColor = textColorSecondary,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(90.dp),
                    color = colorSecondary
                ),
            onValueChange = { value ->
                promptFieldText.value = value
            },
            placeholder = {
                Text("Say what you want to generate...")
            })


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ),
            onClick = {
                if(promptFieldText.value.isNotEmpty()) {
                viewModel.getGptSuggestions(promptFieldText.value, ignoreOldHistory.value)
                promptFieldText.value = ""
                ctnScope.launch {
                    data.promptsList?.size?.let { it1 ->
                        promptScrollState.animateScrollToItem(
                            it1
                        )
                    }
                }
                }
            },
            modifier = Modifier
                .background(primaryColor, RoundedCornerShape(80.dp))
                .fillMaxWidth()
        ) {
            Text(
                text = "✨ Generate",
                color = textColorPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}