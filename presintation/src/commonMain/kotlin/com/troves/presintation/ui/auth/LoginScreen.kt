package com.troves.presintation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.textfield.CustomTextField
import com.troves.designsystem.components.toast.TrovesToast
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_eye
import troves.designsystem.generated.resources.ic_eye_off
import troves.designsystem.generated.resources.ic_outline_email
import troves.designsystem.generated.resources.ic_google // استيراد أيقونة جوجل الخاصة بكِ

import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {},
    viewModel: AuthViewModel = koinViewModel(),
    onLoggedIn: () -> Unit,
) {
    val googleAuthHandler = LocalGoogleAuthHandler.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var successMessage by remember { mutableStateOf<String?>(null) }
    var toastError by remember { mutableStateOf<String?>(null) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is AuthEffect.ShowMessage -> successMessage = effect.message
            is AuthEffect.ShowError -> toastError = effect.message
            AuthEffect.NavigateToHome -> onLoginSuccess()
            is AuthEffect.OnRegistered -> onLoggedIn()
        }
    }

    val email = state.email
    val password = state.password
    val passwordVisible = state.passwordVisible
    val isLoading = state.isLoading
    val errorMessage = state.errorMessage

    val eyeIcon = Res.drawable.ic_eye
    val eyeOffIcon = Res.drawable.ic_eye_off

    val customBorderShape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))
            BasicText(
                text = "Welcome Back!",
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(8.dp))

            BasicText(
                text = "Log in to your account",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(42.dp))

            CustomTextField(
                text = email,
                onTextChange = { viewModel.onIntent(AuthIntent.EmailChanged(it)) },
                title = "Your Email",
                hint = "hello@veyra.shop",
                singleLine = true,
                borderColor = Theme.colors.hint.copy(alpha = 0.4f),
                onFocusBorderColor = Theme.colors.primary,
                leadingIcon = painterResource(Res.drawable.ic_outline_email),
                leadingIconColor = Theme.colors.primaryFont.copy(alpha = 0.7f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = errorMessage != null && email.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            CustomTextField(
                text = password,
                onTextChange = { viewModel.onIntent(AuthIntent.PasswordChanged(it)) },
                title = "Your Password",
                hint = "••••••••",
                singleLine = true,
                borderColor = Theme.colors.hint.copy(alpha = 0.4f),
                onFocusBorderColor = Theme.colors.primary,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = if (passwordVisible) painterResource(eyeIcon) else painterResource(eyeOffIcon),
                trailingIconColor = Theme.colors.primaryFont.copy(alpha = 0.7f),
                onClickTrailingIcon = { viewModel.onIntent(AuthIntent.TogglePasswordVisibility) },
                isError = errorMessage != null && password.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                BasicText(
                    text = "Forgot password?",
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.clickable { onNavigateToForgotPassword() }
                )
            }

            AnimatedVisibility(visible = errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Theme.colors.error.copy(alpha = 0.10f),
                            shape = Theme.shapes.medium
                        )
                        .padding(Theme.spacing.small)
                ) {
                    BasicText(
                        text = errorMessage ?: "",
                        style = Theme.typography.body.small.copy(color = Theme.colors.error)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                caption = "Log in",
                onClick = { viewModel.onIntent(AuthIntent.Login) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isDisabled = isLoading
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Theme.colors.secondary)
                BasicText(
                    text = "  or continue with  ",
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp
                    )
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Theme.colors.secondary)
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(customBorderShape)
                    .background(Color.White)
                    .border(1.dp, Theme.colors.hint.copy(alpha = 0.4f), customBorderShape)
                    .clickable {
                        googleAuthHandler?.signIn(
                            onSuccess = { idToken, accessToken ->
                                viewModel.onIntent(AuthIntent.GoogleSignIn(idToken, accessToken))
                            },
                            onError = { error ->
                                toastError = error.message ?: "Google Sign-In failed"
                            }
                        ) ?: run { toastError = "Google Sign-In is not supported on this platform" }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                BasicText(
                    text = "Continue with Google",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(Modifier.height(48.dp))

            BasicText(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Theme.colors.secondaryFont)) {
                        append("Don't have an account? ")
                    }
                    withStyle(
                        SpanStyle(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Sign up")
                    }
                },
                style = Theme.typography.body.large.copy(textAlign = TextAlign.Center),
                modifier = Modifier.clickable(onClick = onNavigateToRegister)
            )
        }

        TrovesToast(
            message = successMessage ?: toastError,
            onDismiss = {
                successMessage = null
                toastError = null
            },
        )
    }
}