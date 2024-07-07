package com.awesome.shopnowuser.UI

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.R
import com.awesome.shopnowuser.Adapters.SimilarProductsAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailFragment : Fragment(), SimilarProductsAdapter.OnItemClickListener {

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT, product)
            fragment.arguments = args
            Log.d("ProductDetailFragment", "newInstance: $product")
            return fragment
        }
    }

    private var product: Product? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var allProducts: MutableList<Product>
    private lateinit var similarProductsAdapter: SimilarProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getParcelable(ARG_PRODUCT)
            Log.d("ProductDetailFragment", "onCreate: $product")
        }
        firestore = FirebaseFirestore.getInstance()
        allProducts = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productName: TextView = view.findViewById(R.id.productName)
        val productDescription: TextView = view.findViewById(R.id.productDescription)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val buttonDecrease: Button = view.findViewById(R.id.button_decrease)
        val buttonIncrease: Button = view.findViewById(R.id.button_increase)
        val textQuantity: TextView = view.findViewById(R.id.text_quantity)
        val buttonAddToCart: Button = view.findViewById(R.id.button_add_to_cart)
        val buttonBuyNow: Button = view.findViewById(R.id.button_buy_now)
        val similarProductsRecyclerView: RecyclerView = view.findViewById(R.id.similarProductsRecyclerView)
        var quantity = 1

        product?.let { currentProduct ->
            productName.text = currentProduct.name
            productDescription.text = currentProduct.description
            productPrice.text = currentProduct.price.toString()
            Glide.with(this).load(currentProduct.imageUrl).into(productImage)

            // Expandable description
            var isExpanded = false
            productDescription.setOnClickListener {
                if (isExpanded) {
                    productDescription.maxLines = 3
                    productDescription.ellipsize = TextUtils.TruncateAt.END
                } else {
                    productDescription.maxLines = Int.MAX_VALUE
                    productDescription.ellipsize = null
                }
                isExpanded = !isExpanded
            }

            // Quantity controls
            buttonDecrease.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    textQuantity.text = quantity.toString()
                }
            }

            buttonIncrease.setOnClickListener {
                quantity++
                textQuantity.text = quantity.toString()
            }

            buttonAddToCart.setOnClickListener {
                addToCart(currentProduct, quantity)
            }

            buttonBuyNow.setOnClickListener {
                val fragment = CartFragment()
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
            }


        similarProductsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        similarProductsAdapter = SimilarProductsAdapter(allProducts, this)
        similarProductsRecyclerView.adapter = similarProductsAdapter

        fetchSimilarProducts()
    }

    private fun fetchSimilarProducts() {
        // Fetch similar products from Firestore and update the adapter
        firestore.collection("products").get()
            .addOnSuccessListener { documents ->
                allProducts.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    allProducts.add(product)
                }
                similarProductsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("ProductDetailFragment", "Error fetching similar products", e)
            }
    }



    private fun addToCart(product: Product, quantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot.exists()) {
                    val role = userSnapshot.getString("role")

                    if (role == "user") {
                        val cartItemDocRef = userDocRef.collection("cartItems").document(product.id)

                        // Check if the product is already in the cart
                        cartItemDocRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // Product is already in the cart, so update the quantity
                                    val currentQuantity = documentSnapshot.getLong("quantity")?.toInt() ?: 0
                                    val newQuantity = currentQuantity + quantity
                                    cartItemDocRef.update("quantity", newQuantity)
                                        .addOnSuccessListener {
                                            Log.d("ProductDetailFragment", "Product quantity updated in cart")
                                            refreshFragment()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("ProductDetailFragment", "Error updating product quantity: ${e.message}", e)
                                            Toast.makeText(context, "Failed to update product quantity", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // Product is not in the cart, add it
                                    val cartItem = hashMapOf(
                                        "id" to product.id,
                                        "name" to product.name,
                                        "quantity" to quantity,
                                        "price" to product.price,
                                        "imageUrl" to product.imageUrl
                                    )
                                    cartItemDocRef.set(cartItem)
                                        .addOnSuccessListener {
                                            Log.d("ProductDetailFragment", "Product added to cart")
                                            refreshFragment()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("ProductDetailFragment", "Error adding product to cart: ${e.message}", e)
                                            Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ProductDetailFragment", "Error checking product in cart: ${e.message}", e)
                                Toast.makeText(context, "Failed to check product in cart", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.e("addToCart", "User is not authorized to add products to cart (role: $role)")
                        Toast.makeText(context, "Only users can add products to cart", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("addToCart", "User document does not exist")
                    Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("addToCart", "Error getting user document: ${e.message}", e)
                Toast.makeText(context, "Failed to get user document", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }














    private fun refreshFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
    }

    override fun onItemClick(product: Product) {
        val fragment = newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
