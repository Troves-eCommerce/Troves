package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNav

@Composable
@Preview
fun App() {
        SpTheme {
            AppNav()
        }
}