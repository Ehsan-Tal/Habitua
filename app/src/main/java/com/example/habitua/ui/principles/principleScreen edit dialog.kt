package com.example.habitua.ui.principles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.habitua.R
import com.example.habitua.data.PrincipleDetails
import java.text.SimpleDateFormat


@Composable
fun PrincipleDetailEditMenuDialog(
    expandEditMenu: Boolean,
    onEditMenuDismiss: () -> Unit,

    isBeforeYesterday: Boolean,
    canApplyChanges: Boolean,
    editMenuPrincipleDetails: PrincipleDetails,

    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,

    editMenuDeleteConfirmation: Boolean,
    editMenuDeleteToExpand: () -> Unit,
){
    if (expandEditMenu) {
        Dialog(
            onDismissRequest = onEditMenuDismiss,
        ) {
            PrincipleDetailEditMenuDialogContent(
                editMenuPrincipleDetails = editMenuPrincipleDetails,
                editMenuUpdatePrincipleInUiState = editMenuUpdatePrincipleInUiState,
                editMenuApplyChangesToPrinciple = editMenuApplyChangesToPrinciple,
                editMenuDeletePrinciple = editMenuDeletePrinciple,
                editMenuDeleteConfirmation = editMenuDeleteConfirmation,
                editMenuDeleteToExpand = editMenuDeleteToExpand,

                isBeforeYesterday = isBeforeYesterday,
                canApplyChanges = canApplyChanges
            )
        }
    }
}

@Composable
fun PrincipleDetailEditMenuDialogContent(
    editMenuPrincipleDetails: PrincipleDetails,
    isBeforeYesterday: Boolean,
    canApplyChanges: Boolean,
    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,

    editMenuDeleteConfirmation: Boolean,
    editMenuDeleteToExpand: () -> Unit,
){
    val modifierMaxWidth = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
        .padding(dimensionResource(id = R.dimen.padding_large))

    OutlinedCard (
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_large))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth(),

    ){
        // title
        Text(
            text = "Editing principle",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_large))
        )

        Button(
            onClick = editMenuApplyChangesToPrinciple,
            enabled = canApplyChanges,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) { Text( text = "Apply changes" ) }
        //TODO: set this read only if there were no changes made

        Column( modifier = modifierMaxWidth ) {
            // name
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                label = {
                    Text(
                        text = stringResource(id = R.string.form_required_name),
                        style = MaterialTheme.typography.displaySmall
                    ) },
                value = editMenuPrincipleDetails.name,
                onValueChange = {
                    editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(name = it))
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.displaySmall
            )

            // description
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                label = {
                    Text(
                        text = stringResource(id = R.string.form_required_description),
                        style = MaterialTheme.typography.displaySmall
                    )},
                value = editMenuPrincipleDetails.description,
                onValueChange = {
                    editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(description = it))
                },
                singleLine = false,
                minLines = 2,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // value
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text( text = "Checked" )
                OutlinedButton(
                    onClick = {
                        editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(value =
                        !editMenuPrincipleDetails.value ))
                    },
                ) {
                    if (editMenuPrincipleDetails.value) { Text(text = "Yes") }
                    else { Text(text = "No") }
                }
            }
        }


        // group
        /*
        Text(
            text = "${editMenuPrincipleDetails.group}"
        )
        //TODO: add group as a property and allow some editing maybe
         */

        if (editMenuPrincipleDetails.dateFirstActive != null) {
            // Date first active
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text( text = "First active on:" )
                Text( text = SimpleDateFormat("dd-MM-yyyy").format(editMenuPrincipleDetails.dateFirstActive) )
            }
        }

        // date created
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text( text = "Created on:" )
            Text( text = SimpleDateFormat("dd-MM-yyyy").format(editMenuPrincipleDetails.dateCreated) )
        }
        //TODO: Make a function and pass it down for the dating
        //TODO: make a property for that



        // delete expand and actual delete
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                .padding(dimensionResource(id = R.dimen.padding_large)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = editMenuDeleteToExpand,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) { Text( text = "Delete principle" ) }

            if ( editMenuDeleteConfirmation ) {
                Button(
                    onClick = editMenuDeletePrinciple,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) { Text( text = "Confirm deletion" ) }
            }
        }
    }
}

