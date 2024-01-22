package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.buildAnnotatedStringWithColors
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.findFirstDifferenceIndex
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.ContributorsItems
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.AlertDialogComponent
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.RichTextToolBox
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalRichTextApi::class
)
@Composable
fun DocumentPage(
    viewModel: DocumentViewModel,
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
    val backContentState = rememberRichTextState()
    val contentState = rememberRichTextState()

    contentState.setConfig(
        linkColor = Color.Blue,
        linkTextDecoration = TextDecoration.Underline,
        codeColor = Color.Cyan,
        codeBackgroundColor = Color.Black,
        codeStrokeColor = Color.Transparent,
    )
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
                false -> SheetValue.Hidden
                true -> SheetValue.Expanded
            }
        )
    )

    val showDialog = remember {
        mutableStateOf(false)
    }
    if (showDialog.value) {
        AlertDialogComponent(context,
            title = "Are you sure you want to clear history?",
            bodyMsg = "Clearing the prompts history will clear it for everyone, you can't undo this change.",
            onYesPressed = {
                showDialog.value = false
                viewModel.clearPromptHistory()
            },
            yesLabel = "Yes",
            onClose = {
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
        contentState.setHtml(data.content?.content.toString())
        content.value = TextFieldValue(
            text = contentState.annotatedString.text,
            selection = content.value.selection
        )
    }


    LaunchedEffect(contentState.selection) {
        viewModel.updateCursorPosition(
            contentState
                .selection.end
        )
    }
    LaunchedEffect(contentState.annotatedString.text) {

        Log.d(
            "isSame", data.content?.content?.equals(contentState.toHtml()).toString()
        )
        if (data.content?.content?.equals(contentState.toHtml()) == false) {
            content.value = TextFieldValue(
                text = contentState.annotatedString.text,
                selection = contentState.selection
            )
            val position = findFirstDifferenceIndex(
                content.value.text, data.content?.content ?: ""
            )
            if (position > 0) {
                viewModel.handleUndoRedoStack(backContentState.toHtml())
                viewModel.updateContent(
                    contentState.toHtml(),
                    contentState.selection.start
                )
            } else {
                viewModel.handleUndoRedoStack(backContentState.toHtml())
                viewModel.updateContent(
                    contentState.toHtml(),
                    contentState.selection.end
                )
            }
            backContentState.setHtml(contentState.toHtml())
        }
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
        bottomBar = {
            RichTextToolBox(contentState,
                onBoldClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleSpanStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)

                },
                onItalicClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleSpanStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onUnderlineClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onLineThroughClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextFontChange = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextSizeClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextColorClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextBgClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextShadowClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextLeftClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState
                        .toggleParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Left
                            )
                        )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextCenterClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState
                        .toggleParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Center
                            )
                        )
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onTextRightClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    val cursorPos = contentState.selection.end
                    contentState
                        .toggleParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Right
                            )
                        )

                    contentState.selection = TextRange(cursorPos)
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onUnOrderedListClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleUnorderedList()
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onOrderedListClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.toggleOrderedList()
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onCodeClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    contentState.addCodeSpan()
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onLetterSpacingClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                },
                onLineHeightClick = {
                    viewModel.handleUndoRedoStack(contentState.toHtml())
                    // TBD
                    viewModel.updateContent(contentState.toHtml(),
                        contentState.selection.end)
                })
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        it

        BottomSheetScaffold(
            sheetContent = {
                AIBottomSheet(
                    viewModel = viewModel,
                    data = data,
                    onAddMessage = { msg ->
                        viewModel.handleUndoRedoStack(contentState.toHtml())
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
                        // Need to test this msg whether it will affect UI
                        contentState.setText(
                            contentState
                                .toHtml().plus(" $tempContent")
                        )
                        contentState.selection = TextRange(
                            contentState.annotatedString.text.length
                        )
                        content.value =
                            TextFieldValue(
                                contentState.annotatedString.text,
                                TextRange(
                                    contentState.annotatedString.text.length
                                )
                            )
                        viewModel.updateContent(
                            contentState.toHtml(),
                            contentState.selection.end
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
                ) {
                    showDialog.value = true
                }

            },
            containerColor = colorSecondary,
            sheetContainerColor = colorSecondary,
            scaffoldState = sheetState,
            content = {

                Column(
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    verticalArrangement = Arrangement.Top
                ) {

                    if (!data.documentId.isNullOrEmpty() && dataGot.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
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
                                    text = "✨ AI",
                                    color = textColorPrimary,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier
                                        .background(
                                            primaryColor,
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
                                            tint = textColorPrimary,
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
                                            tint = textColorSecondary,
                                            modifier = Modifier.height(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    if (redoStack.value.size > 0) {

                                        Icon(
                                            painter = painterResource(id = R.drawable.redo_icon),
                                            contentDescription = "Redo changes",
                                            tint = textColorPrimary,
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
                                            tint = textColorSecondary,
                                            modifier = Modifier.height(24.dp)
                                        )
                                    }

                                }

                                Row {
                                    Icon(Icons.Filled.Comment,
                                        contentDescription = "Put comment",
                                        tint = textColorPrimary,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .clickable {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Comments coming soon", Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                    )

                                    Spacer(Modifier.width(12.dp))

                                    Icon(Icons.Filled.Share,
                                        contentDescription = "Share comment",
                                        tint = textColorPrimary,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .clickable {
                                                viewModel.emitUIEvent(
                                                    UIEvents.ShareDocument(
                                                        viewModel.documentData.value.documentId
                                                            ?: ""
                                                    )
                                                )
                                            }
                                    )
                                }
                            }

                            Column {
                                if (data.liveCollaborators?.isNotEmpty() == true) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(end = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TextField(
                                            modifier = Modifier
                                                .fillMaxWidth(0.6f),
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
                                                unfocusedTextColor = textColorPrimary,
                                                focusedTextColor = textColorPrimary,
                                                unfocusedPlaceholderColor = textColorSecondary,
                                                focusedPlaceholderColor = textColorSecondary,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                cursorColor = Color.White
                                            ),
                                            textStyle = TextStyle(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 20.sp,
                                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                            )
                                        )

                                        Column {
                                            Spacer(modifier = Modifier.height(12.dp))

                                        LazyRow(
                                            modifier = Modifier
                                                .scrollable(
                                                    contributorScrollState,
                                                    Orientation.Horizontal
                                                )
                                        ) {
                                            items(
                                                key = { user ->
                                                    user.userDetails?.id!!
                                                },
                                                items = data.liveCollaborators
                                                    ?: listOf()
                                            ) {
                                                ContributorsItems(item = it, onClick = { pos ->
                                                    if (pos != -1) {
                                                        contentState.selection = TextRange(pos)
                                                    }
                                                })
                                            }
                                        }
                                        }
                                    }
                                }

                                Box {
                                    TextField(
                                        readOnly = true,
                                        shape = RoundedCornerShape(10.dp),
                                        interactionSource = contentSource,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(),
                                        value = content.value,
                                        onValueChange = {},
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
                                                    it.userDetails?.id!! != viewModel.userDetails.value.id
                                                } ?: listOf(), content.value.text),
                                                OffsetMapping.Identity
                                            )

                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            unfocusedTextColor = Color.Transparent,
                                            focusedTextColor = Color.Transparent,
                                            unfocusedPlaceholderColor = textColorSecondary,
                                            focusedPlaceholderColor = textColorSecondary,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.Transparent
                                        ),
                                        textStyle = TextStyle(
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp,
                                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                            lineHeight = 22.sp
                                        ),
                                    )

                                    RichTextEditor(
                                        state = contentState,
                                        shape = RoundedCornerShape(10.dp),
                                        interactionSource = contentSource,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(),
                                        colors = RichTextEditorDefaults.richTextEditorColors(
                                            containerColor = Color.Transparent,
                                            placeholderColor = textColorSecondary,
                                            textColor = textColorPrimary,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = textColorPrimary
                                        ),
                                        textStyle = TextStyle(
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp,
                                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                            lineHeight = 22.sp
                                        ),
                                    )
                                }

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
                                color = primaryColor,
                                modifier = Modifier.then(Modifier.size(42.dp))
                            )
                        }
                    }
                }

            })

    }

}