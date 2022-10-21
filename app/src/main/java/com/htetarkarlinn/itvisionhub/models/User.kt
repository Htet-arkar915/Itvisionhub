package com.htetarkarlinn.itvisionhub.models

import java.io.Serializable

data class User(var name : String, var phone :String,
                var email :String, var password : String,
                var img :String,var role :String,
                var degree : String,var camp : String,
                var request : String,var noti: String):Serializable
