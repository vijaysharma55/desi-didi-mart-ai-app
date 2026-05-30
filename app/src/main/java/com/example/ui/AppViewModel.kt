package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Order
import com.example.data.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)
        
        // Populate database with rich assets on initial launch in background
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    // Exposed lists backed by Room database flows
    val productsFlow: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val ordersFlow: StateFlow<List<Order>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Vernacular Language State (EN default, toggles to HI)
    var currentLanguage by mutableStateOf(LocaleStrings.Lang.EN)
        private set

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == LocaleStrings.Lang.EN) {
            LocaleStrings.Lang.HI
        } else {
            LocaleStrings.Lang.EN
        }
    }

    fun setLanguage(lang: LocaleStrings.Lang) {
        currentLanguage = lang
    }

    // Navigation and tab state
    var selectedTab by mutableStateOf("home")
    var selectedProductForDetails by mutableStateOf<Product?>(null)
    var checkoutProduct by mutableStateOf<Product?>(null)
    var showSupportScreen by mutableStateOf(false)
    var showPartnerLoginScreen by mutableStateOf(false)
    var showPartnerDashboard by mutableStateOf(false)

    // Partner Delivery Portal variables
    var partnerDeliveriesCount by mutableStateOf(8)
    var partnerDistanceKms by mutableStateOf(42.0)
    var partnerMilestoneStep by mutableStateOf(1) // 0: Picked up, 1: Arriving, 2: Arrived (near location), 3: Delivered/Completed
    var isPartnerOrderDelivered by mutableStateOf(false)
    var activeCashCollectionOrderId by mutableStateOf<String?>(null)

    // Search and Voice Search Simulation States
    var searchQuery by mutableStateOf("")
    var isListeningByVoice by mutableStateOf(false)
    var voiceStatusText by mutableStateOf("")

    fun triggerVoiceSearch() {
        isListeningByVoice = true
        voiceStatusText = if (currentLanguage == LocaleStrings.Lang.EN) "Listening..." else "आवाज सुन रहे हैं..."
        
        viewModelScope.launch {
            delay(1500)
            voiceStatusText = if (currentLanguage == LocaleStrings.Lang.EN) {
                "Heard: 'Pure Turmeric'"
            } else {
                "सुना: 'शुद्ध हल्दी'"
            }
            delay(1000)
            searchQuery = if (currentLanguage == LocaleStrings.Lang.EN) "Turmeric" else "हल्दी"
            isListeningByVoice = false
        }
    }

    // Add Product Modal Form States
    var showAddProductDialog by mutableStateOf(false)
    var newProductName by mutableStateOf("")
    var newProductCategory by mutableStateOf("Groceries")
    var newProductPrice by mutableStateOf("")

    fun onAddProductSubmit() {
        val parsedPrice = newProductPrice.toDoubleOrNull() ?: 100.0
        val tempImageUrl = if (newProductName.contains("Turmeric", ignoreCase = true) || newProductName.contains("हल्दी", ignoreCase = true)) {
            "https://lh3.googleusercontent.com/aida-public/AB6AXuAQoTrclN9kdF9HB5bz9KgYl9jMyfiaBApN6kzawhFDiAJTk0y8mUXlc8VXhBJAxEseeT8ZBy89NpPyCbrf32R7l-hyLTMebGk-HtEtNtF2EX0G6WpZQZZOb5Y24jn5iD_tZJnhafQtDQMUenK2iXr9mieK3LbBYew7hXs49Zdxsa9JF5aMYFh8Sr6ziy1rncNzv1WKT3BJT4F973V-MVvhkLV2QCf0orYoojnhOc_0SNZiyedLYPTqX4wVDc8WCfcvsGF7iHboxWaI"
        } else {
            "https://lh3.googleusercontent.com/aida-public/AB6AXuCXDKsLgWstbZLCqreGvDSZ-lqItuhI8mVJskSmIaHWXOfJziQa1ozH2Fyjr0sLrPwmQn2dna_Wzv4uNQP7uGmRsuWj4GDHzLDl1QxsnFrltnF43IzbFr8yXRYnbMyGbq9-tZu4wyWlkaAFRIuhJopZi7SyaIRIDNPowe0dSNuNCSBSE5F1BV5DklMextbztxyLgODTdhTirrS52XDuinkNi1z2cNaImFwmQEKAqFFSzSosREcTS8FovSDMH5dR1O0bSNg0EDPvVQkk"
        }

        val newProd = Product(
            name = newProductName,
            price = parsedPrice,
            imageUrl = tempImageUrl,
            category = newProductCategory,
            rating = 4.5,
            reviewCount = 1,
            isNew = true
        )

        viewModelScope.launch {
            repository.insertProduct(newProd)
            showAddProductDialog = false
            // Reset form
            newProductName = ""
            newProductPrice = ""
        }
    }

    // Coin Transaction State
    var didiCoinsBalance by mutableStateOf(150)

    fun rewardDidiCoins(amount: Int) {
        didiCoinsBalance += amount
    }

    // Seller fulfillment logic - updating order status live
    fun readyToShipOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "SHIPPED")
        }
    }

    // Simulate buying an item from the home screen
    fun purchaseProductDirectly(product: Product) {
        viewModelScope.launch {
            val randomNum = (10000..99999).random()
            val newOrder = Order(
                orderId = "#DDM-$randomNum",
                productName = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
                itemsCount = 1,
                status = "ORDERED",
                expectedDelivery = "Expected by Today",
                dateString = "2026-05-30",
                isSellerOrder = false
            )
            repository.insertOrder(newOrder)
            // Deduct some coins or give loyalty reward
            rewardDidiCoins(25)
            // Push notification or trigger navigation to tracking tab
            selectedTab = "orders"
        }
    }

    // Simulate shipping item back to ordered for demo loop
    fun resetOrderFulfillmentDemo() {
        viewModelScope.launch {
            repository.updateOrderStatus("#ORD-7829", "ORDERED")
        }
    }
}
