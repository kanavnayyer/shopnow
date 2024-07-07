// Product.kt
// Product.kt
package com.awesome.shopnowuser.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val name: String = "",
    val description: String = "",
    val price: Double =0.0,
    val imageUrl: String = "",
    val category: String = "", // Add category field
    val similarProducts: List<Product> = emptyList()
    , val id:String=""
    , val quantity:Int=0,
    val vendorId: String=""
) : Parcelable


