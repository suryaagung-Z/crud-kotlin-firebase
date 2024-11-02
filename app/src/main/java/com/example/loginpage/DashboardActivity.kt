package com.example.loginpage

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.initialize
import android.content.Intent


class DashboardActivity : AppCompatActivity() {
    private lateinit var btnStore: Button
    private lateinit var insertName: EditText
    private lateinit var insertJurusan: EditText
    private lateinit var insertNIM: EditText
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var dbRef: DatabaseReference
    private lateinit var navView: NavigationView
    private lateinit var iconMenu: ImageView
    private lateinit var viewMahasiswa: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbRef = FirebaseDatabase.getInstance().getReference("Mahasiswa")

        enableEdgeToEdge()
        Firebase.initialize(this)
        setContentView(R.layout.activity_dashboard)

        insertName = findViewById(R.id.textInputNama1)
        insertJurusan = findViewById(R.id.textInputJurusan)
        insertNIM = findViewById(R.id.textInputNIM)
        btnStore = findViewById(R.id.btnSimpan)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        iconMenu = findViewById(R.id.iconMenu)
        viewMahasiswa = findViewById(R.id.btnViewdata)

        // Set click listener untuk iconMenu agar membuka drawer
        iconMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navView)) {
                drawerLayout.closeDrawer(navView)
            } else {
                drawerLayout.openDrawer(navView)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnStore.setOnClickListener {
            saveMahasiswaData()
//            sendData()
        }
        viewMahasiswa.setOnClickListener{
            val intent = Intent(this, ViewMahasiswa::class.java)
            startActivity(intent)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

//    private fun sendData() {
//        btnStore.isEnabled = false
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        myRef.setValue("Hello, World!")
//            .addOnSuccessListener { Log.d(TAG, "Data sent successfully") }
//            .addOnFailureListener { e -> Log.w(TAG, "Error sending data", e) }
//    }


    private fun saveMahasiswaData() {
        val name = insertName.text.toString()
        val jurusan = insertJurusan.text.toString()
        val nim = insertNIM.text.toString()
        val dbId = dbRef.push().key!!
        val mahasiswa = MahasiswaModel(dbId, name,nim,jurusan)

        if (name.isEmpty()) {
            insertName.error = "Please enter name"
            btnStore.isEnabled = true
            return
        }
        if (jurusan.isEmpty()) {
            insertJurusan.error = "Please enter jurusan"
            btnStore.isEnabled = true // Re-enable button
            return
        }
        if (nim.isEmpty()) {
            insertNIM.error = "Please enter NIM"
            btnStore.isEnabled = true // Re-enable button
            return
        }
        Toast.makeText(this, "Nama: $name, Jurusan: $jurusan", Toast.LENGTH_SHORT).show()

        dbRef.child(dbId).setValue(mahasiswa)
            .addOnCompleteListener {
                Log.d("DashboardActivity", "Success Saving Data" )
//                Toast.makeText(this, "Nama: $name, Jurusan: $jurusan", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
                val editTexts = listOf(insertName, insertJurusan, insertNIM)
                for (editText in editTexts) {
                    editText.text.clear()
                }
            }
            .addOnFailureListener { err ->
                Log.e("DashboardActivity", "Error saving data: ${err.message}", err)
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }.addOnCompleteListener {
                // Re-enable the button after the operation completes
                btnStore.isEnabled = true
            }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", false)
            putString("username", null)
            apply()
        }
        // Kembali ke halaman login
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}