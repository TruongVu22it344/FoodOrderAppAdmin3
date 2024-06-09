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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.model.TabItems
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.feature_tasks.state.CardsScreenUiState
import com.breens.foodappadmin.feature_tasks.state.TasksScreenUiState
import com.breens.foodappadmin.theme.Xam
import com.google.accompanist.pager.ExperimentalPagerApi
import java.text.DecimalFormat

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun DetailTaskComponent(
    uiState: TasksScreenUiState,
    uiStateCard: CardsScreenUiState,
    deleteTask: (String) -> Unit,
    updateTask: (Task) -> Unit,
    deleteCard: (String) -> Unit,
    updateCard: (Card) -> Unit,

    ){
    val tabItems = listOf(
        TabItems(
            id = 1,
            title = "Món ăn 1",

            ),
        TabItems(
            id = 2,
            title = "Món ăn 2",
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
            androidx.compose.material3.IconButton(onClick = {

            }) {
                androidx.compose.material3.Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back Button"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            androidx.compose.material3.Text(
                text = "Quản lý món ăn",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall
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
        androidx.compose.foundation.pager.HorizontalPager(
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
                                items(uiState.tasks.size) { index ->

                                    Column(
                                        modifier = Modifier
                                            .background(color = Xam)
                                    ) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(12.dp)
                                            
                                        ) {
                                            /*AsyncImage(
                                                model = uiState.tasks[index].image,
                                                error = painterResource(R.drawable.baseline_broken_image_24),
                                                placeholder = painterResource(R.drawable.loading_img),
                                                contentDescription = stringResource(R.string.mars_photo),
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(width = 100.dp, height = 90.dp)
                                                    .padding(end = 10.dp)
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(15.dp)
                                                    )
                                                    .clip(RoundedCornerShape(15.dp))
                                            )*/
                                            Image(
                                                painter = rememberImagePainter(uiState.tasks[index].image),
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
                                            Column(

                                                modifier = Modifier.weight(0.7f)
                                            ) {
                                                Text(
                                                    text =uiState.tasks[index].title,
                                                    color = Color.Black,
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${DecimalFormat("#,###").format(uiState.tasks[index].price)} đ",
                                                    color = Color.Red,
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                            }
                                            Column(verticalArrangement = Arrangement.SpaceAround) {
                                                IconButton(onClick = {
                                                    deleteTask(uiState.tasks[index].taskId)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = "Delete",
                                                    )
                                                }

                                                IconButton(onClick = {
                                                    updateTask(uiState.tasks[index])
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Create,
                                                        contentDescription = "Update",
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            2 ->
                                LazyColumn(contentPadding = PaddingValues(12.dp)) {
                                    items(uiStateCard.cards.size) { index ->

                                        Column(
                                            modifier = Modifier
                                                .background(color = Xam)
                                        ) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Image(
                                                    painter = rememberImagePainter(uiStateCard.cards[index].imageCard),
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
                                                Column(

                                                    modifier = Modifier.weight(0.7f)
                                                ) {
                                                    Text(
                                                        text = uiStateCard.cards[index].titleCard,
                                                        color = Color.Black,
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "${DecimalFormat("#,###").format(uiStateCard.cards[index].priceCard)} đ",
                                                        color = Color.Red,
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )

                                                }
                                                Column(verticalArrangement = Arrangement.SpaceAround) {
                                                    IconButton(onClick = {
                                                        deleteCard(uiStateCard.cards[index].cardId)
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Delete,
                                                            contentDescription = "Delete",
                                                        )
                                                    }

                                                    IconButton(onClick = {
                                                        updateCard(uiStateCard.cards[index])
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Create,
                                                            contentDescription = "Update",
                                                        )
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
}

