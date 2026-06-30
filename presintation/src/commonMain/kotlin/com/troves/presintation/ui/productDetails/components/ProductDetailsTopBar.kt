package com.troves.presintation.ui.productDetails.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    enabled: Boolean = true
) {
    var isClicked by mutableStateOf(enabled)

    BaseTopAppBar(
        modifier = modifier,
        title = title,
        leadingIcon = painterResource(Res.drawable.ic_arrow_back),
        onLeadingClick = {
            if (!isClicked) return@BaseTopAppBar
            onBackClick()
            isClicked = false
        }
    )
}
