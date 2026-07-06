package com.troves.presintation.ui.payment

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.troves.presintation.navigation.AppRoute
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.payment_methods_title

@Composable
fun PaymentMethodsScreen(){
    Text(text = stringResource(Res.string.payment_methods_title))
}