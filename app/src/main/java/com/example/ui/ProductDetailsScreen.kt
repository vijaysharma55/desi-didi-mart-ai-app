package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CustomerReview(
    val author: String,
    val rating: Int,
    val dateString: String,
    val text: String,
    val isVerified: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lang = viewModel.currentLanguage

    // Interactive States
    var pincodeText by remember { mutableStateOf("") }
    var checkingPincode by remember { mutableStateOf(false) }
    var checkedPincodeStatus by remember { mutableStateOf<String?>(null) }
    var isCheckingSuccess by remember { mutableStateOf(true) }

    // local reviews list to allow live reviews append
    val initialReviews = remember(product.id) {
        mutableStateListOf(
            CustomerReview(
                author = if (lang == LocaleStrings.Lang.EN) "Ananya K." else "अनन्या के.",
                rating = 5,
                dateString = if (lang == LocaleStrings.Lang.EN) "2 days ago" else "२ दिन पहले",
                text = if (lang == LocaleStrings.Lang.EN) {
                    "The quality of this ${product.name} is exceptional for the price. Direct authentic touch, feels very reliable and sturdy. Highly recommend this to everyone!"
                } else {
                    "इस ${product.name} की गुणवत्ता कीमत के हिसाब से उत्कृष्ट है। बेहद टिकाऊ और प्रामाणिक! मैं सभी को इसकी सलाह देती हूँ।"
                }
            ),
            CustomerReview(
                author = if (lang == LocaleStrings.Lang.EN) "Rajesh Kumar" else "राजेश कुमार",
                rating = 4,
                dateString = if (lang == LocaleStrings.Lang.EN) "1 week ago" else "१ सप्ताह पहले",
                text = if (lang == LocaleStrings.Lang.EN) {
                    "Very satisfied with the quick delivery and uncompromised packing! Excellent local craftsmanship."
                } else {
                    "त्वरित डिलीवरी और शानदार पैकेजिंग से बेहद संतुष्ट! उत्कृष्ट स्थानीय शिल्प कौशल।"
                }
            )
        )
    }

    // Interactive review form states
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewAuthor by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }

    // Dynamic specs count and labels matching the mockup
    val isRug = product.name.contains("Rug", ignoreCase = true) || product.name.contains("rug", ignoreCase = true)
    val finalSpecsText = if (isRug) {
        if (lang == LocaleStrings.Lang.EN) "Natural Earthy Finish • Sustainable Organic Fiber • 4x6 Feet"
        else "प्राकृतिक मिट्टी की फिनिश • टिकाऊ जैविक फाइबर • ४x६ फीट"
    } else if (product.name.contains("Dal", ignoreCase = true) || product.name.contains("dal", ignoreCase = true)) {
        if (lang == LocaleStrings.Lang.EN) "100% Organic • High Protein • Unpolished Indian Pulses"
        else "१००% जैविक • उच्च प्रोटीन • बिना पॉलिश की हुई भारतीय दाल"
    } else if (product.name.contains("Pot", ignoreCase = true) || product.name.contains("Pottery", ignoreCase = true)) {
        if (lang == LocaleStrings.Lang.EN) "Handcrafted Terracotta • Eco-friendly Paints • 8 inch Height"
        else "हस्तनिर्मित टेराकोटा • पर्यावरण अनुकूल पेंट • ८ इंच ऊंचाई"
    } else if (product.name.contains("Turmeric", ignoreCase = true) || product.name.contains("Haldi", ignoreCase = true)) {
        if (lang == LocaleStrings.Lang.EN) "Pure Lakadong Turmeric • High Curcumin • Immunity Booster"
        else "शुद्ध लाकाडोंग हल्दी • उच्च करक्यूमिन • रोग प्रतिरोधक क्षमता बढ़ाने वाला"
    } else {
        if (lang == LocaleStrings.Lang.EN) "Authentic Desi Produce • Locally Crafted • Safe Packaging"
        else "प्रामाणिक देसी उत्पाद • स्थानीय रूप से निर्मित • सुरक्षित पैकेजिंग"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Desi Didi Mart" else "देसी दीदी मार्ट",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("details_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyBlue
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Product link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextVariant
                        )
                    }
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = {
                            Toast.makeText(context, "Cart contains 2 items", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = TextVariant
                            )
                        }
                        // Badge count "2"
                        Surface(
                            shape = CircleShape,
                            color = NavyLight,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "2",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Sticky Bottom Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                tonalElevation = 8.dp,
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E9EB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to Cart
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                if (lang == LocaleStrings.Lang.EN) "Added to checkout cart!" else "कार्ट में जोड़ा गया!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("add_to_cart_btn"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, NavyLight),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyLight)
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "ADD TO CART" else "कार्ट में जोड़ें",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Buy Now
                    Button(
                        onClick = {
                            viewModel.checkoutProduct = product
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("buy_now_btn_detail"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "BUY NOW" else "अभी खरीदें",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7FAFC))
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Hero Product Image and Carousel Indicator Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                // Mock carousel position/indicators overlay [1/5] as requested in HTML mockup
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Photos",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "1/5",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Divider line
            HorizontalDivider(color = Color(0xFFEBEEF0), thickness = 1.dp)

            // Container for Info details & Badges
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Badges Row
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = LightGreen,
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(
                                text = "TOP RATED",
                                color = IndianGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = LightSaffron,
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(
                                text = "HANDMADE",
                                color = SaffronDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Product Name
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface,
                            fontSize = 20.sp,
                            lineHeight = 26.sp
                        )
                    )

                    // Specs/Finish info
                    Text(
                        text = finalSpecsText,
                        fontSize = 12.sp,
                        color = TextVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFFF1F4F6))

                    // Price section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "₹${product.price.toInt()}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = TextOnSurface
                                )

                                // Crossed original price
                                val discountRatio = if (product.discountPercent > 0) product.discountPercent else 15
                                val originalPrice = (product.price / (1 - discountRatio / 100.0)).toInt()
                                Text(
                                    text = "₹$originalPrice",
                                    fontSize = 14.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = TextVariant.copy(alpha = 0.5f)
                                )

                                Text(
                                    text = "$discountRatio% OFF",
                                    fontSize = 14.sp,
                                    color = IndianGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Inclusive of all taxes" else "सभी करों सहित",
                                fontSize = 11.sp,
                                color = TextVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Rating Summary Box
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(4) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = Saffron,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.StarHalf,
                                    contentDescription = "Star Half",
                                    tint = Saffron,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${product.rating} (${product.reviewCount} ${if (lang == LocaleStrings.Lang.EN) "Reviews" else "समीक्षाएं"})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyLight,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    // Scroll behavior or just focus
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Interactive Delivery Estimator Box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E9EB))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Shipping",
                            tint = NavyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Check Delivery Availability" else "वितरण उपलब्धता की जांच करें",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pincodeText,
                            onValueChange = {
                                if (it.length <= 6) pincodeText = it
                            },
                            placeholder = {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "Enter Pincode (e.g. 110001)" else "पिनकोड दर्ज करें (जैसे: 110001)",
                                    fontSize = 12.sp,
                                    color = TextVariant.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyLight,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                focusedContainerColor = Color(0xFFF7FAFC),
                                unfocusedContainerColor = Color(0xFFF7FAFC)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("pincode_input")
                        )

                        Button(
                            onClick = {
                                if (pincodeText.length >= 5) {
                                    checkingPincode = true
                                    coroutineScope.launch {
                                        delay(1000)
                                        checkingPincode = false
                                        isCheckingSuccess = true
                                        checkedPincodeStatus = if (lang == LocaleStrings.Lang.EN) {
                                            "Serviceable! Free standard delivery available at $pincodeText."
                                        } else {
                                            "सेवा उपलब्ध! $pincodeText पर मुफ्त डिलीवरी उपलब्ध है।"
                                        }
                                    }
                                } else {
                                    isCheckingSuccess = false
                                    checkedPincodeStatus = if (lang == LocaleStrings.Lang.EN) {
                                        "Please enter a valid 5 or 6 digit pincode."
                                    } else {
                                        "कृपया मान्य ५ या ६ अंकों का पिनकोड दर्ज करें।"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("pincode_check_btn")
                        ) {
                            if (checkingPincode) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Text(
                                    text = if (lang == LocaleStrings.Lang.EN) "CHECK" else "जांचें",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Output status message
                    AnimatedVisibility(visible = checkedPincodeStatus != null) {
                        checkedPincodeStatus?.let { status ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isCheckingSuccess) Icons.Default.Verified else Icons.Default.Error,
                                    contentDescription = "Status",
                                    tint = if (isCheckingSuccess) IndianGreen else ErrorColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = status,
                                    color = if (isCheckingSuccess) IndianGreen else ErrorColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F4F6))

                    // Verified highlights row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Icon",
                                tint = IndianGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Free Delivery" else "फ्री डिलीवरी",
                                fontSize = 11.sp,
                                color = TextVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Delivery date icon",
                                tint = NavyLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Delivery by Wed, Oct 25" else "वितरण बुधवार, २५ अक्टूबर",
                                fontSize = 11.sp,
                                color = TextVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Customer Reviews Bento Box Layout
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E9EB))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header inside reviews card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == LocaleStrings.Lang.EN) "Customer Reviews" else "ग्राहक समीक्षाएं",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )

                        TextButton(
                            onClick = { showReviewDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = NavyBlue),
                            modifier = Modifier.testTag("write_review_action_btn")
                        ) {
                            Text(
                                text = if (lang == LocaleStrings.Lang.EN) "Write a Review" else "समीक्षा लिखें",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Star Bars breakdown layout (Bento card style)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7FAFC), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Rating display
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(0.35f)
                        ) {
                            Text(
                                text = "4.5",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row {
                                repeat(4) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Gold Star",
                                        tint = Saffron,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.StarHalf,
                                    contentDescription = "Gold Star Half",
                                    tint = Saffron,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Based on 1.2k ratings",
                                fontSize = 9.sp,
                                color = TextVariant.copy(alpha = 0.7f),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }

                        // Right progress bars breakdown list
                        Column(
                            modifier = Modifier.weight(0.65f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RatingProgressBarRow(stars = "5", ratio = 0.75f, percentText = "75%")
                            RatingProgressBarRow(stars = "4", ratio = 0.15f, percentText = "15%")
                            RatingProgressBarRow(stars = "3", ratio = 0.05f, percentText = "5%")
                            RatingProgressBarRow(stars = "2", ratio = 0.03f, percentText = "3%")
                            RatingProgressBarRow(stars = "1", ratio = 0.02f, percentText = "2%", isErrorBar = true)
                        }
                    }

                    // Reviews List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        initialReviews.forEach { review ->
                            HorizontalDivider(color = Color(0xFFF1F4F6))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Initials circle avatar
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(NavyBlue.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (review.author.isNotEmpty()) review.author.take(2).uppercase() else "A",
                                                color = NavyBlue,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = review.author,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextOnSurface
                                            )
                                            Text(
                                                text = "${if (review.isVerified) "Verified Buyer • " else ""}${review.dateString}",
                                                fontSize = 9.sp,
                                                color = TextVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    // Display stars as solid layout
                                    Row {
                                        repeat(5) { starIndex ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Star",
                                                tint = if (starIndex < review.rating) Saffron else Color.LightGray.copy(alpha = 0.5f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = review.text,
                                    fontSize = 12.sp,
                                    color = TextOnSurface.copy(alpha = 0.9f),
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Interactive Review Writing Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Text(
                    text = if (lang == LocaleStrings.Lang.EN) "Write a Customer Review" else "अपनी समीक्षा लिखें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyBlue
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Rating Star Input Toggles
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Select Rating" else "रेटिंग चुनें",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { starIndex ->
                            val currentStar = starIndex + 1
                            Icon(
                                imageVector = if (currentStar <= reviewRating) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = "Rating Star $currentStar",
                                tint = Saffron,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { reviewRating = currentStar }
                            )
                        }
                    }

                    // Author input Field
                    OutlinedTextField(
                        value = reviewAuthor,
                        onValueChange = { reviewAuthor = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Your Name" else "आपका नाम", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyBlue,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Review Text input Field
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text(if (lang == LocaleStrings.Lang.EN) "Your Review" else "आपकी समीक्षा", fontSize = 11.sp) },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyBlue,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalAuthor = if (reviewAuthor.trim().isEmpty()) {
                            if (lang == LocaleStrings.Lang.EN) "Guest Buyer" else "अतिथि खरीदार"
                        } else reviewAuthor.trim()

                        val finalReviewText = if (reviewText.trim().isEmpty()) {
                            if (lang == LocaleStrings.Lang.EN) "Highly satisfied! Sturdy, highly authentic hand-crafted product." else "बेहद संतुष्ट! प्रामाणिक रूप से हस्तनिर्मित उत्पाद।"
                        } else reviewText.trim()

                        // Append dynamic review to live list
                        initialReviews.add(
                            0, // Insert at top
                            CustomerReview(
                                author = finalAuthor,
                                rating = reviewRating,
                                dateString = if (lang == LocaleStrings.Lang.EN) "Just now" else "अभी-अभी",
                                text = finalReviewText,
                                isVerified = false
                            )
                        )

                        // Clear and close
                        reviewAuthor = ""
                        reviewText = ""
                        reviewRating = 5
                        showReviewDialog = false

                        Toast.makeText(
                            context,
                            if (lang == LocaleStrings.Lang.EN) "Thank you! Review posted successfully." else "धन्यवाद! समीक्षा सफलतापूर्वक पोस्ट की गई।",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Submit" else "जमा करें",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text(
                        text = if (lang == LocaleStrings.Lang.EN) "Cancel" else "रद्द करें",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextVariant
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun RatingProgressBarRow(
    stars: String,
    ratio: Float,
    percentText: String,
    isErrorBar: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stars,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextVariant,
            modifier = Modifier.width(8.dp)
        )
        LinearProgressIndicator(
            progress = { ratio },
            color = if (isErrorBar) ErrorColor.copy(alpha = 0.5f) else Saffron,
            trackColor = Color.LightGray.copy(alpha = 0.2f),
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
        )
        Text(
            text = percentText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextVariant.copy(alpha = 0.7f),
            modifier = Modifier.width(24.dp)
        )
    }
}
