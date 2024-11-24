package com.apc.revenuerise.ui.screens


import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.revenuerise.dataClasses.Consumer
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ConsumerDetailFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var consumer: Consumer
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = ConsumerDetailFragArgs.fromBundle(requireArguments())
        val consumer = args.consumer
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    //    TestUi()
                    ConsumerDetails(consumer)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConsumerDetails(consumer: Consumer) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.border(2.dp, Color.Black),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF3A7BD5),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = {
                        Text(
                            consumer.NAME,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                )

            },
            bottomBar = {
                BottomAppBar {
                    Button(
                        onClick = {
                            //   navController.navigate(ConsumerDetailFragDirections.actionConsumerDetailFragToPhotoFrag())
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Next")
                    }
                }
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
                            ),
                            startY = 0.0f,
                            //       endY = 1000.0f
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
                ) {
                    // Reusable composable for displaying a heading-content pair
                    @Composable
                    fun InfoItem(heading: String, content: String) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                //     .padding(8.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = heading,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    // Using the InfoItem composable for each field
                    InfoItem("Name:", consumer.NAME)
                    InfoItem("Account ID:", consumer.ACCT_ID.toString())
                    InfoItem("Address:", consumer.ADDRESS)
                    InfoItem("Division Code:", consumer.DIV_CODE)
                    InfoItem("Bill Amount:", consumer.PRED_BILL.toString())
                    InfoItem("Bill Date:", convertDate(consumer.BILL_DATE))
                    InfoItem("SDO Code:", consumer.SDO_CODE)
                    InfoItem("Substation:", consumer.SUBSTATION)
                    InfoItem("Mobile Number:", consumer.MOBILE_NO)

                    Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom


                }
            }

        }
    }

    private fun convertDate(dateMillis: Long): String {
        val date = Date(dateMillis)
        return DateFormat.format("dd MMM yyyy", date).toString()
    }

}