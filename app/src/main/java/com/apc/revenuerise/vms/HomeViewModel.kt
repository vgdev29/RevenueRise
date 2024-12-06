package com.apc.revenuerise.vms

import android.content.ContentResolver
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apc.lossreduction.dataClasses.geocode.GeocodeResponse
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dataClasses.LoginRequest
import com.apc.revenuerise.dataClasses.LoginResponse
import com.apc.revenuerise.dataClasses.ServerCallLogsRes
import com.apc.revenuerise.dataStore.UserPreferences
import com.apc.revenuerise.dispatchers.DispatcherTypes
import com.apc.revenuerise.repository.home.HomeDefRepo
import com.apc.solarsuvidha.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeDefRepo,
    private val dispatchers: DispatcherTypes,
    private val userPreferences: UserPreferences

) : ViewModel() {
    var lat = MutableLiveData<String?>("")
    var long = MutableLiveData<String?>("")

    private val _userState = MutableStateFlow<String?>("-1")

    val userState: StateFlow<String?> = _userState

    init {
        // Load user ID on ViewModel initialization
        viewModelScope.launch {
            _userState.value = userPreferences.user.first()
        }
    }

    fun saveUser(userId: String) {
        viewModelScope.launch {
            Log.d("USER>>vm", "saveUser: $userId")
            userPreferences.saveUser(userId)
            _userState.value = userId
        }
    }

    fun clearUser() {
        viewModelScope.launch {
            userPreferences.clearUser()
            _userState.value = null
        }
    }

    // Sealed class to represent different states of the login process
    sealed class GetAssignedConsumersEvent {
        object Empty : GetAssignedConsumersEvent()
        object Loading : GetAssignedConsumersEvent()
        data class Success(val resultText: List<Consumer>?) : GetAssignedConsumersEvent()
        data class Failure(val errorText: String) : GetAssignedConsumersEvent()
    }

    // Mutable StateFlow to hold the current state of login
    private val _consListState = MutableStateFlow<GetAssignedConsumersEvent>(GetAssignedConsumersEvent.Empty)

    // Exposed immutable StateFlow for composable to observe
    val consListState: StateFlow<GetAssignedConsumersEvent> = _consListState

    // Function to handle user login
    fun getAssignedConsumers(uid: String) {
        viewModelScope.launch(dispatchers.io) {
            _consListState.value = GetAssignedConsumersEvent.Loading // Set loading state
            when (val result = repository.getAssignedConsumers(uid)) {
                is Resource.Success -> {
                    if (result.data!!.isNotEmpty()) {
                        _consListState.value = GetAssignedConsumersEvent.Success(result.data)
                    } else {
                        _consListState.value = GetAssignedConsumersEvent.Failure("No Data Found !")
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

    /////


    // Sealed class to represent different states of the login process
    sealed class GetCallLogsFromServerEvent {
        object Empty : GetCallLogsFromServerEvent()
        object Loading : GetCallLogsFromServerEvent()
        data class Success(val resultText: ServerCallLogsRes?) : GetCallLogsFromServerEvent()
        data class Failure(val errorText: String) : GetCallLogsFromServerEvent()
    }

    // Mutable StateFlow to hold the current state of login
    private val _serverCallLogsState = MutableStateFlow<GetCallLogsFromServerEvent>(GetCallLogsFromServerEvent.Empty)

    // Exposed immutable StateFlow for composable to observe
    val serverCallLogsState: StateFlow<GetCallLogsFromServerEvent> = _serverCallLogsState

    // Function to handle user login
    fun getServerCallLogs() {
        viewModelScope.launch(dispatchers.io) {
            _serverCallLogsState.value = GetCallLogsFromServerEvent.Loading // Set loading state
            when (val result = repository.getServerCallRecord()) {
                is Resource.Success -> {
                    if (result.data!!.isNotEmpty()) {
                        _serverCallLogsState.value = GetCallLogsFromServerEvent.Success(result.data)
                    } else {
                        _serverCallLogsState.value = GetCallLogsFromServerEvent.Failure("No Data Found !")
                    }
                }
                is Resource.Error -> {
                    _serverCallLogsState.value = GetCallLogsFromServerEvent.Failure(result.message ?: "Network Error")
                }
            }
        }
    }

    // Reinitialize login state
    fun reInitServerCallLogs() {
        _serverCallLogsState.value = GetCallLogsFromServerEvent.Empty
    }



    // Sealed class to represent different states of the login process
    sealed class LoginEvent {
        object Empty : LoginEvent()
        object Loading : LoginEvent()
        data class Success(val resultText: LoginResponse?) : LoginEvent()
        data class Failure(val errorText: String) : LoginEvent()
    }
    // Mutable StateFlow to hold the current state of login
    private val _loginState = MutableStateFlow<LoginEvent>(LoginEvent.Empty)

    // Exposed immutable StateFlow for composable to observe
    val loginState: StateFlow<LoginEvent> = _loginState

    // Function to handle user login
    fun loginUser(loginRequest: LoginRequest) {
        viewModelScope.launch(dispatchers.io) {
            _loginState.value = LoginEvent.Loading
            when (val result = repository.loginUser(loginRequest)) {
                is Resource.Success -> {
                    if (!result.data!!.error) {
                        _loginState.value = LoginEvent.Success(result.data)
                    } else {
                        _loginState.value = LoginEvent.Failure(result.data.message ?: "Unknown Error")
                    }
                }
                is Resource.Error -> {
                    _loginState.value = LoginEvent.Failure(result.message ?: "Network Error")
                }
            }
        }
    }


    // Reinitialize login state
    fun reInitLogin() {
        _loginState.value = LoginEvent.Empty
    }


    //



    sealed class CalculateDistancesEvent {
        object Empty : CalculateDistancesEvent()
        object Loading : CalculateDistancesEvent()
        data class Success(val updatedCons: List<Consumer>) : CalculateDistancesEvent()
        data class Failure(val errorText: String) : CalculateDistancesEvent()
    }

    private val _distanceState = MutableStateFlow<CalculateDistancesEvent>(CalculateDistancesEvent.Empty)
    val distanceState: StateFlow<CalculateDistancesEvent> = _distanceState

    fun calculateDistances(
        sourceLat: Double,
        sourceLon: Double,
        cons: GetConsumersForCallingRes
    ) {
        viewModelScope.launch(dispatchers.io) {
            _distanceState.value = CalculateDistancesEvent.Loading
            try {

                val updatedCons = cons.consumers.map { con ->
                    var dist =0.0
                    if(con.LATITUDE>0.0 && con.LONGTIUDE>0.0){
                        dist = haversineGreatCircleDistance(sourceLat, sourceLon, con.LATITUDE, con.LONGTIUDE)

                    }

                    con.distance = dist
                    con
                }
                _distanceState.value = CalculateDistancesEvent.Success(updatedCons)
            } catch (e: Exception) {
                _distanceState.value = CalculateDistancesEvent.Failure(e.message ?: "Error occurred")
            }
        }
    }

    fun reInitDistanceState() {
        _distanceState.value = CalculateDistancesEvent.Empty
    }

    private fun haversineGreatCircleDistance(
        latitudeFrom: Double,
        longitudeFrom: Double,
        latitudeTo: Double,
        longitudeTo: Double,
        earthRadius: Double = 6371000.0
    ): Double {
        val latFrom = Math.toRadians(latitudeFrom)
        val lonFrom = Math.toRadians(longitudeFrom)
        val latTo = Math.toRadians(latitudeTo)
        val lonTo = Math.toRadians(longitudeTo)

        val latDelta = latTo - latFrom
        val lonDelta = lonTo - lonFrom

        val a = sin(latDelta / 2).pow(2) +
                cos(latFrom) * cos(latTo) * sin(lonDelta / 2).pow(2)

        val angle = 2 * asin(sqrt(a))
        return angle * earthRadius
    }

    // GeoCode

    sealed class GeocodeEvent {
        object Empty : GeocodeEvent()
        object Loading : GeocodeEvent()
        data class Success(val resultText: GeocodeResponse?) : GeocodeEvent()
        data class Failure(val errorText: String) : GeocodeEvent()

    }
    private val _geocodeState = MutableStateFlow<GeocodeEvent>(GeocodeEvent.Empty)
    val geocodeState: StateFlow<GeocodeEvent> = _geocodeState
    fun geocode(key: String, address: String) {
        viewModelScope.launch(dispatchers.io) {
            _geocodeState.value = GeocodeEvent.Loading // Set loading state
            when (val result = repository.geocode(key, address)) {
                is Resource.Success -> {
                    if (result.data!!.status=="OK" && result.data.results.isNotEmpty()) {
                        _geocodeState.value = GeocodeEvent.Success(result.data)
                    } else {
                        _geocodeState.value = GeocodeEvent.Failure(result.data.status)


                    }
                }
                is Resource.Error ->{
                    _geocodeState.value = GeocodeEvent.Failure("Geocode Failed")

                }
            }
        }

    }
    fun reInitGeocode() {
        _geocodeState.value = GeocodeEvent.Empty
    }

}
