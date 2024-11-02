package com.example.loginpage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MahasiswaAdapter(
    private val mahasiswaList: ArrayList<MahasiswaModel>,
    private val context: Context
) : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Mahasiswa")

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaMahasiswa: TextView = view.findViewById(R.id.tvNamaMahasiswa)
        val tvJurusan: TextView = view.findViewById(R.id.tvJurusan)
        val tvNIM: TextView = view.findViewById(R.id.tvNIM)
        val btnEdit: ImageView = view.findViewById(R.id.iconEdit)
        val btnDelete: ImageView = view.findViewById(R.id.iconDelete)

        fun bind(mahasiswa: MahasiswaModel) {
            tvNamaMahasiswa.text = mahasiswa.name
            tvJurusan.text = mahasiswa.jurusan
            tvNIM.text = mahasiswa.nim

            btnEdit.setOnClickListener {
                val intent = Intent(context, EditMahasiswaActivity::class.java)
                intent.putExtra("mahasiswa_data", mahasiswa)
                context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                // Show confirmation dialog
                AlertDialog.Builder(context)
                    .setTitle("Delete Data")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes") { _, _ ->
                        mahasiswa.dbId?.let { id ->
                            dbRef.child(id).removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Remove the item from the list and notify adapter
                                    mahasiswaList.removeAt(adapterPosition)
                                    notifyItemRemoved(adapterPosition)
                                    notifyItemRangeChanged(adapterPosition, mahasiswaList.size)
                                    Toast.makeText(context,
                                        "Data deleted successfully: ${mahasiswa.name}, Jurusan: ${mahasiswa.jurusan}, NIM: ${mahasiswa.nim}",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    // Show error dialog with detailed error message
                                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                                    AlertDialog.Builder(context)
                                        .setTitle("Error Deleting Data")
                                        .setMessage("Failed to delete data: $errorMessage")
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_mahasiswa_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.bind(mahasiswa)
    }

    override fun getItemCount(): Int = mahasiswaList.size
}
