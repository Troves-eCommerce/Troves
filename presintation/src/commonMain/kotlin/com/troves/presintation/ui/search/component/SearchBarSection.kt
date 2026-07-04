package com.troves.presintation.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SlidersHorizontal
import com.composables.icons.lucide.X
import com.troves.designsystem.components.textfield.CustomTextField
import com.troves.designsystem.components.topbar.IconBox
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_search

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val barBg = if (isSystemInDarkTheme()) Color(0xFF2C2C2C) else Color(0xFFEFEFEF)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconBox(
            icon = painterResource(Res.drawable.ic_arrow_back),
            onClick = onBackClick,
            backgroundColor = barBg,
            iconTint = Theme.colors.primaryFont,
            shape = CircleShape,
            autoMirror = true,
        )

        CustomTextField(
            text = query,
            onTextChange = onQueryChange,
            modifier = Modifier.weight(1f),
            hint = "Search products, brands...",
            leadingIcon = painterResource(Res.drawable.ic_search),
            trailingIcon = if (query.isNotEmpty()) rememberVectorPainter(Lucide.X) else null,
            singleLine = true,
            containerColor = barBg,
            borderColor = Color.Transparent, 
            onFocusBorderColor = Color.Transparent,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            fieldHeight = 48.dp,
            onClickTrailingIcon = { if (query.isNotEmpty()) onQueryChange("") },
            shape = CircleShape
        )
        
        IconBox(
            icon = rememberVectorPainter(Lucide.SlidersHorizontal),
            onClick = onFilterClick,
            backgroundColor = barBg,
            iconTint = Theme.colors.primaryFont,
            shape = CircleShape
        )
    }
}