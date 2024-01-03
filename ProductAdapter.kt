package com.example.ichoosecoffetoday.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ichoosecoffetoday.Activity.DetailProductActivity
import com.example.ichoosecoffetoday.Domain.Product
import com.example.ichoosecoffetoday.R

class ProductAdapter(
    private val context: Context,
    private var dataList: List<Product>, // Ganti dengan var agar bisa diubah
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.viewholder_pop_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].imageUrl).into(holder.tvImage)
        holder.tvNamaProduct.text = dataList[position].nama
        holder.tvHargaProduct.text = dataList[position].harga

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position)
        }
    }

    // Fungsi untuk memperbarui data pada adapter
    fun setData(newList: List<Product>) {
        dataList = newList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvImage: ImageView = itemView.findViewById(R.id.retrieveImage)
    var tvNamaProduct: TextView = itemView.findViewById(R.id.namaProductTxt)
    var tvHargaProduct: TextView = itemView.findViewById(R.id.hargaProductTxt)
}

