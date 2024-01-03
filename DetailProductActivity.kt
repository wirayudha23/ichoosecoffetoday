package com.example.ichoosecoffetoday.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ichoosecoffetoday.databinding.ActivityDetailProductBinding

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productImage = intent.getStringExtra("PRODUCT_IMAGE")
        val jumlahProduct = intent.getStringExtra("JUMLAH_PRODUCT")
        val deskripsiProduct = intent.getStringExtra("DESKRIPSI_PRODUCT")

        // Tampilkan data pada UI
        binding.namaProductTxt.text = productName
        binding.hargaProductTxt.text = "Rp. $productPrice"
        binding.jumlahProductTxt.text = jumlahProduct
        binding.deskripsiProductTxt.text = deskripsiProduct
        Glide.with(this).load(productImage).into(binding.retrieveImage)
    }
}
