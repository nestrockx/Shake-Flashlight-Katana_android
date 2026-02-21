package com.wegielek.katanaflashlight.presentation.ui.views.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaBackground
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaHyperlinkText
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaIconButton

@Composable
fun AboutScreen(navigateToLanding: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        KatanaBackground()
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            KatanaIconButton(
                painterResource(id = R.drawable.ic_arrow_back),
                stringResource(R.string.back_icon_button),
            ) { navigateToLanding() }
            Column(
                modifier =
                    Modifier
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter),
            ) {
                KatanaHyperlinkText(
                    fullText = "Icon assets from https://www.onlinewebfonts.com/icon used under CC BY 4.0",
                    linkText = listOf("https://www.onlinewebfonts.com/icon"),
                    hyperlinks = listOf("https://www.onlinewebfonts.com/icon"),
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    fontSize = 16.sp,
                    linkTextColor = MaterialTheme.colorScheme.primary,
                    contentDescription = "https://www.onlinewebfonts.com/icon link",
                )
                Text(
                    text = stringResource(R.string.version),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                )
            }
        }
    }
}
