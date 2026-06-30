package com.troves.presintation.ui.productDetails.components

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.theme.Theme

@Composable
fun ProductDetailsShimmer(
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            ProductDetailTopBar(
                title = "",
                onBackClick = {}
            )
        },
        containerColor = Theme.colors.backGround,
        modifier = modifier
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .padding(16.dp)
                        .shimmer()
                )
            }

            item {

                Column(
                    Modifier.padding(horizontal = 16.dp)
                ) {

                    Spacer(Modifier.height(8.dp))

                    Box(
                        Modifier
                            .fillMaxWidth(0.7f)
                            .height(28.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Box(
                            Modifier
                                .width(120.dp)
                                .height(24.dp)
                                .shimmer()
                        )

                        Box(
                            Modifier
                                .width(90.dp)
                                .height(20.dp)
                                .shimmer()
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Box(
                        Modifier
                            .width(100.dp)
                            .height(20.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(4) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .shimmer()
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Box(
                        Modifier
                            .width(100.dp)
                            .height(20.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(5) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .shimmer(cornerRadius = 20)
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Box(
                        Modifier
                            .width(140.dp)
                            .height(20.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(12.dp))

                    repeat(4) {

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .padding(bottom = 8.dp)
                                .shimmer()
                        )
                    }

                    Spacer(Modifier.height(30.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(32.dp))

                    Box(
                        Modifier
                            .width(170.dp)
                            .height(22.dp)
                            .shimmer()
                    )

                    Spacer(Modifier.height(20.dp))

                    repeat(2) {

                        Row {

                            Box(
                                Modifier
                                    .size(52.dp)
                                    .shimmer(cornerRadius = 26)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(
                                Modifier.weight(1f)
                            ) {

                                Box(
                                    Modifier
                                        .fillMaxWidth(0.4f)
                                        .height(18.dp)
                                        .shimmer()
                                )

                                Spacer(Modifier.height(8.dp))

                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .shimmer()
                                )

                                Spacer(Modifier.height(6.dp))

                                Box(
                                    Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(14.dp)
                                        .shimmer()
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
