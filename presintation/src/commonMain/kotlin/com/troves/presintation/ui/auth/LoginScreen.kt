package com.troves.presintation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.troves.designsystem.components.toast.ToastType
import com.troves.designsystem.components.toast.TrovesToastHost
import com.troves.designsystem.components.toast.rememberTrovesToastState
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import com.troves.presintation.core.mvi.ObserveEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.*

import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToEmailVerification: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
    onLoggedIn: () -> Unit,
) {
    val googleAuthHandler = LocalGoogleAuthHandler.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val toast = rememberTrovesToastState()

    val googleSignInFailedText = stringResource(Res.string.login_google_failed)
    val googleSignInUnsupportedText = stringResource(Res.string.login_google_unsupported)

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is AuthEffect.ShowMessage -> toast.show(effect.message, ToastType.Success)
            is AuthEffect.ShowError -> toast.show(effect.message, ToastType.Error)
            AuthEffect.NavigateToHome -> onLoginSuccess()
            is AuthEffect.OnRegistered -> onLoggedIn()
            AuthEffect.NavigateToEmailVerification -> onNavigateToEmailVerification()
        }
    }

    val email = state.email
    val password = state.password
    val passwordVisible = state.passwordVisible
    val isLoading = state.isLoading
    val isGoogleLoading = state.isGoogleLoading
    val isAnyLoading = isLoading || isGoogleLoading
    val errorMessage = state.errorMessage

    val eyeIcon = Res.drawable.ic_eye
    val eyeOffIcon = Res.drawable.ic_eye_off

    val customBorderShape = RoundedCornerShape(14.dp)

    Scaffold(
        containerColor = Theme.colors.backGround,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            Spacer(Modifier.height(24.dp))
            BasicText(
                text = stringResource(Res.string.login_welcome),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(4.dp))

            BasicText(
                text = stringResource(Res.string.login_desc),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(36.dp))

            CustomTextField(
                text = email,
                onTextChange = { viewModel.onIntent(AuthIntent.EmailChanged(it)) },
                title = stringResource(Res.string.login_email_title),
                hint = stringResource(Res.string.login_email_hint),
                singleLine = true,
                borderColor = Theme.colors.hint.copy(alpha = 0.4f),
                onFocusBorderColor = Theme.colors.primary,
                leadingIcon = painterResource(Res.drawable.ic_outline_email),
                leadingIconColor = Theme.colors.primaryFont.copy(alpha = 0.7f),
                enabled = !isAnyLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = errorMessage != null && email.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            CustomTextField(
                text = password,
                onTextChange = { viewModel.onIntent(AuthIntent.PasswordChanged(it)) },
                title = stringResource(Res.string.login_password_title),
                hint = stringResource(Res.string.login_password_hint),
                singleLine = true,
                borderColor = Theme.colors.hint.copy(alpha = 0.4f),
                onFocusBorderColor = Theme.colors.primary,
                enabled = !isAnyLoading,
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
                caption = stringResource(Res.string.login_button),
                onClick = { viewModel.onIntent(AuthIntent.Login) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isDisabled = isAnyLoading
            )

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Theme.colors.secondary)
                BasicText(
                    text = stringResource(Res.string.login_or_continue),
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
                    .background(Theme.colors.surface, customBorderShape)
                    .border(1.dp, Theme.colors.hint.copy(alpha = 0.4f), customBorderShape)
                    .bounceClick(
                        shape = customBorderShape,
                        maxPadding = 4.dp,
                        onClick = {
                            if (!isAnyLoading) {
                                googleAuthHandler?.signIn(
                                    onSuccess = { idToken, accessToken ->
                                        viewModel.onIntent(AuthIntent.GoogleSignIn(idToken, accessToken))
                                    },
                                    onError = { error ->
                                        toast.show(error.message ?: googleSignInFailedText, ToastType.Error)
                                    }
                                ) ?: run { toast.show(googleSignInUnsupportedText, ToastType.Error) }
                            }
                        }
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isGoogleLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Theme.colors.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Image(
                        painter = painterResource(Res.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        text = stringResource(Res.string.login_google),
                        style = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            BasicText(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Theme.colors.secondaryFont)) {
                        append(stringResource(Res.string.login_dont_have_account))
                    }
                    withStyle(
                        SpanStyle(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(Res.string.login_sign_up))
                    }
                },
                style = Theme.typography.body.large.copy(textAlign = TextAlign.Center),
                modifier = Modifier.bounceClick(
                    shape = RoundedCornerShape(4.dp),
                    maxPadding = 2.dp,
                    onClick = onNavigateToRegister
                )
            )

            Spacer(Modifier.height(24.dp))

            androidx.compose.material3.TextButton(
                onClick = onLoginSuccess,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = Theme.colors.primaryFont)
            ) {
                BasicText(
                    text = stringResource(Res.string.verify_email_continue_as_guest),
                    style = Theme.typography.body.large.copy(fontWeight = FontWeight.Medium, color = Theme.colors.primaryFont)
                )
            }
        }

        TrovesToastHost(state = toast)
    }
}
}