package com.apc.revenuerise.vms

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dispatchers.DispatcherTypes
import com.apc.revenuerise.repository.home.HomeDefRepo
import com.apc.solarsuvidha.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeDefRepo,
    private val dispatchers: DispatcherTypes
) : ViewModel() {



    // Sealed class to represent different states of the login process
    sealed class GetAssignedConsumersEvent {
        object Empty : GetAssignedConsumersEvent()
        object Loading : GetAssignedConsumersEvent()
        data class Success(val resultText: GetConsumersForCallingRes?) : GetAssignedConsumersEvent()
        data class Failure(val errorText: String) : GetAssignedConsumersEvent()
    }

    // Mutable StateFlow to hold the current state of login
    private val _consListState = MutableStateFlow<GetAssignedConsumersEvent>(GetAssignedConsumersEvent.Empty)

    // Exposed immutable StateFlow for composable to observe
    val consListState: StateFlow<GetAssignedConsumersEvent> = _consListState

    // Function to handle user login
    fun getAssignedConsumers(uid: Int) {
        viewModelScope.launch(dispatchers.io) {
            _consListState.value = GetAssignedConsumersEvent.Loading // Set loading state
            when (val result = repository.getAssignedConsumers(uid)) {
                is Resource.Success -> {
                    if (!result.data!!.error) {
                        _consListState.value = GetAssignedConsumersEvent.Success(result.data)
                    } else {
                        _consListState.value = GetAssignedConsumersEvent.Failure(result.data?.message ?: "Unknown Error")
                    }
                }
                is Resource.Error -> {
                    _consListState.value = GetAssignedConsumersEvent.Failure(result.message ?: "Network Error")
                }
            }
        }
    }

    // Reinitialize login state
    fun reInitAssignedConsList() {
        _consListState.value = GetAssignedConsumersEvent.Empty
    }


    //////

    // Sealed class to represent different states of the login process
    sealed class GetCallLogsEvent {
        object Empty : GetCallLogsEvent()
        object Loading : GetCallLogsEvent()
        data class Success(val resultText: List<CallLogEntry>?) : GetCallLogsEvent()
        data class Failure(val errorText: String) : GetCallLogsEvent()
    }

    // Mutable StateFlow to hold the current state of login
    private val _callLogState = MutableStateFlow<GetCallLogsEvent>(GetCallLogsEvent.Empty)

    // Exposed immutable StateFlow for composable to observe
    val callLogState: StateFlow<GetCallLogsEvent> = _callLogState
    // Function to handle user login
    fun getCallLogs(contentResolver: ContentResolver,
                    startDate: Long,
                    endDate: Long,
                    numbers: List<String>) {
        viewModelScope.launch(dispatchers.io) {
            _callLogState.value = GetCallLogsEvent.Loading // Set loading state
            when (val result = repository.getCallLogs(
                contentResolver,
                startDate,
                endDate,
                numbers
            )) {
                is Resource.Success -> {
                    if (result.data?.isEmpty() != true) {
                        _callLogState.value = GetCallLogsEvent.Success(result.data)
                    } else {
                        _callLogState.value = GetCallLogsEvent.Failure("No record found !")
                    }
                }
                is Resource.Error -> {
                    _callLogState.value = GetCallLogsEvent.Failure(result.message ?: "Network Error")
                }
            }
        }
    }

    // Reinitialize login state
    fun reInitCallLogs() {
        _callLogState.value = GetCallLogsEvent.Empty
    }


}
