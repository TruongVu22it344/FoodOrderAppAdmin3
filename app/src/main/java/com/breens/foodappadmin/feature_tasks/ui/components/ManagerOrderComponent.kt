package com.breens.foodappadmin.feature_tasks.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.model.TabItems
import com.breens.foodappadmin.feature_tasks.state.OrderScreenUiState
import java.text.DecimalFormat


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManagerOrderComponent(
    uiStateOrder: OrderScreenUiState,
    updateStatus: (Order) -> Unit,
    setOrderStatus: (String) -> Unit,
    saveStatus: () -> Unit,
){

    val tabItems = listOf(
        TabItems(
            id = 1,
            title = "Đơn mới",

         ),
        TabItems(
            id = 2,
            title = "Đã giao",
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
                horizontal = 16.dp, vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {

            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Spacer(modifier = Modifier.width(16.dp))
            androidx.compose.material3.Text(
                text = "Quản lý đơn",
                style = MaterialTheme.typography.titleSmall
            )
        }
        androidx.compose.material3.TabRow(

            selectedTabIndex = selectedTabIndex
        ) {
            tabItems.forEachIndexed { index, item ->
                androidx.compose.material3.Tab(
                    modifier = Modifier
                        .background(color = Color.White)
                        .height(63.dp),
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },
                    text = {
                        androidx.compose.material3.Text(
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
                            1 ->LazyColumn(contentPadding = PaddingValues(12.dp)) {
                                items(uiStateOrder.orders.size) { index ->
                                    if(uiStateOrder.orders[index].status != "2"){
                                        Column {
                                            Column(

                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(

                                                    verticalAlignment = Alignment.CenterVertically,

                                                    modifier = Modifier.padding(12.dp),
                                                ) {
                                                    Image(
                                                        painter = rememberImagePainter(uiStateOrder.orders[index].imageOrder),
                                                        contentDescription = "Food Image",
                                                        contentScale = ContentScale.FillBounds,
                                                        modifier = Modifier
                                                            .size(width = 100.dp, height = 90.dp)
                                                            .padding(end = 10.dp)
                                                            .border(
                                                                width = 1.dp,
                                                                color = Color.White,
                                                                shape = RoundedCornerShape(15.dp)
                                                            )
                                                            .clip(RoundedCornerShape(15.dp))
                                                    )
                                                    Spacer(modifier = Modifier.width(15.dp))
                                                    Column(

                                                        modifier = Modifier.weight(0.7f),
                                                    ) {
                                                        androidx.compose.material3.Text(
                                                            text = uiStateOrder.orders[index].titleOrder,
                                                            color = Color.Black,
                                                            fontSize = 22.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = uiStateOrder.orders[index].address,
                                                            color = Color.Black,
                                                            fontSize = 15.sp,
                                                        )
                                                        Text(
                                                            text = "${DecimalFormat("#,###").format(uiStateOrder.orders[index].total)} đ",
                                                            color = Color.Red,
                                                            fontSize = 22.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )

                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    if(uiStateOrder.orders[index].status == "0"){
                                                        Button(
                                                            onClick = {
                                                                updateStatus(uiStateOrder.orders[index])
                                                                setOrderStatus("1")
                                                                saveStatus()

                                                            },
                                                            colors = ButtonDefaults.buttonColors(Green)
                                                        ) {
                                                            androidx.compose.material3.Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Check",

                                                                )
                                                        }
                                                    }else if(uiStateOrder.orders[index].status == "1"){
                                                        androidx.compose.material3.Text(text = "Đã xác nhận")
                                                    }
                                                    else if(uiStateOrder.orders[index].status == "3"){
                                                        androidx.compose.material3.Text(text = "Đã bị hủy", color = Red)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(40.dp))

                                            }
                                        }
                                    }
                                }
                            }
                            2 ->
                                LazyColumn(contentPadding = PaddingValues(12.dp)) {
                                    items(uiStateOrder.orders.size) { index ->
                                        if (uiStateOrder.orders[index].status == "2") {

                                            Column {
                                                Column(

                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Row(

                                                        verticalAlignment = Alignment.CenterVertically,

                                                        modifier = Modifier.padding(12.dp),
                                                    ) {
                                                        Image(
                                                            painter = rememberImagePainter(
                                                                uiStateOrder.orders[index].imageOrder
                                                            ),
                                                            contentDescription = "Food Image",
                                                            contentScale = ContentScale.FillBounds,
                                                            modifier = Modifier
                                                                .size(
                                                                    width = 100.dp,
                                                                    height = 90.dp
                                                                )
                                                                .padding(end = 10.dp)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = Color.White,
                                                                    shape = RoundedCornerShape(
                                                                        15.dp
                                                                    )
                                                                )
                                                                .clip(RoundedCornerShape(15.dp))
                                                        )
                                                        Spacer(modifier = Modifier.width(15.dp))
                                                        Column(

                                                            modifier = Modifier.weight(0.7f),
                                                        ) {
                                                            androidx.compose.material3.Text(
                                                                text = uiStateOrder.orders[index].titleOrder,
                                                                color = Color.Black,
                                                                fontSize = 22.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Spacer(
                                                                modifier = Modifier.height(
                                                                    2.dp
                                                                )
                                                            )
                                                            Text(
                                                                text = uiStateOrder.orders[index].address,
                                                                color = Color.Black,
                                                                fontSize = 15.sp,
                                                            )
                                                            Text(
                                                                text = "${
                                                                    DecimalFormat("#,###").format(
                                                                        uiStateOrder.orders[index].total
                                                                    )
                                                                } đ",
                                                                color = Color.Red,
                                                                fontSize = 22.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )

                                                        }
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                    }

                                                    Spacer(modifier = Modifier.height(40.dp))

                                                }
                                            }

                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}