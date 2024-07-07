// ProductAdapter.kt
package com.awesome.shopnowuser.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.R
import com.awesome.shopnowuser.UI.ProductsScreenFragment
import com.bumptech.glide.Glide

class ProductAdapter(
    private var productList: MutableList<Product>,
    private val itemClickListener: ProductsScreenFragment
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price.toString()
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(product)
        }
    }

    fun updateData(newProductList: MutableList<Product>) {
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = productList.size
}
