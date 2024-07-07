package com.awesome.shopnowuser.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awesome.shopnowuser.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val registerButton: Button = findViewById(R.id.registerButton)
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkEmailExists(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val tvRedirectLogin: TextView = findViewById(R.id.tvRedirectLogin)
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkEmailExists(email: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.isEmpty) {
                        // Email does not exist, proceed with registration
                        createUser(email, password)
                    } else {
                        // Email already exists
                        Toast.makeText(this, "Email already in use. Please use a different email.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle error
                    Log.d("Firestore", "Error checking email: ", task.exception)
                }
            }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    val userRole = hashMapOf(
                        "email" to email,
                        "role" to "user" // Replace "user" with "IoT" or any other role as needed
                    )

                    if (userId != null) {
                        // Store user role in Firestore
                        firestore.collection("users").document(userId).set(userRole)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show()



                                // Redirect to UserProductListActivity or any other activity
                                val intent = Intent(this, UserProductListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Example method to add an item to cart after registration

    }

