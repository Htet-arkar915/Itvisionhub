package com.htetarkarlinn.itvisionhub.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
@kotlinx.parcelize.Parcelize
data class VideoModel(
    @SerializedName("videoId")
    var videoId: String?="",
    @SerializedName("title")
    var title: String?=""
    ) : Parcelable
