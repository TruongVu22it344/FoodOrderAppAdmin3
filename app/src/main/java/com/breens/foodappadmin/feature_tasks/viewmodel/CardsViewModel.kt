package com.breens.foodappadmin.feature_tasks.viewmodel // ktlint-disable package-name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.repositories.Repository
import com.breens.foodappadmin.feature_tasks.events.CardsScreenUiEvent
import com.breens.foodappadmin.feature_tasks.side_effects.CardScreenSideEffects
import com.breens.foodappadmin.feature_tasks.state.CardsScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(private val cardRepository: Repository) : ViewModel() {

    private val _state: MutableStateFlow<CardsScreenUiState> = MutableStateFlow(CardsScreenUiState())
    val state: StateFlow<CardsScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<CardScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(CardsScreenUiEvent.GetCards)
    }


    fun sendEvent(event: CardsScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> CardScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: CardsScreenUiState) {
        _state.value = newState
    }




    private fun reduce(oldState: CardsScreenUiState, event: CardsScreenUiEvent) {
        when (event) {
            is CardsScreenUiEvent.AddCard -> {
                addCard(oldState = oldState,cate= event.cate, image= event.image, title = event.title, body = event.body, price = event.price, favorite = event.favorite, views =  event.views, sale = event.sale)
            }

            is CardsScreenUiEvent.DeleteNote -> {
                deleteNote(oldState = oldState, cardId = event.cardId)
            }
            is CardsScreenUiEvent.OnChangeCate-> {
                onChangeCate(oldState = oldState, cate = event.cate)
            }
            CardsScreenUiEvent.GetCards -> {
                getCards(oldState = oldState)
            }

            is CardsScreenUiEvent.OnChangeAddCardDialogState -> {
                onChangeAddCardDialog(oldState = oldState, isShown = event.show)
            }

            is CardsScreenUiEvent.OnChangeUpdateCardDialogState -> {
                onUpdateAddCardDialog(oldState = oldState, isShown = event.show)
            }

            is CardsScreenUiEvent.OnChangeCardImage-> {
                onChangeCardImage(oldState = oldState, image = event.image)
            }
            is CardsScreenUiEvent.OnChangeCardBody -> {
                onChangeCardBody(oldState = oldState, body = event.body)
            }

            is CardsScreenUiEvent.OnChangeCardTitle -> {
                onChangeCardTitle(oldState = oldState, title = event.title)
            }
            is CardsScreenUiEvent.OnChangeCardPrice -> {
                onChangeCardPrice(oldState = oldState, price = event.price)
            }
            is CardsScreenUiEvent.OnChangeCardFavorite -> {
                onChangeCardFavorite(oldState = oldState, favorite = event.favorite)
            }
            is CardsScreenUiEvent.OnChangeCardViews -> {
                onChangeCardViews(oldState = oldState, views = event.views)
            }
            is CardsScreenUiEvent.OnChangeCardSales -> {
                onChangeCardSale(oldState = oldState, sale = event.sale)
            }

            is CardsScreenUiEvent.SetCardToBeUpdated -> {
                setCardToBeUpdated(oldState = oldState, card = event.cardToBeUpdated)
            }
            CardsScreenUiEvent.UpdateNote -> {
                updateNote(oldState = oldState)
            }

        }
    }

    private fun addCard(cate: String, image: String,title: String, body: String,price: Int,favorite: Int, views: Int, sale: Int, oldState: CardsScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = cardRepository.addCard(cate= cate, image = image, title = title, body = body, price = price, favorite = favorite, views = views, sale = sale)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when adding task"
                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            cate= "",
                            imgUrl = "",
                            bitmap = null,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                            currentTextFieldPrice = 0,
                        ),
                    )

                    sendEvent(CardsScreenUiEvent.OnChangeAddCardDialogState(show = false))

                    sendEvent(CardsScreenUiEvent.GetCards)

                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = "Task added successfully") }
                }
            }
        }
    }

    private fun getCards(oldState: CardsScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = cardRepository.getAllCards()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when getting your task"
                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = errorMessage) }
                }

                is Result.Success -> {
                    val cards = result.data
                    setState(oldState.copy(isLoading = false, cards = cards))
                }
            }
        }
    }
    private fun deleteNote(oldState: CardsScreenUiState, cardId: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = cardRepository.deleteCard(cardId = cardId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when deleting task"
                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))

                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = "Xóa thành công!") }

                    sendEvent(CardsScreenUiEvent.GetCards)
                }
            }
        }
    }

    private fun updateNote(oldState: CardsScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            val cate = oldState.cardToBeUpdated?.cate?: ""
            val image =
                if(oldState.imgUrl == ""){
                    oldState.cardToBeUpdated?.imageCard ?:""
                }else{
                    oldState.imgUrl
                }

            val title =
                if( oldState.currentTextFieldTitle == ""){
                    oldState.cardToBeUpdated?.titleCard ?: ""
                }else{
                    oldState.currentTextFieldTitle
                }

            val body =
                if(oldState.currentTextFieldBody == ""){
                    oldState.cardToBeUpdated?.bodyCard?:""
                }else{
                    oldState.currentTextFieldBody
                }

            val price =
                if(oldState.currentTextFieldPrice == 0){
                    oldState.cardToBeUpdated?.priceCard ?:0
                }else{
                    oldState.currentTextFieldPrice
                }

            val favorite = oldState.cardToBeUpdated?.favorite ?: 0
            val views = oldState.cardToBeUpdated?.views ?: 0
            val sale =
                if( oldState.currentTextFieldSale == 0){
                    oldState.cardToBeUpdated?.sale?:0
                }else{
                    oldState.currentTextFieldSale
                }

            val cardToBeUpdated = oldState.cardToBeUpdated

            when (
                val result = cardRepository.updateCard(
                    cate = cate,
                    image = image,
                    title = title,
                    body = body,
                    price = price,
                    favorite = favorite,
                    views = views,
                    sale = sale,
                    cardID = cardToBeUpdated?.cardId ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            imgUrl = "",
                            bitmap = null,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                            currentTextFieldPrice = 0,
                            currentTextFieldSale = 0

                        ),
                    )

                    sendEvent(CardsScreenUiEvent.OnChangeUpdateCardDialogState(show = false))

                    setEffect { CardScreenSideEffects.ShowSnackBarMessage(messageCard = "Cập nhập thành công") }

                    sendEvent(CardsScreenUiEvent.GetCards)
                }
            }
        }
    }
    private fun onChangeAddCardDialog(oldState: CardsScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowAddCardDialog = isShown))
    }

    private fun onUpdateAddCardDialog(oldState: CardsScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowUpdateCardDialog = isShown))
    }

    private fun onChangeCardImage(oldState: CardsScreenUiState, image: String) {
        setState(oldState.copy(imgUrl = image))
    }

    private fun onChangeCardBody(oldState: CardsScreenUiState, body: String) {
        setState(oldState.copy(currentTextFieldBody = body))
    }

    private fun onChangeCardTitle(oldState: CardsScreenUiState, title: String) {
        setState(oldState.copy(currentTextFieldTitle = title))
    }

    private fun onChangeCardPrice(oldState: CardsScreenUiState, price: Int) {
        setState(oldState.copy(currentTextFieldPrice = price))
    }
    private fun onChangeCardFavorite(oldState: CardsScreenUiState, favorite: Int) {
        setState(oldState.copy(currentTextFieldFavorite = favorite))
    }
    private fun onChangeCardViews(oldState: CardsScreenUiState, views: Int) {
        setState(oldState.copy(currentTextFieldViews = views))
    }
    private fun onChangeCardSale(oldState: CardsScreenUiState, sale: Int) {
        setState(oldState.copy(currentTextFieldSale = sale))
    }
    private fun setCardToBeUpdated(oldState: CardsScreenUiState, card: Card) {
        setState(oldState.copy(cardToBeUpdated = card))
    }
    private fun onChangeCate(oldState: CardsScreenUiState, cate: String) {
        setState(oldState.copy(cate = cate))
    }
}
