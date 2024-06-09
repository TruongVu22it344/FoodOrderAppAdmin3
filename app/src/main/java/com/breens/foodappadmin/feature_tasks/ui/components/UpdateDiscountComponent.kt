package com.breens.foodappadmin.feature_tasks.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.breens.foodappadmin.R
import com.breens.foodappadmin.feature_tasks.state.TasksScreenUiState
import com.breens.foodappadmin.feature_tasks.state.options
import com.example.movieui.core.theme.Yellow

@Composable
fun UpdateDiscountComponent(navController: NavHostController, uiState: TasksScreenUiState){
    val scrollState = rememberScrollState()
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Yellow
                ),
                shape = RoundedCornerShape(32.dp),
                onClick = {

                    navController.popBackStack()
                },
            ) {
                Text(text = "Áp dụng ngay")
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {

                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Áp dụng mã giảm giá", style = MaterialTheme.typography.h6)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                options.forEach { option ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .selectable(
                                selected = uiState.selectedOption == option,
                                onClick = { uiState.selectedOption = option }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()



                        ) {
                            Text(text = "", modifier = Modifier
                                .background(color = Color.LightGray)
                                .fillMaxWidth()
                                .height(1.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top=15.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.burger),
                                    contentDescription = "Movie Image",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 70.dp)
                                        .padding(end = 10.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(50.dp)
                                        )
                                        .clip(RoundedCornerShape(50.dp))
                                )
                                Column(

                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Text(
                                        text = "Giảm ${option}% ",
                                        color = Color.Black,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            text = "Duy nhất trong ngày hôm nay",
                                            color = Color.Black,
                                            fontSize = 15.sp,
                                        )
                                    }
                                }



                            }

                        }
                        RadioButton(
                            selected =  uiState.selectedOption == option,
                            onClick = null
                        )
                        Text(
                            text = "${option}",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }






        }
    }
}