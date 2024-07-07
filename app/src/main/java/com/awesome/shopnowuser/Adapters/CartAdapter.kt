package com.awesome.shopnowuser.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.R
import com.bumptech.glide.Glide


class CartAdapter(private val cartItems: List<Product>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.itemName.text = currentItem.name
        holder.itemQuantity.text = "Quantity: ${currentItem.quantity}"
        holder.itemPrice.text = "Price: ${currentItem.price}"

        // Load image using Glide
        Glide.with(holder.itemView)
            .load(currentItem.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
            .error(R.drawable.ic_launcher_foreground) // Image to show if loading fails
            .into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
