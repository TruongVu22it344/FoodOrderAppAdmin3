package com.breens.foodappadmin.feature_tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.breens.foodappadmin.R
import com.breens.foodappadmin.feature_tasks.state.ChatScreenUiState
import com.breens.foodappadmin.feature_tasks.state.SignInScreenUiState
import com.breens.foodappadmin.theme.BlueMess
import com.breens.foodappadmin.theme.LightRed
import com.breens.foodappadmin.theme.LightYellow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatComponent(
    navController: NavHostController,
    uiStateAccount : SignInScreenUiState,
    uiState: ChatScreenUiState,
    senderID: (String) -> Unit,
    setMessage: (String) -> Unit,
    direction: (Boolean) -> Unit,
    senderMessage: () -> Unit
) {
    val listState = rememberLazyListState()
    var receiveID by remember {
        mutableStateOf("")
    }
    LaunchedEffect(uiState.messages.size) {
        listState.scrollToItem(uiState.messages.size - 1)
    }
    LazyColumn(contentPadding = PaddingValues(12.dp)){
        items(uiState.nameusers.size) { indexAccount ->

            receiveID = uiState.nameusers[indexAccount].Nameuser

        }
    }
    senderID(receiveID)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueMess)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    androidx.compose.material3.IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                    SpacerWidth()
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_account_circle_24),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    SpacerWidth()
                    Column {
                        Text(
                            text = receiveID, style = TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = stringResource(R.string.online), style = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
                IconComponentImageVector(icon = Icons.Default.MoreVert, size = 24.dp, tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White, RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
                    .padding(top = 25.dp)


            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 15.dp,
                        top = 25.dp,
                        end = 15.dp,
                        bottom = 75.dp
                    ),
                    state = listState
                ) {
                    items(uiState.messages.size) { chat->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (uiState.messages[chat].direction) Alignment.End else Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (uiState.messages[chat].direction) LightYellow else LightRed,
                                        RoundedCornerShape(100.dp)
                                    ),
                                contentAlignment = Center
                            ) {
                                Text(
                                    text = uiState.messages[chat].message, style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 15.sp
                                    ),
                                    modifier = Modifier.padding(
                                        vertical = 8.dp,
                                        horizontal = 15.dp
                                    ),
                                    textAlign = TextAlign.End
                                )
                            }
                            Text(
                                text = uiState.messages[chat].createdAt,
                                style = TextStyle(
                                    color = com.breens.foodappadmin.theme.Gray,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                            )
                        }
                    }


                }
            }

        }





        Column() {
            Spacer(modifier = Modifier.weight(1f))
            TextField(
                value = uiState.currentMessage, onValueChange = { message ->
                    setMessage(message)
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.type_message),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    cursorColor = Color.Black,
                ),
                leadingIcon = { Icons.Default.Add },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .background(com.breens.foodappadmin.theme.Yellow, CircleShape)
                            .size(33.dp), contentAlignment = Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_send_24),
                            contentDescription = "",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(15.dp)
                                .clickable {
                                    direction(true)
                                    senderMessage()
                                    setMessage("")
                                }
                        )
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp).fillMaxWidth(),
                shape = CircleShape
            )

        }

    }
}
@Composable
fun IconComponentImageVector(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp
) {
    Icon(imageVector = icon, contentDescription = "", modifier = modifier.size(size), tint = tint)
}
@Composable
fun SpacerWidth(
    width: Dp = 10.dp
) {
    Spacer(modifier = Modifier.width(width))
}


