package com.hane24.hoursarenotenough24.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import java.util.Calendar

@Composable
fun OverviewScreen(viewModel: OverViewViewModel) {
    val mainInfo by viewModel.mainInfo.collectAsState()
    val inOut = mainInfo.inoutState == "IN"
    val graphInfo by viewModel.accumulationGraphInfo.collectAsState()
    val accumulationTimeInfo by viewModel.accumulationTime.collectAsState()
    val dayTargetTime by viewModel.dayTargetTime.collectAsState()
    val monthAccumulationTime by viewModel.monthAccumulationTime.collectAsState()
    val acceptedAccumulationTime by viewModel.acceptedAccumulationTime.collectAsState()
    val tagAt = mainInfo.tagAt.split("-", "T", ":", "Z").let {
        val calendar = Calendar.getInstance()
        calendar.set(
            it[0].toInt(),
            it[1].toInt(),
            it[2].toInt(),
            it[3].toInt(),
            it[4].toInt(),
            it[5].toDouble().toInt()
        )
        calendar.timeInMillis
    }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.overview_in_color))
    ) {
        if (inOut) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.in_background),
                contentDescription = "background",
                contentScale = ContentScale.FillHeight
            )
        }

        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OverviewProfile(
                mainInfo.login,
                mainInfo.profileImage,
                inOut
            )
            Column(Modifier.verticalScroll(scrollState)) {
                TimeCardView(
                    todayAccumulationTime = accumulationTimeInfo?.todayAccumulationTime ?: 0L,
                    dayTargetTime = dayTargetTime,
                    inTimeStamp = if (inOut) tagAt else null,
                    monthAccumulationTime = monthAccumulationTime,
                    monthAcceptedTime = acceptedAccumulationTime,
                    tagLatencyNotice = mainInfo.infoMessages.tagLatencyNotice.title to mainInfo.infoMessages.tagLatencyNotice.content,
                    fundInfoNotice = mainInfo.infoMessages.fundInfoNotice.title to mainInfo.infoMessages.fundInfoNotice.content
                ) {
                    viewModel.onClickSaveTargetTime(false, it)
                }
                Spacer(modifier = Modifier.height(22.dp))
                TimeGraphViewPager(graphInfo = graphInfo)
                Spacer(modifier = Modifier.height(22.dp))
                PopulationCard(inOut, mainInfo.gaepo)
                Spacer(modifier = Modifier.height(22.dp))
            }

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OverviewProfile(intraId: String, profileUrl: String, inout: Boolean) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        GlideImage(
            model = profileUrl,
            contentDescription = "profile_img",
            modifier = Modifier
                .size(width = 32.dp, height = 32.dp)
                .clip(CircleShape),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Row {
            Text(
                text = intraId,
                color = if (inout) colorResource(R.color.intra_id_in_color) else colorResource(R.color.intra_id_out_color),
                fontSize = TextUnit(20.0f, TextUnitType.Sp),
            )

            if (inout) {
                Box(
                    modifier = Modifier.padding(1.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.overview_inout_state_color))
                    )
                }
            }
        }
    }
}