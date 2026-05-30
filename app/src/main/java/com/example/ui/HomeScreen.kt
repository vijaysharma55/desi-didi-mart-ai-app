package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.theme.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    products: List<Product>,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang = viewModel.currentLanguage
    var showVoiceTooltip by remember { mutableStateOf(true) }
    val filteredProducts = products.filter {
        it.name.contains(viewModel.searchQuery, ignoreCase = true) ||
        it.category.contains(viewModel.searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Flipkart Style Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Saffron)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = "Logo icon",
                                        tint = Saffron,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = LocaleStrings.get("app_name", lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            )
                        }

                        // App language & Cart Badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.toggleLanguage() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .height(28.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    .testTag("language_toggle_header")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Language",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = LocaleStrings.get("lang_btn", lang),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Cart with Badge
                            Box(modifier = Modifier.testTag("cart_badge_btn")) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-6).dp)
                                        .background(Color.Yellow, CircleShape)
                                        .border(1.dp, Saffron, CircleShape)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "3",
                                        color = SaffronDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    // Search row bar with voice mic
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        placeholder = {
                            Text(
                                text = LocaleStrings.get("search_placeholder", lang),
                                color = TextVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextVariant.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.triggerVoiceSearch() },
                                modifier = Modifier.testTag("voice_search_mic")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Search",
                                    tint = NavyBlue
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextOnSurface,
                            unfocusedTextColor = TextOnSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        singleLine = true
                    )

                    // 1b. Prominent Voice Search Bouncing Tooltip Balloon under search bar
                    if (showVoiceTooltip && viewModel.searchQuery.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                // Rotate 45deg box to act as a tooltip triangle pointer
                                Box(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(10.dp)
                                        .offset(y = 5.dp)
                                        .graphicsLayer(rotationZ = 45f)
                                        .background(LightSaffron)
                                )
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = LightSaffron),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .clickable { 
                                            showVoiceTooltip = false
                                            viewModel.triggerVoiceSearch()
                                        }
                                        .testTag("voice_search_tooltip")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Mic helper",
                                            tint = SaffronDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = LocaleStrings.get("voice_search_tip", lang),
                                            color = SaffronDark,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { showVoiceTooltip = false },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close description",
                                                tint = SaffronDark.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Background)
            ) {
                item {
                    // Dynamic Carousel banner with auto-play slider
                    val banners = listOf(
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuAA3kpS88AHV-tmV_G8eKp1kKzXbgls1yCrmZreqPRi5CnTYOiqpTtFja9K_1WsUpmfeYSge2rbQ95fPJDYAK4rTxf3ialBFxe_MRApqizDgODUKEqP3JjbtVp-nNBv3E7M1OuX21YECOaPK1lbNAqL84ugBgWBEqFRIvVimwdMIcqThJhbKzrSpPp2ASspVQSvV62kwLBUKLOhxaXr5aAMDTrulQkpsYCVQQQoiDEFvL6E6e3D9px9IkeWGaIlsJACzzLLsJXeLqgT",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuAQoTrclN9kdF9HB5bz9KgYl9jMyfiaBApN6kzawhFDiAJTk0y8mUXlc8VXhBJAxEseeT8ZBy89NpPyCbrf32R7l-hyLTMebGk-HtEtNtF2EX0G6WpZQZZOb5Y24jn5iD_tZJnhafQtDQMUenK2iXr9mieK3LbBYew7hXs49Zdxsa9JF5aMYFh8Sr6ziy1rncNzv1WKT3BJT4F973V-MVvhkLV2QCf0orYoojnhOc_0SNZiyedLYPTqX4wVDc8WCfcvsGF7iHboxWaI",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuCXDKsLgWstbZLCqreGvDSZ-lqItuhI8mVJskSmIaHWXOfJziQa1ozH2Fyjr0sLrPwmQn2dna_Wzv4uNQP7uGmRsuWj4GDHzLDl1QxsnFrltnF43IzbFr8yXRYnbMyGbq9-tZu4wyWlkaAFRIuhJopZi7SyaIRIDNPowe0dSNuNCSBSE5F1BV5DklMextbztxyLgODTdhTirrS52XDuinkNi1z2cNaImFwmQEKAqFFSzSosREcTS8FovSDMH5dR1O0bSNg0EDPvVQkk"
                    )
                    PromotionSlider(banners = banners)
                }

                item {
                    // Categories Row - Circular Icons
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CategoryItem(
                                title = LocaleStrings.get("groceries", lang),
                                icon = Icons.Default.ShoppingBasket,
                                bgColor = Color(0xFFFFF4E5),
                                iconColor = Saffron
                            )
                            CategoryItem(
                                title = LocaleStrings.get("clothing", lang),
                                icon = Icons.Default.Checkroom,
                                bgColor = Color(0xFFECEEFF),
                                iconColor = NavyLight
                            )
                            CategoryItem(
                                title = LocaleStrings.get("home_essentials", lang),
                                icon = Icons.Default.HomeRepairService,
                                bgColor = Color(0xFFE8F5E9),
                                iconColor = IndianGreen
                            )
                            CategoryItem(
                                title = LocaleStrings.get("view_all", lang),
                                icon = Icons.Default.GridView,
                                bgColor = Color(0xFFF1F3F4),
                                iconColor = TextVariant
                            )
                        }
                    }
                }

                item {
                    // Main deals list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Title section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = LocaleStrings.get("deals_of_the_day", lang),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface,
                                        fontSize = 17.sp
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Clock",
                                        tint = Saffron,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = LocaleStrings.get("ending_soon", lang),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = LocaleStrings.get("view_all", lang),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        HorizontalDivider(
                            color = Color(0xFFF1F4F6),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(filteredProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val first = pair[0]
                        ProductCard(
                            product = first,
                            lang = lang,
                            onBuyClick = { viewModel.checkoutProduct = first },
                            onProductClick = { viewModel.selectedProductForDetails = first },
                            modifier = Modifier.weight(1f)
                        )
                        if (pair.size > 1) {
                            val second = pair[1]
                            ProductCard(
                                product = second,
                                lang = lang,
                                onBuyClick = { viewModel.checkoutProduct = second },
                                onProductClick = { viewModel.selectedProductForDetails = second },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp).background(Color.White))
                }

                item {
                    // 3. Trust Indicators Banner
                    Surface(
                        color = Background,
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TrustIndicator(text = LocaleStrings.get("quality_guarantee", lang), icon = Icons.Default.Verified)
                            TrustIndicator(text = LocaleStrings.get("fast_delivery", lang), icon = Icons.Default.LocalShipping)
                            TrustIndicator(text = LocaleStrings.get("support_247", lang), icon = Icons.Default.SupportAgent)
                        }
                    }
                }
            }
        }

        // 4. Speech Listener Simulation Dialog Overlay
        AnimatedVisibility(
            visible = viewModel.isListeningByVoice,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = LocaleStrings.get("voice_activated", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Saffron
                        )

                        // Pulsing Icon
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Saffron.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, Saffron, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic listening",
                                tint = Saffron,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = viewModel.voiceStatusText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextOnSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = LocaleStrings.get("voice_speak", lang),
                            fontSize = 11.sp,
                            color = TextVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(bgColor, CircleShape)
                .border(1.dp, iconColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextOnSurface
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    lang: LocaleStrings.Lang,
    onBuyClick: () -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .border(1.dp, Color(0xFFF1F4F6), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            
            // Image + discount badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .background(Color(0xFFFCFCFC))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                // Render discount or New badge
                if (product.discountPercent > 0) {
                    Surface(
                        color = IndianGreen,
                        shape = RoundedCornerShape(bottomEnd = 4.dp),
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (product.isNew) {
                    Surface(
                        color = Saffron,
                        shape = RoundedCornerShape(bottomEnd = 4.dp),
                    ) {
                        Text(
                            text = "NEW",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                // Stars rating row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(IndianGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%.1f".format(product.rating),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }

                    Text(
                        text = LocaleStrings.get("rating_reviews", lang).format(product.reviewCount),
                        fontSize = 10.sp,
                        color = TextVariant.copy(alpha = 0.7f)
                    )
                }

                // Price and Quick Buy Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = SaffronDark
                        )
                        if (product.discountPercent > 0) {
                            val originalPrice = (product.price / (1 - product.discountPercent / 100.0)).toInt()
                            Text(
                                text = "₹$originalPrice",
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = TextVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Direct Buy Now Trigger
                    Button(
                        onClick = onBuyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("buy_now_btn_${product.id}")
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Buy" else "खरीदें",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrustIndicator(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = IndianGreen,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextVariant
        )
    }
}

// Home Screen में ऐड करने के लिए कैरोसेल स्ट्रक्चर
@Composable
fun PromotionSlider(banners: List<String>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    // ऑटो-प्ले लॉजिक के साथ हॉरिजॉन्टल पेजर
    if (banners.isNotEmpty()) {
        LaunchedEffect(key1 = pagerState) {
            while (true) {
                delay(4000)
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(8.dp)
    ) { page ->
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // यहाँ आपके ऑफर्स और 20% OFF वाले बैनर की इमेज लोड होगी
            ImageLoader(url = banners[page])
        }
    }
}

@Composable
fun ImageLoader(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = "Offer Banner",
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}
