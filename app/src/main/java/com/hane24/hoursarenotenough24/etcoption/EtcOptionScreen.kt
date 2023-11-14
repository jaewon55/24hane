package com.hane24.hoursarenotenough24.etcoption

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hane24.hoursarenotenough24.R


@Composable
private fun EtcOptionItem(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 13.dp, horizontal = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = null,
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier.defaultMinSize(minWidth = 24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = text),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.inter_medium)),
            color = colorResource(id = R.color.etc_text)
        )
    }
}

@Composable
fun EtcOptionScreen(modifier: Modifier = Modifier) {
    var openLicenseDialog by remember { mutableStateOf(false) }

    if (openLicenseDialog) {
        LicenseDialog(onDismissRequest = { openLicenseDialog = false })
    }

    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.etc_option_title),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.inter_medium)),
            color = colorResource(id = R.color.etc_title_color),
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(28.dp))
        EtcOptionItem(drawable = R.drawable.ic_card, text = R.string.card_option, onClick = {
            /* TODO 재발급 신청 페이지 */
        })
        EtcOptionItem(drawable = R.drawable.ic_book, text = R.string.etc_information, onClick = {
            /* TODO 지원금 지침 안내 webpage */
        })
        EtcOptionItem(drawable = R.drawable.ic_complain, text = R.string.etc_complain, onClick = {
            /* TODO 출입기록 문의 webpage */
        })
        EtcOptionItem(drawable = R.drawable.ic_guide, text = R.string.etc_guide, onClick = {
            /* TODO 이용 가이드 webpage */
        })
        EtcOptionItem(drawable = R.drawable.ic_feedback, text = R.string.etc_feedback, onClick = {
            /* TODO 앱 피드백 webpage */
        })
        EtcOptionItem(
            drawable = R.drawable.ic_license,
            text = R.string.etc_license,
            onClick = { openLicenseDialog = true })
        EtcOptionItem(drawable = R.drawable.ic_logout, text = R.string.logout, onClick = {
            /* TODO 로그아웃 */
        })
        Divider(color = Color(red = 0xD8, green = 0xD8, blue = 0xD8, alpha = 0x42))
        Spacer(modifier = Modifier.height(18.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_copyright),
            contentDescription = "copyright",
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun EtcOptionItemPreview() {
    EtcOptionItem(
        drawable = R.drawable.ic_card,
        text = R.string.card_option,
        onClick = {}
    )
}

@Composable
@Preview(showBackground = true)
private fun EtcOptionScreenPreView() {
    EtcOptionScreen()
}


