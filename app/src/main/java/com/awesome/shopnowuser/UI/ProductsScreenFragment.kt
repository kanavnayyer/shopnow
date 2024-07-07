package com.awesome.shopnowuser.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.awesome.shopnowuser.Model.Product
import com.awesome.shopnowuser.Adapters.ProductAdapter
import com.awesome.shopnowuser.R

class ProductsScreenFragment : Fragment(), FilterBottomScreenFragmentFragment.FilterListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var productList: MutableList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var filteredList: MutableList<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products_screen, container, false)

        firestore = FirebaseFirestore.getInstance()
        productList = mutableListOf()
        filteredList = mutableListOf()
        productAdapter = ProductAdapter(filteredList,this@ProductsScreenFragment)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = productAdapter

        val filterButton: Button = view.findViewById(R.id.filterButton)
        filterButton.setOnClickListener {
            val filterBottomSheet = FilterBottomScreenFragmentFragment()
            filterBottomSheet.setTargetFragment(this, 0)
            filterBottomSheet.show(parentFragmentManager, filterBottomSheet.tag)
        }

        loadProducts()

        return view

    }
    fun onItemClick(product: Product) {
        // Handle the click event to open the new fragment
        val fragment = ProductDetailFragment.newInstance(product)
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun loadProducts() {
        firestore.collection("products").get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                applyFilter()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun applyFilter(priceFilter: String = "", categoryFilter: String = "") {
        Log.d("ProductListFragment", "Applying filter: Price = $priceFilter, Category = $categoryFilter")

        val filtered = productList.filter { product ->
            (categoryFilter.isEmpty() || categoryFilter == "All" || product.category == categoryFilter)
        }.toMutableList()

        when (priceFilter) {
            "Low to High" -> filtered.sortBy { it.price }
            "High to Low" -> filtered.sortByDescending { it.price }
        }

        Log.d("ProductListFragment", "Filtered list size: ${filtered.size}")
        productAdapter.updateData(filtered)
    }

    override fun onFilterApplied(priceFilter: String, categoryFilter: String) {
        Log.d("ProductListFragment", "Filter applied: Price = $priceFilter, Category = $categoryFilter")
        applyFilter(priceFilter, categoryFilter)
    }
}
