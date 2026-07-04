package com.troves.presintation.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon
import kotlinx.coroutines.launch

import org.koin.compose.viewmodel.koinViewModel
import com.troves.presintation.core.mvi.ObserveEffect

@Composable
fun ManageSavedAddressesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNewAddress: () -> Unit,
    onNavigateToEditAddress: (Address) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ManageSavedAddressesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Re-fetch when returning to the screen (e.g. after re-authenticating, or adding an address).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(ManageSavedAddressesIntent.OnResume)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ManageSavedAddressesEffect.NavigateBack -> onNavigateBack()
            ManageSavedAddressesEffect.NavigateToNewAddress -> onNavigateToNewAddress()
            is ManageSavedAddressesEffect.NavigateToEditAddress -> onNavigateToEditAddress(effect.address)
            is ManageSavedAddressesEffect.RequireLogin -> {
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
                onNavigateToLogin()
            }
            is ManageSavedAddressesEffect.ShowToast -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
        }
    }

    ManageSavedAddressesScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ManageSavedAddressesScreenContent(
    state: ManageSavedAddressesUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ManageSavedAddressesIntent) -> Unit
) {
    var addressToDelete by remember { mutableStateOf<Address?>(null) }
    
    if (addressToDelete != null) {
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            title = { Text(stringResource(Res.string.address_delete_title), style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold)) },
            text = { Text(stringResource(Res.string.address_delete_msg), style = Theme.typography.body.medium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        addressToDelete?.let { onIntent(ManageSavedAddressesIntent.OnDelete(it.id)) }
                        addressToDelete = null
                    }
                ) {
                    Text(stringResource(Res.string.address_delete), color = Theme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { addressToDelete = null }) {
                    Text(stringResource(Res.string.profile_cancel), color = Theme.colors.primary)
                }
            },
            containerColor = Theme.colors.surface,
            titleContentColor = Theme.colors.primaryFont,
            textContentColor = Theme.colors.secondaryFont
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BaseTopAppBar(
                title = stringResource(Res.string.address_manage_title),
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { onIntent(ManageSavedAddressesIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colors.backGround)
                    .navigationBarsPadding()
                    .padding(Theme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                SecondaryButton(
                    caption = stringResource(Res.string.address_add_new),
                    onClick = { onIntent(ManageSavedAddressesIntent.OnAddNew) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Theme.spacing.medium)
        ) {
            Text(
                text = stringResource(Res.string.address_manage_desc),
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                modifier = Modifier.padding(bottom = Theme.spacing.medium)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.addresses, key = { it.id }) { address ->
                    AddressCard(
                        address = address,
                        onEdit = { onIntent(ManageSavedAddressesIntent.OnEdit(address)) },
                        onDelete = { addressToDelete = address },
                        onSetDefault = { onIntent(ManageSavedAddressesIntent.OnSetDefault(address.id)) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) } // Room for bottom button if scrolling overlaps
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (address.icon == AddressIcon.HOME) painterResource(Res.drawable.ic_home) else painterResource(Res.drawable.ic_profile),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.label ?: address.recipientName.ifBlank { stringResource(Res.string.address_fallback_label) },
                    style = Theme.typography.body.large.copy(fontWeight = FontWeight.SemiBold),
                    color = Theme.colors.primaryFont
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = address.phone.orEmpty(),
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont
                )
                Spacer(Modifier.height(2.dp))
                address.lines.forEach { line ->
                    Text(
                        text = line,
                        style = Theme.typography.body.medium,
                        color = Theme.colors.secondaryFont
                    )
                }
            }
            if (address.isDefault) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Theme.colors.primary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.address_default),
                        color = Theme.colors.onPrimary,
                        style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Divider(color = Theme.colors.hint.copy(alpha = 0.2f))
        Spacer(Modifier.height(8.dp))

        Row {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
            ) {
                Text(stringResource(Res.string.address_edit), style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Medium))
            }
            Spacer(Modifier.width(8.dp))
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.error)
            ) {
                Text(stringResource(Res.string.address_delete), style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Medium))
            }
            if (!address.isDefault) {
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = onSetDefault,
                    colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.secondaryFont)
                ) {
                    Text(stringResource(Res.string.address_set_default), style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}
