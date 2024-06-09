package com.breens.foodappadmin.feature_tasks.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.breens.foodappadmin.R
import com.breens.foodappadmin.data.model.TabItems
import com.breens.foodappadmin.feature_tasks.state.ChatScreenUiState
import com.breens.foodappadmin.feature_tasks.state.SignInScreenUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListChatComponent(
    navControllerNotes: NavHostController,
    uiStateAccount : SignInScreenUiState,
    uiState: ChatScreenUiState,

    ){
    val tabItems = listOf(
        TabItems(
            id = 1,
            title = "Chats",

            ),
        TabItems(
            id = 2,
            title = "Calls",
        ),
    )
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabIndex){
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage){
        selectedTabIndex = pagerState.currentPage
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),

        ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp, vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(id =  R.drawable.baseline_mark_unread_chat_alt_24) , contentDescription = "",Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(16.dp))
            androidx.compose.material3.Text(
                text = "Tin nhắn",
                style = MaterialTheme.typography.titleLarge
            )
        }
        TabRow(

            selectedTabIndex = selectedTabIndex
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    modifier = Modifier
                        .background(color = Color.White)
                        .height(63.dp),
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = 15.sp,
                            modifier = Modifier,
                            color = if (index == selectedTabIndex) {
                                Color.Red
                            } else {
                                Color.Gray
                            }
                        )

                    },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),

                ) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = tabItems[page].id.toString()
                ) {
                    composable(route = tabItems[page].id.toString()) {
                        when (tabItems[page].id) {
                            1 -> LazyColumn(contentPadding = PaddingValues(12.dp)) {
                                items(uiState.nameusers.size) { indexAccount->
                                    Column(
                                        modifier = Modifier.clickable {  navControllerNotes.navigate("ChatComponent") }
                                    ) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.duc),
                                                contentDescription = "Food Image",
                                                contentScale = ContentScale.FillBounds,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .border(
                                                        BorderStroke(2.dp, Color.Gray),
                                                        shape = CircleShape
                                                    )
                                                    .clip(
                                                        CircleShape
                                                    )

                                            )
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Column(

                                                modifier = Modifier.weight(0.7f)
                                            ) {
                                                androidx.compose.material3.Text(
                                                    text = uiState.nameusers[indexAccount].Nameuser,
                                                    color = Color.Black,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                androidx.compose.material3.Text(
                                                    text ="Xin chào!",
                                                    color = Color.Gray,
                                                    fontSize = 15.sp,
                                                )

                                            }
                                            Column(verticalArrangement = Arrangement.SpaceBetween) {
                                                androidx.compose.material3.Text(text = "12:25 PM", color = Color.Black,
                                                    fontSize = 15.sp,)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .border(
                                                            BorderStroke(2.dp, Color.White),
                                                            shape = CircleShape
                                                        )
                                                        .clip(CircleShape)
                                                        .size(23.dp) // Tăng kích thước để chứa Text dễ dàng hơn
                                                        .background(color = Color.Green)
                                                        .align(Alignment.CenterHorizontally),
                                                    contentAlignment = Alignment.Center // Căn giữa nội dung
                                                ) {
                                                    androidx.compose.material3.Text(
                                                        text = "2",
                                                        color = Color.White,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }



                                            }

                                        }

                                    }
                                }
                            }

                            2 -> null

                        }
                    }
                }
            }
        }
    }




}