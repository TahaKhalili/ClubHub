package hu.bme.ait.clubhub.ui.screen.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import hu.bme.ait.clubhub.R


@Composable
fun SigninScreen(
    viewModel: SigninViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onSigninSuccess: () -> Unit,
) {

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("demo@ait.hu") }
    var password by rememberSaveable { mutableStateOf("123456") }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.club_hub),
                fontSize = 30.sp,
                fontStyle = Italic,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.find_and_explore_activities_around_you),
                fontSize = 15.sp,
                color = Color.Gray
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row {
                Image(painter = painterResource(id = R.drawable.ball), contentDescription = null)
                Image(painter = painterResource(id = R.drawable.pawn), contentDescription = null)
                Image(painter = painterResource(id = R.drawable.melody), contentDescription = null)
                Image(painter = painterResource(id = R.drawable.dumbell), contentDescription = null)
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                label = { Text(stringResource(R.string.e_mail), color = Color.White) },
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, null, tint = Color.White)
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                label = { Text(stringResource(R.string.password), color = Color.White) },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, tint = Color.White)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Close else Icons.Default.Face,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val result = viewModel.signinUser(email, password)
                            if (result?.user != null) onSigninSuccess()
                        }
                    },
                    border = BorderStroke(2.dp, Color(0xFF445E96)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF445E96)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.log_in), color = Color.White)
                }

                OutlinedButton(
                    onClick = { viewModel.registerUser(email, password) },
                    border = BorderStroke(2.dp, Color(0xFF445E96)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF445E96)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.register), color = Color.White)
                }
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = viewModel.signinUiState) {
                is SigninUiState.Error ->
                    Text(stringResource(R.string.error_format, state.errorMessage ?: ""), color = Color.Red)

                is SigninUiState.Loading ->
                    CircularProgressIndicator()

                is SigninUiState.SigninSuccess ->
                    Text(stringResource(R.string.signin_ok), color = Color.White)

                is SigninUiState.RegisterSuccess ->
                    Text(stringResource(R.string.register_ok), color = Color.White)

                else -> {}
            }
        }
    }
}