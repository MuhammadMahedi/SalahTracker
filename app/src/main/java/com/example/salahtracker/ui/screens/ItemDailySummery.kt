package com.example.salahtracker.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.salahtracker.R
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.ui.MainViewModel
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR
@Composable
fun ItemDailySummery(day: DailySalah,mainViewModel: MainViewModel,onShowSheet: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = colorResource(R.color.borderBg), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            day.apply {

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .background(
                            color = colorResource(R.color.cardBg),
                            shape = RoundedCornerShape(8.dp)
                        )

                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterStart).padding(horizontal = 4.dp, vertical = 10.dp)
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            /*.background(
                                color = colorResource(R.color.ic_launcher_background),
                                shape = CircleShape
                            )*/
                            .padding(4.dp)
                            .clickable { onShowSheet(date) }
                    )
                }



                SalahStatusItem(mainViewModel,day,FAZR, fajr, modifier = Modifier.weight(1f))
                SalahStatusItem(mainViewModel,day,ZUHR, zuhr, modifier = Modifier.weight(1f))
                SalahStatusItem(mainViewModel,day,ASR, asr, modifier = Modifier.weight(1f))
                SalahStatusItem(mainViewModel,day,MAGHRIB, maghrib, modifier = Modifier.weight(1f))
                SalahStatusItem(mainViewModel,day,ISHA, isha, modifier = Modifier.weight(1f))
            }


        }


    }

}

@Composable
fun SalahStatusItem(viewModel: MainViewModel, day: DailySalah,name: String, prayerStatus: PrayerStatus, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val icon: ImageVector = when (prayerStatus) {
        PrayerStatus.Done -> ImageVector.vectorResource(id = R.drawable.ic_done)
        PrayerStatus.Miss -> ImageVector.vectorResource(id = R.drawable.ic_miss)
        PrayerStatus.Qaza -> ImageVector.vectorResource(id = R.drawable.ic_qaza)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.clickable(onClick = {
            if(prayerStatus == PrayerStatus.Miss){
                showDialog = true
            }
        })
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            modifier = Modifier.size(30.dp),
            tint = Color.Unspecified // important: keeps original SVG color
        )
        Text(text = name, fontSize = 10.sp)
    }

    ChangeStatusDialog(viewModel,day,showDialog = showDialog,name) {
        showDialog = false
    }
}

@Composable
fun ChangeStatusDialog(viewModel: MainViewModel,day: DailySalah, showDialog: Boolean, salahName: String, onClose: () -> Unit) {
    val ctx = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text("Mark as Qaza!") },
            text = { Text("Have you completed this Qaza prayer and want to update the status from Miss to Qaza?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(ctx, "Status changed!", Toast.LENGTH_SHORT).show()
                        val updatedDay = setPrayerToQaza(day,salahName)
                        Log.e( "ChangeStatusDialog: ", updatedDay.toString())
                        viewModel.updateDay(updatedDay)
                        onClose()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4CAF50)) // green
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(ctx, "Cancelled", Toast.LENGTH_SHORT).show()
                        onClose()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // disabled color
                    )
                ) {
                    Text("No")
                }
            }
        )
    }
}


//update the prayerStatus to QAZA from MISS for the selected prayer
fun setPrayerToQaza(day : DailySalah,prayerKey: String): DailySalah = when (prayerKey) {
    FAZR -> day.copy(fajr = PrayerStatus.Qaza)
    ZUHR -> day.copy(zuhr = PrayerStatus.Qaza)
    ASR -> day.copy(asr = PrayerStatus.Qaza)
    MAGHRIB -> day.copy(maghrib = PrayerStatus.Qaza)
    ISHA -> day.copy(isha = PrayerStatus.Qaza)
    else -> day
}
