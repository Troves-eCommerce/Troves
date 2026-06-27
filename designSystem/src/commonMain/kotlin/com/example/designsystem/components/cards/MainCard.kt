package com.example.designsystem.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Theme

@Composable
fun MainCard(
    mainIcon: Painter,
    productImage: Painter,
    productName: String,
    productPrice: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(172.dp).height(292.dp)
    ) {
        Box(
            modifier = Modifier.size(172.dp)
                .background(
                    Theme.colors.backGround,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        ) {
            Image(
                painter = productImage,
                contentDescription = productName,
                modifier = modifier.fillMaxWidth(),
            )
            Image(
                painter = mainIcon,
                contentDescription = "Main Icon"
            )
        }
        Column {

        }

    }

}
