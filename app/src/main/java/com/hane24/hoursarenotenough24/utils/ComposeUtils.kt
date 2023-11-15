package com.hane24.hoursarenotenough24.utils

import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.hane24.hoursarenotenough24.R

fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed(
    factory = {
        this.then(
            Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = { onClick() }
            )
        )
    }
)

@Composable
fun NumberPicker(
    minValue: Int,
    maxValue: Int,
    value: Int,
    displayedValues: Array<String>,
    modifier: Modifier = Modifier,
    setOnValueChangedListener: (Int) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.number_picker, null)
            val numberPicker = view.findViewById<NumberPicker>(R.id.number_picker_view)
            numberPicker.apply {
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = value
                this.displayedValues = displayedValues
                this.setOnValueChangedListener { _, _, new ->
                    setOnValueChangedListener(new)
                }
            }
        },
        update = { numberPicker ->
            numberPicker.displayedValues = null
            numberPicker.minValue = minValue
            numberPicker.maxValue = maxValue
            numberPicker.value = value
            numberPicker.displayedValues = displayedValues
            numberPicker.setOnValueChangedListener { _, _, new ->
                setOnValueChangedListener(new)
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun NumberPickerPreview() {
    Row {
        NumberPicker(
            minValue = 0,
            maxValue = 1,
            value = 1,
            displayedValues = arrayOf("2022", "2023")
        )
        NumberPicker(
            minValue = 0,
            maxValue = 28,
            value = 1,
            displayedValues = Array(31) { "${it + 1}" }
        )
    }
}
