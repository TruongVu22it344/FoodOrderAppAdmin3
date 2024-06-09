package com.breens.foodappadmin.feature_tasks.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Banner
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.repositories.Repository


import com.breens.foodappadmin.feature_tasks.events.BannerScreenUiEvent
import com.breens.foodappadmin.feature_tasks.events.CatesScreenUiEvent
import com.breens.foodappadmin.feature_tasks.side_effects.BannerScreenSideEffects
import com.breens.foodappadmin.feature_tasks.side_effects.CateScreenSideEffects

import com.breens.foodappadmin.feature_tasks.state.BannerScreenUiState
import com.breens.foodappadmin.feature_tasks.state.CatesScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(private val bannerRepository: Repository) : ViewModel() {

    private val _state1: MutableStateFlow<BannerScreenUiState> =
        MutableStateFlow(BannerScreenUiState())
    val state1: StateFlow<BannerScreenUiState> = _state1.asStateFlow()

    private val _effect1: Channel<BannerScreenSideEffects> = Channel()
    val effect1 = _effect1.receiveAsFlow()

    init {
        sendEvent(BannerScreenUiEvent.GetBanner)
    }

    fun sendEvent(event: BannerScreenUiEvent) {
        reduce(oldState1 = _state1.value, event = event)
    }

    private fun setEffect(builder: () -> BannerScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect1.send(effectValue) }
    }

    private fun setState(newState: BannerScreenUiState) {
        _state1.value = newState
    }

    private fun reduce(oldState1: BannerScreenUiState, event: BannerScreenUiEvent) {
        when (event) {
            is BannerScreenUiEvent.AddBanner -> {
                addBanner(oldState1 = oldState1,imageBanner= event.imageBanner, titleBanner = event.titleBanner)
            }


            BannerScreenUiEvent.GetBanner -> {
                getBanner(oldState1 = oldState1)
            }

            is BannerScreenUiEvent.OnChangeAddBannerDialogState -> {
                onChangeAddBannerDialog(oldState1 = oldState1, isShown = event.show)
            }


            is BannerScreenUiEvent.OnChangeBannerImage-> {
                onChangeBannerImage(oldState1 = oldState1, imageBanner = event.imageBanner)
            }


            is BannerScreenUiEvent.OnChangeBannerTitle -> {
                onChangeBannerTitle(oldState1 = oldState1, titleBanner = event.titleBanner)
            }
            is BannerScreenUiEvent.SetBannerToBeUpdated -> {
                setBannerToBeUpdated(oldState1 = oldState1, banner = event.bannerToBeUpdated)
            }

            BannerScreenUiEvent.UpdateNote -> {
                updateNote(oldState1 = oldState1)
            }
            is BannerScreenUiEvent.OnChangeUpdateBannerDialogState -> {
                onUpdateAddBannerDialog(oldState1 = oldState1, isShown = event.show)
            }
            is BannerScreenUiEvent.DeleteNote -> {
                deleteNote(oldState1 = oldState1, bannerId = event.bannerId)
            }

        }
    }

    private fun addBanner(imageBanner: String,titleBanner: String, oldState1: BannerScreenUiState) {
        viewModelScope.launch {
            setState(oldState1.copy(isLoadingBanner = true))

            when (val result1 = bannerRepository.addBanner(imageBanner = imageBanner, titleBanner = titleBanner)) {
                is Result.Failure -> {
                    setState(oldState1.copy(isLoadingBanner = false))

                    val errorMessage =
                        result1.exception.message ?: "An error occurred when adding banner"
                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState1.copy(
                            isLoadingBanner = false,
                            bitmapBanner = null,
                            currentTextFieldTitleBanner = "",
                        ),
                    )

                    sendEvent(BannerScreenUiEvent.OnChangeAddBannerDialogState(show = false))

                    sendEvent(BannerScreenUiEvent.GetBanner)

                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = "Banner added successfully") }
                }
            }
        }
    }

    private fun getBanner(oldState1: BannerScreenUiState) {
        viewModelScope.launch {
            setState(oldState1.copy(isLoadingBanner = true))

            when (val result1 = bannerRepository.getAllBanner()) {
                is Result.Failure -> {
                    setState(oldState1.copy(isLoadingBanner = false))

                    val errorMessage =
                        result1.exception.message ?: "An error occurred when getting your banner"
                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = errorMessage) }
                }

                is Result.Success -> {
                    val banners = result1.data
                    setState(oldState1.copy(isLoadingBanner = false, banners = banners))
                }
            }
        }
    }
    private fun updateNote(oldState1: BannerScreenUiState) {
        viewModelScope.launch {
            setState(oldState1.copy(isLoadingBanner = true))
            val imageBanner =
                if(oldState1.imgUrlBanner == ""){
                    oldState1.bannerToBeUpdated?.imageBanner?:""
                }else{
                    oldState1.imgUrlBanner
                }
            val titleBanner =
                if( oldState1.currentTextFieldTitleBanner== ""){
                    oldState1.bannerToBeUpdated?.titleBanner ?: ""
                }else{
                    oldState1.currentTextFieldTitleBanner
                }

            val bannerToBeUpdated = oldState1.bannerToBeUpdated

            when (
                val result = bannerRepository.updateBanner(
                    imageBanner = imageBanner,
                    titleBanner = titleBanner,
                    bannerId = bannerToBeUpdated?.bannerId?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldState1.copy(isLoadingBanner = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState1.copy(
                            isLoadingBanner = false,
                            imgUrlBanner = "",
                            bitmapBanner = null,
                            currentTextFieldTitleBanner = "",
                        ),
                    )

                    sendEvent(BannerScreenUiEvent.OnChangeUpdateBannerDialogState(show = false))

                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = "Task updated successfully") }

                    sendEvent(BannerScreenUiEvent.GetBanner)
                }
            }
        }
    }
    private fun deleteNote(oldState1: BannerScreenUiState, bannerId: String) {
        viewModelScope.launch {
            setState(oldState1.copy(isLoadingBanner = true))

            when (val resultCate = bannerRepository.deleteBanner(bannerId = bannerId)) {
                is Result.Failure -> {
                    setState(oldState1.copy(isLoadingBanner = false))

                    val errorMessage =
                        resultCate.exception.message ?: "An error occurred when deleting task"
                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState1.copy(isLoadingBanner = false))

                    setEffect { BannerScreenSideEffects.ShowSnackBarMessage(messageBanner = "Xóa thành công") }

                    sendEvent(BannerScreenUiEvent.GetBanner)
                }
            }
        }
    }


    private fun onChangeAddBannerDialog(oldState1: BannerScreenUiState, isShown: Boolean) {
        setState(oldState1.copy(isShowAddBannerDialog = isShown))
    }

    private fun onChangeBannerImage(oldState1: BannerScreenUiState, imageBanner: String) {
        setState(oldState1.copy(imgUrlBanner = imageBanner))
    }

    private fun onChangeBannerTitle(oldState1: BannerScreenUiState, titleBanner: String) {
        setState(oldState1.copy(currentTextFieldTitleBanner = titleBanner))
    }
    private fun onUpdateAddBannerDialog(oldState1: BannerScreenUiState, isShown: Boolean) {
        setState(oldState1.copy(isShowUpdateBannerDialog = isShown))
    }
    private fun setBannerToBeUpdated(oldState1: BannerScreenUiState, banner: Banner) {
        setState(oldState1.copy(bannerToBeUpdated = banner))
    }
}

