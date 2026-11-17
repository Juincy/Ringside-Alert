package com.juincy.ringsidealert

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

object Store {
    lateinit var app: Context
    var foreground = false

    val shows = mutableStateListOf<Show>()
    val favs = mutableStateListOf<String>()
    val alerts = mutableStateListOf<String>()
    val cos = mutableStateListOf<String>()

    var tab by mutableIntStateOf(0)
    var open by mutableStateOf<String?>(null)
    var company by mutableStateOf("All")
    var brand by mutableStateOf("All")
    var day by mutableStateOf(LocalDate.now())
    var lead by mutableStateOf("30 min before")
    var theme by mutableStateOf("system")
    var au by mutableStateOf(true)
    var wf by mutableStateOf(true)
    var upd by mutableStateOf("Not checked yet")
    var rel by mutableStateOf<Updater.Release?>(null)
    var busy by mutableStateOf(false)
    var last by mutableStateOf(0L)
    var tick by mutableIntStateOf(0)

    private fun p() = app.getSharedPreferences("ra", 0)

    fun init(c: Context) {
        if (::app.isInitialized) return
        app = c.applicationContext
        val p = p()
        favs.addAll(p.getStringSet("favs", emptySet())!!)
        alerts.addAll(p.getStringSet("alerts", emptySet())!!)
        cos.addAll(p.getStringSet("cos", COMPANIES.toSet())!!)
        lead = p.getString("lead", lead)!!
        theme = p.getString("theme", theme)!!
        au = p.getBoolean("au", true)
        wf = p.getBoolean("wf", true)
        last = p.getLong("last", 0L)
        shows.addAll(Repo.cached(app))
    }

    fun save() {
        p().edit()
            .putStringSet("favs", favs.toSet())
            .putStringSet("alerts", alerts.toSet())
            .putStringSet("cos", cos.toSet())
            .putString("lead", lead)
            .putString("theme", theme)
            .putBoolean("au", au)
            .putBoolean("wf", wf)
            .apply()
        Alerts.reschedule(app)
    }

    fun toggle(list: MutableList<String>, id: String) {
        if (!list.remove(id)) list.add(id)
        save()
    }

    fun reload() {
        val list = Repo.cached(app)
        shows.clear()
        shows.addAll(list)
        last = p().getLong("last", 0L)
        Alerts.reschedule(app)
    }

    fun next(id: String): Show? {
        val now = java.time.ZonedDateTime.now().minusHours(3)
        return shows.filter { it.id == id && it.start.isAfter(now) }.minByOrNull { it.start }
            ?: shows.firstOrNull { it.id == id }
    }
}
