package com.wegielek.katanaflashlight.presentation.ui.views.katana

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KatanaSlider(
    name: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    contentDescription: String,
    onValueChange: (Float) -> Unit,
) {
    Text(
        text = name,
        color = Color.White,
        fontSize = 20.sp,
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
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(0.75f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Slider(
            value = value,
            onValueChange = { onValueChange(it) },
            valueRange = valueRange,
            steps = steps,
            enabled = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .semantics {
                        this.contentDescription = contentDescription
                    },
            colors =
                SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
                    activeTickColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTickColor = MaterialTheme.colorScheme.tertiary,
                ),
        )
    }
    Spacer(modifier = Modifier.padding(10.dp))
}
