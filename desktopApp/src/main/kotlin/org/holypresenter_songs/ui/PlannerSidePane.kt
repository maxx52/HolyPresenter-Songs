package org.holypresenter_songs.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.planner.PlannerInfo
import holypresenter.org.platform.api.planner.PlannerItem
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter.platform.ui.workspace.HolySidePane

@Composable
fun PlannerSidePane(
    plannerService: PlannerService?,
    modifier: Modifier = Modifier,
    onItemClick: (item: PlannerItem, index: Int) -> Unit = { _, _ -> }
) {
    val items = plannerService
        ?.state
        ?.items
        .orEmpty()

    val activeItemIndex = plannerService
        ?.state
        ?.activeItemIndex

    val currentPlanName = plannerService?.currentPlanName
    val availablePlans = plannerService?.plans.orEmpty()

    var showSaveAsDialog by remember {
        mutableStateOf(false)
    }

    var showOpenDialog by remember {
        mutableStateOf(false)
    }

    var showNewPlanDialog by remember {
        mutableStateOf(false)
    }

    var saveAsName by remember {
        mutableStateOf("")
    }

    var saveAsError by remember {
        mutableStateOf<String?>(null)
    }

    var planToRename by remember {
        mutableStateOf<PlannerInfo?>(null)
    }

    var renameName by remember {
        mutableStateOf("")
    }

    var renameError by remember {
        mutableStateOf<String?>(null)
    }

    var planToDelete by remember {
        mutableStateOf<PlannerInfo?>(null)
    }

    var deleteError by remember {
        mutableStateOf<String?>(null)
    }

    HolySidePane(
        title = "План служения",
        modifier = modifier
    ) {
        PlannerFileControls(
            currentPlanName = currentPlanName,
            enabled = plannerService != null,
            onNewPlan = {
                if (items.isEmpty()) {
                    plannerService?.newPlan()
                } else {
                    showNewPlanDialog = true
                }
            },
            onOpenPlan = {
                showOpenDialog = true
            },
            onSaveAs = {
                saveAsName = ""
                saveAsError = null
                showSaveAsDialog = true
            }
        )

        Spacer(Modifier.height(12.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Добавьте песню в план",
                    color = MaterialTheme
                        .colorScheme
                        .outline
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
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        } else {
                            MaterialTheme
                                .colorScheme
                                .surface
                        },
                    tonalElevation =
                        if (active) {
                            2.dp
                        } else {
                            0.dp
                        }
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
                            text = "${index + 1}. " + item.title,
                            modifier = Modifier.weight(1f),
                            color =
                                if (active) {
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
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

                Spacer(
                    Modifier.height(8.dp)
                )
            }
        }
    }

    if (showNewPlanDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewPlanDialog = false
            },
            title = {
                Text("Создать новый план?")
            },
            text = {
                Text(
                    "Текущий план будет закрыт. " + "Именованный план уже сохранён автоматически."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        plannerService?.newPlan()
                        showNewPlanDialog = false
                    }
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNewPlanDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showSaveAsDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveAsDialog = false
                saveAsError = null
            },
            title = {
                Text("Сохранить план как")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = saveAsName,
                        onValueChange = { value ->
                            saveAsName = value
                            saveAsError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Название плана")
                        },
                        singleLine = true,
                        isError = saveAsError != null,
                        supportingText = {
                            saveAsError?.let {
                                Text(it)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = saveAsName.isNotBlank(),
                    onClick = {
                        val saved = plannerService
                            ?.saveAs(saveAsName) == true

                        if (saved) {
                            showSaveAsDialog = false
                            saveAsName = ""
                            saveAsError = null
                        } else {
                            saveAsError = "Введите уникальное название"
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveAsDialog = false
                        saveAsError = null
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showOpenDialog) {
        AlertDialog(
            onDismissRequest = {
                showOpenDialog = false
            },
            title = {
                Text("Открыть план")
            },
            text = {
                if (availablePlans.isEmpty()) {
                    Text("Сохранённых планов пока нет.")
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            items = availablePlans,
                            key = { plan ->
                                plan.id
                            }
                        ) { plan ->
                            val selected = plan.id == plannerService?.currentPlanId

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color =
                                    if (selected) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = plan.name,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                val opened =
                                                    plannerService
                                                        ?.openPlan(plan.id) == true
                                                if (opened) {
                                                    showOpenDialog = false
                                                }
                                            }
                                            .padding(
                                                horizontal = 12.dp,
                                                vertical = 10.dp
                                            ),
                                        color =
                                            if (selected) {
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                    )

                                    TextButton(
                                        onClick = {
                                            planToRename = plan
                                            renameName = plan.name
                                            renameError = null
                                            showOpenDialog = false
                                        }
                                    ) {
                                        Text("Изменить")
                                    }

                                    TextButton(
                                        onClick = {
                                            planToDelete = plan
                                            deleteError = null
                                            showOpenDialog = false
                                        }
                                    ) {
                                        Text(
                                            text = "Удалить",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showOpenDialog = false
                    }
                ) {
                    Text("Закрыть")
                }
            }
        )
    }

    planToRename?.let { plan ->
        AlertDialog(
            onDismissRequest = {
                planToRename = null
                renameError = null
                showOpenDialog = true
            },
            title = {
                Text("Переименовать план")
            },
            text = {
                OutlinedTextField(
                    value = renameName,
                    onValueChange = { value ->
                        renameName = value
                        renameError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Название плана")
                    },
                    singleLine = true,
                    isError = renameError != null,
                    supportingText = {
                        renameError?.let { message ->
                            Text(message)
                        }
                    }
                )
            },
            confirmButton = {
                Button(
                    enabled = renameName.isNotBlank(),
                    onClick = {
                        val renamed = plannerService?.renamePlan(plan.id, renameName) == true

                        if (renamed) {
                            planToRename = null
                            renameName = ""
                            renameError = null
                            showOpenDialog = true
                        } else {
                            renameError = "Введите уникальное название"
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        planToRename = null
                        renameError = null
                        showOpenDialog = true
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    planToDelete?.let { plan ->
        AlertDialog(
            onDismissRequest = {
                planToDelete = null
                deleteError = null
                showOpenDialog = true
            },
            title = {
                Text("Удалить план?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("План «${plan.name}» будет удалён без возможности восстановления.")

                    deleteError?.let { message ->
                        Text(
                            text = message,
                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deleted = plannerService?.deletePlan(plan.id) == true

                        if (deleted) {
                            planToDelete = null
                            deleteError = null
                            showOpenDialog = true
                        } else {
                            deleteError = "Не удалось удалить план"
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        planToDelete = null
                        deleteError = null
                        showOpenDialog = true
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun PlannerFileControls(
    currentPlanName: String?,
    enabled: Boolean,
    onNewPlan: () -> Unit,
    onOpenPlan: () -> Unit,
    onSaveAs: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = currentPlanName ?: "Новый план",
            style = MaterialTheme
                    .typography
                    .titleSmall,
            color = if (currentPlanName == null) {
                    MaterialTheme
                        .colorScheme
                        .outline
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                },
            maxLines = 1
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = onNewPlan
            ) {
                Text(
                    text = "Новый",
                    maxLines = 1
                )
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = onOpenPlan
            ) {
                Text(
                    text = "Открыть",
                    maxLines = 1
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = onSaveAs
        ) {
            Text(
                text = "Сохранить как",
                maxLines = 1
            )
        }
    }
}