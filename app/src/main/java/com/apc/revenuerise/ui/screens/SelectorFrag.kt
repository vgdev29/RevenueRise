package com.apc.revenuerise.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.apc.revenuerise.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectorFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SelectorScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @Composable
    fun SelectorScreen() {
        Scaffold { innerPadding ->
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
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1f),
                        border = BorderStroke(2.dp, Color.Black),

                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        //     elevation = 4.dp
                    ) {
                        Column (
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.logo), // Replace with your image resource
                                contentDescription = "Image",
                                modifier = Modifier
                                    //   .aspectRatio(1f)
                                    .weight(7f)
                            )
                            Text(
                                text = "REVENUE RECOVERY", // Replace with your title
                                fontSize = 24.sp, // Increased font size for better visibility
                                fontWeight = FontWeight.ExtraBold, // Bold text for emphasis

                                modifier = Modifier
                                    .weight(2f)
                                    .padding(8.dp),
                                //      .shadow(4.dp, shape = RectangleShape, clip = false), // Add subtle shadow for a 3D effect
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    letterSpacing = 1.5.sp, // Spacing for better readability
                                    fontFamily = FontFamily.Monospace // Apply a serif font for a more formal feel
                                )
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1f),


                        border = BorderStroke(2.dp, Color.Black),

                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        //     elevation = 4.dp
                    ) {
                        Column (
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.logo), // Replace with your image resource
                                contentDescription = "Image",
                                modifier = Modifier
                                 //   .aspectRatio(1f)
                                    .weight(7f)
                            )
                            Text(
                                text = "REVENUE RECOVERY", // Replace with your title
                                fontSize = 24.sp, // Increased font size for better visibility
                                fontWeight = FontWeight.ExtraBold, // Bold text for emphasis

                                modifier = Modifier
                                    .weight(2f)
                                    .padding(8.dp),
                                //      .shadow(4.dp, shape = RectangleShape, clip = false), // Add subtle shadow for a 3D effect
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    letterSpacing = 1.5.sp, // Spacing for better readability
                                    fontFamily = FontFamily.Monospace // Apply a serif font for a more formal feel
                                )
                            )
                        }

                    }

                }

            }
        }
    }
}
