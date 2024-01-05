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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.buildAnnotatedStringWithColors
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.findFirstDifferenceIndex
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.AlertDialogComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DocumentPage(
    viewModel: DocumentViewModel,
    userViewModel: SignUpViewModel,
    context: Context
) {
    val configuration = LocalConfiguration.current
    val data by viewModel.documentData
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
    val contributorScrollState = rememberLazyListState()
    val ctnScope = rememberCoroutineScope()

    val sheetIsOpen = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            confirmValueChange = {
                sheetIsOpen.value = it == SheetValue.Expanded

                true
            },
            skipPartiallyExpanded = false,
            initialValue = when (sheetIsOpen.value) {
                false -> SheetValue.PartiallyExpanded
                true -> SheetValue.Expanded
            }
        )
    )

    val showDialog = remember {
        mutableStateOf(false)
    }
    if (showDialog.value) {
        AlertDialogComponent(context, title = "Are you sure you want to clear history?",
            bodyMsg = "Clearing the prompts history will clear it for everyone, you can't undo this change.", onYesPressed = {
                showDialog.value = false
                viewModel.clearPromptHistory()
            }, yesLabel = "Yes", onClose = {
                showDialog.value = false;
            })
    }

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
    ) {
        it

        BottomSheetScaffold(
            sheetContent = {
                AIBottomSheet(
                    context = context,
                    viewModel = viewModel,
                    data = data,
                    onHideSheet = { msg ->
                        val tempContent = content.value.text
                            .substring(
                                0,
                                content.value.selection.start
                            )
                            .plus(msg)
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
                    },
                    onClearHistory = {
                        showDialog.value = true
                    })

            },
            containerColor = Color(0xFF151516),
            sheetContainerColor = Color(0xFF151516),
            scaffoldState = sheetState,
            content = {
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
                                            Log.d("pressed", "yes")
                                            ctnScope.launch {
                                                keyboardController?.hide()
                                                if (sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
                                                    sheetState.bottomSheetState.expand()
                                                    sheetIsOpen.value = true
                                                } else {
                                                    sheetState.bottomSheetState.hide()
                                                    sheetIsOpen.value = false
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
                                            viewModel.updateContent(
                                                value.text,
                                                value.selection.start
                                            )
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

            })

    }

}