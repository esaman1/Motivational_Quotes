package com.daily.motivational.quotes.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageClass : Serializable {

	@field:SerializedName("image")
	val image: String? = null

	@field:SerializedName("bgcolor")
	val bgcolor: Any? = null

	@field:SerializedName("author")
	val author: String? = null

	@field:SerializedName("name")
	val name: String? = null

	@field:SerializedName("cname")
	val cname: String? = null

	@field:SerializedName("id")
	val id: String? = null

	@field:SerializedName("text")
	val text: Any? = null

	@field:SerializedName("type")
	val type: String? = null
}
