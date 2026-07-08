package com.troves.presintation.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.all_categories_title

@Composable
fun AllCategoriesScreen (){
    Text(stringResource(Res.string.all_categories_title))
}
