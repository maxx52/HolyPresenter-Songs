package org.holypresenter_songs.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter.platform.ui.workspace.HolySidePane

@Composable
fun PlannerSidePane(
    plannerService: PlannerService?,
    modifier: Modifier = Modifier
) {
    HolySidePane(
        title = "План служения",
        modifier = modifier
    ) {
        val items = plannerService?.state?.items.orEmpty()

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Добавьте песню в план",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            HolyReorderColumn(
                items = items,
                modifier = Modifier.weight(1f),
                onMove = { fromIndex, toIndex ->
                    plannerService?.move(fromIndex, toIndex)
                }
            ) { item, index, _ ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}. ${item.title}",
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = {
                            plannerService?.remove(item)
                        }
                    ) {
                        Text("×")
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}