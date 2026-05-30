package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Order
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    orders: List<Order>,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang = viewModel.currentLanguage
    var currentSubTab by remember { mutableStateOf("active") } // "active" or "past"
    var trackingOrder by remember { mutableStateOf<Order?>(null) }
    
    val activeOrders = orders.filter { it.status != "DELIVERED" }
    val pastOrders = orders.filter { it.status == "DELIVERED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 1. App Bar
        item {
            TopAppBar(
                title = {
                    Text(
                        text = LocaleStrings.get("my_orders", lang),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                    }
                }
            )
        }

        // 2. Tabs: Active and Past Orders
        item {
            Surface(
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TabButton(
                        text = LocaleStrings.get("active_orders", lang).format(activeOrders.size),
                        isActive = currentSubTab == "active",
                        onClick = { currentSubTab = "active" },
                        modifier = Modifier.weight(1f).testTag("active_orders_tab")
                    )
                    TabButton(
                        text = LocaleStrings.get("past_orders", lang),
                        isActive = currentSubTab == "past",
                        onClick = { currentSubTab = "past" },
                        modifier = Modifier.weight(1f).testTag("past_orders_tab")
                    )
                }
            }
        }

        // 3. Orders Content
        if (currentSubTab == "active") {
            // ACTIVE ORDERS
            if (activeOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AssignmentLate,
                                contentDescription = "No active orders",
                                tint = TextVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "No active orders right now" else "वर्तमान में कोई सक्रिय ऑर्डर नहीं है",
                                color = TextVariant,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(activeOrders) { order ->
                    ActiveOrderCard(
                        order = order,
                        lang = lang,
                        onTrackClick = { trackingOrder = order },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            // PAST ORDERS
            if (pastOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "No past orders" else "कोई पुराना ऑर्डर नहीं है",
                            color = TextVariant
                        )
                    }
                }
            } else {
                items(pastOrders) { order ->
                    PastOrderCard(
                        order = order,
                        lang = lang,
                        onBuyAgain = {
                            viewModel.purchaseProductDirectly(
                                com.example.data.Product(
                                    name = order.productName,
                                    price = order.price,
                                    imageUrl = order.imageUrl,
                                    category = "Groceries"
                                )
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    trackingOrder?.let { order ->
        TrackingBottomSheet(
            order = order,
            lang = lang,
            onDismiss = { trackingOrder = null }
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Saffron else TextVariant.copy(alpha = 0.7f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
        if (isActive) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(Saffron, RoundedCornerShape(1.5.dp))
            )
        }
    }
}

@Composable
fun ActiveOrderCard(
    order: Order,
    lang: LocaleStrings.Lang,
    onTrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Image and Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F4F6))
                ) {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = order.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = order.productName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextOnSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₹${order.price.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = TextOnSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = LocaleStrings.get("order_id", lang).format(order.orderId),
                        fontSize = 11.sp,
                        color = TextVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Shipping",
                            tint = IndianGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = LocaleStrings.get("expected_by", lang).format(order.expectedDelivery),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndianGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stepper progress indicator with core dot styling
            val progressStep = when (order.status) {
                "ORDERED" -> 1
                "SHIPPED" -> 2
                "OUT_FOR_DELIVERY" -> 3
                "DELIVERED" -> 4
                else -> 1
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepperDot(isActive = progressStep >= 1)
                    StepperLine(isActive = progressStep >= 2, modifier = Modifier.weight(1f))
                    StepperDot(isActive = progressStep >= 2)
                    StepperLine(isActive = progressStep >= 3, modifier = Modifier.weight(1f))
                    StepperDot(isActive = progressStep >= 3)
                    StepperLine(isActive = progressStep >= 4, modifier = Modifier.weight(1f))
                    StepperDot(isActive = progressStep >= 4)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StepperLabel(text = LocaleStrings.get("ordered_step", lang), isActive = progressStep >= 1, align = Alignment.Start)
                    StepperLabel(text = LocaleStrings.get("shipped_step", lang), isActive = progressStep >= 2, align = Alignment.CenterHorizontally)
                    StepperLabel(text = LocaleStrings.get("out_delivery_step", lang), isActive = progressStep >= 3, align = Alignment.CenterHorizontally)
                    StepperLabel(text = LocaleStrings.get("delivered_step", lang), isActive = progressStep >= 4, align = Alignment.End)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "HelpCenter", modifier = Modifier.size(16.dp))
                        Text(text = LocaleStrings.get("order_help", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onTrackClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("track_order_btn_${order.orderId}")
                ) {
                    Text(text = LocaleStrings.get("track_order", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PastOrderCard(
    order: Order,
    lang: LocaleStrings.Lang,
    onBuyAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F4F6))
                ) {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = order.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = order.productName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextOnSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₹${order.price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextOnSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Delivered",
                            tint = IndianGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = order.expectedDelivery,
                            fontSize = 12.sp,
                            color = TextVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.StarBorder, contentDescription = "StarRating", modifier = Modifier.size(14.dp))
                        Text(text = LocaleStrings.get("write_review", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onBuyAgain,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Saffron),
                    border = BorderStroke(1.dp, Saffron),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("buy_again_btn_${order.orderId}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "RebuyAction", tint = Saffron, modifier = Modifier.size(14.dp))
                        Text(text = LocaleStrings.get("buy_again", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StepperDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(if (isActive) IndianGreen else Color(0xFFDCC2B0), CircleShape)
    )
}

@Composable
fun StepperLine(isActive: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(2.dp)
            .background(if (isActive) IndianGreen else Color(0xFFE5E9EB))
    )
}

@Composable
fun StepperLabel(text: String, isActive: Boolean, align: Alignment.Horizontal) {
    Column(horizontalAlignment = align) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) IndianGreen else TextVariant.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingBottomSheet(
    order: Order,
    lang: LocaleStrings.Lang,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Live Tracking" else "लाइव ट्रैकिंग",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NavyBlue
                    )
                    Text(
                        text = "Order ${order.orderId}",
                        fontSize = 12.sp,
                        color = TextVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The Map Component requested by the user
            val deliveryAgentLocation = remember { LatLng(28.6139, 77.2090) }
            val userLocation = remember { LatLng(28.6250, 77.2200) }
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                LiveTrackingMap(
                    deliveryAgentLocation = deliveryAgentLocation,
                    userLocation = userLocation
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Agent Card Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Background),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delivery Boy Avatar
                    Surface(
                        shape = CircleShape,
                        color = Saffron.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Motorcycle,
                                contentDescription = "Rider",
                                tint = SaffronDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Ramesh Shah" else "रमेश शाह",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextOnSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "4.9 (120+ deliveries)",
                                fontSize = 11.sp,
                                color = TextVariant
                            )
                        }
                    }

                    // Contact button
                    Button(
                        onClick = { /* Demo Action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Call" else "कॉल करें",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub Status Details/Address indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Destination",
                    tint = IndianGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Delivering to: Sector 15, New Delhi" else "डिलीवरी का पता: सेक्टर 15, नई दिल्ली",
                    fontSize = 12.sp,
                    color = TextVariant
                )
            }
        }
    }
}
