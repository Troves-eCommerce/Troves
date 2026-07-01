package com.troves.presintation.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchLoadingContent(
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            SearchBarSkeleton()
        }

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            RecentSearchSkeleton()
        }

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            BrandRowSkeleton()
        }

        items(18) {
            ProductCardSkeleton()
        }
    }
}