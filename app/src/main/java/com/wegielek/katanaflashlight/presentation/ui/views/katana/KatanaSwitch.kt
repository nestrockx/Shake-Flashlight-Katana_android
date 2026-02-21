package com.wegielek.katanaflashlight.presentation.ui.views.katana

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KatanaSwitch(
    name: String,
    checked: Boolean,
    contentDescription: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    Text(
        text = name,
        fontSize = 20.sp,
        color = Color.White,
        textAlign = TextAlign.Left,
        modifier =
            Modifier
                .fillMaxWidth(),
    )
    Spacer(modifier = Modifier.size(4.dp))
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(0.75f),
                ).padding(end = 8.dp),
    ) {
        Switch(
            checked = checked,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                ),
            onCheckedChange = { onCheckedChange(it) },
            enabled = true,
            modifier =
                Modifier.align(Alignment.CenterEnd).semantics {
                    this.contentDescription = contentDescription
                },
        )
    }
    Spacer(modifier = Modifier.size(10.dp))
}
