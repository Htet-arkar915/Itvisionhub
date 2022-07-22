package com.htetarkarlinn.itvisionhub.Models

import android.net.Uri

data class CampModel(
    var start_date: String, var end_date: String,
    var camp_name: String,
    var time: String,
    var description: String, var images: ArrayList<Uri?>?
)
