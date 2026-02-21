package com.wegielek.katanaflashlight.presentation.ui.views.katana

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun KatanaIconButton(
    painter: Painter,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Icon(
        tint = tint,
        painter = painter,
        contentDescription = contentDescription,
        modifier =
            Modifier
                .semantics {
                    this.contentDescription = contentDescription
                }.clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, color = MaterialTheme.colorScheme.onSurface),
                ).padding(8.dp)
                .size(50.dp)
                .padding(8.dp),
    )
}
