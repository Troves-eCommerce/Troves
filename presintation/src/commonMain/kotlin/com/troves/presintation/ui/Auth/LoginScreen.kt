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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.BasicText
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
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate on success
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message

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
                text = "Welcome Back",
                style = Theme.typography.display.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            BasicText(
                text = "Sign in to continue shopping",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont
                )
            )

            Spacer(Modifier.height(Theme.spacing.large))

            // ── Email Field ──────────────────────────────────────────────────
            TextField(
                text = email,
                onTextChange = {
                    email = it
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

            // ── Password Field ───────────────────────────────────────────────
            TextField(
                text = password,
                onTextChange = {
                    password = it
                    viewModel.clearError()
                },
                title = "Password",
                hint = "Enter your password",
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = androidx.compose.ui.graphics.painter.ColorPainter(
                    if (passwordVisible) Theme.colors.primary else Theme.colors.hint
                ),
                onClickTrailingIcon = { passwordVisible = !passwordVisible },
                isError = errorMessage != null && password.isBlank(),
            )

            // ── Error Message ────────────────────────────────────────────────
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

            // ── Login Button ─────────────────────────────────────────────────
            PrimaryButton(
                caption = "Sign In",
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isDisabled = isLoading,
            )

            // ── Navigate to Register ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BasicText(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(
                            SpanStyle(
                                color = Theme.colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) { append("Register") }
                    },
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont
                    ),
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }
        }
    }
}
