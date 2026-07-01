package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.troves.data.network.provideApolloClient
import com.troves.data.source.local.preferenceses.AppPreferencesDataSourceImpl
import com.troves.data.source.remote.service.apollo.ApolloTrovesApiServiceImpl
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNav
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    SpTheme {
        val apiService = koinInject<ApolloTrovesApiServiceImpl>()
        LaunchedEffect(key1 = Unit) {
            apiService.getAllProducts()
        }
            AppNav()
        }
}