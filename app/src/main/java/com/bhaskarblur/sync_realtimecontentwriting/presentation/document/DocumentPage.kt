package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.buildAnnotatedStringWithColors
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.findFirstDifferenceIndex
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp.SignUpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DocumentPage(
    viewModel: DocumentViewModel,
    userViewModel: SignUpViewModel,
    context : Context
) {
    val configuration = LocalConfiguration.current
    val data by viewModel.documentData
    val gptData by viewModel.gptData
    val undoStack = remember {
        mutableStateOf(viewModel.undoStack)
    }
    val redoStack = remember {
        mutableStateOf(viewModel.redoStack)
    }
    val dataGot = remember {
        mutableStateOf(false)
    }
    val title = remember {
        mutableStateOf(data.documentName ?: "")
    }
    val content = remember {
        mutableStateOf(TextFieldValue(data.content?.content ?: ""))
    }
    val contentSource = remember {
        MutableInteractionSource()
    }
    val promptFieldText = remember {
        mutableStateOf("")
    }
    val contributorScrollState = rememberLazyListState()
    val ctnScope = rememberCoroutineScope()

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Hidden
        )
    )
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = viewModel.redoStack) {
        redoStack.value = viewModel.redoStack

    }
    LaunchedEffect(key1 = viewModel.undoStack) {
        undoStack.value = viewModel.undoStack
    }
    LaunchedEffect(key1 = data.content?.content) {
        if (!dataGot.value) {
            delay(1200)
            dataGot.value = true
        } else {
            delay(10)
        }
        content.value = TextFieldValue(
            text = data.content?.content.toString(),
            selection = content.value.selection
        )
    }

    LaunchedEffect(key1 = data.documentName) {
        if (!dataGot.value) {
            delay(1200)
            dataGot.value = true
        } else {
            delay(10)
        }
        title.value = data.documentName.toString()
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
//            if (!data.documentId.isNullOrEmpty()) {
//            Row(
//                Modifier
//                    .padding(vertical = 8.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Button(
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                    ),
//                    onClick = {
//                        ctnScope.launch {
//                            if(sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
//                                sheetState.bottomSheetState.expand()
//                            }
//                        }
//                    },
//                    modifier = Modifier
//                        .background(Color(0xFF6105E2), RoundedCornerShape(80.dp))
//                ) {
//
//                    Text(
//                        text = "✨ Write with AI!",
//                        color = Color.White,
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//            }
//            }

        },
    ) {
        it
        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF151516))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Write with AI", color = Color.White,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Use AI to generate content & use it in your board.",
                        color = Color.Gray,
                        style = TextStyle(
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(value = promptFieldText.value,
                        colors = TextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedContainerColor = Color(0xff1b1b1c),
                            unfocusedContainerColor = Color(0xff1b1b1c),
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
                                color = Color(0xFF151516)
                            ),
                        onValueChange = { value ->
                            promptFieldText.value = value
                        },
                        placeholder = {
                            Text("Say what you want to generate...")
                        })

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(value = gptData.content,
                        colors = TextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedContainerColor = Color(0xff1b1b1c),
                            unfocusedContainerColor = Color(0xff1b1b1c),
                            disabledContainerColor = Color(0xff1b1b1c),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        ),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                shape = RoundedCornerShape(90.dp),
                                color = Color(0xFF151516)
                            ),
                        suffix = {
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = "Add to Text",
                                tint = Color.White,
                                modifier = Modifier
                                    .height(24.dp)
                                    .clickable {
                                        if (gptData.content.isNotEmpty()) {
                                            val tempContent = content.value.text
                                                .substring(
                                                    0,
                                                    content.value.selection.start
                                                )
                                                .plus(gptData.content)
                                                .plus(
                                                    content.value.text.substring(
                                                        content.value.selection.start,
                                                        content.value.text.length
                                                    )
                                                )
                                            content.value =
                                                TextFieldValue(
                                                    tempContent,
                                                    TextRange(content.value.text.length)
                                                )
                                            viewModel.handleUndoRedoStack(content.value.text)
                                            viewModel.updateContent(
                                                content.value.text,
                                                content.value.selection.start
                                            )
                                            Toast
                                                .makeText(
                                                    context, "Text pasted to board",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            ctnScope.launch {
                                                sheetState.bottomSheetState.hide()
                                                TextFieldValue(
                                                    content.value.text,
                                                    content.value.selection
                                                )
                                            }
                                        }
                                    }
                            )
                        },
                        placeholder = {
                            Text("Results will be shown here")
                        })

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        ),
                        onClick = {
                            viewModel.getGptSuggestions(promptFieldText.value)
                        },
                        modifier = Modifier
                            .background(Color(0xFF6105E2), RoundedCornerShape(80.dp))
                            .fillMaxWidth()
                    ) {

                        Text(
                            text = "✨ Generate",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            },
            sheetContainerColor = Color(0xFF151516),
            scaffoldState = sheetState
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xff1b1b1c)),
                verticalArrangement = Arrangement.Top
            ) {

                if (!data.documentId.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(62.dp)
                                .background(Color(0xFF151516))
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Text(
                                text = "✨ AI",
                                color = Color.White,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .background(
                                        Color(0xFF6105E2),
                                        RoundedCornerShape(90.dp)
                                    )
                                    .clickable {
                                        ctnScope.launch {
                                            keyboardController?.hide()
                                            if (sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
                                                sheetState.bottomSheetState.expand()
                                            } else {
                                                sheetState.bottomSheetState.hide()
                                            }
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (undoStack.value.size > 0) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.undo_icon),
                                        contentDescription = "Undo changes",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .clickable {
                                                viewModel.undoChanges()
                                            }
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.undo_icon),
                                        contentDescription = "Undo changes",
                                        tint = Color.Gray,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                if (redoStack.value.size > 0) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.redo_icon),
                                        contentDescription = "Redo changes",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .clickable {
                                                viewModel.redoChanges()
                                            }
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.redo_icon),
                                        contentDescription = "Redo changes",
                                        tint = Color.Gray,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                            }
                            LazyRow(
                                Modifier
                                    .scrollable(contributorScrollState, Orientation.Horizontal)
                                    .widthIn(max = configuration.screenWidthDp.dp / 2)
                            ) {
                                items(
                                    key = { user ->
                                        user.userDetails?.id!!
                                    },
                                    items = data.liveCollaborators
                                        ?: listOf<UserModelCursor>()
                                ) {
                                    ContributorsItems(item = it, onClick = { pos ->
                                        if (pos != -1) {
                                            content.value = TextFieldValue(
                                                text = content.value.text,
                                                selection = TextRange(pos)
                                            )
                                        }
                                    })
                                }
                            }
                        }

                        Column {
                            Spacer(modifier = Modifier.height(6.dp))
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp),
                                singleLine = true,
                                value = title.value,
                                onValueChange = { value ->
                                    title.value = value
                                    viewModel.updateTitle(value)
                                },
                                placeholder = {
                                    Text(
                                        "Title",
                                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
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
                                    fontSize = 20.sp,
                                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                    lineHeight = 22.sp
                                )
                            )

                            TextField(
                                interactionSource = contentSource,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .padding(0.dp),
                                value = content.value,
                                onValueChange = { value ->
                                    content.value = value
                                    Log.d("cursorPos", value.selection.start.toString())
                                    val position = findFirstDifferenceIndex(
                                        content.value.text, data.content?.content ?: ""
                                    )
                                    if (position > 0) {
                                        viewModel.handleUndoRedoStack(value.text)
                                        viewModel.updateContent(value.text, value.selection.start)
                                    } else {
                                        viewModel.handleUndoRedoStack(value.text)
                                        viewModel.updateContent(
                                            value.text,
                                            content.value.text.length
                                        )
                                    }
                                },
                                placeholder = {
                                    Text(
                                        "Write what you want or use help of AI ✨",
                                        fontSize = 16.sp, fontWeight = FontWeight.Normal,
                                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                                    )
                                },
                                visualTransformation = {
                                    TransformedText(
                                        buildAnnotatedStringWithColors(data.liveCollaborators?.filter {
                                            it.userDetails?.id!! != userViewModel.userState.value.id
                                        } ?: listOf(), content.value.text),
                                        OffsetMapping.Identity
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
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                    lineHeight = 22.sp
                                ),
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                } else {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6105E2),
                            modifier = Modifier.then(Modifier.size(42.dp))
                        )
                    }
                }
            }

        }
    }

}