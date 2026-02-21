package com.wegielek.katanaflashlight.presentation.ui.views.katana

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun KatanaImageButton(
    painter: Painter,
    imageContentDescription: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
        modifier =
            Modifier.semantics {
                this.contentDescription = contentDescription
            },
    ) {
        Image(
            painter = painter,
            contentDescription = imageContentDescription,
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
        )
    }
}
