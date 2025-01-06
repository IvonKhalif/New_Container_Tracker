package com.example.containertracker

import com.example.containertracker.data.container.models.Container
import com.google.gson.Gson

/**
 * Created by yovi.putra on 22/07/22"
 * Project name: Container Tracker
 **/


object Dummy {

    private val containerJson = """
        {
           "id_tracking_container":89,
           "so_number":"2110001051",
           "batch_id":"23062022",
           "code_container":"OOLU1538560",
           "color_container":null,
           "long_container":null,
           "wide_container":null,
           "tall_container":null,
           "unique_id_container":"OOLU1538560",
           "origin":null,
           "destination":null,
           "status":"UNFINISH",
           "seal_id":null,
           "rightside_condition":Hole,
           "leftside_condition":Bulging,
           "roofside_condition":null,
           "floorside_condition":null,
           "frontdoor_condition":null,
           "backdoor_condition":null,
           "voyage_id_out":null,
           "voyage_id_in":null,
           "history":[
              
           ]
        }
    """.trimIndent()

    val container: Container = Gson().fromJson(containerJson, Container::class.java)
}