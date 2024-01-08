package com.hane24.hoursarenotenough24.ui.home.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.ui.home.GraphInfo
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.floor


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(backgroundColor = 0x000000)
fun TimeGraphViewPager(
    modifier: Modifier = Modifier, graphInfo: List<GraphInfo> = listOf(
        GraphInfo(
            listOf(
                22990,
                223639,
                164895,
                195015,
                135654,
                142993
            ),
            false
        ),
        GraphInfo(
            listOf(
                446713,
                705377,
                466280,
                153657,
                464216,
                375666
            ),
            true
        )
    )
) {
    val pageState = rememberPagerState { 2 }


    val coroutineScope = rememberCoroutineScope()
    val moveToPage = { page: Int ->
        coroutineScope.launch {
            pageState.animateScrollToPage(page)
        }
        Unit
    }
    var selectedIdx by remember {
        mutableStateOf<Int?>(null)
    }
    val onClick = { idx: Int ->
        selectedIdx = idx
    }

        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(vertical = 28.dp, horizontal = 20.dp)) {
                HorizontalPager(state = pageState, pageSpacing = 20.dp) { page ->
                    Column {
                        GraphTitle(page)
                        Spacer(modifier = Modifier.height(12.dp))
                        GraphInfoCard(page, graphInfo, selectedIdx ?: 0)
                        TimeGraph(page, selectedIdx ?: 0, onClick, graphInfo)
                    }
                }
                DotPageIndicator(onClick = moveToPage, currentPage = pageState.currentPage)
            }
        }
}

@Composable
fun GraphTitle(page: Int) {
    val title = if (page == 0) "최근 주간 그래프" else "최근 월간 그래프"
    val dateTitle = if (page == 0) " (6주)" else " (6달)"

    val titleTextStyle =
        TextStyle(fontSize = TextUnit(14.0f, TextUnitType.Sp), fontWeight = FontWeight.Bold)
    val dateTextStyle = TextStyle(
        fontSize = TextUnit(14.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        color = Color(0xff9b9797)
    )

    Row {
        Text(text = title, style = titleTextStyle)
        Text(text = dateTitle, style = dateTextStyle)
    }
}

@Composable
fun GraphInfoCard(page: Int, graphInfo: List<GraphInfo>, selectedIdx: Int) {
    val textStyle = TextStyle(
        color = Color.White,
        fontSize = TextUnit(12.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold
    )

    Column {
        Card(backgroundColor = Color(0xff333333), shape = RoundedCornerShape(10.dp)) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = graphInfo[page].parseDateText(selectedIdx, Calendar.getInstance()),
                    style = textStyle
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(
                            "총 %d시간", floor(
                                graphInfo[page].calculateTotalTime(
                                    selectedIdx
                                )
                            ).toInt()
                        ),
                        style = textStyle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(
                            "평균 %.1f시간",
                            graphInfo[page].calculateAverageTime(selectedIdx)
                        ), style = textStyle
                    )
                }
            }
        }
    }

}

@Composable
fun TimeGraph(page: Int, selectedIdx: Int, onClick: (Int) -> Unit, graphInfo: List<GraphInfo>) {
    val textStyle = TextStyle(
        fontSize = TextUnit(12.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
        color = Color(0xff9b9797)
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            GraphIndicator(0 == selectedIdx)
            GraphIndicator(1 == selectedIdx)
            GraphIndicator(2 == selectedIdx)
            GraphIndicator(3 == selectedIdx)
            GraphIndicator(4 == selectedIdx)
            GraphIndicator(5 == selectedIdx)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {

            GraphItem(
                idx = 0,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(0)
            )
            GraphItem(
                idx = 1,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(1)
            )
            GraphItem(
                idx = 2,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(2)
            )
            GraphItem(
                idx = 3,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(3)
            )
            GraphItem(
                idx = 4,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(4)
            )
            GraphItem(
                idx = 5,
                onClick = onClick,
                selectedIdx = selectedIdx,
                height = graphInfo[page].calculateGraphHeight(5)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "최신순", style = textStyle)
            Text(text = "오래된 순", style = textStyle)
        }
    }
}

@Composable
fun GraphItem(onClick: (Int) -> Unit, idx: Int, selectedIdx: Int, height: Int) {
    Column(
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(), indication = null
        ) { onClick(idx) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        GraphBar(idx == selectedIdx, height)
    }
}

@Composable
fun GraphIndicator(isSelected: Boolean) {
    Crossfade(targetState = isSelected, label = "graph_indicator_anim") { isSelect ->
        when (isSelect) {
            true -> Image(
                painter = painterResource(id = R.drawable.ic_reverse_triangle),
                contentDescription = "graph_arrow"
            )

            false -> Image(
                painter = painterResource(id = R.drawable.ic_reverse_triangle),
                alpha = 0.0f,
                contentDescription = "invisible_graph_arrow"
            )
        }
    }
}

@Composable
fun GraphBar(isSelected: Boolean, height: Int) {
    val newGradientColors = arrayOf(
        0.1167f to Color(0xFF9AC3F4), 0.8656f to Color(0xFF735BF2)
    )

    val gradient = remember {
        Brush.linearGradient(
            *newGradientColors,
            start = Offset(Float.POSITIVE_INFINITY, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        )
    }

    val animatedValue = remember { Animatable(1.0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            animatedValue.animateTo(targetValue = 1.0f,
                animationSpec = keyframes {
                    durationMillis = 200
                    1.0f at 0
                    1.1f at 100
                    1.0f at 200
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .graphicsLayer {
                this.scaleX = animatedValue.value
                this.scaleY = animatedValue.value
            }
            .wrapContentSize()
            .background(gradient, shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
    ) {
        Spacer(
            modifier = Modifier
                .size(width = 25.dp, height = height.dp)

        )
    }
}

@Composable
fun DotPageIndicator(onClick: (Int) -> Unit, currentPage: Int) {
    val selectedColor = Color(0xff735BF2)
    val defaultColor = Color(0xffd9d9d9)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onClick(0) },
            shape = CircleShape,
            modifier = Modifier.size(6.dp, 6.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (currentPage == 0) selectedColor else defaultColor)
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onClick(1) },
            shape = CircleShape,
            modifier = Modifier.size(6.dp, 6.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (currentPage == 1) selectedColor else defaultColor)
        ) {}
    }
}