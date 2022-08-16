package com.htetarkarlinn.itvisionhub.Models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Keep
@kotlinx.parcelize.Parcelize
data class VideoModel(
    @SerializedName("videoId")
    var videoId: String?="",
    @SerializedName("title")
    var title: String?=""
    ) : Parcelable
