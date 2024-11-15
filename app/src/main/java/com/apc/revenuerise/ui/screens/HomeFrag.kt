package com.apc.revenuerise.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.vms.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


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
        vm.getAssignedConsumers(123)
    }

    @Composable
    fun TestUi() {
        Text(
            text = "Test",

            )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConsumerList(consumerViewModel: HomeViewModel) {
        val consumersState by consumerViewModel.consListState.collectAsState()
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
                            "Consumers for Calling",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF3A7BD5),
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
                                Color(0xFF3A7BD5),
                                Color(0xFF00D2FF)
                            ), // Gradient colors
                            startY = 0.0f,
                            endY = 1000.0f
                        )
                    )
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),

                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    when (consumersState) {
                        is HomeViewModel.GetAssignedConsumersEvent.Success -> {
                            val consumers =
                                (consumersState as HomeViewModel.GetAssignedConsumersEvent.Success).resultText?.consumers
                            consumers?.let {
                                items(it.size) { index ->
                                    ConsumerItem(consumer = consumers[index])
                                }
                            }
                        }

                        is HomeViewModel.GetAssignedConsumersEvent.Loading -> {
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

                        is HomeViewModel.GetAssignedConsumersEvent.Failure -> {
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

                        HomeViewModel.GetAssignedConsumersEvent.Empty -> {
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
                    .shadow(2.dp, RoundedCornerShape(8.dp)),// Offset to float half outside the main card
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
                    IconButton(
                        onClick = {
                            navController.navigate(HomeFragDirections.actionHomeFragToCallLogFrag(consumer.MOBILE_NO))
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