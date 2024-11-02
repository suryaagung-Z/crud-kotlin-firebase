package com.example.loginpage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InsertActivity : AppCompatActivity() {

    private lateinit var insertName : EditText
    private lateinit var insertJurusan : EditText
    private lateinit var btnStore : Button
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insertName = findViewById(R.id.textInputNama1)
            insertJurusan = findViewById(R.id.textInputJurusan)

            dbRef = FirebaseDatabase.getInstance().getReference("Mahasiswa")
            btnStore = findViewById(R.id.btnSimpan)
            btnStore.setOnClickListener {
                saveMahasiswaData()
            }
            insets
        }
    }

    private fun saveMahasiswaData() {
        val name = insertName.text.toString()
        val jurusan = insertJurusan.text.toString()
        val dbId = dbRef.push().key!!
        val mahasiswa = MahasiswaModel(dbId, name, jurusan)

        if (name.isEmpty()) {
            insertName.error = "Please enter name"
        }
        if (jurusan.isEmpty()) {
            insertJurusan.error = "Please enter jurusan"
        }

        dbRef.child(dbId).setValue(mahasiswa).addOnCompleteListener {
            Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
            insertName.text.clear()
            insertJurusan.text.clear()
        }.addOnFailureListener { err ->
            Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
        }
    }
}