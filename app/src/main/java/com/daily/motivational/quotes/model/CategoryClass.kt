package com.daily.motivational.quotes.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CategoryClass : Serializable {

    @field:SerializedName("name")
    val name: String? = null

    @field:SerializedName("id")
    val id: String? = null

    @field:SerializedName("url")
    val url: String? = null
}
