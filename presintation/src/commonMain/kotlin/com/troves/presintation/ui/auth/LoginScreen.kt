package com.troves.presintation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
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
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.components.toast.TrovesToast
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_eye
import troves.designsystem.generated.resources.ic_eye_off

import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
    onLoggedIn:()-> Unit,
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
            is AuthEffect.OnRegistered -> {
                onLoggedIn()
            }
        }
    }

    val email = state.email
    val password = state.password
    val passwordVisible = state.passwordVisible
    val isLoading = state.isLoading
    val errorMessage = state.errorMessage

    val eyeIcon = Res.drawable.ic_eye
    val eyeOffIcon = Res.drawable.ic_eye_off

    val pageBg = Color(0xFFF0F2F5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
        ) {

            BasicText(
                text = "Log In",
                style = Theme.typography.display.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(32.dp))

            TextField(
                text = email,
                onTextChange = { viewModel.onIntent(AuthIntent.EmailChanged(it)) },
                title = "Email",
                hint = "Enter your email",
                singleLine = true,
                containerColor = Theme.colors.surface,
                borderColor = Color.Transparent,
                onFocusBorderColor = Theme.colors.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = errorMessage != null && email.isBlank(),
            )

            Spacer(Modifier.height(20.dp))

            TextField(
                text = password,
                onTextChange = { viewModel.onIntent(AuthIntent.PasswordChanged(it)) },
                title = "Your Password",
                hint = "••••••••",
                singleLine = true,
                containerColor = Theme.colors.surface,
                borderColor = Color.Transparent,
                onFocusBorderColor = Theme.colors.primary,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = if (passwordVisible) painterResource(eyeIcon)  else painterResource(eyeOffIcon),
                trailingIconColor = Theme.colors.hint,
                onClickTrailingIcon = { viewModel.onIntent(AuthIntent.TogglePasswordVisibility) },
                isError = errorMessage != null && password.isBlank(),
            )

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

            Spacer(Modifier.height(28.dp))

            PrimaryButton(
                caption = "Login",
                onClick = { viewModel.onIntent(AuthIntent.Login) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isDisabled = isLoading,
            )

            Spacer(Modifier.height(24.dp))

            AuthDivider()

            Spacer(Modifier.height(24.dp))

            SocialLoginSection(
                onGoogleClick = {
                    googleAuthHandler?.signIn(
                        onSuccess = { idToken, accessToken ->
                            viewModel.onIntent(AuthIntent.GoogleSignIn(idToken, accessToken))
                        },
                        onError = { error ->
                            toastError = error.message ?: "Google Sign-In failed"
                        }
                    ) ?: run { toastError = "Google Sign-In is not supported on this platform" }
                }
            )

            Spacer(Modifier.height(32.dp))

            BasicText(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Theme.colors.secondaryFont)) {
                        append("Don't have an account?  ")
                    }
                    withStyle(
                        SpanStyle(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Sign Up")
                    }
                },
                style = Theme.typography.body.medium.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToRegister)
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
