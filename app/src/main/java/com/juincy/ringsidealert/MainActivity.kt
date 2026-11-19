package com.juincy.ringsidealert

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Store.init(this)
        Notifs.channels(this)
        Work.start(this)
        setContent { RingsideTheme { AppRoot() } }
    }

    override fun onStart() {
        super.onStart()
        Store.foreground = true
        Store.tick++
    }

    override fun onStop() {
        super.onStop()
        Store.foreground = false
    }
}

@Composable
fun AppRoot() {
    val askNotif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) askNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
        val ok = withContext(Dispatchers.IO) { Repo.refresh(Store.app) }
        Store.err = if (ok) "" else Repo.lastError
        Store.reload()
        if (Store.au) {
            val r = withContext(Dispatchers.IO) { runCatching { Updater.latest() }.getOrNull() }
            if (r != null) {
                Store.rel = r
                if (Updater.isNewer(r.version, Updater.current(Store.app))) Store.upd = "Version ${r.version} is available."
            }
        }
    }
    BackHandler(enabled = Store.open != null) { Store.open = null }
    var splash by rememberSaveable { mutableStateOf(true) }
    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val cs = MaterialTheme.colorScheme
                    listOf(
                        "Shows" to Icons.Filled.DateRange,
                        "Events" to Icons.Filled.PlayArrow,
                        "Alerts" to Icons.Filled.Notifications,
                        "Favorites" to Icons.Filled.Star,
                        "Settings" to Icons.Filled.Settings,
                    ).forEachIndexed { i, (label, icon) ->
                        NavigationBarItem(
                            selected = Store.tab == i && Store.open == null,
                            onClick = { Store.tab = i; Store.open = null },
                            icon = { Icon(icon, label) },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = cs.primary,
                                selectedTextColor = cs.primary,
                                indicatorColor = cs.primary.copy(alpha = 0.16f),
                                unselectedIconColor = cs.onSurfaceVariant,
                                unselectedTextColor = cs.onSurfaceVariant,
                            ),
                        )
                    }
                }
            },
        ) { pad ->
            Box(Modifier.padding(pad)) {
                Crossfade(targetState = Pair(Store.open, Store.tab), label = "screen") { (open, tab) ->
                    when {
                        open != null -> DetailScreen(open)
                        tab == 0 -> ShowsScreen()
                        tab == 1 -> EventsScreen()
                        tab == 2 -> AlertsScreen()
                        tab == 3 -> FavoritesScreen()
                        else -> SettingsScreen()
                    }
                }
            }
        }
        if (splash) Splash { splash = false }
    }
}
