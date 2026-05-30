package com.example.ui
 
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Order
import com.example.data.Product
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    products: List<Product>,
    orders: List<Order>,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang = viewModel.currentLanguage
    val sellerProducts = products.filter { it.category == "Groceries" || it.id > 3 }
    val sellerOrders = orders.filter { it.isSellerOrder }

    // Aggregate stats
    val pendingCountValue = sellerOrders.count { it.status == "ORDERED" }
    val pendingCountStr = "%02d".format(pendingCountValue)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = "store info", tint = Color.White)
                        Text(
                            text = LocaleStrings.get("app_name", lang),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.White)
                    }
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Saffron, CircleShape)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                
                // Welcome Baner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(LightSaffron, Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = LocaleStrings.get("seller_welcome", lang),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextOnSurface,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = LocaleStrings.get("seller_sub", lang),
                            fontSize = 13.sp,
                            color = TextVariant
                        )
                    }
                }

                // Stats Dashboard Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = LocaleStrings.get("total_sales", lang),
                        value = "₹12,450",
                        icon = Icons.Default.Payments,
                        iconBg = Color(0xFFE8F5E9),
                        iconColor = IndianGreen
                    )
                    StatMetricCard(
                        title = LocaleStrings.get("pending_orders", lang),
                        value = pendingCountStr,
                        icon = Icons.Default.Schedule,
                        iconBg = LightSaffron,
                        iconColor = SaffronDark,
                        onClickForPromo = {
                            viewModel.resetOrderFulfillmentDemo() // Demo loop trigger!
                        }
                    )
                    StatMetricCard(
                        title = LocaleStrings.get("settlements", lang),
                        value = "₹4,200",
                        icon = Icons.Default.AccountBalance,
                        iconBg = Color(0xFFECEEFF),
                        iconColor = NavyLight
                    )
                }

                // 2. Order Fulfillment column queue
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LocaleStrings.get("order_fulfillment", lang),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        Text(
                            text = LocaleStrings.get("view_all", lang) + " >",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronDark,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sellerOrders.forEach { order ->
                            FulfillmentItemRow(
                                order = order,
                                lang = lang,
                                onShipClick = { viewModel.readyToShipOrder(order.orderId) }
                            )
                        }
                    }
                }

                // 3. My products grid/section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LocaleStrings.get("my_products", lang),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                        
                        Button(
                            onClick = { viewModel.showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("add_product_dialog_trigger")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon", tint = Color.White, modifier = Modifier.size(14.dp))
                                Text(text = LocaleStrings.get("add_product", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // Product listing list
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        sellerProducts.take(3).forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                SellerProductSmallCard(product = product)
                            }
                        }
                    }
                }

                // 4. Seller Support cards
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = LocaleStrings.get("seller_support_title", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextOnSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Background, RoundedCornerShape(8.dp))
                                    .clickable { }
                                    .padding(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.School, contentDescription = "School", tint = Saffron, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = LocaleStrings.get("academy", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextOnSurface)
                                Text(text = LocaleStrings.get("academy_desc", lang), fontSize = 10.sp, color = TextVariant)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Background, RoundedCornerShape(8.dp))
                                    .clickable { }
                                    .padding(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "Support Helpline", tint = NavyLight, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = LocaleStrings.get("experts", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextOnSurface)
                                Text(text = LocaleStrings.get("talk_experts_desc", lang), fontSize = 10.sp, color = TextVariant)
                            }
                        }
                    }
                }

                // 5. Expand Shop banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF181C1E))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = LocaleStrings.get("expand_shop", lang),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 19.sp
                            )
                            Text(
                                text = LocaleStrings.get("invite_artisans", lang),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text(
                                text = LocaleStrings.get("seller_onboarding", lang),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Add Product Form Overlay Modal
        AnimatedVisibility(
            visible = viewModel.showAddProductDialog,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.showAddProductDialog = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) { } // prevent click propagate
                        .testTag("add_product_bottom_sheet"),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = LocaleStrings.get("add_product", lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextOnSurface
                            )
                            IconButton(onClick = { viewModel.showAddProductDialog = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close form")
                            }
                        }

                        // Product Name Input
                        OutlinedTextField(
                            value = viewModel.newProductName,
                            onValueChange = { viewModel.newProductName = it },
                            label = { Text(text = LocaleStrings.get("product_name_label", lang)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Saffron,
                                unfocusedBorderColor = BorderColor
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                            singleLine = true
                        )

                        // Category Dropdown
                        OutlinedTextField(
                            value = viewModel.newProductCategory,
                            onValueChange = { viewModel.newProductCategory = it },
                            label = { Text(text = LocaleStrings.get("category_label", lang)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Saffron,
                                unfocusedBorderColor = BorderColor
                            ),
                            placeholder = { Text(text = "Groceries") },
                            modifier = Modifier.fillMaxWidth().testTag("product_category_input"),
                            singleLine = true
                        )

                        // Price Input
                        OutlinedTextField(
                            value = viewModel.newProductPrice,
                            onValueChange = { viewModel.newProductPrice = it },
                            label = { Text(text = LocaleStrings.get("price_label", lang)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Saffron,
                                unfocusedBorderColor = BorderColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("product_price_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.onAddProductSubmit() },
                            colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("product_submit_btn")
                        ) {
                            Text(
                                text = LocaleStrings.get("submit_label", lang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClickForPromo: (() -> Unit)? = null
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClickForPromo != null) { onClickForPromo?.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextVariant,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextOnSurface
                )
            }
        }
    }
}

@Composable
fun FulfillmentItemRow(
    order: Order,
    lang: LocaleStrings.Lang,
    onShipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF1F4F6), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (order.status == "ORDERED") Icons.Default.Inbox else Icons.Default.LocalShipping,
                        contentDescription = "order status",
                        tint = TextVariant
                    )
                }

                Column {
                    Text(
                        text = order.orderId,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextOnSurface
                    )
                    Text(
                        text = "${order.itemsCount} Items • ₹${order.price.toInt()}",
                        fontSize = 11.sp,
                        color = TextVariant
                    )
                }
            }

            if (order.status == "ORDERED") {
                Button(
                    onClick = onShipClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronDark),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("ship_fulfill_btn_${order.orderId}")
                ) {
                    Text(text = LocaleStrings.get("ready_to_ship", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(text = LocaleStrings.get("shipped_status", lang), color = IndianGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "done", tint = IndianGreen, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SellerProductSmallCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₹${product.price.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SaffronDark
                )
            }
        }
    }
}
