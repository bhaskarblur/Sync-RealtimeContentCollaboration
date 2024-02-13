package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.UIValuesConstant
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.buildAnnotatedStringWithColors
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.findFirstDifferenceIndex
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.utils.BottomSheetType
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.ContributorsItems
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.AlertDialogComponent
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.ColorPickerDialog
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.FontDropDown
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.RichTextToolBox
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.UnitDropDown
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

@SuppressLint("MutableCollectionMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalRichTextApi::class
)
@Composable
fun DocumentPage(
    viewModel: DocumentViewModel,
    context: Context
) {
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
    val contentChangedFromType = remember {
        mutableStateOf(false)
    }
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
    val bottomSheetType  = remember {
        mutableStateOf(BottomSheetType.AiChatBottomSheet)
    }

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
                showDialog.value = false
            })
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(data.documentId) {
        viewModel.addToRecentDocuments()
    }
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
        }
        Log.d("undoRedoDone", viewModel.hasDoneUndoRedo.value.toString() )
        if (contentState.annotatedString.text.isNotEmpty()) {
            if (!data.content?.lastEditedBy.equals(viewModel.userDetails.value.id) || viewModel.hasDoneUndoRedo.value) {
                contentChangedFromType.value = false
                if (contentState.annotatedString.text != data.content?.content.toString()) {
                    contentState.setHtml(data.content?.content.toString())
                    content.value = TextFieldValue(
                        text = contentState.annotatedString.text,
                        selection = content.value.selection
                    )
                }
            }
        } else {
                contentChangedFromType.value = false
                if (contentState.annotatedString.text != data.content?.content.toString()) {
                    contentState.setHtml(data.content?.content.toString())
                    content.value = TextFieldValue(
                        text = contentState.annotatedString.text,
                        selection = content.value.selection
                    )
                }
        }
        viewModel.hasDoneUndoRedo.value = false
    }

    LaunchedEffect(contentState.selection) {
        viewModel.updateCursorPosition(
            contentState
                .selection.end
        )
    }
    LaunchedEffect(contentState.annotatedString.text) {
        if (contentChangedFromType.value) {
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
        contentChangedFromType.value = true
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
            val isColorPopupExpanded = remember {
                mutableStateOf(false)
            }
            val isUnitDropDownExpanded = remember {
                mutableStateOf(false)
            }
            val unitDropDownType = remember {
                mutableStateOf("")
            }
            val isFontDropDownExpanded = remember {
                mutableStateOf(false)
            }
            val colorPopupType = remember {
                mutableStateOf("")
            }

            if (sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
                Box {
                    RichTextToolBox(contentState,
                        onBoldClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.toggleSpanStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )

                        },
                        onItalicClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.toggleSpanStyle(
                                SpanStyle(
                                    fontStyle = FontStyle.Italic
                                )
                            )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onUnderlineClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                                    contentState.toggleSpanStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onLineThroughClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.toggleSpanStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onTextSizeClick = {
                            if (isUnitDropDownExpanded.value) {
                                isUnitDropDownExpanded.value = false
                                colorPopupType.value = ""
                            } else {
                                isUnitDropDownExpanded.value = true
                                unitDropDownType.value = "textSize"
                            }
                        },
                        onTextColorClick = {
                            isUnitDropDownExpanded.value = false
                            isColorPopupExpanded.value = true
                            colorPopupType.value = "textColor"
                        },
                        onTextBgClick = {
                            isUnitDropDownExpanded.value = false
                            isColorPopupExpanded.value = true
                            colorPopupType.value = "bgColor"
                        },
                        onTextLeftClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState
                                .toggleParagraphStyle(
                                    ParagraphStyle(
                                        textAlign = TextAlign.Left
                                    )
                                )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onTextCenterClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState
                                .toggleParagraphStyle(
                                    ParagraphStyle(
                                        textAlign = TextAlign.Center
                                    )
                                )
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
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
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onUnOrderedListClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.toggleUnorderedList()
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onOrderedListClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.toggleOrderedList()
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onCodeClick = {
                            viewModel.handleUndoRedoStack(contentState.toHtml())
                            contentState.addCodeSpan()
                            viewModel.updateContent(
                                contentState.toHtml(),
                                contentState.selection.end
                            )
                        },
                        onLetterSpacingClick = {
                            if (isUnitDropDownExpanded.value) {
                                isUnitDropDownExpanded.value = false
                                colorPopupType.value = ""
                            } else {
                                isUnitDropDownExpanded.value = true
                                unitDropDownType.value = "letterSpacing"
                            }
                        },
                        onLineHeightClick = {
                            if (isUnitDropDownExpanded.value) {
                                isUnitDropDownExpanded.value = false
                                colorPopupType.value = ""
                            } else {
                                isUnitDropDownExpanded.value = true
                                unitDropDownType.value = "lineHeight"
                            }
                        })

                    if (isFontDropDownExpanded.value) {
                        Column(
                            Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FontDropDown(
                                selectedValue = contentState.currentSpanStyle.fontFamily
                                    ?: FontFamily.Default,
                                listItems = UIValuesConstant.fontsList,
                                onClosed = {
                                    isFontDropDownExpanded.value = false
                                },
                                onSelected = {
                                    viewModel.handleUndoRedoStack(contentState.toHtml())
                                    contentState.toggleSpanStyle(
                                        SpanStyle(
                                            fontFamily = it
                                        )
                                    )
                                    viewModel.updateContent(
                                        contentState.toHtml(),
                                        contentState.selection.end
                                    )
                                }
                            )
                        }
                    }

                    if (isUnitDropDownExpanded.value) {
                        Column(
                            Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UnitDropDown(
                                selectedValue = when (unitDropDownType.value) {
                                    "textSize" -> contentState.currentSpanStyle.fontSize.value.toInt()
                                    "lineHeight" -> contentState.currentParagraphStyle.lineHeight.value.toInt()
                                    "letterSpacing" -> contentState.currentSpanStyle.letterSpacing.value.toInt()
                                    else -> 0
                                },
                                listItems = UIValuesConstant.textSizeList,
                                onClosed = {
                                    isUnitDropDownExpanded.value = false
                                    unitDropDownType.value = ""
                                },
                                onSelected = {
                                    viewModel.handleUndoRedoStack(contentState.toHtml())

                                    when (unitDropDownType.value) {
                                        "textSize" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    fontSize = it
                                                )
                                            )
                                        }

                                        "lineHeight" -> {
                                            contentState.toggleParagraphStyle(
                                                ParagraphStyle(
                                                    lineHeight = it
                                                )
                                            )
                                        }

                                        "letterSpacing" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    letterSpacing = it
                                                )
                                            )
                                        }
                                    }
                                    viewModel.updateContent(
                                        contentState.toHtml(),
                                        contentState.selection.end
                                    )
                                }
                            )
                        }
                    }

                    if (isColorPopupExpanded.value) {
                        Column {
                            ColorPickerDialog(
                                onSetColor = {
                                    viewModel.handleUndoRedoStack(contentState.toHtml())
                                    when (colorPopupType.value) {
                                        "textColor" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    color = it
                                                )
                                            )
                                        }

                                        "bgColor" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    background = it
                                                )
                                            )
                                        }
                                    }
                                    viewModel.updateContent(
                                        contentState.toHtml(),
                                        contentState.selection.end
                                    )
                                    isColorPopupExpanded.value = false
                                    colorPopupType.value = ""
                                }, onResetColor = {
                                    viewModel.handleUndoRedoStack(contentState.toHtml())
                                    when (colorPopupType.value) {
                                        "textColor" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    color = Color.White
                                                )
                                            )
                                        }

                                        "bgColor" -> {
                                            contentState.toggleSpanStyle(
                                                SpanStyle(
                                                    background = Color.Transparent
                                                )
                                            )
                                        }

                                    }
                                    viewModel.updateContent(
                                        contentState.toHtml(),
                                        contentState.selection.end
                                    )
                                    isColorPopupExpanded.value = false
                                    colorPopupType.value = ""
                                }, onCloseClick = {
                                    isColorPopupExpanded.value = false
                                    colorPopupType.value = ""
                                })
                        }
                    }
                }
            }

        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        it

        BottomSheetScaffold(
            sheetContent = {

                when(bottomSheetType.value) {
                    BottomSheetType.AiChatBottomSheet -> {
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
                                contentState.setHtml(
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

                                viewModel.emitUIEvent(UIEvents.ShowSnackbar(
                                    "Text pasted to board"
                                ))
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
                    }
                    BottomSheetType.CommentBottomSheet -> {

                    }
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
                                            ctnScope.launch {
                                                keyboardController?.hide()
                                                if (sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
                                                    sheetState.bottomSheetState.expand()
                                                    sheetIsOpen.value = true
                                                    bottomSheetType.value = BottomSheetType.AiChatBottomSheet
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
                                                ctnScope.launch {
                                                    keyboardController?.hide()
                                                    if (sheetState.bottomSheetState.currentValue != SheetValue.Expanded) {
                                                        sheetState.bottomSheetState.expand()
                                                        sheetIsOpen.value = true
                                                        bottomSheetType.value = BottomSheetType.CommentBottomSheet
                                                    } else {
                                                        sheetState.bottomSheetState.hide()
                                                        sheetIsOpen.value = false
                                                    }
                                                }
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
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.SemiBold,
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
                                                ) {model ->
                                                    ContributorsItems(item = model, onClick = { pos ->
                                                        if (pos != -1) {
                                                            contentState.selection = TextRange(pos)
                                                        }
                                                    })
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
                                        value = content.value.text,
                                        onValueChange = {},
                                        visualTransformation = {
                                            try {
                                                TransformedText(
                                                    buildAnnotatedStringWithColors(
                                                        data.liveCollaborators?.filter {model ->
                                                            model.userDetails?.id!! != viewModel.userDetails.value.id
                                                        } ?: listOf(),
                                                        text = content.value.text
                                                    ),
                                                    OffsetMapping.Identity
                                                )
                                            } catch (e: Exception) {
                                                TransformedText(
                                                    buildAnnotatedStringWithColors(
                                                        data.liveCollaborators?.filter {model ->
                                                            model.userDetails?.id!! != viewModel.userDetails.value.id
                                                        } ?: listOf(),
                                                        text = content.value.text,
                                                    ),
                                                    OffsetMapping.Identity
                                                )
                                            }

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
                                        placeholder = {
                                            Text(
                                                "Write what you want or use help of AI ✨",
                                                fontSize = 16.sp, fontWeight = FontWeight.Normal,
                                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                                            )
                                        },
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