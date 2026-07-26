package org.holypresenter_songs.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.planner.PlannerItem
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter.platform.ui.workspace.HolySidePane

@Composable
fun PlannerSidePane(
    plannerService: PlannerService?,
    modifier: Modifier = Modifier,
    onItemClick: (
        item: PlannerItem,
        index: Int
    ) -> Unit = { _, _ -> }
) {
    val items = plannerService
        ?.state
        ?.items
        .orEmpty()

    val activeItemIndex = plannerService
        ?.state
        ?.activeItemIndex

    HolySidePane(
        title = "План служения",
        modifier = modifier
    ) {
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
                    plannerService?.move(
                        fromIndex = fromIndex,
                        toIndex = toIndex
                    )
                }
            ) { item, index, _ ->
                val active = activeItemIndex == index

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = if (active) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    tonalElevation = if (active) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(item, index)
                            }
                            .padding(
                                start = 12.dp,
                                end = 4.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. ${item.title}",
                            modifier = Modifier.weight(1f),
                            color = if (active) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )

                        TextButton(
                            onClick = {
                                plannerService?.remove(item)
                            }
                        ) {
                            Text("×")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}