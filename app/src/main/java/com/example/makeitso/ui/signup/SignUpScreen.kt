
package com.example.makeitso.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.R
import com.example.makeitso.common.composable.BasicButton
import com.example.makeitso.common.composable.BasicTextButton
import com.example.makeitso.common.composable.BasicToolbar
import com.example.makeitso.common.composable.EmailField
import com.example.makeitso.common.composable.PasswordField
import com.example.makeitso.common.ext.basicButton
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.textButton
import com.example.makeitso.R.string as AppText
import com.example.makeitso.ui.theme.MakeItSoTheme
import kotlinx.serialization.Serializable

@Serializable
object SignUpRoute

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    SignUpScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
        onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
        onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) }
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    BasicToolbar(AppText.create_account)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(uiState.email, onEmailChange, Modifier.fieldModifier())
        PasswordField(uiState.password, onPasswordChange, Modifier.fieldModifier())
        PasswordField(uiState.repeatPassword, onRepeatPasswordChange, Modifier.fieldModifier())

        BasicButton(AppText.create_account, Modifier.basicButton()) { onSignUpClick() }

        BasicTextButton(AppText.sign_in, Modifier.textButton()) { onSignInClick() }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MakeItSoTheme {
        SignUpScreenContent(
            uiState = SignUpUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = {},
            onSignInClick = {},
            onSignUpClick = {}
        )
    }
}
