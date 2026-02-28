package com.JochemKuipers.irawansupdatechecker

import android.os.Bundle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.JochemKuipers.irawansupdatechecker.ui.RomDetailContent
import com.JochemKuipers.irawansupdatechecker.ui.RomDrawerContent
import com.JochemKuipers.irawansupdatechecker.ui.theme.IrawansUpdateCheckerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            @OptIn(ExperimentalMaterial3Api::class)
            IrawansUpdateCheckerTheme {
                val viewModel: com.JochemKuipers.irawansupdatechecker.ui.RomViewModel = viewModel()
                val state by viewModel.state.collectAsState()
                var sidebarExpanded by remember { mutableStateOf(true) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Irawan's ROM updates") },
                            navigationIcon = {
                                IconButton(onClick = { sidebarExpanded = !sidebarExpanded }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = if (sidebarExpanded) "Hide sidebar" else "Show sidebar"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedVisibility(
                            visible = sidebarExpanded,
                            enter = slideInHorizontally(animationSpec = tween(200)) { -it },
                            exit = slideOutHorizontally(animationSpec = tween(200)) { -it }
                        ) {
                            RomDrawerContent(
                                devices = state.devices,
                                selectedPost = state.selectedPost,
                                onPostSelected = { viewModel.selectPost(it) },
                                modifier = Modifier
                                    .width(280.dp)
                                    .fillMaxHeight()
                                    .border(
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        shape = RoundedCornerShape(0.dp)
                                    )
                            )
                        }
                        RomDetailContent(
                            post = state.selectedPost,
                            loading = state.loading,
                            error = state.error,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
