package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double = 4.5,
    val reviewCount: Int = 120,
    val isNew: Boolean = false,
    val discountPercent: Int = 0,
    val category: String = "Groceries"
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: String,
    val productName: String,
    val imageUrl: String,
    val price: Double,
    val itemsCount: Int,
    val status: String, // "ORDERED", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED"
    val expectedDelivery: String,
    val dateString: String,
    val isSellerOrder: Boolean = false
)
