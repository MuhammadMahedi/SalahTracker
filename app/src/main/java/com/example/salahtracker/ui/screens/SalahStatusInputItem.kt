package com.example.salahtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.salahtracker.domain.model.PrayerStatus

@Composable
fun SalahStatusInputItem(
    title: String,
    selectedStatus: PrayerStatus,
    onStatusChange: (PrayerStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp,vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prayer name
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.20f)
        )

        // Status options
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(0.80f)
        ) {
            // Done
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)   // Equal space
            ) {
                RadioButton(
                    selected = selectedStatus == PrayerStatus.Done,
                    onClick = { onStatusChange(PrayerStatus.Done) },
                    modifier.size(20.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green,
                        unselectedColor = Color.Gray
                    )
                )
                Text("Done",modifier = Modifier.padding(start = 8.dp))
            }

            // Qaza
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)   // Equal space
            ) {
                RadioButton(
                    selected = selectedStatus == PrayerStatus.Qaza,
                    onClick = { onStatusChange(PrayerStatus.Qaza) },
                    modifier = Modifier.size(20.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Yellow,
                        unselectedColor = Color.Gray
                    )
                )
                Text("Qaza",modifier = Modifier.padding(start = 8.dp))
            }

            // Miss
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)   // Equal space
            ) {
                RadioButton(
                    selected = selectedStatus == PrayerStatus.Miss,
                    onClick = { onStatusChange(PrayerStatus.Miss) },
                    modifier.size(20.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Gray
                    )
                )
                Text("Miss",modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SalahStatusInputItem("Magrib", PrayerStatus.Done, onStatusChange = {})
}