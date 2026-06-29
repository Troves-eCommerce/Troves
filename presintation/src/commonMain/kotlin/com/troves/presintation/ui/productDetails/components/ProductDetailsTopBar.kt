package com.troves.presintation.ui.productDetails.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.topbar.BaseTopAppBar
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BaseTopAppBar(
        modifier = modifier,
        title = title,
        leadingIcon = painterResource(Res.drawable.ic_arrow_back),
        onLeadingClick = onBackClick
    )
}