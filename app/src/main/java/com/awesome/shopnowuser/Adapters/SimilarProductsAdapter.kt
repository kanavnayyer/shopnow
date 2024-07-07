// SimilarProductsAdapter.kt
package com.awesome.shopnowuser.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.R
import com.bumptech.glide.Glide

class SimilarProductsAdapter(
    private val similarProducts: List<Product>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SimilarProductsAdapter.SimilarProductViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    class SimilarProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.similarProductImage)
        val productName: TextView = itemView.findViewById(R.id.similarProductName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_similar_product, parent, false)
        return SimilarProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimilarProductViewHolder, position: Int) {
        val product = similarProducts[position]
        holder.productName.text = product.name
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(product)
        }

        // Log the product being bound
        Log.d("SimilarProductsAdapter", "Binding product: $product")
    }

    override fun getItemCount() = similarProducts.size
}
