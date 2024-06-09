package com.breens.foodappadmin.feature_tasks.events

import com.breens.foodappadmin.data.model.Banner
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.model.Task

sealed class TasksScreenUiEvent {
    object GetTasks : TasksScreenUiEvent()
    data class AddTask(val image: String,val title: String, val body: String, val price: Int) : TasksScreenUiEvent()
    object UpdateNote : TasksScreenUiEvent()
    data class DeleteNote(val taskId: String) : TasksScreenUiEvent()
    data class OnChangeTaskImage(val image: String) : TasksScreenUiEvent()
    data class OnChangeTaskTitle(val title: String) : TasksScreenUiEvent()
    data class OnChangeTaskBody(val body: String) : TasksScreenUiEvent()
    data class OnChangeTaskPrice(val price: Int) : TasksScreenUiEvent()
    data class OnChangeAddTaskDialogState(val show: Boolean) : TasksScreenUiEvent()
    data class OnChangeUpdateTaskDialogState(val show: Boolean) : TasksScreenUiEvent()
    data class SetTaskToBeUpdated(val taskToBeUpdated: Task) : TasksScreenUiEvent()
}
sealed class CardsScreenUiEvent {
    object GetCards : CardsScreenUiEvent()
    data class AddCard(val cate: String, val image: String,val title: String, val body: String, val price: Int,val favorite: Int, val views: Int, val sale: Int) : CardsScreenUiEvent()
    object UpdateNote : CardsScreenUiEvent()
    data class DeleteNote(val cardId: String) : CardsScreenUiEvent()
    data class OnChangeCate(val cate: String) : CardsScreenUiEvent()
    data class OnChangeCardImage(val image: String) : CardsScreenUiEvent()
    data class OnChangeCardTitle(val title: String) : CardsScreenUiEvent()
    data class OnChangeCardBody(val body: String) : CardsScreenUiEvent()
    data class OnChangeCardPrice(val price: Int) : CardsScreenUiEvent()
    data class OnChangeCardFavorite(val favorite: Int) : CardsScreenUiEvent()
    data class OnChangeCardViews(val views: Int) : CardsScreenUiEvent()
    data class OnChangeCardSales(val sale: Int) : CardsScreenUiEvent()
    data class OnChangeAddCardDialogState(val show: Boolean) : CardsScreenUiEvent()
    data class OnChangeUpdateCardDialogState(val show: Boolean) : CardsScreenUiEvent()
    data class SetCardToBeUpdated(val cardToBeUpdated: Card) : CardsScreenUiEvent()
}

sealed class CatesScreenUiEvent {
    object GetCates : CatesScreenUiEvent()
    object UpdateNote : CatesScreenUiEvent()
    data class SetCateToBeUpdated(val cateToBeUpdated: Cate) : CatesScreenUiEvent()
    data class DeleteNote(val cateId: String) : CatesScreenUiEvent()
    data class AddCate(val imageCate: String,val titleCate: String) : CatesScreenUiEvent()
    data class OnChangeCateImage(val imageCate: String) : CatesScreenUiEvent()
    data class OnChangeCateTitle(val titleCate: String) : CatesScreenUiEvent()
    data class OnChangeAddCateDialogState(val show: Boolean) : CatesScreenUiEvent()
    data class OnChangeUpdateCateDialogState(val show: Boolean) : CatesScreenUiEvent()

}

sealed class BannerScreenUiEvent {
    object GetBanner : BannerScreenUiEvent()
    data class AddBanner(val imageBanner: String,val titleBanner: String) : BannerScreenUiEvent()
    object UpdateNote : BannerScreenUiEvent()
    data class SetBannerToBeUpdated(val bannerToBeUpdated: Banner) : BannerScreenUiEvent()
    data class OnChangeUpdateBannerDialogState(val show: Boolean) : BannerScreenUiEvent()
    data class DeleteNote(val bannerId: String) : BannerScreenUiEvent()
    data class OnChangeBannerImage(val imageBanner: String) : BannerScreenUiEvent()
    data class OnChangeBannerTitle(val titleBanner: String) : BannerScreenUiEvent()
    data class OnChangeAddBannerDialogState(val show: Boolean) : BannerScreenUiEvent()
}

sealed class OrderScreenUiEvent {
    object GetOrder : OrderScreenUiEvent()
    data class AddOrder(val address: String, val imageOrder: String,val titleOrder: String,val price: Int,val quantity: Int,val paymentMethods: String, val total: Int) : OrderScreenUiEvent()
    data class OnChangeOrderAddress(val address: String) : OrderScreenUiEvent()
    data class OnChangeOrderQuantity(val quantity: Int) : OrderScreenUiEvent()
    data class OnChangeOrderPayment(val paymentMethods: String) : OrderScreenUiEvent()
    data class OnChangeOrderTitle(val title: String) : OrderScreenUiEvent()
    data class OnChangeOrderImageOrder(val imageOrder: String) : OrderScreenUiEvent()
    data class OnChangeOrderPrice(val price: Int) : OrderScreenUiEvent()
    data class OnChangeOrderTotal(val total: Int) : OrderScreenUiEvent()
    data class OnChangeOrderStatus(val status: String) : OrderScreenUiEvent()
    object UpdateNote : OrderScreenUiEvent()
    data class SetStatusToBeUpdated(val statusToBeUpdated: Order) : OrderScreenUiEvent()
}
sealed class SignInScreenUiEvent {
    object GetAccount : SignInScreenUiEvent()
    data class RegisterUser(val firstname: String, val lastname: String, val email: String,val password: String) : SignInScreenUiEvent()
    object LoginUser : SignInScreenUiEvent()
    object LogoutUser : SignInScreenUiEvent()
    data class OnChangeFirstname(val firstname: String) : SignInScreenUiEvent()
    data class OnChangeLastname(val lastname: String) : SignInScreenUiEvent()
    data class OnChangeEmail(val email: String) : SignInScreenUiEvent()
    data class OnChangePassword(val password: String) : SignInScreenUiEvent()

}
sealed class ChatScreenUiEvent {
    object GetUser : ChatScreenUiEvent()
    object GetMessage : ChatScreenUiEvent()
    data class AddMessage(val senderID: String, val message: String, val direction: Boolean ) : ChatScreenUiEvent()
    data class OnChangeSenderID(val senderID: String) : ChatScreenUiEvent()
    data class OnChangeMessage(val message: String) : ChatScreenUiEvent()
    data class OnChangeReceiveID(val receiveID: String) : ChatScreenUiEvent()
    data class OnChangeDirection(val direction: Boolean) : ChatScreenUiEvent()
}

