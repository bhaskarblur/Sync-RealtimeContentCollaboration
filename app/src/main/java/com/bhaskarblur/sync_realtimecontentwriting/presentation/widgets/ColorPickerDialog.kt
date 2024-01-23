package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import android.util.Patterns
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerDialog(onSetColor : (color: Color) -> Unit, onResetColor : () -> Unit,
                      onCloseClick : () -> Unit) {

    val controller = rememberColorPickerController()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorSecondary, RoundedCornerShape(36.dp))
            .padding(all = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(Icons.Filled.Close, "Close",
                tint = textColorPrimary, modifier = Modifier.size(24.dp)
                    .clickable {
                        onCloseClick()
                    })
//            AlphaTile(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .clip(RoundedCornerShape(6.dp)),
//                controller = controller
//            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            controller = controller,
            onColorChanged = {

            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            controller = controller,
            tileOddColor = Color.White,
            tileEvenColor = Color.Black
        )
        Spacer(modifier = Modifier.height(24.dp))
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            controller = controller,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth()) {

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
                onClick = {
                          onResetColor()

                },
            ) {
                Text("Reset color", fontSize = 16.sp, color = Color.Black)

            }

            Spacer(modifier = Modifier.width(14.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = primaryColor
                ),
                onClick = {
                    onSetColor(controller.selectedColor.value)
                },
            ) {
                Text("Select Color", fontSize = 16.sp, color = textColorPrimary,
                    textAlign = TextAlign.Center)

            }
        }
    }
}