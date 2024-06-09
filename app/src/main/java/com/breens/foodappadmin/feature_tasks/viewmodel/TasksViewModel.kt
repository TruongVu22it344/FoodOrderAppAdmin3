package com.breens.foodappadmin.feature_tasks.viewmodel // ktlint-disable package-name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.data.repositories.Repository
import com.breens.foodappadmin.feature_tasks.events.TasksScreenUiEvent
import com.breens.foodappadmin.feature_tasks.side_effects.TaskScreenSideEffects
import com.breens.foodappadmin.feature_tasks.state.TasksScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskRepository: Repository) : ViewModel() {

    private val _state: MutableStateFlow<TasksScreenUiState> = MutableStateFlow(TasksScreenUiState())
    val state: StateFlow<TasksScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<TaskScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(TasksScreenUiEvent.GetTasks)
    }


    fun sendEvent(event: TasksScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> TaskScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: TasksScreenUiState) {
        _state.value = newState
    }




    private fun reduce(oldState: TasksScreenUiState, event: TasksScreenUiEvent) {
        when (event) {
            is TasksScreenUiEvent.AddTask -> {
                addTask(oldState = oldState,image= event.image, title = event.title, body = event.body, price = event.price)
            }

            is TasksScreenUiEvent.DeleteNote -> {
                deleteNote(oldState = oldState, taskId = event.taskId)
            }

            TasksScreenUiEvent.GetTasks -> {
                getTasks(oldState = oldState)
            }

            is TasksScreenUiEvent.OnChangeAddTaskDialogState -> {
                onChangeAddTaskDialog(oldState = oldState, isShown = event.show)
            }

            is TasksScreenUiEvent.OnChangeUpdateTaskDialogState -> {
                onUpdateAddTaskDialog(oldState = oldState, isShown = event.show)
            }

            is TasksScreenUiEvent.OnChangeTaskImage-> {
                onChangeTaskImage(oldState = oldState, image = event.image)
            }
            is TasksScreenUiEvent.OnChangeTaskBody -> {
                onChangeTaskBody(oldState = oldState, body = event.body)
            }

            is TasksScreenUiEvent.OnChangeTaskTitle -> {
                onChangeTaskTitle(oldState = oldState, title = event.title)
            }
            is TasksScreenUiEvent.OnChangeTaskPrice -> {
                onChangeTaskPrice(oldState = oldState, price = event.price)
            }

            is TasksScreenUiEvent.SetTaskToBeUpdated -> {
                setTaskToBeUpdated(oldState = oldState, task = event.taskToBeUpdated)
            }

            TasksScreenUiEvent.UpdateNote -> {
                updateNote(oldState = oldState)
            }

        }
    }

    private fun addTask(image: String,title: String, body: String,price: Int, oldState: TasksScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = taskRepository.addTask(image = image, title = title, body = body, price = price)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when adding task"
                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            bitmap = null,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                            currentTextFieldPrice = 0,
                        ),
                    )

                    sendEvent(TasksScreenUiEvent.OnChangeAddTaskDialogState(show = false))

                    sendEvent(TasksScreenUiEvent.GetTasks)

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Task added successfully") }
                }
            }
        }
    }

    private fun getTasks(oldState: TasksScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = taskRepository.getAllTasks()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when getting your task"
                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    val tasks = result.data
                    setState(oldState.copy(isLoading = false, tasks = tasks))
                }
            }
        }
    }

    private fun deleteNote(oldState: TasksScreenUiState, taskId: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = taskRepository.deleteTask(taskId = taskId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when deleting task"
                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Deleted successfully") }

                    sendEvent(TasksScreenUiEvent.GetTasks)
                }
            }
        }
    }

    private fun updateNote(oldState: TasksScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            val image =
                if(oldState.imgUrl == ""){
                    oldState.taskToBeUpdated?.image?:""
                }else{
                    oldState.imgUrl
                }

            val title =
                if( oldState.currentTextFieldTitle == ""){
                    oldState.taskToBeUpdated?.title ?: ""
                }else{
                    oldState.currentTextFieldTitle
                }

            val body =
                if(oldState.currentTextFieldBody == ""){
                    oldState.taskToBeUpdated?.body?:""
                }else{
                    oldState.currentTextFieldBody
                }

            val price =
                if(oldState.currentTextFieldPrice == 0){
                    oldState.taskToBeUpdated?.price ?:0
                }else{
                    oldState.currentTextFieldPrice
                }
            val taskToBeUpdated = oldState.taskToBeUpdated

            when (
                val result = taskRepository.updateTask(
                    image = image,
                    title = title,
                    body = body,
                    price = price,
                    taskId = taskToBeUpdated?.taskId ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
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
                        ),
                    )

                    sendEvent(TasksScreenUiEvent.OnChangeUpdateTaskDialogState(show = false))

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Task updated successfully") }

                    sendEvent(TasksScreenUiEvent.GetTasks)
                }
            }
        }
    }

    private fun onChangeAddTaskDialog(oldState: TasksScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowAddTaskDialog = isShown))
    }

    private fun onUpdateAddTaskDialog(oldState: TasksScreenUiState, isShown: Boolean) {
        setState(oldState.copy(isShowUpdateTaskDialog = isShown))
    }
    private fun onChangeTaskImage(oldState: TasksScreenUiState, image: String) {
        setState(oldState.copy(imgUrl = image))
    }

    private fun onChangeTaskBody(oldState: TasksScreenUiState, body: String) {
        setState(oldState.copy(currentTextFieldBody = body))
    }

    private fun onChangeTaskTitle(oldState: TasksScreenUiState, title: String) {
        setState(oldState.copy(currentTextFieldTitle = title))
    }

    private fun onChangeTaskPrice(oldState: TasksScreenUiState, price: Int) {
        setState(oldState.copy(currentTextFieldPrice = price))
    }

    private fun setTaskToBeUpdated(oldState: TasksScreenUiState, task: Task) {
        setState(oldState.copy(taskToBeUpdated = task))
    }

}
