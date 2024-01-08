package com.hane24.hoursarenotenough24.ui.reissue

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.network.BASE_URL
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.repository.ReissueRepository
import com.hane24.hoursarenotenough24.utils.LoadingAnimation
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.clickableWithoutRipple


@Composable
private fun ReissueHeader(
    backButtonOnClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "back button",
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier
                .clickableWithoutRipple {
                    backButtonOnClick()
                }
                .padding(top = 18.dp, bottom = 18.dp, end = 36.dp)
                .size(30.dp)
        )
        Text(
            text = "카드 재발급 신청",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorResource(id = R.color.etc_title_color)
        )
        Spacer(
            modifier = Modifier
                .padding(top = 18.dp, bottom = 18.dp, start = 36.dp)
                .size(30.dp)
        )
    }
}

@Composable
private fun ReissueState(
    @DrawableRes drawable: Int,
    @StringRes description: Int,
    @StringRes additionalDescription: Int,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(painter = painterResource(id = drawable), contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = stringResource(id = description),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = stringResource(id = additionalDescription),
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ReissueStateCard(
    loadingState: Boolean,
    state: ReissueState
) {
    Card(
        backgroundColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            LoadingAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (loadingState) 1f else 0f)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 30.dp)
                    .alpha(if (loadingState) 0f else 1f)
            ) {
                ReissueState(
                    drawable = if (state == ReissueState.APPLY) R.drawable.ic_reissue_apply_yes else R.drawable.ic_reissue_apply_no,
                    description = R.string.reissue_apply_description,
                    additionalDescription = R.string.reissue_apply_additional_description
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xD9, 0xD9, 0xD9),
                    modifier = Modifier.width(50.dp)
                )
                ReissueState(
                    drawable = if (state == ReissueState.IN_PROGRESS) R.drawable.ic_reissue_make_yes else R.drawable.ic_reissue_make_no,
                    description = R.string.reissue_progress_description,
                    additionalDescription = R.string.reissue_progress_additional_description
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xD9, 0xD9, 0xD9),
                    modifier = Modifier.width(50.dp)
                )
                ReissueState(
                    drawable = if (state == ReissueState.PICK_UP_REQUESTED) R.drawable.ic_reissue_end_yes else R.drawable.ic_reissue_end_no,
                    description = R.string.reissue_end_description,
                    additionalDescription = R.string.reissue_end_additional_description
                )
            }
        }
    }
}

@Composable
private fun ReissueApplyButton(
    loadingState: Boolean,
    state: ReissueState,
    apply: () -> Unit,
    finish: () -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        ReissueDialog(
            isApply = state != ReissueState.PICK_UP_REQUESTED,
            onDismissRequest = { openDialog = false },
            onConfirmation = if (state != ReissueState.PICK_UP_REQUESTED) apply else finish
        )
    }
    Button(
        onClick = { openDialog = true },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.front_gradient_end)
        ),
        enabled = !loadingState && !(state == ReissueState.APPLY || state == ReissueState.IN_PROGRESS),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (state == ReissueState.PICK_UP_REQUESTED) "데스크 카드수령 완료" else "카드 신청하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
    }
}

@Composable
fun ReissueScreen(
    viewModel: ReissueViewModel,
    backButtonOnClick: () -> Unit,
) {
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun openWebpage(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(
            horizontal = 30.dp,
            vertical = 20.dp
        ).verticalScroll(scrollState)
    ) {
        ReissueHeader(backButtonOnClick = backButtonOnClick)
        Text(
            text = "재발급 신청 방법",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.etc_title_color),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "자세히 보기",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.log_list_background),
            modifier = Modifier
                .clickable {
                    openWebpage(BASE_URL + "redirect/reissuance_guidelines")
                }
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.overview_curr_status_color),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(vertical = 12.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "재발급 신청 현황",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.etc_title_color),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        ReissueStateCard(loadingState = loadingState, state = viewModel.reissueState)
        Spacer(modifier = Modifier.height(32.dp))
        ReissueApplyButton(
            loadingState = loadingState,
            state = viewModel.reissueState,
            apply = viewModel::reissueApply,
            finish = viewModel::reissueFinish
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ReissueHeaderPreview() {
    ReissueHeader(backButtonOnClick = {})
}

@Composable
@Preview(showBackground = true)
private fun ReissueStatePreview() {
    ReissueState(
        drawable = R.drawable.ic_reissue_apply_no,
        description = R.string.reissue_apply_description,
        additionalDescription = R.string.reissue_apply_additional_description
    )
}

@Composable
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReissueStateCardPreview() {
    ReissueStateCard(false, ReissueState.IN_PROGRESS)
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReissueScreenPreview() {
    ReissueScreen(
        ReissueViewModel(
            ReissueRepository(
                Hane24Apis.hane24ApiService,
                SharedPreferenceUtils.initialize(LocalContext.current)
            )
        ),
        backButtonOnClick = {}
    )
}

