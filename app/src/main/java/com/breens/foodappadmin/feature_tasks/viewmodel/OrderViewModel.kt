package com.breens.foodappadmin.feature_tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.repositories.Repository
import com.breens.foodappadmin.feature_tasks.events.OrderScreenUiEvent
import com.breens.foodappadmin.feature_tasks.side_effects.OrderScreenSideEffects
import com.breens.foodappadmin.feature_tasks.state.OrderScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: Repository) : ViewModel() {

    private val _stateOrder: MutableStateFlow<OrderScreenUiState> =
        MutableStateFlow(OrderScreenUiState())
    val stateOrder: StateFlow<OrderScreenUiState> = _stateOrder.asStateFlow()

    private val _effectOrder: Channel<OrderScreenSideEffects> = Channel()
    val effectOrder = _effectOrder.receiveAsFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(5000) // Thực hiện cập nhật mỗi 5 giây
                sendEvent(OrderScreenUiEvent.GetOrder)
            }
        }

    }


    fun sendEvent(event: OrderScreenUiEvent) {
        reduce(oldStateOrder = _stateOrder.value, event = event)
    }

    private fun setEffect(builder: () -> OrderScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effectOrder.send(effectValue) }
    }

    private fun setState(newStateOrder: OrderScreenUiState) {
        _stateOrder.value = newStateOrder
    }




    private fun reduce(oldStateOrder: OrderScreenUiState, event: OrderScreenUiEvent) {
        when (event) {
            is OrderScreenUiEvent.AddOrder -> {
                addOrder(oldStateOrder = oldStateOrder,address = event.address, imageOrder = event.imageOrder, titleOrder = event.titleOrder, price = event.price, quantity = event.quantity, paymentMethods = event.paymentMethods,total= event.total)
            }
            OrderScreenUiEvent.GetOrder -> {
                getOrder(oldStateOrder = oldStateOrder)
            }
            is OrderScreenUiEvent.OnChangeOrderAddress-> {
                onChangeOrderAddress(oldStateOrder = oldStateOrder, address = event.address)
            }
            is OrderScreenUiEvent.OnChangeOrderImageOrder-> {
                onChangeOrderImageOrder(oldStateOrder = oldStateOrder, imageOrder = event.imageOrder)
            }
            is OrderScreenUiEvent.OnChangeOrderTitle -> {
                onChangeOrderTitle(oldStateOrder = oldStateOrder, title = event.title)
            }

            is OrderScreenUiEvent.OnChangeOrderPrice -> {
                onChangeOrderPrice(oldStateOrder = oldStateOrder, price = event.price)
            }
            is OrderScreenUiEvent.OnChangeOrderTotal -> {
                onChangeOrderTotal(oldStateOrder = oldStateOrder, total = event.total)
            }
            is OrderScreenUiEvent.OnChangeOrderQuantity -> {
                onChangeOrderQuantity(oldStateOrder = oldStateOrder, quantity = event.quantity)
            }
            is OrderScreenUiEvent.OnChangeOrderStatus -> {
                onChangeOrderStatus(oldStateOrder = oldStateOrder, status = event.status)
            }

            is OrderScreenUiEvent.OnChangeOrderPayment -> {
                onChangeOrderPayment(oldStateOrder = oldStateOrder, paymentMethods = event.paymentMethods)
            }

            is OrderScreenUiEvent.SetStatusToBeUpdated -> {
                setStatusToBeUpdated(oldStateOrder = oldStateOrder, order = event.statusToBeUpdated)
            }

            OrderScreenUiEvent.UpdateNote -> {
                updateNote(oldStateOrder = oldStateOrder)
            }
        }
    }

    private fun addOrder(address: String, imageOrder: String,titleOrder: String,price: Int, quantity: Int, paymentMethods: String,total: Int, oldStateOrder: OrderScreenUiState) {
        viewModelScope.launch {
            setState(oldStateOrder.copy(isLoading = true))

            when (val resultOrder = orderRepository.addOrder(address = address, imageOrder = imageOrder, titleOrder = titleOrder,price= price, quantity = quantity, paymentMethods = paymentMethods,total= total)) {
                is Result.Failure -> {
                    setState(oldStateOrder.copy(isLoading = false))

                    val errorMessage =
                        resultOrder.exception.message ?: "An error occurred when adding task"
                    setEffect { OrderScreenSideEffects.ShowSnackBarMessage(messageOrder = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldStateOrder.copy(
                            isLoading = false,
                            bitmapOrder = null,
                            currentAddressOrder = "",
                            currentQuantityOrder = 1,
                            currentPaymentOrder = ""
                        ),
                    )

                    sendEvent(OrderScreenUiEvent.GetOrder)

                    setEffect { OrderScreenSideEffects.ShowSnackBarMessage(messageOrder = "Đặt hàng thành công") }
                }
            }
        }
    }

    private fun getOrder(oldStateOrder: OrderScreenUiState) {
        viewModelScope.launch {

            when (val result = orderRepository.getAllOrder()) {
                is Result.Failure -> {
                    val errorMessage =
                        result.exception.message ?: "An error occurred when getting your task"
                    setEffect { OrderScreenSideEffects.ShowSnackBarMessage(messageOrder = errorMessage) }
                }

                is Result.Success -> {
                    val orders = result.data
                    setState(oldStateOrder.copy( orders = orders))
                }
            }
        }
    }
    private fun updateNote(oldStateOrder: OrderScreenUiState) {
        viewModelScope.launch {
            setState(oldStateOrder.copy(isLoading = true))
            val imageOrder = oldStateOrder.statusToBeUpdated?.imageOrder ?:""
            val title = oldStateOrder.statusToBeUpdated?.titleOrder ?:""
            val address = oldStateOrder.statusToBeUpdated?.address ?:""
            val paymentMethods = oldStateOrder.statusToBeUpdated?.paymentMethods ?:""
            val price = oldStateOrder.statusToBeUpdated?.price ?: 0
            val quantity = oldStateOrder.statusToBeUpdated?.quantity ?: 0
            val status = oldStateOrder.currentStatus
            val total = oldStateOrder.statusToBeUpdated?.total ?: 0
            val orderToBeUpdated = oldStateOrder.statusToBeUpdated


            when (
                val result = orderRepository.updateStatus(
                    imageOrder = imageOrder,
                    titleOrder = title,
                    address = address,
                    paymentMethods = paymentMethods,
                    price = price,
                    quantity = quantity,
                    status = status,
                    total = total,
                    orderId = orderToBeUpdated?.orderId ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldStateOrder.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { OrderScreenSideEffects.ShowSnackBarMessage(messageOrder = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldStateOrder.copy(
                            isLoading = false,
                            imgUrlOrder = "",
                        ),
                    )


                    setEffect { OrderScreenSideEffects.ShowSnackBarMessage(messageOrder = "Status updated successfully") }

                    sendEvent(OrderScreenUiEvent.GetOrder)
                }
            }
        }
    }

    private fun onChangeOrderAddress(oldStateOrder: OrderScreenUiState, address: String) {
        setState(oldStateOrder.copy(currentAddressOrder = address))
    }

    private fun onChangeOrderPayment(oldStateOrder: OrderScreenUiState, paymentMethods: String) {
        setState(oldStateOrder.copy(currentPaymentOrder = paymentMethods))
    }

    private fun onChangeOrderQuantity(oldStateOrder: OrderScreenUiState, quantity: Int) {
        setState(oldStateOrder.copy(currentQuantityOrder = quantity))
    }
    private fun onChangeOrderStatus(oldStateOrder: OrderScreenUiState, status: String) {
        setState(oldStateOrder.copy(currentStatus = status))
    }
    private fun setStatusToBeUpdated(oldStateOrder: OrderScreenUiState, order: Order) {
        setState(oldStateOrder.copy(statusToBeUpdated = order))
    }
    private fun onChangeOrderTotal(oldStateOrder: OrderScreenUiState, total: Int) {
        setState(oldStateOrder.copy(total = total))
    }

    private fun onChangeOrderPrice(oldStateOrder: OrderScreenUiState, price: Int) {
        setState(oldStateOrder.copy(currentPriceOrder = price))
    }

    private fun onChangeOrderTitle(oldStateOrder: OrderScreenUiState, title: String) {
        setState(oldStateOrder.copy(currentTitle = title))
    }

    private fun onChangeOrderImageOrder(oldStateOrder: OrderScreenUiState, imageOrder: String) {
        setState(oldStateOrder.copy(imgUrlOrder = imageOrder))
    }
}
