package com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.profile

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun ProfilePage(
    userViewModel: SignUpViewModel,
    documentViewModel: DocumentViewModel
) {

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
                "Profile", style = TextStyle(
                    textColorPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                )
            )
        }

        Column(Modifier.fillMaxSize()
            .padding(18.dp)) {

            Icon(Icons.Filled.AccountCircle, contentDescription = "pfp",
                modifier = Modifier.size(84.dp), tint = textColorSecondary)
            Spacer(Modifier.height(8.dp))
            Text(
                userViewModel.userState.value.fullName?:"", style = TextStyle(
                    textColorPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                ))

                Spacer(Modifier.height(8.dp))

            Text(
                "@${userViewModel.userState.value.userName}", style = TextStyle(
                    textColorSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium
                ))
        }

    }
}