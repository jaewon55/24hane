package com.hane24.hoursarenotenough24.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hane24.hoursarenotenough24.R

@Composable
@Preview
fun PopulationCard(inOut: Boolean = true) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Text(
                "서울",
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = colorResource(R.color.default_text),
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    "321",
                    modifier = Modifier.alignByBaseline(),
                    fontSize = TextUnit(20.0f, TextUnitType.Sp),
                    color = colorResource(R.color.default_text),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "명",
                    modifier = Modifier.alignByBaseline(),
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = colorResource(R.color.default_text),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}