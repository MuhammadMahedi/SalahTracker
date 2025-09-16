package com.example.salahtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salahtracker.R
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR

@Composable
fun ItemDailySummery(day: DailySalah) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            day.apply {

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(2f) // 👈 gives twice the space
                )

                SalahStatusItem(FAZR, fajr, modifier = Modifier.weight(1f))
                SalahStatusItem(ZUHR, zuhr, modifier = Modifier.weight(1f))
                SalahStatusItem(ASR, asr, modifier = Modifier.weight(1f))
                SalahStatusItem(MAGHRIB, maghrib, modifier = Modifier.weight(1f))
                SalahStatusItem(ISHA, isha, modifier = Modifier.weight(1f))
            }


        }


    }

}

@Composable
fun SalahStatusItem(name: String, prayerStatus: PrayerStatus, modifier: Modifier = Modifier) {
    val icon: ImageVector = when (prayerStatus) {
        PrayerStatus.Attend -> ImageVector.vectorResource(id = R.drawable.ic_done)
        PrayerStatus.Miss -> ImageVector.vectorResource(id = R.drawable.ic_miss)
        PrayerStatus.Qaza -> ImageVector.vectorResource(id = R.drawable.ic_qaza)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            modifier = Modifier.size(30.dp),
            tint = Color.Unspecified // important: keeps original SVG color
        )
        Text(text = name, fontSize = 10.sp)
    }
}



@Preview
@Composable
fun ItemDailySummeryPreview() {
    ItemDailySummery(
        DailySalah(
            "23 May 2023",
            PrayerStatus.Miss,
            PrayerStatus.Attend,
            PrayerStatus.Qaza,
            PrayerStatus.Attend,
            PrayerStatus.Attend
        )
    )
}


