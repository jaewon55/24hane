package com.hane24.hoursarenotenough24.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed(
    factory = {
        this.then(
            Modifier.clickable(
                indication = null,
                interactionSource = remember{ MutableInteractionSource() },
                enabled = enabled,
                onClick = { onClick() }
            )
        )
    }
)

