package com.apc.revenuerise.ui.screens

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.vms.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CallLogFrag:Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private val vm: HomeViewModel by viewModels()
    private lateinit var mob:String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    //    TestUi()

                    CallLogList()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        mob=CallLogFragArgs.fromBundle(requireArguments()).mob
        val startCalendar = Calendar.getInstance()
        startCalendar.set(2024, Calendar.NOVEMBER, 1, 0, 0, 0)
        val startDate = startCalendar.timeInMillis

// Set end date to October 31, 2023
        val endCalendar = Calendar.getInstance()
        endCalendar.set(2024, Calendar.NOVEMBER, 12, 23, 59, 59)
        val endDate = endCalendar.timeInMillis
// List of numbers to filter by
        val numbers = listOf(mob)
        //val numbers = listOf("+919212033808", "01140777777")

        vm.getCallLogs(
            mContext.contentResolver,
            startDate,
            endDate,
            numbers

        )


    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CallLogList() {
        val callLogsState by vm.callLogState.collectAsState()
        Scaffold(
            topBar = {
                TopAppBar(
                    actions = {
                        IconButton(onClick = {
                            navController.navigateUp()

                            //    vm1.clearUser()
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
                            "Call history for ${mob}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF3A7BD5),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (callLogsState) {
                    is HomeViewModel.GetCallLogsEvent.Success -> {
                        val callLogs =
                            (callLogsState as HomeViewModel.GetCallLogsEvent.Success).resultText
                        callLogs?.let {
                            item{
                                Text(
                                    "Total Calls: ${it.size}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            items(it.size) { index ->
                                CallLogCard(callLog = callLogs[index])

                            }
                        }
                    }

                    is HomeViewModel.GetCallLogsEvent.Loading -> {
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

                    is HomeViewModel.GetCallLogsEvent.Failure -> {
                        val callLogsErr =
                            (callLogsState as HomeViewModel.GetCallLogsEvent.Failure).errorText
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = callLogsErr,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    HomeViewModel.GetCallLogsEvent.Empty -> {
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

                /*   items(callLogs.size) { index ->
                   CallLogCard(callLog = callLogs[index])
               }*/
            }
        }
    }

    @Composable
    fun CallLogCard(callLog: CallLogEntry) {
        Card(
            onClick = {






                //      vm1.consumer.value=consumer
                //     vm1.inspectionData.value?.conId = consumer.id
                //    navController.navigate(R.id.action_customerListFrag_to_actionListFrag)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            border = BorderStroke(2.dp, Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Date: ${convertDate(callLog.date)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    text = "Duration: ${formatDuration(callLog.duration)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }

    private fun convertDate(dateMillis: Long): String {
        val date = Date(dateMillis)
        return DateFormat.format("dd MMM yyyy, hh:mm a", date).toString()
    }

    private fun formatDuration(durationSeconds: Long): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return String.format("%02d:%02d min", minutes, seconds)
    }
}