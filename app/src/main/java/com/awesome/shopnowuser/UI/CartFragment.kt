package com.awesome.shopnowuser.UI
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowuser.Adapters.CartAdapter
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<Product>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var buyButton: Button
    private lateinit var totalPriceTextView: TextView





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)


        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewCartItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItems = mutableListOf()
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        buyButton = view.findViewById(R.id.buttonBuy)
        totalPriceTextView = view.findViewById(R.id.textViewTotalPrice)

        buyButton.setOnClickListener {
transferCartItemsToVendors()
            val fragment = PurchaseFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()

clearCart()

        }




        fetchCartItems()

        return view
    }




    private fun transferCartItemsToVendors() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userCartItemsRef = firestore.collection("users").document(userId)
                .collection("cartItems")

            userCartItemsRef.get()
                .addOnSuccessListener { cartItemsSnapshot ->
                    val batch = firestore.batch()

                    for (cartItemDoc in cartItemsSnapshot.documents) {
                        val productId = cartItemDoc.id
                        val productVendorId = cartItemDoc.getString("vendorId")

                        if (productVendorId != null) {
                            // Query vendor with matching vendorId
                            firestore.collection("users")
                                .whereEqualTo("role", "vendor")
                                .whereEqualTo("vendorId", productVendorId)
                                .get()
                                .addOnSuccessListener { vendorsSnapshot ->
                                    if (vendorsSnapshot.isEmpty) {
                                        Log.e("TransferDataActivity", "No vendor found for product with ID: $productId")
                                        return@addOnSuccessListener
                                    }

                                    // Assume one vendor per vendorId for simplicity
                                    val vendorDoc = vendorsSnapshot.documents[0]
                                    val vendorId = vendorDoc.id

                                    // Destination reference for receivedProducts
                                    val vendorReceivedProductsRef = firestore
                                        .collection("users").document(vendorId)
                                        .collection("receivedProducts").document(productId)

                                    // Prepare product data to transfer
                                    val productData = hashMapOf(
                                        "name" to cartItemDoc.getString("name"),
                                        "quantity" to cartItemDoc.getLong("quantity"),
                                        // Add other necessary fields
                                    )

                                    // Add set operation to batch
                                    batch.set(vendorReceivedProductsRef, productData)

                                    // Optionally, delete from user's cartItems collection
                                    val userCartItemRef = userCartItemsRef.document(productId)
                                    batch.delete(userCartItemRef)

                                    // Commit the batch operation after loop completes
                                    batch.commit()
                                        .addOnSuccessListener {
                                            Log.d("TransferDataActivity", "Batch operation completed successfully")
                                            // Optionally, refresh UI or notify user of successful transfer


                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("TransferDataActivity", "Error performing batch operation: ${e.message}", e)
                                            // Handle failure to perform batch operation
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TransferDataActivity", "Error querying vendors: ${e.message}", e)
                                    // Handle failure to query vendors
                                }
                        } else {
                            Log.e("TransferDataActivity", "Product with ID: $productId does not have a vendorId")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TransferDataActivity", "Error fetching cart items: ${e.message}", e)
                    // Handle failure to fetch cart items
                }
        } else {
            Log.e("TransferDataActivity", "User not logged in")
            // Handle user not logged in
        }
    }










    private fun fetchCartItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid)
            userDocRef.collection("cartItems")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    cartItems.clear()
                    var totalPrice = 0.0

                    for (document in querySnapshot.documents) {
                        val productName = document.getString("name") ?: ""
                        val quantity = document.getLong("quantity")?.toInt() ?: 0
                        val price = document.getDouble("price") ?: 0.0
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val vendorId = document.getString("vendorId") ?: "" // Assuming vendorId is stored in Firestore

                        val cartItem = Product(productName, "", price, imageUrl, "", emptyList(), "", quantity,vendorId)
                        cartItems.add(cartItem)

                        totalPrice += quantity * price
                    }

                    totalPriceTextView.text = "Total: $${"%.2f".format(totalPrice)}"
                    cartAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("CartFragment", "Error fetching cart items: ${e.message}", e)
                    Toast.makeText(context, "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
                }
        }
    }







    private fun clearCart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid)
            userDocRef.collection("cartItems")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = FirebaseFirestore.getInstance().batch()

                    for (document in querySnapshot.documents) {
                        val cartItemRef = userDocRef.collection("cartItems").document(document.id)
                        batch.delete(cartItemRef)
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            Log.d("CartFragment", "Cart items deleted successfully")
                            Toast.makeText(requireContext(), "Cart items cleared", Toast.LENGTH_SHORT).show()
                            // Refresh UI or navigate to a different fragment/activity
                            cartItems.clear()
                            cartAdapter.notifyDataSetChanged()
                            totalPriceTextView.text = "Total: $0.00"
                        }
                        .addOnFailureListener { e ->
                            Log.e("CartFragment", "Error deleting cart items: ${e.message}", e)
                            Toast.makeText(requireContext(), "Failed to clear cart items", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("CartFragment", "Error fetching cart items to delete: ${e.message}", e)
                    Toast.makeText(requireContext(), "Failed to fetch cart items to delete", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
