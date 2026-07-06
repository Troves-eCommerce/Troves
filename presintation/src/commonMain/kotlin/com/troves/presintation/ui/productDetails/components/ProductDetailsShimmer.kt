package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.theme.Theme

@Composable
fun ProductDetailsShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 86.dp)
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .shimmerEffect()
                )
            }

            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(20.dp))
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(28.dp)
                            .shimmerEffect()
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(Modifier.width(100.dp).height(24.dp).shimmerEffect())
                        Box(Modifier.width(80.dp).height(20.dp).shimmerEffect())
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.width(80.dp).height(20.dp).shimmerEffect())
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) {
                            Box(Modifier.size(38.dp).shimmerEffect())
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(3) {
                            Box(Modifier.weight(1f).height(46.dp).shimmerEffect())
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.fillMaxWidth().height(100.dp).shimmerEffect())
                }
            }
        }

        Row(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.width(80.dp).height(30.dp).shimmerEffect())
            Box(Modifier.weight(1f).height(48.dp).shimmerEffect())
            Box(Modifier.size(48.dp).shimmerEffect())
        }
    }
}