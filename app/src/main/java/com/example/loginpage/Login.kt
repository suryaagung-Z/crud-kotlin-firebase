package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class Login : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        // Cek status login
        checkLoginStatus()

        database = FirebaseDatabase.getInstance().getReference("Users")

        btnRegister.setOnClickListener {
            val registerIntent = Intent(this, Register::class.java)
            startActivity(registerIntent)
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
            } else {
                // Mencari user berdasarkan username
                database.orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userSnapshot in snapshot.children) {
                                    val savedPassword = userSnapshot.child("password").getValue(String::class.java)
                                    if (savedPassword == password) {
                                        // Login berhasil, simpan status login
                                        saveLoginStatus(username)
                                        Toast.makeText(this@Login, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                        val dashboardIntent = Intent(this@Login, DashboardActivity::class.java)
                                        startActivity(dashboardIntent)
                                        finish()
                                        return
                                    } else {
                                        Toast.makeText(this@Login, "Password salah", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@Login, "Data Belum Terdaftar", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@Login, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Fungsi untuk menyimpan status login
    private fun saveLoginStatus(username: String) {
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("username", username)
            apply()
        }
    }

    // Fungsi untuk mengecek status login
    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // Jika sudah login, langsung pindah ke Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
