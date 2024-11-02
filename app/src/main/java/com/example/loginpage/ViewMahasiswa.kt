package com.example.loginpage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ViewMahasiswa : AppCompatActivity() {

    private lateinit var mahasiswaRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var btnBackToDashboard: Button
    private lateinit var mahasiswaList: ArrayList<MahasiswaModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_mahasiswa)

        mahasiswaRecyclerView = findViewById(R.id.recyclerViewMahasiswa)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        mahasiswaRecyclerView.layoutManager = LinearLayoutManager(this)
        mahasiswaRecyclerView.setHasFixedSize(true)

        mahasiswaList = arrayListOf()
        dbRef = FirebaseDatabase.getInstance().getReference("Mahasiswa")

        // Fetch data from Firebase
        getMahasiswaData()

        btnBackToDashboard.setOnClickListener {
            val dashboardIntent = Intent(this, DashboardActivity::class.java)
            startActivity(dashboardIntent)
        }
    }

    private fun getMahasiswaData() {
        tvLoadingData.text = "Loading data..."

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mahasiswaList.clear()
                if (snapshot.exists()) {
                    for (mahasiswaSnap in snapshot.children) {
                        val mahasiswa = mahasiswaSnap.getValue(MahasiswaModel::class.java)
                        if (mahasiswa != null) {
                            mahasiswaList.add(mahasiswa)
                            Log.d("ViewMahasiswa", "Data mahasiswa: ${mahasiswaList.size}")
                        }
                    }
                    mahasiswaRecyclerView.adapter = MahasiswaAdapter(mahasiswaList, this@ViewMahasiswa)
                    tvLoadingData.text = ""
                } else {
                    tvLoadingData.text = "No data found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewMahasiswa", "Error fetching data", error.toException())
                tvLoadingData.text = "Failed to load data"
            }
        })
    }
}
