package com.example.ichoosecoffetoday.Domain

data class Product(
    val id: String = "",
    val nama: String? = null,
    val harga: String? = null,
    val jumlah: Int? = null,
    val deskripsi: String? = null,
    val imageUrl: String? = null,
    val category: String = ""
) {
    constructor() : this("", "", "", 0, "", "", "")
}
