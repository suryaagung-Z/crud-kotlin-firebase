package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditMahasiswaActivity : AppCompatActivity() {

    private lateinit var editNamaMahasiswa: EditText
    private lateinit var editJurusan: EditText
    private lateinit var editNIM: EditText
    private lateinit var btnUpdate: Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var mahasiswa: MahasiswaModel
    private lateinit var btnBackToViewMahasiswa: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mahasiswa)

        editNamaMahasiswa = findViewById(R.id.editNamaMahasiswa)
        editJurusan = findViewById(R.id.editJurusan)
        editNIM = findViewById(R.id.editNIM)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnBackToViewMahasiswa = findViewById(R.id.btnBackToViewMahasiswa)

        // Retrieve the passed MahasiswaModel
        mahasiswa = intent.getParcelableExtra("mahasiswa_data")!!

        // Populate the fields with current data
        editNamaMahasiswa.setText(mahasiswa.name)
        editJurusan.setText(mahasiswa.jurusan)
        editNIM.setText(mahasiswa.nim)

        dbRef = FirebaseDatabase.getInstance().getReference("Mahasiswa").child(mahasiswa.dbId!!)

        btnUpdate.setOnClickListener {
            updateMahasiswa()
            btnUpdate.isEnabled = false
        }
        btnBackToViewMahasiswa.setOnClickListener {
            btnUpdate.isEnabled = false
            val intent = Intent(this, ViewMahasiswa::class.java)
            startActivity(intent)
        }
    }

    private fun updateMahasiswa() {
        val name = editNamaMahasiswa.text.toString()
        val jurusan = editJurusan.text.toString()
        val nim = editNIM.text.toString()

        if (name.isEmpty()) {
            editNamaMahasiswa.error = "Please enter name"
            btnUpdate.isEnabled = true
            return
        }
        if (jurusan.isEmpty()) {
            editJurusan.error = "Please enter jurusan"
            btnUpdate.isEnabled = true
            return
        }
        if (nim.isEmpty()) {
            editNIM.error = "Please enter NIM"
            btnUpdate.isEnabled = true
            return
        }

        val updatedMahasiswa = MahasiswaModel(mahasiswa.dbId, name, nim, jurusan)
        dbRef.setValue(updatedMahasiswa).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous activity
            } else {
                Toast.makeText(this, "Failed to update data.", Toast.LENGTH_SHORT).show()
            }
        }.addOnCompleteListener {
            // Re-enable the button after the operation completes
            btnUpdate.isEnabled = true
        }.addOnFailureListener { err ->
            Log.e("EditMahasiswaActivity", "Error saving data: ${err.message}", err)
            Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
        }
    }

}
