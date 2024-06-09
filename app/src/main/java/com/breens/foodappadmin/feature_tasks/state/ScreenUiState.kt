package com.breens.foodappadmin.feature_tasks.state

import android.graphics.Bitmap
import com.breens.foodappadmin.data.model.Banner
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.model.Chat
import com.breens.foodappadmin.data.model.Nameuser
import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.data.model.User

data class SignInScreenUiState(
    val accounts: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentFirstname: String = "",
    val currentLastName: String = "",
    val currentEmail: String = "",
    val currentPassword: String = "",
)
data class TasksScreenUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val taskToBeUpdated: Task? = null,
    val isShowAddTaskDialog: Boolean = false,
    val isShowUpdateTaskDialog: Boolean = false,
    val currentTextFieldTitle: String = "",
    val currentTextFieldBody: String = "",
    val currentTextFieldPrice: Int = 0,
    var imgUrl: String = "",
    var bitmap: Bitmap? = null,
    var selectedOption : Double = options[0],
    /*val cartProducts: List<Task> = emptyList(),*/

    )
val options = listOf(10.0 ,15.0, 20.0)

data class CardsScreenUiState(
    val isLoading: Boolean = false,
    val cards: List<Card> = emptyList(),
    val errorMessage: String? = null,
    val cardToBeUpdated: Card? = null,
    val isShowAddCardDialog: Boolean = false,
    val isShowUpdateCardDialog: Boolean = false,
    val currentTextFieldTitle: String = "",
    val currentTextFieldBody: String = "",
    val currentTextFieldPrice: Int = 0,
    val currentTextFieldViews: Int = 0,
    val currentTextFieldFavorite: Int = 0,
    val currentTextFieldSale: Int = 0,
    var cate: String = "",
    var imgUrl: String = "",
    var bitmap: Bitmap? = null,

    var selectedOption : Double = options[0],
    /*val cartProducts: List<Card> = emptyList(),*/

    )

data class BannerScreenUiState(
    val isLoadingBanner: Boolean = false,
    val banners: List<Banner> = emptyList(),
    val errorMessage: String? = null,
    val isShowAddBannerDialog: Boolean = false,
    val bannerToBeUpdated: Banner? = null,
    val isShowUpdateBannerDialog: Boolean = false,
    val currentTextFieldTitleBanner: String = "",
    var imgUrlBanner: String = "",
    var bitmapBanner: Bitmap? = null
)

data class CatesScreenUiState(
    val isLoadingCate: Boolean = false,
    val cates: List<Cate> = emptyList(),
    val errorMessage: String? = null,
    val isShowAddCateDialog: Boolean = false,
    val cateToBeUpdated: Cate? = null,
    val isShowUpdateCateDialog: Boolean = false,
    val currentTextFieldTitleCate: String = "",
    var imgUrlCate: String = "",
    var bitmapCate: Bitmap? = null,
)

data class OrderScreenUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val errorMessage: String? = null,
    val statusToBeUpdated: Order? = null,
    val currentTitle: String = "",
    val currentAddressOrder: String = "",
    val currentQuantityOrder: Int = 0,
    val currentPriceOrder: Int = 0,
    val total: Int = 0,
    val currentPaymentOrder: String = "",
    var imgUrlOrder: String = "",
    var bitmapOrder: Bitmap? = null,
    val currentStatus: String = "",
)
data class ChatScreenUiState(
    val isLoading: Boolean = false,
    val isLoadingMessage: Boolean = false,
    val nameusers: List<Nameuser> = emptyList(),
    val messages: List<Chat> = emptyList(),
    val errorMessage: String? = null,
    val currentMessage: String = "",
    val currentSenderID: String = "",
    val currentReceiveID: String = "",
    val direction: Boolean = true,
    var imgUrl: String = "",
    var bitmap: Bitmap? = null,
    )


