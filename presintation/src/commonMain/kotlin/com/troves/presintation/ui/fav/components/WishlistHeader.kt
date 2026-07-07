package com.troves.presintation.ui.fav.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.wishlist_header_title
import troves.presintation.generated.resources.wishlist_header_items_count
import troves.presintation.generated.resources.clear_all

@Composable
fun WishlistHeader(
    itemCount: Int,
    showClearButton: Boolean,
    onClearAllClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

         Text(
             text = stringResource(Res.string.wishlist_header_title),
             style = Theme.typography.title,
             color = Theme.colors.primaryFont,
         )
 
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(top = 8.dp),
             horizontalArrangement = Arrangement.SpaceBetween
         ) {
 
             Text(
                 text = stringResource(Res.string.wishlist_header_items_count, itemCount),
                 style = Theme.typography.body.medium,
                 color = Theme.colors.secondaryFont,
             )
 
             if (showClearButton) {
 
                 TextButton(onClick = onClearAllClick) {
                     Text(
                         text = stringResource(Res.string.clear_all),
                         style = Theme.typography.body.medium,
                         color = Theme.colors.error,
                     )
                 }
             }
         }
     }
 }