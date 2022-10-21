package com.htetarkarlinn.itvisionhub.models

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Keep
data class CampModel (
    @SerializedName("start_date")
    var start_date: String,
    @SerializedName("end_date")
    var end_date: String,
    @SerializedName("camp_name")
    var camp_name: String,
    @SerializedName("time")
    var time: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("images")
    var images: ArrayList<Uri?>?
) :  Serializable
