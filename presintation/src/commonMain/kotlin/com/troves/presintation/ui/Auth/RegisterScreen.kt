package com.troves.presintation.ui.Auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading = uiState is AuthUiState.Loading
    val remoteError = (uiState as? AuthUiState.Error)?.message
    val errorMessage = localError ?: remoteError

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.extraLarge),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Spacer(Modifier.height(Theme.spacing.extraLarge))

            BasicText(
                text = "Create Account",
                style = Theme.typography.display.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            BasicText(
                text = "Join Troves and start shopping",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont
                )
            )

            Spacer(Modifier.height(Theme.spacing.large))

            // ── Email ────────────────────────────────────────────────────────
            TextField(
                text = email,
                onTextChange = {
                    email = it
                    localError = null
                    viewModel.clearError()
                },
                title = "Email",
                hint = "Enter your email address",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = errorMessage != null && email.isBlank(),
            )

            // ── Password ─────────────────────────────────────────────────────
            TextField(
                text = password,
                onTextChange = {
                    password = it
                    localError = null
                    viewModel.clearError()
                },
                title = "Password",
                hint = "At least 6 characters",
                tipText = "min. 6 chars",
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = androidx.compose.ui.graphics.painter.ColorPainter(
                    if (passwordVisible) Theme.colors.primary else Theme.colors.hint
                ),
                onClickTrailingIcon = { passwordVisible = !passwordVisible },
                isError = errorMessage != null && password.isBlank(),
            )

            // ── Confirm Password ─────────────────────────────────────────────
            TextField(
                text = confirmPassword,
                onTextChange = {
                    confirmPassword = it
                    localError = null
                },
                title = "Confirm Password",
                hint = "Re-enter your password",
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = androidx.compose.ui.graphics.painter.ColorPainter(
                    if (confirmPasswordVisible) Theme.colors.primary else Theme.colors.hint
                ),
                onClickTrailingIcon = { confirmPasswordVisible = !confirmPasswordVisible },
                isError = confirmPassword.isNotBlank() && confirmPassword != password,
                errorMessage = if (confirmPassword.isNotBlank() && confirmPassword != password)
                    "Passwords do not match" else null,
            )

            // ── Error Banner ─────────────────────────────────────────────────
            AnimatedVisibility(visible = errorMessage != null) {
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
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.error
                        )
                    )
                }
            }

            Spacer(Modifier.height(Theme.spacing.small))

            // ── Register Button ──────────────────────────────────────────────
            PrimaryButton(
                caption = "Create Account",
                onClick = {
                    if (password != confirmPassword) {
                        localError = "Passwords do not match"
                        return@PrimaryButton
                    }
                    viewModel.register(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isDisabled = isLoading,
            )

            // ── Navigate to Login ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BasicText(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(
                            SpanStyle(
                                color = Theme.colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) { append("Sign In") }
                    },
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont
                    ),
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
        }
    }
}
