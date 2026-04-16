package com.purnendu.hiremyidea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.purnendu.hiremyidea.ui.appearingColumn.VisibleColumn
import com.purnendu.hiremyidea.ui.theme.HireMyIdeaTheme
import com.purnendu.hiremyidea.ui.insights.InsightsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        setContent {
            HireMyIdeaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // InsightsScreen()
                    //AnalogClock()


                    val isIconColumnVisible = remember { mutableStateOf(false) }

                    Scaffold(
                        floatingActionButton = {

                            FloatingActionButton(
                                interactionSource = remember { MutableInteractionSource() },
                                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                                containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = if(isIconColumnVisible.value) 0.1f else 1f ),
                                onClick = {
                                    isIconColumnVisible.value = true
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "addIcon")
                            }


                        }
                    ) { paddingValues ->

                        Box(modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(color = Color.Gray.copy(alpha =if(isIconColumnVisible.value) 1f else 0.1f)))
                        {

                            VisibleColumn(
                                modifier = Modifier.align(BottomEnd).padding(bottom = 20.dp),
                                onCloseClick = {isIconColumnVisible.value = false},
                                isVisible = isIconColumnVisible.value
                            )


                        }


                    }


                }
            }
        }
    }
}
