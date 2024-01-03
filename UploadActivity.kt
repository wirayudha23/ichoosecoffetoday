package com.example.ichoosecoffetoday.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ichoosecoffetoday.Domain.Product
import com.example.ichoosecoffetoday.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.example.ichoosecoffetoday.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var edtNamaProduct: EditText
    private lateinit var edtHargaProduct: EditText
    private lateinit var edtJumlahProduct: EditText
    private lateinit var edtDeskripsiProduct: EditText
    private lateinit var buttonUpload: Button
    private lateinit var buttonChooseImage: Button
    private lateinit var imageView: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var selectedCategory: String

    private lateinit var binding: ActivityUploadBinding
    private lateinit var ref: DatabaseReference
    private lateinit var storageRef: StorageReference

    private var filePath: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val STORAGE_PERMISSION_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("product")
        storageRef = FirebaseStorage.getInstance().reference

        binding.buttonUpload.setOnClickListener(this)
        binding.buttonChooseImage.setOnClickListener(this)
        edtNamaProduct = findViewById(R.id.uploadNamaProduct)
        edtHargaProduct = findViewById(R.id.uploadHargaProduct)
        edtJumlahProduct = findViewById(R.id.uploadJumlahProduct)
        edtDeskripsiProduct = findViewById(R.id.uploadDescripsiProduct)
        imageView = findViewById(R.id.uploadImage)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Inisialisasi Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
        spinnerCategory.onItemSelectedListener = this

        // Memeriksa dan meminta izin saat aplikasi dijalankan
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Meminta izin penyimpanan secara dinamis jika belum diberikan
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lanjutkan dengan operasi yang memerlukan izin
                pilihGambar()
            } else {
                Toast.makeText(
                    this,
                    "Izin penyimpanan dibutuhkan untuk memilih gambar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle jika tidak ada yang dipilih
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonUpload -> simpanData()
            R.id.buttonChooseImage -> pilihGambar()
        }
    }

    private fun pilihGambar() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Pilih Gambar"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun simpanData() {
        val namaProduct = edtNamaProduct.text.toString().trim()
        val hargaProduct = edtHargaProduct.text.toString()
        val jumlahProduct = edtJumlahProduct.text.toString()
        val deskripsiProduct = edtDeskripsiProduct.text.toString().trim()

        if (namaProduct.isEmpty() or hargaProduct.isEmpty() or jumlahProduct.isEmpty() or deskripsiProduct.isEmpty()) {
            Toast.makeText(this, "Isi data secara lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengonversi jumlahProduct menjadi Int
        val jumlahProductInt = jumlahProduct.toInt()

        if (filePath != null) {
            // Jika ada gambar yang dipilih, unggah gambar ke Firebase Storage
            val storageReference = storageRef.child("images/${System.currentTimeMillis()}_$namaProduct")
            storageReference.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Gambar berhasil diunggah, dapatkan URL gambar
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        // URL gambar berhasil didapatkan, simpan data produk bersama dengan URL gambar ke Firebase Database
                        val productId = ref.push().key
                        val product = Product(productId!!, namaProduct, hargaProduct, jumlahProductInt, deskripsiProduct, uri.toString(), selectedCategory)

                        ref.child(productId).setValue(product).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                                Log.e("Firebase", "Gagal menambahkan data", task.exception)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Gagal mengunggah gambar", e)
                }
        } else {
            // Jika tidak ada gambar yang dipilih, simpan data produk tanpa URL gambar
            val productId = ref.push().key
            val product = Product(productId!!, namaProduct, hargaProduct, jumlahProductInt, deskripsiProduct, "", selectedCategory)

            ref.child(productId).setValue(product).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Gagal menambahkan data", task.exception)
                }
            }
        }
    }
}
