package com.breens.orderfood.feature_tasks.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.breens.foodappadmin.R
import com.breens.foodappadmin.feature_tasks.state.SignInScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpComponent(
    uiStateAccount: SignInScreenUiState,
    navController: NavController,
    setFirstname: (String)-> Unit,
    setLastname: (String)-> Unit,
    setEmail: (String)-> Unit,
    setPassword: (String)-> Unit,
    saveAccount: () -> Unit,
){

    val scrollState = rememberScrollState()
    Column {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp
            ).padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Spacer(modifier = Modifier.width(16.dp))
            androidx.compose.material.Text(
                text = "Quay về",
                style = MaterialTheme.typography.h6
            )
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fast_food), contentDescription = "",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Vui lòng đăng ký!", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = uiStateAccount.currentFirstname
                , onValueChange = {firstname->
                    setFirstname(firstname)
                }
                , label = { Text(text = "Họ") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiStateAccount.currentLastName
                , onValueChange = {lastname->
                    setLastname(lastname)
                }
                , label = { Text(text = "Tên") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiStateAccount.currentEmail
                , onValueChange = {email->
                    setEmail(email)
                }
                , label = { Text(text = "Email") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiStateAccount.currentPassword,
                onValueChange = {pass->
                    setPassword(pass)
                } ,
                label = { Text(text = "Mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                ),

                )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                saveAccount()
                navController.popBackStack()
            }, colors = ButtonDefaults.buttonColors(Color(0xFFFF9F00))) {
                Text(text = "Đăng ký",
                    modifier = Modifier.width(250.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}