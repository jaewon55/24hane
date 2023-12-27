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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hane24.hoursarenotenough24.R

@Composable
@Preview
fun OverviewScreen() {
//    val viewModel: OverViewViewModel = viewModel(
//        factory = OverViewModelFactory(
//            App.sharedPreferenceUtilss,
//            UserRepository(Hane24Apis.hane24ApiService, App.sharedPreferenceUtilss)
//        )
//    )
//    val profileUrl by viewModel.profileImageUrl.collectAsState()
    val inOut = false
    val scrollState = rememberScrollState()
    val m1 = "입실 중 이용 시간은 \n실제 기록과 다를 수 있습니다."
    val s1 = "입실 / 퇴실 태깅에 유의해주세요."
    val m2 = "인정 시간은 지원금 산정 시\n반영 되는 시간입니다."
    val s2 = "1일 최대 12시간"

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.overview_in_color))
        .verticalScroll(scrollState)
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
                "https://cdn.intra.42.fr/users/cf44b163dd848ae2308f4b1b4fd4b6ed/seongjki.JPG",
                inOut
            )
            TimeCardView(
                todayAccumulationTime = ((3600L * 1) + (60L * 0)) * 1000,
                dayTargetTime = 12,
                inTimeStamp = 1703577908187L,
                monthAccumulationTime = "0" to "5",
                monthAcceptedTime = "0" to "5",
                tagLatencyNotice = m1 to s1,
                fundInfoNotice = m2 to s2,
                {}
            )
            Spacer(modifier = Modifier.height(22.dp))
            TimeGraphViewPager()
            Spacer(modifier = Modifier.height(22.dp))
            PopulationCard(inOut)
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OverviewProfile(profileUrl: String, inout: Boolean) {
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
                text = "seongjki",
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