package com.troves

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import co.touchlab.kermit.Logger
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.designsystem.theme.SpTheme
import com.troves.di.initKoin
import com.troves.domain.Result
import com.troves.domain.map
import com.troves.presintation.navigation.AppNavHost
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {


    initKoin { }

    val logger = Logger
    logger.setTag("Response")
    MaterialTheme {
        SpTheme {
            val datasource: RemoteDatasource = koinInject()
            LaunchedEffect(Unit) {
                val products = datasource.getAllProducts()
                when(products){
                    is Result.Error -> {
                        logger.d {
                            products.throwable.message.toString()
                        }
                    }
                    Result.Loading -> {

                    }
                    is Result.Success<ProductResponse> -> {
                            logger.d {
                                products.value.products.toString()
                            }
                    }
                }

            }
            AppNavHost()
        }
    }
}
