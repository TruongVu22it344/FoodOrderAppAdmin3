package com.breens.foodappadmin.feature_tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.repositories.Repository
import com.breens.foodappadmin.feature_tasks.events.CatesScreenUiEvent
import com.breens.foodappadmin.feature_tasks.side_effects.CateScreenSideEffects
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
class CateViewModel @Inject constructor(private val cateRepository: Repository) : ViewModel() {

    private val _stateCate: MutableStateFlow<CatesScreenUiState> = MutableStateFlow(CatesScreenUiState())
    val stateCate: StateFlow<CatesScreenUiState> = _stateCate.asStateFlow()

    private val _effectCate: Channel<CateScreenSideEffects> = Channel()
    val effectCate = _effectCate.receiveAsFlow()

    init {
        sendEvent(CatesScreenUiEvent.GetCates)
    }


    fun sendEvent(event: CatesScreenUiEvent) {
        reduce(oldStateCate = _stateCate.value, event = event)
    }

    private fun setEffect(builder: () -> CateScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effectCate.send(effectValue) }
    }

    private fun setState(newState: CatesScreenUiState) {
        _stateCate.value = newState
    }




    private fun reduce(oldStateCate: CatesScreenUiState, event: CatesScreenUiEvent) {
        when (event) {
            is CatesScreenUiEvent.AddCate -> {
                addCate(oldStateCate = oldStateCate,imageCate = event.imageCate, titleCate = event.titleCate)
            }

            CatesScreenUiEvent.GetCates -> {
                getCates(oldStateCate = oldStateCate)
            }
            is CatesScreenUiEvent.SetCateToBeUpdated -> {
                setCateToBeUpdated(oldStateCate = oldStateCate, cate = event.cateToBeUpdated)
            }

            CatesScreenUiEvent.UpdateNote -> {
                updateNote(oldStateCate = oldStateCate)
            }
            is CatesScreenUiEvent.DeleteNote -> {
                deleteNote(oldStateCate = oldStateCate, cateId = event.cateId)
            }
            is CatesScreenUiEvent.OnChangeUpdateCateDialogState -> {
                onUpdateAddCateDialog(oldStateCate = oldStateCate, isShown = event.show)
            }
            is CatesScreenUiEvent.OnChangeAddCateDialogState -> {
                onChangeAddCateDialog(oldStateCate = oldStateCate, isShown = event.show)
            }



            is CatesScreenUiEvent.OnChangeCateImage-> {
                onChangeCateImage(oldStateCate = oldStateCate, imageCate = event.imageCate)
            }

            is CatesScreenUiEvent.OnChangeCateTitle -> {
                onChangeCateTitle(oldStateCate = oldStateCate, titleCate = event.titleCate)
            }



        }
    }



    private fun addCate(
        imageCate: String,
        titleCate: String,
        oldStateCate: CatesScreenUiState
    ) {
        viewModelScope.launch {
            setState(oldStateCate.copy(isLoadingCate = true))

            when (val resultCate = cateRepository.addCate(
                imageCate = imageCate,
                titleCate = titleCate
            )) {
                is Result.Failure -> {
                    setState(oldStateCate.copy(isLoadingCate = false))

                    val errorMessage =
                        resultCate.exception.message ?: "An error occurred when adding banner"
                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldStateCate.copy(
                            isLoadingCate = false,
                            bitmapCate = null,
                            currentTextFieldTitleCate = "",
                        ),
                    )

                    sendEvent(CatesScreenUiEvent.OnChangeAddCateDialogState(show = false))

                    sendEvent(CatesScreenUiEvent.GetCates)

                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = "Thêm thành công!") }
                }
            }
        }
    }

    private fun getCates(oldStateCate: CatesScreenUiState) {
        viewModelScope.launch {
            setState(oldStateCate.copy(isLoadingCate = true))

            when (val resultCate = cateRepository.getAllCates()) {
                is Result.Failure -> {
                    setState(oldStateCate.copy(isLoadingCate = false))

                    val errorMessage =
                        resultCate.exception.message ?: "An error occurred when getting your category"
                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = errorMessage) }
                }

                is Result.Success -> {
                    val cates = resultCate.data
                    setState(
                        oldStateCate.copy(
                            isLoadingCate = false,
                            cates = cates
                        )
                    )
                }
            }
        }
    }
    private fun updateNote(oldStateCate: CatesScreenUiState) {
        viewModelScope.launch {
            setState(oldStateCate.copy(isLoadingCate = true))
            val imageCate =
                if(oldStateCate.imgUrlCate == ""){
                    oldStateCate.cateToBeUpdated?.imageCate?:""
                }else{
                    oldStateCate.imgUrlCate
                }
            val titleCate =
                if( oldStateCate.currentTextFieldTitleCate == ""){
                    oldStateCate.cateToBeUpdated?.titleCate ?: ""
                }else{
                    oldStateCate.currentTextFieldTitleCate
                }

            val cateToBeUpdated = oldStateCate.cateToBeUpdated

            when (
                val result = cateRepository.updateCate(
                    imageCate = imageCate,
                    titleCate = titleCate,
                    cateId = cateToBeUpdated?.cateId ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldStateCate.copy(isLoadingCate = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldStateCate.copy(
                            isLoadingCate = false,
                            imgUrlCate = "",
                            bitmapCate = null,
                            currentTextFieldTitleCate = "",
                        ),
                    )

                    sendEvent(CatesScreenUiEvent.OnChangeUpdateCateDialogState(show = false))

                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = "Task updated successfully") }

                    sendEvent(CatesScreenUiEvent.GetCates)
                }
            }
        }
    }
    private fun deleteNote(oldStateCate: CatesScreenUiState, cateId: String) {
        viewModelScope.launch {
            setState(oldStateCate.copy(isLoadingCate = true))

            when (val resultCate = cateRepository.deleteCate(cateId = cateId)) {
                is Result.Failure -> {
                    setState(oldStateCate.copy(isLoadingCate = false))

                    val errorMessage =
                        resultCate.exception.message ?: "An error occurred when deleting task"
                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldStateCate.copy(isLoadingCate = false))

                    setEffect { CateScreenSideEffects.ShowSnackBarMessage(messageCate = "Xóa thành công") }

                    sendEvent(CatesScreenUiEvent.GetCates)
                }
            }
        }
    }


    private fun onChangeAddCateDialog(
        oldStateCate: CatesScreenUiState,
        isShown: Boolean
    ) {
        setState(oldStateCate.copy(isShowAddCateDialog = isShown))
    }

    private fun onChangeCateImage(
        oldStateCate: CatesScreenUiState,
        imageCate: String
    ) {
        setState(oldStateCate.copy(imgUrlCate = imageCate))
    }

    private fun onChangeCateTitle(
        oldStateCate: CatesScreenUiState,
        titleCate: String
    ) {
        setState(oldStateCate.copy(currentTextFieldTitleCate = titleCate))
    }
    private fun onUpdateAddCateDialog(oldStateCate: CatesScreenUiState, isShown: Boolean) {
        setState(oldStateCate.copy(isShowUpdateCateDialog = isShown))
    }
    private fun setCateToBeUpdated(oldStateCate: CatesScreenUiState, cate: Cate) {
        setState(oldStateCate.copy(cateToBeUpdated = cate))
    }


}
