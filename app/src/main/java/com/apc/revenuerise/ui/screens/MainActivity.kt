package com.apc.revenuerise.ui.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.PowerManager
import android.provider.CallLog
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.apc.revenuerise.R
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.databinding.ActivityMainBinding
import com.apc.revenuerise.service.CallMonitorService
import com.apc.revenuerise.ui.theme.RevenueRiseTheme
import com.apc.revenuerise.vms.HomeViewModel
import com.apc.solarsuvidha.util.PermissionHelper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() , NavController.OnDestinationChangedListener {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var perms: Array<String>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionHelper: PermissionHelper
    private var latitude: String? = ""
    private var longitude: String? = ""
    private val vm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   deleteDatabase("inspection_db")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = Navigation.findNavController(binding.root.findViewById(R.id.fragment))
        navController.setGraph(R.navigation.main_nav_graph)
        permissionHelper = PermissionHelper(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            perms = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_CALL_LOG,




                )
        } else {
            perms = arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.POST_NOTIFICATIONS,

                )
        }


        navController.addOnDestinationChangedListener(this)

        if (!permissionHelper.checkPermissions(perms)) {
            permissionHelper.requestPermissions(perms, 100)
        } else {
            lastLocation
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_CALL_LOG),
                    100
                )
            } else {
                val intent = Intent(this, CallMonitorService::class.java)
                startService(intent)
                val pm = getSystemService(PowerManager::class.java)
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    val intent2 = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        .setData(Uri.parse("package:$packageName"))
                    startActivity(intent2)
                }

            }
        }
        //    enableEdgeToEdge()
        /*        setContent {
            SolarSuvidhaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }*/
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        title = destination.label
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Granted !", Toast.LENGTH_LONG).show()
            lastLocation
            // The user granted the permission.
        } else {
            Toast.makeText(this, "Denied !", Toast.LENGTH_LONG).show()
            finish()

            // The user denied the permission.
        }
    }

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        get() {
            // check if permissions are given

            // check if location is enabled
            if (isLocationEnabled) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        Log.d("LAT>>>", location.latitude.toString())
                        Log.d("LONG>>>", location.longitude.toString())

                        try {
                            val addresses: List<Address>
                            val geocoder = Geocoder(this, Locale.getDefault())

                            addresses =
                                geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1

                                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            val city: String
                            val state: String
                            val address: String =
                                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses[0].locality
                            state = addresses[0].adminArea
                            val country: String = addresses[0].countryName
                            val postalCode: String = addresses[0].postalCode
                            val knownName: String = addresses[0].featureName
                            //     vm.address.value= "$city,$state"
                            //   vm.inspectionData.value?.address="$city,$state"


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //     vm.lat.value=latitude
                        //   vm.long.value=longitude
                        // vm.inspectionData.value?.latLng= "$latitude,$longitude"
                        //  latitudeTextView.setText(location.getLatitude() + "");
                        //longitTextView.setText(location.getLongitude() + "");
                    }
                }
            } else {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                //   startActivity(intent)

                val locationRequest = LocationRequest.create().apply {
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    interval = 10000
                    fastestInterval = 5000


                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val locationSettingsRequest = builder.build()
                val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
                val task = settingsClient.checkLocationSettings(locationSettingsRequest)


                task.addOnSuccessListener(
                    this,
                    OnSuccessListener<LocationSettingsResponse> { response ->
                        Log.d("LOC>>", "All location settings are satisfied.")
                        lastLocation
                        // All location settings are satisfied. The client can initialize location requests here.
                    })

                task.addOnFailureListener(this, OnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {
                            exception.startResolutionForResult(this, 1001)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                })
            }
        }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            /*     if (mLastLocation != null) {
                     getCurrentStation(
                         mLastLocation.latitude.toString(),
                         mLastLocation.longitude.toString()
                     )
                 }*/
            //  latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    }


// method to request for permissions

    // method to check
// if location is enabled
    private val isLocationEnabled: Boolean
        private get() {
            val locationManager =
                getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
    /*

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SolarSuvidhaTheme {
        Greeting("Android")
    }
}*/

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


fun getFilteredCallLogsByNumbers(
    contentResolver: ContentResolver,
    startDate: Long,
    endDate: Long,
    numbers: List<String>
): List<CallLogEntry> {
    val callLogEntries = mutableListOf<CallLogEntry>()

    // Prepare placeholders for the numbers list, e.g., (?, ?, ?)
    val numberPlaceholders = numbers.joinToString(",") { "?" }
    val selection = "${CallLog.Calls.TYPE} = ? AND ${CallLog.Calls.DATE} BETWEEN ? AND ? AND ${CallLog.Calls.NUMBER} IN ($numberPlaceholders)"

    // Combine the type, date range, and numbers into selectionArgs
    val selectionArgs = mutableListOf<String>().apply {
        add(CallLog.Calls.OUTGOING_TYPE.toString())
        add(startDate.toString())
        add(endDate.toString())
        addAll(numbers)
    }.toTypedArray()

    val cursor: Cursor? = contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        null,
        selection,
        selectionArgs,
        CallLog.Calls.DATE + " DESC"
    )

    cursor?.use {
        val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
        val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
        val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

        while (it.moveToNext()) {
            val number = it.getString(numberIndex)
            val type = it.getInt(typeIndex)
            val date = it.getLong(dateIndex)
            val duration = it.getLong(durationIndex)

            callLogEntries.add(CallLogEntry(number, type, date, duration))
        }
    }

    return callLogEntries
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RevenueRiseTheme {
        Greeting("Android")
    }

}