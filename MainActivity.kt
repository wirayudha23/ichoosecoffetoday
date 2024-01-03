package com.example.ichoosecoffetoday.Activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ichoosecoffetoday.Adapter.ProductAdapter
import com.example.ichoosecoffetoday.Domain.Product
import com.example.ichoosecoffetoday.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataList: ArrayList<Product>
    private lateinit var adapter: ProductAdapter
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@MainActivity, 2)
        binding.view1.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
        adapter = ProductAdapter(this@MainActivity, dataList) { position ->
            showProductDetail(position)
        }

        binding.view1.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("product")
        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val product = itemSnapshot.getValue(Product::class.java)
                    product?.let { dataList.add(it) }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

        binding.categorie1.setOnClickListener {
            filterByCategory("Buah")
        }

        binding.categorie2.setOnClickListener {
            filterByCategory("Sayur")
        }
    }

    private fun showProductDetail(position: Int) {
        val selectedProduct = dataList[position]
        val intent = Intent(this@MainActivity, DetailProductActivity::class.java).apply {
            putExtra("PRODUCT_NAME", selectedProduct.nama)
            putExtra("PRODUCT_PRICE", selectedProduct.harga)
            putExtra("PRODUCT_IMAGE", selectedProduct.imageUrl)
            putExtra("JUMLAH_PRODUCT", selectedProduct.jumlah?.toString() ?: "0")
            putExtra("DESKRIPSI_PRODUCT", selectedProduct.deskripsi.orEmpty())
        }
        startActivity(intent)
    }

    private fun filterByCategory(category: String) {
        val filteredList = dataList.filter { it.category == category } as ArrayList<Product>
        adapter.setData(filteredList)
    }
}
