package com.apc.revenuerise.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.vms.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FiledVisitFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private val vm: HomeViewModel by activityViewModels()
    private var lat: Double = 26.8468229
    private var long: Double = 80.8635901

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    //    TestUi()
                    ConsumerList(vm)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        if (vm.lat.value?.isNotEmpty() == true) {
            lat = vm.lat.value?.toDouble()!!
            long = vm.long.value?.toDouble()!!
        }

        //      navController.navigate(HomeFragDirections.actionCustomerListFragToPerformSignatureFrag())
    }

    @Composable
    fun TestUi() {
        Text(
            text = "Test",

            )
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun ConsumerList(consumerViewModel: HomeViewModel) {

        var showDialog by remember { mutableStateOf(false) }

        val user by vm.userState.collectAsState()
        val updatedConsState by vm.distanceState.collectAsState()


        when (user) {
            "-1" -> {
                // Show loading spinner while user state is being loaded
                CircularProgressIndicator()
            }

            else -> {
                if (user == null) {
                    Log.d("USER>>", "NULL")
                    // User is not logged in, navigate to LoginScreen

                    navController.navigate(HomeFragDirections.actionHomeFragToLoginFrag())

                } else {
                    // User is logged in, navigate to HomeScreen
                    vm.getAssignedConsumers(user!!)

                }
            }
        }




        val consumersState by consumerViewModel.consListState.collectAsState()
        when (consumersState) {
            is HomeViewModel.GetAssignedConsumersEvent.Success -> {
                val consumers =
                    (consumersState as HomeViewModel.GetAssignedConsumersEvent.Success).resultText
                val mCons = GetConsumersForCallingRes(consumers!!)
                if (consumers.isNotEmpty()) {
                        vm.calculateDistances(lat, long, mCons)
                    } else {
                        vm.clearUser()
                        Toast.makeText(mContext, "No Data found for user !", Toast.LENGTH_SHORT).show()
                        //  navController.navigate(HomeFragDirections.actionHomeFragToLoginFrag())
                    }


            }

            is HomeViewModel.GetAssignedConsumersEvent.Loading -> {

            }

            is HomeViewModel.GetAssignedConsumersEvent.Failure -> {

            }

            HomeViewModel.GetAssignedConsumersEvent.Empty -> {

            }
        }

        val geocodeState by consumerViewModel.geocodeState.collectAsState()
        when (geocodeState) {
            is HomeViewModel.GeocodeEvent.Success -> {
                val geocode =
                    (geocodeState as HomeViewModel.GeocodeEvent.Success).resultText
                val result = geocode?.results?.get(0)
                val conLat = result?.geometry?.location?.lat
                val conLong = result?.geometry?.location?.lng
                showDialog = false
                //   val uri = "https://www.google.com/maps/dir/?api=1&origin=$lat,$long&destination=$conLat,$conLong"
                val uri =
                    "https://www.google.com/maps/dir/?api=1&origin=current location&destination=$conLat,$conLong"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(intent)


            }

            is HomeViewModel.GeocodeEvent.Loading -> {
                showDialog = true


            }

            is HomeViewModel.GeocodeEvent.Failure -> {
                showDialog = false

                Toast.makeText(mContext, "Geocode Error", Toast.LENGTH_SHORT).show()

            }

            HomeViewModel.GeocodeEvent.Empty -> {

            }
        }


        /*  val user by vm1.userState.collectAsState()


        when (user) {
            0 -> {
                // Show loading spinner while user state is being loaded
                CircularProgressIndicator()
            }

            else -> {
                if (user == null) {
                    // User is not logged in, navigate to LoginScreen
                    navController.navigate(HomeFragDirections.actionCustomerListFragToLoginFrag())

                } else {
                    // User is logged in, navigate to HomeScreen
                    vm1.inspectionData.value?.capturedBy = user!!
                    vm.getAssignedConsumers(user!!)

                }
            }
        }
*/
        Scaffold(
            topBar = {
                TopAppBar(
                    actions = {
                        IconButton(onClick = {

                            vm.clearUser()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    },


                    modifier = Modifier.border(2.dp, Color.Black),
                    //BorderStroke(2.dp, Color.Black),
                    title = {
                        Text(
                            "Suspected Consumers",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFFFF9800),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFF9800),

                                Color(0xFFB9B9B9),

                                //         Color(0xFFFF9800),
                                //  Color(0xFFFFFFFF),
                                Color(0xFFFFFFFF)

                            ), // Gradient colors
                            startY = 0.0f,
                            //         endY = 1000.0f
                        )
                    )
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),

                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    when (updatedConsState) {
                        is HomeViewModel.CalculateDistancesEvent.Success -> {
                            val consumers =
                                (updatedConsState as HomeViewModel.CalculateDistancesEvent.Success).updatedCons
                            if (consumers.isNotEmpty()) {


                                // Map to categorize items



                                items(consumers.size) { index ->
                                    //    val con=consumers[index]
                                    ConsumerItem(
                                        consumer = consumers[index],
                                        index
                                    )

                                }
                                showDialog = false

                            } else {
                                vm.clearUser()
                                Toast.makeText(
                                    mContext,
                                    "No Data found for user !",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //  navController.navigate(HomeFragDirections.actionHomeFragToLoginFrag())
                            }


                        }

                        is HomeViewModel.CalculateDistancesEvent.Loading -> {
                            showDialog = true
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is HomeViewModel.CalculateDistancesEvent.Failure -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Failed to load consumers",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            showDialog = false

                        }

                        HomeViewModel.CalculateDistancesEvent.Empty -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No consumers available",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        ProgressDialog(isDialogOpen = showDialog)

    }

    @Composable
    fun ProgressDialog(isDialogOpen: Boolean) {
        if (isDialogOpen) {
            Dialog(onDismissRequest = {}) {
                Surface(
                    modifier = Modifier
                        .size(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.Blue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Loading...")
                    }
                }
            }
        }
    }

    @Composable
    fun ConsumerItem(consumer: Consumer, idx: Int) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Card content
            Card(
                onClick = {
                    val action = HomeFragDirections.actionHomeFragToConsumerDetailFrag(consumer)
                    navController.navigate(action)


                    //      vm1.consumer.value=consumer
                    //     vm1.inspectionData.value?.conId = consumer.id
                    //    navController.navigate(R.id.action_customerListFrag_to_actionListFrag)
                },
                modifier = Modifier
                    .fillMaxWidth(),

                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {


                    if (consumer.distance > 0)
                        Text(
                            text = "Approx. Distance : ${
                                String.format("%.2f", consumer.distance / 1000).toDouble()
                            } km(s)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),

                            )

                    Text(
                        text = "${idx + 1}.)  ${consumer.NAME}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(4.dp),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.labelLarge.copy(
                            //    letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 28.sp
                        )
                    )
                    Text(
                        text = "Acc. ID: ${consumer.ACCT_ID}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                        Text(
                            text = "SUBSTATION: ${consumer.SUBSTATION}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                }
            }
            // Floating Action Button (FAB)
            if ((consumer.LATITUDE > 0 && consumer.LONGTIUDE > 0) || consumer.ADDRESS.isNotEmpty())
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)// Align it to the center-right
                        .offset(x = 24.dp, y = 0.dp)
                        .clip(RoundedCornerShape(8.dp)) // Circular image shape
                        .border(
                            2.dp,
                            Color.Black,
                            RoundedCornerShape(8.dp)
                        ) // Border around the image
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .clickable {
                            if (consumer.LATITUDE > 0 && consumer.LONGTIUDE > 0) {
                                //         val uri = "https://www.google.com/maps/dir/?api=1&origin=$lat,$long&destination=${consumer.LATITUDE},${consumer.LONGTIUDE}"
                                val uri =
                                    "https://www.google.com/maps/dir/?api=1&origin=current location&destination=${consumer.LATITUDE},${consumer.LONGTIUDE}"

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                startActivity(intent)
                            } else
                                vm.geocode(
                                    "AIzaSyDtj4Bwn_vqj0Dq7B--q51phjr39jYYAKA",
                                    consumer.ADDRESS
                                )

                            //    uri = "https://www.google.com/maps/dir/?api=1&origin=$lat,$long&destination=${Uri.encode(consumer.ADDRESS)}"

                            /* val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                             intent.setPackage("com.google.android.apps.maps")
                             if (intent.resolveActivity(mContext.packageManager) != null) {
                                 startActivity(intent)
                             } else {
                                 Toast.makeText(mContext, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show()
                             }*/


                        },// Offset to float half outside the main card
                    // Optional border
                    shape = RoundedCornerShape(8.dp), // Circular shape to mimic a FAB
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Elevation to float it above the card
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Share",
                        tint = Color.Unspecified,// Prevent automatic tinting

                        modifier = Modifier
                            .size(40.dp) // Icon size
                        // Padding inside the card
                    )
                }

        }
    }


}