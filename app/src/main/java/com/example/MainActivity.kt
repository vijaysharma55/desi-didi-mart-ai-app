package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppViewModel
import com.example.ui.HomeScreen
import com.example.ui.LocaleStrings
import com.example.ui.OrdersScreen
import com.example.ui.ProfileScreen
import com.example.ui.SellerScreen
import com.example.ui.ProductDetailsScreen
import com.example.ui.CheckoutScreen
import com.example.ui.SupportScreen
import com.example.ui.PartnerLoginScreen
import com.example.ui.PartnerDashboardScreen
import com.example.ui.ConfirmCashCollectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Saffron
import com.example.ui.theme.SaffronDark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AppViewModel = viewModel()
                
                val products by viewModel.productsFlow.collectAsState()
                val orders by viewModel.ordersFlow.collectAsState()
                val lang = viewModel.currentLanguage

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("app_bottom_nav"),
                            tonalElevation = 8.dp
                        ) {
                            val homeLabel = LocaleStrings.get("home_tab", lang)
                            val ordersLabel = LocaleStrings.get("orders_tab", lang)
                            val accountLabel = LocaleStrings.get("account_tab", lang)
                            val sellerLabel = LocaleStrings.get("seller_tab", lang)

                            NavigationBarItem(
                                selected = viewModel.selectedTab == "home",
                                onClick = { viewModel.selectedTab = "home" },
                                icon = {
                                    Icon(
                                        imageVector = if (viewModel.selectedTab == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = homeLabel
                                    )
                                },
                                label = { Text(text = homeLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = SaffronDark,
                                    indicatorColor = Saffron,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_home_tab")
                            )

                            NavigationBarItem(
                                selected = viewModel.selectedTab == "orders",
                                onClick = { viewModel.selectedTab = "orders" },
                                icon = {
                                    Icon(
                                        imageVector = if (viewModel.selectedTab == "orders") Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                        contentDescription = ordersLabel
                                    )
                                },
                                label = { Text(text = ordersLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = SaffronDark,
                                    indicatorColor = Saffron,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_orders_tab")
                            )

                            NavigationBarItem(
                                selected = viewModel.selectedTab == "profile",
                                onClick = { viewModel.selectedTab = "profile" },
                                icon = {
                                    Icon(
                                        imageVector = if (viewModel.selectedTab == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = accountLabel
                                    )
                                },
                                label = { Text(text = accountLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = SaffronDark,
                                    indicatorColor = Saffron,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_profile_tab")
                            )

                            NavigationBarItem(
                                selected = viewModel.selectedTab == "seller",
                                onClick = { viewModel.selectedTab = "seller" },
                                icon = {
                                    Icon(
                                        imageVector = if (viewModel.selectedTab == "seller") Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                        contentDescription = sellerLabel
                                    )
                                },
                                label = { Text(text = sellerLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = SaffronDark,
                                    indicatorColor = Saffron,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_seller_tab")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val detailsProduct = viewModel.selectedProductForDetails
                        val checkoutProduct = viewModel.checkoutProduct
                        
                        if (checkoutProduct != null) {
                            CheckoutScreen(
                                product = checkoutProduct,
                                viewModel = viewModel,
                                onBack = { viewModel.checkoutProduct = null }
                            )
                        } else if (detailsProduct != null) {
                            ProductDetailsScreen(
                                product = detailsProduct,
                                viewModel = viewModel,
                                onBack = { viewModel.selectedProductForDetails = null }
                            )
                        } else if (viewModel.showSupportScreen) {
                            SupportScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.showSupportScreen = false }
                            )
                        } else if (viewModel.showPartnerLoginScreen) {
                            PartnerLoginScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.showPartnerLoginScreen = false }
                            )
                        } else if (viewModel.showPartnerDashboard) {
                            val activeId = viewModel.activeCashCollectionOrderId
                            if (activeId != null) {
                                ConfirmCashCollectionScreen(
                                    viewModel = viewModel,
                                    orderId = activeId,
                                    amountToCollect = if (activeId == "#DD-8821") 850.0 else 450.0,
                                    onBack = { viewModel.activeCashCollectionOrderId = null }
                                )
                            } else {
                                PartnerDashboardScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.showPartnerDashboard = false }
                                )
                            }
                        } else {
                            when (viewModel.selectedTab) {
                                "home" -> HomeScreen(products = products, viewModel = viewModel)
                                "orders" -> OrdersScreen(orders = orders, viewModel = viewModel)
                                "profile" -> ProfileScreen(viewModel = viewModel)
                                "seller" -> SellerScreen(products = products, orders = orders, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
