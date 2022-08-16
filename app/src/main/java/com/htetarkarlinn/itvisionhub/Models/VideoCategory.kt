package com.htetarkarlinn.itvisionhub.Models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Keep
@Parcelize
data class VideoCategory(
    @SerializedName("name")
    var name : String?="",
    @SerializedName("forShow")
    var forShow: String?="",
    @SerializedName("videoList")
    var videoList : List<VideoModel>? = null
): Parcelable

