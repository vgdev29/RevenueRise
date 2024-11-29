package com.apc.revenuerise.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.revenuerise.R
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.ui.screens.HomeFragDirections
import com.apc.revenuerise.vms.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class HomeFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private val vm: HomeViewModel by viewModels()

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
        //      navController.navigate(HomeFragDirections.actionCustomerListFragToPerformSignatureFrag())

        vm.getServerCallLogs()
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
        val user by vm.userState.collectAsState()


        when (user) {
            "-1" -> {
                // Show loading spinner while user state is being loaded
                CircularProgressIndicator()
            }

            else -> {
                if (user == null) {
                    Log.d("USER>>","NULL")
                    // User is not logged in, navigate to LoginScreen
                    navController.navigate(HomeFragDirections.actionHomeFragToLoginFrag())

                } else {
                    // User is logged in, navigate to HomeScreen
                    vm.getAssignedConsumers(user!!)

                }
            }
        }

        var selectedFilter by remember { mutableStateOf("All") }

        // Combine the two StateFlows
        val combinedState = combine(
            vm.consListState,
            vm.serverCallLogsState
        ) { catState, inspectionDataState ->
            Pair(catState, inspectionDataState)
        }.collectAsState(
            initial = Pair(
                HomeViewModel.GetAssignedConsumersEvent.Empty,
                HomeViewModel.GetCallLogsFromServerEvent.Empty
            )
        )
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
                        if(user!=null && user!="-1") {
                            IconButton(onClick = {
                                vm.reInitAssignedConsList()
                                vm.reInitServerCallLogs()
                                vm.getAssignedConsumers(user!!)
                                vm.getServerCallLogs()
                                //    vm1.clearUser()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                            }
                        }
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
                            "Consumers for Calling",
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

                    val (conState, serverCallLogs) = combinedState.value

                    when {
                        conState is HomeViewModel.GetAssignedConsumersEvent.Success && serverCallLogs is HomeViewModel.GetCallLogsFromServerEvent.Success -> {
                            //       vm.reInitServerCallLogs()
                            //     vm.reInitAssignedConsList()
                            val consumers = conState.resultText!!.map {
                                it.copy(
                                    MOBILE_NO = it.MOBILE_NO.toString().removeSuffix(".0").trim()
                                )
                            }

                            val callLogs = serverCallLogs.resultText!!.map {
                                it.copy(MOBILE_NO = it.MOBILE_NO.toString().trim())
                            }

// Join lists by the common property "MOBILE_NO"
                            val joinedList = consumers.mapNotNull { consumer ->
                                val callDetails = callLogs.filter {
                                    it.MOBILE_NO == consumer.MOBILE_NO
                                }
                                if (callDetails.isNotEmpty()) {
                                    val last = callDetails.size - 1
                                    consumer.callDuration = callDetails[last].CALL_DURATION
                                    consumer.callDate = callDetails[last].CALL_DATE_TIME
                                    if (callDetails[last].CALL_DURATION > 0) {
                                        consumer.callingStatus = 2
                                    } else {
                                        consumer.callingStatus = 1
                                    }
                                    consumer.callDetails = callDetails

                                    // consumer to callDetails

                                } else {
                                    null
                                }
                            }

                            Log.d("joinedListSize", joinedList.size.toString())
                            Log.d("callDetailsSize", callLogs.size.toString())
                            Log.d("consumersSize", consumers.size.toString())

                            //     Log.d("joinedList",joinedList.toString())
                            Log.d("callDetails", callLogs.toString())
                            Log.d("consumers", consumers.toString())
                            if (consumers.isNotEmpty()) {


                                // Map to categorize items
                                val categorizedItems = mapOf(
                                    "All" to consumers,
                                    "Not Dialed" to consumers.filter { it.callingStatus == 0 },
                                    "Not Connected" to consumers.filter { it.callingStatus == 1 },
                                    "Talked" to consumers.filter { it.callingStatus == 2 },
                                    //    "Category4" to consumers.filter { it.callingStatus == "Category4" }
                                )
                                item {
                                    Column {
                                        // ChipGroup equivalent
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            categorizedItems.keys.forEach { category ->
                                                FilterChip(
                                                    selected = selectedFilter == category,
                                                    onClick = { selectedFilter = category },
                                                    label = {
                                                        Text(
                                                            text = category,
                                                            fontSize = 14.sp,
                                                            style = TextStyle(
                                                                color = Color.Black
                                                            )
                                                        )
                                                    },
                                                    modifier = Modifier.padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                items(categorizedItems[selectedFilter]!!.size) { index ->
                                    //    val con=consumers[index]

                                    ConsumerItem(consumer = categorizedItems[selectedFilter]!![index])
                                }
                            }
                        }

                        conState is HomeViewModel.GetAssignedConsumersEvent.Success && serverCallLogs is HomeViewModel.GetCallLogsFromServerEvent.Failure -> {
                            //           vm.reInitServerCallLogs()
                            //           vm.reInitAssignedConsList()
                            val consumers = conState.resultText!!.map {
                                it.copy(
                                    MOBILE_NO = it.MOBILE_NO.toString().removeSuffix(".0").trim()
                                )
                            }




                            if (consumers.isNotEmpty()) {


                                // Map to categorize items
                                val categorizedItems = mapOf(
                                    "All" to consumers,
                                    "Not Dialed" to consumers.filter { it.callingStatus == 0 },
                                    "Not Connected" to consumers.filter { it.callingStatus == 1 },
                                    "Talked" to consumers.filter { it.callingStatus == 2 },
                                    //    "Category4" to consumers.filter { it.callingStatus == "Category4" }
                                )
                                item {
                                    Column {
                                        // ChipGroup equivalent
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            categorizedItems.keys.forEach { category ->
                                                FilterChip(
                                                    selected = selectedFilter == category,
                                                    onClick = { selectedFilter = category },
                                                    label = {
                                                        Text(
                                                            text = category,
                                                            fontSize = 14.sp,
                                                            style = TextStyle(
                                                                color = Color.Black
                                                            )
                                                        )
                                                    },
                                                    modifier = Modifier.padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                items(categorizedItems[selectedFilter]!!.size) { index ->
                                    //    val con=consumers[index]

                                    ConsumerItem(consumer = categorizedItems[selectedFilter]!![index])
                                }
                            }
                        }

                        conState is HomeViewModel.GetAssignedConsumersEvent.Loading -> {
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

                        conState is HomeViewModel.GetAssignedConsumersEvent.Failure -> {
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
                        }

                        conState is HomeViewModel.GetAssignedConsumersEvent.Empty -> {
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
    }

    @Composable
    fun ConsumerItem(consumer: Consumer) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Card content
            Card(
                onClick = {

                    val bundle = Bundle()
                    bundle.putParcelable("consumer", consumer)
                    //     navController.navigate(R.id.action_homeFrag_to_consumerDetailFrag,bundle)
                    try {
                        navController.navigate(
                            HomeFragDirections.actionHomeFragToConsumerDetailFrag(
                                consumer
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    //      vm1.consumer.value=consumer
                    //     vm1.inspectionData.value?.conId = consumer.id
                    //    navController.navigate(R.id.action_customerListFrag_to_actionListFrag)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                border = BorderStroke(2.dp, Color.Black),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (consumer.callingStatus > 0)
                        Text(
                            text = "Last called on: ${
                                getDate(
                                    consumer.callDate,
                                    "dd-MM-yy hh:mm a"
                                )
                            } for ${consumer.callDuration} sec(s)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Left,
                            /*style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )*/
                        )
                    Text(
                        text = consumer.NAME,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "CRN: ${consumer.ACCT_ID}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Address: ${consumer.ADDRESS}",
                        fontSize = 18.sp,
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
            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)// Align it to the center-right
                    .offset(x = 24.dp, y = 0.dp)
                    .clip(RoundedCornerShape(8.dp)) // Circular image shape
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp)) // Border around the image
                    .shadow(
                        2.dp,
                        RoundedCornerShape(8.dp)
                    ),// Offset to float half outside the main card

                // Optional border
                shape = RoundedCornerShape(8.dp), // Circular shape to mimic a FAB
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Elevation to float it above the card
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    IconButton(
                        onClick = {
                            val phoneNumber = consumer.MOBILE_NO
                            val intent = Intent(
                                Intent.ACTION_DIAL, Uri.parse(
                                    "tel:$phoneNumber"
                                )
                            )
                            startActivity(intent)
                        }
                    ) {
                        Icon(

                            imageVector = Icons.Outlined.Call,
                            //     painter = painterResource(id = Icons.Outlined.Call),
                            contentDescription = "Share",
                            tint = Color.Unspecified,// Prevent automatic tinting

                            modifier = Modifier
                                .size(35.dp) // Icon size
                            // Padding inside the card
                        )
                    }
                    if (consumer.callDetails != null) {
                        if (consumer.callDetails!!.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        HomeFragDirections.actionHomeFragToCallLogFrag(
                                            consumer
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    //     painter = painterResource(id = Icons.Outlined.Call),
                                    contentDescription = "Share",
                                    tint = Color.Unspecified,// Prevent automatic tinting

                                    modifier = Modifier
                                        .size(35.dp) // Icon size
                                    // Padding inside the card
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter: SimpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }


    override fun onResume() {
        super.onResume()
        if(vm.userState.value.toString()!="-1"){
            vm.reInitAssignedConsList()
            vm.reInitServerCallLogs()
            vm.getAssignedConsumers(vm.userState.value.toString())
            vm.getServerCallLogs()
        }

    }
}