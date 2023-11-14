package com.hane24.hoursarenotenough24.etcoption

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hane24.hoursarenotenough24.R


@Composable
private fun LicenseContents(
    @StringRes name: Int,
    @StringRes license: Int,
) {
    Column {
        Text(
            text = stringResource(id = name),
            color = colorResource(id = R.color.default_text),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = license),
            color = colorResource(id = R.color.default_text),
            fontSize = 12.sp
        )
    }
}

@Composable
fun LicenseDialog(
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 11.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 11.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(licenseList) {
                        LicenseContents(name = it.first, license = it.second)
                    }
                }
                Button(
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.reissue_dialog_cancel)
                    ),
                    modifier = Modifier
                        .width(180.dp)
                ) {
                    Text(
                        text = "닫기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

private val licenseList = listOf(
    R.string.retrofit2 to R.string.license_retrofit2,
)

@Composable
@Preview(showBackground = true)
private fun LicenseContentsPreview() {
    LicenseContents(R.string.retrofit2, R.string.license_retrofit2)
}

@Composable
@Preview(showBackground = true)
private fun LicenseDialogPreView() {
    LicenseDialog(onDismissRequest = {})
}

