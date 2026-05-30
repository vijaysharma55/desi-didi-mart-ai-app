package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val db: AppDatabase) {
    val allProducts: Flow<List<Product>> = db.productDao().getAllProducts()
    val allOrders: Flow<List<Order>> = db.orderDao().getAllOrders()

    suspend fun insertProduct(product: Product) {
        db.productDao().insertProduct(product)
    }

    suspend fun insertOrder(order: Order) {
        db.orderDao().insertOrder(order)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        db.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: String) {
        db.orderDao().deleteOrder(orderId)
    }

    // Populate initial rich mock data if empty
    suspend fun populateInitialDataIfEmpty() {
        val productDao = db.productDao()
        val orderDao = db.orderDao()

        // Check if products list is empty
        val currentProducts = productDao.getAllProducts().first()
        val juteRug = Product(
            name = "Ghar Sansar Hand-woven Jute Rug",
            price = 2499.0,
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBeldb81Tt_5jV8YMUBMb7lNzIY5ibTWN5Day0fOMU-S93omkJY7KwYe0kYvlDAln7KljKHXW8HVXHW0yJJisE2wc81yln41eO7GfQnYyA-dl2u9XzK_JBIRtFLk3wygvdPN8Gf222TAMzmjUgleSTgmfk1Q-_5dm_MP0TzNVOlzbXFJvd_MR9oeFnWzE16lesJtJ3v4sA6ra8uL3I5ky9AGMVNh3lt0295gNVD68iMaKCYtPexCLSv1dslM2C-FKHzyvSf8umV94ow",
            rating = 4.5,
            reviewCount = 1240,
            isNew = false,
            discountPercent = 50,
            category = "Home Essentials"
        )
        if (currentProducts.isEmpty()) {
            val defaultProducts = listOf(
                juteRug,
                Product(
                    name = "Organic Toor Dal (Unpolished) - 1kg",
                    price = 185.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCXx1_XRC86XLn-c7D4GnbUDBy8ISTZ2Tgyrh3DAeYftCkLJTIQgcg8G7UvU7Z5JUNxHd4JX7LnPaok5Utrsu6UbX6OTjCkHRmWmSL3BcYWuQFpjlMwXKOE5H8Y7e4LEegurjHHe72vnDjKdqH3uPblNZPTqvL16_aH4W3c4pxMIVsyCIxNYGhikpMe6NiRkUy0ng72Nc8tRyn7a5XPy4Rdd7j67isaU56ZFWkg3g0yGZ8BtQ--vVoxZaUFp5uzwO3ZcRXedW7THu2J",
                    rating = 4.4,
                    reviewCount = 124,
                    isNew = false,
                    discountPercent = 15,
                    category = "Groceries"
                ),
                Product(
                    name = "Hand-Painted Terracotta Pot",
                    price = 450.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCXDKsLgWstbZLCqreGvDSZ-lqItuhI8mVJskSmIaHWXOfJziQa1ozH2Fyjr0sLrPwmQn2dna_Wzv4uNQP7uGmRsuWj4GDHzLDl1QxsnFrltnF43IzbFr8yXRYnbMyGbq9-tZu4wyWlkaAFRIuhJopZi7SyaIRIDNPowe0dSNuNCSBSE5F1BV5DklMextbztxyLgODTdhTirrS52XDuinkNi1z2cNaImFwmQEKAqFFSzSosREcTS8FovSDMH5dR1O0bSNg0EDPvVQkk",
                    rating = 5.0,
                    reviewCount = 48,
                    isNew = true,
                    discountPercent = 0,
                    category = "Home Essentials"
                ),
                Product(
                    name = "Pure Turmeric",
                    price = 120.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQoTrclN9kdF9HB5bz9KgYl9jMyfiaBApN6kzawhFDiAJTk0y8mUXlc8VXhBJAxEseeT8ZBy89NpPyCbrf32R7l-hyLTMebGk-HtEtNtF2EX0G6WpZQZZOb5Y24jn5iD_tZJnhafQtDQMUenK2iXr9mieK3LbBYew7hXs49Zdxsa9JF5aMYFh8Sr6ziy1rncNzv1WKT3BJT4F973V-MVvhkLV2QCf0orYoojnhOc_0SNZiyedLYPTqX4wVDc8WCfcvsGF7iHboxWaI",
                    rating = 4.8,
                    reviewCount = 95,
                    isNew = false,
                    discountPercent = 10,
                    category = "Groceries"
                ),
                Product(
                    name = "Spicy Mango Pickle",
                    price = 250.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBU1-B_tdtIde0GtHsvhN2u-GJ3dJvXj1dm9nPoxNIdgjm8iRXzn4LEUiLus93YsLsazhP-9dUWoue3uKNEHg8HdIOUpZ18_6cXHbpBoh0uYAXJnc1qJixs_HIx73dEQOU8pzRyxc4s5lDh9A4I_U7bWHL8UWAV9IbZMkXcuu5KihWAb9gIE-QnmB-3NX1syT9T1yWxvtiOWWFqyhU3jBgqVf5cAPuQYuGFduOWUBgqEUKE3Gs0YMqg9u1VUzjJNH7NRVhn1pKvizBt",
                    rating = 4.7,
                    reviewCount = 67,
                    isNew = false,
                    discountPercent = 5,
                    category = "Groceries"
                ),
                Product(
                    name = "Heavy Duty Stainless Steel Kadhai",
                    price = 1299.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBdptTwvBYaxQskP2dsfUBghuclRsDRvsa3okoBfflQ5tOfcTKL8r4jP5tpXHnDgJDa8ACWUy_0xJU2y2tyNZuuAl7ZGOQPvQzTsCveqEKdTYvMHhcCqVGc09ziog7bPydGIC9SiH-oxz1XErCy1QYh94dZ0yc9LvdryHUKHV6Owv7120xrHFoN0eh1qMVk55LSzAfyQU92yUQhhMNP01ZAtR1516tdfEjFb4Kcrl3Y0Y7fm4ePvZbP4mCqz8xh_CamuYW53QvWL-NN",
                    rating = 4.0,
                    reviewCount = 215,
                    isNew = false,
                    discountPercent = 28,
                    category = "Home Essentials"
                ),
                Product(
                    name = "Pure Cotton Block Print Fabric",
                    price = 320.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC_QdqPA49SoSUlbgZ8W4G3Ha6ff0KPseIYBOgW_qqG7j31hw6ab3TH57x24UMFX9eommQKlCr9rP7qnHs2xEnU8PPhRT5bVwaEinunHFO_jhVVqG8tVgFCj2Mgf8siMUyD46ldTtHuo7NPpSTWcCG8gjrFEo7OzciBfECfHQyWnejV4aSgp6_49Gp4oWF_tunK_fj6EYtyhfNIu5RtDXeNWq-7xIViSIMXhMskSZdYjjZh_PhBy_c6RvLmrXcMIthFeweo219lh3Um",
                    rating = 4.5,
                    reviewCount = 89,
                    isNew = false,
                    discountPercent = 20,
                    category = "Clothing"
                )
            )
            productDao.insertAllProducts(defaultProducts)
        } else {
            val hasRug = currentProducts.any { it.name.contains("Ghar Sansar") }
            if (!hasRug) {
                productDao.insertProduct(juteRug)
            }
            val hasKadhai = currentProducts.any { it.name.contains("Heavy Duty Stainless Steel Kadhai") }
            if (!hasKadhai) {
                productDao.insertProduct(Product(
                    name = "Heavy Duty Stainless Steel Kadhai",
                    price = 1299.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBdptTwvBYaxQskP2dsfUBghuclRsDRvsa3okoBfflQ5tOfcTKL8r4jP5tpXHnDgJDa8ACWUy_0xJU2y2tyNZuuAl7ZGOQPvQzTsCveqEKdTYvMHhcCqVGc09ziog7bPydGIC9SiH-oxz1XErCy1QYh94dZ0yc9LvdryHUKHV6Owv7120xrHFoN0eh1qMVk55LSzAfyQU92yUQhhMNP01ZAtR1516tdfEjFb4Kcrl3Y0Y7fm4ePvZbP4mCqz8xh_CamuYW53QvWL-NN",
                    rating = 4.0,
                    reviewCount = 215,
                    isNew = false,
                    discountPercent = 28,
                    category = "Home Essentials"
                ))
            }
            val hasFabric = currentProducts.any { it.name.contains("Pure Cotton Block Print Fabric") }
            if (!hasFabric) {
                productDao.insertProduct(Product(
                    name = "Pure Cotton Block Print Fabric",
                    price = 320.0,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC_QdqPA49SoSUlbgZ8W4G3Ha6ff0KPseIYBOgW_qqG7j31hw6ab3TH57x24UMFX9eommQKlCr9rP7qnHs2xEnU8PPhRT5bVwaEinunHFO_jhVVqG8tVgFCj2Mgf8siMUyD46ldTtHuo7NPpSTWcCG8gjrFEo7OzciBfECfHQyWnejV4aSgp6_49Gp4oWF_tunK_fj6EYtyhfNIu5RtDXeNWq-7xIViSIMXhMskSZdYjjZh_PhBy_c6RvLmrXcMIthFeweo219lh3Um",
                    rating = 4.5,
                    reviewCount = 89,
                    isNew = false,
                    discountPercent = 20,
                    category = "Clothing"
                ))
            }
        }

        // Check if orders list is empty
        val currentOrders = orderDao.getAllOrders().first()
        if (currentOrders.isEmpty()) {
            val defaultOrders = listOf(
                Order(
                    orderId = "#ORD-7829",
                    productName = "Pure Turmeric",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQoTrclN9kdF9HB5bz9KgYl9jMyfiaBApN6kzawhFDiAJTk0y8mUXlc8VXhBJAxEseeT8ZBy89NpPyCbrf32R7l-hyLTMebGk-HtEtNtF2EX0G6WpZQZZOb5Y24jn5iD_tZJnhafQtDQMUenK2iXr9mieK3LbBYew7hXs49Zdxsa9JF5aMYFh8Sr6ziy1rncNzv1WKT3BJT4F973V-MVvhkLV2QCf0orYoojnhOc_0SNZiyedLYPTqX4wVDc8WCfcvsGF7iHboxWaI",
                    price = 850.0,
                    itemsCount = 3,
                    status = "ORDERED",
                    expectedDelivery = "Expected by Tomorrow",
                    dateString = "2026-05-30",
                    isSellerOrder = true
                ),
                Order(
                    orderId = "#ORD-7821",
                    productName = "Spicy Mango Pickle",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBU1-B_tdtIde0GtHsvhN2u-GJ3dJvXj1dm9nPoxNIdgjm8iRXzn4LEUiLus93YsLsazhP-9dUWoue3uKNEHg8HdIOUpZ18_6cXHbpBoh0uYAXJnc1qJixs_HIx73dEQOU8pzRyxc4s5lDh9A4I_U7bWHL8UWAV9IbZMkXcuu5KihWAb9gIE-QnmB-3NX1syT9T1yWxvtiOWWFqyhU3jBgqVf5cAPuQYuGFduOWUBgqEUKE3Gs0YMqg9u1VUzjJNH7NRVhn1pKvizBt",
                    price = 250.0,
                    itemsCount = 1,
                    status = "SHIPPED",
                    expectedDelivery = "Picked up today • In Transit",
                    dateString = "2026-05-29",
                    isSellerOrder = true
                ),
                Order(
                    orderId = "#DDM-98765",
                    productName = "Hand-Painted Terracotta Pot",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCXDKsLgWstbZLCqreGvDSZ-lqItuhI8mVJskSmIaHWXOfJziQa1ozH2Fyjr0sLrPwmQn2dna_Wzv4uNQP7uGmRsuWj4GDHzLDl1QxsnFrltnF43IzbFr8yXRYnbMyGbq9-tZu4wyWlkaAFRIuhJopZi7SyaIRIDNPowe0dSNuNCSBSE5F1BV5DklMextbztxyLgODTdhTirrS52XDuinkNi1z2cNaImFwmQEKAqFFSzSosREcTS8FovSDMH5dR1O0bSNg0EDPvVQkk",
                    price = 450.0,
                    itemsCount = 1,
                    status = "OUT_FOR_DELIVERY",
                    expectedDelivery = "Expected by Today",
                    dateString = "2026-05-30",
                    isSellerOrder = false
                ),
                Order(
                    orderId = "#DDM-10493",
                    productName = "Organic Toor Dal (Unpolished) - 1kg",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCXx1_XRC86XLn-c7D4GnbUDBy8ISTZ2Tgyrh3DAeYftCkLJTIQgcg8G7UvU7Z5JUNxHd4JX7LnPaok5Utrsu6UbX6OTjCkHRmWmSL3BcYWuQFpjlMwXKOE5H8Y7e4LEegurjHHe72vnDjKdqH3uPblNZPTqvL16_aH4W3c4pxMIVsyCIxNYGhikpMe6NiRkUy0ng72Nc8tRyn7a5XPy4Rdd7j67isaU56ZFWkg3g0yGZ8BtQ--vVoxZaUFp5uzwO3ZcRXedW7THu2J",
                    price = 185.0,
                    itemsCount = 1,
                    status = "DELIVERED",
                    expectedDelivery = "Delivered on Oct 15",
                    dateString = "2025-10-15",
                    isSellerOrder = false
                ),
                Order(
                    orderId = "#DDM-01493",
                    productName = "Premium Basmati Rice (5kg)",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBu56NA7z0wMJVSgHjKxNUnzcLpvTneqviCh76giF6tpZZZ0DW4MD6G5KXI0TTAyFrj6pDoEzt3PvRMyGxkJSKTQovQWfqBaeyuOz5-nxdLfjRrN8KeVybZhPtSFg1JTFY9M6TgxBrLek0HvN0FyKjC3-bIMBoRO0MyZYXsvOpgYuDfxNzXGeiVWmrLf_RMTGfh3KO6D1Hl0OfqSHUuy72oNDNxddYEsEOCP_0BPw5ENO_r3a6R4Vf2aF4xoxcUSGBNq5RdXq4C8aUn",
                    price = 599.0,
                    itemsCount = 1,
                    status = "DELIVERED",
                    expectedDelivery = "Delivered on Oct 10",
                    dateString = "2025-10-10",
                    isSellerOrder = false
                )
            )
            orderDao.insertAllOrders(defaultOrders)
        }
    }
}
